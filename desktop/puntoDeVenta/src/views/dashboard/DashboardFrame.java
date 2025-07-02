package views.dashboard;

import utils.SessionManager;
import utils.UIUtils;
import views.auth.LoginFrame;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Dashboard principal - Réplica exacta del dashboard web
 */
public class DashboardFrame extends JFrame {
    private JPanel mainPanel;
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private JLabel usernameLabel;
    private JLabel dateTimeLabel;
    private Timer clockTimer;
    private SessionManager sessionManager;
    
    // Módulos del sistema
    private String currentModule = "pos";
    
    public DashboardFrame() {
        this.sessionManager = SessionManager.getInstance();
        
        // Verificar que el usuario esté logueado
        if (!sessionManager.isLoggedIn()) {
            // Redirigir al login si no está logueado
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            this.dispose();
            return;
        }
        
        initComponents();
        setupLayout();
        setupEventListeners();
        startClock();
        
        // Mostrar mensaje de bienvenida
        SwingUtilities.invokeLater(() -> {
            UIUtils.showSuccessMessage(this, 
                "¡Bienvenido " + sessionManager.getUsername() + "!");
        });
    }
    
    private void initComponents() {
        setTitle("Punto de Venta - Punto-D");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Obtener tamaño de la pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.9); // 90% del ancho
        int height = (int) (screenSize.height * 0.9); // 90% del alto
        
        setSize(width, height);
        setMinimumSize(new Dimension(1400, 700)); // Tamaño mínimo
        
        // Centrar en pantalla
        setLocationRelativeTo(null);
        
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
        
