package com.ferre.controller.reportes;

import com.ferre.model.Producto;
import com.ferre.report.ReportService;
import com.ferre.service.ProductoService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.util.HashMap;

public class KardexReportController {

    @FXML
    private ComboBox<Producto> cmbProducto;
    @FXML
    private DatePicker dpDesde, dpHasta;

    private final ReportService rs = new ReportService();
    private final ProductoService prodSrv = new ProductoService();

    @FXML
    public void initialize() {
        cmbProducto.setItems(FXCollections.observableArrayList(prodSrv.listar()));
        cmbProducto.setCellFactory(cb -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Producto p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getCodigo() + " — " + p.getNombre());
            }
        });
        cmbProducto.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Producto p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getCodigo() + " — " + p.getNombre());
            }
        });

        LocalDate hoy = LocalDate.now();
        dpDesde.setValue(hoy);
        dpHasta.setValue(hoy);
    }

    private HashMap<String, Object> buildParams(Producto prod, LocalDate desde, LocalDate hasta) {
        var params = new HashMap<String, Object>();
        params.put("P_PRODUCTO_ID", prod.getId());
        params.put("P_FECHA_INI", java.sql.Date.valueOf(desde)); // DATE inclusivo
        params.put("P_FECHA_FIN", java.sql.Date.valueOf(hasta)); // DATE inclusivo
        return params;
    }

    @FXML
    private void ver() {
        var p = cmbProducto.getSelectionModel().getSelectedItem();
        if (p == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un producto.").showAndWait();
            return;
        }
        var desde = dpDesde.getValue();
        var hasta = dpHasta.getValue();
        if (desde == null || hasta == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona el rango de fechas.").showAndWait();
            return;
        }
        if (hasta.isBefore(desde)) {
            new Alert(Alert.AlertType.WARNING, "La fecha 'Hasta' no puede ser menor que 'Desde'.").showAndWait();
            return;
        }

        try {
            var print = rs.fill("/reports/kardex_producto.jrxml", buildParams(p, desde, hasta));

            if (print.getPages() == null || print.getPages().isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "No hay movimientos para el rango seleccionado.").showAndWait();
                return;
            }
            rs.view(print, "Kárdex — " + p.getCodigo());
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error al generar Kárdex:\n" + ex.getMessage()).showAndWait();
            ex.printStackTrace();
        }
    }
}
