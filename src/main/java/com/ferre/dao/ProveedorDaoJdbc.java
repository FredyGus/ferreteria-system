package com.ferre.dao;

import com.ferre.config.DataSourceFactory;
import com.ferre.model.Proveedor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDaoJdbc implements ProveedorDao {

    private Proveedor map(ResultSet rs) throws SQLException {
        return new Proveedor(
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getString("nit"),
            rs.getString("telefono"),
            rs.getString("direccion"),
            rs.getString("email")
        );
    }

    @Override public List<Proveedor> listar() {
        String sql = "SELECT * FROM proveedores ORDER BY id DESC";
        List<Proveedor> out = new ArrayList<>();
        try (Connection cn = DataSourceFactory.getConnection();
             Statement st = cn.createStatement(); ResultSet rs = st.executeQuery(sql)){
            while (rs.next()) out.add(map(rs));
        } catch(Exception e){ throw new RuntimeException(e); }
        return out;
    }

    @Override public void crear(Proveedor p) {
        String sql = "INSERT INTO proveedores(nombre,nit,telefono,direccion,email) VALUES(?,?,?,?,?)";
        try (Connection cn = DataSourceFactory.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1, p.getNombre()); ps.setString(2, p.getNit());
            ps.setString(3, p.getTelefono()); ps.setString(4, p.getDireccion()); ps.setString(5, p.getEmail());
            ps.executeUpdate();
        } catch(Exception e){ throw new RuntimeException(e); }
    }

    @Override public void actualizar(Proveedor p) {
        String sql = "UPDATE proveedores SET nombre=?, nit=?, telefono=?, direccion=?, email=? WHERE id=?";
        try (Connection cn = DataSourceFactory.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1,p.getNombre()); ps.setString(2,p.getNit()); ps.setString(3,p.getTelefono());
            ps.setString(4,p.getDireccion()); ps.setString(5,p.getEmail()); ps.setLong(6,p.getId());
            ps.executeUpdate();
        } catch(Exception e){ throw new RuntimeException(e); }
    }

    @Override public void eliminar(long id) {
        String sql = "DELETE FROM proveedores WHERE id=?";
        try (Connection cn = DataSourceFactory.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setLong(1,id); ps.executeUpdate();
        } catch(Exception e){ throw new RuntimeException(e); }
    }
}
