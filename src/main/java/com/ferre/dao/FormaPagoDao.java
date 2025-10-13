package com.ferre.dao;

import com.ferre.config.DataSourceFactory;
import com.ferre.model.FormaPago;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FormaPagoDao {

    public List<FormaPago> listar() {
        String sql = "SELECT * FROM forma_pago ORDER BY nombre";
        List<FormaPago> out = new ArrayList<>();
        try (Connection cn = DataSourceFactory.getConnection(); Statement st = cn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                FormaPago f = new FormaPago();
                f.setId(rs.getLong("id"));
                f.setNombre(rs.getString("nombre"));
                out.add(f);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return out;
    }
}
