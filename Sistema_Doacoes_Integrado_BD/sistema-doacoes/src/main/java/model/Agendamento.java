package model;

import java.time.LocalDateTime;

public class Agendamento extends Registro implements Validavel {
    private LocalDateTime dataHora;
    private TipoAgendamento tipo;
    private String responsavel;
    private PedidoDistribuicao pedido;

    public Agendamento(String id, LocalDateTime dataHora, TipoAgendamento tipo, String responsavel) {
        super(id);
        this.dataHora = dataHora;
        this.tipo = tipo;
        this.responsavel = responsavel;
        validar();
    }

    @Override
    public void validar() {
        if (dataHora == null) throw new IllegalArgumentException("Data e hora obrigatórias.");
        if (tipo == null) throw new IllegalArgumentException("Tipo de agendamento obrigatório.");
        if (responsavel == null || responsavel.isBlank()) throw new IllegalArgumentException("Responsável obrigatório.");
    }

    public void vincularPedido(PedidoDistribuicao pedido) {
        if (pedido == null) throw new IllegalArgumentException("Pedido obrigatório.");
        this.pedido = pedido;
    }

    public void definirRota() {
        if (pedido == null) throw new IllegalStateException("Não é possível definir rota sem pedido vinculado.");
        System.out.println("Rota definida para o agendamento " + getId());
    }

    public LocalDateTime getDataHora() { return dataHora; }
    public TipoAgendamento getTipo() { return tipo; }
    public String getResponsavel() { return responsavel; }
    public PedidoDistribuicao getPedido() { return pedido; }

    @Override
    public String resumo() {
        return "Agendamento " + getId() + " | " + tipo + " | " + dataHora + " | Resp: " + responsavel;
    }
}
