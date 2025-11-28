package app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    // Variável estática para guardar a conexão única (Singleton)
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // 1. FORÇA O CARREGAMENTO DO DRIVER (Evita erro "No suitable driver")
                Class.forName("org.sqlite.JDBC");

                // 2. Conecta (ou cria) o arquivo do banco na raiz do projeto
                connection = DriverManager.getConnection("jdbc:sqlite:loja_tech.db");

                // 3. Garante que as tabelas existam assim que conectar
                criarTabelas();

            } catch (ClassNotFoundException e) {
                System.err.println("❌ CRÍTICO: Driver do SQLite não encontrado! Verifique as bibliotecas.");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("❌ Erro ao conectar com o banco de dados.");
                e.printStackTrace();
            }
        }
        return connection;
    }

    private static void criarTabelas() throws SQLException {
        if (connection == null) return;

        Statement stmt = connection.createStatement();

        // --- TABELA DE PRODUTOS (COM SUPORTE A SUBCLASSES) ---
        // Estratégia: Single Table Inheritance (Tabela Única)
        // Todas as subclasses salvam aqui, usando as colunas 'especificacao_' conforme a necessidade.
        String sqlProdutos = """
            CREATE TABLE IF NOT EXISTS produtos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT,
                categoria TEXT,
                marca TEXT,
                preco_custo REAL,  
                preco REAL, --preço da Venda
                estoque INTEGER,
                estoque_minimo INTEGER,
                
                data_cadastro TEXT,
                qtd_vendida INTEGER DEFAULT 0,
                valor_total_vendido REAL DEFAULT 0.0,
                
                -- Colunas para Polimorfismo (Subclasses) --
                tipo_produto TEXT,          -- Ex: 'Mouse', 'Monitor', 'Teclado'
                especificacao_int INTEGER,  -- Mouse(DPI), Armazenamento(GB), Roteador(Mbps)
                especificacao_double REAL,  -- Monitor(Polegadas)
                especificacao_texto TEXT    -- Teclado(Switch), Fone(Conexão), etc.
            );
        """;

        // --- TABELA FINANCEIRA (PARA O FUTURO DASHBOARD) ---
        String sqlTransacoes = """
            CREATE TABLE IF NOT EXISTS transacoes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                data_hora TEXT,
                tipo TEXT,        -- 'RECEITA' ou 'DESPESA'
                descricao TEXT,
                valor REAL
            );
        """;

        // --- TABELA DE META (Guarda apenas 1 registro, ID=1) ---
        String sqlMeta = "CREATE TABLE IF NOT EXISTS meta_financeira (id INTEGER PRIMARY KEY, valor REAL)";
        stmt.execute(sqlMeta);

        // Inicializa com 0 se estiver vazia para evitar erros
        stmt.execute("INSERT OR IGNORE INTO meta_financeira (id, valor) VALUES (1, 0.0)");

        stmt.execute(sqlProdutos);
        stmt.execute(sqlTransacoes);
    }
}