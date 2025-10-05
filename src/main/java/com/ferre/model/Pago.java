package com.ferre.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa un pago aplicado a una factura.
 * Tabla relacionada: pago (id, factura_id, forma_pago_id, monto, creado_en)
 */
public class Pago {

    private Long id;
    private Long facturaId;
    private FormaPago formaPago;         // objeto con id y nombre
    private BigDecimal monto = BigDecimal.ZERO;
    private LocalDateTime creadoEn;      // opcional, si lo guardas en BD

    public Pago() { }

    public Pago(Long facturaId, FormaPago formaPago, BigDecimal monto) {
        this.facturaId = facturaId;
        this.formaPago = formaPago;
        this.monto = monto;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFacturaId() { return facturaId; }
    public void setFacturaId(Long facturaId) { this.facturaId = facturaId; }

    public FormaPago getFormaPago() { return formaPago; }
    public void setFormaPago(FormaPago formaPago) { this.formaPago = formaPago; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
