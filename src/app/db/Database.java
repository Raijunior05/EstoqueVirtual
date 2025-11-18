package app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // --- A MÁGICA ACONTECE AQUI ---
                // Força o Java a carregar a classe do Driver na memória
                Class.forName("org.sqlite.JDBC");

                // Agora a conexão vai funcionar
                connection = DriverManager.getConnection("jdbc:sqlite:loja_tech.db");
                criarTabelas();

            } catch (ClassNotFoundException e) {
                System.err.println("❌ CRÍTICO: Driver do SQLite não encontrado! Verifique se o .jar está na biblioteca.");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("❌ Erro ao conectar com o banco de dados.");
                e.printStackTrace();
            }
        }
        return connection;
    }

    private static void criarTabelas() throws SQLException {
        // Se a conexão falhou acima, connection será null, então evitamos o erro aqui
        if (connection == null) return;

        Statement stmt = connection.createStatement();

        // 1. Tabela de Produtos (Tech)
        stmt.execute("CREATE TABLE IF NOT EXISTS produtos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT, categoria TEXT, marca TEXT, " +
                "preco REAL, estoque INTEGER, estoque_minimo INTEGER)");

        // 2. Tabela de Transações (Financeiro)
        stmt.execute("CREATE TABLE IF NOT EXISTS transacoes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tipo TEXT, descricao TEXT, valor REAL, data TEXT)");
    }
}