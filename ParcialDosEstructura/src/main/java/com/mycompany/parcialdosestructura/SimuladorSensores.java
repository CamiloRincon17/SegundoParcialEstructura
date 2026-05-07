package com.mycompany.parcialdosestructura;

import java.util.Random;

/**
 * SimuladorSensores
 * -----------------
 * Responsabilidad: generar lecturas aleatorias de sensores industriales.
 * NO contiene lógica de validación ni de interfaz gráfica.
 */
public class SimuladorSensores {

    private final Random random = new Random();

    // Tipos de sensores disponibles
    private static final String[] TIPOS = {"Temperatura", "Humedad", "Vibración"};

    // IDs de sensores disponibles
    private static final String[] SENSORES = {"SENS-1", "SENS-2", "SENS-3"};

    /**
     * Genera una lectura aleatoria de sensor.
     * Incluye ocasionalmente valores fuera de rango para probar la validación.
     *
     * @return arreglo con [sensorId, tipoSensor, valor(String)]
     */
    public String[] generarLectura() {
        String tipo     = TIPOS[random.nextInt(TIPOS.length)];
        String sensorId = SENSORES[random.nextInt(SENSORES.length)];
        float  valor    = generarValor(tipo);

        return new String[]{sensorId, tipo, String.valueOf(valor)};
    }

    /**
     * Devuelve el tipo de sensor elegido en la última lectura (info auxiliar).
     */
    private float generarValor(String tipo) {
        return switch (tipo) {
            // Rango ampliado para que se generen lecturas corruptas ocasionalmente
            case "Temperatura" -> -45 + random.nextFloat() * 170;  // -45 a 125
            case "Humedad"     -> -5  + random.nextFloat() * 110;  // -5  a 105
            default            -> -1  + random.nextFloat() * 11;   // -1  a 10  (Vibración)
        };
    }
}
