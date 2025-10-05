package com.ferre.dao;

import com.ferre.model.Ingreso;
import com.ferre.model.IngresoDet;

import java.sql.Connection;

public interface IngresoDao {
    long crearCabecera(Connection cn, Ingreso in);
    void crearDetalle(Connection cn, IngresoDet d);
    void aumentarStock(Connection cn, long productoId, int cantidad);
}
