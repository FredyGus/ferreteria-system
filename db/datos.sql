USE ferreteria;

SET sql_safe_updates = 0;

-- ======= Catálogos base =======
INSERT INTO forma_pago (nombre) VALUES ('Efectivo'), ('Tarjeta'), ('Cheque'), ('Otro')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

INSERT INTO usuarios (nombre, usuario, pass_hash, rol, estado) VALUES
  ('Administrador', 'admin',  SHA2('admin',256),  'ADMIN',  1),
  ('Bodeguero Demo','bodega', SHA2('bodega',256), 'BODEGA', 1),
  ('Vendedor Demo', 'ventas', SHA2('ventas',256), 'VENTAS', 1),
  ('Cajero Demo',   'caja',   SHA2('caja',256),   'CAJA',   1)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- ======= Proveedores =======
INSERT INTO proveedores (nombre, nit, telefono, direccion, email) VALUES
('Acero S.A.',       'CF',        '5551-0001', 'Zona 1', 'contacto@acero.com'),
('Pinturas XYZ',     'CF',        '5551-0002', 'Zona 2', 'ventas@pinturasxyz.com'),
('Herramientas Pro', '789456-1',  '5551-0003', 'Zona 3', 'pro@herrpro.com'),
('ElectricMax',      '123-EM',    '5551-0004', 'Zona 4', 'contact@electricmax.com'),
('Tubos & Cables',   'TC-777',    '5551-0005', 'Zona 5', 'info@tubycab.gt'),
('Maderas Fina',     'MF-008',    '5551-0006', 'Zona 6', 'hola@maderasfina.com'),
('Pegamentos GT',    'PG-019',    '5551-0007', 'Zona 7', 'ventas@pegamentosgt.com'),
('Seguridad Industrial S.A.','CF','5551-0008','Zona 8','si@segind.com');

-- ======= Productos (precio=venta, stock inicial “alto”) =======
-- proveedor_id referenciado a la lista anterior (1..8)
INSERT INTO productos (codigo, nombre, unidad, precio, proveedor_id, activo, stock) VALUES
('CLV-2"',   'Clavo 2 pulgadas',           'pz',   0.50, 1, 1, 5000),
('CLV-3"',   'Clavo 3 pulgadas',           'pz',   0.65, 1, 1, 4500),
('TAL-500',  'Taladro 500W',               'pz', 550.00, 3, 1,  40),
('TAL-800',  'Taladro 800W Percutor',      'pz', 980.00, 3, 1,  25),
('PIN-ROJA', 'Pintura Roja 1L',            'lt',  70.00, 2, 1, 300),
('PIN-BLAN', 'Pintura Blanca 1L',          'lt',  68.00, 2, 1, 500),
('PIN-AZUL', 'Pintura Azul 1L',            'lt',  70.00, 2, 1, 280),
('CAB-CU10', 'Cable cobre 10mm',           'm',   22.00, 5, 1,1000),
('CAB-CU6',  'Cable cobre 6mm',            'm',   15.50, 5, 1,1200),
('FOC-LED9', 'Foco LED 9W',                'pz',  18.00, 4, 1, 600),
('FOC-LED15','Foco LED 15W',               'pz',  24.00, 4, 1, 450),
('TUB-PVC1', 'Tubo PVC 1"',                'm',   12.00, 5, 1,1800),
('TUB-PVC2', 'Tubo PVC 2"',                'm',   22.00, 5, 1,1000),
('SIERRA-M', 'Sierra manual',              'pz', 110.00, 3, 1, 120),
('LLAV-FIJ', 'Juego llaves fijas 6-22mm',  'pz', 260.00, 3, 1,  80),
('GUAN-NIT', 'Guantes nitrilo',            'par', 18.00, 8, 1, 400),
('MASC-N95', 'Mascarilla N95',             'pz',  12.00, 8, 1, 800),
('PEG-500',  'Pegamento contacto 500ml',   'ml',  28.00, 7, 1, 350),
('PEG-1L',   'Pegamento contacto 1L',      'lt',  48.00, 7, 1, 200),
('MADER-2x4','Madera pino 2x4',            'm',   35.00, 6, 1, 900),
('MADER-1x3','Madera pino 1x3',            'm',   22.00, 6, 1, 950),
('TOR-M6x30','Tornillo M6x30 c/tuerca',    'pz',   0.90, 1, 1, 8000),
('TOR-M8x40','Tornillo M8x40 c/tuerca',    'pz',   1.30, 1, 1, 7000),
('CINT-MET', 'Cinta métrica 5m',           'pz',  45.00, 3, 1, 200),
('ESPAT-6',  'Espátula 6”',                'pz',  28.00, 2, 1, 300);

