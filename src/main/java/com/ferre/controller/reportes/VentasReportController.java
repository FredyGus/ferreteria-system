package com.ferre.controller.reportes;

import com.ferre.model.Cliente;
import com.ferre.report.ReportService;
import com.ferre.service.ClienteService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;

public class VentasReportController {
    @FXML private DatePicker dpDesde, dpHasta;
    @FXML private ComboBox<Cliente> cmbCliente;

    private final ReportService rs = new ReportService();
    private final ClienteService cliSrv = new ClienteService();

    @FXML
    public void initialize() {
        LocalDate hoy = LocalDate.now();
        dpDesde.setValue(hoy);
        dpHasta.setValue(hoy);
        cmbCliente.setItems(FXCollections.observableArrayList(cliSrv.listar()));
        cmbCliente.setCellFactory(cb -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Cliente c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty||c==null?null: c.getNombre() + (c.getNit()!=null && !c.getNit().isBlank()? " — "+c.getNit():""));
            }
        });
        cmbCliente.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Cliente c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty||c==null?null: c.getNombre() + (c.getNit()!=null && !c.getNit().isBlank()? " — "+c.getNit():""));
            }
        });
    }

    private HashMap<String,Object> buildParams(){
        var p = new HashMap<String,Object>();
        var d1 = Timestamp.valueOf(dpDesde.getValue().atStartOfDay());
        var d2 = Timestamp.valueOf(dpHasta.getValue().plusDays(1).atStartOfDay().minusSeconds(1));
        p.put("P_FECHA_INI", d1);
        p.put("P_FECHA_FIN", d2);
        var c = cmbCliente.getSelectionModel().getSelectedItem();
        p.put("P_CLIENTE_ID", c != null ? c.getId() : null);
        return p;
    }

    @FXML private void verDetalle() {
        var print = rs.fill("/reports/ventas_detalle.jrxml", buildParams());
        rs.view(print, "Ventas - Detalle");
    }

    @FXML private void verResumen() {
        var print = rs.fill("/reports/ventas_resumen.jrxml", buildParams());
        rs.view(print, "Ventas - Resumen");
    }
}
