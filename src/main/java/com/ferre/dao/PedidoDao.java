package com.ferre.dao;

import com.ferre.model.Pedido;
import com.ferre.model.PedidoDet;

import java.sql.Connection;
import java.util.List;

public interface PedidoDao {

    long crearCabecera(Connection cn, Pedido p);

    void crearDetalle(Connection cn, PedidoDet d);

    List<Pedido> listarPendientes();

    List<PedidoDet> obtenerDetalles(long pedidoId);
}
