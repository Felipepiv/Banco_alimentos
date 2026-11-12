package repository;

import db.Conexao;
import exception.RegistroNaoEncontradoException;
import exception.RegraNegocioException;
import model.CategoriaAlimento;
import model.Doacao;
import model.Lote;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repositório de Doações/Lotes sobre o banco banco_de_alimentos_ldm.
 *
 * Mapeamento Java -> banco:
 *   Doacao.id            -> doacao.id_Doacao (int)
 *   Doacao.doador        -> doador.Nome (reaproveita o doador se já existir, senão cria)
 *   Doacao.categoria     -> doacao.tipo_Entrada e produto.Categoria
 *   Doacao.unidade       -> produto.Unidade_Medida (produto é reaproveitado/criado por categoria+unidade)
 *   Lote.id              -> lote.Numero_Lote (ex.: "LOTE-10")
 *   Lote.quantidade      -> lote.Quantidade
 *   Lote.dataValidade    -> lote.Data_validade
 */
public class DoacaoRepositoryJdbc implements CrudRepository<Doacao, String> {

    private static final String SQL_LOTES =
            "SELECT l.id_Lote, COALESCE(l.Numero_Lote, CONCAT('LOTE-', l.id_Lote)) AS numero, " +
            "       l.Data_validade, COALESCE(l.Quantidade, 0) AS quantidade, " +
            "       l.id_Doacao, p.Unidade_Medida " +
            "FROM lote l JOIN produto p ON p.id_Produto = l.idProduto ";

    // ------------------------------------------------------------------ CRUD de Doação

