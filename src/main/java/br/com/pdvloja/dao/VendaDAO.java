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


        try (Connection conn = ConnectionFactory.getConnection()) {

            // Inserir a venda, sem pedir as chaves geradas
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

            // Buscar o ID da venda que acabamos de inserir na mesma conexão
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

    public List<Venda> listarPorCaixaId(int idCaixa) {
        List<Venda> vendas = new ArrayList<>();
        String sql = "SELECT * FROM vendas WHERE id_caixa = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCaixa);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Venda venda = new Venda();
                venda.setId(rs.getInt("id"));
                // Este campo de data/hora precisa ser montado, já que no banco estão separados
                // O ideal seria unir data e hora do banco em um LocalDateTime (sugestão de atualização futura).
                venda.setDataHora(java.time.LocalDate.parse(rs.getString("data")).atStartOfDay());
                venda.setFormaPagamento(rs.getString("forma_pagamento"));
                venda.setValorTotal(rs.getDouble("valor_total"));
                venda.setValorPago(rs.getDouble("valor_pago"));
                venda.setTroco(rs.getDouble("troco"));
                vendas.add(venda);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar vendas por caixa: " + e.getMessage(), e);
        }
        return vendas;
    }

}

