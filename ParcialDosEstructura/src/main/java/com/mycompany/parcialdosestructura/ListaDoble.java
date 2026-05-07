package com.mycompany.parcialdosestructura;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class ListaDoble {
    public NodoDoble cabeza;
    public NodoDoble cola;
    public int size;
    private final int MAX_NODOS = 100;
    private int nextId = 1;

    public ListaDoble() {
        cabeza = null;
        cola = null;
        size = 0;
        cargarDesdeArchivo();
    }

    public String validarEInsertar(String sensorId, String tipoSensor, float valor) {
        if (tipoSensor.equals("Temperatura")) {
            if (valor < -40 || valor > 120) {
                return "Error: LECTURA CORRUPTA. Temperatura fuera de rango.";
            }
            int prioridad = 1; 
            if (valor > 80 || valor < 0) prioridad = 3;
            else if (valor > 60 || valor < 10) prioridad = 2;
            
            insertarNodo(sensorId, tipoSensor, valor, prioridad, "");
        } else if (tipoSensor.equals("Humedad")) {
            if (valor < 0 || valor > 100) {
                return "Error: LECTURA CORRUPTA. Humedad fuera de rango.";
            }
            int prioridad = 1;
            String etiqueta = "";
            if (valor >= 0 && valor <= 20) {
                prioridad = 2; // Media
                if (valor < 10) etiqueta = "Alerta de Riesgo Electroestático";
            } else if (valor >= 21 && valor <= 60) {
                prioridad = 1; // Baja
            } else if (valor >= 61 && valor <= 80) {
                prioridad = 2; // Media
            } else if (valor >= 81 && valor <= 100) {
                prioridad = 3; // Crítica
                if (valor > 90) etiqueta = "Alerta de Inundación/Condensación";
            }
            insertarNodo(sensorId, tipoSensor, valor, prioridad, etiqueta);
        } else if (tipoSensor.equals("Vibración")) {
            if (valor < 0) {
                return "Error: LECTURA CORRUPTA. Vibración fuera de rango.";
            }
            int prioridad = 1;
            if (valor >= 0.0 && valor <= 1.1) {
                prioridad = 1;
            } else if (valor >= 1.2 && valor <= 4.5) {
                prioridad = 1; // Satisfactorio
            } else if (valor >= 4.6 && valor <= 7.1) {
                prioridad = 2;
            } else if (valor > 7.1) {
                prioridad = 3;
            }
            insertarNodo(sensorId, tipoSensor, valor, prioridad, "");
        } else {
            return "Error: Tipo de sensor desconocido.";
        }
        return "Alerta registrada correctamente.";
    }

    private void insertarNodo(String sensorId, String tipoSensor, float valor, int prioridad, String etiqueta) {
        NodoDoble nuevo = new NodoDoble(nextId++, sensorId, tipoSensor, valor, prioridad, System.currentTimeMillis(), "Pendiente", etiqueta);
        agregarAlFinal(nuevo);
    }

    private void agregarAlFinal(NodoDoble nuevo) {
        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.siguiente = nuevo;
            nuevo.anterior = cola;
            cola = nuevo;
        }
        size++;
        
        if (size > MAX_NODOS) {
            // Eliminar el nodo más antiguo (el primero, la cabeza)
            cabeza = cabeza.siguiente;
            if (cabeza != null) {
                cabeza.anterior = null;
            } else {
                cola = null;
            }
            size--;
        }
        guardarEnArchivo();
    }
    
    public String consultarPorId(int id) {
        NodoDoble actual = cabeza;
        while (actual != null) {
            if (actual.id == id) return nodoToString(actual);
            actual = actual.siguiente;
        }
        return "Alerta no encontrada.";
    }

    public String filtrarPorSensor(String sensorId) {
        StringBuilder sb = new StringBuilder();
        NodoDoble actual = cabeza;
        while (actual != null) {
            if (actual.sensorId.equalsIgnoreCase(sensorId)) sb.append(nodoToString(actual)).append("\n");
            actual = actual.siguiente;
        }
        String res = sb.toString();
        return res.isEmpty() ? "No se encontraron alertas para ese sensor." : res;
    }

    public boolean actualizarEstado(int id, String nuevoEstado) {
        NodoDoble actual = cabeza;
        while (actual != null) {
            if (actual.id == id) {
                actual.estado = nuevoEstado;
                guardarEnArchivo();
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    public boolean eliminarPorId(int id) {
        NodoDoble actual = cabeza;
        while (actual != null) {
            if (actual.id == id) {
                eliminarNodoRef(actual);
                guardarEnArchivo();
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }
    
    private void eliminarNodoRef(NodoDoble nodo) {
        if (nodo == cabeza) {
            cabeza = nodo.siguiente;
            if (cabeza != null) cabeza.anterior = null;
            else cola = null;
        } else if (nodo == cola) {
            cola = nodo.anterior;
            if (cola != null) cola.siguiente = null;
            else cabeza = null;
        } else {
            nodo.anterior.siguiente = nodo.siguiente;
            nodo.siguiente.anterior = nodo.anterior;
        }
        size--;
    }

    public String listarTodas(boolean inverso) {
        StringBuilder sb = new StringBuilder();
        if (inverso) {
            NodoDoble actual = cola;
            while (actual != null) {
                sb.append(nodoToString(actual)).append("\n");
                actual = actual.anterior;
            }
        } else {
            NodoDoble actual = cabeza;
            while (actual != null) {
                sb.append(nodoToString(actual)).append("\n");
                actual = actual.siguiente;
            }
        }
        String res = sb.toString();
        return res.isEmpty() ? "No hay alertas registradas." : res;
    }

    public String generarReportes() {
        if (cabeza == null) return "No hay datos para generar reportes.";

        int countActivas = 0;
        float sumaValores = 0;
        int countBaja = 0, countMedia = 0, countCritica = 0;
        Map<String, Integer> sensorCount = new HashMap<>();

        NodoDoble actual = cabeza;
        while (actual != null) {
            if (actual.estado.equals("Pendiente")) {
                countActivas++;
                sumaValores += actual.valor;
            }
            if (actual.prioridad == 1) countBaja++;
            else if (actual.prioridad == 2) countMedia++;
            else if (actual.prioridad == 3) countCritica++;

            sensorCount.put(actual.sensorId, sensorCount.getOrDefault(actual.sensorId, 0) + 1);
            actual = actual.siguiente;
        }

        String sensorProblematico = "";
        int maxAlerts = 0;
        for (Map.Entry<String, Integer> entry : sensorCount.entrySet()) {
            if (entry.getValue() > maxAlerts) {
                maxAlerts = entry.getValue();
                sensorProblematico = entry.getKey();
            }
        }

        float promedio = countActivas > 0 ? sumaValores / countActivas : 0;

        return "--- Reportes Estadísticos ---\n" +
               "1. Promedio valores (alertas pendientes): " + String.format("%.2f", promedio) + "\n" +
               "2. Conteo por prioridad: Baja=" + countBaja + ", Media=" + countMedia + ", Crítica=" + countCritica + "\n" +
               "3. Sensor más problemático: " + sensorProblematico + " (" + maxAlerts + " alertas)\n";
    }

    public String buscarPorRango(long inicio, long fin) {
        StringBuilder sb = new StringBuilder();
        NodoDoble actual = cabeza;
        while (actual != null) {
            if (actual.timestamp >= inicio && actual.timestamp <= fin) {
                sb.append(nodoToString(actual)).append("\n");
            }
            actual = actual.siguiente;
        }
        String res = sb.toString();
        return res.isEmpty() ? "No se encontraron alertas en ese rango." : res;
    }

    public int eliminarAlertasMenoresA(float umbralTemp) {
        int eliminados = 0;
        NodoDoble actual = cabeza;
        while (actual != null) {
            NodoDoble sig = actual.siguiente;
            if (actual.tipoSensor.equals("Temperatura") && actual.valor < umbralTemp) {
                eliminarNodoRef(actual);
                eliminados++;
            }
            actual = sig;
        }
        if (eliminados > 0) guardarEnArchivo();
        return eliminados;
    }

    public int contarPatronesSensor(String sensorId) {
        int count = 0;
        int revisados = 0;
        NodoDoble actual = cola;
        while (actual != null && revisados < 10) {
            if (actual.sensorId.equalsIgnoreCase(sensorId)) {
                if (actual.prioridad == 3) {
                    count++;
                }
            }
            revisados++;
            actual = actual.anterior;
        }
        return count;
    }

    public String listarVibracionPeligro() {
        StringBuilder sb = new StringBuilder();
        NodoDoble actual = cabeza;
        while (actual != null) {
            if (actual.tipoSensor.equals("Vibración") && actual.valor > 7.1) {
                sb.append(nodoToString(actual)).append("\n");
            }
            actual = actual.siguiente;
        }
        String res = sb.toString();
        return res.isEmpty() ? "No hay alertas de peligro por vibración." : res;
    }

    public String consultarHistorialHumedadCondensacion() {
        StringBuilder sb = new StringBuilder();
        NodoDoble actual = cabeza;
        while (actual != null) {
            if (actual.tipoSensor.equals("Humedad") && actual.valor > 80) {
                sb.append(nodoToString(actual)).append("\n");
            }
            actual = actual.siguiente;
        }
        String res = sb.toString();
        return res.isEmpty() ? "No hay registros con riesgo de condensación." : res;
    }

    public String nodoToString(NodoDoble n) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String date = sdf.format(new Date(n.timestamp));
        String priorStr = n.prioridad == 1 ? "Baja" : n.prioridad == 2 ? "Media" : "Crítica";
        return String.format("[ID: %d] %s | %s | %s | Val: %.2f | Pri: %s | Est: %s %s",
                n.id, date, n.sensorId, n.tipoSensor, n.valor, priorStr, n.estado, n.etiqueta.isEmpty()?"":"| "+n.etiqueta);
    }

    private void guardarEnArchivo() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("src/main/resources/alertas.csv"))) {
            NodoDoble actual = cabeza;
            while (actual != null) {
                pw.println(actual.id + "," + actual.sensorId + "," + actual.tipoSensor + "," + actual.valor + "," + 
                           actual.prioridad + "," + actual.timestamp + "," + actual.estado + "," + actual.etiqueta);
                actual = actual.siguiente;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarDesdeArchivo() {
        File f = new File("src/main/resources/alertas.csv");
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 8) {
                    int id = Integer.parseInt(p[0]);
                    float val = Float.parseFloat(p[3]);
                    int prio = Integer.parseInt(p[4]);
                    long ts = Long.parseLong(p[5]);
                    NodoDoble n = new NodoDoble(id, p[1], p[2], val, prio, ts, p[6], p[7]);
                    agregarAlFinalSinGuardar(n);
                    if (id >= nextId) nextId = id + 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void agregarAlFinalSinGuardar(NodoDoble nuevo) {
        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.siguiente = nuevo;
            nuevo.anterior = cola;
            cola = nuevo;
        }
        size++;
    }
}
