package app.model;

public class Produto {
    private int id;
    private String nome;
    private String categoria;
    private String marca;
    private double preco;
    private int estoque;
    private int estoqueMinimo; // Gatilho para avisos LLL

    public Produto(int id, String nome, String categoria, String marca, double preco, int estoque, int estoqueMinimo) {
        this.id = id; this.nome = nome; this.categoria = categoria;
        this.marca = marca; this.preco = preco; this.estoque = estoque; this.estoqueMinimo = estoqueMinimo;
    }
    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    // Getters essenciais para o JavaFX TableView
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public String getMarca() { return marca; }
    public double getPreco() { return preco; }
    public int getEstoque() { return estoque; }
    public int getEstoqueMinimo() { return estoqueMinimo; }
}