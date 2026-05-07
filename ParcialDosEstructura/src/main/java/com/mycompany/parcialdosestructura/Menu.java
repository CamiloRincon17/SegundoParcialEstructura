package com.mycompany.parcialdosestructura;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Menu extends javax.swing.JFrame {

    private ListaDoble lista;
    private Timer timerSensores;
    private JTextArea textArea;
    private Random random;

    public Menu() {
        lista = new ListaDoble();
        random = new Random();
        initCustomComponents();
    }

    private void initCustomComponents() {
        setTitle("Sistema de Gestión de Alertas IoT");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel Superior para entradas y acciones principales
        JPanel panelTop = new JPanel(new GridLayout(3, 1));
        
        JPanel panelSimulacion = new JPanel();
        JButton btnSimular = new JButton("Iniciar Simulación de Sensores");
        JButton btnDetener = new JButton("Detener Simulación");
        panelSimulacion.add(btnSimular);
        panelSimulacion.add(btnDetener);

        JPanel panelManual = new JPanel();
        JTextField txtSensorId = new JTextField("SENS-1", 5);
        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"Temperatura", "Humedad", "Vibración"});
        JTextField txtValor = new JTextField(5);
        JButton btnInsertar = new JButton("Insertar Manual");
        panelManual.add(new JLabel("Sensor ID:"));
        panelManual.add(txtSensorId);
        panelManual.add(new JLabel("Tipo:"));
        panelManual.add(cmbTipo);
        panelManual.add(new JLabel("Valor:"));
        panelManual.add(txtValor);
        panelManual.add(btnInsertar);

        JPanel panelAcciones = new JPanel();
        JButton btnListar = new JButton("Listar Todas");
        JButton btnListarInv = new JButton("Listar Inverso");
        JButton btnReportes = new JButton("Reportes Estadísticos");
        JButton btnPeligroVib = new JButton("Peligro Vibración");
        JButton btnCondensacion = new JButton("Riesgo Condensación");
        JButton btnLimpiarText = new JButton("Limpiar Pantalla");
        panelAcciones.add(btnListar);
        panelAcciones.add(btnListarInv);
        panelAcciones.add(btnReportes);
        panelAcciones.add(btnPeligroVib);
        panelAcciones.add(btnCondensacion);
        panelAcciones.add(btnLimpiarText);

        panelTop.add(panelSimulacion);
        panelTop.add(panelManual);
        panelTop.add(panelAcciones);

        add(panelTop, BorderLayout.NORTH);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(textArea);
        add(scroll, BorderLayout.CENTER);
        
        // Panel Derecho para operaciones por ID y utilidades
        JPanel panelRight = new JPanel(new GridLayout(12, 1, 5, 5));
        panelRight.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField txtId = new JTextField();
        JButton btnConsultarId = new JButton("Consultar por ID");
        JButton btnRevisar = new JButton("Marcar Revisada");
        JButton btnEliminar = new JButton("Eliminar por ID");
        
        JTextField txtSensorBuscar = new JTextField();
        JButton btnFiltrarSensor = new JButton("Filtrar Sensor");
        
        JTextField txtUmbral = new JTextField();
        JButton btnEliminarUmbral = new JButton("Eliminar Temp < X");

        panelRight.add(new JLabel("ID Operación:"));
        panelRight.add(txtId);
        panelRight.add(btnConsultarId);
        panelRight.add(btnRevisar);
        panelRight.add(btnEliminar);
        
        panelRight.add(new JLabel("Buscar Sensor ID:"));
        panelRight.add(txtSensorBuscar);
        panelRight.add(btnFiltrarSensor);
        
        panelRight.add(new JLabel("Umbral Temp (°C):"));
        panelRight.add(txtUmbral);
        panelRight.add(btnEliminarUmbral);
        
        add(panelRight, BorderLayout.EAST);

        // Eventos
        timerSensores = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simularSensores();
            }
        });

        btnSimular.addActionListener(e -> {
            timerSensores.start();
            mostrar("--- SIMULACIÓN INICIADA ---");
        });
        btnDetener.addActionListener(e -> {
            timerSensores.stop();
            mostrar("--- SIMULACIÓN DETENIDA ---");
        });

        btnInsertar.addActionListener(e -> {
            try {
                String sId = txtSensorId.getText();
                String tipo = (String) cmbTipo.getSelectedItem();
                float val = Float.parseFloat(txtValor.getText());
                String res = lista.validarEInsertar(sId, tipo, val);
                mostrar("Manual: " + res);
            } catch (Exception ex) {
                mostrar("Error en formato de datos manuales. Verifique que el valor sea numérico.");
            }
        });

        btnListar.addActionListener(e -> {
            mostrar("--- TODAS LAS ALERTAS ---");
            mostrar(lista.listarTodas(false));
        });
        btnListarInv.addActionListener(e -> {
            mostrar("--- TODAS LAS ALERTAS (INVERSO) ---");
            mostrar(lista.listarTodas(true));
        });
        btnReportes.addActionListener(e -> mostrar(lista.generarReportes()));
        btnPeligroVib.addActionListener(e -> {
            mostrar("--- PELIGRO DE VIBRACIÓN (> 7.1) ---");
            mostrar(lista.listarVibracionPeligro());
        });
        btnCondensacion.addActionListener(e -> {
            mostrar("--- RIESGO DE CONDENSACIÓN (Humedad > 80%) ---");
            mostrar(lista.consultarHistorialHumedadCondensacion());
        });
        btnLimpiarText.addActionListener(e -> textArea.setText(""));

        btnConsultarId.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                mostrar("Consulta ID " + id + ":\n" + lista.consultarPorId(id));
            } catch (Exception ex) { mostrar("Ingrese un ID numérico válido."); }
        });

        btnRevisar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                if (lista.actualizarEstado(id, "Revisada")) mostrar("Estado de ID " + id + " actualizado a Revisada.");
                else mostrar("ID " + id + " no encontrado.");
            } catch (Exception ex) { mostrar("Ingrese un ID numérico válido."); }
        });

        btnEliminar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                if (lista.eliminarPorId(id)) mostrar("Registro ID " + id + " eliminado.");
                else mostrar("ID " + id + " no encontrado.");
            } catch (Exception ex) { mostrar("Ingrese un ID numérico válido."); }
        });
        
        btnFiltrarSensor.addActionListener(e -> {
            String sid = txtSensorBuscar.getText();
            if (!sid.isEmpty()) {
                mostrar("--- ALERTAS DE " + sid + " ---");
                mostrar(lista.filtrarPorSensor(sid));
                int patrones = lista.contarPatronesSensor(sid);
                mostrar("--> Alertas críticas en últimos 10 registros de la cola global para " + sid + ": " + patrones);
            }
        });

        btnEliminarUmbral.addActionListener(e -> {
            try {
                float temp = Float.parseFloat(txtUmbral.getText());
                int eliminados = lista.eliminarAlertasMenoresA(temp);
                mostrar("Se eliminaron " + eliminados + " alertas de temperatura menores a " + temp + " °C");
            } catch (Exception ex) { mostrar("Ingrese un valor de umbral válido."); }
        });

        mostrar("Sistema Iniciado. " + lista.size + " registros cargados en memoria (Buffer max 100).");
    }

    private void simularSensores() {
        String[] tipos = {"Temperatura", "Humedad", "Vibración"};
        String tipo = tipos[random.nextInt(tipos.length)];
        String sensorId = "SENS-" + (random.nextInt(3) + 1); // SENS-1, SENS-2, SENS-3
        
        float val = 0;
        if (tipo.equals("Temperatura")) {
            // Rango de -45 a 125 (para generar "Lectura Corrupta" ocasionalmente)
            val = -45 + random.nextFloat() * 170;
        } else if (tipo.equals("Humedad")) {
            // Rango de -5 a 105 
            val = -5 + random.nextFloat() * 110;
        } else if (tipo.equals("Vibración")) {
            // Rango de -1 a 10
            val = -1 + random.nextFloat() * 11;
        }
        
        String res = lista.validarEInsertar(sensorId, tipo, val);
        mostrar("Generado -> " + sensorId + " (" + tipo + "): " + String.format("%.2f", val) + " -> " + res);
    }

    private void mostrar(String msg) {
        textArea.append(msg + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {}

        java.awt.EventQueue.invokeLater(() -> {
            new Menu().setVisible(true);
        });
    }
}
