package app.model;

public abstract class Transacao {
    protected String descricao;
    protected double valor;

    public Transacao(String descricao, double valor) {
        this.descricao = descricao;
        this.valor = valor;
    }

    // Método Abstrato (Polimorfismo): Cada filha decide como impacta o caixa
    public abstract double calcularImpacto();

    public String getDescricao() { return descricao; }
    public double getValor() { return valor; }
}