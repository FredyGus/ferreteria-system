package com.ferre.controller;

import com.ferre.model.Producto;
import com.ferre.model.Proveedor;
import com.ferre.service.ProductoService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;

public class ProductosController {

    @FXML
    private TableView<Producto> tbl;
    @FXML
    private TableColumn<Producto, String> colId, colCodigo, colNombre, colUnidad, colPrecio, colStock;
    @FXML
    private TableColumn<Producto, Boolean> colActivo;
    @FXML
    private TextField txtBuscar, txtCodigo, txtNombre, txtUnidad, txtPrecio, txtStock;
    @FXML
    private ComboBox<Proveedor> cmbProveedor;
    @FXML
    private Label lblInfo;

    private final ProductoService service = new ProductoService();
    private Producto seleccionado;
    private FilteredList<Producto> filtered;

    @FXML
    public void initialize() {
        cmbProveedor.setItems(FXCollections.observableArrayList(service.listarProveedores()));

        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colUnidad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUnidad()));
        colPrecio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrecio().toPlainString()));
        colStock.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getStock())));
        colActivo.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isActivo()));
        colActivo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean b, boolean empty) {
                super.updateItem(b, empty);
                setText(empty ? null : (b ? "Sí" : "No"));
            }
        });

        tbl.getSelectionModel().selectedItemProperty().addListener((o, old, now) -> {
            seleccionado = now;
            if (now != null) {
                txtCodigo.setText(now.getCodigo());
                txtNombre.setText(now.getNombre());
                txtUnidad.setText(now.getUnidad());
                txtPrecio.setText(now.getPrecio().toPlainString());
                txtStock.setText(String.valueOf(now.getStock()));
                if (now.getProveedorId() == null) {
                    cmbProveedor.getSelectionModel().clearSelection();
                } else {
                    cmbProveedor.getSelectionModel().select(
                            cmbProveedor.getItems().stream().filter(p -> p.getId() == now.getProveedorId()).findFirst().orElse(null)
                    );
                }
            }
        });

        cargarTabla();
        txtBuscar.textProperty().addListener((obs, o, n) -> aplicarFiltro(n));
    }

    private void cargarTabla() {
        filtered = new FilteredList<>(FXCollections.observableArrayList(service.listar()), p -> true);
        SortedList<Producto> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tbl.comparatorProperty());
        tbl.setItems(sorted);
        lblInfo.setText("Total: " + tbl.getItems().size());
    }

    private void aplicarFiltro(String q) {
        if (q == null) {
            q = "";
        }
        final String s = q.toLowerCase();
        filtered.setPredicate(p
                -> p.getCodigo().toLowerCase().contains(s)
                || p.getNombre().toLowerCase().contains(s)
        );
        lblInfo.setText("Filtrados: " + filtered.size());
    }

    @FXML
    private void nuevo() {
        seleccionado = null;
        txtCodigo.clear();
        txtNombre.clear();
        txtUnidad.clear();
        txtPrecio.setText("0.00");
        txtStock.setText("0");
        cmbProveedor.getSelectionModel().clearSelection();
        tbl.getSelectionModel().clearSelection();
    }

    @FXML
    private void guardar() {
        try {
            String codigo = txtCodigo.getText() == null ? "" : txtCodigo.getText().trim();
            String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
            String unidad = txtUnidad.getText() == null ? "" : txtUnidad.getText().trim();
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            Proveedor prov = cmbProveedor.getSelectionModel().getSelectedItem();
            Long provId = prov == null ? null : prov.getId();

            Producto p = (seleccionado == null) ? new Producto() : seleccionado;
            p.setCodigo(codigo);
            p.setNombre(nombre);
            p.setUnidad(unidad);
            p.setPrecio(precio);
            p.setStock(stock);
            p.setProveedorId(provId);
            if (p.getId() == 0) {
                p.setActivo(true);
            }

            service.guardar(p);
            info("Guardado", "Producto guardado correctamente");
            cargarTabla();
            nuevo();
        } catch (NumberFormatException nfe) {
            error("Formato numérico", "Precio/Stock inválidos");
        } catch (Exception ex) {
            error("Error", ex.getMessage());
        }
    }

    @FXML
    private void toggleEstado() {
        if (seleccionado == null) {
            warn("Seleccione un producto");
            return;
        }
        try {
            boolean nuevo = !seleccionado.isActivo();
            service.toggleActivo(seleccionado.getId(), nuevo);
            info("Estado", nuevo ? "Activado" : "Desactivado");
            cargarTabla();
            nuevo();
        } catch (Exception ex) {
            error("Error", ex.getMessage());
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
        a.setTitle("Productos");
        a.showAndWait();
    }
}
