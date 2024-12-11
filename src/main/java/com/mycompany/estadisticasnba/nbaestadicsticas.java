/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.estadisticasnba;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JOptionPane;
import java.util.logging.Level;
import java.util.logging.Logger;

// Apache POI para trabajar con archivos Excel
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// JFreeChart para gráficos
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

// iText para trabajar con PDFs
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;





/**
 *
 * @author nacho
 */

public class nbaestadicsticas extends javax.swing.JFrame {

    // Define equipos y jugadores
    String[] Atm = {"Antoine Griezmann", "Koke Resurrección", "Pablo Barrios Rivas", "Julián Álvarez", "José María Giménez"};
    String[] Dam = {"Nacho Robledo", "Sergio Martin", "Victor Ruiz", "Juan Marin", "Alba Gonzalez"};
    
    private List<letraconfg> variables;
    private Terminos aceptas = new Terminos();


    public nbaestadicsticas() {
        initComponents();
        this.setLocationRelativeTo(null);
        equipo.addItem("Atm");  // Añadir equipo Atlético de Madrid
        equipo.addItem("Dam");  // Añadir equipo DAM
        equipo.addActionListener(evt -> seleccionarEquipo());
        Guardar.addActionListener(evt -> crearExcel());
        graficos.addActionListener(evt -> generarGrafico());
        pdf.addActionListener(evt -> pdf());
        setResizable(false);
        
        configurarEtiquetas();
        configurarMenu();
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
            Medias(filePath);

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

    private void Medias(String filePath) throws IOException {
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
    
    
    private void generarGrafico() {
    String equipoSeleccionado = (String) equipo.getSelectedItem();
    String jugadorSeleccionado = (String) jugadores.getSelectedItem();

    if (jugadorSeleccionado == null) {
        JOptionPane.showMessageDialog(this, "Por favor, selecciona un jugador.");
        return;
    }

    String rutaArchivo = "C:\\Users\\nacho\\OneDrive\\Desarrollo de interzaces\\NETBEANS\\EstadisticasNBA\\EstadisticasNBA_" + equipoSeleccionado + ".xlsx";

    try (FileInputStream fis = new FileInputStream(new File(rutaArchivo))) {
        Workbook libroExcel = new XSSFWorkbook(fis);
        Sheet hojaJugador = libroExcel.getSheet(jugadorSeleccionado);

        if (hojaJugador == null || hojaJugador.getPhysicalNumberOfRows() <= 1) {
            JOptionPane.showMessageDialog(this, "No hay datos suficientes para el jugador seleccionado.");
            return;
        }

        // Crear listas para almacenar las estadísticas
        ArrayList<Integer> puntos = new ArrayList<>();
        ArrayList<Integer> rebotes = new ArrayList<>();
        ArrayList<Integer> asistencias = new ArrayList<>();

        for (Row fila : hojaJugador) {
            if (fila.getRowNum() == 0) continue; // Saltar la fila del encabezado

            int valorPuntos = (int) obtenerValorCelda(fila.getCell(10));
            int valorRebotes = (int) obtenerValorCelda(fila.getCell(11));
            int valorAsistencias = (int) obtenerValorCelda(fila.getCell(12));

            puntos.add(valorPuntos);
            rebotes.add(valorRebotes);
            asistencias.add(valorAsistencias);
        }

        // Calcular la media de puntos
        double mediaPuntos = calcularMedia(puntos);

        // Crear gráficos
        JFreeChart graficoPuntos = crearGraficoConMedia(puntos, jugadorSeleccionado, mediaPuntos, "Puntos");
        JFreeChart graficoRebotes = crearGrafico(rebotes, jugadorSeleccionado, "Rebotes por Partido");
        JFreeChart graficoAsistencias = crearGrafico(asistencias, jugadorSeleccionado, "Asistencias por Partido");

        // Crear carpetas para guardar las gráficas
        String carpetaBase = "C:\\Users\\nacho\\OneDrive\\Desarrollo de interzaces\\NETBEANS\\EstadisticasNBA\\graficas\\";
        String carpetaJugador = carpetaBase + jugadorSeleccionado + "\\";

        new File(carpetaJugador).mkdirs();

        // Guardar gráficos
        ChartUtils.saveChartAsJPEG(new File(carpetaJugador + "Puntos.jpg"), graficoPuntos, 800, 600);
        ChartUtils.saveChartAsJPEG(new File(carpetaJugador + "Rebotes.jpg"), graficoRebotes, 800, 600);
        ChartUtils.saveChartAsJPEG(new File(carpetaJugador + "Asistencias.jpg"), graficoAsistencias, 800, 600);

        JOptionPane.showMessageDialog(this, "Gráficas guardadas en: " + carpetaJugador);
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error al leer el archivo Excel: " + e.getMessage());
    }
}

    private double obtenerValorCelda(Cell celda) {
        return celda != null && celda.getCellType() == CellType.NUMERIC ? celda.getNumericCellValue() : 0;
    }

    private double calcularMedia(ArrayList<Integer> valores) {
        int suma = 0;
        for (int valor : valores) {
            suma += valor;
        }
        return (double) suma / valores.size();
    }

    private JFreeChart crearGraficoConMedia(ArrayList<Integer> datos, String jugador, double media, String titulo) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < datos.size(); i++) {
            dataset.addValue(datos.get(i), titulo, "Partido " + (i + 1));
        }

        DefaultCategoryDataset datasetMedia = new DefaultCategoryDataset();
        for (int i = 0; i < datos.size(); i++) {
            datasetMedia.addValue(media, "Media", "Partido " + (i + 1));
        }

        JFreeChart chart = ChartFactory.createBarChart(
                titulo + " de " + jugador,
                "Partido",
                titulo,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();

        // Añadir la línea de la media
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        plot.setDataset(1, datasetMedia);
        plot.mapDatasetToRangeAxis(1, 0);
        plot.setRenderer(1, renderer);

        return chart;
    }

    private JFreeChart crearGrafico(ArrayList<Integer> datos, String jugador, String titulo) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < datos.size(); i++) {
            dataset.addValue(datos.get(i), titulo, "Partido " + (i + 1));
        }

