package com.ferre.service;

import com.ferre.dao.ProveedorDao;
import com.ferre.dao.ProveedorDaoJdbc;
import com.ferre.model.Proveedor;
import java.util.List;

public class ProveedorService {

    private final ProveedorDao dao = new ProveedorDaoJdbc();

    public List<Proveedor> listar() {
        return dao.listar();
    }

    public void guardar(Proveedor p) {
        if (p.getNombre() == null || p.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }
        if (p.getId() == 0) {
            dao.crear(p);
        } else {
            dao.actualizar(p);
        }
    }

    public void eliminar(long id) {
        dao.eliminar(id);
    }
}
