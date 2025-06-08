package br.com.pdvloja.dao;

import br.com.pdvloja.model.Caixa;
import br.com.pdvloja.util.ConnectionFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CaixaDAO {

    /**
     * Insere um novo registro de caixa no banco, marcando-o como aberto.
     * @param caixa O objeto Caixa com a data e o valor inicial.
     */
    public void abrirCaixa(Caixa caixa) {
        String sql = "INSERT INTO caixa (data_abertura, hora_abertura, valor_inicial) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            stmt.setString(1, caixa.getData().format(dateFormatter));
            stmt.setString(2, LocalTime.now().format(timeFormatter));
            stmt.setDouble(3, caixa.getValorInicial());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao abrir caixa: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza o registro do caixa no banco com os totais e as datas/horas de fechamento.
     */
    public void fecharCaixa(int caixaId, double totalDinheiro, double totalCartao, double totalPix, double totalGeral) {
        String sql = "UPDATE caixa SET data_fechamento = ?, hora_fechamento = ?, total_dinheiro = ?, total_cartao = ?, total_pix = ?, total_geral = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            stmt.setString(1, LocalDate.now().format(dateFormatter));
            stmt.setString(2, LocalTime.now().format(timeFormatter));
            stmt.setDouble(3, totalDinheiro);
            stmt.setDouble(4, totalCartao);
            stmt.setDouble(5, totalPix);
            stmt.setDouble(6, totalGeral);
            stmt.setInt(7, caixaId); // O índice do ID muda de 8 para 7

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao fechar caixa: " + e.getMessage(), e);
        }
    }

    /**
     * Busca no banco de dados se existe algum caixa com o status "aberto".
     * Um caixa é considerado aberto se a sua data_fechamento for nula.
     * @return Um objeto Caixa se houver um aberto, ou null caso contrário.
     */
    public Caixa buscarCaixaAberto() {
        String sql = "SELECT * FROM caixa WHERE data_fechamento IS NULL LIMIT 1";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                Caixa caixa = new Caixa();
                caixa.setId(rs.getInt("id"));
                caixa.setData(LocalDate.parse(rs.getString("data_abertura")));
                caixa.setValorInicial(rs.getDouble("valor_inicial"));
                caixa.setAberto(true);
                return caixa;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar caixa aberto: " + e.getMessage(), e);
        }
        return null; // Retorna null se nenhum caixa estiver aberto
    }
}