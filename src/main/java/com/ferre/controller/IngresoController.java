package com.ferre.controller;

import com.ferre.config.Session;
import com.ferre.model.Ingreso;
import com.ferre.model.IngresoDet;
import com.ferre.model.Producto;
import com.ferre.model.Proveedor;
import com.ferre.service.IngresoService;
import com.ferre.service.ProductoService;
import com.ferre.service.ProveedorService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IngresoController {

    @FXML
    private ComboBox<Proveedor> cmbProveedor;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private TextField txtNoDoc;

    @FXML
    private ComboBox<Producto> cmbProducto;
    @FXML
    private TextField txtCantidad, txtCosto;

    @FXML
    private TableView<IngresoDet> tbl;
    @FXML
    private TableColumn<IngresoDet, String> colCodigo, colNombre, colCantidad, colCosto, colSubtotal;

    @FXML
    private Label lblTotal;

    private final ProveedorService provService = new ProveedorService();
    private final ProductoService prodService = new ProductoService();
    private final IngresoService ingresoService = new IngresoService();

    private final List<IngresoDet> detalles = new ArrayList<>();

    @FXML
    public void initialize() {
        cmbProveedor.setItems(FXCollections.observableArrayList(provService.listar()));
        cmbProducto.setItems(FXCollections.observableArrayList(prodService.listar()));
        cmbProducto.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Producto p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getCodigo() + " — " + p.getNombre());
            }
        });
        cmbProducto.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Producto p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getCodigo() + " — " + p.getNombre());
            }
        });

        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(findProductoCodigo(c.getValue().getProductoId())));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(findProductoNombre(c.getValue().getProductoId())));
        colCantidad.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCantidad())));
        colCosto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCostoUnit().toPlainString()));
        colSubtotal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubtotal().toPlainString()));

        dpFecha.setValue(LocalDate.now());
        refrescarTabla();
        recalcularTotal();
    }

    private String findProductoCodigo(long id) {
        return cmbProducto.getItems().stream().filter(p -> p.getId() == id).map(Producto::getCodigo).findFirst().orElse("?");
    }

    private String findProductoNombre(long id) {
        return cmbProducto.getItems().stream().filter(p -> p.getId() == id).map(Producto::getNombre).findFirst().orElse("?");
    }

    @FXML
    private void agregarItem() {
        try {
            var p = cmbProducto.getSelectionModel().getSelectedItem();
            if (p == null) {
                warn("Selecciona un producto");
                return;
            }
            int cant = Integer.parseInt(txtCantidad.getText().trim());
            BigDecimal costo = new BigDecimal(txtCosto.getText().trim());
            if (cant <= 0) {
                warn("Cantidad debe ser > 0");
                return;
            }
            if (costo.compareTo(BigDecimal.ZERO) <= 0) {
                warn("Costo debe ser > 0");
                return;
            }

            // Si ya existe el producto en la lista, sumamos cantidad
            for (IngresoDet d : detalles) {
                if (d.getProductoId() == p.getId()) {
                    d.setCantidad(d.getCantidad() + cant);
                    d.setCostoUnit(costo); // último costo
                    d.setSubtotal(costo.multiply(BigDecimal.valueOf(d.getCantidad())));
                    refrescarTabla();
                    recalcularTotal();
                    limpiarLinea();
                    return;
                }
            }

            IngresoDet d = new IngresoDet();
            d.setProductoId(p.getId());
            d.setCantidad(cant);
            d.setCostoUnit(costo);
            d.setSubtotal(costo.multiply(BigDecimal.valueOf(cant)));
            detalles.add(d);

            refrescarTabla();
            recalcularTotal();
            limpiarLinea();
        } catch (NumberFormatException nfe) {
            warn("Cantidad o costo inválidos");
        }
    }

    @FXML
    private void eliminarItem() {
        var sel = tbl.getSelectionModel().getSelectedItem();
        if (sel == null) {
            warn("Selecciona una fila");
            return;
        }
        detalles.remove(sel);
        refrescarTabla();
        recalcularTotal();
    }

    @FXML
    private void nuevo() {
        cmbProveedor.getSelectionModel().clearSelection();
        dpFecha.setValue(LocalDate.now());
        txtNoDoc.clear();
        detalles.clear();
        refrescarTabla();
        recalcularTotal();
        limpiarLinea();
    }

    @FXML
    private void guardar() {
        try {
            var prov = cmbProveedor.getSelectionModel().getSelectedItem();
            if (prov == null) {
                warn("Selecciona proveedor");
                return;
            }
            if (detalles.isEmpty()) {
                warn("Agrega al menos un ítem");
                return;
            }

            Ingreso cab = new Ingreso();
            cab.setProveedorId(prov.getId());
            cab.setBodegueroId(Session.get() != null ? Session.get().getId() : null);
            cab.setFecha(LocalDateTime.of(dpFecha.getValue(), java.time.LocalTime.now()));
            cab.setNoDoc(txtNoDoc.getText());

            long id = ingresoService.registrar(cab, detalles);
            info("Ingreso registrado", "No. " + id + " guardado. Stock actualizado.");
            nuevo();
        } catch (Exception ex) {
            error("Error al guardar", ex.getMessage());
        }
    }

    private void limpiarLinea() {
        cmbProducto.getSelectionModel().clearSelection();
        txtCantidad.clear();
        txtCosto.clear();
    }

    private void refrescarTabla() {
        tbl.setItems(FXCollections.observableArrayList(detalles));
    }

    private void recalcularTotal() {
        java.math.BigDecimal t = detalles.stream()
                .map(IngresoDet::getSubtotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        lblTotal.setText("Total: " + t.toPlainString());
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
        a.setTitle("Ingreso");
        a.showAndWait();
    }
}
