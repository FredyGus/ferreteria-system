package com.ferre.service;

import com.ferre.dao.ClienteDao;
import com.ferre.dao.ClienteDaoJdbc;
import com.ferre.model.Cliente;
import java.util.List;

public class ClienteService {

    private final ClienteDao dao = new ClienteDaoJdbc();

    public List<Cliente> listar() {
        return dao.listar();
    }

    public void guardar(Cliente c) {
        if (c.getNombre() == null || c.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }
        if (c.getId() == 0) {
            dao.crear(c);
        } else {
            dao.actualizar(c);
        }
    }

    public void eliminar(long id) {
        dao.eliminar(id);
    }

    public com.ferre.model.Cliente findById(long id) {
        String sql = "SELECT id, nombre, nit, telefono, direccion, email FROM clientes WHERE id=?";
        try (var cn = com.ferre.config.DataSourceFactory.getConnection(); var ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                var c = new com.ferre.model.Cliente();
                c.setId(rs.getLong("id"));
                c.setNombre(rs.getString("nombre"));
                c.setNit(rs.getString("nit"));
                c.setTelefono(rs.getString("telefono"));
                c.setDireccion(rs.getString("direccion"));
                c.setEmail(rs.getString("email"));
                return c;
            }
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar el cliente: " + e.getMessage(), e);
        }
    }

}
