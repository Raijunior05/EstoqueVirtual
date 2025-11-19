package app.model;

public class Microfone extends Produto {
    private String tipo; // Condensador, Dinâmico
    public Microfone(int id, String nome, String marca, double preco, int estoque, String tipo) {
        super(id, nome, "Microfone", marca, preco, estoque, 5);
        this.tipo = tipo;
    }
    public String getTipo() { return tipo; }
}