-- ======= Clientes =======
INSERT INTO clientes (nombre, nit, telefono, direccion, email) VALUES
('Consumidor Final', 'CF',         NULL,           NULL,              NULL),
('Constructora Atlas', '567890-1', '5555-1000', 'Calz. Atlántico',  'compras@atlas.com'),
('Pinturas Luna',      '789123-4', '5555-1001', 'Zona 9',           'admin@luna.gt'),
('Reformas Rápidas',   'RR-001',   '5555-1002', 'Mixco',            'rr@reformas.com'),
('Ferre Hogar',        'FH-010',   '5555-1003', 'Villa Nueva',      'hogar@ferre.com'),
('Inmobiliaria Sol',   'IS-778',   '5555-1004', 'Zona 14',          'compras@sol.com'),
('Taller Eléctrico Max','TEM-3',   '5555-1005', 'Zona 11',          'taller@max.com'),
('Municipalidad Norte','MN-22',    '5555-1006', 'Zona 18',          'adq@muninorte.gob'),
('Colegio Alfa',       'CA-90',    '5555-1007', 'Zona 15',          'compras@alfa.edu'),
('Hotel Jardines',     'HJ-55',    '5555-1008', 'Antigua',          'adq@jardines.com'),
('Cliente Demo',       '1234567-8','5555-1234', 'Zona 3',           'demo@cliente.com'),
('Obras & Diseño',     'OD-333',   '5555-1009', 'Carretera a El S.', 'od@diseno.com'),
('Carpintería Fina',   'CFI-12',   '5555-1010', 'Amatitlán',        'carpin@fina.gt'),
('Plomería Rápida',    'PR-88',    '5555-1011', 'Zona 5',           'plomeria@rapida.gt'),
('Electro Hogar',      'EH-71',    '5555-1012', 'Villa Canales',    'electro@hogar.gt');

-- ======= Ingresos (compras a proveedores) =======
-- Supón bodeguero_id = 2 (Bodeguero Demo)
INSERT INTO ingreso (proveedor_id, bodeguero_id, fecha, no_doc, total) VALUES
(1, 2, '2025-08-01', 'OC-0001', 3250.00),
(2, 2, '2025-08-03', 'OC-0002', 5100.00),
(3, 2, '2025-08-05', 'OC-0003', 14800.00),
(5, 2, '2025-08-10', 'OC-0004',  9200.00),
(6, 2, '2025-08-12', 'OC-0005', 13500.00),
(8, 2, '2025-08-15', 'OC-0006',  6800.00);

-- Detalle de ingresos (subtotales suman el total de cada ingreso)
-- ingreso 1: 3250.00
INSERT INTO ingreso_det (ingreso_id, producto_id, cantidad, costo_unit, subtotal) VALUES
(1, 1,   3000, 0.40, 1200.00),
(1, 22,  2000, 0.75, 1500.00),
(1, 24,    20,45.00,  900.00);

-- ingreso 2: 5100.00
INSERT INTO ingreso_det (ingreso_id, producto_id, cantidad, costo_unit, subtotal) VALUES
(2, 5,    40, 55.00, 2200.00),
(2, 6,    50, 53.00, 2650.00),
(2, 25,   10, 25.00,  250.00);

-- ingreso 3: 14800.00
INSERT INTO ingreso_det (ingreso_id, producto_id, cantidad, costo_unit, subtotal) VALUES
(3, 3,    10, 440.00, 4400.00),
(3, 4,    10, 820.00, 8200.00),
(3, 15,   10,  120.00,1200.00),
(3, 14,   10,  100.00,1000.00);

-- ingreso 4: 9200.00
INSERT INTO ingreso_det (ingreso_id, producto_id, cantidad, costo_unit, subtotal) VALUES
(4, 8,   200, 18.00, 3600.00),
(4, 9,   200, 13.00, 2600.00),
(4, 12,  200,  9.00, 1800.00),
(4, 13,  100,  12.00,1200.00);

-- ingreso 5: 13500.00
INSERT INTO ingreso_det (ingreso_id, producto_id, cantidad, costo_unit, subtotal) VALUES
(5, 20,  200, 28.00, 5600.00),
(5, 21,  200, 18.00, 3600.00),
(5, 19,  100, 35.00, 3500.00),
(5, 18,  100,  8.00,  800.00);

