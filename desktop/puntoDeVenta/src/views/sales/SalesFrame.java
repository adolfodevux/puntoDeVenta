package views.sales;

import models.Sale;
import utils.UIUtils;
import utils.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

public class SalesFrame extends JFrame {
    private JPanel summaryPanel;
    private JLabel totalSalesLabel, totalIncomeLabel, todaySalesLabel, todayIncomeLabel;
    private JTextField searchField;
    private JTextField dateFromField, dateToField;
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private JButton prevPageBtn, nextPageBtn;
    private JLabel pageInfoLabel;
    
    private List<Sale> allSales;
    private List<Sale> filteredSales;
    private int currentPage = 0;
    private final int ITEMS_PER_PAGE = 10;
    
    private DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    public SalesFrame() {
        initializeComponents();
        loadSalesData();
        updateSummaryCards();
        updateTable();
    }
    
    private void initializeComponents() {
        setTitle("Ventas - Punto de Venta");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel);
        
        // Header con título y botón volver
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Panel de contenido
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Panel de tarjetas resumen
        summaryPanel = createSummaryPanel();
        contentPanel.add(summaryPanel, BorderLayout.NORTH);
        
        // Panel central que contiene filtros y tabla
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(Color.WHITE);
        
        // Panel de filtros
        JPanel filtersPanel = createFiltersPanel();
        centerPanel.add(filtersPanel, BorderLayout.NORTH);
        
        // Panel de tabla con paginación
        JPanel tablePanel = createTablePanel();
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        
        contentPanel.add(centerPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Ventas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Botón volver al dashboard
        JButton backButton = UIUtils.createStyledButton("← Volver al Dashboard", new Color(41, 128, 185), Color.WHITE);
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
        headerPanel.add(backButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Tarjeta Total de Ventas
        JPanel totalSalesCard = createSummaryCard("Total de Ventas", "0", new Color(52, 152, 219));
        totalSalesLabel = (JLabel) ((JPanel) totalSalesCard.getComponent(0)).getComponent(1);
        panel.add(totalSalesCard);
        
        // Tarjeta Ingresos Totales
        JPanel totalIncomeCard = createSummaryCard("Ingresos Totales", "$0.00", new Color(46, 204, 113));
        totalIncomeLabel = (JLabel) ((JPanel) totalIncomeCard.getComponent(0)).getComponent(1);
        panel.add(totalIncomeCard);
        
        // Tarjeta Ventas del Día
        JPanel todaySalesCard = createSummaryCard("Ventas del Día", "0", new Color(155, 89, 182));
        todaySalesLabel = (JLabel) ((JPanel) todaySalesCard.getComponent(0)).getComponent(1);
        panel.add(todaySalesCard);
        
        // Tarjeta Ingresos del Día
        JPanel todayIncomeCard = createSummaryCard("Ingresos del Día", "$0.00", new Color(230, 126, 34));
        todayIncomeLabel = (JLabel) ((JPanel) todayIncomeCard.getComponent(0)).getComponent(1);
        panel.add(todayIncomeCard);
        
        return panel;
    }
    
    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(color);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(Color.WHITE);
        
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(valueLabel, BorderLayout.CENTER);
        
        card.add(contentPanel);
        return card;
    }
    
