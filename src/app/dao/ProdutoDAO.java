package app.dao;

import app.db.Database;
import app.model.*; // Importa todas as subclasses (Mouse, Monitor, Teclado, etc.)
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO implements ICrud<Produto> {

    // --- SALVAR (INSERT) ---
    @Override
    public void salvar(Produto p) {
        String sql = "INSERT INTO produtos (nome, categoria, marca, preco, estoque, estoque_minimo, tipo_produto, especificacao_int, especificacao_double, especificacao_texto) VALUES (?,?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            // 1. Dados Comuns
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCategoria());
            stmt.setString(3, p.getMarca());
            stmt.setDouble(4, p.getPreco());
            stmt.setInt(5, p.getEstoque());
            stmt.setInt(6, p.getEstoqueMinimo());

            // 2. Dados Específicos (Polimorfismo no Banco)
            configurarStatementEspecifico(stmt, p, 7, 8, 9, 10);

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao salvar produto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- ATUALIZAR (UPDATE) ---
    @Override
    public void atualizar(Produto p) {
        String sql = "UPDATE produtos SET nome=?, categoria=?, marca=?, preco=?, estoque=?, estoque_minimo=?, tipo_produto=?, especificacao_int=?, especificacao_double=?, especificacao_texto=? WHERE id=?";

        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCategoria());
            stmt.setString(3, p.getMarca());
            stmt.setDouble(4, p.getPreco());
            stmt.setInt(5, p.getEstoque());
            stmt.setInt(6, p.getEstoqueMinimo());

            // Configura os dados específicos nas posições corretas
            configurarStatementEspecifico(stmt, p, 7, 8, 9, 10);

            stmt.setInt(11, p.getId()); // ID para o WHERE

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar produto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- LISTAR TODOS (SELECT) ---
    @Override
    public List<Produto> listarTodos() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produtos";

        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(criarObjetoDoResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // --- DELETAR (DELETE) ---
    @Override
    public void deletar(int id) {
        String sql = "DELETE FROM produtos WHERE id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==================================================================================
    // MÉTODOS AUXILIARES (Para não repetir código gigante)
    // ==================================================================================

    /**
     * Decide o que salvar nas colunas genéricas dependendo da classe do objeto.
     */
    private void configurarStatementEspecifico(PreparedStatement stmt, Produto p, int idxTipo, int idxInt, int idxDouble, int idxTexto) throws SQLException {
        // Limpa os campos opcionais primeiro (define como NULL)
        stmt.setNull(idxInt, Types.INTEGER);
        stmt.setNull(idxDouble, Types.REAL);
        stmt.setNull(idxTexto, Types.VARCHAR);

        if (p instanceof Mouse) {
            stmt.setString(idxTipo, "Mouse");
            stmt.setInt(idxInt, ((Mouse) p).getDpi());
        }
        else if (p instanceof Monitor) {
            stmt.setString(idxTipo, "Monitor");
            stmt.setDouble(idxDouble, ((Monitor) p).getPolegadas());
        }
        else if (p instanceof Teclado) {
            stmt.setString(idxTipo, "Teclado");
            stmt.setString(idxTexto, ((Teclado) p).getTipoSwitch());
        }
        else if (p instanceof Armazenamento) {
            stmt.setString(idxTipo, "Armazenamento");
            stmt.setInt(idxInt, ((Armazenamento) p).getCapacidadeGB());
        }
        else if (p instanceof Roteador) {
            stmt.setString(idxTipo, "Roteador");
            stmt.setInt(idxInt, ((Roteador) p).getVelocidadeMbps());
        }
        else if (p instanceof Microfone) {
            stmt.setString(idxTipo, "Microfone");
            stmt.setString(idxTexto, ((Microfone) p).getTipo());
        }
        else if (p instanceof Camera) {
            stmt.setString(idxTipo, "Camera");
            stmt.setString(idxTexto, ((Camera) p).getResolucao());
        }
        else if (p instanceof Fone) {
            stmt.setString(idxTipo, "Fone");
            stmt.setString(idxTexto, ((Fone) p).getConexao());
        }
        else if (p instanceof Impressora) {
            stmt.setString(idxTipo, "Impressora");
            stmt.setString(idxTexto, ((Impressora) p).getTipoImpressao());
        }
        else if (p instanceof Controle) {
            stmt.setString(idxTipo, "Controle");
            stmt.setString(idxTexto, ((Controle) p).getCompatibilidade());
        }
        else {
            // Produto Genérico (Outros)
            stmt.setString(idxTipo, "Outros");
        }
    }

    /**
     * Lê o banco e cria a instância correta (Factory Method).
     */
    private Produto criarObjetoDoResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        String marca = rs.getString("marca");
        double preco = rs.getDouble("preco");
        int estoque = rs.getInt("estoque");
        String tipo = rs.getString("tipo_produto");

        // Lê as colunas genéricas
        int specInt = rs.getInt("especificacao_int");
        double specDouble = rs.getDouble("especificacao_double");
        String specTexto = rs.getString("especificacao_texto");

        if (tipo == null) tipo = "Outros";

        switch (tipo) {
            case "Mouse":
                return new Mouse(id, nome, marca, preco, estoque, specInt);
            case "Monitor":
                return new Monitor(id, nome, marca, preco, estoque, specDouble);
            case "Teclado":
                return new Teclado(id, nome, marca, preco, estoque, specTexto);
            case "Armazenamento":
                return new Armazenamento(id, nome, marca, preco, estoque, specInt);
            case "Roteador":
                return new Roteador(id, nome, marca, preco, estoque, specInt);
            case "Microfone":
                return new Microfone(id, nome, marca, preco, estoque, specTexto);
            case "Camera":
                return new Camera(id, nome, marca, preco, estoque, specTexto);
            case "Fone":
                return new Fone(id, nome, marca, preco, estoque, specTexto);
            case "Impressora":
                return new Impressora(id, nome, marca, preco, estoque, specTexto);
            case "Controle":
                return new Controle(id, nome, marca, preco, estoque, specTexto);
            default:
                return new Produto(id, nome, "Geral", marca, preco, estoque, 5);
        }
    }
}