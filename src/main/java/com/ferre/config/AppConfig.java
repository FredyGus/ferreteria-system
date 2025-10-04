package com.ferre.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public final class AppConfig {
    private static final Properties PROPS = new Properties();

    static {
        try {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("app.properties");
            if (in == null) {
                in = AppConfig.class.getResourceAsStream("/app.properties");
            }
            if (in == null) {
                throw new IllegalStateException("No se encontró app.properties en resources (src/main/resources/app.properties). " +
                        "Verifica que el archivo exista y que aparezca en target/classes/app.properties tras compilar.");
            }
            PROPS.load(in);
            in.close();
        } catch (IOException e) {
            throw new RuntimeException("Error cargando app.properties", e);
        }
    }

    private AppConfig(){}

    public static String get(String key) {
        String val = PROPS.getProperty(key);
        return Objects.requireNonNull(val, "Falta clave en app.properties: " + key).trim();
    }
}
