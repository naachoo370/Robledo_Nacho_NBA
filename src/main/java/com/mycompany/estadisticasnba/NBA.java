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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author nacho
 */
public class NBA extends javax.swing.JFrame {
    

    /**
     * Creates new form NBA
     */
    public NBA() {
        initComponents();
        this.setLocationRelativeTo(null);
    }

    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    
    private void calcularYGenerarInforme() {
        try {
            String nombreJugador = mostrar_nombre.getText();
            int tirosCompletados = (int) mostrar_tirosrealixasos.getValue();
            int tirosDeDos = (int) mostrar_tirosdedos.getValue();
            int tirosDeTres = (int) mostar_tirosdetres.getValue();

            if (nombreJugador.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor ingrese el nombre del jugador.");
                return;
            }

            if (tirosCompletados == 0) {
                JOptionPane.showMessageDialog(null, "Los tiros realizados no pueden ser 0.");
                return;
            }

            double porcentajeTirosCampo = ((double) (tirosDeDos + tirosDeTres) / tirosCompletados) * 100;
            double porcentajeTirosEfectivos = ((double) (tirosDeDos + 1.5 * tirosDeTres) / tirosCompletados) * 100;

            generarExcelInforme("C:\\Users\\nacho\\Documents\\EstadisticasNBA.xlsx", nombreJugador, tirosCompletados, tirosDeDos, tirosDeTres, porcentajeTirosCampo, porcentajeTirosEfectivos);

            JOptionPane.showMessageDialog(null, "Se ha creado correctamente el exel: EstadisticasNBA.xlsx");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    private void generarExcelInforme(String archivoRuta, String nombreJugador, int tirosCompletados, int tirosDeDos, int tirosDeTres, double porcentajeTirosCampo, double porcentajeTirosEfectivos) throws IOException {
        Workbook archivoLibro;
        Sheet hoja;

        File archivoExcel = new File(archivoRuta);

        if (archivoExcel.exists()) {
            FileInputStream fis = new FileInputStream(archivoExcel);
            archivoLibro = new XSSFWorkbook(fis);
            hoja = archivoLibro.getSheetAt(0); // Obtener la primera hoja
            fis.close();
        } else {
            archivoLibro = new XSSFWorkbook();
            hoja = archivoLibro.createSheet("Estadísticas");

            Row encabezadoFila = hoja.createRow(0);
            encabezadoFila.createCell(0).setCellValue("Nombre del Jugador");
            encabezadoFila.createCell(1).setCellValue("Tiros Realizados");
            encabezadoFila.createCell(2).setCellValue("Tiros Metidos de 2");
            encabezadoFila.createCell(3).setCellValue("Tiros Metidos de 3");
            encabezadoFila.createCell(4).setCellValue("% Tiros de Campo (FG)");
            encabezadoFila.createCell(5).setCellValue("% Tiros Efectivos (eFG)");
        }

        int siguienteFila = hoja.getLastRowNum() + 1;

        Row filaDatos = hoja.createRow(siguienteFila);
        filaDatos.createCell(0).setCellValue(nombreJugador);
        filaDatos.createCell(1).setCellValue(tirosCompletados);
        filaDatos.createCell(2).setCellValue(tirosDeDos);
        filaDatos.createCell(3).setCellValue(tirosDeTres);
        filaDatos.createCell(4).setCellValue(porcentajeTirosCampo);
        filaDatos.createCell(5).setCellValue(porcentajeTirosEfectivos);

        for (int i = 0; i <= 5; i++) {
            hoja.autoSizeColumn(i);
        }

        try (FileOutputStream archivoSalida = new FileOutputStream(archivoRuta)) {
            archivoLibro.write(archivoSalida);
        }
        archivoLibro.close();
    }

    
    @SuppressWarnings("unchecked")
    
    
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        calcular = new javax.swing.JToggleButton();
        tirosrealizados = new javax.swing.JLabel();
        nombredeljugador = new javax.swing.JLabel();
        mostrar_tirosrealixasos = new javax.swing.JSpinner();
        mostrar_tirosdedos = new javax.swing.JSpinner();
        mostar_tirosdetres = new javax.swing.JSpinner();
        tirosmetidosdetres = new javax.swing.JLabel();
        tirosmetidosdedos = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mostrar_nombre = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));

        calcular.setText("Calcular");
        calcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcularActionPerformed(evt);
            }
        });

        tirosrealizados.setText("Tiros realizados");

        nombredeljugador.setBackground(new java.awt.Color(255, 255, 255));
        nombredeljugador.setText("Nombre del jugador");

        tirosmetidosdetres.setText("Tiros  metidos se tres");

        tirosmetidosdedos.setText("Tiros metidos de dos");

        jScrollPane1.setViewportView(mostrar_nombre);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tirosmetidosdetres, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tirosrealizados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nombredeljugador, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                            .addComponent(tirosmetidosdedos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(85, 85, 85)
                                .addComponent(mostrar_tirosrealixasos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(87, 87, 87)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(mostrar_tirosdedos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(mostar_tirosdetres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(98, 98, 98)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(calcular, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(45, 45, 45))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(nombredeljugador, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(mostrar_tirosrealixasos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tirosrealizados, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tirosmetidosdedos, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mostrar_tirosdedos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tirosmetidosdetres, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mostar_tirosdetres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(69, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(calcular)
                        .addGap(20, 20, 20))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void calcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcularActionPerformed
        calcularYGenerarInforme();
    }//GEN-LAST:event_calcularActionPerformed

    
    
    

  
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
            java.util.logging.Logger.getLogger(NBA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NBA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NBA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NBA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NBA().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton calcular;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner mostar_tirosdetres;
    private javax.swing.JTextPane mostrar_nombre;
    private javax.swing.JSpinner mostrar_tirosdedos;
    private javax.swing.JSpinner mostrar_tirosrealixasos;
    private javax.swing.JLabel nombredeljugador;
    private javax.swing.JLabel tirosmetidosdedos;
    private javax.swing.JLabel tirosmetidosdetres;
    private javax.swing.JLabel tirosrealizados;
    // End of variables declaration//GEN-END:variables
}
