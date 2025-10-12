package com.ferre.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Pago aplicado a una factura: pago(id, factura_id, forma_pago_id, monto, creado_en) */
public class Pago {
    private long id;
    private long facturaId;
    private long formaPagoId;          // << Usamos el ID directo
    private BigDecimal monto = BigDecimal.ZERO;
    private LocalDateTime creadoEn;

    public Pago() {}

    public Pago(long facturaId, long formaPagoId, BigDecimal monto) {
        this.facturaId = facturaId;
        this.formaPagoId = formaPagoId;
        this.monto = monto;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getFacturaId() { return facturaId; }
    public void setFacturaId(long facturaId) { this.facturaId = facturaId; }

    public long getFormaPagoId() { return formaPagoId; }
    public void setFormaPagoId(long formaPagoId) { this.formaPagoId = formaPagoId; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
