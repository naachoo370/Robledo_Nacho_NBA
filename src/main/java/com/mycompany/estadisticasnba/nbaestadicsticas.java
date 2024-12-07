/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.estadisticasnba;
import java.io.File;
import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author nacho
 */

public class nbaestadicsticas extends javax.swing.JFrame {

    // Define equipos y jugadores
    String[] Atm = {"Antoine Griezmann", "Koke Resurrección", "Pablo Barrios Rivas", "Julián Álvarez", "José María Giménez"};
    String[] Dam = {"Nacho Robledo", "Sergio Martin", "Victor Ruiz", "Juan Marin", "Alba Gonzalez"};


    public nbaestadicsticas() {
        initComponents();
        this.setLocationRelativeTo(null);
        equipo.addItem("Atm");  // Añadir equipo Atlético de Madrid
        equipo.addItem("Dam");  // Añadir equipo DAM
        equipo.addActionListener(evt -> seleccionarEquipo());
        Guardar.addActionListener(evt -> crearExcel());
    }

    private void seleccionarEquipo() {
        // Obtiene el equipo seleccionado en el componente "equipo"
        String cogerequipo = (String) equipo.getSelectedItem();

        // Elimina todos los elementos de la lista de jugadores para evitar duplicados
        jugadores.removeAllItems();

        // Añade los jugadores del equipo seleccionado
        if ("Atm".equals(cogerequipo)) {
            for (String jugador : Atm) {
                jugadores.addItem(jugador);
            }
        } else if ("Dam".equals(cogerequipo)) {
            for (String jugador : Dam) {
                jugadores.addItem(jugador);
            }
        }
    }

    private void crearExcel() {
        // Obtener equipo y jugador seleccionados
        String equipoSeleccionado = (String) equipo.getSelectedItem();
        String jugadorSeleccionado = (String) jugadores.getSelectedItem();

        // Validación de entrada para asegurarse de que un jugador ha sido seleccionado
        if (jugadorSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un jugador.");
            return;
        }

        // Datos de pestaña tiros
        int tirosRealizados = (int) jSpinnertirosRealizados.getValue();
        int tirosDeDosRealizados = (int) tiros_realizados_2_spinner.getValue();
        int tirosDeDosEncestados = (int) tiros_encestados_2_spinner.getValue();
        int tirosDeTresRealizados = (int) tiros_realizados_3_spinner.getValue();
        int tirosDeTresEncestados = (int) tiros_encestados_3_spinner.getValue();
        int tirosLibresRealizados = (int) tiros_realizados_libres_spinner.getValue();
        int tirosLibresEncestados = (int) tiros_encestados_libres_spinner.getValue();

        int tirosTotales = tirosLibresRealizados + tirosDeDosRealizados + tirosDeTresRealizados;
        int tirosMetidos = tirosLibresEncestados + tirosDeDosEncestados + tirosDeTresEncestados;
        int tirosFallados = tirosTotales - tirosMetidos;

        // Datos de pestaña más datos
        int totalRebotes = (int) rebotes_spinner.getValue();
        int asistenciasTotales = (int) asistencias_spinner.getValue();
        int robosTotales = (int) robos_spinner.getValue();
        int taponesAFavor = (int) tapones_a_favor_spinner.getValue();
        int faltasRecibidasTotales = (int) FaltasRecibidas_Spinner.getValue();

        int fallosDeCampo = tirosRealizados - (tirosDeDosEncestados + tirosDeTresEncestados);
        int fallosLibres = tirosLibresRealizados - tirosLibresEncestados;
        int perdidasTotales = (int) perdidas_spinner.getValue();
        int taponesRecibidos = (int) tapones_recibidos_spinner.getValue();
        int faltasCometidas = (int) faltas_realizadas_spinner.getValue();

        // Cálculo de los puntos totales
        int puntosTotales = tirosLibresEncestados + (2 * tirosDeDosEncestados) + (3 * tirosDeTresEncestados);

        // Cálculo de la valoración (valoración = suma de estadísticas positivas - estadísticas negativas)
        double valoracion = (puntosTotales + totalRebotes + asistenciasTotales + robosTotales + taponesAFavor + faltasRecibidasTotales)
                            - (fallosDeCampo + perdidasTotales + taponesRecibidos + faltasCometidas);

        // Validaciones
        if (tirosDeDosRealizados + tirosDeTresRealizados <= 0) {
            JOptionPane.showMessageDialog(this, "El total de tiros dobles y triples no puede ser 0");
            return;
        }

        if (tirosLibresEncestados > tirosLibresRealizados || tirosDeDosEncestados > tirosDeDosRealizados || tirosDeTresEncestados > tirosDeTresRealizados) {
            JOptionPane.showMessageDialog(this, "Los tiros encestados no pueden ser más que los realizados");
            return;
        }

        // Cálculos de estadísticas
        double FG = (double) (tirosDeDosEncestados + tirosDeTresEncestados) / (tirosDeDosRealizados + tirosDeTresRealizados) * 100;
        double EFG = (double) (tirosDeDosEncestados + 1.5 * tirosDeTresEncestados) / (tirosDeDosRealizados + tirosDeTresRealizados) * 100;
        double TS = (double) puntosTotales / (2 * (tirosDeDosRealizados + tirosDeTresRealizados + 0.44 * tirosLibresRealizados)) * 100;

        // Ruta de archivo basada en equipo
        String filePath = "C:\\Users\\nacho\\OneDrive\\Desarrollo de interzaces\\NETBEANS\\EstadisticasNBA\\EstadisticasNBA_" + equipoSeleccionado + ".xlsx";

        try {
            Excel(filePath, jugadorSeleccionado, tirosLibresRealizados, tirosLibresEncestados, tirosDeDosRealizados,
                    tirosDeDosEncestados, tirosDeTresRealizados, tirosDeTresEncestados, FG, EFG, TS, puntosTotales,
                    totalRebotes, asistenciasTotales, robosTotales, perdidasTotales, taponesAFavor, taponesRecibidos,
                    faltasCometidas, faltasRecibidasTotales, valoracion);

            // Mostrar el mensaje de éxito cuando se haya creado el archivo Excel correctamente
            JOptionPane.showMessageDialog(this, "Archivo actualizado: " + filePath);

            // Llamada para calcular medias (si tienes esta funcionalidad)
            calcularMedias(filePath);

            // Mostrar mensaje cuando se inserte el jugador y sus puntuaciones
            JOptionPane.showMessageDialog(this, "Jugador y puntuaciones insertados correctamente.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al crear el archivo Excel: " + e.getMessage());
        }
    }

