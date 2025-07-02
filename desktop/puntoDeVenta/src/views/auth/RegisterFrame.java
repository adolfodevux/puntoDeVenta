package views.auth;

import models.User;
import utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Ventana de Registro - Réplica exacta del registro web
 */
public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton togglePasswordButton;
    private JButton toggleConfirmPasswordButton;
    private JButton registerButton;
    private JButton backToLoginButton;
    private JLabel errorLabel;
    private User userModel;
    
    // Patrón para validar email
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    public RegisterFrame() {
        this.userModel = new User();
        initComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initComponents() {
        setTitle("Registro Punto-D");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 600); // Reducir tamaño
        setResizable(false);
        
        // Configurar Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Si Nimbus no está disponible, usar el por defecto
            e.printStackTrace();
        }
        
        // Crear componentes con tamaño específico
        usernameField = createCustomTextField("Usuario");
        emailField = createCustomTextField("Email");
        passwordField = createCustomPasswordField("Contraseña");
        confirmPasswordField = createCustomPasswordField("Confirmar Contraseña");
        togglePasswordButton = createTogglePasswordButton(passwordField);
        toggleConfirmPasswordButton = createTogglePasswordButton(confirmPasswordField);
        
        registerButton = UIUtils.createPrimaryButton("Crear Cuenta");
        backToLoginButton = UIUtils.createSecondaryButton("Volver al Login");
        
        errorLabel = UIUtils.createErrorLabel("");
        errorLabel.setVisible(false);
        
        // Centrar ventana
        UIUtils.centerWindow(this);
    }
    
    private void setupLayout() {
        setLayout(null); // Usar layout absoluto para control preciso
        getContentPane().setBackground(UIUtils.BACKGROUND_COLOR);
        
        // Panel principal centrado
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(UIUtils.CARD_COLOR);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        
        // Calcular posición centrada
        int panelWidth = 350;
        int panelHeight = 480;
        int x = (450 - panelWidth) / 2; // Centrar horizontalmente
        int y = (600 - panelHeight) / 2 - 30; // Centrar verticalmente y subir un poco
        
        mainPanel.setBounds(x, y, panelWidth, panelHeight);
        
        // Header con logo y título
        JPanel headerPanel = createHeaderPanel();
        
        // Panel de formulario
        JPanel formPanel = createFormPanel();
        
        // Panel de botones
        JPanel buttonPanel = createButtonPanel();
        
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(buttonPanel);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(UIUtils.CARD_COLOR);
        
        // Logo (imagen de Tux)
        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = UIUtils.loadImage("tux.png", 50, 50); // Proporciones normales
        if (logoIcon != null) {
            logoLabel.setIcon(logoIcon);
        } else {
            // Fallback al emoji si no se carga la imagen
            logoLabel.setText("🐧");
            logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50)); // Hacer más grande
        }
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Título
        JLabel titleLabel = UIUtils.createTitleLabel("Punto-D");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtítulo
        JLabel subtitleLabel = UIUtils.createSubtitleLabel("Crear Nueva Cuenta");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        
        return headerPanel;
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(UIUtils.CARD_COLOR);
        
        // Campo usuario
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Campo email
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Panel para contraseña con botón de toggle
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setBackground(UIUtils.CARD_COLOR);
        passwordPanel.setMaximumSize(new Dimension(200, 35));
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(togglePasswordButton, BorderLayout.EAST);
        passwordPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Panel para confirmar contraseña con botón de toggle
        JPanel confirmPasswordPanel = new JPanel(new BorderLayout());
        confirmPasswordPanel.setBackground(UIUtils.CARD_COLOR);
        confirmPasswordPanel.setMaximumSize(new Dimension(200, 35));
        confirmPasswordPanel.add(confirmPasswordField, BorderLayout.CENTER);
        confirmPasswordPanel.add(toggleConfirmPasswordButton, BorderLayout.EAST);
        confirmPasswordPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Label de error
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(12)); // Reducir espacio
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(12)); // Reducir espacio
        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(12)); // Reducir espacio
        formPanel.add(confirmPasswordPanel);
        formPanel.add(Box.createVerticalStrut(12)); // Reducir espacio
        formPanel.add(errorLabel);
        
        return formPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(UIUtils.CARD_COLOR);
        
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Separador
        JLabel separatorLabel = new JLabel("¿Ya tienes cuenta?", SwingConstants.CENTER);
        separatorLabel.setFont(UIUtils.LABEL_FONT);
        separatorLabel.setForeground(UIUtils.TEXT_SECONDARY);
        separatorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        backToLoginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        buttonPanel.add(registerButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(separatorLabel);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(backToLoginButton);
        
        return buttonPanel;
    }
    
    private void setupEventListeners() {
        // Botón de registro
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegister();
            }
        });
        
        // Botón volver al login
        backToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToLogin();
            }
        });
        
        // Navegación con Enter
        usernameField.addActionListener(e -> emailField.requestFocus());
        emailField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> confirmPasswordField.requestFocus());
        confirmPasswordField.addActionListener(e -> performRegister());
    }
    
    private void performRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());
        String confirmPassword = String.valueOf(confirmPasswordField.getPassword());
        
        // Validaciones
        if (!validateForm(username, email, password, confirmPassword)) {
            return;
        }
        
        // Realizar registro
        registerButton.setEnabled(false);
        registerButton.setText("Creando cuenta...");
        
        // Ejecutar en hilo separado para no bloquear UI
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<Map<String, Object>, Void>() {
            @Override
            protected Map<String, Object> doInBackground() throws Exception {
                return userModel.register(username, email, password);
            }
            
            @Override
            protected void done() {
                try {
                    Map<String, Object> result = get();
                    boolean success = (Boolean) result.get("success");
                    String message = (String) result.get("message");
                    
                    if (success) {
                        // Registro exitoso
                        UIUtils.showSuccessMessage(RegisterFrame.this, 
                            "Cuenta creada exitosamente. ¡Ahora puedes iniciar sesión!");
                        
                        // Volver al login
                        backToLogin();
                        
                    } else {
                        // Error en registro
                        showError(message);
                    }
                    
                } catch (Exception ex) {
                    showError("Error interno del servidor");
                    ex.printStackTrace();
                }
                
                // Restaurar botón
                registerButton.setEnabled(true);
                registerButton.setText("Crear Cuenta");
            }
        };
        
        worker.execute();
    }
    
    private boolean validateForm(String username, String email, String password, String confirmPassword) {
        // Validar username
        if (username.isEmpty() || username.equals("Usuario")) {
            showError("El nombre de usuario es requerido");
            return false;
        }
        
        if (username.length() < 3) {
            showError("El nombre de usuario debe tener al menos 3 caracteres");
            return false;
        }
        
        if (username.length() > 50) {
            showError("El nombre de usuario no puede tener más de 50 caracteres");
            return false;
        }
        
        // Validar email
        if (email.isEmpty() || email.equals("Email")) {
            showError("El email es requerido");
            return false;
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showError("Por favor, ingrese un email válido");
            return false;
        }
        
        // Validar contraseña
        if (password.isEmpty() || password.equals("Contraseña")) {
            showError("La contraseña es requerida");
            return false;
        }
        
        if (password.length() < 6) {
            showError("La contraseña debe tener al menos 6 caracteres");
            return false;
        }
        
        if (password.length() > 255) {
            showError("La contraseña no puede tener más de 255 caracteres");
            return false;
        }
        
        // Validar confirmación de contraseña
        if (confirmPassword.isEmpty() || confirmPassword.equals("Confirmar Contraseña")) {
            showError("Debe confirmar la contraseña");
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Las contraseñas no coinciden");
            return false;
        }
        
        return true;
    }
    
    private void backToLogin() {
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);
        this.dispose();
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        
        // Auto-ocultar después de 5 segundos
        Timer timer = new Timer(5000, e -> errorLabel.setVisible(false));
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Crear campo de texto personalizado con tamaño específico
     */
    private JTextField createCustomTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setFont(UIUtils.INPUT_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        textField.setPreferredSize(new Dimension(200, 35)); // Tamaño específico más pequeño
        textField.setMaximumSize(new Dimension(200, 35)); // Limitar tamaño máximo
        
        // Placeholder
        textField.setText(placeholder);
        textField.setForeground(UIUtils.TEXT_SECONDARY);
        
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(UIUtils.TEXT_COLOR);
                }
            }
            
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(UIUtils.TEXT_SECONDARY);
                }
            }
        });
        
        return textField;
    }
    
    /**
     * Crear campo de contraseña personalizado con tamaño específico
     */
    private JPasswordField createCustomPasswordField(String placeholder) {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(UIUtils.INPUT_FONT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 0, new Color(189, 195, 199)), // Sin border derecho
            BorderFactory.createEmptyBorder(8, 12, 8, 5)
        ));
        passwordField.setPreferredSize(new Dimension(170, 35)); // Reducir ancho para el botón
        passwordField.setMaximumSize(new Dimension(170, 35));
        passwordField.setEchoChar((char) 0); // Mostrar texto inicialmente
        
        // Placeholder
        passwordField.setText(placeholder);
        passwordField.setForeground(UIUtils.TEXT_SECONDARY);
        
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (String.valueOf(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setForeground(UIUtils.TEXT_COLOR);
                    passwordField.setEchoChar('•'); // Activar ocultación
                }
            }
            
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (passwordField.getPassword().length == 0) {
                    passwordField.setText(placeholder);
                    passwordField.setForeground(UIUtils.TEXT_SECONDARY);
                    passwordField.setEchoChar((char) 0); // Mostrar placeholder
                }
            }
        });
        
        return passwordField;
    }
    
    /**
     * Crear botón para mostrar/ocultar contraseña
     */
    private JButton createTogglePasswordButton(JPasswordField targetPasswordField) {
        JButton button = new JButton("👁");
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        button.setPreferredSize(new Dimension(30, 35));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, new Color(189, 195, 199))); // Solo border derecho, arriba y abajo
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> {
            if (targetPasswordField.getEchoChar() == 0) {
                // Si está visible, ocultarla
                targetPasswordField.setEchoChar('•');
                button.setText("👁");
            } else {
                // Si está oculta, mostrarla
                targetPasswordField.setEchoChar((char) 0);
                button.setText("🙈");
            }
        });
        
        return button;
    }

    @Override
    public void dispose() {
        if (userModel != null) {
            userModel.closeConnection();
        }
        super.dispose();
    }
}
