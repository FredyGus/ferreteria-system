package com.ferre.controller.reportes;

import com.ferre.model.Cliente;
import com.ferre.report.ReportService;
import com.ferre.service.ClienteService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Timestamp;
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

    private Map<String, Object> buildParams() {
        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();

        Map<String, Object> p = new HashMap<>();
        p.put("P_FECHA_INI", Timestamp.valueOf(desde.atStartOfDay()));                // inclusivo
        p.put("P_FECHA_FIN_EXC", Timestamp.valueOf(hasta.plusDays(1).atStartOfDay())); // exclusivo
        p.put("P_FECHA_FIN_SHOW", java.sql.Date.valueOf(hasta));                       // solo para el título

        Cliente c = cmbCliente.getSelectionModel().getSelectedItem();
        p.put("P_CLIENTE_ID", c == null ? null : c.getId());

        return p;
    }

    @FXML
    private void verDetalle() {
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
