package com.ferre.dao;

import com.ferre.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoDao {
    List<Producto> listar();
    void crear(Producto p);
    void actualizar(Producto p);
    void cambiarEstado(long id, boolean activo);
    Optional<Producto> findByCodigo(String codigo);
}
