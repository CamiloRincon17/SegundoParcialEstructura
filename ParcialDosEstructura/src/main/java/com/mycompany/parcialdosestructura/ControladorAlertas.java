package com.mycompany.parcialdosestructura;

/**
 * ControladorAlertas
 * ------------------
 * Responsabilidad: actuar como intermediario entre la interfaz gráfica (MenuParcial)
 * y la lógica de datos (ListaDoble / SimuladorSensores).
 *
 * La UI NO llama directamente a ListaDoble; todo pasa por aquí.
 * Esto permite cambiar la estructura de datos sin tocar la interfaz.
 */
public class ControladorAlertas {

    private final ListaDoble       lista;
    private final SimuladorSensores simulador;

    public ControladorAlertas() {
        this.lista     = new ListaDoble();
        this.simulador = new SimuladorSensores();
    }

    // ─────────────────────────────────────────────────────────
    //  SIMULACIÓN
    // ─────────────────────────────────────────────────────────

    /**
     * Genera un ciclo de simulación automática.
     * Llama al simulador, valida e inserta. Retorna el log para mostrar en UI.
     */
    public String ejecutarCicloSimulacion() {
        String[] lectura  = simulador.generarLectura();
        String   sensorId = lectura[0];
        String   tipo     = lectura[1];
        float    valor    = Float.parseFloat(lectura[2]);

        String resultado = lista.validarEInsertar(sensorId, tipo, valor);
        return String.format("Auto → %s (%s): %.2f → %s", sensorId, tipo, valor, resultado);
    }

    // ─────────────────────────────────────────────────────────
    //  CRUD — CREAR
    // ─────────────────────────────────────────────────────────

    /**
     * Inserción manual desde la interfaz.
     * Retorna mensaje de resultado para mostrar en el textArea.
     */
    public String insertarManual(String sensorId, String tipo, String valorTexto) {
        try {
            float valor = Float.parseFloat(valorTexto.trim());
            return "Manual: " + lista.validarEInsertar(sensorId, tipo, valor);
        } catch (NumberFormatException e) {
            return "Error: el valor ingresado no es un número válido.";
        }
    }

    // ─────────────────────────────────────────────────────────
    //  CRUD — LEER / CONSULTAR
    // ─────────────────────────────────────────────────────────

    /** Lista todas las alertas en orden normal o inverso. */
    public String listarTodas(boolean inverso) {
        return lista.listarTodas(inverso);
    }

    /** Consulta una alerta por su ID numérico. */
    public String consultarPorId(String idTexto) {
        if (idTexto == null || idTexto.isBlank())
            return "⚠️ El campo ID está vacío. Escriba un número y vuelva a intentarlo.";
        try {
            int id = Integer.parseInt(idTexto.trim());
            return "Consulta ID " + id + ":\n" + lista.consultarPorId(id);
        } catch (NumberFormatException e) {
            return "⚠️ '" + idTexto.trim() + "' no es un ID válido. Use solo números (ej: 1, 5, 12).";
        }
    }

    /** Filtra todas las alertas de un sensor específico. */
    public String filtrarPorSensor(String sensorId) {
        if (sensorId == null || sensorId.isBlank()) {
            return "Error: ingrese un Sensor ID para filtrar.";
        }
        String alertas   = lista.filtrarPorSensor(sensorId);
        int    patrones  = lista.contarPatronesSensor(sensorId);
        return "--- ALERTAS DE " + sensorId + " ---\n"
             + alertas
             + "\n→ Alertas críticas en últimos 10 registros: " + patrones;
    }

    // ─────────────────────────────────────────────────────────
    //  CRUD — ACTUALIZAR
    // ─────────────────────────────────────────────────────────

    /** Cambia el estado de una alerta de Pendiente → Revisada. */
    public String marcarRevisada(String idTexto) {
        try {
            int id = Integer.parseInt(idTexto.trim());
            if (lista.actualizarEstado(id, "Revisada")) {
                return "ID " + id + " marcado como Revisada.";
            }
            return "ID " + id + " no encontrado.";
        } catch (NumberFormatException e) {
            return "Error: ingrese un ID numérico válido.";
        }
    }

    // ─────────────────────────────────────────────────────────
    //  CRUD — ELIMINAR
    // ─────────────────────────────────────────────────────────

    /** Elimina una alerta por ID. */
    public String eliminarPorId(String idTexto) {
        if (idTexto == null || idTexto.isBlank())
            return "⚠️ El campo ID está vacío. Escriba un número y vuelva a intentarlo.";
        try {
            int id = Integer.parseInt(idTexto.trim());
            if (lista.eliminarPorId(id))
                return "✅ Registro ID " + id + " eliminado correctamente.";
            return "❌ ID " + id + " no encontrado. Use 'Listar Todas' para ver los IDs disponibles.";
        } catch (NumberFormatException e) {
            return "⚠️ '" + idTexto.trim() + "' no es un ID válido. Use solo números (ej: 1, 5, 12).";
        }
    }

    /** Elimina todas las alertas de Temperatura con valor menor al umbral dado. */
    public String eliminarPorUmbral(String umbralTexto) {
        try {
            float umbral    = Float.parseFloat(umbralTexto.trim());
            int   eliminados = lista.eliminarAlertasMenoresA(umbral);
            return "Se eliminaron " + eliminados + " alerta(s) de Temperatura < " + umbral + " °C.";
        } catch (NumberFormatException e) {
            return "Error: ingrese un valor de umbral numérico válido.";
        }
    }

    // ─────────────────────────────────────────────────────────
    //  REPORTES
    // ─────────────────────────────────────────────────────────

    /** Genera todos los reportes estadísticos. */
    public String generarReportes() {
        return lista.generarReportes();
    }

    /** Retorna alertas de vibración en estado de peligro (> 7.1 mm/s). */
    public String reporteVibracionPeligro() {
        return "--- PELIGRO VIBRACIÓN (> 7.1 mm/s) ---\n" + lista.listarVibracionPeligro();
    }

    /** Retorna alertas de humedad con riesgo de condensación. */
    public String reporteCondensacion() {
        return "--- RIESGO DE CONDENSACIÓN (Humedad > 80%) ---\n"
             + lista.consultarHistorialHumedadCondensacion();
    }

    // ─────────────────────────────────────────────────────────
    //  INFO GENERAL
    // ─────────────────────────────────────────────────────────

    /** Retorna la cantidad de registros actuales en memoria. */
    public int getTotalRegistros() {
        return lista.size;
    }
}
