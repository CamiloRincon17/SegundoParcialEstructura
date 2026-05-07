package com.mycompany.parcialdosestructura;

public class NodoDoble {
    public int id;
    public String sensorId;
    public String tipoSensor; // "Temperatura", "Humedad", "Vibración"
    public float valor;
    public int prioridad; // 1: Baja, 2: Media, 3: Crítica
    public long timestamp;
    public String estado; // "Pendiente", "Revisada"
    public String etiqueta; 

    public NodoDoble siguiente;
    public NodoDoble anterior;

    public NodoDoble(int id, String sensorId, String tipoSensor, float valor, int prioridad, long timestamp, String estado, String etiqueta) {
        this.id = id;
        this.sensorId = sensorId;
        this.tipoSensor = tipoSensor;
        this.valor = valor;
        this.prioridad = prioridad;
        this.timestamp = timestamp;
        this.estado = estado;
        this.etiqueta = etiqueta;
        this.siguiente = null;
        this.anterior = null;
    }
}
