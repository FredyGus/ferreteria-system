package com.ferre.controller;

import com.ferre.config.Session;
import com.ferre.model.Factura;
import com.ferre.model.Pedido;
import com.ferre.model.PedidoDet;
import com.ferre.model.Producto;
import com.ferre.service.FacturaService;
import com.ferre.service.PedidoService;
import com.ferre.service.ProductoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class FacturarController {

    // referencia al Main para poder abrir Caja con la factura creada
    private MainController main;

    @FXML private ComboBox<Pedido> cmbPedido;
    @FXML private TextField txtSerie, txtNumero;
    @FXML private TableView<PedidoDet> tbl;
    @FXML private TableColumn<PedidoDet,String> colCodigo, colNombre, colCantidad, colPrecio, colSubtotal;
    @FXML private Label lblTotal;

    private final PedidoService pedidoService = new PedidoService();
    private final FacturaService facturaService = new FacturaService();
    private final ProductoService prodService = new ProductoService();

    private List<Pedido> pendientes;

    // llamado por MainController al abrir esta pantalla
    public void setMain(MainController main) { this.main = main; }

    @FXML
    public void initialize() {
        // lista de pedidos pendientes
        cargarPedidos();

        // cómo mostrar cada pedido en el combo
        cmbPedido.setCellFactory(cb -> new ListCell<>() {
            @Override protected void updateItem(Pedido p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p==null ? null : "Pedido #" + p.getId() + " — Total: " + p.getTotal());
            }
        });
        cmbPedido.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Pedido p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p==null ? null : "Pedido #" + p.getId() + " — Total: " + p.getTotal());
            }
        });

        // columnas de la tabla
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(findCodigo(c.getValue().getProductoId())));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(findNombre(c.getValue().getProductoId())));
        colCantidad.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCantidad())));
        colPrecio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrecioUnit().toPlainString()));
        colSubtotal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubtotal().toPlainString()));

        // refrescar detalle al cambiar el pedido seleccionado
        cmbPedido.getSelectionModel().selectedItemProperty().addListener((o, oldV, now) -> {
            if (now != null) cargarDetalles(now);
        });
    }

    private void cargarPedidos(){
        pendientes = pedidoService.listarPendientes();
        cmbPedido.setItems(FXCollections.observableArrayList(pendientes));
        if (!pendientes.isEmpty()){
            cmbPedido.getSelectionModel().select(0);
            cargarDetalles(pendientes.get(0));
        } else {
            tbl.setItems(FXCollections.observableArrayList());
            lblTotal.setText("Total: 0");
        }
    }

    private void cargarDetalles(Pedido p){
        var dets = pedidoService.detalles(p.getId());
        tbl.setItems(FXCollections.observableArrayList(dets));
        lblTotal.setText("Total: " + p.getTotal());
    }

    private String findCodigo(long id){
        return prodService.listar().stream()
                .filter(x->x.getId()==id).map(Producto::getCodigo)
                .findFirst().orElse("?");
    }
    private String findNombre(long id){
        return prodService.listar().stream()
                .filter(x->x.getId()==id).map(Producto::getNombre)
                .findFirst().orElse("?");
    }

    @FXML private void refrescarPedidos(){ cargarPedidos(); }

    @FXML
    private void facturar(){
        try{
            Pedido p = cmbPedido.getSelectionModel().getSelectedItem();
            if (p==null){ warn("No hay pedido seleccionado"); return; }

            String serie = txtSerie.getText()==null ? "" : txtSerie.getText().trim();
            String numero = txtNumero.getText()==null ? "" : txtNumero.getText().trim();
            if (serie.isEmpty() || numero.isEmpty()){ warn("Serie y número son requeridos"); return; }

            // ahora facturar() NO recibe formaPagoId (pago se hace en Caja)
            long facturaId = facturaService.facturar(
                    p.getId(), Session.get().getId(), serie, numero
            );

            // Ir a CAJA con la factura recién creada
            if (main != null) {
                Factura f = new Factura();
                f.setId(facturaId);
                main.openCajaConFactura(f);
            } else {
                info("Factura creada",
                        "Factura #" + facturaId + " generada y stock actualizado.");
            }
        }catch(Exception ex){
            var msg = ex.getMessage();
            if (msg == null || msg.isBlank()) msg = ex.toString();
            error("No se pudo facturar", msg);
        }
    }

    private void info(String h, String m){ alert(Alert.AlertType.INFORMATION,h,m); }
    private void warn(String m){ alert(Alert.AlertType.WARNING,"Atención",m); }
    private void error(String h, String m){ alert(Alert.AlertType.ERROR,h,m); }
    private void alert(Alert.AlertType t, String h, String m){
        var a=new Alert(t);
        a.setHeaderText(h);
        a.setContentText(m);
        a.setTitle("Facturar");
        a.showAndWait();
    }
}
