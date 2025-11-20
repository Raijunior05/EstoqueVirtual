package app.model;

public class Despesa extends Transacao {
    public Despesa(String descricao, double valor, String data) {
        super(descricao, valor, data);
    }

    @Override
    public double calcularImpacto() {
        return -this.valor; // Retorna negativo (reduz caixa)
    }
}