package com.ferre.controller;

import com.ferre.model.Cliente;
import com.ferre.service.ClienteService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ClientesController {

    @FXML
    private TableView<Cliente> tbl;
    @FXML
    private TableColumn<Cliente, String> colId, colNombre, colNit, colTelefono, colEmail;
    @FXML
    private TextField txtBuscar, txtNombre, txtNit, txtTelefono, txtEmail, txtDireccion;
    @FXML
    private Label lblInfo;

    private final ClienteService service = new ClienteService();
    private Cliente seleccionado;
    private FilteredList<Cliente> filtered;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colNit.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getNit())));
        colTelefono.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getTelefono())));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getEmail())));

        tbl.getSelectionModel().selectedItemProperty().addListener((o, old, now) -> {
            seleccionado = now;
            if (now != null) {
                txtNombre.setText(now.getNombre());
                txtNit.setText(now.getNit());
                txtTelefono.setText(now.getTelefono());
                txtEmail.setText(now.getEmail());
                txtDireccion.setText(now.getDireccion());
            }
        });

        cargarTabla();
        txtBuscar.textProperty().addListener((obs, o, n) -> aplicarFiltro(n));
    }

    private void cargarTabla() {
        filtered = new FilteredList<>(FXCollections.observableArrayList(service.listar()), p -> true);
        SortedList<Cliente> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tbl.comparatorProperty());
        tbl.setItems(sorted);
        lblInfo.setText("Total: " + tbl.getItems().size());
    }

    private void aplicarFiltro(String q) {
        if (q == null) {
            q = "";
        }
        final String s = q.toLowerCase();
        filtered.setPredicate(c -> c.getNombre().toLowerCase().contains(s) || (c.getNit() != null && c.getNit().toLowerCase().contains(s)));
        lblInfo.setText("Filtrados: " + filtered.size());
    }

    @FXML
    private void nuevo() {
        seleccionado = null;
        txtNombre.clear();
        txtNit.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        tbl.getSelectionModel().clearSelection();
    }

    @FXML
    private void guardar() {
        try {
            Cliente c = (seleccionado == null) ? new Cliente() : seleccionado;
            c.setNombre(txtNombre.getText());
            c.setNit(txtNit.getText());
            c.setTelefono(txtTelefono.getText());
            c.setEmail(txtEmail.getText());
            c.setDireccion(txtDireccion.getText());
            service.guardar(c);
            info("Guardado", "Cliente guardado");
            cargarTabla();
            nuevo();
        } catch (Exception ex) {
            error("Error", ex.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        if (seleccionado == null) {
            warn("Selecciona un cliente");
            return;
        }
        try {
            service.eliminar(seleccionado.getId());
            info("Eliminado", "Cliente eliminado");
            cargarTabla();
            nuevo();
        } catch (Exception ex) {
            error("No se puede eliminar", "Puede estar referenciado por ventas.\nDetalle: " + ex.getMessage());
        }
    }

    private void info(String h, String m) {
        alert(Alert.AlertType.INFORMATION, h, m);
    }

    private void warn(String m) {
        alert(Alert.AlertType.WARNING, "Atención", m);
    }

    private void error(String h, String m) {
        alert(Alert.AlertType.ERROR, h, m);
    }

    private void alert(Alert.AlertType t, String h, String m) {
        var a = new Alert(t);
        a.setHeaderText(h);
        a.setContentText(m);
        a.setTitle("Clientes");
        a.showAndWait();
    }
}
