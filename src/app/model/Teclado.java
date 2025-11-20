package app.model;

public class Teclado extends Produto {
    private String tipoSwitch; // Ex: Mecânico, Membrana
    public Teclado(int id, String nome, String marca, double precoCusto, double preco, int estoque, int estoqueMinimo, String dataCadastro, String tipoSwitch) {
        super(id, nome, "Teclado", marca, precoCusto, preco, estoque, estoqueMinimo, dataCadastro);
        this.tipoSwitch = tipoSwitch;
    }
    public String getTipoSwitch() { return tipoSwitch; }
}