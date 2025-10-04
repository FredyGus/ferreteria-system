package com.ferre.app;

import com.ferre.config.DataSourceFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.sql.Connection;

public class FerreteriaApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        Scene scene = new Scene(loader.load(), 920, 600);
        scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());

        stage.setTitle("Ferretería — Sistema (Fase 0)");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/html/ferreteria.png"))); 
        stage.setScene(scene);
        stage.show();

        try (Connection cn = DataSourceFactory.getConnection()) {
            String db = cn.getMetaData().getDatabaseProductName() + " " + cn.getMetaData().getDatabaseProductVersion();
            stage.setTitle("Ferretería — Conectado a " + db);
        } catch (Exception ex) {
            stage.setTitle("Ferretería — ⚠ SIN CONEXIÓN a MySQL: " + ex.getMessage());
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
