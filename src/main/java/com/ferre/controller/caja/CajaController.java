package com.ferre.controller.caja;

import com.ferre.config.Session;
import com.ferre.model.Cliente;
import com.ferre.model.Factura;
import com.ferre.model.FormaPago;
import com.ferre.model.Pago;
import com.ferre.model.Pedido;
import com.ferre.model.Usuario;
import com.ferre.service.ClienteService;
import com.ferre.service.FacturaService;
import com.ferre.service.FormaPagoService;
import com.ferre.service.PagoService;
import com.ferre.service.PedidoService;
import com.ferre.service.UsuarioService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class CajaController {

    // Cabecera
    @FXML
    private Label lblFactura;
    @FXML
    private Label lblCliente;
    @FXML
    private Label lblVendedor;
    @FXML
    private Label lblFecha;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblPagado;
    @FXML
    private Label lblSaldo;

    // Entrada
    @FXML
    private ComboBox<FormaPago> cmbForma;
    @FXML
    private TextField txtMonto;

    // Tabla
    @FXML
    private TableView<Pago> tblPagos;
    @FXML
    private TableColumn<Pago, String> colForma;
    @FXML
    private TableColumn<Pago, String> colMonto;

    // Estado
    private final ObservableList<Pago> pagosSesion = FXCollections.observableArrayList();
    private Factura facturaActual;

    // Servicios
    private final FormaPagoService formaSrv = new FormaPagoService();
    private final PagoService pagoSrv = new PagoService();
    private final FacturaService facturaSrv = new FacturaService();
    private final PedidoService pedidoSrv = new PedidoService();
    private final ClienteService clienteSrv = new ClienteService();
    private final UsuarioService usuarioSrv = new UsuarioService();

    // Utilidades
    private final NumberFormat money = NumberFormat.getCurrencyInstance(new Locale("es", "GT"));
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private Map<Long, String> formaNombreById = new HashMap<>();

    @FXML
    public void initialize() {
        // Seguridad básica
        if (Session.get() == null) {
            new Alert(Alert.AlertType.WARNING,
                    "No hay usuario en sesión. Vuelve a iniciar sesión para usar Caja.",
                    ButtonType.OK).showAndWait();
            deshabilitarTodo();
            return;
        }

        // Combos
        var formas = FXCollections.observableArrayList(formaSrv.listar());
        formaNombreById = formas.stream()
                .collect(Collectors.toMap(FormaPago::getId, FormaPago::getNombre));
        cmbForma.setItems(formas);
        cmbForma.setConverter(new StringConverter<>() {
            @Override
            public String toString(FormaPago fp) {
                return fp == null ? "" : fp.getNombre();
            }

            @Override
            public FormaPago fromString(String s) {
                return null;
            }
        });

        // Solo números/decimal en monto
        txtMonto.textProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) {
                return;
            }
            String clean = newV.replace(",", ".")
                    .replaceAll("[^0-9.]", "");
            // máximo un punto
            int first = clean.indexOf('.');
            if (first >= 0) {
                int next = clean.indexOf('.', first + 1);
                if (next > 0) {
                    clean = clean.substring(0, next);
                }
            }
            if (!clean.equals(newV)) {
                txtMonto.setText(clean);
            }
        });

        // Tabla
        tblPagos.setItems(pagosSesion);
        colForma.setCellValueFactory(cd -> {
            // >>> Ajusta aquí si tu getter se llama distinto:
            long formaId = cd.getValue().getFormaPagoId();
            String nombre = formaNombreById.getOrDefault(formaId, String.valueOf(formaId));
            return new SimpleStringProperty(nombre);
        });
        colMonto.setCellValueFactory(cd
                -> new SimpleStringProperty(money.format(cd.getValue().getMonto())));

        refrescarTotales(); // valores en blanco inicialmente
    }

    public void setFactura(com.ferre.model.Factura f) {
        // Traemos la factura completa por id
        this.facturaActual = facturaSrv.findById(f.getId());

        // Limpiamos pagos en memoria y refrescamos la UI
        pagosSesion.clear();
        pintarCabecera();   // debe llenar lblFactura, lblCliente, lblVendedor, lblFecha, lblTotal
        refrescarTotales(); // debe recalcular Pagado/Saldo con lo de BD (0 si aún no hay pagos)
    }

    private void pintarCabecera() {
        if (facturaActual == null) {
            return;
        }

        lblFactura.setText(facturaActual.getSerie() + "-" + facturaActual.getNumero());

        // Pedido -> cliente y vendedor
        Pedido ped = pedidoSrv.findById(facturaActual.getPedidoId()); // Ajusta si tu service se llama distinto
        if (ped != null) {
            Cliente cli = clienteSrv.findById(ped.getClienteId());
            Usuario ven = usuarioSrv.findById(ped.getVendedorId());
            lblCliente.setText(cli != null ? cli.getNombre() : "-");
            lblVendedor.setText(ven != null ? ven.getNombre() : "-");
        } else {
            lblCliente.setText("-");
            lblVendedor.setText("-");
        }

        lblFecha.setText(facturaActual.getFecha() == null
                ? "-"
                : dtf.format(facturaActual.getFecha()));
        lblTotal.setText(money.format(nvl(facturaActual.getTotal())));
    }

    @FXML
    private void agregarPago() {
        var fp = cmbForma.getValue();
        var monto = toBig(txtMonto.getText());
        if (fp == null) {
            alert(Alert.AlertType.WARNING, "Selecciona una forma de pago.");
            return;
        }
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            alert(Alert.AlertType.WARNING, "Ingresa un monto válido mayor a 0.");
            return;
        }
        var p = new Pago();
        // >>> Ajusta esta línea si tu modelo usa otro nombre de setter:
        p.setFormaPagoId(fp.getId());
        p.setMonto(monto);
        pagosSesion.add(p);
        txtMonto.clear();
        refrescarTotales();
    }

    @FXML
    private void quitarPago() {
        var sel = tblPagos.getSelectionModel().getSelectedItem();
        if (sel != null) {
            pagosSesion.remove(sel);
            refrescarTotales();
        }
    }

    @FXML
    private void cobrar() {
        if (facturaActual == null) {
            return;
        }

        BigDecimal totalSesion = pagosSesion.stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalSesion.compareTo(BigDecimal.ZERO) <= 0) {
            alert(Alert.AlertType.WARNING, "No hay pagos en la lista.");
            return;
        }

        var ok = new Alert(Alert.AlertType.CONFIRMATION,
                "Se registrarán " + pagosSesion.size() + " pago(s) por " + money.format(totalSesion) + ". ¿Continuar?",
                ButtonType.OK, ButtonType.CANCEL).showAndWait();

        if (ok.isEmpty() || ok.get() == ButtonType.CANCEL) {
            return;
        }

        // Persistir cada pago
        for (Pago p : pagosSesion) {
            // >>> Ajusta getFormaPagoId() si tu getter tiene otro nombre
            pagoSrv.registrar(facturaActual.getId(), p.getFormaPagoId(), p.getMonto());
        }

        new Alert(Alert.AlertType.INFORMATION, "Pagos registrados con éxito.").showAndWait();
        pagosSesion.clear();
        refrescarTotales();
        // puedes recargar cabecera si cambió estado/saldo en BD
    }

    // ======================
    // Utilidades de pantalla
    // ======================
    private void refrescarTotales() {
        if (facturaActual == null) {
            lblPagado.setText(money.format(0));
            lblSaldo.setText(money.format(0));
            return;
        }
        BigDecimal pagadoDb = pagoSrv.totalPagadoPorFactura(facturaActual.getId()); // 0 si no hay
        BigDecimal pagadoSesion = pagosSesion.stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = nvl(facturaActual.getTotal());
        BigDecimal saldo = total.subtract(pagadoDb).subtract(pagadoSesion);

        lblPagado.setText(money.format(pagadoDb.add(pagadoSesion)));
        lblSaldo.setText(money.format(saldo.max(BigDecimal.ZERO)));
    }

    private void deshabilitarTodo() {
        if (cmbForma != null) {
            cmbForma.setDisable(true);
        }
        if (txtMonto != null) {
            txtMonto.setDisable(true);
        }
        if (tblPagos != null) {
            tblPagos.setDisable(true);
        }
    }

    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private BigDecimal toBig(String s) {
        if (s == null || s.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(s.replace(",", ".").trim());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private void alert(Alert.AlertType t, String msg) {
        new Alert(t, msg, ButtonType.OK).showAndWait();
    }
}