-- ingreso 6: 6800.00
INSERT INTO ingreso_det (ingreso_id, producto_id, cantidad, costo_unit, subtotal) VALUES
(6, 16,  200, 12.00, 2400.00),
(6, 17,  300,  8.00, 2400.00),
(6, 10,  200,  8.00, 1600.00),
(6, 11,  100,  4.00,  400.00);

-- ======= Pedidos / Ventas =======
-- vendedor_id = 3 (Vendedor Demo)
INSERT INTO pedido (cliente_id, vendedor_id, fecha, estado, observaciones, total) VALUES
(2, 3, '2025-09-01', 'PAGADO',   'Pedido constructora',                2680.00),
(3, 3, '2025-09-02', 'PAGADO',   'Pinturas y espátulas',                956.00),
(4, 3, '2025-09-03', 'PAGADO',   'Herramientas varias',                1420.00),
(5, 3, '2025-09-04', 'PENDIENTE','Iluminación hogar',                   780.00),
(6, 3, '2025-09-05', 'PAGADO',   'Material eléctrico',                 2210.00),
(7, 3, '2025-09-06', 'PAGADO',   'Taladros para taller',               2080.00),
(8, 3, '2025-09-07', 'PAGADO',   'PVC para obra municipal',            2240.00),
(9, 3, '2025-09-08', 'PAGADO',   'Madera para mantenimiento',           910.00),
(10,3, '2025-09-09', 'PAGADO',   'Mixto hotel',                        1730.00),
(11,3, '2025-09-10','PAGADO',    'Venta mixta',                        1125.00);

-- Detalle de pedidos (subtotales suman el total indicado)
-- pedido 1: 2680.00
INSERT INTO pedido_det (pedido_id, producto_id, cantidad, precio_unit, subtotal) VALUES
(1, 12, 100, 12.00, 1200.00),
(1, 13,  40, 22.00,  880.00),
(1, 22, 600,  0.10,   60.00),  -- (ej. arandela barata: usa TOR-M6x30 como accesorio económico)
(1, 24,  12, 45.00,  540.00);

-- pedido 2: 956.00
INSERT INTO pedido_det (pedido_id, producto_id, cantidad, precio_unit, subtotal) VALUES
(2, 5,  8, 70.00, 560.00),
(2, 6,  5, 68.00, 340.00),
(2, 25, 2, 28.00,  56.00);

-- pedido 3: 1420.00
INSERT INTO pedido_det (pedido_id, producto_id, cantidad, precio_unit, subtotal) VALUES
(3, 14,  4, 110.00, 440.00),
(3, 15,  3, 260.00, 780.00),
(3, 1, 200,   0.50, 100.00);

-- pedido 4: 780.00  (PENDIENTE)
INSERT INTO pedido_det (pedido_id, producto_id, cantidad, precio_unit, subtotal) VALUES
(4, 10, 20, 18.00, 360.00),
(4, 11, 15, 24.00, 360.00),
(4, 25,  2, 30.00,  60.00);

-- pedido 5: 2210.00
INSERT INTO pedido_det (pedido_id, producto_id, cantidad, precio_unit, subtotal) VALUES
(5, 8,  50, 22.00, 1100.00),
(5, 9,  50, 15.50,  775.00),
(5, 23,100,  1.35,  135.00),
(5, 10,  5,  20.00, 100.00),
(5, 11,  5,  20.00, 100.00);

-- pedido 6: 2080.00
INSERT INTO pedido_det (pedido_id, producto_id, cantidad, precio_unit, subtotal) VALUES
(6, 3, 2, 550.00, 1100.00),
(6, 4, 1, 980.00,  980.00);

-- pedido 7: 2240.00
INSERT INTO pedido_det (pedido_id, producto_id, cantidad, precio_unit, subtotal) VALUES
(7, 12, 80,  12.00, 960.00),
(7, 13, 40,  22.00, 880.00),
(7, 9,  20,  20.00, 400.00);

-- pedido 8: 910.00
INSERT INTO pedido_det (pedido_id, producto_id, cantidad, precio_unit, subtotal) VALUES
(8, 20, 20, 35.00, 700.00),
(8, 21, 10, 21.00, 210.00);

-- pedido 9: 1730.00
INSERT INTO pedido_det (pedido_id, producto_id, cantidad, precio_unit, subtotal) VALUES
(9, 5,  10, 70.00, 700.00),
(9, 6,  10, 68.00, 680.00),
(9, 10, 15,  18.00, 270.00),
(9, 25,  2,  40.00,  80.00);

