package com.ferre.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DataSourceFactory {

    private DataSourceFactory() {
    }

    public static Connection getConnection() throws SQLException {
        Connection cn = DriverManager.getConnection(
                AppConfig.get("db.url"),
                AppConfig.get("db.user"),
                AppConfig.get("db.pass")
        );

        try (Statement st = cn.createStatement()) {
            try {
                st.execute("SET time_zone = 'America/Guatemala'");
            } catch (SQLException tzEx) {
                st.execute("SET time_zone = '-06:00'");
            }
        }

        return cn;
    }
}
