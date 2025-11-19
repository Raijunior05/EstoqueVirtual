package app.model;

public class Roteador extends Produto {
    private int velocidadeMbps;
    public Roteador(int id, String nome, String marca, double precoCusto, double preco, int estoque, int velocidadeMbps) {
        super(id, nome, "Roteador", marca, precoCusto, preco, estoque, 5);
        this.velocidadeMbps = velocidadeMbps;
    }
    public int getVelocidadeMbps() { return velocidadeMbps; }
}