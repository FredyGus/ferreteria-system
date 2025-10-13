package com.ferre.service;

import com.ferre.config.Security;
import com.ferre.dao.UsuarioDao;
import com.ferre.dao.UsuarioDaoJdbc;
import com.ferre.model.Usuario;

public class AuthService {

    private final UsuarioDao dao = new UsuarioDaoJdbc();

    public Usuario login(String usuario, String password) throws Exception {
        var opt = dao.findByUsuario(usuario);
        if (opt.isEmpty()) {
            throw new Exception("Usuario no existe");
        }
        var u = opt.get();
        if (!u.isActivo()) {
            throw new Exception("Usuario inactivo");
        }
        String hash = Security.sha256Hex(password);
        if (!u.getPassHash().equalsIgnoreCase(hash)) {
            throw new Exception("Contraseña incorrecta");
        }
        return u;
    }
}
