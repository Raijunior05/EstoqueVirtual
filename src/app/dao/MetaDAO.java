package app.dao;

import app.db.Database;
import java.sql.*;

public class MetaDAO {

    public void definirMeta(double valor) {
        String sql = "UPDATE meta_financeira SET valor = ? WHERE id = 1";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setDouble(1, valor);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getMetaAtual() {
        String sql = "SELECT valor FROM meta_financeira WHERE id = 1";
        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("valor");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}