package com.ferre.model;

import java.math.BigDecimal;

public class IngresoDet {

    private long id;
    private long ingresoId;
    private long productoId;
    private int cantidad;
    private BigDecimal costoUnit;
    private BigDecimal subtotal;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIngresoId() {
        return ingresoId;
    }

    public void setIngresoId(long ingresoId) {
        this.ingresoId = ingresoId;
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

    public BigDecimal getCostoUnit() {
        return costoUnit;
    }

    public void setCostoUnit(BigDecimal costoUnit) {
        this.costoUnit = costoUnit;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
