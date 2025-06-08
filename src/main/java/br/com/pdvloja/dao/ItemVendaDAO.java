package br.com.pdvloja.dao;

import br.com.pdvloja.util.ConnectionFactory;
import br.com.pdvloja.model.ItemVenda;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ItemVendaDAO {

    public void inserir(ItemVenda item) {

        String sql = "INSERT INTO venda_itens (venda_id, produto_id, quantidade, preco_unitario, preco_total) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getVendaId());


            stmt.setInt(2, item.getProduto().getId());

            stmt.setInt(3, item.getQuantidade());
            stmt.setDouble(4, item.getPrecoUnitario());
            stmt.setDouble(5, item.getSubtotal());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir item da venda: " + e.getMessage(), e);
        }
    }
}