        return ChartFactory.createLineChart(
                titulo + " de " + jugador,
                "Partido",
                titulo,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }
    
    // Método para generar el PDF
    private void pdf() {
        String jugadorSeleccionado = (String) jugadores.getSelectedItem();
        String equipoSeleccionado = (String) equipo.getSelectedItem();

        if (jugadorSeleccionado == null || equipoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un jugador y un equipo.");
            return;
        }

        try {
            // Ruta base para los PDFs
            String carpetaBasePDF = "C:\\Users\\nacho\\OneDrive\\Desarrollo de interzaces\\NETBEANS\\EstadisticasNBA\\PDF\\";
            if (!verificarOCrearCarpeta(carpetaBasePDF)) {
                JOptionPane.showMessageDialog(this, "No se pudo crear la carpeta base para los PDFs.");
                return;
            }

            // Crear la carpeta del equipo si no existe
            String carpetaEquipo = carpetaBasePDF + equipoSeleccionado + "\\";
            if (!verificarOCrearCarpeta(carpetaEquipo)) {
                JOptionPane.showMessageDialog(this, "No se pudo crear la carpeta para el equipo: " + carpetaEquipo);
                return;
            }

            // Ruta final del PDF
            String rutaPDF = carpetaEquipo + jugadorSeleccionado + ".pdf";
            System.out.println("Ruta final del PDF: " + rutaPDF);

            // Configurar y crear el documento PDF
            PdfWriter writer = new PdfWriter(new FileOutputStream(rutaPDF));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4, false);
            document.setMargins(10, 10, 10, 10);

            // Añadir título al PDF
            Paragraph titulo = new Paragraph("Estadísticas de " + jugadorSeleccionado + " - " + equipoSeleccionado)
                    .setFontSize(16)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);
            document.add(new Paragraph("\n"));

            // Añadir gráficos y estadísticas al PDF
            agregarGraficosAlPDF(document, jugadorSeleccionado);
            agregarOtrasEstadisticas(document, jugadorSeleccionado, equipoSeleccionado);

            document.close();

