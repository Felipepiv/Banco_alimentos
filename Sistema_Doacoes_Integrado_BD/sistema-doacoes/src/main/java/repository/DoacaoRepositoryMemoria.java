package repository;

import model.Doacao;
import model.Lote;
import exception.RegistroNaoEncontradoException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoacaoRepositoryMemoria implements CrudRepository<Doacao, String> {
    private final List<Doacao> doacoes = new ArrayList<>();
    private final List<Lote> lotes = new ArrayList<>();

    @Override
    public void salvar(Doacao doacao) {
        if (doacao == null) {
            throw new IllegalArgumentException("Doação não pode ser nula.");
        }
        doacoes.removeIf(d -> d.getId().equals(doacao.getId()));
        doacoes.add(doacao);

        if (doacao.getLoteAssociado() != null) {
            salvarOuAtualizarLote(doacao.getLoteAssociado());
        }
    }

    public void salvarOuAtualizarLote(Lote lote) {
        Optional<Lote> existente = lotes.stream().filter(l -> l.getId().equals(lote.getId())).findFirst();
        if (existente.isPresent()) {
            Lote l = existente.get();
            // Atualiza quantidade e validade se já existir
            lotes.remove(l);
            lotes.add(lote);
        } else {
            lotes.add(lote);
        }
    }

    public List<Lote> listarLotes() {
        return new ArrayList<>(lotes);
    }

    public Optional<Lote> buscarLotePorId(String idLote) {
        return lotes.stream().filter(l -> l.getId().equals(idLote)).findFirst();
    }

    @Override
    public Optional<Doacao> buscarPorId(String id) {
        return doacoes.stream().filter(d -> d.getId().equals(id)).findFirst();
    }

    @Override
    public List<Doacao> listar() {
        return new ArrayList<>(doacoes);
    }

    @Override
    public void atualizar(Doacao doacao) {
        boolean encontrada = doacoes.removeIf(d -> d.getId().equals(doacao.getId()));
        if (!encontrada) {
            throw new RegistroNaoEncontradoException("Doação não encontrada para atualização: " + doacao.getId());
        }
        doacoes.add(doacao);
        if (doacao.getLoteAssociado() != null) {
            salvarOuAtualizarLote(doacao.getLoteAssociado());
        }
    }

    @Override
    public void excluir(String id) {
        Optional<Doacao> doacaoOpt = buscarPorId(id);
        if (doacaoOpt.isPresent()) {
            Doacao d = doacaoOpt.get();
            if (d.getLoteAssociado() != null) {
                lotes.removeIf(l -> l.getId().equals(d.getLoteAssociado().getId()));
            }
        }
        boolean removida = doacoes.removeIf(d -> d.getId().equals(id));
        if (!removida) {
            throw new RegistroNaoEncontradoException("Doação não encontrada para exclusão: " + id);
        }
    }
}
