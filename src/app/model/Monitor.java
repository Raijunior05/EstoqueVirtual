package app.model;

public class Monitor extends Produto {
    private double polegadas;
    public Monitor(int id, String nome, String marca, double precoCusto, double preco, int estoque, double polegadas) {
        super(id, nome, "Monitor", marca, precoCusto, preco, estoque, 5);
        this.polegadas = polegadas;
    }
    public double getPolegadas() { return polegadas; }
}