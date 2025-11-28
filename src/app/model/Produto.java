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
    private String dataCadastro;
    private int qtdVendida;
    private double valorTotalVendido;

    public Produto(int id, String nome, String categoria, String marca, double precoCusto, double preco, int estoque, int estoqueMinimo, String dataCadastro) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.marca = marca;
        this.precoCusto = precoCusto;
        this.preco = preco;
        this.estoque = estoque;
        this.estoqueMinimo = estoqueMinimo;
        this.dataCadastro = dataCadastro;
        // qtdVendida e valorTotalVendido começam com 0, não precisam estar no construtor
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
    public String getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(String dataCadastro) { this.dataCadastro = dataCadastro; }
    public int getQtdVendida() { return qtdVendida; }
    public void setQtdVendida(int qtdVendida) { this.qtdVendida = qtdVendida; }
    public double getValorTotalVendido() { return valorTotalVendido; }
    public void setValorTotalVendido(double valorTotalVendido) { this.valorTotalVendido = valorTotalVendido; }
}