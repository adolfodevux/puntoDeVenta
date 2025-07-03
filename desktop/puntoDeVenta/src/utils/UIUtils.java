package utils;

import javax.swing.*;
import java.awt.*;

/**
 * Utilidades para crear componentes UI consistentes
 * Réplica del estilo visual de la aplicación web
 */
public class UIUtils {
    
    // Colores del tema (basados en el CSS web)
    public static final Color PRIMARY_COLOR = new Color(52, 152, 219); // #3498db
    public static final Color PRIMARY_DARK = new Color(41, 128, 185); // #2980b9
    public static final Color SUCCESS_COLOR = new Color(39, 174, 96); // #27ae60
    public static final Color ERROR_COLOR = new Color(231, 76, 60); // #e74c3c
    public static final Color WARNING_COLOR = new Color(243, 156, 18); // #f39c12
    public static final Color BACKGROUND_COLOR = new Color(236, 240, 241); // #ecf0f1
    public static final Color CARD_COLOR = Color.WHITE;
    public static final Color TEXT_COLOR = new Color(44, 62, 80); // #2c3e50
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141); // #7f8c8d
    
    // Fuentes
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    /**
     * Crear un botón estilizado
     */
    public static JButton createStyledButton(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 40)); // Tamaño normal
        
        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = backgroundColor;
            
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.darker());
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });
        
        return button;
    }
    
    /**
     * Crear un botón primario
     */
    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, PRIMARY_COLOR, Color.WHITE);
    }
    
    /**
     * Crear un botón secundario
     */
    public static JButton createSecondaryButton(String text) {
        return createStyledButton(text, TEXT_SECONDARY, Color.WHITE);
    }
    
    /**
     * Crear un campo de texto estilizado
     */
    public static JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setFont(INPUT_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        textField.setPreferredSize(new Dimension(220, 35)); // Reducir más el ancho
        
        // Placeholder
        textField.setText(placeholder);
        textField.setForeground(TEXT_SECONDARY);
        
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(TEXT_COLOR);
                }
            }
            
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(TEXT_SECONDARY);
                }
            }
        });
        
        return textField;
    }
    
    /**
     * Crear un campo de contraseña estilizado
     */
    public static JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(INPUT_FONT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        passwordField.setPreferredSize(new Dimension(220, 35)); // Reducir más el ancho
        passwordField.setEchoChar((char) 0); // Mostrar texto inicialmente
        
        // Placeholder
        passwordField.setText(placeholder);
        passwordField.setForeground(TEXT_SECONDARY);
        
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (String.valueOf(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setForeground(TEXT_COLOR);
                    passwordField.setEchoChar('•'); // Activar ocultación
                }
            }
            
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (passwordField.getPassword().length == 0) {
                    passwordField.setText(placeholder);
                    passwordField.setForeground(TEXT_SECONDARY);
                    passwordField.setEchoChar((char) 0); // Mostrar placeholder
                }
            }
        });
        
        return passwordField;
    }
    
    /**
     * Crear una etiqueta de título
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(TITLE_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }
    
    /**
     * Crear una etiqueta de subtítulo
     */
    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(SUBTITLE_FONT);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }
    
    /**
     * Crear una etiqueta de error
     */
    public static JLabel createErrorLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(ERROR_COLOR);
        return label;
    }
    
    /**
     * Mostrar mensaje de éxito
     */
    public static void showSuccessMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Mostrar mensaje de error
     */
    public static void showErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Centrar ventana en pantalla
     */
    public static void centerWindow(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = window.getSize();
        int x = (screenSize.width - windowSize.width) / 2;
        int y = (screenSize.height - windowSize.height) / 2;
        window.setLocation(x, y);
    }
    
    /**
     * Cargar imagen desde recursos
     */
    public static ImageIcon loadImage(String imagePath, int width, int height) {
        try {
            java.net.URL imageURL = null;
            
            // Primer intento: cargar desde el classpath con /assets/
            imageURL = UIUtils.class.getResource("/assets/" + imagePath);
            
            // Segundo intento: cargar desde el classpath sin /assets/
            if (imageURL == null) {
                imageURL = UIUtils.class.getResource("/" + imagePath);
            }
            
            // Tercer intento: cargar desde el ClassLoader
            if (imageURL == null) {
                imageURL = UIUtils.class.getClassLoader().getResource("assets/" + imagePath);
            }
            
            // Cuarto intento: cargar desde el ClassLoader sin assets/
            if (imageURL == null) {
                imageURL = UIUtils.class.getClassLoader().getResource(imagePath);
            }
            
            // Quinto intento: cargar desde el directorio bin/assets/ directamente
            if (imageURL == null) {
                try {
                    java.io.File imageFile = new java.io.File("bin/assets/" + imagePath);
                    if (imageFile.exists()) {
                        imageURL = imageFile.toURI().toURL();
                    }
                } catch (Exception fileEx) {
                    // Continuar con el siguiente intento
                }
            }
            
            // Sexto intento: cargar desde el directorio src/assets/ directamente
            if (imageURL == null) {
                try {
                    java.io.File imageFile = new java.io.File("src/assets/" + imagePath);
                    if (imageFile.exists()) {
                        imageURL = imageFile.toURI().toURL();
                    }
                } catch (Exception fileEx) {
                    // Continuar con el siguiente intento
                }
            }
            
            if (imageURL != null) {
                ImageIcon originalIcon = new ImageIcon(imageURL);
                Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            } else {
                System.err.println("No se pudo cargar la imagen: " + imagePath);
                System.err.println("Ruta actual de trabajo: " + System.getProperty("user.dir"));
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen " + imagePath + ": " + e.getMessage());
            return null;
        }
    }
}
