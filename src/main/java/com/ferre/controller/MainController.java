package com.ferre.controller;

import com.ferre.config.Session;
import com.ferre.model.Factura;
import com.ferre.model.Rol;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class MainController {

    @FXML private Menu menuAdmin, menuBodega, menuVentas, menuCaja, menuCatalogos;
    @FXML private Label lblSesion;
    @FXML private BorderPane content;

    @FXML
    public void initialize() {
        var u = Session.get();
        lblSesion.setText("Sesión: " + u.getUsuario() + " (" + u.getRol() + ")");

        // visibilidad por rol
        menuAdmin.setVisible(u.getRol() == Rol.ADMIN);
        menuCatalogos.setVisible(u.getRol() == Rol.ADMIN); // solo ADMIN edita catálogos
        menuBodega.setVisible(u.getRol() == Rol.BODEGA || u.getRol() == Rol.ADMIN);
        menuVentas.setVisible(u.getRol() == Rol.VENTAS || u.getRol() == Rol.ADMIN);
        menuCaja.setVisible(u.getRol() == Rol.CAJA || u.getRol() == Rol.ADMIN);
    }

    @FXML private void exitApp() { System.exit(0); }

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

    @FXML private void openUsuarios()      { loadCenter("/fxml/users/UsersView.fxml"); }
    @FXML private void openProductos()     { loadCenter("/fxml/catalogos/ProductosView.fxml"); }
    @FXML private void openProveedores()   { loadCenter("/fxml/catalogos/ProveedoresView.fxml"); }
    @FXML private void openClientes()      { loadCenter("/fxml/catalogos/ClientesView.fxml"); }
    @FXML private void openIngreso()       { loadCenter("/fxml/bodega/IngresoView.fxml"); }
    @FXML private void openNuevoPedido()   { loadCenter("/fxml/ventas/NuevoPedidoView.fxml"); }

    @FXML
    private void openFacturar() {
        // Pasamos referencia de Main a FacturarController para que, al facturar,
        // pueda redirigir a Caja con la factura cargada.
        loadCenter("/fxml/ventas/FacturarView.fxml",
                (FacturarController ctrl) -> ctrl.setMain(this));
    }

    @FXML private void openRptVentas()     { loadCenter("/fxml/reportes/VentasReportView.fxml"); }
    @FXML private void openRptStock()      { loadCenter("/fxml/reportes/StockReportView.fxml"); }
    @FXML private void openRptKardex()     { loadCenter("/fxml/reportes/KardexReportView.fxml"); }

    @FXML
    private void openCaja() {
        // Caja vacía (sin factura en contexto)
        loadCenter("/fxml/caja/CajaView.fxml");
    }

    /** Abre Caja y le inyecta una factura (solo id es suficiente por ahora). */
    public void openCajaConFactura(Factura f) {
        loadCenter("/fxml/caja/CajaView.fxml",
                (com.ferre.controller.caja.CajaController c) -> c.setFactura(f));
    }

    // ---------------- helpers de carga ----------------

    /** Carga simple al centro del BorderPane. */
    private void loadCenter(String fxml) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxml));
            content.setCenter(node);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Carga + callback con el controller para inicialización programática. */
    private <T> void loadCenter(String fxml, Consumer<T> init) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Node node = loader.load();
            @SuppressWarnings("unchecked")
            T controller = (T) loader.getController();
            if (init != null) init.accept(controller);
            content.setCenter(node);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
