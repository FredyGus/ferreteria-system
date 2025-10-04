package com.ferre.service;

import com.ferre.config.Security;
import com.ferre.dao.UsuarioDao;
import com.ferre.dao.UsuarioDaoJdbc;
import com.ferre.model.Usuario;

import java.util.List;

public class UsuarioService {
    private final UsuarioDao dao = new UsuarioDaoJdbc();

    public List<Usuario> listar(){ return dao.listar(); }

    public void crear(Usuario u, String passwordPlano){
        u.setPassHash(Security.sha256Hex(passwordPlano));
        dao.crear(u);
    }

    public void actualizar(Usuario u){ dao.actualizar(u); }

    public void resetPassword(long id, String nueva){
        dao.cambiarPassword(id, Security.sha256Hex(nueva));
    }

    public void cambiarEstado(long id, boolean activo){ dao.cambiarEstado(id, activo); }
}
