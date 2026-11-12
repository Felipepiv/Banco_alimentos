package model;

public class Beneficiario extends Registro implements Validavel {
    private String nome;
    private int capacidadeArmazenamento;
    private int prioridade;

    public Beneficiario(String id, String nome, int capacidadeArmazenamento, int prioridade) {
        super(id);
        this.nome = nome;
        this.capacidadeArmazenamento = capacidadeArmazenamento;
        this.prioridade = prioridade;
        validar();
    }

    @Override
    public void validar() {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do beneficiário obrigatório.");
        }
        if (capacidadeArmazenamento < 0) {
            throw new IllegalArgumentException("Capacidade de armazenamento inválida.");
        }
    }

    public String getNome() { return nome; }
    public int getCapacidadeArmazenamento() { return capacidadeArmazenamento; }
    public int getPrioridade() { return prioridade; }

    @Override
    public String resumo() {
        return "Beneficiário " + getId() + " | " + nome + " (Prioridade: " + prioridade + ")";
    }
}
