package com.ferre.dao;

import com.ferre.config.DataSourceFactory;
import com.ferre.model.Producto;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoDaoJdbc implements ProductoDao {

    private Producto map(ResultSet rs) throws SQLException {
        Long prov = rs.getObject("proveedor_id") == null ? null : rs.getLong("proveedor_id");
        return new Producto(
                rs.getLong("id"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("unidad"),
                rs.getBigDecimal("precio"),
                rs.getInt("stock"),
                prov,
                rs.getInt("activo") == 1
        );
    }

    @Override
    public List<Producto> listar() {
        String sql = "SELECT * FROM productos ORDER BY id DESC";
        List<Producto> out = new ArrayList<>();
        try (Connection cn = DataSourceFactory.getConnection(); Statement st = cn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                out.add(map(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public void crear(Producto p) {
        String sql = "INSERT INTO productos(codigo,nombre,unidad,precio,stock,proveedor_id,activo) VALUES(?,?,?,?,?,?,?)";
        try (Connection cn = DataSourceFactory.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getUnidad());
            ps.setBigDecimal(4, p.getPrecio());
            ps.setInt(5, p.getStock());
            if (p.getProveedorId() == null) {
                ps.setNull(6, Types.BIGINT);
            } else {
                ps.setLong(6, p.getProveedorId());
            }
            ps.setInt(7, p.isActivo() ? 1 : 0);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void actualizar(Producto p) {
        String sql = "UPDATE productos SET codigo=?, nombre=?, unidad=?, precio=?, stock=?, proveedor_id=?, activo=? WHERE id=?";
        try (Connection cn = DataSourceFactory.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getUnidad());
            ps.setBigDecimal(4, p.getPrecio());
            ps.setInt(5, p.getStock());
            if (p.getProveedorId() == null) {
                ps.setNull(6, Types.BIGINT);
            } else {
                ps.setLong(6, p.getProveedorId());
            }
            ps.setInt(7, p.isActivo() ? 1 : 0);
            ps.setLong(8, p.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cambiarEstado(long id, boolean activo) {
        String sql = "UPDATE productos SET activo=? WHERE id=?";
        try (Connection cn = DataSourceFactory.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, activo ? 1 : 0);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Producto> findByCodigo(String codigo) {
        String sql = "SELECT * FROM productos WHERE codigo=?";
        try (Connection cn = DataSourceFactory.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
