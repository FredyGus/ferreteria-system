package com.ferre.controller.caja;

import com.ferre.model.Rol;
import com.ferre.model.Usuario;

import com.ferre.config.Session;
import com.ferre.model.Factura;
import com.ferre.model.FormaPago;
import com.ferre.model.Pago;
import com.ferre.service.FormaPagoService;
import com.ferre.service.PagoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;   // ← ESTE es el que falta


import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

public class CajaController {

    // --- Cabecera
    @FXML
    private Label lblFactura;
    @FXML
    private Label lblCliente;     // si tu Factura no trae el nombre, lo dejamos vacío
    @FXML
    private Label lblVendedor;    // igual que arriba
    @FXML
    private Label lblFecha;
    @FXML
    private Label lblTotal;

    // --- Totales dinámicos
    @FXML
    private Label lblPagado;
    @FXML
    private Label lblSaldo;

    // --- Entrada de pagos
    @FXML
    private ComboBox<FormaPago> cmbForma;
    @FXML
    private TextField txtMonto;
    @FXML
    private Button btnAgregar;
    @FXML
    private Button btnQuitar;

    // --- Tabla de pagos en sesión
    @FXML
    private TableView<Pago> tblPagos;
    @FXML
    private TableColumn<Pago, String> colForma;
    @FXML
    private TableColumn<Pago, String> colMonto;

    // --- Acción final
    @FXML
    private Button btnCobrar;

    // Estado
    private Factura facturaActual;
    private final ObservableList<Pago> pagosSesion = FXCollections.observableArrayList();

    // Servicios
    private final PagoService pagoSrv = new PagoService();
    private final FormaPagoService formaSrv = new FormaPagoService();

    // Formato
    private final NumberFormat money = NumberFormat.getCurrencyInstance(new Locale("es", "GT"));
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        // Seguridad básica (ADMIN o CAJA)
        Usuario u = Session.get();
        if (u == null || !(u.getRol() == Rol.ADMIN || u.getRol() == Rol.CAJA)) {
            new Alert(Alert.AlertType.WARNING,
                    "No tienes permisos para acceder a Caja (requiere rol ADMIN o CAJA).",
                    ButtonType.OK).showAndWait();
            deshabilitarTodo(); // tu método que bloquea la pantalla
            return;
        }

        // Combos y tabla
        cmbForma.setItems(FXCollections.observableArrayList(formaSrv.listar()));
        cmbForma.setConverter(new StringConverter<FormaPago>() {
            @Override
            public String toString(FormaPago fp) {
                return fp == null ? "" : fp.getNombre();
            }

            @Override
            public FormaPago fromString(String s) {
                return null;
            }
        });

        tblPagos.setItems(pagosSesion);
        colForma.setCellValueFactory(data
                -> new SimpleStringProperty(data.getValue().getFormaPago() == null
                        ? "" : data.getValue().getFormaPago().getNombre()));
        colMonto.setCellValueFactory(data
                -> new SimpleStringProperty(money.format(nvl(data.getValue().getMonto()))));

