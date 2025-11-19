package app.dao;

import app.db.Database;
import app.model.Despesa;
import app.model.Receita;
import app.model.Transacao;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransacaoDAO {

    public void salvar(Transacao t) {
        String sql = "INSERT INTO transacoes (data_hora, tipo, descricao, valor) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String tipo = (t instanceof Receita) ? "RECEITA" : "DESPESA";

            stmt.setString(1, dataHora);
            stmt.setString(2, tipo);
            stmt.setString(3, t.getDescricao());
            stmt.setDouble(4, t.getValor());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Transacao> listarTodas() {
        List<Transacao> lista = new ArrayList<>();
        String sql = "SELECT * FROM transacoes ORDER BY data_hora DESC"; // Mais recentes primeiro

        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String tipo = rs.getString("tipo");
                String desc = rs.getString("descricao");
                double valor = rs.getDouble("valor");

                if ("RECEITA".equals(tipo)) {
                    lista.add(new Receita(desc, valor));
                } else {
                    lista.add(new Despesa(desc, valor));
                }
                // Nota: Se quiser guardar a data no objeto, precisaria adicionar o campo Data na classe Transacao
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}