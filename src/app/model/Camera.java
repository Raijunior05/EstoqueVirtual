package app.model;

public class Camera extends Produto {
    private String resolucao; // 1080p, 4K
    public Camera(int id, String nome, String marca, double precoCusto, double preco, int estoque, int estoqueMinimo, String dataCadastro, String resolucao) {
        super(id, nome, "Camera", marca, precoCusto, preco, estoque, estoqueMinimo, dataCadastro);
        this.resolucao = resolucao;
    }
    public String getResolucao() { return resolucao; }
}