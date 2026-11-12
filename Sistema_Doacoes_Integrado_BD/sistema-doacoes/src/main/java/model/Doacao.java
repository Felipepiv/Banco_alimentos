package model;

import java.time.LocalDate;

public class Doacao extends Registro implements Validavel {
    private String doador;
    private LocalDate data_Nascimento;
    private CategoriaAlimento categoria;
    private LocalDate data_validade;
    private int quantidade;
    private String unidade_medida;
    private Lote loteAssociado;

    public Doacao(String id_Doacao, String doador, LocalDate data_Nascimento, CategoriaAlimento categoria, LocalDate data_validade, int quantidade, String unidade_medida) {
        this(id_Doacao, doador, data_Nascimento, categoria, data_validade, quantidade, unidade_medida, true);
    }

    private Doacao(String id_Doacao, String doador, LocalDate data_Nascimento, CategoriaAlimento categoria, LocalDate data_validade, int quantidade, String unidade_medida, boolean validarAgora) {
        super(id_Doacao);
        this.doador = doador;
        this.data_Nascimento = data_Nascimento;
        this.categoria = categoria;
        this.data_validade = data_validade;
        this.quantidade = quantidade;
        this.unidade_medida = unidade_medida;
        if (validarAgora) validar();
    }

    /**
     * Reconstrói uma doação que já está gravada no banco. Não revalida a data de validade,
     * porque doações antigas (lotes vencidos) precisam continuar aparecendo na listagem.
     */
    public static Doacao restaurar(String id, String doador, CategoriaAlimento categoria, LocalDate data_validade,
                                   int quantidade, String unidade_medida, Lote lote) {
        Doacao d = new Doacao(id, doador, null, categoria, data_validade, quantidade, unidade_medida, false);
        d.loteAssociado = lote;
        return d;
    }

    public Doacao(String id_Doacao, String doador, CategoriaAlimento categoria, LocalDate data_validade, int quantidade, String unidade_medida) {
        this(id_Doacao, doador, null, categoria, data_validade, quantidade, unidade_medida);
    }

    @Override
    public void validar() {
        if (doador == null || doador.isBlank()) {
            throw new IllegalArgumentException("Doador obrigatório.");
        }
        if (data_validade == null || data_validade.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data de validade inválida ou lote vencido.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade da doação deve ser maior que zero.");
        }
    }

    public boolean cadastrar() {
        try {
            validar();
            Lote novoLote = new Lote("LOTE-" + getId(), quantidade, unidade_medida, data_validade);
            associarLote(novoLote);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar doação: " + e.getMessage());
            return false;
        }
    }

    public void associarLote(Lote lote) {
        if (lote == null) {
            throw new IllegalArgumentException("Lote obrigatório para associação.");
        }
        this.loteAssociado = lote;
    }

    public Lote getLoteAssociado() {
        return loteAssociado;
    }

    public String getId_Doacao() { return getId(); }
    public String getDoador() { return doador; }
    public LocalDate getData_Nascimento() { return data_Nascimento; }
    public CategoriaAlimento getCategoria() { return categoria; }
    public LocalDate getData_validade() { return data_validade; }
    public int getQuantidade() { return quantidade; }
    public String getUnidade_medida() { return unidade_medida; }

    @Override
    public String resumo() {
        return "Doação " + getId() + " | Doador: " + doador + " | Categoria: " + categoria + " | Validade: " + data_validade;
    }
}
