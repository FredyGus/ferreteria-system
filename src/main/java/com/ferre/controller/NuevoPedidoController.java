package com.ferre.controller;

import com.ferre.config.Session;
import com.ferre.model.Cliente;
import com.ferre.model.Pedido;
import com.ferre.model.PedidoDet;
import com.ferre.model.Producto;
import com.ferre.service.ClienteService;
import com.ferre.service.PedidoService;
import com.ferre.service.ProductoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NuevoPedidoController {

    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private Label lblVendedor;
    @FXML private TextField txtObs;

    @FXML private ComboBox<Producto> cmbProducto;
    @FXML private TextField txtCantidad, txtPrecio;

    @FXML private TableView<PedidoDet> tbl;
    @FXML private TableColumn<PedidoDet,String> colCodigo, colNombre, colCantidad, colPrecio, colSubtotal;
    @FXML private Label lblTotal;

    private final ClienteService cliService = new ClienteService();
    private final ProductoService prodService = new ProductoService();
    private final PedidoService pedidoService = new PedidoService();

    private final List<PedidoDet> detalles = new ArrayList<>();

    @FXML
    public void initialize(){
        cmbCliente.setItems(FXCollections.observableArrayList(cliService.listar()));
        cmbProducto.setItems(FXCollections.observableArrayList(prodService.listar()));
        cmbProducto.setCellFactory(cb -> new ListCell<>() {
            @Override protected void updateItem(Producto p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p==null ? null : p.getCodigo() + " — " + p.getNombre());
            }
        });
        cmbProducto.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Producto p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p==null ? null : p.getCodigo() + " — " + p.getNombre());
            }
        });

        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(findCodigo(c.getValue().getProductoId())));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(findNombre(c.getValue().getProductoId())));
        colCantidad.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCantidad())));
        colPrecio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrecioUnit().toPlainString()));
        colSubtotal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubtotal().toPlainString()));

        lblVendedor.setText(Session.get().getUsuario());
        refrescarTabla(); recalcularTotal();
    }

    private String findCodigo(long id){
        return cmbProducto.getItems().stream().filter(p->p.getId()==id).map(Producto::getCodigo).findFirst().orElse("?");
    }
    private String findNombre(long id){
        return cmbProducto.getItems().stream().filter(p->p.getId()==id).map(Producto::getNombre).findFirst().orElse("?");
    }

    @FXML private void agregarItem(){
        try{
            var p = cmbProducto.getSelectionModel().getSelectedItem();
            if (p==null){ warn("Selecciona un producto"); return; }
            int cant = Integer.parseInt(txtCantidad.getText().trim());
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            if (cant <= 0) { warn("Cantidad debe ser > 0"); return; }
            if (precio.compareTo(BigDecimal.ZERO) <= 0) { warn("Precio debe ser > 0"); return; }

            // Si el producto ya está, suma cantidades y recalcula
            for (PedidoDet d : detalles){
                if (d.getProductoId() == p.getId()){
                    d.setCantidad(d.getCantidad() + cant);
                    d.setPrecioUnit(precio);
                    d.setSubtotal(precio.multiply(BigDecimal.valueOf(d.getCantidad())));
                    refrescarTabla(); recalcularTotal(); limpiarLinea();
                    return;
                }
            }

            PedidoDet d = new PedidoDet();
            d.setProductoId(p.getId());
            d.setCantidad(cant);
            d.setPrecioUnit(precio);
            d.setSubtotal(precio.multiply(BigDecimal.valueOf(cant)));
            detalles.add(d);

            refrescarTabla(); recalcularTotal(); limpiarLinea();
        }catch (NumberFormatException nfe){
            warn("Cantidad/Precio inválidos");
        }
    }

    @FXML private void eliminarItem(){
        var sel = tbl.getSelectionModel().getSelectedItem();
        if (sel==null){ warn("Selecciona una fila"); return; }
        detalles.remove(sel); refrescarTabla(); recalcularTotal();
    }

    @FXML private void nuevo(){
        cmbCliente.getSelectionModel().clearSelection();
        txtObs.clear();
        detalles.clear();
        refrescarTabla(); recalcularTotal(); limpiarLinea();
    }

    @FXML private void guardar(){
        try{
            var cli = cmbCliente.getSelectionModel().getSelectedItem();
            if (cli==null){ warn("Selecciona cliente"); return; }
            if (detalles.isEmpty()){ warn("Agrega ítems"); return; }

            Pedido cab = new Pedido();
            cab.setClienteId(cli.getId());
            cab.setVendedorId(Session.get().getId());
            cab.setFecha(LocalDateTime.now());
            cab.setObservaciones(txtObs.getText());

            long id = pedidoService.crearPedido(cab, detalles);
            info("Pedido creado","No. " + id + " guardado (estado PENDIENTE).");
            nuevo();
        }catch(Exception ex){ error("Error", ex.getMessage()); }
    }

    private void limpiarLinea(){ cmbProducto.getSelectionModel().clearSelection(); txtCantidad.clear(); txtPrecio.clear(); }
    private void refrescarTabla(){ tbl.setItems(FXCollections.observableArrayList(detalles)); }
    private void recalcularTotal(){
        java.math.BigDecimal t = detalles.stream().map(PedidoDet::getSubtotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        lblTotal.setText("Total: " + t.toPlainString());
    }

    private void info(String h, String m){ alert(Alert.AlertType.INFORMATION,h,m); }
    private void warn(String m){ alert(Alert.AlertType.WARNING,"Atención",m); }
    private void error(String h, String m){ alert(Alert.AlertType.ERROR,h,m); }
    private void alert(Alert.AlertType t, String h, String m){ var a=new Alert(t); a.setHeaderText(h); a.setContentText(m); a.setTitle("Nuevo Pedido"); a.showAndWait(); }
}
