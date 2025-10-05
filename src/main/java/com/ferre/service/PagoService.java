package com.ferre.service;

import com.ferre.config.DataSourceFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PagoService {

    /** Suma los pagos existentes en BD para una factura. */
    public BigDecimal totalPagadoPorFactura(long facturaId) {
        String sql = "SELECT COALESCE(SUM(monto), 0) FROM pago WHERE factura_id = ?";
        try (Connection cn = DataSourceFactory.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, facturaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
                return BigDecimal.ZERO;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando total pagado de factura " + facturaId, e);
        }
    }

    /** Inserta un pago. Si quieres, puedes auditar cajero_id en una columna aparte. */
    public void registrar(long facturaId, long formaPagoId, BigDecimal monto, Long cajeroId) {
        String sql = "INSERT INTO pago (factura_id, forma_pago_id, monto) VALUES (?,?,?)";
        try (Connection cn = DataSourceFactory.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, facturaId);
            ps.setLong(2, formaPagoId);
            ps.setBigDecimal(3, monto);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error registrando pago", e);
        }
    }
}
