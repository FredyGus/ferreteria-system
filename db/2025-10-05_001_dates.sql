-- ===== MIGRACIÓN A DATE PARA FECHAS DE NEGOCIO =====
-- Ingreso.fecha
ALTER TABLE ingreso
  ADD COLUMN fecha_d DATE NOT NULL AFTER id;

UPDATE ingreso
   SET fecha_d = DATE(fecha);

ALTER TABLE ingreso
  DROP COLUMN fecha,
  CHANGE COLUMN fecha_d fecha DATE NOT NULL;

-- Factura.fecha
ALTER TABLE factura
  ADD COLUMN fecha_d DATE NOT NULL AFTER id;

UPDATE factura
   SET fecha_d = DATE(fecha);

ALTER TABLE factura
  DROP COLUMN fecha,
  CHANGE COLUMN fecha_d fecha DATE NOT NULL;

-- (Opcional) Índices por fecha si consultas mucho por rango
-- CREATE INDEX idx_ingreso_fecha ON ingreso(fecha);
-- CREATE INDEX idx_factura_fecha ON factura(fecha);
