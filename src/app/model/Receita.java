package app.model;

public class Receita extends Transacao {
    public Receita(String descricao, double valor, String data) {
        super(descricao, valor, data);
    }

    @Override
    public double calcularImpacto() {
        return this.valor; // Retorna positivo
    }
}