        // Botones
        btnAgregar.setOnAction(e -> onAgregar());
        btnQuitar.setOnAction(e -> onQuitar());
        btnCobrar.setOnAction(e -> onCobrar());
    }

    private void deshabilitarTodo() {
        cmbForma.setDisable(true);
        txtMonto.setDisable(true);
        btnAgregar.setDisable(true);
        btnQuitar.setDisable(true);
        btnCobrar.setDisable(true);
    }

    /**
     * Inyecta la factura a cobrar (desde la pantalla que abre Caja).
     */
    public void setFactura(Factura f) {
        this.facturaActual = f;         // usamos la que viene; evitamos dependencias extra
        pagosSesion.clear();
        pintarCabecera();
        refrescarTotales();
    }

    private void pintarCabecera() {
        if (facturaActual == null) {
            return;
        }

        String folio = (safe(facturaActual.getSerie()) + "-" + safe(facturaActual.getNumero())).replaceAll("^-|-$", "");
        lblFactura.setText(folio.isBlank() ? ("#" + facturaActual.getId()) : folio);

        // Si tu modelo no trae nombres anidados, dejamos vacío (no rompe nada)
        lblCliente.setText("");
        lblVendedor.setText("");

        lblFecha.setText(facturaActual.getFecha() == null
                ? "" : df.format(facturaActual.getFecha()));

        lblTotal.setText(money.format(nvl(facturaActual.getTotal())));
    }

    private void refrescarTotales() {
        if (facturaActual == null) {
            return;
        }

        BigDecimal pagadoDb = pagoSrv.totalPagadoPorFactura(facturaActual.getId());
        BigDecimal pagadoSesion = pagosSesion.stream()
                .map(p -> nvl(p.getMonto()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = nvl(facturaActual.getTotal());
        BigDecimal pagado = pagadoDb.add(pagadoSesion);
        BigDecimal saldo = total.subtract(pagado);

        lblPagado.setText(money.format(pagado.max(BigDecimal.ZERO)));
        lblSaldo.setText(money.format(saldo.max(BigDecimal.ZERO)));
    }

    // =============== Acciones de UI ===============
    private void onAgregar() {
        if (facturaActual == null) {
            return;
        }

        FormaPago fp = cmbForma.getValue();
        if (fp == null) {
            alertWarn("Selecciona la forma de pago.");
            return;
        }

        BigDecimal monto = toBig(txtMonto.getText());
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            alertWarn("Ingresa un monto válido mayor que cero.");
            return;
        }

        BigDecimal saldoActual = getSaldoActual();
        if (monto.compareTo(saldoActual) > 0) {
            alertWarn("El monto excede el saldo actual (" + money.format(saldoActual) + ").");
            return;
        }

        Pago p = new Pago();
        p.setFormaPago(fp);
        p.setMonto(monto);
        pagosSesion.add(p);

        txtMonto.clear();
        cmbForma.getSelectionModel().clearSelection();
        refrescarTotales();
    }

    private void onQuitar() {
        Pago sel = tblPagos.getSelectionModel().getSelectedItem();
        if (sel != null) {
            pagosSesion.remove(sel);
            refrescarTotales();
        }
    }

    private void onCobrar() {
        if (facturaActual == null) {
            return;
        }
        if (pagosSesion.isEmpty()) {
            alertWarn("Agrega al menos un pago.");
            return;
        }

        BigDecimal totalPagos = pagosSesion.stream()
                .map(p -> nvl(p.getMonto()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Optional<ButtonType> ok = new Alert(Alert.AlertType.CONFIRMATION,
                "Se registrarán " + pagosSesion.size() + " pago(s) por " + money.format(totalPagos) + ". ¿Continuar?",
                ButtonType.OK, ButtonType.CANCEL).showAndWait();

        if (ok.isEmpty() || ok.get() == ButtonType.CANCEL) {
            return;
        }

        // Persistimos pagos
        for (Pago p : pagosSesion) {
            pagoSrv.registrar(facturaActual.getId(),
                    p.getFormaPago().getId(),
                    nvl(p.getMonto()),
                    Session.get() == null ? null : Session.get().getId());
        }

        pagosSesion.clear();
        refrescarTotales();

        new Alert(Alert.AlertType.INFORMATION, "Pagos registrados con éxito.").showAndWait();
        // Si quieres marcar pagada cuando saldo = 0, puedes hacerlo aquí
        // (depende de cómo lo manejes en tu FacturaService/DAO).
    }

    // =============== Utiles ===============
    private BigDecimal getSaldoActual() {
        BigDecimal pagadoDb = pagoSrv.totalPagadoPorFactura(facturaActual.getId());
        BigDecimal pagadoSesion = pagosSesion.stream()
                .map(p -> nvl(p.getMonto()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return nvl(facturaActual.getTotal()).subtract(pagadoDb).subtract(pagadoSesion).max(BigDecimal.ZERO);
    }

    private static BigDecimal nvl(BigDecimal b) {
        return b == null ? BigDecimal.ZERO : b;
    }

    private static BigDecimal toBig(String s) {
        if (s == null) {
            return BigDecimal.ZERO;
        }
        String t = s.trim().replace(",", "");
        if (t.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(t);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private static void alertWarn(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
