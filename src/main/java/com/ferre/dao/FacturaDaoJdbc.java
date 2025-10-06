package com.ferre.dao;

import com.ferre.model.Factura;

import java.sql.*;

/**
 * Implementación JDBC de FacturaDao. Nota: el descuento de stock se hace de
 * forma ATÓMICA con UPDATE ... WHERE stock >= ?, para evitar inventario
 * negativo incluso bajo concurrencia.
 */
public class FacturaDaoJdbc implements FacturaDao {

    // reemplaza TODO el método por esto
    @Override
    public long crearFactura(Connection cn, Factura f) {
        String sql = "INSERT INTO factura(pedido_id, cajero_id, fecha, serie, numero, total) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, f.getPedidoId());
            ps.setLong(2, f.getCajeroId());

            // <<< GUARDA SOLO LA FECHA >>>
            // Si tu modelo tiene LocalDateTime:
            // ps.setDate(3, java.sql.Date.valueOf(f.getFecha().toLocalDate()));
            // Si tu modelo ya usa LocalDate:
            // ps.setDate(3, java.sql.Date.valueOf(f.getFecha()));
            ps.setDate(3, java.sql.Date.valueOf(f.getFecha().toLocalDate()));

            ps.setString(4, f.getSerie());
            ps.setString(5, f.getNumero());
            ps.setBigDecimal(6, f.getTotal());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                throw new RuntimeException("No se obtuvo ID de factura");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void crearPago(Connection cn, long facturaId, long formaPagoId, java.math.BigDecimal monto) {
        final String sql = "INSERT INTO pago (factura_id, forma_pago_id, monto) VALUES (?,?,?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, facturaId);
            ps.setLong(2, formaPagoId);
            ps.setBigDecimal(3, monto);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error registrando pago: " + e.getMessage(), e);
        }
    }

    @Override
    public int obtenerStockForUpdate(Connection cn, long productoId) {
        // Mantengo tu tabla 'productos' tal como la tienes en el proyecto.
        final String sql = "SELECT stock FROM productos WHERE id = ? FOR UPDATE";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, productoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new IllegalStateException("Producto no encontrado: id=" + productoId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo stock (FOR UPDATE): " + e.getMessage(), e);
        }
    }

    @Override
    public void disminuirStock(Connection cn, long productoId, int cantidad) {
        // DESCUENTO ATÓMICO: solo descuenta si stock >= cantidad
        final String sql = "UPDATE productos "
                + "SET stock = stock - ? "
                + "WHERE id = ? AND stock >= ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setLong(2, productoId);
            ps.setInt(3, cantidad);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                // Nadie actualizó: o no existe el producto, o stock era insuficiente.
                throw new IllegalStateException(
                        "Stock insuficiente para producto ID " + productoId + " al descontar " + cantidad + "."
                );
            }
        } catch (IllegalStateException ise) {
            throw ise;
        } catch (Exception e) {
            throw new RuntimeException("Error al disminuir stock: " + e.getMessage(), e);
        }
    }

    @Override
    public void cambiarEstadoPedido(Connection cn, long pedidoId, String nuevoEstado) {
        final String sql = "UPDATE pedido SET estado = ? WHERE id = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setLong(2, pedidoId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error cambiando estado de pedido: " + e.getMessage(), e);
        }
    }
}
