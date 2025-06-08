package br.com.pdvloja.dao;

import br.com.pdvloja.util.ConnectionFactory;
import br.com.pdvloja.model.Venda;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO {

    public int inserir(Venda venda, int idCaixa) {
        String sqlInsert = "INSERT INTO vendas (id_caixa, data, hora, forma_pagamento, valor_total, valor_pago, troco) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlSelectId = "SELECT last_insert_rowid()"; // Comando específico do SQLite para pegar o último ID

        // É crucial usar a mesma conexão para as duas operações
        try (Connection conn = ConnectionFactory.getConnection()) {

            // Passo 1: Inserir a venda, sem pedir as chaves geradas
            try (PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                stmt.setInt(1, idCaixa);
                stmt.setString(2, venda.getDataHora().toLocalDate().format(dateFormatter));
                stmt.setString(3, venda.getDataHora().toLocalTime().format(timeFormatter));
                stmt.setString(4, venda.getFormaPagamento());
                stmt.setDouble(5, venda.getValorTotal());
                stmt.setDouble(6, venda.getValorPago());
                stmt.setDouble(7, venda.getTroco());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Falha ao criar a venda, nenhuma linha afetada.");
                }
            }

            // Passo 2: Buscar o ID da venda que acabamos de inserir na mesma conexão
            try (Statement stmtSelect = conn.createStatement();
                 ResultSet rs = stmtSelect.executeQuery(sqlSelectId)) {
                if (rs.next()) {
                    return rs.getInt(1); // Retorna o ID!
                } else {
                    throw new SQLException("Falha ao obter o ID da venda inserida.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir venda: " + e.getMessage(), e);
        }
    }

}