    private JPanel createFiltersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)), "Filtros"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(0, 80));
        
        JPanel filtersContent = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filtersContent.setBackground(Color.WHITE);
        
        // Campo de búsqueda
        JLabel searchLabel = new JLabel("Buscar:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 12));
        filtersContent.add(searchLabel);
        
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        searchField.addActionListener(e -> applyFilters());
        filtersContent.add(searchField);
        
        // Filtro de fecha desde
        JLabel fromLabel = new JLabel("Desde:");
        fromLabel.setFont(new Font("Arial", Font.BOLD, 12));
        filtersContent.add(fromLabel);
        
        dateFromField = new JTextField(10);
        dateFromField.setPreferredSize(new Dimension(100, 30));
        dateFromField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        dateFromField.setToolTipText("Formato: dd/mm/yyyy");
        dateFromField.addActionListener(e -> applyFilters());
        filtersContent.add(dateFromField);
        
        // Filtro de fecha hasta
        JLabel toLabel = new JLabel("Hasta:");
        toLabel.setFont(new Font("Arial", Font.BOLD, 12));
        filtersContent.add(toLabel);
        
        dateToField = new JTextField(10);
        dateToField.setPreferredSize(new Dimension(100, 30));
        dateToField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        dateToField.setToolTipText("Formato: dd/mm/yyyy");
        dateToField.addActionListener(e -> applyFilters());
        filtersContent.add(dateToField);
        
        // Botones
        JButton searchButton = UIUtils.createStyledButton("Buscar", new Color(52, 152, 219), Color.WHITE);
        searchButton.setPreferredSize(new Dimension(80, 30));
        searchButton.addActionListener(e -> applyFilters());
        filtersContent.add(searchButton);
        
        JButton clearButton = UIUtils.createStyledButton("Limpiar", new Color(149, 165, 166), Color.WHITE);
        clearButton.setPreferredSize(new Dimension(80, 30));
        clearButton.addActionListener(e -> clearFilters());
        filtersContent.add(clearButton);
        
        panel.add(filtersContent, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Historial de Ventas"));
        
        // Crear tabla
        String[] columnNames = {"#", "Usuario", "Cliente", "Total", "Método de Pago", "Fecha", "Estado", "Acciones"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Todas las celdas son no editables
            }
        };
        
        salesTable = new JTable(tableModel);
        salesTable.setRowHeight(45);
        salesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        salesTable.getTableHeader().setBackground(new Color(52, 73, 94));
        salesTable.getTableHeader().setForeground(Color.BLACK);
        salesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Configurar renderer para las celdas
        salesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Asegurar que siempre hay texto visible
                if (value != null) {
                    setText(value.toString());
                }
                
                // Configuración por defecto
                setOpaque(true);
                setHorizontalAlignment(LEFT);
                setFont(new Font("Arial", Font.PLAIN, 12));
                
                if (isSelected) {
                    // Fila seleccionada
                    setBackground(new Color(52, 152, 219));
                    setForeground(Color.WHITE);
                } else {
                    // Colores alternados para las filas no seleccionadas
                    if (row % 2 == 0) {
                        setBackground(Color.WHITE);
                    } else {
                        setBackground(new Color(248, 249, 250));
                    }
                    setForeground(Color.BLACK); // Texto negro por defecto
                    
                    // Estilo especial para la columna de estado
                    if (column == 6) { // Columna de estado
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        setText("COMPLETADA");
                        setHorizontalAlignment(CENTER);
                    }
                    
                    // Estilo para la columna de acciones
                    if (column == 7) { // Columna de acciones
                        setBackground(new Color(52, 152, 219));
                        setForeground(Color.WHITE);
                        setText("Ver | Imprimir");
                        setHorizontalAlignment(CENTER);
                    }
                }
                
                return this;
            }
        });
        
        // Configurar anchos de columnas
        TableColumnModel columnModel = salesTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // #
        columnModel.getColumn(1).setPreferredWidth(100); // Usuario
        columnModel.getColumn(2).setPreferredWidth(150); // Cliente
        columnModel.getColumn(3).setPreferredWidth(80);  // Total
        columnModel.getColumn(4).setPreferredWidth(120); // Método de Pago
        columnModel.getColumn(5).setPreferredWidth(140); // Fecha
        columnModel.getColumn(6).setPreferredWidth(100); // Estado
        columnModel.getColumn(7).setPreferredWidth(80);  // Acciones
        
        // Agregar listener para clics en la tabla
        salesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = salesTable.rowAtPoint(evt.getPoint());
                int col = salesTable.columnAtPoint(evt.getPoint());
                
                if (row >= 0 && col == 7) { // Click en columna de acciones
                    // Obtener el ID real de la venta basado en la fila
                    int actualIndex = (currentPage * ITEMS_PER_PAGE) + row;
                    if (actualIndex < filteredSales.size()) {
                        int saleId = filteredSales.get(actualIndex).getId();
                        showSaleDetailsDialog(saleId);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de paginación
        JPanel paginationPanel = createPaginationPanel();
        panel.add(paginationPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createPaginationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        
        prevPageBtn = UIUtils.createStyledButton("← Anterior", new Color(52, 152, 219), Color.WHITE);
        prevPageBtn.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateTable();
            }
        });
        panel.add(prevPageBtn);
        
        pageInfoLabel = new JLabel("Página 1 de 1");
        pageInfoLabel.setBorder(new EmptyBorder(0, 20, 0, 20));
        panel.add(pageInfoLabel);
        
        nextPageBtn = UIUtils.createStyledButton("Siguiente →", new Color(52, 152, 219), Color.WHITE);
        nextPageBtn.addActionListener(e -> {
            int totalPages = (int) Math.ceil((double) filteredSales.size() / ITEMS_PER_PAGE);
            if (currentPage < totalPages - 1) {
                currentPage++;
                updateTable();
            }
        });
        panel.add(nextPageBtn);
        
        return panel;
    }
    
    private void loadSalesData() {
        System.out.println("Cargando datos de ventas...");
        allSales = Sale.getAllSales();
        System.out.println("Ventas cargadas: " + (allSales != null ? allSales.size() : "null"));
        
        if (allSales == null) {
            allSales = new ArrayList<>();
        }
        
        // Si no hay datos reales, crear datos de prueba
        if (allSales.isEmpty()) {
            System.out.println("No hay ventas en la base de datos, creando datos de prueba...");
            allSales = createTestSales();
        }
        
        filteredSales = new ArrayList<>(allSales);
        System.out.println("Ventas filtradas inicializadas: " + filteredSales.size());
    }
    
    private List<Sale> createTestSales() {
        List<Sale> testSales = new ArrayList<>();
        String[] paymentMethods = {"Efectivo", "Tarjeta", "Transferencia"};
        String[] usernames = {"admin", "vendedor1", "cajero2"};
        
        try {
            for (int i = 1; i <= 38; i++) {
                Sale sale = new Sale();
                sale.setId(i);
                sale.setUserId(1);
                sale.setUsername(usernames[i % usernames.length]);
                sale.setClienteId(i % 5 + 1);
                sale.setClienteNombre(i % 3 == 0 ? "Sin cliente" : "Cliente " + (i % 5 + 1));
                sale.setTotalAmount(5.0 + (i * 1.5) + (Math.random() * 10));
                sale.setSubtotal(sale.getTotalAmount() / 1.19);
                sale.setTaxAmount(sale.getTotalAmount() - sale.getSubtotal());
                sale.setPaymentMethod(paymentMethods[i % paymentMethods.length]);
                sale.setAmountPaid(sale.getTotalAmount());
                sale.setChangeAmount(0.0);
                
                // Crear fechas más variadas
                int daysAgo = i % 30;
                int hoursAgo = i % 24;
                int minutesAgo = i % 60;
                sale.setSaleDate(java.time.LocalDateTime.now()
                    .minusDays(daysAgo)
                    .minusHours(hoursAgo)
                    .minusMinutes(minutesAgo));
                
                sale.setStatus("completed");
                sale.setNotes("Venta de prueba " + i);
                
                testSales.add(sale);
            }
        } catch (Exception e) {
            System.err.println("Error creando datos de prueba: " + e.getMessage());
            e.printStackTrace();
        }
        
        return testSales;
    }
    
    private void updateSummaryCards() {
        if (allSales == null || allSales.isEmpty()) {
            totalSalesLabel.setText("0");
            totalIncomeLabel.setText("$0.00");
            todaySalesLabel.setText("0");
            todayIncomeLabel.setText("$0.00");
            return;
        }
        
        // Calcular totales generales
        int totalSales = allSales.size();
        double totalIncome = allSales.stream().mapToDouble(Sale::getTotalAmount).sum();
        
        // Calcular totales del día actual
        LocalDate today = LocalDate.now();
        List<Sale> todaySales = allSales.stream()
            .filter(sale -> sale.getSaleDate() != null && sale.getSaleDate().toLocalDate().equals(today))
            .collect(Collectors.toList());
        
        int todaySalesCount = todaySales.size();
        double todayIncome = todaySales.stream().mapToDouble(Sale::getTotalAmount).sum();
        
        // Actualizar labels
        totalSalesLabel.setText(String.valueOf(totalSales));
        totalIncomeLabel.setText(currencyFormat.format(totalIncome));
        todaySalesLabel.setText(String.valueOf(todaySalesCount));
        todayIncomeLabel.setText(currencyFormat.format(todayIncome));
    }
    
    private void updateTable() {
        System.out.println("Actualizando tabla...");
        tableModel.setRowCount(0);
        
        if (filteredSales == null || filteredSales.isEmpty()) {
            System.out.println("No hay ventas para mostrar");
            updatePaginationControls();
            return;
        }
        
        System.out.println("Ventas filtradas: " + filteredSales.size());
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredSales.size());
        System.out.println("Mostrando desde " + startIndex + " hasta " + endIndex);
        
        for (int i = startIndex; i < endIndex; i++) {
            Sale sale = filteredSales.get(i);
            try {
                // Calcular número consecutivo global (la venta más reciente es #1)
                int consecutiveNumber = i + 1;
                
                Object[] row = {
                    consecutiveNumber, // Número consecutivo simple: 1, 2, 3, 4...
                    sale.getUsername() != null ? sale.getUsername() : "Usuario",
                    sale.getClienteNombre() != null ? sale.getClienteNombre() : "Sin cliente",
                    currencyFormat.format(sale.getTotalAmount()),
                    sale.getPaymentMethod() != null ? sale.getPaymentMethod() : "Efectivo",
                    sale.getSaleDate() != null ? dateFormat.format(java.sql.Timestamp.valueOf(sale.getSaleDate())) : "N/A",
                    "COMPLETADA",
                    "Ver |Imprimir" // Texto de acción
                };
                tableModel.addRow(row);
                System.out.println("Agregada fila " + i + ": #" + consecutiveNumber + " (ID real=" + sale.getId() + ")");
            } catch (Exception e) {
                System.err.println("Error agregando fila " + i + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("Filas agregadas a la tabla: " + tableModel.getRowCount());
        updatePaginationControls();
    }
    
    private void updatePaginationControls() {
        int totalPages = (int) Math.ceil((double) filteredSales.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;
        
        pageInfoLabel.setText("Página " + (currentPage + 1) + " de " + totalPages);
        prevPageBtn.setEnabled(currentPage > 0);
        nextPageBtn.setEnabled(currentPage < totalPages - 1);
    }
    
    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        // TODO: Implementar filtros de fecha en el futuro
        // String dateFrom = dateFromField.getText().trim();
        // String dateTo = dateToField.getText().trim();
        
        filteredSales = allSales.stream()
            .filter(sale -> {
                // Filtro de búsqueda por texto
                if (!searchText.isEmpty()) {
                    String saleInfo = (sale.getClienteNombre() != null ? sale.getClienteNombre() : "").toLowerCase() +
                                     (sale.getUsername() != null ? sale.getUsername() : "").toLowerCase() +
                                     String.valueOf(sale.getId()).toLowerCase() +
                                     sale.getPaymentMethod().toLowerCase();
                    if (!saleInfo.contains(searchText)) {
                        return false;
                    }
                }
                
                // Filtros de fecha (implementación básica)
                // Aquí podrías agregar lógica más sofisticada para el filtrado por fechas
                
                return true;
            })
            .collect(Collectors.toList());
        
        currentPage = 0;
        updateTable();
    }
    
    private void clearFilters() {
        searchField.setText("");
        dateFromField.setText("");
        dateToField.setText("");
        filteredSales = new ArrayList<>(allSales);
        currentPage = 0;
        updateTable();
    }
    
    private void showSaleDetailsDialog(int saleId) {
        // Buscar la venta por ID
        Sale selectedSale = filteredSales.stream()
            .filter(sale -> sale.getId() == saleId)
            .findFirst()
            .orElse(null);
        
        if (selectedSale == null) {
            JOptionPane.showMessageDialog(this, "No se pudo encontrar la venta seleccionada.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Crear diálogo de detalles con el diseño de la imagen
        JDialog detailsDialog = new JDialog(this, "Venta #" + saleId, true);
        detailsDialog.setSize(720, 650);
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.setResizable(false);
        
        // Panel principal con fondo gris claro
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 242, 247));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header con título y botón volver
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 242, 247));
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("📋 Venta #" + saleId);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(52, 73, 94));
        
        JButton backButton = UIUtils.createStyledButton("← Volver a Ventas", new Color(52, 152, 219), Color.WHITE);
        backButton.addActionListener(e -> detailsDialog.dispose());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);
        
        // Panel central con las tarjetas
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        centerPanel.setBackground(new Color(240, 242, 247));
        
        // Tarjeta 1: Información General
        JPanel infoCard = createInfoCard("ℹ️ Información General", selectedSale);
        centerPanel.add(infoCard);
        
        // Tarjeta 2: Información de Pago
        JPanel paymentCard = createPaymentCard("💳 Información de Pago", selectedSale);
        centerPanel.add(paymentCard);
        
        // Tarjeta 3: Productos Vendidos (ocupa las dos columnas de abajo)
        JPanel productsCard = createProductsCard("📦 Productos Vendidos", selectedSale);
        centerPanel.add(productsCard);
        
        // Panel vacío para completar el grid
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(new Color(240, 242, 247));
        centerPanel.add(emptyPanel);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        detailsDialog.add(mainPanel);
        detailsDialog.setVisible(true);
    }
    
    private JPanel createInfoCard(String title, Sale sale) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Header de la tarjeta
        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerLabel.setForeground(new Color(52, 73, 94));
        headerLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Panel de contenido
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Información
        addCardRow(contentPanel, gbc, 0, "ID de Venta:", "#" + sale.getId());
        addCardRow(contentPanel, gbc, 1, "Fecha:", dateFormat.format(java.sql.Timestamp.valueOf(sale.getSaleDate())));
        addCardRow(contentPanel, gbc, 2, "Usuario:", sale.getUsername() != null ? sale.getUsername() : "admin");
        addCardRow(contentPanel, gbc, 3, "Cliente:", sale.getClienteNombre() != null ? sale.getClienteNombre() : "Sin cliente");
        
        // Estado con color
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        JLabel estadoLabel = new JLabel("Estado:");
        estadoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        estadoLabel.setForeground(new Color(108, 117, 125));
        contentPanel.add(estadoLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        JLabel estadoValue = new JLabel("COMPLETADA");
        estadoValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        estadoValue.setForeground(Color.WHITE);
        estadoValue.setBackground(new Color(40, 167, 69));
        estadoValue.setOpaque(true);
        estadoValue.setBorder(new EmptyBorder(4, 8, 4, 8));
        contentPanel.add(estadoValue, gbc);
        
        card.add(headerLabel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }
    
    private JPanel createPaymentCard(String title, Sale sale) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Header de la tarjeta
        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerLabel.setForeground(new Color(52, 73, 94));
        headerLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Panel de contenido
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Método de pago con color
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel metodoLabel = new JLabel("Método de Pago:");
        metodoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        metodoLabel.setForeground(new Color(108, 117, 125));
        contentPanel.add(metodoLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        JLabel metodoValue = new JLabel("Efectivo");
        metodoValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        metodoValue.setForeground(Color.WHITE);
        metodoValue.setBackground(new Color(40, 167, 69));
        metodoValue.setOpaque(true);
        metodoValue.setBorder(new EmptyBorder(4, 8, 4, 8));
        contentPanel.add(metodoValue, gbc);
        
        // Información de montos
        addCardRow(contentPanel, gbc, 1, "Subtotal:", currencyFormat.format(sale.getSubtotal()));
        addCardRow(contentPanel, gbc, 2, "IVA (16%):", currencyFormat.format(sale.getTaxAmount()));
        
        // Total con estilo especial
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        JLabel totalLabel = new JLabel("Total:");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        totalLabel.setForeground(new Color(52, 73, 94));
        contentPanel.add(totalLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        JLabel totalValue = new JLabel(currencyFormat.format(sale.getTotalAmount()));
        totalValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalValue.setForeground(new Color(52, 73, 94));
        contentPanel.add(totalValue, gbc);
        
        addCardRow(contentPanel, gbc, 4, "Monto Recibido:", currencyFormat.format(sale.getAmountPaid()));
        addCardRow(contentPanel, gbc, 5, "Cambio:", currencyFormat.format(sale.getChangeAmount()));
        
        card.add(headerLabel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }
    
    private JPanel createProductsCard(String title, Sale sale) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Header de la tarjeta
        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerLabel.setForeground(new Color(52, 73, 94));
        headerLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Tabla de productos
        String[] columnNames = {"Producto", "Cantidad", "Precio Unitario", "Total"};
        DefaultTableModel productsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Obtener los productos reales de la venta
        List<Sale.SaleItem> saleItems = Sale.getSaleItems(sale.getId());
        for (Sale.SaleItem item : saleItems) {
            Object[] productRow = {
                item.getProductName(),
                String.valueOf(item.getQuantity()),
                currencyFormat.format(item.getPrice()),
                currencyFormat.format(item.getTotal())
            };
            productsModel.addRow(productRow);
        }
        
        JTable productsTable = new JTable(productsModel);
        productsTable.setRowHeight(30);
        productsTable.setGridColor(new Color(220, 220, 220));
        productsTable.getTableHeader().setBackground(new Color(248, 249, 250));
        productsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        productsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(productsTable);
        scrollPane.setPreferredSize(new Dimension(0, 100));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        // Panel de totales
        JPanel totalsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalsPanel.setBackground(Color.WHITE);
        totalsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JLabel subtotalLabel = new JLabel("Subtotal: " + currencyFormat.format(sale.getSubtotal()));
        subtotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JLabel ivaLabel = new JLabel("IVA (16%): " + currencyFormat.format(sale.getTaxAmount()));
        ivaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ivaLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
        
        JLabel totalFinalLabel = new JLabel("TOTAL: " + currencyFormat.format(sale.getTotalAmount()));
        totalFinalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalFinalLabel.setForeground(new Color(40, 167, 69));
        totalFinalLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
        
        totalsPanel.add(subtotalLabel);
        totalsPanel.add(ivaLabel);
        totalsPanel.add(totalFinalLabel);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(totalsPanel, BorderLayout.SOUTH);
        
        card.add(headerLabel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }
    
    private void addCardRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelComponent.setForeground(new Color(108, 117, 125));
        panel.add(labelComponent, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        valueComponent.setForeground(new Color(52, 73, 94));
        panel.add(valueComponent, gbc);
    }
    
    // Método estático para abrir la ventana de ventas
    public static void openSalesWindow() {
        SwingUtilities.invokeLater(() -> {
            SalesFrame salesFrame = new SalesFrame();
            salesFrame.setVisible(true);
        });
    }
}
