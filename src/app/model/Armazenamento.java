package app.model;

public class Armazenamento extends Produto {
    private int capacidadeGB; // Ex: 500, 1000 (1TB)
    public Armazenamento(int id, String nome, String marca, double precoCusto, double preco, int estoque, int capacidadeGB) {
        super(id, nome, "Armazenamento", marca, precoCusto, preco, estoque, 5);
        this.capacidadeGB = capacidadeGB;
    }
    public int getCapacidadeGB() { return capacidadeGB; }
}