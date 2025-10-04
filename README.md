# Sistema de Facturación – Ferretería (Proyecto Programación II)

**Ruta A (Escritorio nativo):** Java 22 · JavaFX · MySQL · JDBC/HikariCP · JasperReports  
**IDE:** NetBeans · **BD:** MySQL Workbench · **Repo:** GitHub
---

## 📌 Alcance funcional (resumen)

- **Usuarios y roles:** ADMIN, BODEGA, VENTAS, CAJA
- **Productos y stock:** CRUD, precios, unidad, proveedor
- **Ingresos a almacén:** orden/factura proveedor, **stock ↑**
- **Ventas:** pedido (vendedor) → caja (pago) → **factura** y **stock ↓**
- **Formas de pago:** efectivo, tarjeta, cheque, otros
- **Reportes PDF:** existencias; ventas por semana/mes
- **Entregables:** Manual de Usuario, Manual Técnico (DER/DF), backup `.sql`, código fuente (ZIP)

---

## 🧱 Arquitectura y stack

- **Capas (MVC):**
  `controller` (UI mínima) → `service` (reglas y transacciones) → `dao` (JDBC) → `model` (POJOs)
- **UI:** JavaFX (tema claro, profesional; tablas densas, botones redondeados)
- **BD:** MySQL (índices por búsqueda, FK estrictas)
- **Conexiones:** JDBC + HikariCP (pool de conexiones)
- **Reportes:** JasperReports (JRXML + parámetros de fechas)
- **Opcional:** HTML/Bootstrap para pantallas estáticas incrustadas (ayuda/vistas previas)

---

## 🗂️ Estructura del proyecto (propuesta)
```plaintext
/ferreteria
├─ src/main/java/com/ferre
│ ├─ app/ # App JavaFX (launcher)
│ ├─ config/ # DataSource / settings
│ ├─ model/ # Entidades (POJOs)
│ ├─ dao/ # DAO interfaces + impl JDBC
│ ├─ service/ # Lógica/Transacciones
│ ├─ controller/ # Controladores JavaFX (UI)
│ └─ report/ # Carga/runner Jasper
├─ src/main/resources/
│ ├─ fxml/ # Vistas JavaFX
│ ├─ css/ # Estilos
│ ├─ reports/ # JRXML/JASPER
│ ├─ html/ # (opcional) ayuda / vistas estáticas
│ └─ i18n/ # (opcional) mensajes
└─ db/
├─ ferreteria.sql # Script de BD + seed
└─ backups/ # Respaldos
```

---

## 📊 Tablero de progreso (marcar estado por fase)


| Fase | Estado | Última actualización | Tag |
|------|--------|----------------------|-----|
| F0 — Fundaciones | ☐ **No iniciada** · ☑ En proceso · ☐ Completada | _aaaa-mm-dd_ | `v0-F0` |
| F1 — Modelo de datos y SQL | ☑ **No iniciada** · ☐ En proceso · ☐ Completada | _aaaa-mm-dd_ | `v0-F1` |
| F2 — Usuarios y Acceso | ☑ **No iniciada** · ☐ En proceso · ☐ Completada | _aaaa-mm-dd_ | `v0-F2` |
| F3 — Catálogos | ☑ **No iniciada** · ☐ En proceso · ☐ Completada | _aaaa-mm-dd_ | `v0-F3` |
| F4 — Ingreso a Almacén | ☑ **No iniciada** · ☐ En proceso · ☐ Completada | _aaaa-mm-dd_ | `v0-F4` |
| F5 — Ventas: Pedido | ☑ **No iniciada** · ☐ En proceso · ☐ Completada | _aaaa-mm-dd_ | `v0-F5` |
| F6 — Caja: Pago & Factura | ☑ **No iniciada** · ☐ En proceso · ☐ Completada | _aaaa-mm-dd_ | `v0-F6` |
| F7 — Reportes PDF | ☑ **No iniciada** · ☐ En proceso · ☐ Completada | _aaaa-mm-dd_ | `v0-F7` |
| F8 — Endurecimiento & Entrega | ☑ **No iniciada** · ☐ En proceso · ☐ Completada | _aaaa-mm-dd_ | `v1.0-final` |


