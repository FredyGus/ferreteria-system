package com.ferre.dao;

import com.ferre.model.Factura;

import java.sql.Connection;

public interface FacturaDao {

    long crearFactura(Connection cn, Factura f);

    void crearPago(Connection cn, long facturaId, long formaPagoId, java.math.BigDecimal monto);

    int obtenerStockForUpdate(Connection cn, long productoId);

    void disminuirStock(Connection cn, long productoId, int cantidad);

    void cambiarEstadoPedido(Connection cn, long pedidoId, String nuevoEstado);
}
