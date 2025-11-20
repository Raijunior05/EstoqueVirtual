package app.model;

public class Controle extends Produto {
    private String compatibilidade; // Xbox, PS5, PC
    public Controle(int id, String nome, String marca, double precoCusto, double preco, int estoque, int estoqueMinimo, String dataCadastro, String compatibilidade) {
        super(id, nome, "Controle", marca, precoCusto, preco, estoque, estoqueMinimo, dataCadastro);
        this.compatibilidade = compatibilidade;
    }
    public String getCompatibilidade() { return compatibilidade; }
}