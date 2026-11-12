package model;

import java.time.LocalDate;

public class Lote extends Registro implements Validavel {
    private int quantidade;
    private final String unidadeMedida;
    private LocalDate dataValidade;

    public Lote(String id, int quantidade, String unidadeMedida, LocalDate dataValidade) {
        super(id);
        this.quantidade = quantidade;
        this.unidadeMedida = unidadeMedida;
        this.dataValidade = dataValidade;
        validar();
    }

    @Override
    public void validar() {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade do lote não pode ser negativa.");
        }
        if (unidadeMedida == null || unidadeMedida.isBlank()) {
            throw new IllegalArgumentException("Unidade de medida obrigatória.");
        }
        if (dataValidade == null) {
            throw new IllegalArgumentException("Validade do lote obrigatória.");
        }
    }

    public boolean verificarValidade() {
        return !dataValidade.isBefore(LocalDate.now());
    }

    public boolean gerarAlertaVencimento() {
        return !verificarValidade() || !dataValidade.isAfter(LocalDate.now().plusDays(7));
    }

    public void retirar(int qtd) {
        if (!verificarValidade()) {
            throw new IllegalStateException("Lote vencido não pode ser distribuído.");
        }
        if (qtd <= 0) {
            throw new IllegalArgumentException("Quantidade retirada deve ser positiva.");
        }
        if (qtd > quantidade) {
            throw new IllegalStateException("Estoque insuficiente. Disponível: " + quantidade);
        }
        quantidade -= qtd;
    }

    public void repor(int qtd) {
        if (qtd <= 0) {
            throw new IllegalArgumentException("Reposição deve ser positiva.");
        }
        quantidade += qtd;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    @Override
    public String resumo() {
        return "Lote " + getId() + " | " + quantidade + " " + unidadeMedida + " | validade: " + dataValidade;
    }
}
