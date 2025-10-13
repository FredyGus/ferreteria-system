package com.ferre.model;

import java.math.BigDecimal;

public class PedidoDet {

    private long id;
    private long pedidoId;
    private long productoId;
    private int cantidad;
    private BigDecimal precioUnit;
    private BigDecimal subtotal;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public long getProductoId() {
        return productoId;
    }

    public void setProductoId(long productoId) {
        this.productoId = productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnit() {
        return precioUnit;
    }

    public void setPrecioUnit(BigDecimal precioUnit) {
        this.precioUnit = precioUnit;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
