package app.model;

public class Monitor extends Produto {
    private double polegadas;
    public Monitor(int id, String nome, String marca, double precoCusto, double preco, int estoque, int estoqueMinimo, String dataCadastro, double polegadas) {
        super(id, nome, "Monitor", marca, precoCusto, preco, estoque, estoqueMinimo, dataCadastro);
        this.polegadas = polegadas;
    }
    public double getPolegadas() { return polegadas; }
}