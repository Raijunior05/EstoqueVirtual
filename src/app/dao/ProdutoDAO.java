package app.dao;

import app.db.Database;
import app.model.Produto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO implements ICrud<Produto> {

    @Override
    public void salvar(Produto p) {
        String sql = "INSERT INTO produtos (nome, categoria, marca, preco, estoque, estoque_minimo) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCategoria());
            stmt.setString(3, p.getMarca());
            stmt.setDouble(4, p.getPreco());
            stmt.setInt(5, p.getEstoque());
            stmt.setInt(6, p.getEstoqueMinimo());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<Produto> listarTodos() {
        List<Produto> lista = new ArrayList<>();
        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM produtos")) {
            while (rs.next()) {
                lista.add(new Produto(
                        rs.getInt("id"), rs.getString("nome"), rs.getString("categoria"),
                        rs.getString("marca"), rs.getDouble("preco"),
                        rs.getInt("estoque"), rs.getInt("estoque_minimo")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    @Override
    public void deletar(int id) {
        try (PreparedStatement stmt = Database.getConnection().prepareStatement("DELETE FROM produtos WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void atualizar(Produto p) {
        String sql = "UPDATE produtos SET nome=?, categoria=?, marca=?, preco=?, estoque=?, estoque_minimo=? WHERE id=?";
        try (java.sql.PreparedStatement stmt = app.db.Database.getConnection().prepareStatement(sql)) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCategoria());
            stmt.setString(3, p.getMarca());
            stmt.setDouble(4, p.getPreco());
            stmt.setInt(5, p.getEstoque());
            stmt.setInt(6, p.getEstoqueMinimo());
            stmt.setInt(7, p.getId()); // O ID é usado para saber qual atualizar
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
}