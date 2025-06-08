module br.com.pdvloja {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens br.com.pdvloja.controller to javafx.fxml;
    opens br.com.pdvloja.app to javafx.fxml;

    // Permite que a base do JavaFX (TableView, etc.) leia os dados do pacote de modelos
    opens br.com.pdvloja.model to javafx.base;

    exports br.com.pdvloja.app;
}
