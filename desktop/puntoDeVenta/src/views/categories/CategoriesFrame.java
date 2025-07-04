package views.categories;

import models.Category;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CategoriesFrame extends JFrame {
    private DefaultTableModel model;
    private JTable table;
    private JLabel totalLabel;
    private JTextField searchField;
    private List<Category> categories;
    private JLabel popLabel; // Referencia directa al label de popular

    public CategoriesFrame() {
        setTitle("Gestión de Categorías");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(20, 20));

        // --- DISEÑO MODERNO Y LIMPIO ---
        // Panel principal con BoxLayout para apilar todo verticalmente
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Header moderno
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(18, 32, 18, 32),
            BorderFactory.createLineBorder(new Color(235,235,235), 1, true)));
        JLabel headerTitle = new JLabel("\uD83D\uDCCB Gestión de Categorías");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerTitle.setForeground(new Color(44, 62, 80));
        JLabel headerDesc = new JLabel("Administra las categorías de productos de tu tienda");
        headerDesc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        headerDesc.setForeground(new Color(120, 130, 140));
        JPanel headerLeft = new JPanel();
        headerLeft.setOpaque(false);
        headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS));
        headerLeft.add(headerTitle);
        headerLeft.add(headerDesc);
        headerPanel.add(headerLeft, BorderLayout.WEST);
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        headerRight.setOpaque(false);
        JButton btnBack = new JButton("← Volver al Dashboard");
        btnBack.setBackground(new Color(149, 165, 166));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnBack.setFocusPainted(false);
        btnBack.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JButton btnNew = new JButton("+ Nueva Categoría");
        btnNew.setBackground(new Color(52, 152, 219));
        btnNew.setForeground(Color.WHITE);
        btnNew.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnNew.setFocusPainted(false);
        btnNew.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btnNew.setCursor(new Cursor(Cursor.HAND_CURSOR));
        headerRight.add(btnBack);
        headerRight.add(btnNew);
        headerPanel.add(headerRight, BorderLayout.EAST);
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(18));

        // Buscador con icono
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(new Color(245, 247, 250));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JPanel searchInner = new JPanel(new BorderLayout());
        searchInner.setBackground(Color.WHITE);
        searchInner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 230, 240), 1, true),
            BorderFactory.createEmptyBorder(0, 8, 0, 0)));
        JLabel searchIcon = new JLabel("\uD83D\uDD0D");
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        searchIcon.setForeground(new Color(180, 190, 200));
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        searchField.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        searchField.setBackground(Color.WHITE);
        searchField.setForeground(new Color(44, 62, 80));
        searchField.setToolTipText("Buscar categorías por nombre o descripción...");
        searchInner.add(searchIcon, BorderLayout.WEST);
        searchInner.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchInner, BorderLayout.CENTER);
        mainPanel.add(searchPanel);
        mainPanel.add(Box.createVerticalStrut(18));

        // Tarjetas resumen (más pequeñas)
        JPanel summaryPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        summaryPanel.setBackground(new Color(245, 247, 250));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JPanel totalPanel = new JPanel();
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 230, 240), 1, true),
            BorderFactory.createEmptyBorder(12, 18, 12, 18)));
        totalPanel.setLayout(new BoxLayout(totalPanel, BoxLayout.Y_AXIS));
        JLabel totalIcon = new JLabel("\uD83D\uDCCB");
        totalIcon.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalIcon.setForeground(new Color(52, 152, 219));
        totalLabel = new JLabel();
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalLabel.setForeground(new Color(44, 62, 80));
        JLabel totalText = new JLabel("Total de Categorías");
        totalText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        totalText.setForeground(new Color(120, 130, 140));
        totalPanel.add(totalIcon);
        totalPanel.add(Box.createVerticalStrut(4));
        totalPanel.add(totalLabel);
        totalPanel.add(totalText);
        summaryPanel.add(totalPanel);
        JPanel popularPanel = new JPanel();
        popularPanel.setBackground(Color.WHITE);
        popularPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 230, 240), 1, true),
            BorderFactory.createEmptyBorder(12, 18, 12, 18)));
        popularPanel.setLayout(new BoxLayout(popularPanel, BoxLayout.Y_AXIS));
        JLabel popIcon = new JLabel("\u2B50");
        popIcon.setFont(new Font("Segoe UI", Font.BOLD, 22));
        popIcon.setForeground(new Color(52, 152, 219));
        popLabel = new JLabel();
        popLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        popLabel.setForeground(new Color(44, 62, 80));
        JLabel popText = new JLabel("Categoría Más Popular");
        popText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        popText.setForeground(new Color(120, 130, 140));
        popularPanel.add(popIcon);
        popularPanel.add(Box.createVerticalStrut(4));
        popularPanel.add(popLabel);
        popularPanel.add(popText);
        summaryPanel.add(popularPanel);
        mainPanel.add(summaryPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Tabla
        String[] columns = {"ID", "Nombre", "Descripción", "Productos", "Acciones"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(44, 62, 80));
        table.getTableHeader().setBackground(new Color(245, 247, 250));
        table.getTableHeader().setForeground(new Color(44, 62, 80));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.setSelectionBackground(new Color(220, 230, 241));
        table.setSelectionForeground(new Color(44, 62, 80));
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        mainPanel.add(tableScroll);

        // PAGINADOR
        JPanel paginatorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        paginatorPanel.setBackground(new Color(245, 247, 250));
        JButton btnPrev = new JButton("Anterior");
        JButton btnNext = new JButton("Siguiente");
        JLabel pageLabel = new JLabel("Página 1");
        btnPrev.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnNext.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pageLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnPrev.setBackground(new Color(220, 230, 240));
        btnNext.setBackground(new Color(220, 230, 240));
        btnPrev.setFocusPainted(false);
        btnNext.setFocusPainted(false);
        paginatorPanel.add(btnPrev);
        paginatorPanel.add(pageLabel);
        paginatorPanel.add(btnNext);
        mainPanel.add(paginatorPanel);

        // PAGINACIÓN LÓGICA
        final int[] currentPage = {1};
        final int pageSize = 7;
        btnPrev.setEnabled(false);

        btnPrev.addActionListener(e -> {
            if (currentPage[0] > 1) {
                currentPage[0]--;
                loadCategoriesWithPagination(searchField.getText(), currentPage[0], pageSize, pageLabel, btnPrev, btnNext);
            }
        });
        btnNext.addActionListener(e -> {
            int totalPages = (int) Math.ceil((double) categories.size() / pageSize);
            if (currentPage[0] < totalPages) {
                currentPage[0]++;
                loadCategoriesWithPagination(searchField.getText(), currentPage[0], pageSize, pageLabel, btnPrev, btnNext);
            }
        });
        // Cargar y mostrar categorías con paginación
        loadCategoriesWithPagination("", 1, pageSize, pageLabel, btnPrev, btnNext);
        searchField.addActionListener(e -> {
            currentPage[0] = 1;
            loadCategoriesWithPagination(searchField.getText(), currentPage[0], pageSize, pageLabel, btnPrev, btnNext);
        });

        setContentPane(mainPanel);

        // Cargar y mostrar categorías
        loadCategories("");

        // Buscar
        searchField.addActionListener(e -> loadCategories(searchField.getText()));

        // Botón nueva categoría
        btnNew.addActionListener(e -> showCategoryDialog(null));

        // Acciones en la tabla (editar/eliminar)
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), this));
    }

    public void loadCategories(String filter) {
        model.setRowCount(0);
        categories = Category.getCategoriesWithProductCount();
        int maxProducts = -1;
        String popular = "";
        for (Category c : categories) {
            if (filter.isEmpty() || c.getName().toLowerCase().contains(filter.toLowerCase()) || (c.getDescription() != null && c.getDescription().toLowerCase().contains(filter.toLowerCase()))) {
                model.addRow(new Object[]{c.getId(), c.getName(), c.getDescription(), c.getProductCount() + " productos", "Editar / Eliminar"});
            }
            if (c.getProductCount() > maxProducts) {
                maxProducts = c.getProductCount();
                popular = c.getName();
            }
        }
        totalLabel.setText(String.valueOf(categories.size()));
        if (popLabel != null) popLabel.setText(popular); // Ahora se actualiza directamente
    }

    public void showCategoryDialog(Category category) {
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        if (category != null) {
            nameField.setText(category.getName());
            descField.setText(category.getDescription());
        }
        Object[] message = {
            "Nombre:", nameField,
            "Descripción:", descField
        };
        int option = JOptionPane.showConfirmDialog(this, message, category == null ? "Nueva Categoría" : "Editar Categoría", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (category == null) {
                category = new Category();
            }
            category.setName(name);
            category.setDescription(desc);
            if (category.save()) {
                JOptionPane.showMessageDialog(this, "Categoría guardada correctamente.");
                loadCategories("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar la categoría.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void deleteCategory(int row) {
        int id = (int) model.getValueAt(row, 0);
        Category category = Category.getCategoryById(id);
        if (category != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas eliminar la categoría '" + category.getName() + "'?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (category.delete()) {
                    JOptionPane.showMessageDialog(this, "Categoría eliminada correctamente.");
                    loadCategories("");
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar la categoría. Puede tener productos asociados.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public DefaultTableModel getModel() {
        return model;
    }

    // Nueva función para paginación
    public void loadCategoriesWithPagination(String filter, int page, int pageSize, JLabel pageLabel, JButton btnPrev, JButton btnNext) {
        model.setRowCount(0);
        categories = Category.getCategoriesWithProductCount();
        int maxProducts = -1;
        String popular = "";
        List<Category> filtered = new java.util.ArrayList<>();
        for (Category c : categories) {
            if (filter.isEmpty() || c.getName().toLowerCase().contains(filter.toLowerCase()) || (c.getDescription() != null && c.getDescription().toLowerCase().contains(filter.toLowerCase()))) {
                filtered.add(c);
            }
            if (c.getProductCount() > maxProducts) {
                maxProducts = c.getProductCount();
                popular = c.getName();
            }
        }
        totalLabel.setText(String.valueOf(filtered.size()));
        if (popLabel != null) popLabel.setText(popular);
        int totalPages = (int) Math.ceil((double) filtered.size() / pageSize);
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, filtered.size());
        for (int i = start; i < end; i++) {
            Category c = filtered.get(i);
            model.addRow(new Object[]{c.getId(), c.getName(), c.getDescription(), c.getProductCount() + " productos", "Editar / Eliminar"});
        }
        pageLabel.setText("Página " + page + " de " + (totalPages == 0 ? 1 : totalPages));
        btnPrev.setEnabled(page > 1);
        btnNext.setEnabled(page < totalPages);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CategoriesFrame().setVisible(true);
        });
    }
}

// Renderizador de botones para la columna de acciones
class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
    private final JButton editButton = new JButton("✏️");
    private final JButton deleteButton = new JButton("🗑️");
    public ButtonRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        add(editButton);
        add(deleteButton);
        setOpaque(true);
    }
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}

// Editor de botones para la columna de acciones
class ButtonEditor extends DefaultCellEditor {
    protected JPanel panel;
    protected JButton editButton;
    protected JButton deleteButton;
    private CategoriesFrame parent;
    private int row;
    public ButtonEditor(JCheckBox checkBox, CategoriesFrame parent) {
        super(checkBox);
        this.parent = parent;
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        editButton = new JButton("✏️");
        deleteButton = new JButton("🗑️");
        panel.add(editButton);
        panel.add(deleteButton);
        editButton.addActionListener(e -> parent.showCategoryDialog(Category.getCategoryById((int) parent.getModel().getValueAt(row, 0))));
        deleteButton.addActionListener(e -> parent.deleteCategory(row));
    }
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row = row;
        return panel;
    }
    public Object getCellEditorValue() { return ""; }
}
