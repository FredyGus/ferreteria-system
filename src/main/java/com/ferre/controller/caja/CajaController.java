package com.ferre.controller.caja;

import com.ferre.config.DataSourceFactory;
import com.ferre.model.Factura;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Caja — Cobro de factura en EFECTIVO con cálculo de CAMBIO.
 * - Muestra datos de factura (cliente, vendedor, total).
 * - Ingresa efectivo recibido y calcula cambio en vivo.
 * - Registra un pago EFECTIVO por el total de la factura (forma_pago_id = 1).
 * - Marca el pedido como PAGADO (si no lo está).
 */
public class CajaController {

    @FXML private Label lblFactura;
    @FXML private Label lblCliente;
    @FXML private Label lblVendedor;
    @FXML private Label lblTotal;
    @FXML private Label lblCambio;
    @FXML private TextField txtEfectivo;
    @FXML private Button btnCobrar;

    // Estado interno
    private long facturaId = 0L;
    private long pedidoId  = 0L;
    private BigDecimal total = BigDecimal.ZERO;

    // Forma de pago EFECTIVO (de tus semillas: id=1)
    private static final long FORMA_PAGO_EFECTIVO_ID = 1L;

    @FXML
    public void initialize() {
        // Recalcular cambio cuando escriben efectivo
        txtEfectivo.textProperty().addListener((obs, oldv, newv) -> calcularCambioSeguro());
        Platform.runLater(this::calcularCambioSeguro);
    }

    /** Llamado desde MainController cuando venimos de Facturar. */
    public void setFactura(Factura f){
        // getId() es long primitivo: no comparar contra null
        if (f == null || f.getId() <= 0) {
            error("Factura inválida", "No se recibió la factura a cobrar.");
            deshabilitarCobro();
            return;
        }
        this.facturaId = f.getId();
        cargarResumenFactura(facturaId);
    }

    /** Limpia solo los campos de efectivo/cambio (no toca datos de factura). */
    @FXML
    private void limpiar(){
        txtEfectivo.clear();
        lblCambio.setText("0.00");
        if (btnCobrar != null) btnCobrar.setDisable(false);
        txtEfectivo.setDisable(false);
        txtEfectivo.requestFocus();
    }

    /** Acción de cobrar: valida efectivo >= total, registra pago y marca pedido PAGADO. */
    @FXML
    private void cobrar(){
        if (facturaId <= 0) {
            warn("No hay factura cargada.");
            return;
        }
        BigDecimal efectivo = leerDecimal(txtEfectivo.getText());
        if (efectivo.compareTo(total) < 0) {
            warn("El efectivo recibido es menor al total.");
            return;
        }

        try (Connection cn = DataSourceFactory.getConnection()) {
            cn.setAutoCommit(false);
            try {
                // 1) Verificar si ya existe un pago para esta factura
                if (existePagoFactura(cn, facturaId)) {
                    throw new IllegalStateException("Esta factura ya fue cobrada.");
                }

                // 2) Insertar pago por el TOTAL (lo contable es el total).
                insertarPago(cn, facturaId, FORMA_PAGO_EFECTIVO_ID, total);

                // 3) Marcar pedido como PAGADO (si aplica)
                marcarPedidoPagado(cn, pedidoId);

                cn.commit();

                BigDecimal cambio = efectivo.subtract(total);
                info("Cobro registrado",
                        "Pago en efectivo guardado.\n" +
                        "Total: " + total + "\n" +
                        "Efectivo: " + efectivo + "\n" +
                        "Cambio: " + cambio);

                // Bloquear edición tras cobrar
                txtEfectivo.setDisable(true);
                btnCobrar.setDisable(true);
                lblCambio.setText(formatear(cambio));

            } catch (Exception ex) {
                cn.rollback();
                throw ex;
            } finally {
                cn.setAutoCommit(true);
            }
        } catch (Exception e) {
            error("No se pudo registrar el cobro", mensajeLimpio(e));
        }
    }

