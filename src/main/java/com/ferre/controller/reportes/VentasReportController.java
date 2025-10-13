package com.ferre.controller.reportes;

import com.ferre.model.Cliente;
import com.ferre.report.ReportService;
import com.ferre.service.ClienteService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class VentasReportController {

    @FXML
    private DatePicker dpDesde, dpHasta;
    @FXML
    private ComboBox<Cliente> cmbCliente;

    private final ReportService rs = new ReportService();
    private final ClienteService cliSrv = new ClienteService();

    @FXML
    public void initialize() {
        LocalDate hoy = LocalDate.now();
        dpDesde.setValue(hoy);
        dpHasta.setValue(hoy);

        var items = FXCollections.observableArrayList(cliSrv.listar());
        cmbCliente.setItems(items);

        cmbCliente.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Cliente c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null
                        : c.getNombre() + (c.getNit() != null && !c.getNit().isBlank() ? " — " + c.getNit() : ""));
            }
        });
        cmbCliente.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Cliente c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null
                        : c.getNombre() + (c.getNit() != null && !c.getNit().isBlank() ? " — " + c.getNit() : ""));
            }
        });
    }

    private boolean validarRango() {
        var desde = dpDesde.getValue();
        var hasta = dpHasta.getValue();
        if (desde == null || hasta == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona el rango de fechas.").showAndWait();
            return false;
        }
        if (hasta.isBefore(desde)) {
            new Alert(Alert.AlertType.WARNING, "La fecha 'Hasta' no puede ser menor que 'Desde'.").showAndWait();
            return false;
        }
        return true;
    }

    private Map<String, Object> buildParams() {
        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();

        Map<String, Object> p = new HashMap<>();
        // Los JRXML esperan java.util.Date (DATE)
        p.put("P_FECHA_INI", java.sql.Date.valueOf(desde));
        p.put("P_FECHA_FIN", java.sql.Date.valueOf(hasta));

        Cliente c = cmbCliente.getSelectionModel().getSelectedItem();
        p.put("P_CLIENTE_ID", c == null ? null : c.getId());

        return p;
    }

    @FXML
    private void verDetalle() {
        if (!validarRango()) {
            return;
        }
        try {
            var print = rs.fill("/reports/ventas_detalle.jrxml", buildParams());
            if (print.getPages().isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "No hay ventas para el rango seleccionado.").showAndWait();
                return;
            }
            rs.view(print, "Ventas — Detalle");
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error al generar reporte:\n" + ex.getMessage()).showAndWait();
            ex.printStackTrace();
        }
    }

    @FXML
    private void verResumen() {
        if (!validarRango()) {
            return;
        }
        try {
            var print = rs.fill("/reports/ventas_resumen.jrxml", buildParams());
            if (print.getPages().isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "No hay ventas para el rango seleccionado.").showAndWait();
                return;
            }
            rs.view(print, "Ventas — Resumen");
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error al generar reporte:\n" + ex.getMessage()).showAndWait();
            ex.printStackTrace();
        }
    }
}
