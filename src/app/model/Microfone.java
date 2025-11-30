package app.model;

public class Microfone extends Produto {
    private String tipo; // Condensador, Dinâmico
    public Microfone(int id, String nome, String marca, double precoCusto,double preco, int estoque, int estoqueMinimo, String dataCadastro, String tipo) {
        super(id, nome, "Microfone", marca, precoCusto,preco, estoque, estoqueMinimo, dataCadastro);
        this.tipo = tipo;
        setEstoqueMinimo(100);
    }
    public String getTipo() { return tipo; }
}