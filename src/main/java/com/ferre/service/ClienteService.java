package com.ferre.service;

import com.ferre.dao.ClienteDao;
import com.ferre.dao.ClienteDaoJdbc;
import com.ferre.model.Cliente;
import java.util.List;

public class ClienteService {
    private final ClienteDao dao = new ClienteDaoJdbc();
    public List<Cliente> listar(){ return dao.listar(); }

    public void guardar(Cliente c){
        if (c.getNombre()==null || c.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es requerido");
        if (c.getId()==0) dao.crear(c); else dao.actualizar(c);
    }
    public void eliminar(long id){ dao.eliminar(id); }
}
