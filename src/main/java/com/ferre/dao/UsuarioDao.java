package com.ferre.dao;

import com.ferre.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioDao {
    Optional<Usuario> findByUsuario(String usuario);
    List<Usuario> listar();
    void crear(Usuario u);                    // u.passHash debe venir ya hasheado
    void actualizar(Usuario u);               // sin cambiar pass
    void cambiarPassword(long id, String nuevoHash);
    void cambiarEstado(long id, boolean activo);
}
