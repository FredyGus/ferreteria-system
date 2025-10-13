package com.ferre.service;

import com.ferre.dao.ProductoDao;
import com.ferre.dao.ProductoDaoJdbc;
import com.ferre.dao.ProveedorDao;
import com.ferre.dao.ProveedorDaoJdbc;
import com.ferre.model.Producto;
import com.ferre.model.Proveedor;

import java.math.BigDecimal;
import java.util.List;

public class ProductoService {

    private final ProductoDao dao = new ProductoDaoJdbc();
    private final ProveedorDao provDao = new ProveedorDaoJdbc();

    public List<Producto> listar() {
        return dao.listar();
    }

    public List<Proveedor> listarProveedores() {
        return provDao.listar();
    }

    public void guardar(Producto p) {
        if (p.getCodigo() == null || p.getCodigo().isBlank()) {
            throw new IllegalArgumentException("Código requerido");
        }
        if (p.getNombre() == null || p.getNombre().isBlank()) {
            throw new IllegalArgumentException("Nombre requerido");
        }
        if (p.getPrecio() == null || p.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Precio debe ser > 0");
        }
        if (p.getStock() < 0) {
            throw new IllegalArgumentException("Stock no puede ser negativo");
        }

        var exist = dao.findByCodigo(p.getCodigo());
        if (p.getId() == 0) {
            if (exist.isPresent()) {
                throw new IllegalArgumentException("El código ya existe");
            }
            dao.crear(p);
        } else {
            if (exist.isPresent() && exist.get().getId() != p.getId()) {
                throw new IllegalArgumentException("El código ya existe en otro producto");
            }
            dao.actualizar(p);
        }
    }

    public void toggleActivo(long id, boolean activo) {
        dao.cambiarEstado(id, activo);
    }
}
