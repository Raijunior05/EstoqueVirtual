package app.model;

public class Impressora extends Produto {
    private String tipoImpressao; // Laser, Jato de Tinta
    public Impressora(int id, String nome, String marca, double precoCusto, double preco, int estoque, int estoqueMinimo, String dataCadastro ,String tipoImpressao) {
        super(id, nome, "Impressora", marca, precoCusto,preco, estoque, estoqueMinimo, dataCadastro);
        this.tipoImpressao = tipoImpressao;
    }
    public String getTipoImpressao() { return tipoImpressao; }
}