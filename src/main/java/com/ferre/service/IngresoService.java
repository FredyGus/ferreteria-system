package com.ferre.service;

import com.ferre.config.DataSourceFactory;
import com.ferre.dao.IngresoDao;
import com.ferre.dao.IngresoDaoJdbc;
import com.ferre.model.Ingreso;
import com.ferre.model.IngresoDet;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

public class IngresoService {

    private final IngresoDao dao = new IngresoDaoJdbc();

    public long registrar(Ingreso cab, List<IngresoDet> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("Debes agregar al menos un producto");
        }
        BigDecimal total = detalles.stream()
                .map(IngresoDet::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cab.setTotal(total);

        try (Connection cn = DataSourceFactory.getConnection()) {
            cn.setAutoCommit(false);
            try {
                long id = dao.crearCabecera(cn, cab);
                for (IngresoDet d : detalles) {
                    d.setIngresoId(id);
                    dao.crearDetalle(cn, d);
                    dao.aumentarStock(cn, d.getProductoId(), d.getCantidad());
                }
                cn.commit();
                return id;
            } catch (Exception ex) {
                cn.rollback();
                throw ex;
            } finally {
                cn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
