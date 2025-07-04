package views.suppliers;

import models.Supplier;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class SuppliersFrame extends JFrame {
    private JTable suppliersTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<Supplier> allSuppliers;
    private List<Supplier> filteredSuppliers;
    private int totalSuppliers = 0;
    private int suppliersWithProducts = 0;

    public SuppliersFrame() {
        setTitle("Gestión de Proveedores - Punto de Venta");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);

        // Primero cargar los datos básicos (sin actualizar tabla)
        loadDataBasic();
        
        // Luego inicializar los componentes con los datos cargados
        initializeComponents();
        
        // Finalmente cargar la tabla
        updateTable();
        
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

        // Panel de contenido principal
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 242, 247));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Panel del título con icono
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(new Color(240, 242, 247));

        // Icono
        JLabel iconLabel = new JLabel("🚚");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 28));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        // Título
        JLabel titleLabel = new JLabel("Gestión de Proveedores");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(52, 73, 94));

        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);

        // Panel de botones a la derecha
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonsPanel.setBackground(new Color(240, 242, 247));

        // Botón Regresar al Dashboard
        JButton backButton = createStyledButton("← Regresar al Dashboard", new Color(108, 117, 125));
        backButton.setPreferredSize(new Dimension(200, 40));
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

        // Botón Agregar Proveedor
        JButton addButton = createStyledButton("+ Agregar Proveedor", new Color(40, 167, 69));
        addButton.setPreferredSize(new Dimension(160, 40));
        addButton.addActionListener(e -> showAddSupplierDialog());

        buttonsPanel.add(backButton);
        buttonsPanel.add(addButton);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(240, 242, 247));

        // Panel de estadísticas (tarjetas superiores)
        JPanel statsPanel = createStatsPanel();
        contentPanel.add(statsPanel, BorderLayout.NORTH);

        // Panel de búsqueda y filtros
        JPanel searchPanel = createSearchPanel();
        contentPanel.add(searchPanel, BorderLayout.CENTER);

        return contentPanel;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setBackground(new Color(240, 242, 247));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Tarjeta Total Proveedores
        JPanel totalCard = createStatsCard(
            String.valueOf(totalSuppliers), 
            "Total Proveedores", 
            new Color(52, 144, 220)
        );

        // Tarjeta Con Productos
        JPanel withProductsCard = createStatsCard(
            String.valueOf(suppliersWithProducts), 
            "Con Productos", 
            new Color(255, 193, 7)
        );

        statsPanel.add(totalCard);
        statsPanel.add(withProductsCard);

        return statsPanel;
    }

    private JPanel createStatsCard(String number, String label, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        // Número principal
        JLabel numberLabel = new JLabel(number);
        numberLabel.setFont(new Font("Arial", Font.BOLD, 48));
        numberLabel.setForeground(accentColor);
        numberLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Texto descriptivo
        JLabel descLabel = new JLabel(label);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        descLabel.setForeground(new Color(108, 117, 125));
        descLabel.setHorizontalAlignment(SwingConstants.LEFT);

        card.add(numberLabel, BorderLayout.CENTER);
        card.add(descLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(new Color(240, 242, 247));

        // Panel de búsqueda y filtros
        JPanel searchControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        searchControlsPanel.setBackground(Color.WHITE);
        searchControlsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Campo de búsqueda
        searchField = new JTextField("Busca");
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Botón Buscar
        JButton searchButton = createStyledButton("Buscar", new Color(0, 123, 255));
        searchButton.setPreferredSize(new Dimension(80, 35));
        searchButton.addActionListener(e -> applyFilters());

        // Botón Limpiar
        JButton clearButton = createStyledButton("Limpiar", new Color(108, 117, 125));
        clearButton.setPreferredSize(new Dimension(80, 35));
        clearButton.addActionListener(e -> clearFilters());

        searchControlsPanel.add(searchField);
        searchControlsPanel.add(searchButton);
        searchControlsPanel.add(clearButton);

        // Panel de la tabla
        JPanel tablePanel = createTablePanel();

        searchPanel.add(searchControlsPanel, BorderLayout.NORTH);
        searchPanel.add(tablePanel, BorderLayout.CENTER);

        return searchPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        // Crear tabla con las columnas de la imagen
        String[] columnNames = {"Proveedor", "Contacto", "Ubicación", "Acciones"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        suppliersTable = new JTable(tableModel);
        suppliersTable.setFont(new Font("Arial", Font.PLAIN, 14));
        suppliersTable.setRowHeight(80);
        suppliersTable.setGridColor(new Color(230, 230, 230));
        suppliersTable.setShowGrid(false);
        suppliersTable.setSelectionBackground(new Color(230, 247, 255));
        suppliersTable.setBackground(Color.WHITE);

        // Configurar anchos de columnas
        suppliersTable.getColumnModel().getColumn(0).setPreferredWidth(300); // Proveedor
        suppliersTable.getColumnModel().getColumn(1).setPreferredWidth(300); // Contacto
        suppliersTable.getColumnModel().getColumn(2).setPreferredWidth(250); // Ubicación
        suppliersTable.getColumnModel().getColumn(3).setPreferredWidth(250); // Acciones

        // Renderer personalizado
        suppliersTable.setDefaultRenderer(Object.class, new SupplierTableCellRenderer());

        // Configurar header
        suppliersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        suppliersTable.getTableHeader().setBackground(new Color(248, 249, 250));
        suppliersTable.getTableHeader().setForeground(new Color(52, 73, 94));
        suppliersTable.getTableHeader().setOpaque(true);
        suppliersTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        suppliersTable.getTableHeader().setPreferredSize(new Dimension(0, 50));

        JScrollPane scrollPane = new JScrollPane(suppliersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // Renderer personalizado para la tabla
    private class SupplierTableCellRenderer extends DefaultTableCellRenderer {
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

            setFont(new Font("Arial", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            // Columna de Acciones (índice 3) - mostrar botones
            if (column == 3) {
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.PLAIN, 12));
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }

            return c;
        }
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

    private void loadDataBasic() {
        // Cargar proveedores básicos para las estadísticas
        allSuppliers = Supplier.getAllSuppliers();
        totalSuppliers = allSuppliers.size();
        suppliersWithProducts = calculateSuppliersWithProducts();
        filteredSuppliers = allSuppliers;
    }

    private void loadData() {
        // Cargar proveedores
        allSuppliers = Supplier.getAllSuppliers();
        totalSuppliers = allSuppliers.size(); // Usar el tamaño de la lista cargada
        
        // Calcular proveedores con productos
        suppliersWithProducts = calculateSuppliersWithProducts();
        
        // Actualizar tabla
        filteredSuppliers = allSuppliers;
        if (tableModel != null) {
            updateTable();
        }
    }

    private void refreshData() {
        // Este método se usa para refrescar después de cambios
        loadData();
        updateStatsCards();
        updateTable();
    }

    private int calculateSuppliersWithProducts() {
        // Obtener el número real de proveedores con productos desde la base de datos
        return Supplier.getSuppliersWithProductsCount();
    }

    private void updateStatsCards() {
        // Los datos ya están cargados, solo necesitamos refrescar la interfaz
        SwingUtilities.invokeLater(() -> {
            revalidate();
            repaint();
        });
    }

    private void updateTable() {
        if (tableModel == null) {
            System.out.println("ERROR: tableModel es null");
            return;
        }
        
        if (filteredSuppliers == null) {
            System.out.println("ERROR: filteredSuppliers es null");
            return;
        }
        
        System.out.println("Actualizando tabla con " + filteredSuppliers.size() + " proveedores");
        
        tableModel.setRowCount(0);
        
        for (Supplier supplier : filteredSuppliers) {
            System.out.println("Agregando proveedor: " + supplier.getName());
            
            // Crear información de contacto
            String contactInfo = "<html><div style='line-height: 1.4;'>";
            if (supplier.getEmail() != null && !supplier.getEmail().isEmpty()) {
                contactInfo += "✉ " + supplier.getEmail() + "<br>";
            }
            if (supplier.getPhone() != null && !supplier.getPhone().isEmpty()) {
                contactInfo += "📞 " + supplier.getPhone();
            }
            contactInfo += "</div></html>";

            // Crear información del proveedor
            String providerInfo = "<html><div style='line-height: 1.4;'>";
            providerInfo += "<b>" + supplier.getName() + "</b><br>";
            if (supplier.getContactPerson() != null && !supplier.getContactPerson().isEmpty()) {
                providerInfo += "👤 " + supplier.getContactPerson();
            }
            providerInfo += "</div></html>";

            Object[] row = {
                providerInfo,
                contactInfo,
                "📍 " + supplier.getLocation(), // Ubicación real
                "👁 Ver   ✏ Editar   🗑 Eliminar" // Acciones
            };
            
            tableModel.addRow(row);
        }
        
        System.out.println("Tabla actualizada. Filas en tableModel: " + tableModel.getRowCount());
    }

    private void setupEventListeners() {
        // Listener para la tabla (click en acciones)
        suppliersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = suppliersTable.rowAtPoint(evt.getPoint());
                int column = suppliersTable.columnAtPoint(evt.getPoint());
                
                if (row >= 0 && column == 3) { // Columna de acciones
                    handleActionClick(row, evt.getX());
                }
            }
        });

        // Listener para placeholder en el campo de búsqueda
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Busca")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Busca");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        // Listener para búsqueda en tiempo real
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                scheduleSearch();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                scheduleSearch();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                scheduleSearch();
            }

            private void scheduleSearch() {
                // Debounce para evitar demasiadas búsquedas
                SwingUtilities.invokeLater(() -> {
                    if (!searchField.getText().equals("Busca")) {
                        applyFilters();
                    }
                });
            }
        });
    }

    private void handleActionClick(int row, int clickX) {
        if (row < filteredSuppliers.size()) {
            Supplier supplier = filteredSuppliers.get(row);
            
            // Determinar qué acción según la posición del click
            // Ver (primera parte), Editar (segunda parte), Eliminar (tercera parte)
            java.awt.Rectangle cellRect = suppliersTable.getCellRect(row, 3, false);
            int relativeX = clickX - cellRect.x;
            int cellWidth = cellRect.width;
            
            if (relativeX < cellWidth / 3) {
                // Ver detalles
                showSupplierDetails(supplier);
            } else if (relativeX < 2 * cellWidth / 3) {
                // Editar
                showEditSupplierDialog(supplier);
            } else {
                // Eliminar
                showDeleteSupplierDialog(supplier);
            }
        }
    }

    private void applyFilters() {
        String searchText = searchField.getText().trim();
        
        if ("busca".equalsIgnoreCase(searchText)) {
            searchText = "";
        }
        
        if (searchText.isEmpty()) {
            filteredSuppliers = allSuppliers;
        } else {
            // Usar el método de búsqueda del modelo para mejor rendimiento
            filteredSuppliers = Supplier.search(searchText);
        }
        
        updateTable();
    }

    private void clearFilters() {
        searchField.setText("Busca");
        searchField.setForeground(Color.GRAY);
        filteredSuppliers = allSuppliers;
        updateTable();
    }

    private void showAddSupplierDialog() {
        SupplierFormDialog dialog = new SupplierFormDialog(this, null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refreshData(); // Recargar datos para mostrar el nuevo proveedor
        }
    }

    private void showSupplierDetails(Supplier supplier) {
        SupplierDetailsDialog dialog = new SupplierDetailsDialog(this, supplier);
        dialog.setVisible(true);
        
        if (dialog.shouldRefresh()) {
            refreshData(); // Recargar datos si hubo cambios
        }
    }

    private void showEditSupplierDialog(Supplier supplier) {
        SupplierFormDialog dialog = new SupplierFormDialog(this, supplier);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refreshData(); // Recargar datos para mostrar los cambios
        }
    }

    private void showDeleteSupplierDialog(Supplier supplier) {
        int result = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de que desea eliminar el proveedor \"" + supplier.getName() + "\"?\n" +
            "Esta acción no se puede deshacer.",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            if (supplier.delete()) {
                JOptionPane.showMessageDialog(this,
                    "Proveedor eliminado exitosamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                refreshData(); // Recargar datos para reflejar la eliminación
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar el proveedor. Por favor, inténtelo de nuevo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