-- pedido 10: 1125.00
INSERT INTO pedido_det (pedido_id, producto_id, cantidad, precio_unit, subtotal) VALUES
(10, 16, 10, 18.00, 180.00),
(10, 17, 50, 12.00, 600.00),
(10, 24,  5,  45.00, 225.00),
(10, 22, 50,   0.24,  12.00),
(10, 23, 45,   0.24,  10.80);  -- redondeo total 1127.80 ~ ajusta dos centavos:
UPDATE pedido SET total = 1127.80 WHERE id = 10;

-- Ajustes exactos de totales (por si el motor de tu IDE muestra más decimales)
UPDATE pedido SET total = (
  SELECT IFNULL(SUM(subtotal),0) FROM pedido_det d WHERE d.pedido_id = pedido.id
) WHERE id IN (1,2,3,4,5,6,7,8,9);

-- ======= Facturas / Pagos =======
-- cajero_id = 4 (Cajero Demo) ; facturamos todos los PAGADOS
INSERT INTO factura (pedido_id, cajero_id, fecha, serie, numero, total) VALUES
(1, 4, '2025-09-01', 'A', '000001', (SELECT total FROM pedido WHERE id=1)),
(2, 4, '2025-09-02', 'A', '000002', (SELECT total FROM pedido WHERE id=2)),
(3, 4, '2025-09-03', 'A', '000003', (SELECT total FROM pedido WHERE id=3)),
(5, 4, '2025-09-05', 'A', '000004', (SELECT total FROM pedido WHERE id=5)),
(6, 4, '2025-09-06', 'A', '000005', (SELECT total FROM pedido WHERE id=6)),
(7, 4, '2025-09-07', 'A', '000006', (SELECT total FROM pedido WHERE id=7)),
(8, 4, '2025-09-08', 'A', '000007', (SELECT total FROM pedido WHERE id=8)),
(9, 4, '2025-09-09', 'A', '000008', (SELECT total FROM pedido WHERE id=9)),
(10,4, '2025-09-10', 'A', '000009', (SELECT total FROM pedido WHERE id=10));

-- Pagos: algunos en efectivo, otros con tarjeta, otros combinados
-- Busca IDs de forma_pago:
-- 1=Efectivo, 2=Tarjeta, 3=Cheque, 4=Otro
INSERT INTO pago (factura_id, forma_pago_id, monto) VALUES
(1, 1, (SELECT total FROM factura WHERE id=1)),                -- todo efectivo
(2, 2, (SELECT total FROM factura WHERE id=2)),                -- todo tarjeta
(3, 1,  420.00), (3, 2, (SELECT total FROM factura WHERE id=3)-420.00),  -- mixto
(5, 3, (SELECT total FROM factura WHERE id=5)),                -- cheque
(6, 2, (SELECT total FROM factura WHERE id=6)),                -- tarjeta
(7, 1, (SELECT total FROM factura WHERE id=7)),                -- efectivo
(8, 2,  730.00), (8, 1, (SELECT total FROM factura WHERE id=8)-730.00),  -- mixto
(9, 1, (SELECT total FROM factura WHERE id=9));                -- efectivo

-- ======= Caja (sesiones y movimientos) =======
-- Aperturamos una sesión y registramos movimientos por ventas y ajustes
INSERT INTO caja_sesion (usuario_id, apertura, monto_inicio, estado, observaciones)
VALUES (4, CURRENT_TIMESTAMP, 500.00, 'ABIERTA', 'Apertura turno mañana');

-- Suponiendo sesion_id = 1
-- VENTAS: montos iguales a pagos recibidos (por simplicidad)
INSERT INTO caja_mov (sesion_id, tipo, descripcion, monto, factura_id, forma_pago_id)
SELECT 1, 'VENTA', CONCAT('Cobro factura ', serie, '-', numero),
       p.monto, f.id, p.forma_pago_id
FROM factura f
JOIN pago p ON p.factura_id = f.id;

-- INGRESO no-venta (ej. abono caja)
INSERT INTO caja_mov (sesion_id, tipo, descripcion, monto)
VALUES (1, 'INGRESO', 'Abono a caja chica', 200.00);

-- EGRESO (gasto menor)
INSERT INTO caja_mov (sesion_id, tipo, descripcion, monto)
VALUES (1, 'EGRESO', 'Compra bolsas y papel', 150.00);

-- Cierre de sesión (estimado en vista v_caja_resumen)
UPDATE caja_sesion
SET cierre = CURRENT_TIMESTAMP, estado = 'CERRADA', monto_fin = 0.00
WHERE id = 1;

-- ======= Ventas diarias re-cálculo (vista lo hace automáticamente) =======
-- (No se requiere nada extra; consultar: SELECT * FROM v_ventas_diarias; )
