package com.ferre.controller;

import com.ferre.config.DataSourceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.sql.Connection;

public class MainController {

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        statusLabel.setText("Estado: Listo");
    }

    @FXML
    private void testConnection(ActionEvent e) {
        try (Connection cn = DataSourceFactory.getConnection()) {
            String db = cn.getMetaData().getDatabaseProductName() + " " + cn.getMetaData().getDatabaseProductVersion();
            statusLabel.setText("Estado: Conectado a " + db);
            alert(Alert.AlertType.INFORMATION, "Conexión OK", "Conectado a: " + db);
        } catch (Exception ex) {
            statusLabel.setText("Estado: SIN CONEXIÓN");
            alert(Alert.AlertType.ERROR, "Error de conexión", ex.getMessage());
        }

    }

    @FXML
    private void exitApp(ActionEvent e) {
        System.exit(0);
    }

    private void alert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
