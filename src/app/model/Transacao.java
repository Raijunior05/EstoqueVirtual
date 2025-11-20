package app.model;

public abstract class Transacao {
    protected String descricao;
    protected double valor;
    protected String data;

    // Atualize o construtor para receber a data
    public Transacao(String descricao, double valor, String data) {
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
    }

    // Método Abstrato (Polimorfismo): Cada filha decide como impacta o caixa
    public abstract double calcularImpacto();

    public String getDescricao() { return descricao; }
    public double getValor() { return valor; }
    public String getData() {return data;}
}