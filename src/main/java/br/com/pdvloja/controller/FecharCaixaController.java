package br.com.pdvloja.controller;

import br.com.pdvloja.dao.CaixaDAO;
import br.com.pdvloja.model.Caixa;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FecharCaixaController {

    @FXML private Label dataAberturaLabel;
    @FXML private Label valorInicialLabel;
    @FXML private Label totalDinheiroLabel;
    @FXML private Label totalCartaoLabel;
    @FXML private Label totalPixLabel;
    @FXML private Label valorFinalCaixaLabel;

    @FXML private Button confirmarButton;
    @FXML private Button cancelarButton;

    private CaixaDAO caixaDAO;
    private Caixa caixaParaFechar;
    private double totalDinheiro, totalCartao, totalPix;

    // Este é um método customizado para passar os dados da tela principal para cá
    public void initData(Caixa caixaAberto, double totalDinheiro, double totalCartao, double totalPix) {
        this.caixaDAO = new CaixaDAO();
        this.caixaParaFechar = caixaAberto;
        this.totalDinheiro = totalDinheiro;
        this.totalCartao = totalCartao;
        this.totalPix = totalPix;

        // Calcula o valor final em dinheiro que deve estar no caixa
        double valorFinal = caixaAberto.getValorInicial() + totalDinheiro;

        // Formata e exibe os dados na tela
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dataAberturaLabel.setText(caixaAberto.getData().format(formatter));
        valorInicialLabel.setText(String.format("R$ %.2f", caixaAberto.getValorInicial()));
        totalDinheiroLabel.setText(String.format("R$ %.2f", totalDinheiro));
        totalCartaoLabel.setText(String.format("R$ %.2f", totalCartao));
        totalPixLabel.setText(String.format("R$ %.2f", totalPix));
        valorFinalCaixaLabel.setText(String.format("R$ %.2f", valorFinal));
    }

    @FXML
    private void handleConfirmarFechamento() {
        // Pega os valores calculados
        double valorFinalDinheiro = caixaParaFechar.getValorInicial() + totalDinheiro;
        double totalGeral = totalDinheiro + totalCartao + totalPix;

        // Atualiza o objeto Caixa e chama o DAO para salvar no banco
        caixaDAO.fecharCaixa(caixaParaFechar.getId(), totalDinheiro, totalCartao, totalPix, totalGeral);

        // Fecha a aplicação
        Platform.exit();
    }

    @FXML
    private void handleCancelar() {
        // Simplesmente fecha a janela de relatório
        Stage stage = (Stage) cancelarButton.getScene().getWindow();
        stage.close();
    }
}