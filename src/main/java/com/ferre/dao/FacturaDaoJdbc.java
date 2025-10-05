package com.ferre.dao;

import com.ferre.model.Factura;

import java.sql.*;

public class FacturaDaoJdbc implements FacturaDao {

    @Override
    public long crearFactura(Connection cn, Factura f) {
        String sql = "INSERT INTO factura(pedido_id, cajero_id, fecha, serie, numero, total) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, f.getPedidoId());
            ps.setLong(2, f.getCajeroId());
            ps.setTimestamp(3, Timestamp.valueOf(f.getFecha()));
            ps.setString(4, f.getSerie());
            ps.setString(5, f.getNumero());
            ps.setBigDecimal(6, f.getTotal());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()) return rs.getLong(1);
                throw new RuntimeException("No se obtuvo ID de factura");
            }
        } catch (Exception e){ throw new RuntimeException(e); }
    }

    @Override
    public void crearPago(Connection cn, long facturaId, long formaPagoId, java.math.BigDecimal monto) {
        String sql = "INSERT INTO pago(factura_id, forma_pago_id, monto) VALUES(?,?,?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, facturaId);
            ps.setLong(2, formaPagoId);
            ps.setBigDecimal(3, monto);
            ps.executeUpdate();
        } catch (Exception e){ throw new RuntimeException(e); }
    }

    @Override
    public int obtenerStockForUpdate(Connection cn, long productoId) {
        String sql = "SELECT stock FROM productos WHERE id=? FOR UPDATE";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, productoId);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) return rs.getInt(1);
                throw new RuntimeException("Producto no encontrado: " + productoId);
            }
        } catch (Exception e){ throw new RuntimeException(e); }
    }

    @Override
    public void disminuirStock(Connection cn, long productoId, int cantidad) {
        String sql = "UPDATE productos SET stock = stock - ? WHERE id=?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setLong(2, productoId);
            ps.executeUpdate();
        } catch (Exception e){ throw new RuntimeException(e); }
    }

    @Override
    public void cambiarEstadoPedido(Connection cn, long pedidoId, String nuevoEstado) {
        String sql = "UPDATE pedido SET estado=? WHERE id=?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setLong(2, pedidoId);
            ps.executeUpdate();
        } catch (Exception e){ throw new RuntimeException(e); }
    }
}