        // Crear componentes principales
        createSidebar();
        createMainContent();
    }
    
    private void createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setBackground(new Color(44, 62, 80)); // Color del sidebar web
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        
        // Header del sidebar
        JPanel sidebarHeader = createSidebarHeader();
        
        // Navegación del sidebar
        JPanel sidebarNav = createSidebarNav();
        
        sidebarPanel.add(sidebarHeader, BorderLayout.NORTH);
        sidebarPanel.add(sidebarNav, BorderLayout.CENTER);
    }
    
    private JPanel createSidebarHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(new Color(44, 62, 80));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Logo y título (imagen de Tux)
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoPanel.setBackground(new Color(44, 62, 80));
        
        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = UIUtils.loadImage("tux.png", 40, 60);
        if (logoIcon != null) {
            logoLabel.setIcon(logoIcon);
        } else {
            // Fallback al emoji si no se carga la imagen
            logoLabel.setText("🐧");
            logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        }
        logoLabel.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Punto de venta");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        
        logoPanel.add(logoLabel);
        logoPanel.add(Box.createHorizontalStrut(10));
        logoPanel.add(titleLabel);
        
        // Info del usuario
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.setBackground(new Color(44, 62, 80));
        
        
        
        usernameLabel = new JLabel(sessionManager.getUsername());
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(189, 195, 199));
        
        
        userPanel.add(Box.createHorizontalStrut(10));
        userPanel.add(usernameLabel);
        
        header.add(logoPanel);
        header.add(Box.createVerticalStrut(10)); // Reducir espacio
        header.add(userPanel);
        
        return header;
    }
    
    private JPanel createSidebarNav() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(new Color(44, 62, 80));
        nav.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Elementos de navegación
        String[] menuItems = {
            "Punto Venta|pos",
            "Inventario|inventory", 
            "Categorías|categories",
            "Ventas|sales",
            "Clientes|customers"
        };
        
        for (String item : menuItems) {
            String[] parts = item.split("\\|");
            String label = parts[0];
            String module = parts[1];
            
            JButton navButton = createNavButton(label, module);
            nav.add(navButton);
            nav.add(Box.createVerticalStrut(3)); // Reducir espacio entre botones
        }
        
        // Separador más pequeño
        nav.add(Box.createVerticalStrut(20));
        
        // Botón de logout más arriba y visible
        JButton logoutButton = createLogoutButton();
        nav.add(logoutButton);
        
        // Espacio restante
        nav.add(Box.createVerticalGlue());
        
        return nav;
    }
    
    private JButton createNavButton(String text, String module) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(44, 62, 80));
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Marcar como activo si es el módulo actual
        if (module.equals(currentModule)) {
            button.setBackground(new Color(52, 152, 219)); // Color activo
        }
        
        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = button.getBackground();
            
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!module.equals(currentModule)) {
                    button.setBackground(new Color(52, 73, 94));
                }
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!module.equals(currentModule)) {
                    button.setBackground(originalColor);
                }
            }
        });
        
        // Event listener
        button.addActionListener(e -> switchModule(module, button));
        
        return button;
    }
    
    private JButton createLogoutButton() {
        JButton button = new JButton("Cerrar Sesión");
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(192, 57, 43)); // Color rojo
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(169, 50, 38));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(192, 57, 43));
            }
        });
        
        button.addActionListener(e -> logout());
        
        return button;
    }
    
    private void createMainContent() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        // Header del contenido
        JPanel contentHeader = createContentHeader();
        
        // Área principal del contenido
        JPanel contentArea = createContentArea();
        
        contentPanel.add(contentHeader, BorderLayout.NORTH);
        contentPanel.add(contentArea, BorderLayout.CENTER);
    }
    
    private JPanel createContentHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        
        // Lado izquierdo del header
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Punto de Venta");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(UIUtils.TEXT_COLOR);
        
        JLabel breadcrumbLabel = new JLabel("Dashboard > Punto de Venta");
        breadcrumbLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        breadcrumbLabel.setForeground(UIUtils.TEXT_SECONDARY);
        
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(breadcrumbLabel);
        
        // Lado derecho del header
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(Color.WHITE);
        
        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateTimeLabel.setForeground(UIUtils.TEXT_COLOR);
        updateDateTime();
        
        rightPanel.add(dateTimeLabel);
        
        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createContentArea() {
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(UIUtils.BACKGROUND_COLOR);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Panel del POS
        JPanel posPanel = createPOSInterface();
        contentArea.add(posPanel, BorderLayout.CENTER);
        
        return contentArea;
    }
    
    private JPanel createPOSInterface() {
        JPanel posPanel = new JPanel(new BorderLayout());
        posPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        // Panel de productos (lado izquierdo)
        JPanel productsPanel = createProductsPanel();
        
        // Panel de carrito (lado derecho)
        JPanel cartPanel = createCartPanel();
        
        posPanel.add(productsPanel, BorderLayout.CENTER);
        posPanel.add(cartPanel, BorderLayout.EAST);
        
        return posPanel;
    }
    
    private JPanel createProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Header de productos
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Productos");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIUtils.TEXT_COLOR);
        
        JTextField searchField = new JTextField("Buscar productos...");
        searchField.setFont(UIUtils.INPUT_FONT);
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setForeground(UIUtils.TEXT_SECONDARY);
        
        // Placeholder behavior
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Buscar productos...")) {
                    searchField.setText("");
                    searchField.setForeground(UIUtils.TEXT_COLOR);
                }
            }
            
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Buscar productos...");
                    searchField.setForeground(UIUtils.TEXT_SECONDARY);
                }
            }
        });
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchField, BorderLayout.EAST);
        
        // Área de productos
        JPanel productsArea = new JPanel();
        productsArea.setBackground(Color.WHITE);
        productsArea.setLayout(new BoxLayout(productsArea, BoxLayout.Y_AXIS));
        
        // Mensaje temporal
        JLabel noProductsLabel = new JLabel("No hay productos disponibles", SwingConstants.CENTER);
        noProductsLabel.setFont(UIUtils.SUBTITLE_FONT);
        noProductsLabel.setForeground(UIUtils.TEXT_SECONDARY);
        productsArea.add(noProductsLabel);
        
        JScrollPane scrollPane = new JScrollPane(productsArea);
        scrollPane.setBorder(null);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(20), BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(360, 0)); // Aumentar ancho
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15) // Reducir padding
        ));
        
        // Header del carrito
        JLabel titleLabel = new JLabel("Carrito de Compras");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIUtils.TEXT_COLOR);
        
        // Área del carrito
        JPanel cartArea = new JPanel();
        cartArea.setBackground(Color.WHITE);
        cartArea.setLayout(new BoxLayout(cartArea, BoxLayout.Y_AXIS));
        
        // Mensaje temporal
        JLabel emptyCartLabel = new JLabel("Carrito vacío", SwingConstants.CENTER);
        emptyCartLabel.setFont(UIUtils.SUBTITLE_FONT);
        emptyCartLabel.setForeground(UIUtils.TEXT_SECONDARY);
        cartArea.add(emptyCartLabel);
        
        JScrollPane cartScrollPane = new JScrollPane(cartArea);
        cartScrollPane.setBorder(null);
        
        // Panel de totales con mejor espaciado
        JPanel totalsPanel = new JPanel();
        totalsPanel.setLayout(new BoxLayout(totalsPanel, BoxLayout.Y_AXIS));
        totalsPanel.setBackground(Color.WHITE);
        totalsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 0, 15, 0) // Mejor espaciado
        ));
        
        JLabel totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(UIUtils.TEXT_COLOR);
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton processButton = UIUtils.createPrimaryButton("Procesar Venta");
        processButton.setPreferredSize(new Dimension(320, 45)); // Ajustar al ancho disponible
        processButton.setMaximumSize(new Dimension(320, 45));
        processButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        totalsPanel.add(totalLabel);
        totalsPanel.add(Box.createVerticalStrut(10));
        totalsPanel.add(processButton);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(cartScrollPane, BorderLayout.CENTER);
        panel.add(totalsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupEventListeners() {
        // No hay listeners adicionales por el momento
    }
    
    private void switchModule(String module, JButton activeButton) {
        this.currentModule = module;
        
        // Actualizar estilos de botones de navegación
        updateNavButtonStyles(activeButton);
        
        // Actualizar contenido según el módulo
        updateContentForModule(module);
    }
    
    private void updateNavButtonStyles(JButton activeButton) {
        // Resetear todos los botones de navegación
        Component[] components = ((JPanel) sidebarPanel.getComponent(1)).getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton && !((JButton) comp).getText().contains("Cerrar Sesión")) {
                comp.setBackground(new Color(44, 62, 80));
            }
        }
        
        // Marcar el botón activo
        activeButton.setBackground(new Color(52, 152, 219));
    }
    
    private void updateContentForModule(String module) {
        // Por ahora solo mostramos el módulo POS
        // En futuras versiones se implementarán los otros módulos
        switch (module) {
            case "pos":
                // Ya está implementado
                break;
            case "inventory":
                UIUtils.showSuccessMessage(this, "Módulo de Inventario - En desarrollo");
                break;
            case "categories":
                UIUtils.showSuccessMessage(this, "Módulo de Categorías - En desarrollo");
                break;
            case "sales":
                UIUtils.showSuccessMessage(this, "Módulo de Ventas - En desarrollo");
                break;
            case "customers":
                UIUtils.showSuccessMessage(this, "Módulo de Clientes - En desarrollo");
                break;
        }
    }
    
    private void startClock() {
        clockTimer = new Timer(1000, e -> updateDateTime());
        clockTimer.start();
    }
    
    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        String dateTime = now.format(dateFormatter) + " " + now.format(timeFormatter);
        dateTimeLabel.setText(dateTime);
    }
    
    private void logout() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro que desea cerrar sesión?",
            "Confirmar cierre de sesión",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            // Cerrar sesión
            sessionManager.logout();
            
            // Parar el reloj
            if (clockTimer != null) {
                clockTimer.stop();
            }
            
            // Volver al login
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            this.dispose();
        }
    }
    
    @Override
    public void dispose() {
        if (clockTimer != null) {
            clockTimer.stop();
        }
        super.dispose();
    }
}