            JOptionPane.showMessageDialog(this, "PDF de: " + jugadorSeleccionado + " creado exitosamente.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar el PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para verificar y crear las carpetas necesarias
    private boolean verificarOCrearCarpeta(String ruta) {
        File carpeta = new File(ruta);
        if (!carpeta.exists()) {
            return carpeta.mkdirs();
        }
        return true;
    }

    // Método para agregar gráficos al PDF
    private void agregarGraficosAlPDF(Document document, String jugadorSeleccionado) throws IOException {
        // Ruta de la carpeta donde se encuentran las imágenes de los gráficos
        String carpetaGraficos = "C:\\Users\\nacho\\OneDrive\\Desarrollo de interzaces\\NETBEANS\\EstadisticasNBA\\graficas\\" + jugadorSeleccionado + "\\";

        try {
            // Cargar y añadir el gráfico de puntos
            ImageData graficoPuntos = ImageDataFactory.create(carpetaGraficos + "Puntos.jpg");
            Image graficoPuntosImage = new Image(graficoPuntos);
            graficoPuntosImage.scaleToFit(300, 200);
            document.add(new Paragraph("Gráfico de Puntos:"));
            document.add(graficoPuntosImage);

            // Cargar y añadir el gráfico de rebotes
            ImageData graficoRebotes = ImageDataFactory.create(carpetaGraficos + "Rebotes.jpg");
            Image graficoRebotesImage = new Image(graficoRebotes);
            graficoRebotesImage.scaleToFit(300, 200);
            document.add(new Paragraph("Gráfico de Rebotes:"));
            document.add(graficoRebotesImage);

            // Cargar y añadir el gráfico de asistencias
            ImageData graficoAsistencias = ImageDataFactory.create(carpetaGraficos + "Asistencias.jpg");
            Image graficoAsistenciasImage = new Image(graficoAsistencias);
            graficoAsistenciasImage.scaleToFit(300, 200);
            document.add(new Paragraph("Gráfico de Asistencias:"));
            document.add(graficoAsistenciasImage);

        } catch (IOException e) {
            // Manejo de errores en caso de que no se encuentren las imágenes
            document.add(new Paragraph("No se encontraron gráficos para " + jugadorSeleccionado));
        }
    }

    private void agregarOtrasEstadisticas(Document document, String jugadorSeleccionado, String equipoSeleccionado) {
        String archivoExcel = Paths.get(System.getProperty("user.dir"), "EstadisticasNBA_" + equipoSeleccionado + ".xlsx").toString();

        try (FileInputStream fis = new FileInputStream(archivoExcel);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet hojaMedias = workbook.getSheet("Medias");
            if (hojaMedias == null) {
                document.add(new Paragraph("No se encontró la hoja 'Medias por jugador' en el archivo Excel."));
                return;
            }

            double mediaTriples = 0, mediaFG = 0, mediaEFG = 0, mediaTS = 0;
            boolean jugadorEncontrado = false;

            for (Row fila : hojaMedias) {
                if (fila.getRowNum() == 0) continue; // Saltar encabezado
                Cell celdaJugador = fila.getCell(0);
                if (celdaJugador != null && celdaJugador.getCellType() == CellType.STRING
                        && celdaJugador.getStringCellValue().equals(jugadorSeleccionado)) {

                    jugadorEncontrado = true;

                    // Asegurarse de que las celdas contienen valores válidos
                    mediaTriples = getNumericCellValue(fila.getCell(6));
                    mediaFG = getNumericCellValue(fila.getCell(7));
                    mediaEFG = getNumericCellValue(fila.getCell(8));
                    mediaTS = getNumericCellValue(fila.getCell(9));
                    break;
                }
            }

            if (jugadorEncontrado) {
                document.add(new Paragraph("Otras estadísticas de " + jugadorSeleccionado).setBold());
                document.add(new Paragraph(String.format("Triples metidos por partido: %.2f", mediaTriples)));
                document.add(new Paragraph(String.format("FG%%: %.2f%%    eFG%%: %.2f%%    TS%%: %.2f%%", mediaFG, mediaEFG, mediaTS)));
            } else {
                document.add(new Paragraph("No se encontraron estadísticas para el jugador " + jugadorSeleccionado));
            }

        } catch (FileNotFoundException e) {
            document.add(new Paragraph("Archivo Excel no encontrado: " + archivoExcel));
            e.printStackTrace();
        } catch (IOException e) {
            document.add(new Paragraph("Error al leer el archivo Excel: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    // Método para manejar celdas con valores numéricos o nulos
    private double getNumericCellValue(Cell cell) {
        if (cell == null) {
            return 0.0;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            default:
                return 0.0;
        }
    }
    
    


    private void configurarEtiquetas() {
    variables = new ArrayList<>();
    variables.add(letraconfg3); 
    variables.add(letraconfg3); 
    variables.add(letraconfg4); 
    variables.add(letraconfg5); 
    variables.add(letraconfg6); 
    variables.add(letraconfg7); 




 
}

    private void actualizarTamañoFuente(int size) {
            for (letraconfg etiqueta : variables) {
                etiqueta.changeSize(size);
            }
        }

    private void configurarMenu() {
            ButtonGroup grupoBotones = new ButtonGroup();

            // Añadir los botones al grupo
            grupoBotones.add(pequeño);
            grupoBotones.add(normal);
            grupoBotones.add(grande);

            // Añadir ActionListeners a los botones
            pequeño.addActionListener(e -> actualizarTamañoFuente(1));
            normal.addActionListener(e -> actualizarTamañoFuente(2));
            grande.addActionListener(e -> actualizarTamañoFuente(3));

            // Establecer el botón "normal" como predeterminado
            normal.setSelected(true);
        }
     
    
    
 
    
    
    
    
    
    
    
    
    
    
   

  
  
 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        letraconfg3 = new com.mycompany.estadisticasnba.letraconfg();
        jTabbedTiros = new javax.swing.JTabbedPane();
        rebotes = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        rebotes_spinner = new javax.swing.JSpinner();
        asistencias_spinner = new javax.swing.JSpinner();
        robos_spinner = new javax.swing.JSpinner();
        tapones_a_favor = new javax.swing.JLabel();
        tapones_a_favor_spinner = new javax.swing.JSpinner();
        tiros_fallados = new javax.swing.JLabel();
        tiros_libres_fallados = new javax.swing.JLabel();
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
        graficos = new javax.swing.JButton();
        pdf = new javax.swing.JButton();
        letraconfg4 = new com.mycompany.estadisticasnba.letraconfg();
        letraconfg5 = new com.mycompany.estadisticasnba.letraconfg();
        letraconfg6 = new com.mycompany.estadisticasnba.letraconfg();
        letraconfg7 = new com.mycompany.estadisticasnba.letraconfg();
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
        jMenuBar2 = new javax.swing.JMenuBar();
        tamaño1 = new javax.swing.JMenu();
        pequeño = new javax.swing.JRadioButtonMenuItem();
        normal = new javax.swing.JRadioButtonMenuItem();
        grande = new javax.swing.JRadioButtonMenuItem();
        condicionesServicio1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedTiros.setBackground(new java.awt.Color(204, 204, 204));

        rebotes.setBackground(new java.awt.Color(204, 204, 204));

        jLabel9.setBackground(new java.awt.Color(0, 0, 0));

        tapones_a_favor.setText("Tapones a Favor");

        tiros_fallados.setText("Tiros fallados");

        tiros_libres_fallados.setText("Tiros libres fallados");

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

        graficos.setText("grafico");
        graficos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graficosActionPerformed(evt);
            }
        });

        pdf.setText("PDF");

        letraconfg4.setText("Rebotes");
        letraconfg4.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        letraconfg5.setText("Rebotes");
        letraconfg5.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        letraconfg6.setText("Perdidas");
        letraconfg6.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        letraconfg7.setText("Asistencias");
        letraconfg7.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        javax.swing.GroupLayout rebotesLayout = new javax.swing.GroupLayout(rebotes);
        rebotes.setLayout(rebotesLayout);
        rebotesLayout.setHorizontalGroup(
            rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rebotesLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(pdf)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rebotesLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(Guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))
                    .addGroup(rebotesLayout.createSequentialGroup()
                        .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                                            .addComponent(faltas_realizadas_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(rebotesLayout.createSequentialGroup()
                                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(rebotesLayout.createSequentialGroup()
                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rebotesLayout.createSequentialGroup()
                                        .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(letraconfg4, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(letraconfg5, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)))
                                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rebotes_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(robos_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(49, 49, 49)
                                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(FaltasRecibidas_Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(graficos, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(rebotesLayout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(rebotesLayout.createSequentialGroup()
                                                .addComponent(letraconfg6, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(perdidas_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(rebotesLayout.createSequentialGroup()
                                                .addComponent(letraconfg7, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(asistencias_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                        .addGap(210, 210, Short.MAX_VALUE))))
        );
        rebotesLayout.setVerticalGroup(
            rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rebotesLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(rebotes_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(asistencias_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(letraconfg5, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(letraconfg7, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(robos_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(perdidas_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(letraconfg4, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(letraconfg6, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(faltas_recibidas)
                    .addComponent(FaltasRecibidas_Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
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
                .addGroup(rebotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(graficos)
                    .addComponent(Guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pdf))
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
                .addContainerGap(294, Short.MAX_VALUE))
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
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jTabbedTiros.addTab("Tiros", jLayeredPane1);

        tamaño1.setText("Tamaño fuente");
        tamaño1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tamaño1ActionPerformed(evt);
            }
        });

        pequeño.setSelected(true);
        pequeño.setText("Pequeño");
        pequeño.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pequeñoActionPerformed(evt);
            }
        });
        tamaño1.add(pequeño);

        normal.setSelected(true);
        normal.setText("Mediano");
        normal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                normalActionPerformed(evt);
            }
        });
        tamaño1.add(normal);

        grande.setSelected(true);
        grande.setText("Grande");
        tamaño1.add(grande);

        jMenuBar2.add(tamaño1);

        condicionesServicio1.setText("Condiciones de servicio");
        condicionesServicio1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                condicionesServicio1MouseClicked(evt);
            }
        });
        condicionesServicio1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                condicionesServicio1ActionPerformed(evt);
            }
        });

        jMenuItem1.setText("jMenuItem1");
        jMenuItem1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuItem1MouseClicked(evt);
            }
        });
        condicionesServicio1.add(jMenuItem1);

        jMenuBar2.add(condicionesServicio1);

        setJMenuBar(jMenuBar2);

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedTiros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void equipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_equipoActionPerformed
    }//GEN-LAST:event_equipoActionPerformed

    private void GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GuardarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_GuardarActionPerformed

    private void graficosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graficosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_graficosActionPerformed

    private void condicionesServicio1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_condicionesServicio1MouseClicked
        if (aceptas == null || !aceptas.isVisible()){
            aceptas = new Terminos();
            aceptas.setVisible(true);
        }

    }//GEN-LAST:event_condicionesServicio1MouseClicked

