package app.model;

public class Produto {
    private int id;
    private String nome;
    private String categoria;
    private String marca;
    private double precoCusto;
    private double preco;   //preco da venda
    private int estoque;
    private int estoqueMinimo; // Gatilho para avisos LLL

    public Produto(int id, String nome, String categoria, String marca, double precoCusto, double preco, int estoque, int estoqueMinimo) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.marca = marca;
        this.precoCusto = precoCusto; // NOVO
        this.preco = preco;
        this.estoque = estoque;
        this.estoqueMinimo = estoqueMinimo;
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
    public double getPrecoCusto() {return precoCusto;}
    public void setPrecoCusto(double precoCusto) {this.precoCusto = precoCusto;}
}