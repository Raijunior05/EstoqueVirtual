package app.service;

import app.dao.ProdutoDAO;
import app.model.Produto;
import java.util.ArrayList;
import java.util.List;

public class MonitorService {

    // Lógica LLL: Verifica quais produtos estão acabando
    public List<String> verificarAlertas() {
        ProdutoDAO dao = new ProdutoDAO();
        List<Produto> todos = dao.listarTodos();
        List<String> alertas = new ArrayList<>();

        for (Produto p : todos) {
            if (p.getEstoque() <= p.getEstoqueMinimo()) {
                alertas.add("⚠️ ALERTA: " + p.getNome() + " está acabando! (Restam: " + p.getEstoque() + ")");
            }
        }
        return alertas;
    }
}