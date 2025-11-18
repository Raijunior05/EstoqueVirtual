package app.model;

public class Despesa extends Transacao {
    public Despesa(String descricao, double valor) {
        super(descricao, valor);
    }

    @Override
    public double calcularImpacto() {
        return -this.valor; // Retorna negativo (reduz caixa)
    }
}