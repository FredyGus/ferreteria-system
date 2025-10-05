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

    public long crearPedido(Pedido cab, List<PedidoDet> dets){
        if (dets==null || dets.isEmpty()) throw new IllegalArgumentException("Agrega artículos al pedido");
        BigDecimal total = dets.stream().map(PedidoDet::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        cab.setTotal(total);
        cab.setEstado("PENDIENTE");
        if (cab.getFecha()==null) cab.setFecha(LocalDateTime.now());

        try (Connection cn = DataSourceFactory.getConnection()){
            cn.setAutoCommit(false);
            try {
                long id = dao.crearCabecera(cn, cab);
                for (PedidoDet d : dets){ d.setPedidoId(id); dao.crearDetalle(cn, d); }
                cn.commit();
                return id;
            } catch(Exception e){
                cn.rollback(); throw e;
            } finally {
                cn.setAutoCommit(true);
            }
        } catch (Exception e){ throw new RuntimeException(e); }
    }

    public java.util.List<Pedido> listarPendientes(){ return dao.listarPendientes(); }
    public java.util.List<PedidoDet> detalles(long pedidoId){ return dao.obtenerDetalles(pedidoId); }
}
