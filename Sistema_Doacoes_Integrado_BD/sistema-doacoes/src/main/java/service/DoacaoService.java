package service;

import model.Doacao;
import model.Lote;
import repository.DoacaoRepositoryJdbc;
import exception.RegistroNaoEncontradoException;

import java.util.List;

public class DoacaoService {
    private final DoacaoRepositoryJdbc repository = new DoacaoRepositoryJdbc();

    public void cadastrarDoacao(Doacao doacao) {
        boolean sucesso = doacao.cadastrar();
        if (sucesso) {
            repository.salvar(doacao);
        } else {
            throw new IllegalArgumentException("Não foi possível validar e cadastrar a doação.");
        }
    }

    public List<Doacao> listar() {
        return repository.listar();
    }

    public List<Lote> listarLotes() {
        return repository.listarLotes();
    }

    public void retirarDoLote(String idLote, int quantidade) {
        Lote lote = repository.buscarLotePorId(idLote)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Lote " + idLote + " não encontrado."));
        lote.retirar(quantidade);
        repository.atualizarQuantidadeLote(lote);
    }

    public Doacao buscarPorId(String id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Doação com ID " + id + " não encontrada."));
    }

    public void atualizar(Doacao doacao) {
        doacao.validar();
        repository.atualizar(doacao);
    }

    public void excluir(String id) {
        repository.excluir(id);
    }
}
