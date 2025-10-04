package com.ferre.model;

import java.math.BigDecimal;

public class Producto {
    private long id;
    private String codigo;
    private String nombre;
    private String unidad;
    private BigDecimal precio;
    private int stock;
    private Long proveedorId;   // puede ser null
    private boolean activo;

    public Producto(){}

    public Producto(long id, String codigo, String nombre, String unidad, BigDecimal precio, int stock, Long proveedorId, boolean activo) {
        this.id = id; this.codigo=codigo; this.nombre=nombre; this.unidad=unidad; this.precio=precio; this.stock=stock; this.proveedorId=proveedorId; this.activo=activo;
    }

    public long getId(){ return id; }            public void setId(long id){ this.id = id; }
    public String getCodigo(){ return codigo; }  public void setCodigo(String codigo){ this.codigo = codigo; }
    public String getNombre(){ return nombre; }  public void setNombre(String nombre){ this.nombre = nombre; }
    public String getUnidad(){ return unidad; }  public void setUnidad(String unidad){ this.unidad = unidad; }
    public java.math.BigDecimal getPrecio(){ return precio; }  public void setPrecio(java.math.BigDecimal precio){ this.precio = precio; }
    public int getStock(){ return stock; }       public void setStock(int stock){ this.stock = stock; }
    public Long getProveedorId(){ return proveedorId; } public void setProveedorId(Long proveedorId){ this.proveedorId = proveedorId; }
    public boolean isActivo(){ return activo; }  public void setActivo(boolean activo){ this.activo = activo; }
}
