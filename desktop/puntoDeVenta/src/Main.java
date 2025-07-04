import views.auth.*;
import javax.swing.*;

/**
 * Clase principal del Sistema de Punto de Venta
 * Punto de entrada de la aplicación Java Swing
 */
public class Main {
    public static void main(String[] args) {
        // Configurar Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel del sistema: " + e.getMessage());
        }
        
        // Configurar propiedades del sistema para mejor renderizado
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Ejecutar la aplicación en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Crear y mostrar la ventana de login
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                
                System.out.println("=== Sistema de Punto de Venta tux ===");
                System.out.println("Aplicación iniciada correctamente");
                System.out.println("Autor: Sistema de Punto de Venta - Punto-D");
                System.out.println("Versión: 1.0.0");
                System.out.println("================================");
                
            } catch (Exception e) {
                System.err.println("Error al iniciar la aplicación: " + e.getMessage());
                e.printStackTrace();
                
                // Mostrar mensaje de error al usuario
                JOptionPane.showMessageDialog(
                    null,
                    "Error al iniciar la aplicación: " + e.getMessage(),
                    "Error de Inicio",
                    JOptionPane.ERROR_MESSAGE
                );
                
                // Salir de la aplicación
                System.exit(1);
            }
        });
    }
}
