package br.com.pdvloja.dao;

import br.com.pdvloja.util.ConnectionFactory;
import br.com.pdvloja.model.Venda;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO {

    public int inserir(Venda venda, int idCaixa) {
        // MUDANÇA: Adicionamos id_caixa, data e hora separados
        String sql = "INSERT INTO vendas (id_caixa, data, hora, forma_pagamento, valor_total, valor_pago, troco) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            stmt.setInt(1, idCaixa);
            stmt.setString(2, venda.getDataHora().toLocalDate().format(dateFormatter));
            stmt.setString(3, venda.getDataHora().toLocalTime().format(timeFormatter));
            stmt.setString(4, venda.getFormaPagamento());
            stmt.setDouble(5, venda.getValorTotal());
            stmt.setDouble(6, venda.getValorPago());
            stmt.setDouble(7, venda.getTroco());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar a venda, nenhum ID obtido.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir venda: " + e.getMessage(), e);
        }
    }

}