    @Override
    public void salvar(Doacao doacao) {
        if (doacao == null) {
            throw new IllegalArgumentException("Doação não pode ser nula.");
        }
        int idDoacao = converterId(doacao.getId());
        executarEmTransacao(conn -> {
            if (existeDoacao(conn, idDoacao)) {
                throw new RegraNegocioException("Já existe uma doação com o ID " + idDoacao + ".");
            }
            int idDoador = obterOuCriarDoador(conn, doacao.getDoador());

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO doacao (id_Doacao, Data_Recebimento, tipo_Entrada, id_Doador) VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, idDoacao);
                ps.setDate(2, Date.valueOf(LocalDate.now()));
                ps.setString(3, doacao.getCategoria().name());
                ps.setInt(4, idDoador);
                ps.executeUpdate();
            }

            Lote lote = doacao.getLoteAssociado();
            if (lote != null) {
                int idProduto = obterOuCriarProduto(conn, doacao.getCategoria(), lote.getUnidadeMedida());
                inserirLote(conn, idDoacao, idProduto, lote);
            }
            return null;
        });
    }

    @Override
    public Optional<Doacao> buscarPorId(String id) {
        int idDoacao = converterId(id);
        return executarEmTransacao(conn -> listarDoacoes(conn, idDoacao).stream().findFirst());
    }

    @Override
    public List<Doacao> listar() {
        return executarEmTransacao(conn -> listarDoacoes(conn, null));
    }

    @Override
    public void atualizar(Doacao doacao) {
        int idDoacao = converterId(doacao.getId());
        executarEmTransacao(conn -> {
            if (!existeDoacao(conn, idDoacao)) {
                throw new RegistroNaoEncontradoException("Doação não encontrada para atualização: " + doacao.getId());
            }
            int idDoador = obterOuCriarDoador(conn, doacao.getDoador());
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE doacao SET tipo_Entrada = ?, id_Doador = ? WHERE id_Doacao = ?")) {
                ps.setString(1, doacao.getCategoria().name());
                ps.setInt(2, idDoador);
                ps.setInt(3, idDoacao);
                ps.executeUpdate();
            }

            // Reflete o formulário no lote gerado por esta doação (LOTE-<id>)
            int idProduto = obterOuCriarProduto(conn, doacao.getCategoria(), doacao.getUnidade_medida());
            String numeroLote = "LOTE-" + doacao.getId();
            int atualizados;
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE lote SET Quantidade = ?, Data_validade = ?, idProduto = ? " +
                    "WHERE id_Doacao = ? AND Numero_Lote = ?")) {
                ps.setInt(1, doacao.getQuantidade());
                ps.setDate(2, Date.valueOf(doacao.getData_validade()));
                ps.setInt(3, idProduto);
                ps.setInt(4, idDoacao);
                ps.setString(5, numeroLote);
                atualizados = ps.executeUpdate();
            }
            // Doação sem nenhum lote: cria o lote. Doação com lotes de outra origem: não mexe neles.
            if (atualizados == 0 && !temLotes(conn, idDoacao)) {
                inserirLote(conn, idDoacao, idProduto,
                        new Lote(numeroLote, doacao.getQuantidade(), doacao.getUnidade_medida(), doacao.getData_validade()));
            }
            return null;
        });
    }

    @Override
    public void excluir(String id) {
        int idDoacao = converterId(id);
        executarEmTransacao(conn -> {
            // lote e perda são removidos em cascata pelo próprio banco
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM doacao WHERE id_Doacao = ?")) {
                ps.setInt(1, idDoacao);
                if (ps.executeUpdate() == 0) {
                    throw new RegistroNaoEncontradoException("Doação não encontrada para exclusão: " + id);
                }
            }
            return null;
        });
    }

    // ------------------------------------------------------------------ Lotes

    public List<Lote> listarLotes() {
        return executarEmTransacao(conn -> {
            List<Lote> lotes = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(SQL_LOTES + "ORDER BY l.id_Lote");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lotes.add(mapearLote(rs));
            }
            return lotes;
        });
    }

    public Optional<Lote> buscarLotePorId(String numeroLote) {
        return executarEmTransacao(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(SQL_LOTES + "WHERE l.Numero_Lote = ?")) {
                ps.setString(1, numeroLote);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? Optional.of(mapearLote(rs)) : Optional.<Lote>empty();
                }
            }
        });
    }

    /** Grava a quantidade atual do lote (após uma retirada, por exemplo). */
    public void atualizarQuantidadeLote(Lote lote) {
        executarEmTransacao(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE lote SET Quantidade = ? WHERE Numero_Lote = ?")) {
                ps.setInt(1, lote.getQuantidade());
                ps.setString(2, lote.getId());
                if (ps.executeUpdate() == 0) {
                    throw new RegistroNaoEncontradoException("Lote " + lote.getId() + " não encontrado.");
                }
            }
            return null;
        });
    }

    // ------------------------------------------------------------------ Auxiliares de leitura

    private List<Doacao> listarDoacoes(Connection conn, Integer idFiltro) throws SQLException {
        // Lotes agrupados por doação (uma consulta só)
        Map<Integer, List<Lote>> lotesPorDoacao = new LinkedHashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_LOTES + "ORDER BY l.id_Lote");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lotesPorDoacao.computeIfAbsent(rs.getInt("id_Doacao"), k -> new ArrayList<>()).add(mapearLote(rs));
            }
        }

        String sql = "SELECT d.id_Doacao, d.tipo_Entrada, COALESCE(dr.Nome, '(sem nome)') AS nome " +
                     "FROM doacao d JOIN doador dr ON dr.id_doador = d.id_Doador " +
                     (idFiltro != null ? "WHERE d.id_Doacao = ? " : "") +
                     "ORDER BY d.id_Doacao";
        List<Doacao> doacoes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (idFiltro != null) ps.setInt(1, idFiltro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_Doacao");
                    List<Lote> lotes = lotesPorDoacao.getOrDefault(id, List.of());

                    // A doação do app tem um único conjunto de dados de lote: mostra o total
                    // em estoque, a menor validade e a unidade do primeiro lote.
                    int quantidade = lotes.stream().mapToInt(Lote::getQuantidade).sum();
                    LocalDate validade = lotes.stream().map(Lote::getDataValidade).min(LocalDate::compareTo).orElse(null);
                    String unidade = lotes.isEmpty() ? null : lotes.get(0).getUnidadeMedida();
                    Lote primeiro = lotes.isEmpty() ? null : lotes.get(0);

                    doacoes.add(Doacao.restaurar(String.valueOf(id), rs.getString("nome"),
                            converterCategoria(rs.getString("tipo_Entrada")), validade, quantidade, unidade, primeiro));
                }
            }
        }
        return doacoes;
    }

    private Lote mapearLote(ResultSet rs) throws SQLException {
        Date validade = rs.getDate("Data_validade");
        String unidade = rs.getString("Unidade_Medida");
        return new Lote(
                rs.getString("numero"),
                rs.getInt("quantidade"),
                (unidade == null || unidade.isBlank()) ? "un" : unidade,
                validade != null ? validade.toLocalDate() : LocalDate.MAX);
    }

    private CategoriaAlimento converterCategoria(String tipoEntrada) {
        if (tipoEntrada != null) {
            try {
                return CategoriaAlimento.valueOf(tipoEntrada.trim().toUpperCase());
            } catch (IllegalArgumentException ignorado) {
                // tipos que não existem no enum (ex.: "Alimentos", "Roupas") caem em OUTROS
            }
        }
        return CategoriaAlimento.OUTROS;
    }

    // ------------------------------------------------------------------ Auxiliares de escrita

    private boolean existeDoacao(Connection conn, int idDoacao) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM doacao WHERE id_Doacao = ?")) {
            ps.setInt(1, idDoacao);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean temLotes(Connection conn, int idDoacao) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM lote WHERE id_Doacao = ? LIMIT 1")) {
            ps.setInt(1, idDoacao);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void inserirLote(Connection conn, int idDoacao, int idProduto, Lote lote) throws SQLException {
        int idLote = proximoId(conn, "lote", "id_Lote");
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO lote (id_Lote, Numero_Lote, Data_validade, Quantidade, id_Doacao, idProduto) " +
                "VALUES (?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, idLote);
            ps.setString(2, lote.getId());
            ps.setDate(3, Date.valueOf(lote.getDataValidade()));
            ps.setInt(4, lote.getQuantidade());
            ps.setInt(5, idDoacao);
            ps.setInt(6, idProduto);
            ps.executeUpdate();
        }
    }

    private int obterOuCriarDoador(Connection conn, String nome) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id_doador FROM doador WHERE Nome = ? LIMIT 1")) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        int id = proximoId(conn, "doador", "id_doador");
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO doador (id_doador, Nome) VALUES (?, ?)")) {
            ps.setInt(1, id);
            ps.setString(2, nome);
            ps.executeUpdate();
        }
        return id;
    }

    private int obterOuCriarProduto(Connection conn, CategoriaAlimento categoria, String unidade) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_Produto FROM produto WHERE Categoria = ? AND Unidade_Medida = ? ORDER BY id_Produto LIMIT 1")) {
            ps.setString(1, categoria.name());
            ps.setString(2, unidade);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        int id = proximoId(conn, "produto", "id_Produto");
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO produto (id_Produto, Nome, Categoria, Unidade_Medida) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, id);
            ps.setString(2, categoria.name());
            ps.setString(3, categoria.name());
            ps.setString(4, unidade);
            ps.executeUpdate();
        }
        return id;
    }

    /** As chaves das tabelas não são AUTO_INCREMENT, então o próximo id é calculado aqui. */
    private int proximoId(Connection conn, String tabela, String coluna) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COALESCE(MAX(" + coluna + "), 0) + 1 FROM " + tabela);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private int converterId(String id) {
        try {
            return Integer.parseInt(id.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("O ID da doação deve ser um número inteiro (ex.: 10).");
        }
    }

    // ------------------------------------------------------------------ Transação

    @FunctionalInterface
    private interface Operacao<T> {
        T executar(Connection conn) throws SQLException;
    }

    private <T> T executarEmTransacao(Operacao<T> operacao) {
        try (Connection conn = Conexao.obter()) {
            conn.setAutoCommit(false);
            try {
                T resultado = operacao.executar(conn);
                conn.commit();
                return resultado;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RegraNegocioException("Operação não permitida pelas regras do banco: " + e.getMessage());
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao acessar o banco de dados: " + e.getMessage());
        }
    }
}
