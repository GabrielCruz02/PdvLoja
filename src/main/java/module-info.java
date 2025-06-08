module br.com.pdvloja {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.xerial.sqlitejdbc;

    opens br.com.pdvloja.controller to javafx.fxml;
    opens br.com.pdvloja.app to javafx.fxml;
    opens br.com.pdvloja.model to javafx.base;

    exports br.com.pdvloja.app;

    uses java.sql.Driver;
}