-- =========================================================
-- PROYECTO: Ferretería - ARMAZÓN (estructura completa)
-- MySQL 8/9 (InnoDB, utf8mb4). Fechas de movimientos como DATE.
-- =========================================================

DROP DATABASE IF EXISTS ferreteria;
CREATE DATABASE ferreteria
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE ferreteria;

SET sql_safe_updates = 0;

-- ============ 1) MAESTROS ============

DROP TABLE IF EXISTS usuarios;
CREATE TABLE usuarios (
  id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre         VARCHAR(100) NOT NULL,
  usuario        VARCHAR(40)  NOT NULL UNIQUE,
  pass_hash      VARCHAR(255) NOT NULL,
  rol            ENUM('ADMIN','BODEGA','VENTAS','CAJA') NOT NULL,
  estado         TINYINT(1) NOT NULL DEFAULT 1,
  creado_en      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

DROP TABLE IF EXISTS proveedores;
CREATE TABLE proveedores (
  id        BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre    VARCHAR(120) NOT NULL,
  nit       VARCHAR(20),
  telefono  VARCHAR(25),
  direccion VARCHAR(200),
  email     VARCHAR(120),
  creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

DROP TABLE IF EXISTS productos;
CREATE TABLE productos (
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  codigo        VARCHAR(30)  NOT NULL,
  nombre        VARCHAR(120) NOT NULL,
  unidad        VARCHAR(20),
  precio        DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  stock         INT UNSIGNED NOT NULL DEFAULT 0,
  proveedor_id  BIGINT UNSIGNED,
  activo        TINYINT(1) NOT NULL DEFAULT 1,
  creado_en     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_productos_codigo (codigo),
  KEY idx_productos_nombre (nombre),
  KEY idx_productos_proveedor (proveedor_id),
  CONSTRAINT fk_productos_proveedor
    FOREIGN KEY (proveedor_id) REFERENCES proveedores(id)
    ON UPDATE RESTRICT ON DELETE SET NULL
) ENGINE=InnoDB;

DROP TABLE IF EXISTS clientes;
CREATE TABLE clientes (
  id        BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre    VARCHAR(120) NOT NULL,
  nit       VARCHAR(20),
  telefono  VARCHAR(25),
  direccion VARCHAR(200),
  email     VARCHAR(120),
  creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ======= 2) MOVIMIENTOS / VENTAS =======

DROP TABLE IF EXISTS ingreso;
CREATE TABLE ingreso (
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  proveedor_id  BIGINT UNSIGNED NOT NULL,
  bodeguero_id  BIGINT UNSIGNED,
  fecha         DATE NOT NULL,
  no_doc        VARCHAR(40),
  total         DECIMAL(14,2) NOT NULL DEFAULT 0.00,
  creado_en     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_ingreso_fecha (fecha),
  KEY idx_ingreso_proveedor (proveedor_id),
  CONSTRAINT fk_ingreso_proveedor
    FOREIGN KEY (proveedor_id) REFERENCES proveedores(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_ingreso_bodeguero
    FOREIGN KEY (bodeguero_id) REFERENCES usuarios(id)
    ON UPDATE RESTRICT ON DELETE SET NULL
) ENGINE=InnoDB;

DROP TABLE IF EXISTS ingreso_det;
CREATE TABLE ingreso_det (
  id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  ingreso_id   BIGINT UNSIGNED NOT NULL,
  producto_id  BIGINT UNSIGNED NOT NULL,
  cantidad     INT UNSIGNED    NOT NULL,
  costo_unit   DECIMAL(12,2)   NOT NULL,
  subtotal     DECIMAL(12,2)   NOT NULL,
  UNIQUE KEY uq_ingreso_det (ingreso_id, producto_id),
  KEY idx_ingreso_det_product (producto_id),
  CONSTRAINT fk_ingreso_det_ingreso
    FOREIGN KEY (ingreso_id) REFERENCES ingreso(id)
    ON UPDATE RESTRICT ON DELETE CASCADE,
  CONSTRAINT fk_ingreso_det_producto
    FOREIGN KEY (producto_id) REFERENCES productos(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB;

DROP TABLE IF EXISTS pedido;
CREATE TABLE pedido (
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  cliente_id    BIGINT UNSIGNED NOT NULL,
  vendedor_id   BIGINT UNSIGNED NOT NULL,
  fecha         DATE NOT NULL,
  estado        ENUM('PENDIENTE','PAGADO','CANCELADO') NOT NULL DEFAULT 'PENDIENTE',
  observaciones VARCHAR(200),
  total         DECIMAL(14,2) NOT NULL DEFAULT 0.00,
  creado_en     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_pedido_fecha (fecha),
  KEY idx_pedido_cliente (cliente_id),
  KEY idx_pedido_estado (estado),
  CONSTRAINT fk_pedido_cliente
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_pedido_vendedor
    FOREIGN KEY (vendedor_id) REFERENCES usuarios(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB;

DROP TABLE IF EXISTS pedido_det;
CREATE TABLE pedido_det (
  id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  pedido_id    BIGINT UNSIGNED NOT NULL,
  producto_id  BIGINT UNSIGNED NOT NULL,
  cantidad     INT UNSIGNED    NOT NULL,
  precio_unit  DECIMAL(12,2)   NOT NULL,
  subtotal     DECIMAL(12,2)   NOT NULL,
  UNIQUE KEY uq_pedido_det (pedido_id, producto_id),
  KEY idx_pedido_det_producto (producto_id),
  CONSTRAINT fk_pedido_det_pedido
    FOREIGN KEY (pedido_id) REFERENCES pedido(id)
    ON UPDATE RESTRICT ON DELETE CASCADE,
  CONSTRAINT fk_pedido_det_producto
    FOREIGN KEY (producto_id) REFERENCES productos(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB;

DROP TABLE IF EXISTS factura;
CREATE TABLE factura (
  id        BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  pedido_id BIGINT UNSIGNED NOT NULL,
  cajero_id BIGINT UNSIGNED NOT NULL,
  fecha     DATE NOT NULL,
  serie     VARCHAR(10),
  numero    VARCHAR(20),
  total     DECIMAL(14,2) NOT NULL,
  creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_factura_pedido (pedido_id),
  UNIQUE KEY uq_factura_serie_numero (serie, numero),
  KEY idx_factura_fecha (fecha),
  CONSTRAINT fk_factura_pedido
    FOREIGN KEY (pedido_id) REFERENCES pedido(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_factura_cajero
    FOREIGN KEY (cajero_id) REFERENCES usuarios(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB;

DROP TABLE IF EXISTS forma_pago;
CREATE TABLE forma_pago (
  id     BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS pago;
CREATE TABLE pago (
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  factura_id    BIGINT UNSIGNED NOT NULL,
  forma_pago_id BIGINT UNSIGNED NOT NULL,
  monto         DECIMAL(14,2) NOT NULL,
  creado_en     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_pago_factura (factura_id),
  CONSTRAINT fk_pago_factura
    FOREIGN KEY (factura_id) REFERENCES factura(id)
    ON UPDATE RESTRICT ON DELETE CASCADE,
  CONSTRAINT fk_pago_forma
    FOREIGN KEY (forma_pago_id) REFERENCES forma_pago(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============ 3) CAJA ============

DROP TABLE IF EXISTS caja_mov;
DROP TABLE IF EXISTS caja_sesion;

CREATE TABLE IF NOT EXISTS caja_sesion (
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  usuario_id    BIGINT UNSIGNED NOT NULL,
  apertura      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  cierre        TIMESTAMP NULL,
  monto_inicio  DECIMAL(14,2) NOT NULL,
  monto_fin     DECIMAL(14,2) NULL,
  estado        ENUM('ABIERTA','CERRADA') NOT NULL DEFAULT 'ABIERTA',
  observaciones VARCHAR(200),
  KEY idx_caja_sesion_usuario (usuario_id),
  KEY idx_caja_sesion_estado (estado),
  CONSTRAINT fk_caja_sesion_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS caja_mov (
  id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  sesion_id      BIGINT UNSIGNED NOT NULL,
  creado_en      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  tipo           ENUM('VENTA','INGRESO','EGRESO') NOT NULL,
  descripcion    VARCHAR(200),
  monto          DECIMAL(14,2) NOT NULL,
  factura_id     BIGINT UNSIGNED NULL,
  forma_pago_id  BIGINT UNSIGNED NULL,
  pago_id        BIGINT UNSIGNED NULL,
  KEY idx_caja_mov_sesion (sesion_id),
  KEY idx_caja_mov_tipo (tipo),
  CONSTRAINT fk_caja_mov_sesion
    FOREIGN KEY (sesion_id) REFERENCES caja_sesion(id)
    ON UPDATE RESTRICT ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============ 4) VISTAS ============

DROP VIEW IF EXISTS v_stock;
CREATE VIEW v_stock AS
SELECT p.id, p.codigo, p.nombre, p.unidad, p.precio, p.stock
FROM productos p
WHERE p.activo = 1;

DROP VIEW IF EXISTS v_ventas_diarias;
CREATE VIEW v_ventas_diarias AS
SELECT f.fecha AS dia, SUM(f.total) AS total_dia
FROM factura f
GROUP BY f.fecha
ORDER BY dia;

CREATE OR REPLACE VIEW v_caja_resumen AS
SELECT s.id,
       s.usuario_id,
       s.apertura,
       s.cierre,
       s.monto_inicio,
       s.monto_fin,
       s.estado,
       SUM(CASE WHEN m.tipo IN ('VENTA','INGRESO') THEN m.monto ELSE 0 END) AS ingresos,
       SUM(CASE WHEN m.tipo = 'EGRESO' THEN m.monto ELSE 0 END)             AS egresos,
       s.monto_inicio
         + SUM(CASE WHEN m.tipo IN ('VENTA','INGRESO') THEN m.monto ELSE 0 END)
         + SUM(CASE WHEN m.tipo = 'EGRESO' THEN -m.monto ELSE 0 END)        AS saldo_estimado
FROM caja_sesion s
LEFT JOIN caja_mov m ON m.sesion_id = s.id
GROUP BY s.id;
