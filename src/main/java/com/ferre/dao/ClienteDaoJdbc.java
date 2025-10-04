package com.ferre.dao;

import com.ferre.config.DataSourceFactory;
import com.ferre.model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDaoJdbc implements ClienteDao {

    private Cliente map(ResultSet rs) throws SQLException {
        return new Cliente(
            rs.getLong("id"), rs.getString("nombre"), rs.getString("nit"),
            rs.getString("telefono"), rs.getString("direccion"), rs.getString("email")
        );
    }

    @Override public List<Cliente> listar() {
        String sql = "SELECT * FROM clientes ORDER BY id DESC";
        List<Cliente> out = new ArrayList<>();
        try (Connection cn = DataSourceFactory.getConnection();
             Statement st = cn.createStatement(); ResultSet rs = st.executeQuery(sql)){
            while (rs.next()) out.add(map(rs));
        } catch(Exception e){ throw new RuntimeException(e); }
        return out;
    }

    @Override public void crear(Cliente c) {
        String sql = "INSERT INTO clientes(nombre,nit,telefono,direccion,email) VALUES(?,?,?,?,?)";
        try (Connection cn = DataSourceFactory.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1,c.getNombre()); ps.setString(2,c.getNit());
            ps.setString(3,c.getTelefono()); ps.setString(4,c.getDireccion()); ps.setString(5,c.getEmail());
            ps.executeUpdate();
        } catch(Exception e){ throw new RuntimeException(e); }
    }

    @Override public void actualizar(Cliente c) {
        String sql = "UPDATE clientes SET nombre=?, nit=?, telefono=?, direccion=?, email=? WHERE id=?";
        try (Connection cn = DataSourceFactory.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1,c.getNombre()); ps.setString(2,c.getNit()); ps.setString(3,c.getTelefono());
            ps.setString(4,c.getDireccion()); ps.setString(5,c.getEmail()); ps.setLong(6,c.getId());
            ps.executeUpdate();
        } catch(Exception e){ throw new RuntimeException(e); }
    }

    @Override public void eliminar(long id) {
        String sql = "DELETE FROM clientes WHERE id=?";
        try (Connection cn = DataSourceFactory.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setLong(1,id); ps.executeUpdate();
        } catch(Exception e){ throw new RuntimeException(e); }
    }
}