    private void Excel(String filePath, String jugador, int tirosLibresRealizados, int tirosLibresEncestados,
                       int tirosDeDosRealizados, int tirosDeDosEncestados, int tirosDeTresRealizados, int tirosDeTresEncestados,
                       double FG, double EFG, double TS, int puntosTotales, int rebotes, int asistencias,
                       int robos, int perdidas, int taponesAFavor, int taponesRecibidos, int faltasCometidas,
                       int faltasRecibidas, double valoracion) throws IOException {

        Workbook excel;
        Sheet hojaJugador;

        // Verificar si el archivo ya existe
        File archivo = new File(filePath);
        if (archivo.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(archivo)) {
                excel = new XSSFWorkbook(fileInputStream);
            }
        } else {
            excel = new XSSFWorkbook(); // Crear nuevo archivo si no existe
        }

        // Crear o obtener la hoja correspondiente al jugador
        hojaJugador = excel.getSheet(jugador) != null ? excel.getSheet(jugador) : excel.createSheet(jugador);

        // Verifica si ya existe una fila de encabezado, si no la crea
        if (hojaJugador.getPhysicalNumberOfRows() == 0) {
            Row encabezado = hojaJugador.createRow(0);
            encabezado.createCell(0).setCellValue("Jugador");
            encabezado.createCell(1).setCellValue("Tiros Libres Realizados");
            encabezado.createCell(2).setCellValue("Tiros Libres Encestados");
            encabezado.createCell(3).setCellValue("Tiros de Dos Realizados");
            encabezado.createCell(4).setCellValue("Tiros de Dos Encestados");
            encabezado.createCell(5).setCellValue("Tiros de Tres Realizados");
            encabezado.createCell(6).setCellValue("Tiros de Tres Encestados");
            encabezado.createCell(7).setCellValue("Porcentaje de Acierto FG");
            encabezado.createCell(8).setCellValue("Porcentaje EFG");
            encabezado.createCell(9).setCellValue("Porcentaje TS");
            encabezado.createCell(10).setCellValue("Puntos Totales");
            encabezado.createCell(11).setCellValue("Rebotes");
            encabezado.createCell(12).setCellValue("Asistencias");
            encabezado.createCell(13).setCellValue("Robos");
            encabezado.createCell(14).setCellValue("Pérdidas");
            encabezado.createCell(15).setCellValue("Tapones a Favor");
            encabezado.createCell(16).setCellValue("Tapones Recibidos");
            encabezado.createCell(17).setCellValue("Faltas Cometidas");
            encabezado.createCell(18).setCellValue("Faltas Recibidas");
            encabezado.createCell(19).setCellValue("Valoración");
        }

