package com.ferre.dao;

import com.ferre.config.DataSourceFactory;
import com.ferre.model.Rol;
import com.ferre.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDaoJdbc implements UsuarioDao {

    private Usuario map(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getString("usuario"),
            rs.getString("pass_hash"),
            Rol.valueOf(rs.getString("rol")),
            rs.getInt("estado")==1
        );
    }

    @Override public Optional<Usuario> findByUsuario(String user) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ?";
        try (Connection cn = com.ferre.config.DataSourceFactory.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, user);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (Exception e){ throw new RuntimeException(e); }
    }

    @Override public List<Usuario> listar() {
        String sql = "SELECT * FROM usuarios ORDER BY id DESC";
        List<Usuario> out = new ArrayList<>();
        try (Connection cn = DataSourceFactory.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) out.add(map(rs));
        } catch (Exception e){ throw new RuntimeException(e); }
        return out;
    }

    @Override public void crear(Usuario u) {
        String sql = "INSERT INTO usuarios(nombre, usuario, pass_hash, rol, estado) VALUES(?,?,?,?,?)";
        try (Connection cn = DataSourceFactory.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getUsuario());
            ps.setString(3, u.getPassHash());
            ps.setString(4, u.getRol().name());
            ps.setInt(5, u.isActivo()?1:0);
            ps.executeUpdate();
        } catch (Exception e){ throw new RuntimeException(e); }
    }

    @Override public void actualizar(Usuario u) {
        String sql = "UPDATE usuarios SET nombre=?, usuario=?, rol=?, estado=? WHERE id=?";
        try (Connection cn = DataSourceFactory.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getUsuario());
            ps.setString(3, u.getRol().name());
            ps.setInt(4, u.isActivo()?1:0);
            ps.setLong(5, u.getId());
            ps.executeUpdate();
        } catch (Exception e){ throw new RuntimeException(e); }
    }

    @Override public void cambiarPassword(long id, String nuevoHash) {
        String sql = "UPDATE usuarios SET pass_hash=? WHERE id=?";
        try (Connection cn = DataSourceFactory.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nuevoHash);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (Exception e){ throw new RuntimeException(e); }
    }

    @Override public void cambiarEstado(long id, boolean activo) {
        String sql = "UPDATE usuarios SET estado=? WHERE id=?";
        try (Connection cn = DataSourceFactory.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, activo?1:0);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (Exception e){ throw new RuntimeException(e); }
    }
}
