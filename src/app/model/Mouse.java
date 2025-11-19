package app.model;

public class Mouse extends Produto {
    private int dpi;
    public Mouse(int id, String nome, String marca, double preco, int estoque, int dpi) {
        super(id, nome, "Mouse", marca, preco, estoque, 5); // 5 é estoque minimo padrao
        this.dpi = dpi;
    }
    public int getDpi() { return dpi; }
}