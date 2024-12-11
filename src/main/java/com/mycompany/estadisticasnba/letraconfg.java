package com.mycompany.estadisticasnba;


import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;

public class letraconfg extends JLabel {
    
      public letraconfg( ){
        super("texto prede");
        configurarEstilo();
        
    }

    public letraconfg(String texto) {
        super(texto);
        configurarEstilo();
        
    }

    private void configurarEstilo() {
        setForeground(Color.WHITE); // Texto blanco
        setBackground(new Color(85, 130, 243)); // Fondo azul
        setOpaque(true); // Fondo visible
        setHorizontalAlignment(SwingConstants.CENTER); // Centrar el texto
        setFont(new Font("Verdana", Font.BOLD | Font.ITALIC, 18)); // Fuente predeterminada
        setBorder(BorderFactory.createLineBorder(new Color(60, 90, 200), 3)); // Borde azul
    }

    // Cambiar el tamaño de la fuente
    public void changeSize(int size) {
        switch (size) {
            case 1:
                setFont(new Font("Arial", Font.PLAIN, 12)); // Letra pequeña
                break;
            case 2:
                setFont(new Font("Courier New", Font.BOLD, 18)); // Letra mediana
                break;
            case 3:
                setFont(new Font("Georgia", Font.ITALIC, 24)); // Letra grande
                break;
            default:
                setFont(new Font("Times New Roman", Font.PLAIN, 18)); // Tamaño predeterminado
                break;
        }
    }
}