    /* =========================
       Carga de datos de factura
       ========================= */
    private void cargarResumenFactura(long facturaId){
        String sql = """
            SELECT f.id, f.serie, f.numero, f.total, f.pedido_id,
                   c.nombre AS cliente, u.nombre AS vendedor
            FROM factura f
            JOIN pedido  p ON p.id = f.pedido_id
            JOIN clientes c ON c.id = p.cliente_id
            JOIN usuarios u ON u.id = p.vendedor_id
            WHERE f.id = ?
        """;
        try (Connection cn = DataSourceFactory.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, facturaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()){
                    throw new IllegalStateException("Factura no encontrada: " + facturaId);
                }
                String serie  = nvl(rs.getString("serie"));
                String numero = nvl(rs.getString("numero"));
                total         = rs.getBigDecimal("total");
                pedidoId      = rs.getLong("pedido_id");
                String cliente  = nvl(rs.getString("cliente"));
                String vendedor = nvl(rs.getString("vendedor"));

                lblFactura.setText( (serie.isBlank() && numero.isBlank())
                        ? ("#" + facturaId)
                        : (serie + "-" + numero) );
                lblCliente.setText(cliente);
                lblVendedor.setText(vendedor);
                lblTotal.setText(formatear(total));
                lblCambio.setText("0.00");
                txtEfectivo.setText("");
                txtEfectivo.requestFocus();
            }
        } catch (Exception e) {
            error("No se pudo cargar la factura", mensajeLimpio(e));
            deshabilitarCobro();
        }
    }

    /* =========================
       Persistencia de pagos/estado
       ========================= */
    private boolean existePagoFactura(Connection cn, long facturaId) throws Exception {
        String sql = "SELECT COUNT(*) FROM pago WHERE factura_id = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, facturaId);
            try (ResultSet rs = ps.executeQuery()){
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private void insertarPago(Connection cn, long facturaId, long formaPagoId, BigDecimal monto) throws Exception {
        String sql = "INSERT INTO pago(factura_id, forma_pago_id, monto) VALUES(?,?,?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, facturaId);
            ps.setLong(2, formaPagoId);
            ps.setBigDecimal(3, monto);
            ps.executeUpdate();
        }
    }

    private void marcarPedidoPagado(Connection cn, long pedidoId) throws Exception {
        if (pedidoId <= 0) return;
        String sql = "UPDATE pedido SET estado='PAGADO' WHERE id=? AND estado<>'PAGADO'";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, pedidoId);
            ps.executeUpdate();
        }
    }

    /* =========================
       Utilidades UI
       ========================= */
    private void calcularCambioSeguro(){
        BigDecimal efectivo = leerDecimal(txtEfectivo.getText());
        BigDecimal cambio = efectivo.subtract(total);
        if (cambio.compareTo(BigDecimal.ZERO) < 0) cambio = BigDecimal.ZERO;
        lblCambio.setText(formatear(cambio));
    }

    private BigDecimal leerDecimal(String s){
        try {
            if (s == null || s.trim().isEmpty()) return BigDecimal.ZERO;
            return new BigDecimal(s.trim());
        } catch (Exception e){
            return BigDecimal.ZERO;
        }
    }

    private String formatear(BigDecimal bd){
        return bd == null ? "0.00" : bd.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    private String mensajeLimpio(Throwable t){
        String m = (t.getMessage() == null || t.getMessage().isBlank()) ? t.toString() : t.getMessage();
        // Mensajes más amigables
        if (m.toLowerCase().contains("duplicate") || m.toLowerCase().contains("ya fue cobrada")){
            return "Esta factura ya está cobrada.";
        }
        return m;
    }

    private void deshabilitarCobro(){
        if (btnCobrar != null) btnCobrar.setDisable(true);
        if (txtEfectivo != null) txtEfectivo.setDisable(true);
    }

    private void info(String h, String m){ alert(Alert.AlertType.INFORMATION, h, m); }
    private void warn(String m){ alert(Alert.AlertType.WARNING, "Atención", m); }
    private void error(String h, String m){ alert(Alert.AlertType.ERROR, h, m); }
    private void alert(Alert.AlertType t, String h, String m){
        var a = new Alert(t);
        a.setHeaderText(h);
        a.setContentText(m);
        a.setTitle("Caja");
        a.showAndWait();
    }

    private static String nvl(String s){ return s == null ? "" : s; }
}
