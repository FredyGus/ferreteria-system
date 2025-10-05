package com.ferre.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Factura {
    private long id;
    private long pedidoId;
    private long cajeroId;
    private LocalDateTime fecha;
    private String serie;
    private String numero;
    private BigDecimal total;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getPedidoId() { return pedidoId; }
    public void setPedidoId(long pedidoId) { this.pedidoId = pedidoId; }
    public long getCajeroId() { return cajeroId; }
    public void setCajeroId(long cajeroId) { this.cajeroId = cajeroId; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
