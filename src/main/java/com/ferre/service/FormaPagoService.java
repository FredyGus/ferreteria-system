package com.ferre.service;

import com.ferre.config.DataSourceFactory;
import com.ferre.model.FormaPago;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FormaPagoService {

    public List<FormaPago> listar() {
        String sql = "SELECT id, nombre FROM forma_pago ORDER BY nombre";
        List<FormaPago> out = new ArrayList<>();
        try (Connection cn = DataSourceFactory.getConnection(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                FormaPago f = new FormaPago();
                f.setId(rs.getLong("id"));
                f.setNombre(rs.getString("nombre"));
                out.add(f);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando formas de pago", e);
        }
        return out;
    }
}