    private void tamaño1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tamaño1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tamaño1ActionPerformed

    private void condicionesServicio1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_condicionesServicio1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_condicionesServicio1ActionPerformed

    private void pequeñoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pequeñoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pequeñoActionPerformed

    private void normalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_normalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_normalActionPerformed

    private void jMenuItem1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem1MouseClicked

        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem1MouseClicked

    
  
  
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
    private javax.swing.JSpinner asistencias_spinner;
    private javax.swing.JMenu condicionesServicio1;
    private javax.swing.JComboBox<String> equipo;
    private javax.swing.JLabel faltas_realizadas;
    private javax.swing.JSpinner faltas_realizadas_spinner;
    private javax.swing.JLabel faltas_recibidas;
    private javax.swing.JButton graficos;
    private javax.swing.JRadioButtonMenuItem grande;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JSpinner jSpinnertirosRealizados;
    private javax.swing.JTabbedPane jTabbedTiros;
    private javax.swing.JLabel jugador;
    private javax.swing.JComboBox<String> jugadores;
    private com.mycompany.estadisticasnba.letraconfg letraconfg3;
    private com.mycompany.estadisticasnba.letraconfg letraconfg4;
    private com.mycompany.estadisticasnba.letraconfg letraconfg5;
    private com.mycompany.estadisticasnba.letraconfg letraconfg6;
    private com.mycompany.estadisticasnba.letraconfg letraconfg7;
    private javax.swing.JRadioButtonMenuItem normal;
    private javax.swing.JButton pdf;
    private javax.swing.JRadioButtonMenuItem pequeño;
    private javax.swing.JSpinner perdidas_spinner;
    private javax.swing.JPanel rebotes;
    private javax.swing.JSpinner rebotes_spinner;
    private javax.swing.JSpinner robos_spinner;
    private javax.swing.JMenu tamaño1;
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
