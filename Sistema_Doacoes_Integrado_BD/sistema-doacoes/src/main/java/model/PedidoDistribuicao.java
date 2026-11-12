package model;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class PedidoDistribuicao extends Registro implements Validavel {
    private final Beneficiario beneficiario;
    private final LocalDate dataPedido;
    private StatusPedido status = StatusPedido.ABERTO;
    private final Map<Lote, Integer> itens = new LinkedHashMap<>();

    public PedidoDistribuicao(String id, Beneficiario beneficiario, LocalDate dataPedido) {
        super(id);
        this.beneficiario = beneficiario;
        this.dataPedido = dataPedido;
        validar();
    }

    @Override
    public void validar() {
        if (beneficiario == null) throw new IllegalArgumentException("Beneficiário obrigatório.");
        if (dataPedido == null) throw new IllegalArgumentException("Data do pedido obrigatória.");
    }

    public void adicionarItem(Lote lote) { adicionarItem(lote, 1); }

    public void adicionarItem(Lote lote, int quantidade) {
        if (status != StatusPedido.ABERTO) throw new IllegalStateException("Só é possível alterar pedidos abertos.");
        if (lote == null) throw new IllegalArgumentException("Lote obrigatório.");
        if (!lote.verificarValidade()) throw new IllegalStateException("Lote vencido não pode ser adicionado.");
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva.");
        if (quantidade > lote.getQuantidade()) throw new IllegalStateException("Estoque insuficiente para o lote " + lote.getId());
        
        itens.merge(lote, quantidade, Integer::sum);
        if (itens.get(lote) > lote.getQuantidade()) {
            itens.put(lote, itens.get(lote) - quantidade);
            throw new IllegalStateException("Quantidade total solicitada excede o estoque.");
        }
    }

    public void finalizarPedido() {
        if (status != StatusPedido.ABERTO && status != StatusPedido.EM_SEPARACAO) {
            throw new IllegalStateException("Pedido não pode ser finalizado.");
        }
        if (itens.isEmpty()) throw new IllegalStateException("Pedido sem itens.");
        
        itens.forEach((lote, qtd) -> {
            if (!lote.verificarValidade() || lote.getQuantidade() < qtd) {
                throw new IllegalStateException("Lote vencido ou estoque insuficiente: " + lote.getId());
            }
        });
        
        itens.forEach(Lote::retirar);
        status = StatusPedido.FINALIZADO;
    }

    public Beneficiario getBeneficiario() { return beneficiario; }
    public LocalDate getDataPedido() { return dataPedido; }
    public StatusPedido getStatus() { return status; }
    public Map<Lote, Integer> getItens() { return Map.copyOf(itens); }

    @Override
    public String resumo() {
        return "Pedido " + getId() + " | " + beneficiario.getNome() + " | " + status + " | itens: " + itens.size();
    }
}
