package com.ferre.controller.caja;

import com.ferre.report.ReportService;
import com.ferre.model.Factura;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import com.ferre.config.DataSourceFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CajaController {

    @FXML
    private Label lblFactura;
    @FXML
    private Label lblCliente;
    @FXML
    private Label lblVendedor;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblCambio;
    @FXML
    private TextField txtEfectivo;

    private Factura factura;
    private BigDecimal total = BigDecimal.ZERO;
    private java.math.BigDecimal totalFactura = java.math.BigDecimal.ZERO;

    private final ReportService reportService = new ReportService();

    public void setFactura(Factura f) {
        this.factura = f;
        if (f != null && f.getId() > 0) {
            cargarFacturaDesdeBD(f.getId());
        } else {
            // Estado por defecto
            lblFactura.setText("-");
            lblCliente.setText("-");
            lblVendedor.setText("-");
            lblTotal.setText("0.00");
            lblCambio.setText("0.00");
            total = java.math.BigDecimal.ZERO;
        }
    }

    @FXML
    private void initialize() {
        lblCambio.setText("0.00");
    }

    @FXML
    private void cobrar() {
        try {
            if (factura == null || factura.getId() <= 0) {
                warn("No hay factura cargada en caja.");
                return;
            }

            String efectivoStr = txtEfectivo.getText() == null ? "" : txtEfectivo.getText().trim();
            if (efectivoStr.isEmpty()) {
                warn("Ingresa el efectivo recibido.");
                return;
            }

            BigDecimal efectivo = new BigDecimal(efectivoStr);
            if (efectivo.compareTo(total) < 0) {
                warn("Efectivo insuficiente. Total: " + total.toPlainString());
                return;
            }

            BigDecimal cambio = efectivo.subtract(total);
            lblCambio.setText(cambio.toPlainString());

            info("Cobro realizado", "Total: " + total + "\nEfectivo: " + efectivo + "\nCambio: " + cambio);

            imprimirRecibo(factura.getId());

        } catch (NumberFormatException nfe) {
            error("Valor inválido", "El efectivo debe ser numérico.");
        } catch (Exception ex) {
            error("No se pudo completar el cobro", ex.getMessage() == null ? ex.toString() : ex.getMessage());
        }
    }

    private void imprimirRecibo(long facturaId) {
        try {
            String facStr = lblFactura.getText() == null ? "-" : lblFactura.getText().trim();
            String cliente = lblCliente.getText() == null ? "-" : lblCliente.getText().trim();
            String vendedor = lblVendedor.getText() == null ? "-" : lblVendedor.getText().trim();

            java.math.BigDecimal tot = (this.total == null) ? java.math.BigDecimal.ZERO : this.total;

            java.util.Map<String, Object> params = new java.util.HashMap<>();
            params.put("P_FACTURA_ID", facturaId);
            params.put("P_FACTURA", facStr);
            params.put("P_CLIENTE", cliente);
            params.put("P_VENDEDOR", vendedor);
            params.put("P_TOTAL", tot);

            var print = reportService.fill("/reports/recibo_factura.jrxml", params);
            reportService.view(print, "Recibo — " + facStr);

        } catch (Exception ex) {
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,
                    "No se pudo imprimir el recibo\n" + ex.getMessage()).showAndWait();
            ex.printStackTrace();
        }
    }

    @FXML
    private void limpiar() {
        txtEfectivo.clear();
        lblCambio.setText("0.00");
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
        a.setTitle("Caja");
        a.showAndWait();
    }

    private void cargarFacturaDesdeBD(long facturaId) {
        final String sql = """
        SELECT f.serie, f.numero, f.total,
               COALESCE(c.nombre, '-')   AS cliente,
               COALESCE(u.nombre, '-')   AS vendedor
        FROM factura f
        LEFT JOIN pedido    p ON p.id = f.pedido_id
        LEFT JOIN clientes  c ON c.id = p.cliente_id
        LEFT JOIN usuarios  u ON u.id = f.cajero_id   -- o p.vendedor_id si prefieres el vendedor del pedido
        WHERE f.id = ?
        """;
        try (Connection cn = DataSourceFactory.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, facturaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String serie = rs.getString("serie");
                    String numero = rs.getString("numero");
                    BigDecimal tot = rs.getBigDecimal("total");
                    String cliente = rs.getString("cliente");
                    String vend = rs.getString("vendedor");

                    String facText = (serie != null && numero != null)
                            ? (serie + "-" + numero)
                            : ("#" + facturaId);
                    lblFactura.setText(facText);

                    lblCliente.setText(cliente != null ? cliente : "-");
                    lblVendedor.setText(vend != null ? vend : "-");

                    total = (tot != null) ? tot : java.math.BigDecimal.ZERO;
                    lblTotal.setText(total.toPlainString());

                    lblCambio.setText("0.00");
                    txtEfectivo.clear();
                }
            }
        } catch (Exception ex) {
            lblFactura.setText("#" + facturaId);
            lblCliente.setText("-");
            lblVendedor.setText("-");
            lblTotal.setText("0.00");
            total = java.math.BigDecimal.ZERO;
            System.err.println("No se pudieron cargar datos de factura " + facturaId + ": " + ex.getMessage());
        }
    }

}
