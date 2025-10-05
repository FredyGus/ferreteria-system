package com.ferre.controller;

import com.ferre.config.Session;
import com.ferre.dao.FormaPagoDao;
import com.ferre.model.FormaPago;
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

import java.math.BigDecimal;
import java.util.List;

public class FacturarController {

    @FXML private ComboBox<Pedido> cmbPedido;
    @FXML private ComboBox<FormaPago> cmbFormaPago;
    @FXML private TextField txtSerie, txtNumero;
    @FXML private TableView<PedidoDet> tbl;
    @FXML private TableColumn<PedidoDet,String> colCodigo, colNombre, colCantidad, colPrecio, colSubtotal;
    @FXML private Label lblTotal;

    private final PedidoService pedidoService = new PedidoService();
    private final FacturaService facturaService = new FacturaService();
    private final ProductoService prodService = new ProductoService();
    private final FormaPagoDao formaPagoDao = new FormaPagoDao();

    private List<Pedido> pendientes;

    @FXML
    public void initialize(){
        cargarPedidos();
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

        cmbFormaPago.setItems(FXCollections.observableArrayList(formaPagoDao.listar()));

        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(findCodigo(c.getValue().getProductoId())));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(findNombre(c.getValue().getProductoId())));
        colCantidad.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCantidad())));
        colPrecio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrecioUnit().toPlainString()));
        colSubtotal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubtotal().toPlainString()));

        cmbPedido.getSelectionModel().selectedItemProperty().addListener((o,old,now)-> {
            if (now!=null) cargarDetalles(now);
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
        return prodService.listar().stream().filter(x->x.getId()==id).map(Producto::getCodigo).findFirst().orElse("?");
    }
    private String findNombre(long id){
        return prodService.listar().stream().filter(x->x.getId()==id).map(Producto::getNombre).findFirst().orElse("?");
    }

    @FXML private void refrescarPedidos(){ cargarPedidos(); }

    @FXML private void facturar(){
        try{
            Pedido p = cmbPedido.getSelectionModel().getSelectedItem();
            if (p==null){ warn("No hay pedido seleccionado"); return; }
            FormaPago fp = cmbFormaPago.getSelectionModel().getSelectedItem();
            if (fp==null){ warn("Selecciona forma de pago"); return; }
            String serie = txtSerie.getText()==null?"":txtSerie.getText().trim();
            String numero = txtNumero.getText()==null?"":txtNumero.getText().trim();
            if (serie.isEmpty() || numero.isEmpty()){ warn("Serie y número son requeridos"); return; }

            long facturaId = facturaService.facturar(
                    p.getId(), Session.get().getId(), serie, numero, fp.getId()
            );
            info("Factura creada","Factura #" + facturaId + " generada y stock actualizado.");
            cargarPedidos();
            txtSerie.clear(); txtNumero.clear(); cmbFormaPago.getSelectionModel().clearSelection();
        }catch(Exception ex){
            error("No se pudo facturar", ex.getMessage());
        }
    }

    private void info(String h, String m){ alert(Alert.AlertType.INFORMATION,h,m); }
    private void warn(String m){ alert(Alert.AlertType.WARNING,"Atención",m); }
    private void error(String h, String m){ alert(Alert.AlertType.ERROR,h,m); }
    private void alert(Alert.AlertType t, String h, String m){ var a=new Alert(t); a.setHeaderText(h); a.setContentText(m); a.setTitle("Facturar"); a.showAndWait(); }
}
