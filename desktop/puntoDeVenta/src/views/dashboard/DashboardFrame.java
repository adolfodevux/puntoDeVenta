package views.dashboard;

import models.*;
import utils.CartManager;
import utils.SessionManager;
import utils.UIUtils;
import views.auth.LoginFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dashboard principal - Réplica exacta del dashboard web con todas las funcionalidades
 */
public class DashboardFrame extends JFrame {
    private JPanel mainPanel;
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private JLabel usernameLabel;
    private JLabel dateTimeLabel;
    private Timer clockTimer;
    private SessionManager sessionManager;
    
    // Componentes del POS
    private JPanel productsGridPanel;
    private JTable cartTable;
    private JTextField searchField;
    private JTextField clienteSearchField;
    private JTextField amountPaidField;
    private JLabel subtotalLabel;
    private JLabel taxLabel;
    private JLabel totalLabel;
    private JLabel changeLabel;
    private JButton checkoutButton;
    private JButton clearCartButton;
    
    // Datos y lógica
    private String currentModule = "pos";
    private List<Product> products;
    private CartManager cartManager;
    private Cliente selectedCliente;
    
    public DashboardFrame() {
        this.sessionManager = SessionManager.getInstance();
        this.cartManager = new CartManager();
        
        // Verificar que el usuario esté logueado
        if (!sessionManager.isLoggedIn()) {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            this.dispose();
            return;
        }
        
        initComponents();
        setupLayout();
        setupEventListeners();
        startClock();
        loadInitialData();
        
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
    
    private void loadInitialData() {
        // Cargar datos en background para no bloquear la UI
        SwingUtilities.invokeLater(() -> {
            loadProducts();
            loadCategories();
            loadClientes();
        });
    }
    
    private void loadProducts() {
        try {
            products = Product.getAllProducts();
            updateProductsGrid();
        } catch (Exception e) {
            UIUtils.showErrorMessage(this, "Error al cargar productos: " + e.getMessage());
        }
    }
    
    private void loadCategories() {
        try {
            // Las categorías se cargan directamente cuando se necesitan
            Category.getAllCategories();
        } catch (Exception e) {
            UIUtils.showErrorMessage(this, "Error al cargar categorías: " + e.getMessage());
        }
    }
    
    private void loadClientes() {
        try {
            // Los clientes se cargan directamente cuando se necesitan
            Cliente.obtenerTodos();
        } catch (Exception e) {
            UIUtils.showErrorMessage(this, "Error al cargar clientes: " + e.getMessage());
        }
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
        
        // Info del usuario en formato card
        JPanel userCard = new JPanel();
        userCard.setLayout(new BoxLayout(userCard, BoxLayout.X_AXIS));
        userCard.setBackground(new Color(52, 73, 94)); // Color más claro para el card
        userCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(74, 95, 116), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Icono del usuario
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        userIcon.setForeground(Color.WHITE);
        
        // Panel de información del usuario
        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setBackground(new Color(52, 73, 94));
        
        // Etiqueta "Usuario"
        JLabel userRoleLabel = new JLabel("Usuario");
        userRoleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        userRoleLabel.setForeground(new Color(149, 165, 166)); // Color más claro
        userRoleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        usernameLabel = new JLabel(sessionManager.getUsername());
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        userInfo.add(userRoleLabel);
        userInfo.add(usernameLabel);
        
        userCard.add(userIcon);
        userCard.add(Box.createHorizontalStrut(10));
        userCard.add(userInfo);
        userCard.add(Box.createHorizontalGlue());
        
        // Configurar el card para que tenga un ancho máximo
        userCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        userCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        userCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                userCard.setBackground(new Color(58, 79, 100)); // Más claro al hacer hover
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                userCard.setBackground(new Color(52, 73, 94)); // Color original
            }
        });
        
        header.add(logoPanel);
        header.add(Box.createVerticalStrut(15)); // Más espacio antes del card
        header.add(userCard);
        
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
            "Provedores|providers", 
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
        
        // Header de productos con título y búsqueda
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Productos");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIUtils.TEXT_COLOR);
        
        searchField = new JTextField("Buscar productos...");
        searchField.setFont(UIUtils.INPUT_FONT);
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setForeground(UIUtils.TEXT_SECONDARY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Placeholder behavior y búsqueda
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
        
        // Búsqueda en tiempo real
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String searchText = searchField.getText();
                if (!searchText.equals("Buscar productos...") && !searchText.isEmpty()) {
                    searchProducts(searchText);
                } else {
                    updateProductsGrid();
                }
            }
        });
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchField, BorderLayout.EAST);
        
        // Panel de categorías como en la web
        JPanel categoriesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        categoriesPanel.setBackground(Color.WHITE);
        
        // Botón "Todos" activo por defecto
        JButton todosButton = createCategoryButton("Todos", true);
        todosButton.addActionListener(e -> {
            setActiveCategoryButton(todosButton);
            updateProductsGrid();
        });
        categoriesPanel.add(todosButton);
        
        // Agregar categorías dinámicamente (se cargarán después)
        
        // Grid de productos como en la web
        productsGridPanel = new JPanel();
        productsGridPanel.setLayout(new GridLayout(0, 5, 15, 15)); // 5 columnas como en la web
        productsGridPanel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(productsGridPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Panel central que contiene categorías y productos
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(categoriesPanel, BorderLayout.NORTH);
        centerPanel.add(Box.createVerticalStrut(15), BorderLayout.CENTER);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(15), BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createCategoryButton(String text, boolean active) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (active) {
            button.setBackground(new Color(52, 152, 219));
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(UIUtils.TEXT_COLOR);
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(7, 15, 7, 15)
            ));
        }
        
        return button;
    }
    
    private void setActiveCategoryButton(JButton activeButton) {
        Component parent = activeButton.getParent();
        if (parent instanceof JPanel) {
            for (Component comp : ((JPanel) parent).getComponents()) {
                if (comp instanceof JButton) {
                    JButton btn = (JButton) comp;
                    btn.setBackground(Color.WHITE);
                    btn.setForeground(UIUtils.TEXT_COLOR);
                    btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                        BorderFactory.createEmptyBorder(7, 15, 7, 15)
                    ));
                }
            }
        }
        
        activeButton.setBackground(new Color(52, 152, 219));
        activeButton.setForeground(Color.WHITE);
        activeButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }
    
    private void updateProductsGrid() {
        if (productsGridPanel == null || products == null) return;
        
        productsGridPanel.removeAll();
        
        for (Product product : products) {
            JPanel productCard = createProductCard(product);
            productsGridPanel.add(productCard);
        }
        
        productsGridPanel.revalidate();
        productsGridPanel.repaint();
    }
    
    private void searchProducts(String searchTerm) {
        List<Product> filteredProducts = Product.searchProducts(searchTerm);
        
        productsGridPanel.removeAll();
        
        for (Product product : filteredProducts) {
            JPanel productCard = createProductCard(product);
            productsGridPanel.add(productCard);
        }
        
        if (filteredProducts.isEmpty()) {
            JLabel noResultsLabel = new JLabel("No se encontraron productos", SwingConstants.CENTER);
            noResultsLabel.setFont(UIUtils.SUBTITLE_FONT);
            noResultsLabel.setForeground(UIUtils.TEXT_SECONDARY);
            productsGridPanel.add(noResultsLabel);
        }
        
        productsGridPanel.revalidate();
        productsGridPanel.repaint();
    }
    
    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        card.setPreferredSize(new Dimension(200, 180));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Panel superior con icono
        JPanel iconPanel = new JPanel();
        iconPanel.setBackground(new Color(248, 249, 250));
        iconPanel.setPreferredSize(new Dimension(200, 80));
        iconPanel.setLayout(new BorderLayout());
        
        // Icono del producto basado en categoría
        JLabel iconLabel = new JLabel(getProductIcon(product.getCategoryName()), SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconPanel.add(iconLabel, BorderLayout.CENTER);
        
        // Panel inferior con información
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Nombre del producto
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(UIUtils.TEXT_COLOR);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Precio
        JLabel priceLabel = new JLabel("$" + String.format("%.2f", product.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        priceLabel.setForeground(new Color(39, 174, 96));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Stock con indicador
        JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        stockPanel.setBackground(Color.WHITE);
        
        JLabel stockLabel = new JLabel("Stock: " + product.getStock());
        stockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        stockLabel.setForeground(UIUtils.TEXT_SECONDARY);
        
        if (product.isLowStock()) {
            JLabel warningIcon = new JLabel(" ⚠️");
            warningIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 10));
            warningIcon.setForeground(Color.RED);
            stockPanel.add(stockLabel);
            stockPanel.add(warningIcon);
            stockLabel.setForeground(Color.RED);
        } else {
            stockPanel.add(stockLabel);
        }
        
        stockPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(stockPanel);
        
        card.add(iconPanel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        
        // Efecto hover como en la web
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2));
                iconPanel.setBackground(new Color(240, 245, 250));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
                iconPanel.setBackground(new Color(248, 249, 250));
            }
            
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addProductToCart(product);
            }
        });
        
        return card;
    }
    
    private String getProductIcon(String categoryName) {
        if (categoryName == null) return "📦";
        
        switch (categoryName.toLowerCase()) {
            case "bebidas": return "🥤";
            case "comida": return "🍽️";
            case "dulces": return "🍭";
            case "mercancia": return "📦";
            case "otros": return "📋";
            case "regalos": return "🎁";
            case "snacks": return "🍿";
            default: return "📦";
        }
    }
    
    private void addProductToCart(Product product) {
        if (product.getStock() <= 0) {
            UIUtils.showErrorMessage(this, "Producto sin stock disponible");
            return;
        }
        
        // Agregar producto al carrito
        cartManager.addProduct(product, 1);
        
        // Forzar actualización inmediata del carrito
        SwingUtilities.invokeLater(() -> {
            // Actualizar el display del carrito
            updateCartDisplay();
            
            // Asegurar que la tabla se repinte completamente
            if (cartTable != null) {
                cartTable.revalidate();
                cartTable.repaint();
            }
        });
        
        // Mensaje de confirmación
        UIUtils.showSuccessMessage(this, "✓ " + product.getName() + " agregado al carrito");
    }
    
    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(380, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Header del carrito con botón limpiar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Carrito de Compras");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIUtils.TEXT_COLOR);
        
        clearCartButton = new JButton("🗑️ Limpiar");
        clearCartButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clearCartButton.setForeground(Color.WHITE);
        clearCartButton.setBackground(new Color(231, 76, 60));
        clearCartButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        clearCartButton.setFocusPainted(false);
        clearCartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearCartButton.addActionListener(e -> clearCart());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(clearCartButton, BorderLayout.EAST);
        
        // Tabla del carrito
        cartTable = new JTable(cartManager.getTableModel());
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cartTable.setRowHeight(25);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.setFillsViewportHeight(true);
        
        // Configurar event listeners para la tabla del carrito INMEDIATAMENTE después de crearla
        setupCartTableListeners();
        
        // Configurar renderizado de celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        cartTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        cartTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        cartTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        
        // Configurar anchos de columnas
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Producto
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(70);  // Cantidad
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Precio
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Total
        
        JScrollPane cartScrollPane = new JScrollPane(cartTable);
        cartScrollPane.setBorder(null);
        cartScrollPane.setPreferredSize(new Dimension(350, 200));
        
        // Panel de cliente
        JPanel clientePanel = new JPanel();
        clientePanel.setLayout(new BoxLayout(clientePanel, BoxLayout.Y_AXIS));
        clientePanel.setBackground(Color.WHITE);
        clientePanel.setBorder(BorderFactory.createTitledBorder("Cliente (Opcional)"));
        
        clienteSearchField = new JTextField("Buscar cliente...");
        clienteSearchField.setFont(UIUtils.INPUT_FONT);
        clienteSearchField.setPreferredSize(new Dimension(320, 35));
        clienteSearchField.setMaximumSize(new Dimension(320, 35));
        clienteSearchField.setForeground(UIUtils.TEXT_SECONDARY);
        clienteSearchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Placeholder behavior para cliente
        clienteSearchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (clienteSearchField.getText().equals("Buscar cliente...")) {
                    clienteSearchField.setText("");
                    clienteSearchField.setForeground(UIUtils.TEXT_COLOR);
                }
            }
            
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (clienteSearchField.getText().isEmpty()) {
                    clienteSearchField.setText("Buscar cliente...");
                    clienteSearchField.setForeground(UIUtils.TEXT_SECONDARY);
                }
            }
        });
        
        clientePanel.add(clienteSearchField);
        
        // Panel de totales y pago
        JPanel totalsPanel = new JPanel();
        totalsPanel.setLayout(new BoxLayout(totalsPanel, BoxLayout.Y_AXIS));
        totalsPanel.setBackground(Color.WHITE);
        totalsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 0, 15, 0)
        ));
        
        // Labels de totales
        subtotalLabel = new JLabel("Subtotal: $0.00");
        subtotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtotalLabel.setForeground(UIUtils.TEXT_COLOR);
        subtotalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        taxLabel = new JLabel("IVA (16%): $0.00");
        taxLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taxLabel.setForeground(UIUtils.TEXT_COLOR);
        taxLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(UIUtils.TEXT_COLOR);
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Campo de monto recibido
        JLabel paymentLabel = new JLabel("Monto recibido:");
        paymentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        paymentLabel.setForeground(UIUtils.TEXT_COLOR);
        paymentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        amountPaidField = new JTextField("0.00");
        amountPaidField.setFont(UIUtils.INPUT_FONT);
        amountPaidField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        amountPaidField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Listener para calcular cambio
        amountPaidField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updateChangeDisplay();
            }
        });
        
        changeLabel = new JLabel("Cambio: $0.00");
        changeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        changeLabel.setForeground(new Color(39, 174, 96));
        changeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Botón de procesar venta
        checkoutButton = UIUtils.createPrimaryButton("💳 Procesar Venta");
        checkoutButton.setPreferredSize(new Dimension(320, 45));
        checkoutButton.setMaximumSize(new Dimension(320, 45));
        checkoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkoutButton.setEnabled(false);
        checkoutButton.addActionListener(e -> processCheckout());
        
        totalsPanel.add(subtotalLabel);
        totalsPanel.add(Box.createVerticalStrut(5));
        totalsPanel.add(taxLabel);
        totalsPanel.add(Box.createVerticalStrut(5));
        totalsPanel.add(totalLabel);
        totalsPanel.add(Box.createVerticalStrut(15));
        totalsPanel.add(paymentLabel);
        totalsPanel.add(Box.createVerticalStrut(5));
        totalsPanel.add(amountPaidField);
        totalsPanel.add(Box.createVerticalStrut(10));
        totalsPanel.add(changeLabel);
        totalsPanel.add(Box.createVerticalStrut(15));
        totalsPanel.add(checkoutButton);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(cartScrollPane, BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(10), BorderLayout.EAST);
        panel.add(clientePanel, BorderLayout.SOUTH);
        
        // Panel principal que incluye la tabla y los totales
        JPanel mainCartPanel = new JPanel(new BorderLayout());
        mainCartPanel.setBackground(Color.WHITE);
        mainCartPanel.add(panel, BorderLayout.CENTER);
        mainCartPanel.add(totalsPanel, BorderLayout.SOUTH);
        
        return mainCartPanel;
    }
    
    private void updateCartDisplay() {
        // NO llamar a fireTableDataChanged() aquí para evitar bucle infinito
        // El listener ya se encarga de esto automáticamente
        
        // Asegurar que la tabla se revalide y repinte si existe
        if (cartTable != null) {
            cartTable.revalidate();
            cartTable.repaint();
        }
        
        // Actualizar totales solo si los labels existen
        if (subtotalLabel != null && taxLabel != null && totalLabel != null) {
            double subtotal = cartManager.getSubtotal();
            double tax = cartManager.getTax();
            double total = cartManager.getTotal();
            
            subtotalLabel.setText("Subtotal: $" + String.format("%.2f", subtotal));
            taxLabel.setText("IVA (16%): $" + String.format("%.2f", tax));
            totalLabel.setText("Total: $" + String.format("%.2f", total));
            
            // Forzar repintado de los labels
            subtotalLabel.repaint();
            taxLabel.repaint();
            totalLabel.repaint();
        }
        
        // Habilitar/deshabilitar botón de checkout
        if (checkoutButton != null) {
            checkoutButton.setEnabled(!cartManager.isEmpty());
        }
        
        // Actualizar cambio
        updateChangeDisplay();
    }
    
    private void updateChangeDisplay() {
        try {
            double amountPaid = Double.parseDouble(amountPaidField.getText());
            double total = cartManager.getTotal();
            double change = amountPaid - total;
            
            if (change >= 0) {
                changeLabel.setText("Cambio: $" + String.format("%.2f", change));
                changeLabel.setForeground(new Color(39, 174, 96));
            } else {
                changeLabel.setText("Falta: $" + String.format("%.2f", Math.abs(change)));
                changeLabel.setForeground(Color.RED);
            }
        } catch (NumberFormatException e) {
            changeLabel.setText("Cambio: $0.00");
            changeLabel.setForeground(new Color(39, 174, 96));
        }
    }
    
    private void clearCart() {
        if (cartManager.isEmpty()) {
            UIUtils.showErrorMessage(this, "El carrito ya está vacío");
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro que desea limpiar el carrito?",
            "Confirmar acción",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            cartManager.clearCart();
            // updateCartDisplay(); - Se llamará automáticamente por el listener de la tabla
            
            // Limpiar campos relacionados
            amountPaidField.setText("");
            selectedCliente = null;
            clienteSearchField.setText("Buscar cliente...");
            clienteSearchField.setForeground(UIUtils.TEXT_SECONDARY);
            
            // Mensaje de confirmación
            UIUtils.showSuccessMessage(this, "✓ Carrito limpiado correctamente");
        }
    }
    
    private void processCheckout() {
        if (cartManager.isEmpty()) {
            UIUtils.showErrorMessage(this, "El carrito está vacío");
            return;
        }
        
        try {
            double amountPaid = Double.parseDouble(amountPaidField.getText());
            double total = cartManager.getTotal();
            
            if (amountPaid < total) {
                UIUtils.showErrorMessage(this, "El monto recibido es insuficiente");
                return;
            }
            
            // Crear venta
            Sale sale = new Sale();
            sale.setUserId(sessionManager.getUserId());
            sale.setItems(cartManager.getSaleItems());
            sale.setAmountPaid(amountPaid);
            
            if (selectedCliente != null) {
                sale.setClienteId(selectedCliente.getId());
            }
            
            // Guardar venta
            if (sale.save()) {
                UIUtils.showSuccessMessage(this, "¡Venta procesada exitosamente por $" + String.format("%.2f", total) + "!");
                
                // Limpiar carrito y campos
                clearCart();
                
                // Recargar productos para actualizar stock
                loadProducts();
            } else {
                UIUtils.showErrorMessage(this, "Error al procesar la venta");
            }
            
        } catch (NumberFormatException e) {
            UIUtils.showErrorMessage(this, "Ingrese un monto válido");
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupEventListeners() {
        // Los listeners del carrito se configuran en setupCartTableListeners() 
        // después de crear la tabla del carrito
        
        System.out.println("✓ Event listeners principales configurados");
    }
    
    /**
     * Configura los event listeners específicos para la tabla del carrito
     * Se debe llamar después de crear cartTable
     */
    private void setupCartTableListeners() {
        if (cartTable == null) {
            System.err.println("Error: cartTable es null al configurar listeners");
            return;
        }
        
        // Listener para cambios en el modelo de la tabla del carrito
        cartManager.getTableModel().addTableModelListener(e -> {
            // Solo actualizar en el EDT y una sola vez por cambio
            if (!SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeLater(() -> updateCartDisplay());
            } else {
                updateCartDisplay();
            }
        });
        
        // Listener para doble click en tabla del carrito (eliminar item)
        cartTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = cartTable.getSelectedRow();
                    if (row >= 0) {
                        int option = JOptionPane.showConfirmDialog(
                            DashboardFrame.this,
                            "¿Desea eliminar este producto del carrito?",
                            "Eliminar producto",
                            JOptionPane.YES_NO_OPTION
                        );
                        if (option == JOptionPane.YES_OPTION) {
                            cartManager.removeItem(row);
                            UIUtils.showSuccessMessage(DashboardFrame.this, "✓ Producto eliminado del carrito");
                        }
                    }
                }
            }
        });
        
        System.out.println("✓ Event listeners del carrito configurados correctamente");
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
        // Actualizar título y breadcrumb
        updateContentHeader(module);
        
        // Crear contenido según módulo
        JPanel newContent = createModuleContent(module);
        
        // Reemplazar contenido
        contentPanel.removeAll();
        contentPanel.add(createContentHeader(), BorderLayout.NORTH);
        contentPanel.add(newContent, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void updateContentHeader(String module) {
        // TODO: Actualizar el header según el módulo activo
    }
    
    private JPanel createModuleContent(String module) {
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(UIUtils.BACKGROUND_COLOR);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        switch (module) {
            case "pos":
                contentArea.add(createPOSInterface(), BorderLayout.CENTER);
                break;
            case "inventory":
                contentArea.add(createInventoryModule(), BorderLayout.CENTER);
                break;
            case "categories":
                contentArea.add(createCategoriesModule(), BorderLayout.CENTER);
                break;
            case "sales":
                contentArea.add(createSalesModule(), BorderLayout.CENTER);
                break;
            case "customers":
                contentArea.add(createCustomersModule(), BorderLayout.CENTER);
                break;
            case "providers":
                contentArea.add(createProvidersModule(), BorderLayout.CENTER);
                break;
            default:
                JLabel notImplementedLabel = new JLabel("Módulo en desarrollo", SwingConstants.CENTER);
                notImplementedLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
                notImplementedLabel.setForeground(UIUtils.TEXT_SECONDARY);
                contentArea.add(notImplementedLabel, BorderLayout.CENTER);
        }
        
        return contentArea;
    }
    
    private JPanel createInventoryModule() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("📦 Módulo de Inventario", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(UIUtils.TEXT_COLOR);
        
        JLabel descLabel = new JLabel("Gestión de productos y stock", SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setForeground(UIUtils.TEXT_SECONDARY);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(descLabel);
        centerPanel.add(Box.createVerticalGlue());
        
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createCategoriesModule() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("🏷️ Módulo de Categorías", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(UIUtils.TEXT_COLOR);
        
        JLabel descLabel = new JLabel("Gestión de categorías de productos", SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setForeground(UIUtils.TEXT_SECONDARY);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(descLabel);
        centerPanel.add(Box.createVerticalGlue());
        
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createSalesModule() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("📊 Módulo de Ventas", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(UIUtils.TEXT_COLOR);
        
        JLabel descLabel = new JLabel("Historial y reportes de ventas", SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setForeground(UIUtils.TEXT_SECONDARY);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(descLabel);
        centerPanel.add(Box.createVerticalGlue());
        
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createCustomersModule() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("👥 Módulo de Clientes", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(UIUtils.TEXT_COLOR);
        
        JLabel descLabel = new JLabel("Gestión de clientes registrados", SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setForeground(UIUtils.TEXT_SECONDARY);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(descLabel);
        centerPanel.add(Box.createVerticalGlue());
        
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createProvidersModule() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("🚚 Módulo de Proveedores", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(UIUtils.TEXT_COLOR);
        
        JLabel descLabel = new JLabel("Gestión de proveedores", SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setForeground(UIUtils.TEXT_SECONDARY);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(descLabel);
        centerPanel.add(Box.createVerticalGlue());
        
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
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