        // Obtener o crear fila para los datos del jugador
        int filaJugador = hojaJugador.getPhysicalNumberOfRows();
        Row row = hojaJugador.createRow(filaJugador);
        row.createCell(0).setCellValue(jugador);

        // Insertar los valores de estadísticas en las celdas correspondientes
        row.createCell(1).setCellValue(tirosLibresRealizados);
        row.createCell(2).setCellValue(tirosLibresEncestados);
        row.createCell(3).setCellValue(tirosDeDosRealizados);
        row.createCell(4).setCellValue(tirosDeDosEncestados);
        row.createCell(5).setCellValue(tirosDeTresRealizados);
        row.createCell(6).setCellValue(tirosDeTresEncestados);
        row.createCell(7).setCellValue(FG);
        row.createCell(8).setCellValue(EFG);
        row.createCell(9).setCellValue(TS);
        row.createCell(10).setCellValue(puntosTotales);
        row.createCell(11).setCellValue(rebotes);
        row.createCell(12).setCellValue(asistencias);
        row.createCell(13).setCellValue(robos);
        row.createCell(14).setCellValue(perdidas);
        row.createCell(15).setCellValue(taponesAFavor);
        row.createCell(16).setCellValue(taponesRecibidos);
        row.createCell(17).setCellValue(faltasCometidas);
        row.createCell(18).setCellValue(faltasRecibidas);
        row.createCell(19).setCellValue(valoracion);

