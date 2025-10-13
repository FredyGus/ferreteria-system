package com.ferre.service;

import com.ferre.config.DataSourceFactory;
import com.ferre.dao.PedidoDao;
import com.ferre.dao.PedidoDaoJdbc;
import com.ferre.model.Pedido;
import com.ferre.model.PedidoDet;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoService {

    private final PedidoDao dao = new PedidoDaoJdbc();

    public long crearPedido(Pedido cab, List<PedidoDet> dets) {
        if (dets == null || dets.isEmpty()) {
            throw new IllegalArgumentException("Agrega artículos al pedido");
        }
        BigDecimal total = dets.stream().map(PedidoDet::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        cab.setTotal(total);
        cab.setEstado("PENDIENTE");
        if (cab.getFecha() == null) {
            cab.setFecha(LocalDateTime.now());
        }

        try (Connection cn = DataSourceFactory.getConnection()) {
            cn.setAutoCommit(false);
            try {
                long id = dao.crearCabecera(cn, cab);
                for (PedidoDet d : dets) {
                    d.setPedidoId(id);
                    dao.crearDetalle(cn, d);
                }
                cn.commit();
                return id;
            } catch (Exception e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public java.util.List<Pedido> listarPendientes() {
        return dao.listarPendientes();
    }

    public java.util.List<PedidoDet> detalles(long pedidoId) {
        return dao.obtenerDetalles(pedidoId);
    }

    public com.ferre.model.Pedido findById(long id) {
        String sql = "SELECT id, cliente_id, vendedor_id, fecha, estado, total FROM pedido WHERE id=?";
        try (var cn = com.ferre.config.DataSourceFactory.getConnection(); var ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                var p = new com.ferre.model.Pedido();
                p.setId(rs.getLong("id"));
                p.setClienteId(rs.getLong("cliente_id"));
                p.setVendedorId(rs.getLong("vendedor_id"));
                p.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                p.setEstado(rs.getString("estado"));
                p.setTotal(rs.getBigDecimal("total"));
                return p;
            }
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar el pedido: " + e.getMessage(), e);
        }
    }

}
