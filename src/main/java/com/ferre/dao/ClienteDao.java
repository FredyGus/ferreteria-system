package com.ferre.dao;

import com.ferre.model.Cliente;
import java.util.List;

public interface ClienteDao {
    List<Cliente> listar();
    void crear(Cliente c);
    void actualizar(Cliente c);
    void eliminar(long id);
}
