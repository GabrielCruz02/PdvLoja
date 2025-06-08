package br.com.pdvloja.app;

import br.com.pdvloja.dao.CaixaDAO;
import br.com.pdvloja.model.Caixa;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        // Instanciar o DAO para acessar o banco
        CaixaDAO caixaDAO = new CaixaDAO();
        Caixa caixaAberto = caixaDAO.buscarCaixaAberto();

        String fxmlInicial;

        // Lógica para decidir qual tela abrir
        if (caixaAberto == null) {
            // Se não há caixa aberto, a primeira tela será a de abertura
            System.out.println("Nenhum caixa aberto. Iniciando tela de abertura.");
            fxmlInicial = "/br/com/pdvloja/fxml/AberturaCaixaView.fxml";
        } else {
            // Se já existe um caixa aberto, vai para a tela principal
            System.out.println("Caixa já está aberto. Iniciando tela principal.");
            fxmlInicial = "/br/com/pdvloja/fxml/MainView.fxml";
        }

        // Carregar e mostrar a tela decidida
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource(fxmlInicial));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("PDV Loja de Bolos");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}