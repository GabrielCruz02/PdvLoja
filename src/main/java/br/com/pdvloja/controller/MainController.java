package br.com.pdvloja.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
    @FXML
    private Label welcomeLabel;

    @FXML
    protected void onHelloButtonClick() {
        welcomeLabel.setText("Bem-vindo ao PDV da Loja de Bolos!");
    }
}
