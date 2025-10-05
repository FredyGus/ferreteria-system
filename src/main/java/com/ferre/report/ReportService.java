package com.ferre.report;

import com.ferre.config.DataSourceFactory;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;

public class ReportService {

    private JasperReport compile(String jrxmlClasspath) {
        try (InputStream in = getClass().getResourceAsStream(jrxmlClasspath)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró el jrxml: " + jrxmlClasspath);
            }
            return JasperCompileManager.compileReport(in);
        } catch (Exception e) {
            throw new RuntimeException("Error compilando reporte " + jrxmlClasspath, e);
        }
    }

    public JasperPrint fill(String jrxmlClasspath, Map<String, Object> params) {
        try (Connection cn = DataSourceFactory.getConnection()) {
            JasperReport rep = compile(jrxmlClasspath);
            return JasperFillManager.fillReport(rep, params, cn);
        } catch (Exception e) {
            throw new RuntimeException("Error llenando reporte " + jrxmlClasspath, e);
        }
    }

    public void view(JasperPrint print, String titulo) {
        JasperViewer v = new JasperViewer(print, false);
        v.setTitle(titulo);
        v.setAlwaysOnTop(true);
        v.setLocationRelativeTo(null); // centrar
        v.setVisible(true);
    }

}
