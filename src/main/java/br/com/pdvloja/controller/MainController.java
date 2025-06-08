package br.com.pdvloja.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class MainController {
    @FXML
    private Label welcomeLabel;

    @FXML
    protected void onHelloButtonClick() {
        welcomeLabel.setText("Bem-vindo ao PDV da Loja de Bolos!");
    }

    @FXML
    private void handleGerenciarProdutos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/pdvloja/fxml/ProdutoView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Gerenciamento de Produtos");
            stage.initModality(Modality.APPLICATION_MODAL); // Impede interação com a janela principal
            stage.setScene(scene);
            stage.showAndWait(); // Mostra a janela e espera ela ser fechada
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
