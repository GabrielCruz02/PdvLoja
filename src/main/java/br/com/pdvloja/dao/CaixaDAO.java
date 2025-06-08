package br.com.pdvloja.dao;

import br.com.pdvloja.util.ConnectionFactory;
import br.com.pdvloja.model.Caixa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class CaixaDAO {

    public void abrirCaixa(Caixa caixa) {
        // MUDANÇA: Inserindo nos campos data_abertura e hora_abertura
        String sql = "INSERT INTO caixa (data_abertura, hora_abertura, valor_inicial) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Formatadores para garantir o padrão de texto do banco
            DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE; // ex: 2025-06-08
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss"); // ex: 10:51:00

            stmt.setString(1, caixa.getData().format(dateFormatter));
            stmt.setString(2, LocalTime.now().format(timeFormatter)); // Usando a hora atual para a abertura
            stmt.setDouble(3, caixa.getValorInicial());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao abrir caixa: " + e.getMessage(), e);
        }
    }

    public void fecharCaixa(int caixaId, double valorFinal) {
        String sql = "UPDATE caixa SET valor_final = ?, aberto = 0 WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, valorFinal);
            stmt.setInt(2, caixaId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao fechar caixa: " + e.getMessage(), e);
        }
    }

    public Caixa buscarCaixaAberto() {
        // MUDANÇA: A lógica de um caixa aberto é não ter data de fechamento
        String sql = "SELECT * FROM caixa WHERE data_fechamento IS NULL LIMIT 1";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                Caixa caixa = new Caixa();
                caixa.setId(rs.getInt("id"));
                caixa.setData(LocalDateTime.parse(rs.getString("data_abertura")));
                caixa.setValorInicial(rs.getDouble("valor_inicial"));
                // O caixa está aberto pois data_fechamento é NULL
                caixa.setAberto(true);
                return caixa;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar caixa aberto: " + e.getMessage(), e);
        }
        return null; // Nenhum caixa aberto encontrado
    }

}

