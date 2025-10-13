package com.ferre.controller;

import com.ferre.model.Rol;
import com.ferre.model.Usuario;
import com.ferre.service.UsuarioService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UsersController {

    @FXML
    private TableView<Usuario> tbl;
    @FXML
    private TableColumn<Usuario, String> colId, colUsuario, colNombre, colRol;
    @FXML
    private TableColumn<Usuario, Boolean> colEstado;
    @FXML
    private TextField txtUsuario, txtNombre;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ComboBox<Rol> cmbRol;
    @FXML
    private Label lblInfo;

    private final UsuarioService service = new UsuarioService();
    private Usuario seleccionado;

    @FXML
    public void initialize() {
        cmbRol.setItems(FXCollections.observableArrayList(Rol.values()));
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colUsuario.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuario()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colRol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRol().name()));
        colEstado.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isActivo()));
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean ok, boolean empty) {
                super.updateItem(ok, empty);
                setText(empty ? null : (ok ? "Sí" : "No"));
            }
        });

        tbl.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            seleccionado = n;
            if (n != null) {
                txtUsuario.setText(n.getUsuario());
                txtNombre.setText(n.getNombre());
                cmbRol.setValue(n.getRol());
                txtPassword.clear();
            }
        });

        recargar();
    }

    private void recargar() {
        tbl.setItems(FXCollections.observableArrayList(service.listar()));
        lblInfo.setText("Total: " + tbl.getItems().size());
    }

    @FXML
    private void nuevo() {
        seleccionado = null;
        txtUsuario.clear();
        txtNombre.clear();
        txtPassword.clear();
        cmbRol.setValue(Rol.VENTAS);
        tbl.getSelectionModel().clearSelection();
    }

    @FXML
    private void guardar(ActionEvent e) {
        String u = txtUsuario.getText() == null ? "" : txtUsuario.getText().trim();
        String n = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        Rol rol = cmbRol.getValue();
        if (u.isEmpty() || n.isEmpty() || rol == null) {
            alert(Alert.AlertType.WARNING, "Datos incompletos", "Usuario, nombre y rol son requeridos");
            return;
        }
        try {
            if (seleccionado == null) {
                String pwd = txtPassword.getText();
                if (pwd == null || pwd.isBlank()) {
                    alert(Alert.AlertType.WARNING, "Contraseña requerida", "Para crear usuario ingresa contraseña");
                    return;
                }
                Usuario nuevo = new Usuario(0, n, u, null, rol, true);
                service.crear(nuevo, pwd);
                alert(Alert.AlertType.INFORMATION, "Creado", "Usuario creado");
            } else {
                seleccionado.setUsuario(u);
                seleccionado.setNombre(n);
                seleccionado.setRol(rol);
                service.actualizar(seleccionado);
                alert(Alert.AlertType.INFORMATION, "Actualizado", "Usuario actualizado");
            }
            recargar();
            nuevo();
        } catch (Exception ex) {
            alert(Alert.AlertType.ERROR, "Error", ex.getMessage());
        }
    }

    @FXML
    private void resetPassword() {
        if (seleccionado == null) {
            alert(Alert.AlertType.WARNING, "Seleccione", "Selecciona un usuario");
            return;
        }
        String pwd = txtPassword.getText();
        if (pwd == null || pwd.isBlank()) {
            alert(Alert.AlertType.WARNING, "Contraseña", "Ingresa nueva contraseña");
            return;
        }
        try {
            service.resetPassword(seleccionado.getId(), pwd);
            alert(Alert.AlertType.INFORMATION, "Password", "Contraseña actualizada");
            txtPassword.clear();
        } catch (Exception ex) {
            alert(Alert.AlertType.ERROR, "Error", ex.getMessage());
        }
    }

    @FXML
    private void toggleEstado() {
        if (seleccionado == null) {
            alert(Alert.AlertType.WARNING, "Seleccione", "Selecciona un usuario");
            return;
        }
        try {
            boolean nuevoEstado = !seleccionado.isActivo();
            service.cambiarEstado(seleccionado.getId(), nuevoEstado);
            alert(Alert.AlertType.INFORMATION, "Estado", nuevoEstado ? "Activado" : "Desactivado");
            recargar();
            nuevo();
        } catch (Exception ex) {
            alert(Alert.AlertType.ERROR, "Error", ex.getMessage());
        }
    }

    private void alert(Alert.AlertType t, String h, String m) {
        var a = new Alert(t);
        a.setHeaderText(h);
        a.setContentText(m);
        a.setTitle("Usuarios");
        a.showAndWait();
    }
}
