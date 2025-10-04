package com.ferre.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DataSourceFactory {
    private DataSourceFactory(){}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            AppConfig.get("db.url"),
            AppConfig.get("db.user"),
            AppConfig.get("db.pass")
        );
    }
}
