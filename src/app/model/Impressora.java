package app.model;

public class Impressora extends Produto {
    private String tipoImpressao; // Laser, Jato de Tinta
    public Impressora(int id, String nome, String marca, double preco, int estoque, String tipoImpressao) {
        super(id, nome, "Impressora", marca, preco, estoque, 5);
        this.tipoImpressao = tipoImpressao;
    }
    public String getTipoImpressao() { return tipoImpressao; }
}