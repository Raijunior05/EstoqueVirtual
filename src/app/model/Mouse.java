package app.model;

public class Mouse extends Produto {
    private int dpi;
    public Mouse(int id, String nome, String marca, double precoCusto, double preco, int estoque, int estoqueMinimo, String dataCadastro, int dpi) {
        super(id, nome, "Mouse", marca, precoCusto, preco, estoque, estoqueMinimo, dataCadastro);
        this.dpi = dpi;
    }
    public int getDpi() { return dpi; }
}