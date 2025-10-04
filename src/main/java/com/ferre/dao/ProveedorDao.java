package com.ferre.dao;

import com.ferre.model.Proveedor;
import java.util.List;

public interface ProveedorDao {
    List<Proveedor> listar();
    void crear(Proveedor p);
    void actualizar(Proveedor p);
    void eliminar(long id); // ON DELETE SET NULL en productos
}
