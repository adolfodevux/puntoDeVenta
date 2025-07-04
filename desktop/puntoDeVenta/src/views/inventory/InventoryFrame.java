package views.inventory;

import models.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class InventoryFrame extends JFrame {
    private JTable productsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<Product> allProducts;
    private List<Product> filteredProducts;
    private int currentPage = 1;
    private static final int ITEMS_PER_PAGE = 10;
    private JLabel pageLabel;
    private JButton prevButton;
    private JButton nextButton;

    public InventoryFrame() {
        setTitle("Inventario de Productos - Punto de Venta");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);

        initializeComponents();
        loadData();
        setupEventListeners();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 242, 247));

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 242, 247));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header con título y botones
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel de búsqueda
        JPanel searchPanel = createSearchPanel();
        mainPanel.add(searchPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 242, 247));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Título
        JLabel titleLabel = new JLabel("Inventario de Productos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(52, 73, 94));

        // Panel de botones a la derecha
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonsPanel.setBackground(new Color(240, 242, 247));

        // Botón Agregar Producto
        JButton addButton = createStyledButton("+ Agregar Producto", new Color(76, 217, 100));
        addButton.setPreferredSize(new Dimension(160, 40));

        // Botón Regresar
        JButton backButton = createStyledButton("← Regresar", new Color(76, 217, 100));
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.addActionListener(e -> {
            this.dispose();
            // Mostrar el dashboard principal si está oculto
            SwingUtilities.invokeLater(() -> {
                for (Window window : Window.getWindows()) {
                    if (window instanceof JFrame && window.getClass().getSimpleName().equals("DashboardFrame")) {
                        window.setVisible(true);
                        window.toFront();
                        break;
                    }
                }
            });
        });

        buttonsPanel.add(addButton);
        buttonsPanel.add(backButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(new Color(240, 242, 247));

        // Panel de búsqueda centrado
        JPanel searchContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchContainer.setBackground(new Color(240, 242, 247));
        searchContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        searchField = new JTextField(25);
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        searchField.setPreferredSize(new Dimension(350, 45));

        // Crear un panel con icono de búsqueda
        JPanel searchWithIcon = new JPanel(new BorderLayout());
        searchWithIcon.setBackground(Color.WHITE);
        searchWithIcon.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218), 1));
        
        searchField.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 40));
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));
        
        searchWithIcon.add(searchField, BorderLayout.CENTER);
        searchWithIcon.add(searchIcon, BorderLayout.EAST);

        searchContainer.add(searchWithIcon);
        searchPanel.add(searchContainer, BorderLayout.NORTH);

        // Panel de la tabla
        JPanel tablePanel = createTablePanel();
        searchPanel.add(tablePanel, BorderLayout.CENTER);

        return searchPanel;
    }



    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        // Crear tabla con las columnas exactas de la imagen
        String[] columnNames = {"Nombre", "Categoría", "Proveedor", "Descripción", "Código de Barras", "Stock", "Precio", "Acciones"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productsTable = new JTable(tableModel);
        productsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        productsTable.setRowHeight(60);
        productsTable.setGridColor(new Color(230, 230, 230));
        productsTable.setShowGrid(true);
        productsTable.setSelectionBackground(new Color(230, 247, 255));
        productsTable.setBackground(Color.WHITE);

        // Configurar anchos de columnas según la imagen
        productsTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Nombre
        productsTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Categoría
        productsTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Proveedor
        productsTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Descripción
        productsTable.getColumnModel().getColumn(4).setPreferredWidth(140); // Código de Barras
        productsTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Stock
        productsTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Precio
        productsTable.getColumnModel().getColumn(7).setPreferredWidth(120); // Acciones

        // Renderer personalizado para coincidir con el diseño web
        productsTable.setDefaultRenderer(Object.class, new ProductTableCellRenderer());

        // Configurar header con el color azul de la imagen
        productsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        productsTable.getTableHeader().setBackground(new Color(52, 144, 220));
        productsTable.getTableHeader().setForeground(Color.WHITE);
        productsTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        productsTable.getTableHeader().setPreferredSize(new Dimension(0, 50));

        JScrollPane scrollPane = new JScrollPane(productsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Panel de paginación
        JPanel paginationPanel = createPaginationPanel();

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(paginationPanel, BorderLayout.SOUTH);

        return tablePanel;
    }

    private JPanel createPaginationPanel() {
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        paginationPanel.setBackground(Color.WHITE);
        paginationPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        prevButton = createStyledButton("« Anterior", new Color(108, 117, 125));
        prevButton.setPreferredSize(new Dimension(100, 35));
        prevButton.addActionListener(e -> previousPage());

        pageLabel = new JLabel("Página 1 de 1");
        pageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        pageLabel.setForeground(Color.BLACK);

        nextButton = createStyledButton("Siguiente »", new Color(108, 117, 125));
        nextButton.setPreferredSize(new Dimension(100, 35));
        nextButton.addActionListener(e -> nextPage());

        paginationPanel.add(prevButton);
        paginationPanel.add(pageLabel);
        paginationPanel.add(nextButton);

        return paginationPanel;
    }

    // Renderer personalizado para la tabla
    private class ProductTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // Colores base
            if (!isSelected) {
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
            } else {
                c.setBackground(new Color(230, 247, 255));
                c.setForeground(Color.BLACK);
            }

            // Columna de Stock (índice 5) - mostrar como badge verde
            if (column == 5 && value != null) {
                setOpaque(true);
                setBackground(new Color(76, 217, 100)); // Verde como en la imagen
                setForeground(Color.WHITE);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                setFont(new Font("Arial", Font.BOLD, 12));
                return this;
            }

            // Columna de Precio (índice 6) - mostrar en verde
            if (column == 6 && value != null) {
                setForeground(new Color(76, 217, 100)); // Verde
                setFont(new Font("Arial", Font.BOLD, 14));
                setHorizontalAlignment(SwingConstants.CENTER);
            }

            // Columna de Acciones (índice 7) - mostrar iconos
            if (column == 7) {
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.PLAIN, 16));
            }

            // Alineación por defecto
            if (column != 5 && column != 6 && column != 7) {
                setHorizontalAlignment(SwingConstants.LEFT);
                setFont(new Font("Arial", Font.PLAIN, 14));
            }

            return c;
        }
    }

    private void loadData() {
        // Cargar productos
        allProducts = Product.getAllProducts();
        
        // Si no hay productos, crear datos de prueba
        if (allProducts.isEmpty()) {
            createSampleData();
        }
        
        // Actualizar tabla
        filteredProducts = allProducts;
        updateTable();
    }

    private void createSampleData() {
        System.out.println("Creando datos de prueba para inventario...");
        // Aquí podrías insertar productos de ejemplo en la base de datos
        // Por ahora, creamos una lista temporal
        allProducts = List.of(
            new Product(1, "Laptop HP Pavilion", "Laptop para oficina", 850.00, 15, 1, "Electrónicos", 0, "", "1234567890123", true),
            new Product(2, "Mouse Inalámbrico", "Mouse ergonómico", 25.50, 50, 1, "Electrónicos", 0, "", "1234567890124", true),
            new Product(3, "Teclado Mecánico", "Teclado gaming", 120.00, 8, 1, "Electrónicos", 0, "", "1234567890125", true),
            new Product(4, "Monitor 24\"", "Monitor Full HD", 280.00, 25, 1, "Electrónicos", 0, "", "1234567890126", true),
            new Product(5, "Impresora Canon", "Impresora multifunción", 220.00, 12, 1, "Electrónicos", 0, "", "1234567890127", true),
            new Product(6, "Disco Duro 1TB", "Almacenamiento externo", 65.00, 30, 1, "Electrónicos", 0, "", "1234567890128", true),
            new Product(7, "Webcam HD", "Cámara web para videoconferencias", 45.00, 20, 1, "Electrónicos", 0, "", "1234567890129", true),
            new Product(8, "Auriculares Bluetooth", "Auriculares inalámbricos", 85.00, 35, 1, "Electrónicos", 0, "", "1234567890130", true),
            new Product(9, "Tablet Samsung", "Tablet de 10 pulgadas", 320.00, 18, 1, "Electrónicos", 0, "", "1234567890131", true),
            new Product(10, "Cargador Universal", "Cargador para múltiples dispositivos", 22.00, 40, 1, "Electrónicos", 0, "", "1234567890132", true),
            new Product(11, "Cable HDMI", "Cable de video de alta definición", 15.00, 60, 1, "Electrónicos", 0, "", "1234567890133", true),
            new Product(12, "Parlantes Bluetooth", "Parlantes portátiles", 55.00, 25, 1, "Electrónicos", 0, "", "1234567890134", true)
        );
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        
        filteredProducts = allProducts.stream()
            .filter(product -> {
                boolean matchesSearch = searchText.isEmpty() || 
                    product.getName().toLowerCase().contains(searchText) ||
                    product.getDescription().toLowerCase().contains(searchText);
                
                return matchesSearch;
            })
            .toList();
        
        currentPage = 1;
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        
        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredProducts.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            Product product = filteredProducts.get(i);
            
            // Crear el formato de stock con colores (según la imagen)
            String stockBadge = String.valueOf(product.getStock());
            String precioBadge = "$ " + String.format("%.2f", product.getPrice());
            
            // Simular códigos de barras como en la imagen
            String barcode = String.format("%013d", (long)(Math.random() * 9999999999999L));
            
            Object[] row = {
                product.getName(),
                product.getCategoryName() != null ? product.getCategoryName() : "Sin categoría",
                "Proveedor " + (i % 3 + 1), // Simular proveedores
                product.getDescription() != null ? product.getDescription() : "Descripción del producto",
                barcode,
                stockBadge,
                precioBadge,
                "Editar | Eliminar" // Iconos de editar y eliminar
            };
            
            tableModel.addRow(row);
        }
        
        updatePagination();
    }



    private void updatePagination() {
        int totalPages = (int) Math.ceil((double) filteredProducts.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;
        
        pageLabel.setText("Página " + currentPage + " de " + totalPages);
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < totalPages);
    }

    private void setupEventListeners() {
        // Listener para la tabla (doble click)
        productsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = productsTable.getSelectedRow();
                    if (row >= 0) {
                        showProductDetails(row);
                    }
                }
            }
        });

        // Listener para búsqueda en tiempo real
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                applyFilters();
            }
        });
    }

    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateTable();
        }
    }

    private void nextPage() {
        int totalPages = (int) Math.ceil((double) filteredProducts.size() / ITEMS_PER_PAGE);
        if (currentPage < totalPages) {
            currentPage++;
            updateTable();
        }
    }

    private void showProductDetails(int row) {
        int productIndex = (currentPage - 1) * ITEMS_PER_PAGE + row;
        if (productIndex < filteredProducts.size()) {
            Product product = filteredProducts.get(productIndex);
            showProductDetailsDialog(product);
        }
    }

    private void showProductDetailsDialog(Product product) {
        JDialog dialog = new JDialog(this, "Detalles del Producto", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        // Crear el contenido del diálogo con el mismo estilo que el de ventas
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 242, 247));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel messageLabel = new JLabel("<html><center><h2>Detalles del producto</h2><br>" +
            "<b>" + product.getName() + "</b><br><br>" +
            "<b>Categoría:</b> " + (product.getCategoryName() != null ? product.getCategoryName() : "Sin categoría") + "<br>" +
            "<b>Descripción:</b> " + (product.getDescription() != null ? product.getDescription() : "Sin descripción") + "<br>" +
            "<b>Precio:</b> $ " + String.format("%.2f", product.getPrice()) + "<br>" +
            "<b>Stock:</b> " + product.getStock() + " unidades<br>" +
            "<b>Estado:</b> " + (product.isLowStock() ? "Stock Bajo" : "Stock Normal") +
            "</center></html>");
        
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton closeButton = createStyledButton("Cerrar", new Color(108, 117, 125));
        closeButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 242, 247));
        buttonPanel.add(closeButton);
        
        mainPanel.add(messageLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
}
