package app.service;

import app.dao.TransacaoDAO;
import app.model.Transacao;
import app.dao.MetaDAO;
import java.util.List;

public class FinanceiroService {

    private TransacaoDAO dao = new TransacaoDAO();
    private MetaDAO metaDAO = new MetaDAO();

    public void registrarReceita(String descricao, double valor, String data) {
        dao.salvar(new app.model.Receita(descricao, valor, data), data);
    }

    public void registrarDespesa(String descricao, double valor, String data) {
        dao.salvar(new app.model.Despesa(descricao, valor, data), data);
    }

    public List<Transacao> getHistorico() {
        return dao.listarTodas();
    }

    // Calcula o Saldo Atual
    public double calcularSaldo() {
        List<Transacao> transacoes = dao.listarTodas();
        double saldo = 0;
        for (Transacao t : transacoes) {
            saldo += t.calcularImpacto(); // Polimorfismo: Receita soma, Despesa subtrai
        }
        return saldo;
    }

    // Calcula apenas Entradas
    public double calcularTotalEntradas() {
        return dao.listarTodas().stream()
                .filter(t -> t instanceof app.model.Receita)
                .mapToDouble(Transacao::getValor)
                .sum();
    }

    // Calcula apenas Saídas
    public double calcularTotalSaidas() {
        return dao.listarTodas().stream()
                .filter(t -> t instanceof app.model.Despesa)
                .mapToDouble(Transacao::getValor)
                .sum();
    }

    // --- MÉTODOS DA META ---

    public void salvarMeta(double valor) {
        metaDAO.definirMeta(valor);
    }

    public double getMeta() {
        return metaDAO.getMetaAtual();
    }

    // Calcula quanto falta para atingir a meta (Meta - Saldo Atual)
    public double calcularFaltaParaMeta() {
        double saldo = calcularSaldo();
        double meta = getMeta();

        if (saldo >= meta) return 0.0; // Já bateu a meta!
        return meta - saldo;
    }

    // Retorna a porcentagem (0.0 a 1.0) para a barra de progresso
    public double calcularProgressoMeta() {
        double meta = getMeta();
        if (meta <= 0) return 0.0; // Evita divisão por zero

        double saldo = calcularSaldo();
        double progresso = saldo / meta;

        return Math.min(progresso, 1.0); // Trava em 100% (1.0) se passar
    }
}