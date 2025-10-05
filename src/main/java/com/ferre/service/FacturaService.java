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
import java.time.LocalDateTime;
import java.util.List;

public class FacturaService {
    private final FacturaDao fdao = new FacturaDaoJdbc();
    private final PedidoDao pdao = new PedidoDaoJdbc();

    /**
     * Factura un pedido pendiente:
     * - crea factura
     * - crea pago (único)
     * - verifica y descuenta stock
     * - cambia pedido a PAGADO
     */
    public long facturar(long pedidoId, long cajeroId, String serie, String numero, long formaPagoId){
        try (Connection cn = DataSourceFactory.getConnection()){
            cn.setAutoCommit(false);
            try {
                List<PedidoDet> dets = pdao.obtenerDetalles(pedidoId);
                if (dets.isEmpty()) throw new IllegalStateException("El pedido no tiene detalles");

                // Verificar stock con FOR UPDATE
                for (PedidoDet d : dets){
                    int stock = fdao.obtenerStockForUpdate(cn, d.getProductoId());
                    if (stock < d.getCantidad()){
                        throw new IllegalStateException("Stock insuficiente para producto ID " + d.getProductoId() +
                                " (actual: " + stock + ", requerido: " + d.getCantidad() + ")");
                    }
                }

                // Totales
                BigDecimal total = dets.stream().map(PedidoDet::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);

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
                for (PedidoDet d : dets){ fdao.disminuirStock(cn, d.getProductoId(), d.getCantidad()); }

                // Pago (único, por el total)
                fdao.crearPago(cn, facturaId, formaPagoId, total);

                // Cambiar estado del pedido
                fdao.cambiarEstadoPedido(cn, pedidoId, "PAGADO");

                cn.commit();
                return facturaId;
            } catch(Exception e){
                cn.rollback(); throw e;
            } finally { cn.setAutoCommit(true); }
        } catch (Exception e){ throw new RuntimeException(e); }
    }
}
