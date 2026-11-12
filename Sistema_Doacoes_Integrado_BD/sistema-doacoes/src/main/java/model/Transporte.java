package model;

import java.time.LocalDateTime;

public class Transporte extends Registro implements Validavel {
    private String idAgendamento;
    private StatusTransporte status = StatusTransporte.PENDENTE;
    private LocalDateTime dataHoraAtualizacao;

    public Transporte(String id, String idAgendamento) {
        super(id);
        this.idAgendamento = idAgendamento;
        this.dataHoraAtualizacao = LocalDateTime.now();
        validar();
    }

    @Override
    public void validar() {
        if (idAgendamento == null || idAgendamento.isBlank()) {
            throw new IllegalArgumentException("ID do agendamento obrigatório.");
        }
    }

    public void atualizarStatus(StatusTransporte novoStatus) {
        if (novoStatus == null) throw new IllegalArgumentException("Novo status obrigatório.");
        this.status = novoStatus;
        this.dataHoraAtualizacao = LocalDateTime.now();
    }

    public StatusTransporte getStatus() { return status; }
    public LocalDateTime getDataHoraAtualizacao() { return dataHoraAtualizacao; }

    @Override
    public String resumo() {
        return "Transporte " + getId() + " | Agendamento: " + idAgendamento + " | Status: " + status;
    }
}