---

## 🧭 Roadmap por Fases con listas de tareas


### ✅ F0 — Fundaciones del proyecto
**Estado:**  
- [ ] No iniciada  
- [x] En proceso  
- [ ] Completada

**Checklist de tareas**
- [ ] Crear proyecto **Maven JavaFX** en NetBeans (`com.ferre:ferreteria`)
- [ ] Configurar paquetes base: `app`, `config`, `model`, `dao`, `service`, `controller`, `report`
- [ ] Añadir dependencias: JavaFX, MySQL Connector/J, HikariCP, JasperReports
- [ ] Crear `src/main/resources/{fxml,css,reports,html,i18n}`
- [ ] `.gitignore` (NetBeans, `/target`, `/dist`, `.idea`, logs)
- [ ] **Prueba de conexión** a MySQL (sin lógica de negocio)
- [ ] Crear repo GitHub, ramas `main` y `dev`, protección de `main`
- [ ] README inicial del proyecto

**Criterios de aceptación (DoD)**
- Proyecto compila y ejecuta ventana base
- Conexión JDBC válida
- Estructura de carpetas creada
- Commit inicial + tag `v0-F0`

---

### 🔶 F1 — Modelo de datos y SQL
**Estado:**  
- [x] No iniciada  
- [ ] En proceso  
- [ ] Completada

**Checklist de tareas**
- [ ] Diseñar DER (tablas y relaciones)
- [ ] Script `db/ferreteria.sql` (CREATE + FK + índices + ENUM/valores)
- [ ] Datos semilla: usuarios/roles, proveedores, productos, clientes
- [ ] Probar integridad (no huérfanos, restricciones)
- [ ] (Opcional) Vistas SQL para reportes
- [ ] Backup en `db/backups/ferreteria_<fecha>.sql`

**Criterios de aceptación (DoD)**
- Script ejecuta sin errores en Workbench
- Validaciones y claves aplicadas
- Tag `v0-F1`

---

### 🔐 F2 — Usuarios y Acceso (ADMIN/Roles)
**Estado:**  
- [x] No iniciada  
- [ ] En proceso  
- [ ] Completada

**Checklist de tareas**
- [ ] Pantalla de **Login**
- [ ] Hash de contraseña + sesión
- [ ] CRUD de **Usuarios** (solo ADMIN)
- [ ] Menús/acciones visibles según **rol** (ADMIN, BODEGA, VENTAS, CAJA)
- [ ] Auditoría mínima (timestamps/usuario)

**Criterios de aceptación (DoD)**
- Autenticación/roles funcionando
- Restricción de pantallas por rol
- Tag `v0-F2`

---

### 📦 F3 — Catálogos (Productos, Proveedores, Clientes)
**Estado:**  
- [x] No iniciada  
- [ ] En proceso  
- [ ] Completada

**Checklist de tareas**
- [ ] CRUD **Productos** (código único, precio>0, stock≥0)
- [ ] CRUD **Proveedores**
- [ ] CRUD **Clientes**
- [ ] **Búsqueda/filtrado** en tablas
- [ ] Validaciones de formularios + confirmaciones

**Criterios de aceptación (DoD)**
- Altas/Ediciones/Eliminaciones correctas
- Tablas filtrables y usables
- Tag `v0-F3`

---

### 🏷️ F4 — Ingreso a Almacén (Bodega)
**Estado:**  
- [x] No iniciada  
- [ ] En proceso  
- [ ] Completada

**Checklist de tareas**
- [ ] Pantalla de **Ingresos** (encabezado: proveedor, fecha, no_doc)
- [ ] **Detalle** de ítems (producto, cantidad, costo, subtotal)
- [ ] **Transacción**: insertar encabezado + detalles
- [ ] **Stock ↑** por cada ítem confirmado
- [ ] Totales validados (cantidad>0, costo>0)

**Criterios de aceptación (DoD)**
- Commit/rollback correcto
- Stock incrementa exactamente
- Tag `v0-F4`


---

### 🧾 F5 — Ventas: Pedido (Vendedor)
**Estado:**  
- [x] No iniciada  
- [ ] En proceso  
- [ ] Completada

