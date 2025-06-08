package br.com.pdvloja.controller;

import br.com.pdvloja.dao.CaixaDAO;
import br.com.pdvloja.model.Caixa;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class AberturaCaixaController {

    // @FXML conecta as variáveis do Java com os componentes do FXML pelo fx:id
    @FXML
    private TextField valorInicialField;

    @FXML
    private Button abrirCaixaButton;

    @FXML
    private Label mensagemErroLabel;

    // Este método é chamado quando o botão "Abrir Caixa" é clicado
    @FXML
    private void handleAbrirCaixa() {
        String valorTexto = valorInicialField.getText();

        // --- Validação do Input ---
        if (valorTexto.isEmpty()) {
            mensagemErroLabel.setText("O valor inicial não pode ser vazio.");
            return;
        }

        double valorInicial;
        try {
            valorInicial = Double.parseDouble(valorTexto.replace(",", "."));
            if (valorInicial < 0) {
                mensagemErroLabel.setText("O valor não pode ser negativo.");
                return;
            }
        } catch (NumberFormatException e) {
            mensagemErroLabel.setText("Valor inválido. Use apenas números.");
            return;
        }

        // --- Lógica de Negócio ---
        CaixaDAO caixaDAO = new CaixaDAO();
        Caixa novoCaixa = new Caixa();
        novoCaixa.setData(LocalDate.now());
        novoCaixa.setValorInicial(valorInicial);

        caixaDAO.abrirCaixa(novoCaixa);

        System.out.println("Caixa aberto com sucesso com o valor: " + valorInicial);

        // --- Navegação para a Tela Principal ---
        irParaTelaPrincipal();
    }

    private void irParaTelaPrincipal() {
        try {
            // Carrega a tela principal que você já tinha
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/pdvloja/fxml/MainView.fxml"));
            Scene scene = new Scene(loader.load());

            // Pega o "palco" (a janela) atual a partir de qualquer componente, como o botão
            Stage stage = (Stage) abrirCaixaButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("PDV - Vendas"); // Pode mudar o título se quiser
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mensagemErroLabel.setText("Erro ao carregar a tela principal.");
        }
    }
}
