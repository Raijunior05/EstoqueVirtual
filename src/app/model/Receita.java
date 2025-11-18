package app.model;

public class Receita extends Transacao {
    public Receita(String descricao, double valor) {
        super(descricao, valor);
    }

    @Override
    public double calcularImpacto() {
        return this.valor; // Retorna positivo
    }
}