**Checklist de tareas**
- [ ] Crear **Pedido** (cliente + ítems)
- [ ] Cálculo de totales
- [ ] Estado inicial `PENDIENTE`
- [ ] Listado de pedidos **pendientes** para Caja
- [ ] Edición antes de pagar

**Criterios de aceptación (DoD)**
- Pedidos almacenados y visibles
- Totales correctos
- Tag `v0-F5`

---

### 💳 F6 — Caja: Pago & Factura (Despacho)
**Estado:**  
- [x] No iniciada  
- [ ] En proceso  
- [ ] Completada

**Checklist de tareas**
- [ ] Buscar **Pedido PENDIENTE** en Caja
- [ ] Verificar **stock firme** (justo antes de facturar)
- [ ] Registrar **Pago(s)** (efectivo/tarjeta/cheque/otro)
- [ ] Generar **Factura** (serie/número/total)
- [ ] **Transacción**: pagos + factura + **stock ↓**
- [ ] Cambiar estado de pedido a `PAGADO`

**Criterios de aceptación (DoD)**
- Lecturas y descuentos de stock exactos
- Persistencia de factura y pagos
- Tag `v0-F6`


---

### 📊 F7 — Reportes PDF (JasperReports)
**Estado:**  
- [x] No iniciada  
- [ ] En proceso  
- [ ] Completada

**Checklist de tareas**
- [ ] **Existencias** (filtros por texto/stock bajo)
- [ ] **Ventas** por periodo (semana/mes) con parámetros de fecha
- [ ] Compilación JRXML + export a **PDF**
- [ ] Totales/agrupaciones (por día / forma de pago)

**Criterios de aceptación (DoD)**
- Reportes se abren/guardan correctamente
- Filtros/fechas funcionando
- Tag `v0-F7`

---

### 🎯 F8 — Endurecimiento & Entrega
**Estado:**  
- [x] No iniciada  
- [ ] En proceso  
- [ ] Completada

**Checklist de tareas**
- [ ] **Manual de Usuario** (PDF con capturas)
- [ ] **Manual Técnico** (PDF con DER, DF, arquitectura)
- [ ] **Backup** actualizado (`db/backups/*.sql`)
- [ ] **ZIP** de entrega (código fuente + manuales + backup)
- [ ] **Guion de demo** (flujo completo por roles)

**Criterios de aceptación (DoD)**
- Documentación en `/docs`
- Backup y ZIP listos
- Tag `v1.0-final`

---

## 🌿 Flujo de trabajo Git

- **Ramas**
  - `main` → estable
  - `dev` → integración
  - `feature/<fase>` → desarrollo (p. ej., `feature/F3-catalogos`)
- **Tags por fase**
  - `v0-F0`, `v0-F1`, … `v0-F7`, `v1.0-final`
- **Commits (Conventional)**
  - `feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`
- **PRs**
  - Abrir PR de `feature/*` → `dev`
  - Al cerrar fase: merge `dev` → `main` + crear **tag**

---

## 🧪 Calidad y pruebas

- **Unitarias (service):** totales, verificación de stock, transición de estados
- **Integración (DAO/DB):** transacciones (commit/rollback)
- **UI (manual):** flujos por rol, validaciones y mensajes claros
- **Datos límite:** stock exacto, pagos exactos, pedidos editados antes de caja

---

## ⚙️ Entorno local (resumen)

- **Requisitos:** JDK 22, NetBeans 22, MySQL 8.x, Workbench
- **Base de datos:** crear schema `ferreteria`; ejecutar `db/ferreteria.sql`
- **Credenciales:** configurar en clase `config` o variables de entorno
- **Ejecución:** abrir en NetBeans → Run (JavaFX)

---

## 🖼️ Guía de demo rápida (cuando el sistema esté completo)

1. Login como **ADMIN** → crear usuarios por rol  
2. **BODEGA** → registrar **ingreso** (stock ↑)  
3. **VENTAS** → crear **pedido** (PENDIENTE)  
4. **CAJA** → cobrar, emitir **factura** (stock ↓)  
5. **REPORTES** → existencias y ventas por período (PDF)

---
