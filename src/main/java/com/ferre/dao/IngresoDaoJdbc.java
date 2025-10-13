package com.ferre.dao;

import com.ferre.model.Ingreso;
import com.ferre.model.IngresoDet;

import java.sql.*;

public class IngresoDaoJdbc implements IngresoDao {

    @Override
    public long crearCabecera(Connection cn, Ingreso in) {
        String sql = "INSERT INTO ingreso(proveedor_id, bodeguero_id, fecha, no_doc, total) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, in.getProveedorId());
            if (in.getBodegueroId() == null) {
                ps.setNull(2, Types.BIGINT);
            } else {
                ps.setLong(2, in.getBodegueroId());
            }
            ps.setTimestamp(3, Timestamp.valueOf(in.getFecha()));
            ps.setString(4, in.getNoDoc());
            ps.setBigDecimal(5, in.getTotal());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            throw new RuntimeException("No se obtuvo ID de ingreso");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void crearDetalle(Connection cn, IngresoDet d) {
        String sql = "INSERT INTO ingreso_det(ingreso_id, producto_id, cantidad, costo_unit, subtotal) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, d.getIngresoId());
            ps.setLong(2, d.getProductoId());
            ps.setInt(3, d.getCantidad());
            ps.setBigDecimal(4, d.getCostoUnit());
            ps.setBigDecimal(5, d.getSubtotal());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void aumentarStock(Connection cn, long productoId, int cantidad) {
        String sql = "UPDATE productos SET stock = stock + ? WHERE id = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setLong(2, productoId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
