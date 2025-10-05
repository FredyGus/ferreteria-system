-- =========================================================
-- PROYECTO: Ferretería - Modelo de datos (F1’ limpio)
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
  fecha         DATE NOT NULL,              -- <== DATE (no arrastre de TZ)
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
  fecha         DATE NOT NULL,              -- <== DATE
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
  fecha     DATE NOT NULL,                 -- <== DATE
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

-- ============ 3) VISTAS ============

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

-- ============ 4) SEMILLA ============

INSERT INTO forma_pago (nombre) VALUES ('Efectivo'), ('Tarjeta'), ('Cheque'), ('Otro')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

INSERT INTO usuarios (nombre, usuario, pass_hash, rol, estado) VALUES
  ('Administrador', 'admin',  SHA2('admin',256),  'ADMIN',  1),
  ('Bodeguero Demo','bodega', SHA2('bodega',256), 'BODEGA', 1),
  ('Vendedor Demo', 'ventas', SHA2('ventas',256), 'VENTAS', 1),
  ('Cajero Demo',   'caja',   SHA2('caja',256),   'CAJA',   1)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

INSERT INTO proveedores (nombre, nit, telefono, direccion) VALUES
  ('Acero S.A.',   'CF', '5551-0001', 'Zona 1'),
  ('Pinturas XYZ', 'CF', '5551-0002', 'Zona 2');

INSERT INTO productos (codigo, nombre, unidad, precio, proveedor_id, activo) VALUES
  ('CLV-2"',  'Clavo 2 pulgadas', 'pz',  0.50, 1, 1),
  ('TAL-500', 'Taladro 500W',      'pz', 550.00, 1, 1),
  ('PIN-ROJA','Pintura Roja 1L',   'lt',  70.00, 2, 1);

INSERT INTO clientes (nombre, nit, telefono, direccion) VALUES
  ('Consumidor Final', 'CF', NULL, NULL),
  ('Cliente Demo',     '1234567-8', '5555-1234', 'Zona 3');
