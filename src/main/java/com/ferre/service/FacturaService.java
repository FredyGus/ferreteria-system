package com.ferre.service;

import com.ferre.config.DataSourceFactory;
import com.ferre.dao.FacturaDao;
import com.ferre.dao.FacturaDaoJdbc;
import com.ferre.dao.PedidoDao;
import com.ferre.dao.PedidoDaoJdbc;
import com.ferre.model.Factura;
import com.ferre.model.Pedido;
import com.ferre.model.PedidoDet;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

public class FacturaService {

    private final FacturaDao fdao = new FacturaDaoJdbc();
    private final PedidoDao pdao = new PedidoDaoJdbc();

    /**
     * Crea la factura a partir de un pedido. Verifica stock y descuenta, pero
     * NO registra pagos.
     */
    public long facturar(long pedidoId, long cajeroId, String serie, String numero) {
        try (Connection cn = DataSourceFactory.getConnection()) {
            cn.setAutoCommit(false);
            try {
                List<PedidoDet> dets = pdao.obtenerDetalles(pedidoId);
                if (dets.isEmpty()) {
                    throw new IllegalStateException("El pedido no tiene detalles.");
                }

                // Verificar stock con FOR UPDATE
                for (PedidoDet d : dets) {
                    int stock = fdao.obtenerStockForUpdate(cn, d.getProductoId());
                    if (stock < d.getCantidad()) {
                        throw new IllegalStateException(
                                "Stock insuficiente para producto ID " + d.getProductoId()
                                + " (actual: " + stock + ", requerido: " + d.getCantidad() + ")"
                        );
                    }
                }

                // Total
                BigDecimal total = dets.stream()
                        .map(PedidoDet::getSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Crear factura
                Factura f = new Factura();
                f.setPedidoId(pedidoId);
                f.setCajeroId(cajeroId);
                f.setFecha(LocalDateTime.now());
                f.setSerie(serie);
                f.setNumero(numero);
                f.setTotal(total);

                long facturaId = fdao.crearFactura(cn, f);

                // Descontar stock
                for (PedidoDet d : dets) {
                    fdao.disminuirStock(cn, d.getProductoId(), d.getCantidad());
                }

                // Cambiar estado del pedido
                fdao.cambiarEstadoPedido(cn, pedidoId, "PAGADO"); // o 'FACTURADO' si prefieres otro estado

                cn.commit();
                return facturaId;
            } catch (Exception ex) {
                cn.rollback();
                // Si es una IllegalStateException (como stock insuficiente), re-lanzamos limpio
                if (ex instanceof IllegalStateException) {
                    throw (IllegalStateException) ex;
                }
                // Para el resto, mensaje claro
                throw new RuntimeException("Error al facturar: " + ex.getMessage(), ex);
            } finally {
                cn.setAutoCommit(true);
            }
        } catch (Exception e) {
            // Se propaga ya limpio hacia el controller
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e.getMessage(), e);
        }
    }

    public com.ferre.model.Factura findById(long id) {
        String sql = "SELECT id, pedido_id, cajero_id, fecha, serie, numero, total FROM factura WHERE id = ?";
        try (var cn = com.ferre.config.DataSourceFactory.getConnection(); var ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                var f = new com.ferre.model.Factura();
                f.setId(rs.getLong("id"));
                f.setPedidoId(rs.getLong("pedido_id"));
                f.setCajeroId(rs.getLong("cajero_id"));
                var ts = rs.getTimestamp("fecha");
                f.setFecha(ts != null ? ts.toLocalDateTime() : null);
                f.setSerie(rs.getString("serie"));
                f.setNumero(rs.getString("numero"));
                f.setTotal(rs.getBigDecimal("total"));
                return f;
            }
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar la factura: " + e.getMessage(), e);
        }
    }

}
