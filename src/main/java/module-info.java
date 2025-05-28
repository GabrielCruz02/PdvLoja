module br.com.pdvloja {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens br.com.pdvloja.controller to javafx.fxml;
    exports br.com.pdvloja.app;
}
