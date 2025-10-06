package com.ferre.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Fábrica de conexiones JDBC con ajuste robusto de zona horaria.
 */
public final class DataSourceFactory {

    private DataSourceFactory() {
    }

    public static Connection getConnection() throws SQLException {
        Connection cn = DriverManager.getConnection(
                AppConfig.get("db.url"),
                AppConfig.get("db.user"),
                AppConfig.get("db.pass")
        );

        // Ajuste de zona horaria de la sesión MySQL.
        // 1) Intentar con el nombre IANA (America/Guatemala).
        // 2) Si el servidor no tiene tablas TZ y falla, caer al offset (-06:00).
        String tzId = AppConfig.get("app.tz"); // p.ej. America/Guatemala
        try (Statement st = cn.createStatement()) {
            try {
                st.execute("SET time_zone = 'America/Guatemala'");
            } catch (SQLException tzEx) {
                // Fallback si el servidor no reconoce el nombre de zona:
                st.execute("SET time_zone = '-06:00'");
            }
        }

        return cn;
    }
}
