package com.ferre.controller.reportes;

import com.ferre.model.Producto;
import com.ferre.report.ReportService;
import com.ferre.service.ProductoService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;

public class KardexReportController {
    @FXML private ComboBox<Producto> cmbProducto;
    @FXML private DatePicker dpDesde, dpHasta;

    private final ReportService rs = new ReportService();
    private final ProductoService prodSrv = new ProductoService();

    @FXML
    public void initialize(){
        cmbProducto.setItems(FXCollections.observableArrayList(prodSrv.listar()));
        cmbProducto.setCellFactory(cb -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Producto p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty||p==null?null: p.getCodigo() + " — " + p.getNombre());
            }
        });
        cmbProducto.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Producto p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty||p==null?null: p.getCodigo() + " — " + p.getNombre());
            }
        });
        LocalDate hoy = LocalDate.now();
        dpDesde.setValue(hoy.minusDays(30));
        dpHasta.setValue(hoy);
    }

    @FXML private void ver(){
        var p = cmbProducto.getSelectionModel().getSelectedItem();
        if (p == null) { new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING,"Selecciona producto").showAndWait(); return; }
        var params = new HashMap<String,Object>();
        params.put("P_PRODUCTO_ID", p.getId());
        params.put("P_FECHA_INI", Timestamp.valueOf(dpDesde.getValue().atStartOfDay()));
        params.put("P_FECHA_FIN", Timestamp.valueOf(dpHasta.getValue().plusDays(1).atStartOfDay().minusSeconds(1)));
        var print = rs.fill("/reports/kardex_producto.jrxml", params);
        rs.view(print, "Kárdex — " + p.getCodigo());
    }
}