        // Guardar el archivo de Excel
        try (FileOutputStream fileOutputStream = new FileOutputStream(archivo)) {
            excel.write(fileOutputStream);
        }
    }

    private void calcularMedias(String filePath) throws IOException {
    File archivo = new File(filePath);
    try (FileInputStream fileInputStream = new FileInputStream(archivo)) {
        Workbook excel = new XSSFWorkbook(fileInputStream);
        
        // Crear o acceder a la hoja de medias
        Sheet hojaMedias = excel.getSheet("Medias");
        
        if (hojaMedias == null) {
            hojaMedias = excel.createSheet("Medias");
            
            // Agregar encabezado para la hoja de medias solo si se crea la hoja
            Row encabezado = hojaMedias.createRow(0);
            encabezado.createCell(0).setCellValue("Equipo");
            encabezado.createCell(1).setCellValue("Media Tiros Libres Realizados");
            encabezado.createCell(2).setCellValue("Media Tiros Libres Encestados");
            encabezado.createCell(3).setCellValue("Media Tiros de Dos Realizados");
            encabezado.createCell(4).setCellValue("Media Tiros de Dos Encestados");
            encabezado.createCell(5).setCellValue("Media Tiros de Tres Realizados");
            encabezado.createCell(6).setCellValue("Media Tiros de Tres Encestados");
            encabezado.createCell(7).setCellValue("Media FG");
            encabezado.createCell(8).setCellValue("Media eFG");
            encabezado.createCell(9).setCellValue("Media TS");
            encabezado.createCell(10).setCellValue("Media Puntos Totales");
            encabezado.createCell(11).setCellValue("Media Rebotes");
            encabezado.createCell(12).setCellValue("Media Asistencias");
            encabezado.createCell(13).setCellValue("Media Robos");
            encabezado.createCell(14).setCellValue("Media Pérdidas");
            encabezado.createCell(15).setCellValue("Media Tapones a Favor");
            encabezado.createCell(16).setCellValue("Media Tapones Recibidos");
            encabezado.createCell(17).setCellValue("Media Faltas Cometidas");
            encabezado.createCell(18).setCellValue("Media Faltas Recibidas");
            encabezado.createCell(19).setCellValue("Media Valoración");
        }
        
        // Recorrer todas las hojas de los equipos
        for (int i = 0; i < excel.getNumberOfSheets(); i++) {
            Sheet sheet = excel.getSheetAt(i);
            
            // Ignorar la hoja de "Medias" para evitar recursividad infinita
            if (sheet.getSheetName().equals("Medias")) {
                continue;
            }
            
            // Verifica si la hoja tiene más de una fila (ignorando el encabezado)
            if (sheet.getPhysicalNumberOfRows() > 1) {
                double totalTirosLibresRealizados = 0;
                double totalTirosLibresEncestados = 0;
                double totalTirosDeDosRealizados = 0;
                double totalTirosDeDosEncestados = 0;
                double totalTirosDeTresRealizados = 0;
                double totalTirosDeTresEncestados = 0;
                double totalFG = 0;
                double totaleFG = 0;
                double totalTS = 0;
                double totalPuntos = 0;
                double totalRebotes = 0;
                double totalAsistencias = 0;
                double totalRobos = 0;
                double totalPerdidas = 0;
                double totalTaponesAFavor = 0;
                double totalTaponesRecibidos = 0;
                double totalFaltasCometidas = 0;
                double totalFaltasRecibidas = 0;
                double totalValoracion = 0;
                
                // Sumar las estadísticas de todos los jugadores en el equipo
                for (int rowNum = 1; rowNum < sheet.getPhysicalNumberOfRows(); rowNum++) {
                    Row row = sheet.getRow(rowNum);
                    totalTirosLibresRealizados += row.getCell(1).getNumericCellValue();
                    totalTirosLibresEncestados += row.getCell(2).getNumericCellValue();
                    totalTirosDeDosRealizados += row.getCell(3).getNumericCellValue();
                    totalTirosDeDosEncestados += row.getCell(4).getNumericCellValue();
                    totalTirosDeTresRealizados += row.getCell(5).getNumericCellValue();
                    totalTirosDeTresEncestados += row.getCell(6).getNumericCellValue();
                    totalFG += row.getCell(7).getNumericCellValue();
                    totaleFG += row.getCell(8).getNumericCellValue();
                    totalTS += row.getCell(9).getNumericCellValue();
                    totalPuntos += row.getCell(10).getNumericCellValue();
                    totalRebotes += row.getCell(11).getNumericCellValue();
                    totalAsistencias += row.getCell(12).getNumericCellValue();
                    totalRobos += row.getCell(13).getNumericCellValue();
                    totalPerdidas += row.getCell(14).getNumericCellValue();
                    totalTaponesAFavor += row.getCell(15).getNumericCellValue();
                    totalTaponesRecibidos += row.getCell(16).getNumericCellValue();
                    totalFaltasCometidas += row.getCell(17).getNumericCellValue();
                    totalFaltasRecibidas += row.getCell(18).getNumericCellValue();
                    totalValoracion += row.getCell(19).getNumericCellValue();
                }
                
                // Evitar división por cero
                int numJugadores = sheet.getPhysicalNumberOfRows() - 1;
                if (numJugadores > 0) {
                    // Calcular las medias
                    double mediaTirosLibresRealizados = totalTirosLibresRealizados / numJugadores;
                    double mediaTirosLibresEncestados = totalTirosLibresEncestados / numJugadores;
                    double mediaTirosDeDosRealizados = totalTirosDeDosRealizados / numJugadores;
                    double mediaTirosDeDosEncestados = totalTirosDeDosEncestados / numJugadores;
                    double mediaTirosDeTresRealizados = totalTirosDeTresRealizados / numJugadores;
                    double mediaTirosDeTresEncestados = totalTirosDeTresEncestados / numJugadores;
                    double mediaFG = totalFG / numJugadores;
                    double mediaeFG = totaleFG / numJugadores;
                    double mediaTS = totalTS / numJugadores;
                    double mediaPuntos = totalPuntos / numJugadores;
                    double mediaRebotes = totalRebotes / numJugadores;
                    double mediaAsistencias = totalAsistencias / numJugadores;
                    double mediaRobos = totalRobos / numJugadores;
                    double mediaPerdidas = totalPerdidas / numJugadores;
                    double mediaTaponesAFavor = totalTaponesAFavor / numJugadores;
                    double mediaTaponesRecibidos = totalTaponesRecibidos / numJugadores;
                    double mediaFaltasCometidas = totalFaltasCometidas / numJugadores;
                    double mediaFaltasRecibidas = totalFaltasRecibidas / numJugadores;
                    double mediaValoracion = totalValoracion / numJugadores;
                    
                    // Crear una fila nueva en la hoja de medias para el equipo
                    Row row = hojaMedias.createRow(hojaMedias.getPhysicalNumberOfRows());
                    row.createCell(0).setCellValue(sheet.getSheetName()); // Nombre del equipo
                    row.createCell(1).setCellValue(mediaTirosLibresRealizados);
                    row.createCell(2).setCellValue(mediaTirosLibresEncestados);
                    row.createCell(3).setCellValue(mediaTirosDeDosRealizados);
                    row.createCell(4).setCellValue(mediaTirosDeDosEncestados);
                    row.createCell(5).setCellValue(mediaTirosDeTresRealizados);
                    row.createCell(6).setCellValue(mediaTirosDeTresEncestados);
                    row.createCell(7).setCellValue(mediaFG);
                    row.createCell(8).setCellValue(mediaeFG);
                    row.createCell(9).setCellValue(mediaTS);
                    row.createCell(10).setCellValue(mediaPuntos);
                    row.createCell(11).setCellValue(mediaRebotes);
                    row.createCell(12).setCellValue(mediaAsistencias);
                    row.createCell(13).setCellValue(mediaRobos);
                    row.createCell(14).setCellValue(mediaPerdidas);
                    row.createCell(15).setCellValue(mediaTaponesAFavor);
                    row.createCell(16).setCellValue(mediaTaponesRecibidos);
                    row.createCell(17).setCellValue(mediaFaltasCometidas);
                    row.createCell(18).setCellValue(mediaFaltasRecibidas);
                    row.createCell(19).setCellValue(mediaValoracion);
                }
            }
        }
        
        // Guardar el archivo de Excel con las medias
        try (FileOutputStream fileOutputStream = new FileOutputStream(archivo)) {
            excel.write(fileOutputStream);
        }
    }
}
  
    
    

        
        
        
        
        
        
        

    
    
    
    
    
    

    

    

    

    

    

        
        
        
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedTiros = new javax.swing.JTabbedPane();
        rebotes = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        rebotes_spinner = new javax.swing.JSpinner();
        asistencias = new javax.swing.JLabel();
        asistencias_spinner = new javax.swing.JSpinner();
        robos = new javax.swing.JLabel();
        robos_spinner = new javax.swing.JSpinner();
        tapones_a_favor = new javax.swing.JLabel();
        tapones_a_favor_spinner = new javax.swing.JSpinner();
        tiros_fallados = new javax.swing.JLabel();
        tiros_libres_fallados = new javax.swing.JLabel();
        perdidas = new javax.swing.JLabel();
        tapones_recibidos = new javax.swing.JLabel();
        tiros_falladas_spinner = new javax.swing.JSpinner();
        tiros_libres_fallados_spinner = new javax.swing.JSpinner();
        tapones_recibidos_spinner = new javax.swing.JSpinner();
        perdidas_spinner = new javax.swing.JSpinner();
        faltas_recibidas = new javax.swing.JLabel();
        FaltasRecibidas_Spinner = new javax.swing.JSpinner();
        faltas_realizadas = new javax.swing.JLabel();
        faltas_realizadas_spinner = new javax.swing.JSpinner();
        Guardar = new javax.swing.JButton();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLabel5 = new javax.swing.JLabel();
        jSpinnertirosRealizados = new javax.swing.JSpinner();
        tiros_libres_realizados = new javax.swing.JLabel();
        tiros_realizados_libres_spinner = new javax.swing.JSpinner();
        tiros_libres_encestados = new javax.swing.JLabel();
        tiros_encestados_libres_spinner = new javax.swing.JSpinner();
        tiros_encestados_2 = new javax.swing.JLabel();
        tiros_encestados_2_spinner = new javax.swing.JSpinner();
        tiros_realizados_2 = new javax.swing.JLabel();
        tiros_realizados_2_spinner = new javax.swing.JSpinner();
        tiros_realizados_3 = new javax.swing.JLabel();
        tiros_encestados_3_spinner = new javax.swing.JSpinner();
        tiros_encestados_3 = new javax.swing.JLabel();
        tiros_realizados_3_spinner = new javax.swing.JSpinner();
        jugador = new javax.swing.JLabel();
        Equipo = new javax.swing.JLabel();
        jugadores = new javax.swing.JComboBox<>();
        equipo = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedTiros.setBackground(new java.awt.Color(204, 204, 204));

        rebotes.setBackground(new java.awt.Color(204, 204, 204));

        jLabel9.setBackground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Rebotes");

        asistencias.setText("Asistencias");

        robos.setText("Robos");

        tapones_a_favor.setText("Tapones a Favor");

        tiros_fallados.setText("Tiros fallados");

        tiros_libres_fallados.setText("Tiros libres fallados");

        perdidas.setText("Perdidas");

        tapones_recibidos.setText("Tapones recibidos");

        faltas_recibidas.setText("Faltas Recibidas");

        faltas_realizadas.setText("Faltas Realiadas");

        Guardar.setText("Guadar");
        Guardar.setPreferredSize(new java.awt.Dimension(72, 30));
        Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GuardarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout rebotesLayout = new javax.swing.GroupLayout(rebotes);
        rebotes.setLayout(rebotesLayout);
        rebotesLayout.setHorizontalGroup(
            rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rebotesLayout.createSequentialGroup()
                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rebotesLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(rebotesLayout.createSequentialGroup()
                                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(robos, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rebotes_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(robos_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(49, 49, 49)
                                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(rebotesLayout.createSequentialGroup()
                                            .addComponent(perdidas)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(perdidas_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(rebotesLayout.createSequentialGroup()
                                            .addComponent(asistencias)
                                            .addGap(49, 49, 49)
                                            .addComponent(asistencias_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(FaltasRecibidas_Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(rebotesLayout.createSequentialGroup()
                                .addGap(79, 79, 79)
                                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(faltas_recibidas)
                                    .addGroup(rebotesLayout.createSequentialGroup()
                                        .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(tiros_fallados)
                                            .addComponent(tapones_a_favor)
                                            .addComponent(tiros_libres_fallados)
                                            .addComponent(tapones_recibidos)
                                            .addComponent(faltas_realizadas))
                                        .addGap(52, 52, 52)
                                        .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(tiros_falladas_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tiros_libres_fallados_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tapones_a_favor_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tapones_recibidos_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(faltas_realizadas_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                    .addGroup(rebotesLayout.createSequentialGroup()
                        .addGap(183, 183, 183)
                        .addComponent(Guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(216, Short.MAX_VALUE))
        );
        rebotesLayout.setVerticalGroup(
            rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rebotesLayout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(rebotes_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(asistencias)
                    .addComponent(asistencias_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(robos)
                    .addComponent(robos_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(perdidas)
                    .addComponent(perdidas_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(faltas_recibidas)
                    .addComponent(FaltasRecibidas_Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tiros_falladas_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tiros_fallados))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tapones_a_favor)
                    .addComponent(tapones_a_favor_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tiros_libres_fallados)
                    .addComponent(tiros_libres_fallados_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tapones_recibidos)
                    .addComponent(tapones_recibidos_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(faltas_realizadas)
                    .addComponent(faltas_realizadas_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(Guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedTiros.addTab("Más Datos", rebotes);

        jLayeredPane1.setBackground(new java.awt.Color(204, 204, 204));
        jLayeredPane1.setOpaque(true);
        jLayeredPane1.setPreferredSize(new java.awt.Dimension(605, 357));

        jLabel5.setText("Tiros Realizados");

        tiros_libres_realizados.setText("Tiros libres realizados");

        tiros_libres_encestados.setText("Tiros libres metidos");

        tiros_encestados_2.setText("Tiros encestados de 2");

        tiros_realizados_2.setText("Tiros realizados de 2");

        tiros_realizados_3.setText("Tiros realizados de 3");

        tiros_encestados_3.setText("Tiros encestados de 3");

        jugador.setText("Jugador");

        Equipo.setText("Equipo");

        jugadores.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        equipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));
        equipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                equipoActionPerformed(evt);
            }
        });

        jLayeredPane1.setLayer(jLabel5, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jSpinnertirosRealizados, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(tiros_libres_realizados, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(tiros_realizados_libres_spinner, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(tiros_libres_encestados, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(tiros_encestados_libres_spinner, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(tiros_encestados_2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(tiros_encestados_2_spinner, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(tiros_realizados_2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(tiros_realizados_2_spinner, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(tiros_realizados_3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(tiros_encestados_3_spinner, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(tiros_encestados_3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(tiros_realizados_3_spinner, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jugador, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(Equipo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jugadores, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(equipo, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jugador, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jugadores, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Equipo))
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addGap(108, 108, 108)
                        .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tiros_realizados_2, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                                .addGap(185, 185, 185)
                                .addComponent(equipo, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                                .addGap(149, 149, 149)
                                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jSpinnertirosRealizados, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(tiros_encestados_3_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(tiros_realizados_3_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(tiros_realizados_2_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(tiros_realizados_libres_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tiros_encestados_2_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tiros_encestados_libres_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(tiros_encestados_3, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tiros_realizados_3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tiros_encestados_2, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tiros_libres_realizados, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tiros_libres_encestados, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(180, Short.MAX_VALUE))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jugador, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jugadores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Equipo)
                    .addComponent(equipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSpinnertirosRealizados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tiros_realizados_3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tiros_realizados_3_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tiros_encestados_3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tiros_encestados_3_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tiros_realizados_2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tiros_realizados_2_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tiros_encestados_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tiros_encestados_2_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tiros_libres_realizados, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tiros_realizados_libres_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tiros_libres_encestados, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tiros_encestados_libres_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(86, Short.MAX_VALUE))
        );

        jTabbedTiros.addTab("Tiros", jLayeredPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedTiros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTabbedTiros)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GuardarActionPerformed

    
    }//GEN-LAST:event_GuardarActionPerformed

    private void equipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_equipoActionPerformed
    }//GEN-LAST:event_equipoActionPerformed

    
  
  
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(nbaestadicsticas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(nbaestadicsticas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(nbaestadicsticas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(nbaestadicsticas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new nbaestadicsticas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Equipo;
    private javax.swing.JSpinner FaltasRecibidas_Spinner;
    private javax.swing.JButton Guardar;
    private javax.swing.JLabel asistencias;
    private javax.swing.JSpinner asistencias_spinner;
    private javax.swing.JComboBox<String> equipo;
    private javax.swing.JLabel faltas_realizadas;
    private javax.swing.JSpinner faltas_realizadas_spinner;
    private javax.swing.JLabel faltas_recibidas;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JSpinner jSpinnertirosRealizados;
    private javax.swing.JTabbedPane jTabbedTiros;
    private javax.swing.JLabel jugador;
    private javax.swing.JComboBox<String> jugadores;
    private javax.swing.JLabel perdidas;
    private javax.swing.JSpinner perdidas_spinner;
    private javax.swing.JPanel rebotes;
    private javax.swing.JSpinner rebotes_spinner;
    private javax.swing.JLabel robos;
    private javax.swing.JSpinner robos_spinner;
    private javax.swing.JLabel tapones_a_favor;
    private javax.swing.JSpinner tapones_a_favor_spinner;
    private javax.swing.JLabel tapones_recibidos;
    private javax.swing.JSpinner tapones_recibidos_spinner;
    private javax.swing.JLabel tiros_encestados_2;
    private javax.swing.JSpinner tiros_encestados_2_spinner;
    private javax.swing.JLabel tiros_encestados_3;
    private javax.swing.JSpinner tiros_encestados_3_spinner;
    private javax.swing.JSpinner tiros_encestados_libres_spinner;
    private javax.swing.JSpinner tiros_falladas_spinner;
    private javax.swing.JLabel tiros_fallados;
    private javax.swing.JLabel tiros_libres_encestados;
    private javax.swing.JLabel tiros_libres_fallados;
    private javax.swing.JSpinner tiros_libres_fallados_spinner;
    private javax.swing.JLabel tiros_libres_realizados;
    private javax.swing.JLabel tiros_realizados_2;
    private javax.swing.JSpinner tiros_realizados_2_spinner;
    private javax.swing.JLabel tiros_realizados_3;
    private javax.swing.JSpinner tiros_realizados_3_spinner;
    private javax.swing.JSpinner tiros_realizados_libres_spinner;
    // End of variables declaration//GEN-END:variables
}
