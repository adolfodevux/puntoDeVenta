package views.auth;

import models.User;
import utils.SessionManager;
import utils.UIUtils;
import views.dashboard.DashboardFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * Ventana de Login - Réplica exacta del login web
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton togglePasswordButton;
    private JCheckBox rememberMeCheckbox;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel errorLabel;
    private User userModel;
    
    public LoginFrame() {
        this.userModel = new User();
        initComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initComponents() {
        setTitle("Punto-D - Sistema de Punto de Venta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 600); // Reducir ancho y altura
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
        usernameField = createCustomTextField("Usuario o Email");
        passwordField = createCustomPasswordField("Contraseña");
        togglePasswordButton = createTogglePasswordButton();
        rememberMeCheckbox = new JCheckBox("Recordarme");
        rememberMeCheckbox.setFont(UIUtils.LABEL_FONT);
        rememberMeCheckbox.setForeground(UIUtils.TEXT_COLOR);
        
        loginButton = UIUtils.createPrimaryButton("Iniciar Sesión");
        registerButton = UIUtils.createSecondaryButton("Crear nueva cuenta");
        
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
        int panelHeight = 400;
        int x = (450 - panelWidth) / 2; // Centrar horizontalmente
        int y = (700 - panelHeight) / 2 - 50; // Centrar verticalmente y subir un poco
        
        mainPanel.setBounds(x, y, panelWidth, panelHeight);
        
        // Header con logo y título
        JPanel headerPanel = createHeaderPanel();
        
        // Panel de formulario
        JPanel formPanel = createFormPanel();
        
        // Panel de botones
        JPanel buttonPanel = createButtonPanel();
        
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(10));
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
        JLabel subtitleLabel = UIUtils.createSubtitleLabel("Sistema de Punto de Venta");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createVerticalStrut(5)); // Reducir más el espacio
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(2)); // Reducir más el espacio
        headerPanel.add(subtitleLabel);
        
        return headerPanel;
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(UIUtils.CARD_COLOR);
        
        // Campo usuario
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Panel para contraseña con botón de toggle
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setBackground(UIUtils.CARD_COLOR);
        passwordPanel.setMaximumSize(new Dimension(200, 35));
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(togglePasswordButton, BorderLayout.EAST);
        passwordPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Checkbox recordarme
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkboxPanel.setBackground(UIUtils.CARD_COLOR);
        checkboxPanel.add(rememberMeCheckbox);
        
        // Label de error
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(10)); // Reducir espacio
        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(5)); // Reducir espacio
        formPanel.add(checkboxPanel);
        formPanel.add(Box.createVerticalStrut(5)); // Reducir espacio
        formPanel.add(errorLabel);
        
        return formPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(UIUtils.CARD_COLOR);
        
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Separador
        JLabel separatorLabel = new JLabel("¿No tienes cuenta?", SwingConstants.CENTER);
        separatorLabel.setFont(UIUtils.LABEL_FONT);
        separatorLabel.setForeground(UIUtils.TEXT_SECONDARY);
        separatorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createVerticalStrut(10)); // Reducir espacio
        buttonPanel.add(separatorLabel);
        buttonPanel.add(Box.createVerticalStrut(5)); // Reducir espacio
        buttonPanel.add(registerButton);
        
        return buttonPanel;
    }
    
    private void setupEventListeners() {
        // Botón de login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        // Botón de registro
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterWindow();
            }
        });
        
        // Enter en los campos
        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> performLogin());
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());
        
        // Validaciones básicas
        if (username.isEmpty() || username.equals("Usuario o Email")) {
            showError("Por favor, ingrese su usuario o email");
            return;
        }
        
        if (password.isEmpty() || password.equals("Contraseña")) {
            showError("Por favor, ingrese su contraseña");
            return;
        }
        
        // Realizar login
        loginButton.setEnabled(false);
        loginButton.setText("Iniciando...");
        
        // Ejecutar en hilo separado para no bloquear UI
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<Map<String, Object>, Void>() {
            @Override
            protected Map<String, Object> doInBackground() throws Exception {
                return userModel.login(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    Map<String, Object> result = get();
                    boolean success = (Boolean) result.get("success");
                    String message = (String) result.get("message");
                    
                    if (success) {
                        // Login exitoso
                        @SuppressWarnings("unchecked")
                        Map<String, Object> userData = (Map<String, Object>) result.get("user");
                        SessionManager.getInstance().login(userData);
                        
                        
                        
                        // Abrir dashboard
                        openDashboard();
                        
                    } else {
                        // Error en login
                        showError(message);
                    }
                    
                } catch (Exception ex) {
                    showError("Error interno del servidor");
                    ex.printStackTrace();
                }
                
                // Restaurar botón
                loginButton.setEnabled(true);
                loginButton.setText("Iniciar Sesión");
            }
        };
        
        worker.execute();
    }
    
    private void openRegisterWindow() {
        RegisterFrame registerFrame = new RegisterFrame();
        registerFrame.setVisible(true);
        this.dispose();
    }
    
    private void openDashboard() {
        DashboardFrame dashboardFrame = new DashboardFrame();
        dashboardFrame.setVisible(true);
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
    private JButton createTogglePasswordButton() {
        JButton button = new JButton("👁");
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        button.setPreferredSize(new Dimension(30, 35));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, new Color(189, 195, 199))); // Solo border derecho, arriba y abajo
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> {
            if (passwordField.getEchoChar() == 0) {
                // Si está visible, ocultarla
                passwordField.setEchoChar('•');
                button.setText("👁");
            } else {
                // Si está oculta, mostrarla
                passwordField.setEchoChar((char) 0);
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
