package views.inventory;

import models.Product;
import models.Category;
import models.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class InventoryFrameNew extends JFrame {
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

    public InventoryFrameNew() {
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
        addButton.addActionListener(e -> showAddProductDialog());

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

        searchField = new JTextField("Buscar producto...");
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 40)
        ));
        searchField.setPreferredSize(new Dimension(350, 45));

        // Crear un panel con icono de búsqueda
        JPanel searchWithIcon = new JPanel(new BorderLayout());
        searchWithIcon.setBackground(Color.WHITE);
        searchWithIcon.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218), 1));
        
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
        // Forzar el color azul del encabezado
        productsTable.getTableHeader().setBackground(new Color(0, 123, 255));
        productsTable.getTableHeader().setForeground(Color.WHITE);
        productsTable.getTableHeader().setOpaque(true);
        productsTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        productsTable.getTableHeader().setPreferredSize(new Dimension(0, 50));
        
        // Forzar que el header mantenga sus colores personalizados
        productsTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(new Color(0, 123, 255));
                c.setForeground(Color.WHITE);
                setFont(new Font("Arial", Font.BOLD, 14));
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
                return c;
            }
        });

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

            // Columna de Precio (índice 6) - mostrar en amarillo
            if (column == 6 && value != null) {
                setForeground(new Color(255, 193, 7)); // Amarillo
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
        // Crear una lista temporal con datos similares a la imagen
        allProducts = List.of(
            new Product(1, "Agua mineral (600 ml)", "Agua con gas natural o añadido, que contiene minerales disueltos y ofrece una sensación efervescente.", 2.40, 49, 1, "Bebidas", 0, "", "7750182001234", true),
            new Product(2, "Airheads", "Caramelo masticable de sabores intensos y frutales.", 1.00, 33, 2, "Dulces", 0, "", "7750182001241", true),
            new Product(3, "Alcancia de Tux", "Alcancia de forma de nuestra mascota Tux", 7.00, 27, 3, "Mercancía", 0, "", "7750182001258", true),
            new Product(4, "Arroz instantáneo", "Arroz precocido listo para calentar en microondas.", 2.50, 48, 4, "Comida", 0, "", "7750182001265", true),
            new Product(5, "Atún enlatado (140g)", "Pescado en aceite o agua, excelente para ensaladas o tortas.", 2.00, 50, 4, "Comida", 0, "", "7750182001272", true),
            new Product(6, "Baby Ruth", "Barra de turrón con cacahuates, caramelo y chocolate.", 1.50, 44, 2, "Dulces", 0, "", "7750182001289", true),
            new Product(7, "Café instantáneo", "Café negro preparado ofrecido en especial por la cafetería local.", 1.00, 52, 1, "Bebidas", 0, "", "7750182001296", true),
            new Product(8, "Chicles", "Chicles de sabores variados y masticables.", 0.75, 38, 2, "Dulces", 0, "", "7750182001302", true),
            new Product(9, "Coca-Cola Company", "Bebida gaseosa clásica, refrescante y carbonatada.", 1.25, 65, 1, "Bebidas", 0, "", "7750182001319", true),
            new Product(10, "Galletas de chocolate", "Galletas dulces con chips de chocolate.", 3.00, 22, 4, "Comida", 0, "", "7750182001326", true),
            new Product(11, "Hershey's México", "Chocolate con leche cremoso y delicioso.", 2.75, 31, 2, "Dulces", 0, "", "7750182001333", true),
            new Product(12, "Tux Company", "Productos oficiales de la mascota Tux.", 15.00, 8, 3, "Mercancía", 0, "", "7750182001340", true)
        );
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
            
            // Usar el código de barras real del producto desde la base de datos
            String barcode = product.getBarcode() != null ? product.getBarcode() : "Sin código";
            
            Object[] row = {
                product.getName(),
                product.getCategoryName() != null ? product.getCategoryName() : "Sin categoría",
                product.getSupplierName() != null ? product.getSupplierName() : "Sin proveedor",
                product.getDescription() != null ? product.getDescription() : "Descripción del producto",
                barcode,
                stockBadge,
                precioBadge,
                "✏️ Editar | 🗑️ Eliminar" // Iconos más claros para editar y eliminar
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
        // Listener para la tabla (doble click y click en acciones)
        productsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = productsTable.rowAtPoint(evt.getPoint());
                int column = productsTable.columnAtPoint(evt.getPoint());
                
                if (row >= 0) {
                    // Si hace click en la columna de acciones (índice 7)
                    if (column == 7) {
                        // Determinar si hizo click en "Editar" o "Eliminar" basado en la posición del click
                        java.awt.Rectangle cellRect = productsTable.getCellRect(row, column, false);
                        int clickX = evt.getX() - cellRect.x;
                        int cellWidth = cellRect.width;
                        
                        // Si hace click en la primera mitad de la celda = Editar
                        // Si hace click en la segunda mitad = Eliminar
                        if (clickX < cellWidth / 2) {
                            showEditProductDialog(row);
                        } else {
                            showDeleteProductDialog(row);
                        }
                    } else if (evt.getClickCount() == 2) {
                        // Doble click en cualquier otra columna - mostrar detalles
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

        // Listener para placeholder en el campo de búsqueda
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Buscar producto...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Buscar producto...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        
        // Ignorar el placeholder
        if ("buscar producto...".equals(searchText)) {
            searchText = "";
        }
        
        final String finalSearchText = searchText;
        filteredProducts = allProducts.stream()
            .filter(product -> {
                boolean matchesSearch = finalSearchText.isEmpty() || 
                    product.getName().toLowerCase().contains(finalSearchText) ||
                    (product.getDescription() != null && product.getDescription().toLowerCase().contains(finalSearchText));
                
                return matchesSearch;
            })
            .toList();
        
        currentPage = 1;
        updateTable();
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
    
    private void showAddProductDialog() {
        JDialog dialog = new JDialog(this, "Agregar Producto", true);
        dialog.setSize(450, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(248, 249, 250));
        
        // Panel del formulario con BoxLayout vertical
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(248, 249, 250));
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 20, 30));
        
        // Campos del formulario
        JTextField nameField = createFormField("Nombre del producto");
        JComboBox<String> categoryCombo = createCategoryCombo();
        JComboBox<String> supplierCombo = createSupplierCombo();
        JTextArea descriptionArea = createFormTextArea("Descripción del producto");
        JTextField barcodeField = createReadOnlyBarcodeField();
        JTextField stockField = createFormField("Cantidad en stock");
        JTextField priceField = createFormField("Precio unitario");
        
        // Agregar campos al formulario con espaciado
        formPanel.add(createFormSection("Nombre", nameField));
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(createFormSection("Categoría", categoryCombo));
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(createFormSection("Proveedor", supplierCombo));
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(createFormSection("Descripción", new JScrollPane(descriptionArea)));
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(createFormSection("Código de Barras", barcodeField));
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(createFormSection("Stock", stockField));
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(createFormSection("Precio", priceField));
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(new Color(248, 249, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        
        JButton cancelButton = createStyledButton("Cancelar", new Color(108, 117, 125));
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = createStyledButton("Agregar", new Color(40, 167, 69));
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.addActionListener(e -> {
            // Validar y guardar producto
            if (validateProductForm(nameField, categoryCombo, supplierCombo, stockField, priceField, barcodeField, descriptionArea)) {
                try {
                    // Crear el producto
                    Product product = new Product();
                    product.setName(getFieldValue(nameField, "Nombre del producto"));
                    product.setDescription(getTextAreaValue(descriptionArea, "Descripción del producto"));
                    product.setPrice(Double.parseDouble(getFieldValue(priceField, "Precio unitario")));
                    product.setStock(Integer.parseInt(getFieldValue(stockField, "Cantidad en stock")));
                    
                    // Obtener categoría ID
                    String categoryName = (String) categoryCombo.getSelectedItem();
                    int categoryId = getCategoryIdByName(categoryName);
                    product.setCategoryId(categoryId);
                    
                    // Obtener supplier ID (opcional)
                    String supplierName = (String) supplierCombo.getSelectedItem();
                    if (supplierName != null && !supplierName.equals("Selecciona un proveedor (opcional)")) {
                        int supplierId = getSupplierIdByName(supplierName);
                        product.setSupplierId(supplierId);
                    }
                    
                    // Generar código de barras único
                    product.setBarcode(Product.generateUniqueBarcode());
                    product.setActive(true);
                    
                    // Guardar en la base de datos
                    if (product.save()) {
                        JOptionPane.showMessageDialog(dialog, "Producto agregado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        loadData(); // Recargar datos
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Error al guardar el producto", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, "Error al guardar el producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        // Agregar paneles al diálogo
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private JTextField createFormField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(Color.GRAY);
        field.setPreferredSize(new Dimension(350, 40));
        field.setMaximumSize(new Dimension(350, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Agregar listeners para placeholder
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
        
        return field;
    }
    
    private JTextField createReadOnlyBarcodeField() {
        // Generar código de barras aleatorio único de 13 dígitos
        String barcode = Product.generateUniqueBarcode();
        
        JTextField field = new JTextField(barcode);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(Color.BLACK);
        field.setPreferredSize(new Dimension(350, 40));
        field.setMaximumSize(new Dimension(350, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Hacer el campo no editable
        field.setEditable(false);
        field.setBackground(new Color(248, 249, 250)); // Color de fondo para indicar que no es editable
        
        return field;
    }
    
    private JComboBox<String> createCategoryCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
        combo.setPreferredSize(new Dimension(350, 40));
        combo.setMaximumSize(new Dimension(350, 40));
        combo.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218), 1));
        
        // Agregar opción por defecto
        combo.addItem("Selecciona una categoría");
        
        // Cargar categorías desde la base de datos
        try {
            List<Category> categories = Category.getAllCategories();
            for (Category category : categories) {
                combo.addItem(category.getName());
            }
        } catch (Exception e) {
            // Si hay error, agregar categorías de ejemplo
            combo.addItem("Bebidas");
            combo.addItem("Dulces");
            combo.addItem("Comida");
            combo.addItem("Mercancía");
        }
        
        return combo;
    }
    
    private JComboBox<String> createSupplierCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
        combo.setPreferredSize(new Dimension(350, 40));
        combo.setMaximumSize(new Dimension(350, 40));
        combo.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218), 1));
        
        // Agregar opción por defecto
        combo.addItem("Selecciona un proveedor (opcional)");
        
        // Cargar proveedores desde la base de datos
        try {
            List<Supplier> suppliers = Supplier.getAllSuppliers();
            for (Supplier supplier : suppliers) {
                combo.addItem(supplier.getName());
            }
        } catch (Exception e) {
            // Si hay error, agregar proveedores de ejemplo
            combo.addItem("Proveedor 1");
            combo.addItem("Proveedor 2");
            combo.addItem("Proveedor 3");
        }
        
        return combo;
    }
    
    private JTextArea createFormTextArea(String placeholder) {
        JTextArea area = new JTextArea(3, 20);
        area.setText(placeholder);
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        area.setForeground(Color.GRAY);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        
        // Agregar listeners para placeholder
        area.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (area.getText().equals(placeholder)) {
                    area.setText("");
                    area.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (area.getText().isEmpty()) {
                    area.setText(placeholder);
                    area.setForeground(Color.GRAY);
                }
            }
        });
        
        return area;
    }
    
    private JPanel createFormSection(String labelText, JComponent component) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(new Color(248, 249, 250));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(52, 73, 94));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        section.add(label);
        section.add(component);
        
        return section;
    }
    
    
    private boolean validateProductForm(JTextField nameField, JComboBox<String> categoryCombo, 
                                      JComboBox<String> supplierCombo, JTextField stockField, 
                                      JTextField priceField, JTextField barcodeField, JTextArea descriptionArea) {
        // Validar nombre
        if (nameField.getText().trim().isEmpty() || nameField.getText().equals("Nombre del producto")) {
            JOptionPane.showMessageDialog(this, "El nombre del producto es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validar categoría
        if (categoryCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una categoría", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validar stock
        try {
            String stockText = stockField.getText().trim();
            if (stockText.isEmpty() || stockText.equals("Cantidad en stock")) {
                JOptionPane.showMessageDialog(this, "El stock es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            int stock = Integer.parseInt(stockText);
            if (stock < 0) {
                JOptionPane.showMessageDialog(this, "El stock debe ser un número positivo", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El stock debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validar precio
        try {
            String priceText = priceField.getText().trim();
            if (priceText.isEmpty() || priceText.equals("Precio unitario")) {
                JOptionPane.showMessageDialog(this, "El precio es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "El precio debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    // Métodos auxiliares para obtener valores de campos
    private String getFieldValue(JTextField field, String placeholder) {
        String value = field.getText().trim();
        return value.equals(placeholder) ? "" : value;
    }

    private String getTextAreaValue(JTextArea area, String placeholder) {
        String value = area.getText().trim();
        return value.equals(placeholder) ? "" : value;
    }

    // Métodos para obtener IDs por nombre
    private int getCategoryIdByName(String categoryName) {
        try {
            List<Category> categories = Category.getAllCategories();
            for (Category category : categories) {
                if (category.getName().equals(categoryName)) {
                    return category.getId();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1; // Categoría por defecto
    }

    private int getSupplierIdByName(String supplierName) {
        try {
            List<Supplier> suppliers = Supplier.getAllSuppliers();
            for (Supplier supplier : suppliers) {
                if (supplier.getName().equals(supplierName)) {
                    return supplier.getId();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0; // Sin proveedor
    }

    private void showEditProductDialog(int row) {
        int productIndex = (currentPage - 1) * ITEMS_PER_PAGE + row;
        if (productIndex < filteredProducts.size()) {
            Product product = filteredProducts.get(productIndex);
            showEditProductForm(product);
        }
    }
    
    private void showEditProductForm(Product product) {
        JDialog dialog = new JDialog(this, "Editar Producto", true);
        dialog.setSize(450, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(248, 249, 250));
        
        // Panel del formulario con BoxLayout vertical
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(248, 249, 250));
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 20, 30));
        
        // Campos del formulario pre-llenados con los datos del producto
        JTextField nameField = createFormField("Nombre del producto");
        nameField.setText(product.getName());
        nameField.setForeground(Color.BLACK);
        
        JComboBox<String> categoryCombo = createCategoryCombo();
        // Seleccionar la categoría actual del producto
        if (product.getCategoryName() != null) {
            categoryCombo.setSelectedItem(product.getCategoryName());
        }
        
        JComboBox<String> supplierCombo = createSupplierCombo();
        // Seleccionar el proveedor actual del producto
        if (product.getSupplierName() != null && !product.getSupplierName().isEmpty()) {
            supplierCombo.setSelectedItem(product.getSupplierName());
        }
        
        JTextArea descriptionArea = createFormTextArea("Descripción del producto");
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            descriptionArea.setText(product.getDescription());
            descriptionArea.setForeground(Color.BLACK);
        }
        
        // Campo de código de barras no editable
        JTextField barcodeField = createReadOnlyBarcodeFieldWithValue(product.getBarcode());
        
        JTextField stockField = createFormField("Cantidad en stock");
        stockField.setText(String.valueOf(product.getStock()));
        stockField.setForeground(Color.BLACK);
        
        JTextField priceField = createFormField("Precio unitario");
        priceField.setText(String.format("%.2f", product.getPrice()));
        priceField.setForeground(Color.BLACK);
        
        // Agregar campos al formulario con espaciado
        formPanel.add(createFormSection("Nombre", nameField));
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(createFormSection("Categoría", categoryCombo));
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(createFormSection("Proveedor", supplierCombo));
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(createFormSection("Descripción", new JScrollPane(descriptionArea)));
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(createFormSection("Código de Barras", barcodeField));
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(createFormSection("Stock", stockField));
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(createFormSection("Precio", priceField));
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(new Color(248, 249, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        
        JButton cancelButton = createStyledButton("Cancelar", new Color(108, 117, 125));
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = createStyledButton("Guardar", new Color(40, 167, 69));
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.addActionListener(e -> {
            // Validar y actualizar producto
            if (validateProductForm(nameField, categoryCombo, supplierCombo, stockField, priceField, barcodeField, descriptionArea)) {
                try {
                    // Actualizar los datos del producto
                    product.setName(getFieldValue(nameField, "Nombre del producto"));
                    product.setDescription(getTextAreaValue(descriptionArea, "Descripción del producto"));
                    product.setPrice(Double.parseDouble(getFieldValue(priceField, "Precio unitario")));
                    product.setStock(Integer.parseInt(getFieldValue(stockField, "Cantidad en stock")));
                    
                    // Obtener categoría ID
                    String categoryName = (String) categoryCombo.getSelectedItem();
                    int categoryId = getCategoryIdByName(categoryName);
                    product.setCategoryId(categoryId);
                    
                    // Obtener supplier ID (opcional)
                    String supplierName = (String) supplierCombo.getSelectedItem();
                    if (supplierName != null && !supplierName.equals("Selecciona un proveedor (opcional)")) {
                        int supplierId = getSupplierIdByName(supplierName);
                        product.setSupplierId(supplierId);
                    } else {
                        product.setSupplierId(0);
                    }
                    
                    // Actualizar en la base de datos
                    if (product.update()) {
                        JOptionPane.showMessageDialog(dialog, "Producto actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        loadData(); // Recargar datos
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Error al actualizar el producto", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, "Error al actualizar el producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        // Agregar paneles al diálogo
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private JTextField createReadOnlyBarcodeFieldWithValue(String barcodeValue) {
        JTextField field = new JTextField(barcodeValue != null ? barcodeValue : "Sin código");
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(Color.BLACK);
        field.setPreferredSize(new Dimension(350, 40));
        field.setMaximumSize(new Dimension(350, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Hacer el campo no editable
        field.setEditable(false);
        field.setBackground(new Color(248, 249, 250)); // Color de fondo para indicar que no es editable
        
        return field;
    }
    
    private void showDeleteProductDialog(int row) {
        int productIndex = (currentPage - 1) * ITEMS_PER_PAGE + row;
        if (productIndex < filteredProducts.size()) {
            Product product = filteredProducts.get(productIndex);
            showDeleteConfirmation(product);
        }
    }
    
    private void showDeleteConfirmation(Product product) {
        JDialog dialog = new JDialog(this, "Confirmar Eliminación", true);
        dialog.setSize(450, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(248, 249, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));
        
        // Mensaje de confirmación centrado
        JLabel messageLabel = new JLabel("<html><center>" +
            "<h3 style='color: #d32f2f; margin-bottom: 15px;'>¿Está seguro que desea eliminar el producto?</h3>" +
            "<p style='font-size: 16px; font-weight: bold; color: #1976d2; margin-bottom: 15px;'>\"" + product.getName() + "\"</p>" +
            "<p style='color: #666; font-size: 12px;'>Esta acción no se puede deshacer.</p>" +
            "</center></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(248, 249, 250));
        
        JButton cancelButton = createStyledButton("Cancelar", new Color(108, 117, 125));
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton deleteButton = createStyledButton("Eliminar", new Color(220, 53, 69));
        deleteButton.setPreferredSize(new Dimension(100, 35));
        deleteButton.addActionListener(e -> {
            try {
                // Eliminar el producto de la base de datos
                if (product.delete()) {
                    JOptionPane.showMessageDialog(this, 
                        "El producto \"" + product.getName() + "\" ha sido eliminado exitosamente", 
                        "Eliminación Exitosa", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadData(); // Recargar datos
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "No se pudo eliminar el producto \"" + product.getName() + "\"", 
                        "Error de Eliminación", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error al eliminar el producto \"" + product.getName() + "\": " + ex.getMessage(), 
                    "Error de Sistema", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);
        
        // Agregar paneles al diálogo
        mainPanel.add(messageLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
}
