package views.dashboard;

import models.*;
import utils.CartManager;
import utils.SessionManager;
import utils.UIUtils;
import views.auth.LoginFrame;
import views.sales.SalesFrame;
import views.inventory.InventoryFrameNew;
import views.suppliers.SuppliersFrame;
import views.clients.ClientsFrame;
import views.categories.CategoriesFrame;
import config.DatabaseConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private JComboBox<String> clienteComboBox;
    private JLabel selectedClienteLabel;
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
    private List<Product> filteredProducts;
    private List<Category> categories;
    private CartManager cartManager;
    private Integer selectedClienteId;
    private JButton activeCategoryButton;
    
    // Paginación
    private int currentPage = 1;
    private int productsPerPage = 8;
    private JLabel pageLabel;
    private JButton prevPageButton;
    private JButton nextPageButton;
    
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
        
        // Cargar solo categorías inicialmente
        loadCategories();
        
        // Mostrar mensaje de bienvenida y cargar productos después
        SwingUtilities.invokeLater(() -> {
            // Mostrar mensaje de bienvenida primero
            int result = JOptionPane.showConfirmDialog(
                this,
                "¡Bienvenido " + sessionManager.getUsername() + "!",
                "Bienvenida",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // Después de que el usuario da OK, cargar los productos
            if (result == JOptionPane.OK_OPTION || result == JOptionPane.CANCEL_OPTION) {
                // Cargar productos después de confirmar el mensaje de bienvenida
                SwingUtilities.invokeLater(() -> {
                    loadProducts();
                    refreshCategoriesPanel();
                });
            }
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
    
    private void loadProducts() {
        try {
            System.out.println("Cargando productos...");
            products = Product.getAllProducts();
            System.out.println("Productos cargados: " + (products != null ? products.size() : 0));
            
            if (products != null && !products.isEmpty()) {
                filteredProducts = new ArrayList<>(products); // Inicializar productos filtrados
                currentPage = 1; // Resetear a la primera página
                System.out.println("Actualizando grid de productos...");
                
                // Forzar actualización en el hilo de la UI
                SwingUtilities.invokeLater(() -> {
                    updateProductsGrid();
                    // Forzar repintado del panel principal
                    if (productsGridPanel != null) {
                        productsGridPanel.revalidate();
                        productsGridPanel.repaint();
                    }
                    if (contentPanel != null) {
                        contentPanel.revalidate();
                        contentPanel.repaint();
                    }
                    repaint(); // Repintar toda la ventana
                });
                
                System.out.println("Grid actualizado correctamente");
            } else {
                System.out.println("No se encontraron productos en la base de datos");
                SwingUtilities.invokeLater(() -> {
                    updateProductsGrid(); // Esto mostrará el mensaje de "No hay productos"
                });
            }
        } catch (Exception e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                UIUtils.showErrorMessage(this, "Error al cargar productos: " + e.getMessage());
            });
        }
    }
    
    private void loadCategories() {
        try {
            categories = Category.getAllCategories();
        } catch (Exception e) {
            UIUtils.showErrorMessage(this, "Error al cargar categorías: " + e.getMessage());
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
        titleLabel.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
        
        JLabel breadcrumbLabel = new JLabel("Dashboard > Punto de Venta");
        breadcrumbLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        breadcrumbLabel.setForeground(Color.GRAY); // Color gris para mejor visibilidad
        
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(breadcrumbLabel);
        
        // Lado derecho del header
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(Color.WHITE);
        
        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateTimeLabel.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
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
        titleLabel.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
        
        searchField = new JTextField("Buscar productos...");
        searchField.setFont(UIUtils.INPUT_FONT);
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setForeground(Color.DARK_GRAY); // Color gris oscuro para mejor visibilidad
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Placeholder behavior y búsqueda
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Buscar productos...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
                }
            }
            
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Buscar productos...");
                    searchField.setForeground(Color.DARK_GRAY); // Color gris oscuro para mejor visibilidad
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
                    // Si no hay búsqueda, mostrar todos los productos y resetear categoría
                    if (products != null) {
                        filteredProducts = new ArrayList<>(products);
                        currentPage = 1;
                        updateProductsGrid();
                    }
                }
            }
        });
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchField, BorderLayout.EAST);
        
        // Panel de categorías como en la web
        JPanel categoriesPanel = createCategoriesPanel();
        
        // Grid de productos como en la web (sin scroll)
        productsGridPanel = new JPanel();
        productsGridPanel.setLayout(new GridLayout(2, 4, 15, 15)); // 2 filas x 4 columnas = 8 productos
        productsGridPanel.setBackground(Color.WHITE);
        productsGridPanel.setPreferredSize(new Dimension(800, 400)); // Tamaño fijo
        
        // Panel de paginación
        JPanel paginationPanel = createPaginationPanel();
        
        // Panel central que contiene categorías, productos y paginación
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(categoriesPanel, BorderLayout.NORTH);
        centerPanel.add(Box.createVerticalStrut(15), BorderLayout.CENTER);
        centerPanel.add(productsGridPanel, BorderLayout.CENTER);
        centerPanel.add(paginationPanel, BorderLayout.SOUTH);
        
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
            button.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
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
                    btn.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
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
        if (productsGridPanel == null) return;
        
        // Si no hay productos filtrados, usar la lista principal
        if (filteredProducts == null && products != null) {
            filteredProducts = new ArrayList<>(products);
        }
        
        // Si aún no hay productos, mostrar mensaje
        if (filteredProducts == null || filteredProducts.isEmpty()) {
            productsGridPanel.removeAll();
            JLabel noProductsLabel = new JLabel("No hay productos disponibles", SwingConstants.CENTER);
            noProductsLabel.setFont(UIUtils.SUBTITLE_FONT);
            noProductsLabel.setForeground(Color.GRAY); // Color gris para mejor visibilidad
            productsGridPanel.add(noProductsLabel);
            productsGridPanel.revalidate();
            productsGridPanel.repaint();
            return;
        }
        
        productsGridPanel.removeAll();
        
        // Calcular productos para la página actual
        int startIndex = (currentPage - 1) * productsPerPage;
        int endIndex = Math.min(startIndex + productsPerPage, filteredProducts.size());
        
        // Mostrar productos de la página actual
        for (int i = startIndex; i < endIndex; i++) {
            Product product = filteredProducts.get(i);
            JPanel productCard = createProductCard(product);
            productsGridPanel.add(productCard);
        }
        
        // Completar con paneles vacíos si es necesario para mantener el grid de 8
        int remainingSlots = productsPerPage - (endIndex - startIndex);
        for (int i = 0; i < remainingSlots; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(Color.WHITE);
            productsGridPanel.add(emptyPanel);
        }
        
        // Actualizar controles de paginación
        updatePaginationControls();
        
        productsGridPanel.revalidate();
        productsGridPanel.repaint();
    }
    
    private void searchProducts(String searchTerm) {
        List<Product> searchResults = Product.searchProducts(searchTerm);
        filteredProducts = new ArrayList<>(searchResults);
        currentPage = 1; // Resetear a la primera página
        updateProductsGrid();
        
        if (filteredProducts.isEmpty()) {
            productsGridPanel.removeAll();
            JLabel noResultsLabel = new JLabel("No se encontraron productos", SwingConstants.CENTER);
            noResultsLabel.setFont(UIUtils.SUBTITLE_FONT);
            noResultsLabel.setForeground(Color.GRAY); // Color gris para mejor visibilidad
            productsGridPanel.add(noResultsLabel);
            productsGridPanel.revalidate();
            productsGridPanel.repaint();
        }
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
        nameLabel.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
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
        stockLabel.setForeground(Color.GRAY); // Color gris para mejor visibilidad
        
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
        
        cartManager.addProduct(product, 1);
        updateCartDisplay();
        UIUtils.showSuccessMessage(this, "Producto agregado: " + product.getName());
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
        titleLabel.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
        
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
        
        // Panel de cliente
        JPanel clientePanel = new JPanel();
        clientePanel.setLayout(new BoxLayout(clientePanel, BoxLayout.Y_AXIS));
        clientePanel.setBackground(Color.WHITE);
        clientePanel.setBorder(BorderFactory.createTitledBorder("Cliente (Opcional)"));
        
        // Panel horizontal para campo de búsqueda, ComboBox y botón limpiar
        JPanel clienteInputPanel = new JPanel(new BorderLayout(5, 0));
        clienteInputPanel.setBackground(Color.WHITE);
        clienteInputPanel.setMaximumSize(new Dimension(320, 35));
        
        clienteSearchField = new JTextField();
        clienteSearchField.setFont(UIUtils.INPUT_FONT);
        clienteSearchField.setPreferredSize(new Dimension(150, 35));
        clienteSearchField.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
        clienteSearchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // ComboBox para seleccionar cliente
        clienteComboBox = new JComboBox<>();
        clienteComboBox.setFont(UIUtils.INPUT_FONT);
        clienteComboBox.setPreferredSize(new Dimension(100, 35));
        clienteComboBox.setBackground(Color.WHITE);
        clienteComboBox.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        clienteComboBox.addItem("Sin cliente");
        clienteComboBox.setSelectedIndex(0);
        
        // Botón para limpiar selección de cliente
        JButton clearClienteButton = new JButton("🗑️");
        clearClienteButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clearClienteButton.setPreferredSize(new Dimension(35, 35));
        clearClienteButton.setBackground(new Color(231, 76, 60));
        clearClienteButton.setForeground(Color.WHITE);
        clearClienteButton.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        clearClienteButton.setFocusPainted(false);
        clearClienteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearClienteButton.setToolTipText("Limpiar cliente seleccionado");
        
        // Panel para ComboBox y botón limpiar
        JPanel comboAndClearPanel = new JPanel(new BorderLayout(2, 0));
        comboAndClearPanel.setBackground(Color.WHITE);
        comboAndClearPanel.add(clienteComboBox, BorderLayout.CENTER);
        comboAndClearPanel.add(clearClienteButton, BorderLayout.EAST);
        
        // Añadir elementos al panel horizontal
        clienteInputPanel.add(clienteSearchField, BorderLayout.CENTER);
        clienteInputPanel.add(comboAndClearPanel, BorderLayout.EAST);
        
        // Listener para búsqueda dinámica de clientes - usando KeyListener en lugar de DocumentListener
        clienteSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
            private Timer searchTimer = new Timer(500, e -> updateClienteComboBox()); // 500ms de retraso
            
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchTimer.setRepeats(false);
                searchTimer.restart();
            }
        });
        
        // Listener para selección de cliente
        clienteComboBox.addActionListener(e -> {
            Object selectedItem = clienteComboBox.getSelectedItem();
            if (selectedItem != null && !selectedItem.toString().equals("Sin cliente")) {
                // Extraer ID del cliente seleccionado
                String clienteText = selectedItem.toString();
                if (clienteText.contains(" - ")) {
                    String[] parts = clienteText.split(" - ");
                    if (parts.length >= 2) {
                        try {
                            selectedClienteId = Integer.parseInt(parts[0]);
                            selectedClienteLabel.setText("Cliente: " + parts[1] + " (ID: " + selectedClienteId + ")");
                            selectedClienteLabel.setForeground(new Color(39, 174, 96)); // Verde para indicar selección
                        } catch (NumberFormatException ex) {
                            selectedClienteId = null;
                            selectedClienteLabel.setText("Cliente: Ninguno seleccionado");
                            selectedClienteLabel.setForeground(Color.GRAY); // Color gris para mejor visibilidad
                        }
                    }
                }
            } else {
                // Si selecciona "Sin cliente"
                selectedClienteId = null;
                selectedClienteLabel.setText("Cliente: Ninguno seleccionado");
                selectedClienteLabel.setForeground(Color.GRAY); // Color gris para mejor visibilidad
            }
        });
        
        // Listener para el botón de limpiar cliente
        clearClienteButton.addActionListener(e -> {
            selectedClienteId = null;
            selectedClienteLabel.setText("Cliente: Ninguno seleccionado");
            selectedClienteLabel.setForeground(Color.GRAY); // Color gris para mejor visibilidad
            clienteSearchField.setText("");
            clienteComboBox.removeAllItems();
            clienteComboBox.addItem("Sin cliente");
            clienteComboBox.setSelectedIndex(0);
        });
        
        clientePanel.add(clienteInputPanel);
        
        // Etiqueta para mostrar cliente seleccionado
        selectedClienteLabel = new JLabel("Cliente: Ninguno seleccionado");
        selectedClienteLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        selectedClienteLabel.setForeground(Color.GRAY); // Color gris para mejor visibilidad
        selectedClienteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectedClienteLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        clientePanel.add(Box.createVerticalStrut(5));
        clientePanel.add(selectedClienteLabel);
        
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
        subtotalLabel.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
        subtotalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        taxLabel = new JLabel("IVA (16%): $0.00");
        taxLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taxLabel.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
        taxLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Campo de monto recibido
        JLabel paymentLabel = new JLabel("Monto recibido:");
        paymentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        paymentLabel.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
        paymentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        amountPaidField = new JTextField();
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
    
    private JPanel createPaginationPanel() {
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        paginationPanel.setBackground(Color.WHITE);
        paginationPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        // Botón anterior
        prevPageButton = new JButton("← Anterior");
        prevPageButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        prevPageButton.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
        prevPageButton.setBackground(Color.WHITE);
        prevPageButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        prevPageButton.setFocusPainted(false);
        prevPageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        prevPageButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                updateProductsGrid();
            }
        });
        
        // Label de página actual
        pageLabel = new JLabel("Página 1 de 1");
        pageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pageLabel.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
        pageLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        // Botón siguiente
        nextPageButton = new JButton("Siguiente →");
        nextPageButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nextPageButton.setForeground(Color.WHITE);
        nextPageButton.setBackground(new Color(52, 152, 219));
        nextPageButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        nextPageButton.setFocusPainted(false);
        nextPageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextPageButton.addActionListener(e -> {
            int totalPages = getTotalPages();
            if (currentPage < totalPages) {
                currentPage++;
                updateProductsGrid();
            }
        });
        
        paginationPanel.add(prevPageButton);
        paginationPanel.add(pageLabel);
        paginationPanel.add(nextPageButton);
        
        return paginationPanel;
    }
    
    private void updatePaginationControls() {
        if (pageLabel == null || prevPageButton == null || nextPageButton == null) return;
        
        int totalPages = getTotalPages();
        
        // Actualizar label
        pageLabel.setText("Página " + currentPage + " de " + totalPages);
        
        // Actualizar estado de botones
        prevPageButton.setEnabled(currentPage > 1);
        nextPageButton.setEnabled(currentPage < totalPages);
        
        // Cambiar estilos según estado
        if (prevPageButton.isEnabled()) {
            prevPageButton.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
            prevPageButton.setBackground(Color.WHITE);
        } else {
            prevPageButton.setForeground(Color.GRAY); // Color gris para mejor visibilidad
            prevPageButton.setBackground(new Color(248, 249, 250));
        }
        
        if (nextPageButton.isEnabled()) {
            nextPageButton.setForeground(Color.WHITE);
            nextPageButton.setBackground(new Color(52, 152, 219));
        } else {
            nextPageButton.setForeground(Color.GRAY); // Color gris para mejor visibilidad
            nextPageButton.setBackground(new Color(248, 249, 250));
        }
    }
    
    private int getTotalPages() {
        if (filteredProducts == null || filteredProducts.isEmpty()) return 1;
        return (int) Math.ceil((double) filteredProducts.size() / productsPerPage);
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
        cartManager.getTableModel().addTableModelListener(e -> updateCartDisplay());
        
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
                            updateCartDisplay();
                        }
                    }
                }
            }
        });
    }
    
    private void switchModule(String module, JButton activeButton) {
        // Caso especial para Ventas - abrir ventana independiente
        if ("sales".equals(module)) {
            SalesFrame.openSalesWindow();
            return;
        }
        // Caso especial para Inventario - abrir ventana independiente
        if ("inventory".equals(module)) {
            openInventoryWindow();
            return;
        }
        // Caso especial para Proveedores - abrir ventana independiente
        if ("providers".equals(module)) {
            openSuppliersWindow();
            return;
        }
        // Caso especial para Clientes - abrir ventana independiente
        if ("customers".equals(module)) {
            openClientsWindow();
            return;
        }
        // Caso especial para Categorías - abrir ventana independiente
        if ("categories".equals(module)) {
            openCategoriesWindow();
            return;
        }
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
                notImplementedLabel.setForeground(Color.GRAY); // Color gris para mejor visibilidad
                contentArea.add(notImplementedLabel, BorderLayout.CENTER);
        }
        
        return contentArea;
    }
    
    private JPanel createInventoryModule() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("📦 Módulo de Inventario", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
        
        JLabel descLabel = new JLabel("Gestión de productos y stock", SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setForeground(Color.GRAY); // Color gris para mejor visibilidad
        
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
        titleLabel.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
        
        JLabel descLabel = new JLabel("Gestión de categorías de productos", SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setForeground(Color.GRAY); // Color gris para mejor visibilidad
        
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
        titleLabel.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
        
        JLabel descLabel = new JLabel("Historial y reportes de ventas", SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setForeground(Color.GRAY); // Color gris para mejor visibilidad
        
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
        titleLabel.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
        
        JLabel descLabel = new JLabel("Gestión de clientes registrados", SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setForeground(Color.GRAY); // Color gris para mejor visibilidad
        
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
        titleLabel.setForeground(Color.BLACK); // Texto negro para mejor visibilidad
        
        JLabel descLabel = new JLabel("Gestión de proveedores", SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setForeground(Color.GRAY); // Color gris para mejor visibilidad
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(descLabel);
        centerPanel.add(Box.createVerticalGlue());
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Agregar mouse listener para abrir la ventana de proveedores
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openSuppliersWindow();
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(240, 245, 250));
                centerPanel.setBackground(new Color(240, 245, 250));
                panel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBackground(Color.WHITE);
                centerPanel.setBackground(Color.WHITE);
                panel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
        });
        
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
    
    private void updateClienteComboBox() {
        String searchText = clienteSearchField.getText().trim();
        
        // No buscar si el texto está vacío o es muy corto
        if (searchText.isEmpty() || searchText.length() < 2) {
            SwingUtilities.invokeLater(() -> {
                if (clienteComboBox.getItemCount() > 1) {
                    clienteComboBox.removeAllItems();
                    clienteComboBox.addItem("Sin cliente");
                }
            });
            return;
        }
        
        // Ejecutar búsqueda en un hilo separado para no bloquear la UI
        new Thread(() -> {
            try {
                // Buscar clientes que coincidan con ID o nombre
                List<Cliente> clientesEncontrados = searchClientes(searchText);
                
                // Actualizar ComboBox en el hilo de la UI
                SwingUtilities.invokeLater(() -> {
                    clienteComboBox.removeAllItems();
                    clienteComboBox.addItem("Sin cliente");
                    
                    for (Cliente cliente : clientesEncontrados) {
                        String displayText = cliente.getId() + " - " + cliente.getNombre();
                        clienteComboBox.addItem(displayText);
                    }
                });
                
            } catch (Exception e) {
                System.err.println("Error al actualizar ComboBox de clientes: " + e.getMessage());
            }
        }).start();
    }
    
    private List<Cliente> searchClientes(String searchText) {
        List<Cliente> results = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            
            // Verificar si el texto de búsqueda es un número (búsqueda por ID exacto)
            boolean isNumeric = searchText.matches("\\d+");
            String sql;
            
            if (isNumeric) {
                // Búsqueda por ID exacto y nombre con LIKE
                sql = "SELECT id, nombre, telefono FROM clientes WHERE " +
                      "id = ? OR nombre LIKE ? ORDER BY " +
                      "CASE WHEN id = ? THEN 1 ELSE 2 END, nombre LIMIT 10";
                
                stmt = conn.prepareStatement(sql);
                int searchId = Integer.parseInt(searchText);
                String searchPattern = "%" + searchText + "%";
                stmt.setInt(1, searchId);
                stmt.setString(2, searchPattern);
                stmt.setInt(3, searchId);
            } else {
                // Búsqueda solo por nombre con LIKE
                sql = "SELECT id, nombre, telefono FROM clientes WHERE " +
                      "nombre LIKE ? ORDER BY nombre LIMIT 10";
                
                stmt = conn.prepareStatement(sql);
                String searchPattern = "%" + searchText + "%";
                stmt.setString(1, searchPattern);
            }
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("telefono")
                );
                results.add(cliente);
            }
            
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error al buscar clientes: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        
        return results;
    }
    
    private JPanel createCategoriesPanel() {
        JPanel categoriesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        categoriesPanel.setBackground(Color.WHITE);
        
        // Botón "Todos" activo por defecto
        JButton todosButton = createCategoryButton("Todos", true);
        todosButton.addActionListener(e -> {
            setActiveCategoryButton(todosButton);
            activeCategoryButton = todosButton;
            filteredProducts = new ArrayList<>(products); // Mostrar todos los productos
            currentPage = 1; // Resetear a la primera página
            updateProductsGrid();
        });
        categoriesPanel.add(todosButton);
        activeCategoryButton = todosButton; // Inicializar el botón activo
        
        // Agregar categorías dinámicamente si están cargadas
        if (categories != null) {
            for (Category category : categories) {
                JButton categoryButton = createCategoryButton(category.getName(), false);
                categoryButton.addActionListener(e -> {
                    setActiveCategoryButton(categoryButton);
                    activeCategoryButton = categoryButton;
                    filterProductsByCategory(category.getId());
                });
                categoriesPanel.add(categoryButton);
            }
        }
        
        return categoriesPanel;
    }
    
    private void filterProductsByCategory(int categoryId) {
        if (products == null) return;
        
        // Filtrar productos por categoría
        filteredProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.getCategoryId() == categoryId) {
                filteredProducts.add(product);
            }
        }
        
        currentPage = 1; // Resetear a la primera página
        updateProductsGrid();
    }
    
    private void refreshCategoriesPanel() {
        // Este método recarga el panel de categorías después de que se hayan cargado desde la base de datos
        if (contentPanel != null) {
            // Recrear la interfaz POS con las categorías actualizadas
            updateContentForModule("pos");
        }
    }
    
    private void updateCartDisplay() {
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
        clearCart(true); // Por defecto pedir confirmación
    }
    
    private void clearCart(boolean askConfirmation) {
        if (askConfirmation) {
            int option = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea limpiar el carrito?",
                "Confirmar acción",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (option != JOptionPane.YES_OPTION) {
                return; // No limpiar si el usuario cancela
            }
        }
        
        // Limpiar carrito sin confirmación
        cartManager.clearCart();
        updateCartDisplay();
        amountPaidField.setText("");
        selectedClienteId = null;
        clienteSearchField.setText("");
        clienteComboBox.removeAllItems();
        clienteComboBox.addItem("Sin cliente");
        clienteComboBox.setSelectedIndex(0);
        selectedClienteLabel.setText("Cliente: Ninguno seleccionado");
        selectedClienteLabel.setForeground(Color.GRAY); // Color gris para mejor visibilidad
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
            
            // Mostrar confirmación antes de procesar la venta
            String message = String.format(
                "<html><div style='text-align: center; font-family: Arial;'>" +
                "<h3 style='color: #1976d2; margin-bottom: 15px;'>💳 Confirmar Procesamiento de Venta</h3>" +
                "<p style='margin-bottom: 10px;'><b>Total a pagar:</b> <span style='color: #2e7d32; font-size: 16px;'>$%.2f</span></p>" +
                "<p style='margin-bottom: 10px;'><b>Monto recibido:</b> <span style='color: #1976d2;'>$%.2f</span></p>" +
                "<p style='margin-bottom: 15px;'><b>Cambio:</b> <span style='color: #f57c00;'>$%.2f</span></p>" +
                "<p style='color: #666; font-size: 12px;'>¿Confirma el procesamiento de esta venta?</p>" +
                "</div></html>",
                total, amountPaid, (amountPaid - total)
            );
            
            int result = JOptionPane.showConfirmDialog(
                this,
                message,
                "Confirmar Venta - Total: $" + String.format("%.2f", total),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            // Solo procesar si el usuario confirma
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Crear venta
            Sale sale = new Sale();
            sale.setUserId(sessionManager.getUserId());
            sale.setItems(cartManager.getSaleItems());
            sale.setAmountPaid(amountPaid);
            
            if (selectedClienteId != null) {
                sale.setClienteId(selectedClienteId);
            }
            
            // Guardar venta
            if (sale.save()) {
                UIUtils.showSuccessMessage(this, "¡Venta procesada exitosamente por $" + String.format("%.2f", total) + "!");
                
                // Limpiar carrito y campos sin pedir confirmación
                clearCart(false);
                
                // Recargar productos para actualizar stock
                loadProducts();
            } else {
                UIUtils.showErrorMessage(this, "Error al procesar la venta");
            }
            
        } catch (NumberFormatException e) {
            UIUtils.showErrorMessage(this, "Ingrese un monto válido");
        }
    }
    
    private void openInventoryWindow() {
        SwingUtilities.invokeLater(() -> {
            InventoryFrameNew inventoryFrame = new InventoryFrameNew();
            inventoryFrame.setVisible(true);
        });
    }

    private void openSuppliersWindow() {
        SwingUtilities.invokeLater(() -> {
            try {
                views.suppliers.SuppliersFrame suppliersFrame = new views.suppliers.SuppliersFrame();
                suppliersFrame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Error al abrir la ventana de proveedores: " + e.getMessage(),
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void openClientsWindow() {
        SwingUtilities.invokeLater(() -> {
            try {
                views.clients.ClientsFrame clientsFrame = new views.clients.ClientsFrame();
                clientsFrame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Error al abrir la ventana de clientes: " + e.getMessage(),
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void openCategoriesWindow() {
        CategoriesFrame categoriesFrame = new CategoriesFrame();
        categoriesFrame.setVisible(true);
    }
}
