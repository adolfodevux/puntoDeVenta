package views.clients;

import models.Cliente;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class ClientsFrame extends JFrame {
    private JTable clientsTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField searchField; // Campo de búsqueda
    private List<Cliente> allClients;
    private List<Cliente> filteredClients;
    
    // Paginación
    private int currentPage = 1;
    private int itemsPerPage = 10;
    private JLabel pageInfoLabel;
    private JButton prevPageButton;
    private JButton nextPageButton;
    private JButton firstPageButton;
    private JButton lastPageButton;

    public ClientsFrame() {
        setTitle("Gestión de Clientes - Punto de Venta");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        // Cargar datos
        loadData();
        
        // Inicializar componentes
        initializeComponents();
        
        // Configurar eventos
        setupEventListeners();
        
        // Cargar la tabla
        updateTable();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 242, 247));

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 242, 247));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header con título y botón volver
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel de formulario
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 242, 247));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Panel del título con icono
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(new Color(240, 242, 247));

        // Icono de clientes
        JLabel iconLabel = new JLabel("👥");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 32));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        // Título
        JLabel titleLabel = new JLabel("Clientes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(52, 144, 220));

        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);

        // Botón Volver al Panel Principal
        JButton backButton = createStyledButton("← Volver al Panel Principal", new Color(52, 144, 220));
        backButton.setPreferredSize(new Dimension(220, 40));
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

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Panel superior con campos de entrada y búsqueda
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Color.WHITE);
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        searchPanel.setBackground(Color.WHITE);
        
        JLabel searchLabel = new JLabel("Buscar cliente:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        searchLabel.setForeground(new Color(51, 51, 51));
        
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.setToolTipText("Buscar por nombre o teléfono");
        
        JPanel searchFieldPanel = new JPanel(new BorderLayout());
        searchFieldPanel.setBackground(Color.WHITE);
        searchFieldPanel.add(searchLabel, BorderLayout.NORTH);
        searchFieldPanel.add(searchField, BorderLayout.CENTER);
        
        searchPanel.add(searchFieldPanel);
        
        // Panel de formulario de entrada
        JPanel formInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        formInputPanel.setBackground(Color.WHITE);

        // Campo Nombre
        JLabel nameLabel = new JLabel("Nombre");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        nameLabel.setForeground(new Color(51, 51, 51));

        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(180, 35));
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Campo Teléfono
        JLabel phoneLabel = new JLabel("Teléfono");
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        phoneLabel.setForeground(new Color(51, 51, 51));

        phoneField = new JTextField();
        phoneField.setPreferredSize(new Dimension(180, 35));
        phoneField.setFont(new Font("Arial", Font.PLAIN, 14));
        phoneField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Campo Búsqueda
        JButton addButton = createStyledButton("+ Agregar", new Color(40, 167, 69));
        addButton.setPreferredSize(new Dimension(100, 35));
        addButton.addActionListener(e -> addClient());

        // Organizar componentes en el panel
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setBackground(Color.WHITE);
        namePanel.add(nameLabel, BorderLayout.NORTH);
        namePanel.add(nameField, BorderLayout.CENTER);

        JPanel phonePanel = new JPanel(new BorderLayout());
        phonePanel.setBackground(Color.WHITE);
        phonePanel.add(phoneLabel, BorderLayout.NORTH);
        phonePanel.add(phoneField, BorderLayout.CENTER);

        formInputPanel.add(namePanel);
        formInputPanel.add(phonePanel);
        formInputPanel.add(addButton);
        
        // Organizar paneles
        inputPanel.add(searchPanel, BorderLayout.NORTH);
        inputPanel.add(formInputPanel, BorderLayout.CENTER);

        // Panel de la tabla
        JPanel tablePanel = createTablePanel();
        
        // Panel de paginación
        JPanel paginationPanel = createPaginationPanel();

        formPanel.add(inputPanel, BorderLayout.NORTH);
        formPanel.add(tablePanel, BorderLayout.CENTER);
        formPanel.add(paginationPanel, BorderLayout.SOUTH);

        return formPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Crear tabla con columnas como en la imagen
        String[] columnNames = {"ID", "Nombre", "Teléfono", "Compras", "Acciones"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        clientsTable = new JTable(tableModel);
        clientsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        clientsTable.setRowHeight(60);
        clientsTable.setGridColor(new Color(230, 230, 230));
        clientsTable.setShowGrid(false);
        clientsTable.setSelectionBackground(new Color(230, 247, 255));
        clientsTable.setBackground(Color.WHITE);

        // Configurar anchos de columnas
        clientsTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        clientsTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Nombre
        clientsTable.getColumnModel().getColumn(2).setPreferredWidth(180); // Teléfono
        clientsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Compras
        clientsTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Acciones

        // Renderer personalizado
        clientsTable.setDefaultRenderer(Object.class, new ClientTableCellRenderer());

        // Configurar header
        clientsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        clientsTable.getTableHeader().setBackground(new Color(233, 236, 239));
        clientsTable.getTableHeader().setForeground(new Color(73, 80, 87));
        clientsTable.getTableHeader().setOpaque(true);
        clientsTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        clientsTable.getTableHeader().setPreferredSize(new Dimension(0, 45));

        JScrollPane scrollPane = new JScrollPane(clientsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // Renderer personalizado para la tabla
    private class ClientTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // Colores base
            if (!isSelected) {
                if (row % 2 == 0) {
                    c.setBackground(Color.WHITE);
                } else {
                    c.setBackground(new Color(248, 249, 250));
                }
                c.setForeground(new Color(73, 80, 87));
            } else {
                c.setBackground(new Color(230, 247, 255));
                c.setForeground(new Color(73, 80, 87));
            }

            setFont(new Font("Arial", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            // Columna ID
            if (column == 0) {
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.BOLD, 14));
            }
            // Columna de Compras (índice 3)
            else if (column == 3) {
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.PLAIN, 12));
            }
            // Columna de Acciones (índice 4)
            else if (column == 4) {
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
        
        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = bgColor;
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });
        
        return button;
    }

    private void loadData() {
        // Cargar clientes con información de ventas
        allClients = Cliente.obtenerTodosConVentas();
        filteredClients = allClients;
        System.out.println("Clientes cargados: " + allClients.size());
    }

    private void updateTable() {
        if (tableModel == null) {
            System.out.println("ERROR: tableModel es null");
            return;
        }
        
        if (filteredClients == null) {
            System.out.println("ERROR: filteredClients es null");
            filteredClients = new java.util.ArrayList<>();
        }
        
        System.out.println("Actualizando tabla con " + filteredClients.size() + " clientes (página " + currentPage + ")");
        
        // Limpiar tabla
        tableModel.setRowCount(0);
        
        // Calcular los clientes para la página actual
        int totalClients = filteredClients.size();
        int totalPages = (int) Math.ceil((double) totalClients / itemsPerPage);
        
        // Ajustar página actual si es necesario
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }
        if (currentPage < 1) {
            currentPage = 1;
        }
        
        // Calcular índices para la paginación
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalClients);
        
        // Agregar solo los clientes de la página actual
        for (int i = startIndex; i < endIndex; i++) {
            Cliente client = filteredClients.get(i);
            
            // Crear información de compras con círculo simple (solo número)
            String comprasInfo = "<html><div style='text-align: center;'>";
            comprasInfo += "<div style='display: inline-block; background-color: " + 
                          (client.getTotalCompras() > 0 ? "#28a745" : "#6c757d") + 
                          "; color: white; border-radius: 50%; width: 30px; height: 30px; " +
                          "line-height: 30px; text-align: center; font-weight: bold; font-size: 14px;'>" + 
                          client.getTotalCompras() + "</div>";
            comprasInfo += "</div></html>";

            Object[] row = {
                client.getId(),
                client.getNombre(),
                client.getTelefono(),
                comprasInfo,
                "✏ Editar    🗑 Eliminar" // Acciones
            };
            
            tableModel.addRow(row);
        }
        
        // Actualizar información de paginación
        updatePaginationInfo();
        updatePaginationButtons();
        
        System.out.println("Tabla actualizada. Mostrando " + (endIndex - startIndex) + " de " + totalClients + " clientes");
    }

    private void setupEventListeners() {
        // Listener para la tabla (click en acciones)
        clientsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = clientsTable.rowAtPoint(evt.getPoint());
                int column = clientsTable.columnAtPoint(evt.getPoint());
                
                if (row >= 0 && column == 4) { // Columna de acciones
                    handleActionClick(row, evt.getX());
                }
            }
        });

        // Enter en los campos de texto para agregar cliente
        ActionListener addClientAction = e -> addClient();
        nameField.addActionListener(addClientAction);
        phoneField.addActionListener(addClientAction);
        
        // Búsqueda en tiempo real
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                performSearch();
            }
        });
    }

    private void handleActionClick(int row, int clickX) {
        // Obtener el índice real del cliente en la lista filtrada
        int actualIndex = (currentPage - 1) * itemsPerPage + row;
        
        if (actualIndex < filteredClients.size()) {
            Cliente client = filteredClients.get(actualIndex);
            
            // Determinar qué acción según la posición del click
            java.awt.Rectangle cellRect = clientsTable.getCellRect(row, 4, false);
            int relativeX = clickX - cellRect.x;
            int cellWidth = cellRect.width;
            
            if (relativeX < cellWidth / 2) {
                // Editar
                showEditClientDialog(client);
            } else {
                // Eliminar
                showDeleteClientDialog(client);
            }
        }
    }

    private void addClient() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, ingrese el nombre del cliente.",
                "Campo Requerido",
                JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return;
        }
        
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, ingrese el teléfono del cliente.",
                "Campo Requerido",
                JOptionPane.WARNING_MESSAGE);
            phoneField.requestFocus();
            return;
        }
        
        Cliente newClient = new Cliente();
        newClient.setNombre(name);
        newClient.setTelefono(phone);
        
        if (newClient.guardar()) {
            JOptionPane.showMessageDialog(this,
                "Cliente agregado exitosamente.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Limpiar campos
            nameField.setText("");
            phoneField.setText("");
            
            // Recargar datos
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error al agregar el cliente. Por favor, inténtelo de nuevo.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEditClientDialog(Cliente client) {
        ClientFormDialog dialog = new ClientFormDialog(this, client);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refreshData();
        }
    }

    private void showDeleteClientDialog(Cliente client) {
        int result = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de que desea eliminar el cliente \"" + client.getNombre() + "\"?\n" +
            "Esta acción no se puede deshacer.",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            if (client.eliminar()) {
                JOptionPane.showMessageDialog(this,
                    "Cliente eliminado exitosamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar el cliente. Es posible que tenga ventas asociadas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshData() {
        loadData();
        // Mantener la página actual o ajustar si es necesario
        currentPage = Math.max(1, currentPage);
        updateTable();
    }

    private JPanel createPaginationPanel() {
        JPanel paginationPanel = new JPanel(new BorderLayout());
        paginationPanel.setBackground(Color.WHITE);
        paginationPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Panel de botones de navegación
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        navigationPanel.setBackground(Color.WHITE);

        // Botón Primera página
        firstPageButton = createPaginationButton("<<");
        firstPageButton.setToolTipText("Primera página");
        firstPageButton.addActionListener(e -> goToFirstPage());

        // Botón Página anterior
        prevPageButton = createPaginationButton("<");
        prevPageButton.setToolTipText("Página anterior");
        prevPageButton.addActionListener(e -> goToPreviousPage());

        // Label de información de página
        pageInfoLabel = new JLabel();
        pageInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        pageInfoLabel.setForeground(new Color(73, 80, 87));
        pageInfoLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Botón Página siguiente
        nextPageButton = createPaginationButton(">");
        nextPageButton.setToolTipText("Página siguiente");
        nextPageButton.addActionListener(e -> goToNextPage());

        // Botón Última página
        lastPageButton = createPaginationButton(">>");
        lastPageButton.setToolTipText("Última página");
        lastPageButton.addActionListener(e -> goToLastPage());

        navigationPanel.add(firstPageButton);
        navigationPanel.add(prevPageButton);
        navigationPanel.add(pageInfoLabel);
        navigationPanel.add(nextPageButton);
        navigationPanel.add(lastPageButton);

        paginationPanel.add(navigationPanel, BorderLayout.CENTER);

        return paginationPanel;
    }

    private JButton createPaginationButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(new Color(52, 144, 220));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(40, 35));
        
        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(52, 144, 220));
                    button.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(Color.WHITE);
                    button.setForeground(new Color(52, 144, 220));
                }
            }
        });
        
        return button;
    }

    // Métodos de paginación
    private void updatePaginationInfo() {
        if (pageInfoLabel == null) return;
        
        int totalClients = filteredClients.size();
        int totalPages = totalClients > 0 ? (int) Math.ceil((double) totalClients / itemsPerPage) : 1;
        int startIndex = (currentPage - 1) * itemsPerPage + 1;
        int endIndex = Math.min(currentPage * itemsPerPage, totalClients);
        
        if (totalClients == 0) {
            pageInfoLabel.setText("No hay clientes para mostrar");
        } else {
            pageInfoLabel.setText(String.format("Mostrando %d-%d de %d clientes (Página %d de %d)", 
                startIndex, endIndex, totalClients, currentPage, totalPages));
        }
    }
    
    private void updatePaginationButtons() {
        if (firstPageButton == null || prevPageButton == null || 
            nextPageButton == null || lastPageButton == null) return;
        
        int totalClients = filteredClients.size();
        int totalPages = totalClients > 0 ? (int) Math.ceil((double) totalClients / itemsPerPage) : 1;
        
        // Habilitar/deshabilitar botones según la página actual
        boolean isFirstPage = currentPage <= 1;
        boolean isLastPage = currentPage >= totalPages;
        
        firstPageButton.setEnabled(!isFirstPage);
        prevPageButton.setEnabled(!isFirstPage);
        nextPageButton.setEnabled(!isLastPage);
        lastPageButton.setEnabled(!isLastPage);
        
        // Cambiar apariencia de botones deshabilitados
        updateButtonAppearance(firstPageButton, !isFirstPage);
        updateButtonAppearance(prevPageButton, !isFirstPage);
        updateButtonAppearance(nextPageButton, !isLastPage);
        updateButtonAppearance(lastPageButton, !isLastPage);
    }
    
    private void updateButtonAppearance(JButton button, boolean enabled) {
        if (enabled) {
            button.setForeground(new Color(52, 144, 220));
            button.setBackground(Color.WHITE);
        } else {
            button.setForeground(new Color(150, 150, 150));
            button.setBackground(new Color(245, 245, 245));
        }
    }
    
    private void goToFirstPage() {
        currentPage = 1;
        updateTable();
    }
    
    private void goToPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateTable();
        }
    }
    
    private void goToNextPage() {
        int totalClients = filteredClients.size();
        int totalPages = (int) Math.ceil((double) totalClients / itemsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            updateTable();
        }
    }
    
    private void goToLastPage() {
        int totalClients = filteredClients.size();
        int totalPages = (int) Math.ceil((double) totalClients / itemsPerPage);
        currentPage = Math.max(1, totalPages);
        updateTable();
    }

    private void performSearch() {
        String searchTerm = searchField.getText().toLowerCase().trim();
        
        if (searchTerm.isEmpty()) {
            // Mostrar todos los clientes
            filteredClients = allClients;
        } else {
            // Filtrar clientes que coincidan con el término de búsqueda
            filteredClients = allClients.stream()
                .filter(client -> 
                    client.getNombre().toLowerCase().contains(searchTerm) ||
                    client.getTelefono().toLowerCase().contains(searchTerm)
                )
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Resetear a la primera página al hacer búsqueda
        currentPage = 1;
        updateTable();
    }
}
