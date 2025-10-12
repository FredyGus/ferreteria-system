package com.ferre.service;

import com.ferre.config.Security;
import com.ferre.dao.UsuarioDao;
import com.ferre.dao.UsuarioDaoJdbc;
import com.ferre.model.Usuario;

import java.util.List;

public class UsuarioService {

    private final UsuarioDao dao = new UsuarioDaoJdbc();

    public List<Usuario> listar() {
        return dao.listar();
    }

    public void crear(Usuario u, String passwordPlano) {
        u.setPassHash(Security.sha256Hex(passwordPlano));
        dao.crear(u);
    }

    public void actualizar(Usuario u) {
        dao.actualizar(u);
    }

    public void resetPassword(long id, String nueva) {
        dao.cambiarPassword(id, Security.sha256Hex(nueva));
    }

    public void cambiarEstado(long id, boolean activo) {
        dao.cambiarEstado(id, activo);
    }

    public Usuario findById(long id) {
        String sql = "SELECT id, nombre, usuario, rol, estado FROM usuarios WHERE id=?";
        try (var cn = com.ferre.config.DataSourceFactory.getConnection();
             var ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                var u = new Usuario();
                u.setId(rs.getLong("id"));
                u.setNombre(rs.getString("nombre"));
                u.setUsuario(rs.getString("usuario"));
                u.setRol(com.ferre.model.Rol.valueOf(rs.getString("rol")));
                u.setActivo(rs.getInt("estado") == 1);   // <— cambio aquí
                return u;
            }
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar el usuario: " + e.getMessage(), e);
        }
    }
}
