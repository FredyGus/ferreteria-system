package com.ferre.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Ingreso {
    private long id;
    private long proveedorId;
    private Long bodegueroId;     // usuario que registra
    private LocalDateTime fecha;
    private String noDoc;
    private BigDecimal total;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getProveedorId() { return proveedorId; }
    public void setProveedorId(long proveedorId) { this.proveedorId = proveedorId; }
    public Long getBodegueroId() { return bodegueroId; }
    public void setBodegueroId(Long bodegueroId) { this.bodegueroId = bodegueroId; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getNoDoc() { return noDoc; }
    public void setNoDoc(String noDoc) { this.noDoc = noDoc; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
