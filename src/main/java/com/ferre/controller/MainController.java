package com.ferre.controller;

import com.ferre.config.Session;
import com.ferre.model.Rol;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private Menu menuAdmin, menuBodega, menuVentas, menuCaja, menuCatalogos;
    @FXML
    private Label lblSesion;
    @FXML
    private BorderPane content;

    @FXML
    public void initialize() {
        var u = com.ferre.config.Session.get();
        lblSesion.setText("Sesión: " + u.getUsuario() + " (" + u.getRol() + ")");

        // visibilidad por rol
        menuAdmin.setVisible(u.getRol() == com.ferre.model.Rol.ADMIN);
        menuCatalogos.setVisible(u.getRol() == com.ferre.model.Rol.ADMIN); // solo ADMIN edita catálogos
        menuBodega.setVisible(u.getRol() == com.ferre.model.Rol.BODEGA || u.getRol() == com.ferre.model.Rol.ADMIN);
        menuVentas.setVisible(u.getRol() == com.ferre.model.Rol.VENTAS || u.getRol() == com.ferre.model.Rol.ADMIN);
        menuCaja.setVisible(u.getRol() == com.ferre.model.Rol.CAJA || u.getRol() == com.ferre.model.Rol.ADMIN);
    }

    @FXML
    private void exitApp() {
        System.exit(0);
    }

    @FXML
    private void logout() {
        Session.clear();
        try {
            Stage stage = (Stage) lblSesion.getScene().getWindow();
            var loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            stage.setScene(new javafx.scene.Scene(loader.load(), 480, 360));
            stage.setTitle("Ingreso — Ferretería");
            stage.setResizable(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void openUsuarios() {
        loadCenter("/fxml/users/UsersView.fxml");
    }

    private void loadCenter(String fxml) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxml));
            content.setCenter(node);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void openProductos() {
        loadCenter("/fxml/catalogos/ProductosView.fxml");
    }

    @FXML
    private void openProveedores() {
        loadCenter("/fxml/catalogos/ProveedoresView.fxml");
    }

    @FXML
    private void openClientes() {
        loadCenter("/fxml/catalogos/ClientesView.fxml");
    }

    @FXML
    private void openIngreso() {
        loadCenter("/fxml/bodega/IngresoView.fxml");
    }

}
