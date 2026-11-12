package model;

public abstract class Registro {
    private final String id;

    public Registro(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID obrigatório.");
        }
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public abstract String resumo();

    @Override
    public String toString() {
        return resumo();
    }
}
