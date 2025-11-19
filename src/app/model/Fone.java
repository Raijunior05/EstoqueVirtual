package app.model;

public class Fone extends Produto {
    private String conexao; // Bluetooth, P2, USB
    public Fone(int id, String nome, String marca, double precoCusto, double preco, int estoque, String conexao) {
        super(id, nome, "Fone", marca, precoCusto, preco, estoque, 5);
        this.conexao = conexao;
    }
    public String getConexao() { return conexao; }
}
