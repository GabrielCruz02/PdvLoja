package br.com.pdvloja.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    // Define o caminho do banco de dados na pasta pessoal do usuário
    private static final String DBNAME = "pdv.db";
    private static final String DBPOLDER_PATH = System.getProperty("user.home") + "/PdvLojaData";
    private static final String DB_URL = "jdbc:sqlite:" + DBPOLDER_PATH + "/" + DBNAME;

    public static Connection getConnection() throws SQLException {
        // Garante que o diretório exista antes de tentar conectar
        File dbFolder = new File(DBPOLDER_PATH);
        if (!dbFolder.exists()) {
            dbFolder.mkdirs();
        }
        return DriverManager.getConnection(DB_URL);
    }
}