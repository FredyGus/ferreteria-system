package com.ferre.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pedido {

    private long id;
    private long clienteId;
    private long vendedorId;
    private LocalDateTime fecha;
    private String estado;
    private String observaciones;
    private BigDecimal total;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getClienteId() {
        return clienteId;
    }

    public void setClienteId(long clienteId) {
        this.clienteId = clienteId;
    }

    public long getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(long vendedorId) {
        this.vendedorId = vendedorId;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
