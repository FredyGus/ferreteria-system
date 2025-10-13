package com.ferre.controller.reportes;

import com.ferre.report.ReportService;
import javafx.fxml.FXML;

import java.util.HashMap;

public class StockReportController {

    private final ReportService rs = new ReportService();

    @FXML
    private void ver() {
        var print = rs.fill("/reports/stock_actual.jrxml", new HashMap<>());
        rs.view(print, "Inventario / Stock");
    }
}
