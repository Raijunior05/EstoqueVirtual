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
        String sql = "INSERT INTO produtos (nome, categoria, marca, preco_custo, preco, estoque, estoque_minimo, data_cadastro, tipo_produto, especificacao_int, especificacao_double, especificacao_texto) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            // 1. Dados Comuns
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCategoria());
            stmt.setString(3, p.getMarca());
            stmt.setDouble(4, p.getPrecoCusto());
            stmt.setDouble(5, p.getPreco());
            stmt.setInt(6, p.getEstoque());
            stmt.setInt(7, p.getEstoqueMinimo());
            stmt.setString(8, p.getDataCadastro());

            // 2. Dados Específicos (Polimorfismo no Banco)
            configurarStatementEspecifico(stmt, p, 9, 10, 11, 12);

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao salvar produto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- ATUALIZAR (UPDATE) ---
    @Override
    public void atualizar(Produto p) {
        String sql = "UPDATE produtos SET nome=?, categoria=?, marca=?, preco_custo=?, preco=?, estoque=?, estoque_minimo=?, qtd_vendida=?, valor_total_vendido=?, tipo_produto=?, especificacao_int=?, especificacao_double=?, especificacao_texto=? WHERE id=?";

        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCategoria());
            stmt.setString(3, p.getMarca());
            stmt.setDouble(4, p.getPrecoCusto());
            stmt.setDouble(5, p.getPreco());
            stmt.setInt(6, p.getEstoque());
            stmt.setInt(7, p.getEstoqueMinimo());
            stmt.setInt(8, p.getQtdVendida());
            stmt.setDouble(9, p.getValorTotalVendido());

            // Configura os dados específicos nas posições corretas
            configurarStatementEspecifico(stmt, p, 10, 11, 12, 13);

            stmt.setInt(14, p.getId()); // ID para o WHERE

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
    // MÉTODOS AUXILIARES
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
        double precoCusto = rs.getDouble("preco_custo");
        double preco = rs.getDouble("preco");
        int estoque = rs.getInt("estoque");
        int estMin = rs.getInt("estoque_minimo");
        String dataCad = rs.getString("data_cadastro");
        // Métricas
        int qtdVendida = rs.getInt("qtd_vendida");
        double totalVendido = rs.getDouble("valor_total_vendido");

        String tipo = rs.getString("tipo_produto");

        // Lê as colunas genéricas
        int specInt = rs.getInt("especificacao_int");
        double specDouble = rs.getDouble("especificacao_double");
        String specTexto = rs.getString("especificacao_texto");

        if (tipo == null) tipo = "Outros";

        Produto p = null;

        switch (tipo) {
            case "Mouse":
                return new Mouse(id, nome, marca, precoCusto, preco, estoque, estMin, dataCad, specInt);
            case "Monitor":
                return new Monitor(id, nome, marca, precoCusto, preco, estoque, estMin, dataCad, specDouble);
            case "Teclado":
                return new Teclado(id, nome, marca, precoCusto, preco, estoque, estMin, dataCad,specTexto);
            case "Armazenamento":
                return new Armazenamento(id, nome, marca, precoCusto, preco, estoque, estMin, dataCad, specInt);
            case "Roteador":
                return new Roteador(id, nome, marca, precoCusto, preco, estoque, estMin, dataCad, specInt);
            case "Microfone":
                return new Microfone(id, nome, marca, precoCusto, preco, estoque, estMin, dataCad, specTexto);
            case "Camera":
                return new Camera(id, nome, marca, precoCusto,preco, estoque, estMin, dataCad, specTexto);
            case "Fone":
                return new Fone(id, nome, marca, precoCusto, preco, estoque, estMin, dataCad, specTexto);
            case "Impressora":
                return new Impressora(id, nome, marca, precoCusto, preco, estoque, estMin, dataCad, specTexto);
            case "Controle":
                return new Controle(id, nome, marca, precoCusto, preco, estoque, estMin, dataCad, specTexto);
            default:
             p = new Produto(id, nome, "Geral", marca, precoCusto, preco, estoque, estMin, dataCad); break;
        }
        // Seta as métricas que não estão no construtor
        if (p != null) {
            p.setQtdVendida(qtdVendida);
            p.setValorTotalVendido(totalVendido);
        }
        return p;
    }
}