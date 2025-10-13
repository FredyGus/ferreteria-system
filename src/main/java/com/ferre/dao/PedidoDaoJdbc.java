package com.ferre.dao;

import com.ferre.model.Pedido;
import com.ferre.model.PedidoDet;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoDaoJdbc implements PedidoDao {

    @Override
    public long crearCabecera(Connection cn, Pedido p) {
        String sql = "INSERT INTO pedido(cliente_id, vendedor_id, fecha, estado, observaciones, total) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, p.getClienteId());
            ps.setLong(2, p.getVendedorId());
            ps.setTimestamp(3, Timestamp.valueOf(p.getFecha()));
            ps.setString(4, p.getEstado());
            ps.setString(5, p.getObservaciones());
            ps.setBigDecimal(6, p.getTotal());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                throw new RuntimeException("No se obtuvo ID de pedido");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void crearDetalle(Connection cn, PedidoDet d) {
        String sql = "INSERT INTO pedido_det(pedido_id, producto_id, cantidad, precio_unit, subtotal) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, d.getPedidoId());
            ps.setLong(2, d.getProductoId());
            ps.setInt(3, d.getCantidad());
            ps.setBigDecimal(4, d.getPrecioUnit());
            ps.setBigDecimal(5, d.getSubtotal());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Pedido> listarPendientes() {
        String sql = "SELECT * FROM pedido WHERE estado='PENDIENTE' ORDER BY id DESC";
        List<Pedido> out = new ArrayList<>();
        try (Connection cn = com.ferre.config.DataSourceFactory.getConnection(); Statement st = cn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Pedido p = new Pedido();
                p.setId(rs.getLong("id"));
                p.setClienteId(rs.getLong("cliente_id"));
                p.setVendedorId(rs.getLong("vendedor_id"));
                p.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                p.setEstado(rs.getString("estado"));
                p.setObservaciones(rs.getString("observaciones"));
                p.setTotal(rs.getBigDecimal("total"));
                out.add(p);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public List<PedidoDet> obtenerDetalles(long pedidoId) {
        String sql = "SELECT * FROM pedido_det WHERE pedido_id=? ORDER BY id";
        List<PedidoDet> out = new ArrayList<>();
        try (Connection cn = com.ferre.config.DataSourceFactory.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, pedidoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PedidoDet d = new PedidoDet();
                    d.setId(rs.getLong("id"));
                    d.setPedidoId(rs.getLong("pedido_id"));
                    d.setProductoId(rs.getLong("producto_id"));
                    d.setCantidad(rs.getInt("cantidad"));
                    d.setPrecioUnit(rs.getBigDecimal("precio_unit"));
                    d.setSubtotal(rs.getBigDecimal("subtotal"));
                    out.add(d);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return out;
    }
}
