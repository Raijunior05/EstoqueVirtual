package app.model;

public class Teclado extends Produto {
    private String tipoSwitch; // Ex: Mecânico, Membrana
    public Teclado(int id, String nome, String marca, double preco, int estoque, String tipoSwitch) {
        super(id, nome, "Teclado", marca, preco, estoque, 5);
        this.tipoSwitch = tipoSwitch;
    }
    public String getTipoSwitch() { return tipoSwitch; }
}