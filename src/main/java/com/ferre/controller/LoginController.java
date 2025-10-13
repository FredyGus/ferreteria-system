package com.ferre.controller;

import com.ferre.config.Session;
import com.ferre.model.Usuario;
import com.ferre.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblError;

    private final AuthService auth = new AuthService();

    @FXML
    private void doLogin(ActionEvent e) {
        lblError.setText("");
        String u = txtUsuario.getText() == null ? "" : txtUsuario.getText().trim();
        String p = txtPassword.getText() == null ? "" : txtPassword.getText().trim();

        if (u.isEmpty() || p.isEmpty()) {
            lblError.setText("Usuario y contraseña son requeridos");
            return;
        }
        try {
            Usuario usr = auth.login(u, p);
            Session.set(usr);

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Scene scene = new Scene(fx.load(), 1024, 640);
            scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Ferretería — Sesión: " + usr.getUsuario() + " (" + usr.getRol() + ")");
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (Exception ex) {
            lblError.setText(ex.getMessage());
        }
    }
}
