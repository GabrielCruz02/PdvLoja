package br.com.pdvloja.util;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        // Define o caminho para o banco de dados na pasta pessoal do usuário
        String userHome = System.getProperty("user.home");
        File dbFile = new File(userHome, "PdvLojaData/pdv.db");

        // Se o arquivo do banco de dados NÃO existir, cria as tabelas.
        if (!dbFile.exists()) {
            System.out.println("Banco de dados não encontrado. Criando novo banco e tabelas...");
            createTables();
        } else {
            System.out.println("Banco de dados encontrado.");
        }
    }

    private static void createTables() {
        // Todos os comandos para criar as tabelas que definimos
        String[] createTableStatements = {
                "CREATE TABLE IF NOT EXISTS produtos (id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT NOT NULL, preco REAL NOT NULL);",
                "CREATE TABLE IF NOT EXISTS caixa (id INTEGER PRIMARY KEY AUTOINCREMENT, data_abertura TEXT NOT NULL, hora_abertura TEXT NOT NULL, valor_inicial REAL NOT NULL, data_fechamento TEXT, hora_fechamento TEXT, total_dinheiro REAL DEFAULT 0, total_cartao REAL DEFAULT 0, total_pix REAL DEFAULT 0, total_geral REAL DEFAULT 0);",
                "CREATE TABLE IF NOT EXISTS vendas (id INTEGER PRIMARY KEY AUTOINCREMENT, id_caixa INTEGER NOT NULL, data TEXT NOT NULL, hora TEXT NOT NULL, forma_pagamento TEXT NOT NULL, valor_total REAL NOT NULL, valor_pago REAL, troco REAL, FOREIGN KEY (id_caixa) REFERENCES caixa(id));",
                "CREATE TABLE IF NOT EXISTS venda_itens (id INTEGER PRIMARY KEY AUTOINCREMENT, id_venda INTEGER NOT NULL, id_produto INTEGER NOT NULL, quantidade INTEGER NOT NULL, preco_unitario REAL NOT NULL, preco_total REAL NOT NULL, FOREIGN KEY (id_venda) REFERENCES vendas(id), FOREIGN KEY (id_produto) REFERENCES produtos(id));"
        };

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String sql : createTableStatements) {
                stmt.execute(sql);
            }
            System.out.println("Tabelas criadas com sucesso.");

        } catch (SQLException e) {
            // Em vez de lançar RuntimeException, vamos imprimir o erro no log
            // que já configuramos no MainApp.
            System.err.println("Erro ao criar as tabelas do banco de dados.");
            e.printStackTrace(System.err);
        }
    }
}
