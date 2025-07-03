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
    
    // Cache de contenido de módulos para evitar recreación
    private JPanel posContent;
    private JPanel inventoryContent;
    private JPanel categoriesContent;
    private JPanel salesContent;
    private JPanel customersContent;
    private JPanel suppliersContent;
    
    // Datos y lógica
    private String currentModule = "pos";
    private List<Product> products;
    private List<Category> categories;
    private List<Cliente> clientes;
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
            categories = Category.getAllCategories();
        } catch (Exception e) {
            UIUtils.showErrorMessage(this, "Error al cargar categorías: " + e.getMessage());
        }
    }
    
    private void loadClientes() {
        try {
            clientes = Cliente.obtenerTodos();
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
        
        // Elementos de navegación (igual al dashboard web)
        String[] menuItems = {
            "🏪 Punto Venta|pos",
            "📦 Inventario|inventory", 
            "🚚 Proveedores|suppliers", 
            "🏷️ Categorías|categories",
            "📊 Ventas|sales",
            "👥 Clientes|customers"
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
        
        // Crear un layout personalizado que funcione mejor para el grid
        productsGridPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15) {
            @Override
            public Dimension preferredLayoutSize(Container target) {
                // Calcular el tamaño preferido dinámicamente
                Dimension dim = super.preferredLayoutSize(target);
                // Asegurar que el contenedor tenga suficiente altura para mostrar todos los elementos
                int componentCount = target.getComponentCount();
                if (componentCount > 0) {
                    // Calcular filas necesarias basado en el ancho disponible
                    int containerWidth = target.getParent() != null ? target.getParent().getWidth() : 800;
                    int cardWidth = 180 + 15; // ancho de tarjeta + gap
                    int cardsPerRow = Math.max(1, (containerWidth - 30) / cardWidth);
                    int rows = (int) Math.ceil((double) componentCount / cardsPerRow);
                    
                    // Altura: filas * altura de tarjeta + gaps
                    int totalHeight = rows * (200 + 15) + 30; // altura de tarjeta + gap + padding
                    return new Dimension(dim.width, Math.max(totalHeight, dim.height));
                }
                return dim;
            }
        });
        
        productsGridPanel.setBackground(Color.WHITE);
        productsGridPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(productsGridPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Scroll más suave
        scrollPane.getVerticalScrollBar().setBlockIncrement(64); // Scroll más rápido con página
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Asegurar que el scroll funcione correctamente
        scrollPane.getViewport().addChangeListener(e -> {
            // Forzar actualización del layout cuando cambie la vista
            SwingUtilities.invokeLater(() -> {
                productsGridPanel.revalidate();
                productsGridPanel.repaint();
            });
        });
        
        // Panel central que contiene categorías y productos
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(categoriesPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);
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
        
        // Si no hay productos, mostrar mensaje
        if (products.isEmpty()) {
            JLabel noProductsLabel = new JLabel("No hay productos disponibles", SwingConstants.CENTER);
            noProductsLabel.setFont(UIUtils.SUBTITLE_FONT);
            noProductsLabel.setForeground(UIUtils.TEXT_SECONDARY);
            productsGridPanel.add(noProductsLabel);
        }
        
        // Forzar actualización visual completa
        productsGridPanel.revalidate();
        productsGridPanel.repaint();
        
        // También actualizar el contenedor padre
        Container parent = productsGridPanel.getParent();
        if (parent != null) {
            parent.revalidate();
            parent.repaint();
        }
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
        // Tamaño igual a la web: aproximadamente 180x200
        card.setPreferredSize(new Dimension(180, 200));
        card.setMinimumSize(new Dimension(180, 200));
        card.setMaximumSize(new Dimension(180, 200));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Panel superior con icono
        JPanel iconPanel = new JPanel();
        iconPanel.setBackground(new Color(248, 249, 250));
        iconPanel.setPreferredSize(new Dimension(180, 90)); // Proporción similar a la web
        iconPanel.setLayout(new BorderLayout());
        
        // Icono del producto basado en categoría
        JLabel iconLabel = new JLabel(getProductIcon(product.getCategoryName()), SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40)); // Icono más grande
        iconPanel.add(iconLabel, BorderLayout.CENTER);
        
        // Panel inferior con información
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Nombre del producto (truncar si es muy largo)
        String productName = product.getName();
        if (productName.length() > 18) {
            productName = productName.substring(0, 15) + "...";
        }
        JLabel nameLabel = new JLabel(productName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(UIUtils.TEXT_COLOR);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Precio
        JLabel priceLabel = new JLabel("$" + String.format("%.2f", product.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        priceLabel.setForeground(new Color(39, 174, 96));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Stock con indicador como en la web
        JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        stockPanel.setBackground(Color.WHITE);
        
        // Obtener cantidad en carrito para mostrar stock disponible real
        int quantityInCart = cartManager.getProductQuantityInCart(product.getId());
        int availableStock = product.getStock() - quantityInCart;
        
        JLabel stockLabel;
        if (availableStock <= 0) {
            stockLabel = new JLabel("Stock: 0");
            stockLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            stockLabel.setForeground(Color.WHITE);
            stockLabel.setOpaque(true);
            stockLabel.setBackground(Color.RED);
            stockLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        } else if (availableStock <= 5) {
            stockLabel = new JLabel("Stock: " + availableStock);
            stockLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            stockLabel.setForeground(Color.WHITE);
            stockLabel.setOpaque(true);
            stockLabel.setBackground(new Color(243, 156, 18)); // Naranja
            stockLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        } else {
            stockLabel = new JLabel("Stock: " + product.getStock());
            stockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            stockLabel.setForeground(new Color(127, 140, 141));
        }
        
        stockPanel.add(stockLabel);
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
                if (availableStock > 0) {
                    card.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2));
                    iconPanel.setBackground(new Color(240, 245, 250));
                    card.setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    card.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                    card.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (availableStock <= 0) {
                    card.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                    iconPanel.setBackground(new Color(255, 240, 240));
                } else {
                    card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
                    iconPanel.setBackground(new Color(248, 249, 250));
                }
            }
            
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addProductToCart(product);
            }
        });
        
        // Establecer estilo inicial basado en disponibilidad
        if (availableStock <= 0) {
            card.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            iconPanel.setBackground(new Color(255, 240, 240));
            card.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        
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
        // Verificar stock disponible total
        if (product.getStock() <= 0) {
            JOptionPane.showMessageDialog(
                this,
                "Este producto no tiene stock disponible",
                "Sin Stock",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        // Verificar si ya está en el carrito y calcular stock disponible
        int quantityInCart = cartManager.getProductQuantityInCart(product.getId());
        int availableStock = product.getStock() - quantityInCart;
        
        if (availableStock <= 0) {
            JOptionPane.showMessageDialog(
                this,
                "No hay más stock disponible de este producto.\nCantidad en carrito: " + quantityInCart + "\nStock total: " + product.getStock(),
                "Stock Insuficiente",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        // Agregar al carrito
        cartManager.addProduct(product, 1);
        
        // Actualizar la vista del carrito
        updateCartDisplay();
        
        // Actualizar solo las tarjetas de productos para reflejar el nuevo stock disponible
        SwingUtilities.invokeLater(() -> {
            updateProductsGrid();
        });
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
        
        // Label de ayuda para el usuario
        JLabel helpLabel = new JLabel("💡 Click para modificar cantidad • Doble click para eliminar");
        helpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        helpLabel.setForeground(UIUtils.TEXT_SECONDARY);
        helpLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
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
        
        // Panel central que incluye la tabla y la ayuda
        JPanel cartCenterPanel = new JPanel(new BorderLayout());
        cartCenterPanel.setBackground(Color.WHITE);
        cartCenterPanel.add(cartScrollPane, BorderLayout.CENTER);
        cartCenterPanel.add(helpLabel, BorderLayout.SOUTH);
        
        panel.add(cartCenterPanel, BorderLayout.CENTER);
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
        cartManager.getTableModel().fireTableDataChanged();
        
        // Actualizar totales
        double subtotal = cartManager.getSubtotal();
        double tax = cartManager.getTax();
        double total = cartManager.getTotal();
        
        subtotalLabel.setText("Subtotal: $" + String.format("%.2f", subtotal));
        taxLabel.setText("IVA (16%): $" + String.format("%.2f", tax));
        totalLabel.setText("Total: $" + String.format("%.2f", total));
        
        // Habilitar/deshabilitar botón de checkout
        checkoutButton.setEnabled(!cartManager.isEmpty());
        
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
        int option = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro que desea limpiar el carrito?",
            "Confirmar acción",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            cartManager.clearCart();
            updateCartDisplay();
            amountPaidField.setText("");
            selectedCliente = null;
            clienteSearchField.setText("Buscar cliente...");
            clienteSearchField.setForeground(UIUtils.TEXT_SECONDARY);
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
        // Listener para cambios en el carrito
        cartManager.getTableModel().addTableModelListener(e -> {
            updateCartDisplay();
            // Actualizar la vista de productos cuando cambie el carrito
            if (currentModule.equals("pos")) {
                updateProductsGrid();
            }
        });
        
        // Listener para doble click en tabla del carrito (eliminar item)
        cartTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = cartTable.getSelectedRow();
                if (row >= 0) {
                    if (evt.getClickCount() == 2) {
                        // Doble click: eliminar item
                        int option = JOptionPane.showConfirmDialog(
                            DashboardFrame.this,
                            "¿Desea eliminar este producto del carrito?",
                            "Eliminar producto",
                            JOptionPane.YES_NO_OPTION
                        );
                        if (option == JOptionPane.YES_OPTION) {
                            cartManager.removeItem(row);
                        }
                    } else if (evt.getClickCount() == 1) {
                        // Click simple: mostrar opciones de cantidad
                        showQuantityDialog(row);
                    }
                }
            }
        });
    }
    
    private void showQuantityDialog(int cartRow) {
        if (cartRow < 0 || cartRow >= cartManager.getItems().size()) return;
        
        String productName = (String) cartTable.getValueAt(cartRow, 0);
        int currentQuantity = (Integer) cartTable.getValueAt(cartRow, 1);
        
        // Encontrar el producto para verificar stock
        Product finalProduct = null;
        if (products != null) {
            for (Product p : products) {
                if (p.getName().equals(productName)) {
                    finalProduct = p;
                    break;
                }
            }
        }
        
        if (finalProduct == null) return;
        
        final Product product = finalProduct; // Variable final para usar en lambdas
        
        // Crear diálogo personalizado para cambiar cantidad
        JDialog dialog = new JDialog(this, "Modificar Cantidad - " + productName, true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        JLabel currentLabel = new JLabel("Cantidad actual: " + currentQuantity);
        JLabel stockLabel = new JLabel("Stock disponible: " + product.getStock());
        
        JPanel quantityPanel = new JPanel(new FlowLayout());
        JButton decreaseBtn = new JButton("-");
        JTextField quantityField = new JTextField(String.valueOf(currentQuantity), 5);
        JButton increaseBtn = new JButton("+");
        
        decreaseBtn.addActionListener(e -> {
            int qty = Math.max(1, Integer.parseInt(quantityField.getText()) - 1);
            quantityField.setText(String.valueOf(qty));
        });
        
        increaseBtn.addActionListener(e -> {
            int qty = Integer.parseInt(quantityField.getText()) + 1;
            if (qty <= product.getStock()) {
                quantityField.setText(String.valueOf(qty));
            }
        });
        
        quantityPanel.add(decreaseBtn);
        quantityPanel.add(quantityField);
        quantityPanel.add(increaseBtn);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okBtn = new JButton("Aplicar");
        JButton cancelBtn = new JButton("Cancelar");
        
        okBtn.addActionListener(e -> {
            try {
                int newQuantity = Integer.parseInt(quantityField.getText());
                if (newQuantity > 0 && newQuantity <= product.getStock()) {
                    cartManager.updateQuantity(cartRow, newQuantity);
                }
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Cantidad inválida", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(okBtn);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(currentLabel, gbc);
        gbc.gridy = 1;
        panel.add(stockLabel, gbc);
        gbc.gridy = 2;
        panel.add(quantityPanel, gbc);
        gbc.gridy = 3;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
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
        
        // Obtener o crear contenido según módulo usando caché
        JPanel newContent = getCachedModuleContent(module);
        
        // Reemplazar contenido
        contentPanel.removeAll();
        contentPanel.add(createContentHeader(), BorderLayout.NORTH);
        contentPanel.add(newContent, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        // Si volvemos al POS, asegurar que los productos estén cargados
        if (module.equals("pos") && products != null) {
            updateProductsGrid();
        }
    }
    
    private JPanel getCachedModuleContent(String module) {
        switch (module) {
            case "pos":
                // Siempre recrear POS para asegurar eventos y estado actualizados
                posContent = createModuleContent(module);
                return posContent;
            case "inventory":
                // Siempre recrear inventario para datos actualizados
                inventoryContent = createModuleContent(module);
                return inventoryContent;
            case "categories":
                if (categoriesContent == null) {
                    categoriesContent = createModuleContent(module);
                }
                return categoriesContent;
            case "sales":
                if (salesContent == null) {
                    salesContent = createModuleContent(module);
                }
                return salesContent;
            case "customers":
                // Siempre recrear clientes para datos actualizados
                customersContent = createModuleContent(module);
                return customersContent;
            case "suppliers":
                if (suppliersContent == null) {
                    suppliersContent = createModuleContent(module);
                }
                return suppliersContent;
            default:
                return createModuleContent(module);
        }
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
            case "suppliers":
                contentArea.add(createSuppliersModule(), BorderLayout.CENTER);
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
        panel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        // Header del módulo
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("📦 Gestión de Inventario");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(UIUtils.TEXT_COLOR);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);
        
        JButton addProductBtn = UIUtils.createPrimaryButton("+ Agregar Producto");
        addProductBtn.addActionListener(e -> showAddProductDialog());
        
        JButton refreshBtn = UIUtils.createSecondaryButton("🔄 Actualizar");
        refreshBtn.addActionListener(e -> refreshInventory());
        
        actionPanel.add(refreshBtn);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(addProductBtn);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(actionPanel, BorderLayout.EAST);
        
        // Content area con tabla de productos
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        // Crear tabla de inventario
        String[] columnNames = {"ID", "Producto", "Categoría", "Precio", "Stock", "Estado", "Acciones"};
        Object[][] data = getInventoryData();
        
        JTable inventoryTable = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Solo la columna de acciones es editable
            }
        };
        
        inventoryTable.setRowHeight(40);
        inventoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        inventoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        inventoryTable.getTableHeader().setBackground(UIUtils.PRIMARY_COLOR);
        inventoryTable.getTableHeader().setForeground(Color.WHITE);
        
        // Configurar renderizado de columnas
        inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        inventoryTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Producto
        inventoryTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Categoría
        inventoryTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Precio
        inventoryTable.getColumnModel().getColumn(4).setPreferredWidth(60);  // Stock
        inventoryTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Estado
        inventoryTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Acciones
        
        // Renderizar estado con colores
        inventoryTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String status = value.toString();
                if (status.equals("Bajo Stock")) {
                    setForeground(Color.RED);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if (status.equals("Normal")) {
                    setForeground(new Color(39, 174, 96));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setForeground(Color.GRAY);
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        // Panel de estadísticas
        JPanel statsPanel = createInventoryStatsPanel();
        
        contentArea.add(statsPanel, BorderLayout.NORTH);
        contentArea.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentArea, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInventoryStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Calcular estadísticas
        int totalProducts = products != null ? products.size() : 0;
        int lowStockCount = 0;
        double totalValue = 0;
        int outOfStockCount = 0;
        
        if (products != null) {
            for (Product product : products) {
                if (product.getStock() <= 0) outOfStockCount++;
                else if (product.isLowStock()) lowStockCount++;
                totalValue += product.getPrice() * product.getStock();
            }
        }
        
        // Crear cards de estadísticas
        JPanel totalCard = createStatsCard("Total Productos", String.valueOf(totalProducts), "📦", UIUtils.PRIMARY_COLOR);
        JPanel lowStockCard = createStatsCard("Bajo Stock", String.valueOf(lowStockCount), "⚠️", Color.ORANGE);
        JPanel outStockCard = createStatsCard("Sin Stock", String.valueOf(outOfStockCount), "❌", Color.RED);
        JPanel valueCard = createStatsCard("Valor Total", "$" + String.format("%.2f", totalValue), "💰", new Color(39, 174, 96));
        
        statsPanel.add(totalCard);
        statsPanel.add(lowStockCard);
        statsPanel.add(outStockCard);
        statsPanel.add(valueCard);
        
        return statsPanel;
    }
    
    private JPanel createStatsCard(String title, String value, String icon, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(UIUtils.TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        textPanel.add(valueLabel);
        textPanel.add(titleLabel);
        
        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private Object[][] getInventoryData() {
        if (products == null || products.isEmpty()) {
            return new Object[0][7];
        }
        
        Object[][] data = new Object[products.size()][7];
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            data[i][0] = product.getId();
            data[i][1] = product.getName();
            data[i][2] = product.getCategoryName();
            data[i][3] = "$" + String.format("%.2f", product.getPrice());
            data[i][4] = product.getStock();
            
            // Determinar estado
            if (product.getStock() <= 0) {
                data[i][5] = "Sin Stock";
            } else if (product.isLowStock()) {
                data[i][5] = "Bajo Stock";
            } else {
                data[i][5] = "Normal";
            }
            
            data[i][6] = "Editar | Eliminar";
        }
        
        return data;
    }
    
    private void showAddProductDialog() {
        JOptionPane.showMessageDialog(this, 
            "Funcionalidad de agregar producto en desarrollo...\n" +
            "Esta función permitirá agregar nuevos productos al inventario.",
            "Agregar Producto", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void refreshInventory() {
        loadProducts();
        updateContentForModule("inventory");
        JOptionPane.showMessageDialog(this, 
            "Inventario actualizado correctamente",
            "Actualización", 
            JOptionPane.INFORMATION_MESSAGE);
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
        panel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        // Header del módulo
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("👥 Gestión de Clientes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(UIUtils.TEXT_COLOR);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);
        
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.setText("Buscar cliente...");
        searchField.setForeground(Color.GRAY);
        
        JButton addClientBtn = UIUtils.createPrimaryButton("+ Agregar Cliente");
        addClientBtn.addActionListener(e -> showAddClientDialog());
        
        JButton refreshBtn = UIUtils.createSecondaryButton("🔄 Actualizar");
        refreshBtn.addActionListener(e -> refreshClients());
        
        actionPanel.add(searchField);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(refreshBtn);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(addClientBtn);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(actionPanel, BorderLayout.EAST);
        
        // Content area con tabla de clientes
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        // Crear tabla de clientes
        String[] columnNames = {"ID", "Nombre", "Email", "Teléfono", "Dirección", "Fecha Registro", "Acciones"};
        Object[][] data = getClientsData();
        
        JTable clientsTable = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Solo la columna de acciones es editable
            }
        };
        
        clientsTable.setRowHeight(35);
        clientsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clientsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        clientsTable.getTableHeader().setBackground(UIUtils.PRIMARY_COLOR);
        clientsTable.getTableHeader().setForeground(Color.WHITE);
        
        // Configurar anchos de columnas
        clientsTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        clientsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Nombre
        clientsTable.getColumnModel().getColumn(2).setPreferredWidth(180); // Email
        clientsTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Teléfono
        clientsTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Dirección
        clientsTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Fecha
        clientsTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Acciones
        
        JScrollPane scrollPane = new JScrollPane(clientsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        // Panel de estadísticas de clientes
        JPanel statsPanel = createClientsStatsPanel();
        
        contentArea.add(statsPanel, BorderLayout.NORTH);
        contentArea.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentArea, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createClientsStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Calcular estadísticas de clientes
        int totalClients = clientes != null ? clientes.size() : 0;
        int newClientsThisMonth = 0; // Esto se calcularía con fecha real
        int activeClients = totalClients; // Todos están activos por defecto
        
        // Crear cards de estadísticas
        JPanel totalCard = createStatsCard("Total Clientes", String.valueOf(totalClients), "👥", UIUtils.PRIMARY_COLOR);
        JPanel newCard = createStatsCard("Nuevos (Mes)", String.valueOf(newClientsThisMonth), "🆕", new Color(39, 174, 96));
        JPanel activeCard = createStatsCard("Activos", String.valueOf(activeClients), "✅", new Color(52, 152, 219));
        
        statsPanel.add(totalCard);
        statsPanel.add(newCard);
        statsPanel.add(activeCard);
        
        return statsPanel;
    }
    
    private Object[][] getClientsData() {
        if (clientes == null || clientes.isEmpty()) {
            return new Object[0][7];
        }
        
        Object[][] data = new Object[clientes.size()][7];
        for (int i = 0; i < clientes.size(); i++) {
            Cliente cliente = clientes.get(i);
            data[i][0] = cliente.getId();
            data[i][1] = cliente.getNombre();
            data[i][2] = cliente.getTelefono(); // Usar teléfono en lugar de email
            data[i][3] = cliente.getTelefono();
            data[i][4] = "N/A"; // Dirección no disponible en el modelo actual
            data[i][5] = "01/07/2025"; // Fecha placeholder
            data[i][6] = "Editar | Ver";
        }
        
        return data;
    }
    
    private void showAddClientDialog() {
        JDialog dialog = new JDialog(this, "Agregar Nuevo Cliente", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Campos del formulario
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextArea addressArea = new JTextArea(3, 20);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        
        // Layout del formulario
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1;
        panel.add(addressScroll, gbc);
        
        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        
        JButton saveBtn = UIUtils.createPrimaryButton("Guardar");
        JButton cancelBtn = UIUtils.createSecondaryButton("Cancelar");
        
        saveBtn.addActionListener(e -> {
            // Aquí se implementaría la lógica para guardar el cliente
            JOptionPane.showMessageDialog(dialog, "Cliente guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void refreshClients() {
        loadClientes();
        updateContentForModule("customers");
        JOptionPane.showMessageDialog(this, 
            "Lista de clientes actualizada",
            "Actualización", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private JPanel createSuppliersModule() {
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
