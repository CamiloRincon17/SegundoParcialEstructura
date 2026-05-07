# 🏭 Sistema de Gestión de Alertas para Sensores Industriales

> Sistema basado en **Lista Doblemente Enlazada** para simular, detectar y gestionar alertas de sensores industriales en tiempo real.

---

## 📋 Tabla de Contenidos

1. [🎯 Objetivo del Sistema](#-objetivo-del-sistema)
2. [🧩 Arquitectura General](#-arquitectura-general)
3. [🧱 Estructura del Nodo](#-estructura-del-nodo)
4. [🔗 Lista Doblemente Enlazada](#-lista-doblemente-enlazada)
5. [⚙️ Función de Simulación](#️-función-de-simulación-sensores)
6. [🧪 Validación de Datos](#-validación-de-datos)
7. [➕ Operaciones CRUD](#-operaciones-crud)
8. [📊 Reportes](#-reportes)
9. [💾 Persistencia](#-persistencia)
10. [♻️ Buffer Circular](#️-buffer-circular)
11. [🖥️ Interfaz](#️-interfaz-botón)
12. [🔄 Flujo Completo](#-flujo-completo)

---

## 🎯 Objetivo del Sistema

Diseñar un sistema capaz de:

- ✅ Simular lecturas de sensores industriales.
- ✅ Detectar anomalías.
- ✅ Gestionar alertas en una **lista doblemente enlazada**.
- ✅ Permitir operaciones **CRUD** (Crear, Leer, Actualizar, Eliminar).
- ✅ Generar reportes inteligentes.
- ✅ Mantener persistencia en archivo.
- ✅ Controlar memoria mediante **buffer circular**.

---

## 🧩 Arquitectura General

### 🔹 Módulos Principales

| Módulo | Descripción |
|--------|-------------|
| 📡 Simulación de sensores | Genera datos aleatorios periódicamente |
| 🔗 Gestor de lista doblemente enlazada | Administra nodos de alertas |
| ✔️ Motor de validación | Valida físicamente y asigna prioridades |
| 📊 Sistema de reportes | Genera estadísticas y análisis |
| 💾 Persistencia (archivo) | Guarda y carga datos en JSON |
| ♻️ Control de memoria (buffer circular) | Limita el tamaño de la lista |
| 🖥️ Interfaz (botón de simulación) | Punto de entrada para el usuario |

---

## 🧱 Estructura del Nodo

Cada nodo representa una **alerta**:

```
Alerta
│
├── id            : int
├── sensorID      : String
├── tipo_sensor   : String
├── valor         : float
├── prioridad     : int
├── timestamp     : long
├── estado        : String  → "Pendiente" | "Revisada"
├── etiqueta      : String  (opcional)
├── siguiente     : Nodo
└── anterior      : Nodo
```

---

## 🔗 Lista Doblemente Enlazada

### 🔹 Estructura

```
ListaAlertas
│
├── cabeza (head)
├── cola (tail)
├── tamaño
└── limite_maximo  (ej: 100)
```

### 🔹 Ventajas Clave

- 🔁 Navegación **bidireccional**.
- ⚡ Eliminación **eficiente**.
- 🧩 Inserción **flexible**.

---

## ⚙️ Función de Simulación: `sensores()`

### 🔹 Comportamiento

- Genera datos aleatorios cada cierto tiempo.
- Selecciona aleatoriamente: **Tipo de sensor**, **SensorID** y **Valor**.

### 🔹 Ejemplo Lógico

```
func sensores():
    mientras activo:
        tipo     = random(["Temperatura", "Humedad", "Vibración"])
        valor    = generar_valor(tipo)
        sensorID = "S" + random(1-50)

        crear_alerta(tipo, valor, sensorID)
```

---

## 🧪 Validación de Datos

### 🔹 Validación Física (Rechazo)

| Sensor | Condición de Rechazo |
|--------|----------------------|
| 🌡️ Temperatura | `< -40` o `> 120` → ❌ ERROR |
| 💧 Humedad | `< 0` o `> 100` → ❌ ERROR |
| ⚙️ Vibración | `< 0` → ❌ ERROR |

> 👉 **Acción:** Registrar `"LECTURA CORRUPTA"` y **NO insertar** el nodo.

---

### 🔹 Validación Lógica (Prioridad)

**🌡️ Temperatura**
> Dentro del rango → Prioridad definida por reglas del sistema.

**💧 Humedad**

| Rango | Estado | Prioridad |
|-------|--------|-----------|
| 0 – 20 | Muy seca | 2 |
| 21 – 60 | Óptima | 1 |
| 61 – 80 | Alta | 2 |
| 81 – 100 | Crítica | 3 |

**⚙️ Vibración (mm/s)**

| Rango | Estado | Prioridad |
|-------|--------|-----------|
| 0 – 1.1 | Excelente | 1 |
| 1.2 – 4.5 | Normal | 1 |
| 4.6 – 7.1 | Insatisfactorio | 2 |
| > 7.1 | Peligro | 3 |

### 🔹 Reglas Adicionales

- Humedad `> 90%` → etiqueta: `"Condensación"`
- Humedad `< 10%` → etiqueta: `"Electroestático"`

---

## ➕ Operaciones CRUD

### 🔹 1. CREAR — Insertar

- **Prioridad = 3** → Insertar al **inicio** de la lista.
- **Otras prioridades** → Insertar al **final**.
- Aplicar **buffer circular** tras la inserción.

### 🔍 2. CONSULTAR

- Por `ID`
- Por `SensorID`
- Por `tipo_sensor`

### 🔄 3. ACTUALIZAR

- Buscar nodo por `ID`.
- Cambiar estado: `Pendiente` → `Revisada`

### ❌ 4. ELIMINAR

| Caso | Descripción |
|------|-------------|
| Caso 1 | Nodo intermedio |
| Caso 2 | Cabeza (head) |
| Caso 3 | Cola (tail) |

---

## 📊 Reportes

| # | Reporte | Descripción |
|---|---------|-------------|
| 1 | Promedio de valores | `suma / cantidad` |
| 2 | Conteo por prioridad | Baja / Media / Crítica |
| 3 | Sensor más problemático | Contar repeticiones → retornar máximo |
| 4 | Búsqueda por rango de tiempo | Filtrar por `timestamp` |
| 5 | Eliminación por umbral | Si `temperatura < X` → eliminar nodo |
| 6 | Detección de patrones | Últimos 10 nodos, contar críticas por sensor |
| 7 | Vibración crítica | Filtrar `valor > 7.1` |
| 8 | Historial de humedad | Filtrar `tipo_sensor = "Humedad"` |

---

## 💾 Persistencia

### 🔹 Formato JSON

```json
[
  {
    "id": 1,
    "sensorID": "S12",
    "tipo": "Humedad",
    "valor": 85,
    "prioridad": 3,
    "timestamp": 123456789
  }
]
```

### 🔹 Funciones

```java
guardarArchivo()   // Exporta la lista a JSON
cargarArchivo()    // Importa datos desde JSON
```

---

## ♻️ Buffer Circular

### 🔹 Regla

```
si tamaño >= limite:
    eliminar cabeza
```

> Esto garantiza que la lista **nunca supere el límite máximo** de nodos en memoria.

---

## 🖥️ Interfaz (Botón)

### 🔹 Comportamiento

| Elemento | Acción |
|----------|--------|
| ▶️ Botón `"Iniciar Sensores"` | Ejecuta `sensores()` en hilo paralelo |
| 🔴 Botón `"Detener Sensores"` | Detiene el hilo de simulación |
| 📋 Tabla / Área de texto | Actualiza la lista en tiempo real |

---

## 🔄 Flujo Completo

```
[Sensor genera dato]
        ↓
[Validación física]
        ↓
  ¿Es válido?
   /       \
 NO         SÍ
  ↓          ↓
[LECTURA  [Asignación de prioridad]
CORRUPTA]        ↓
         [Inserción en lista]
                 ↓
         [Aplicar buffer circular]
                 ↓
         [Disponible para consultas/reportes]
                 ↓
         [Persistencia en archivo]
```

---

> **Tecnología:** Java | **Interfaz:** Java Swing (NetBeans Designer) | **Almacenamiento:** JSON