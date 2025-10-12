-- =========================
-- F7 - Tablas de Caja
-- =========================

-- Sesión de caja (apertura / cierre)
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

-- Movimientos de caja
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

-- Resumen por sesión (vista)
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

