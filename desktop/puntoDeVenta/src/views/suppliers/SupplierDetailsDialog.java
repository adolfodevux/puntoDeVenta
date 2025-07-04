package views.suppliers;

import models.Supplier;
import utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

public class SupplierDetailsDialog extends JDialog {
    private Supplier supplier;
    private JButton editButton;
    private JButton deleteButton;
    private JButton closeButton;
    private boolean shouldRefresh = false;

    public SupplierDetailsDialog(JFrame parent, Supplier supplier) {
        super(parent, "Detalles del Proveedor", true);
        this.supplier = supplier;

        initializeComponents();
        setupLayout();
        setupEventListeners();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        editButton = new JButton("Editar");
        editButton.setBackground(UIUtils.PRIMARY_COLOR);
        editButton.setForeground(Color.WHITE);
        editButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        editButton.setBorderPainted(false);
        editButton.setFocusPainted(false);

        deleteButton = new JButton("Eliminar");
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);

        closeButton = new JButton("Cerrar");
        closeButton.setBackground(Color.GRAY);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Header con nombre del proveedor
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel nameLabel = new JLabel(supplier.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        nameLabel.setForeground(UIUtils.PRIMARY_COLOR);
        headerPanel.add(nameLabel, BorderLayout.WEST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel de detalles
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 15);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Información de contacto
        if (supplier.getContactPerson() != null && !supplier.getContactPerson().trim().isEmpty()) {
            addDetailRow(detailsPanel, gbc, row++, "Persona de Contacto:", supplier.getContactPerson());
        }

        if (supplier.getEmail() != null && !supplier.getEmail().trim().isEmpty()) {
            addDetailRow(detailsPanel, gbc, row++, "Email:", supplier.getEmail());
        }

        if (supplier.getPhone() != null && !supplier.getPhone().trim().isEmpty()) {
            addDetailRow(detailsPanel, gbc, row++, "Teléfono:", supplier.getPhone());
        }

        // Dirección
        if (supplier.getAddress() != null && !supplier.getAddress().trim().isEmpty()) {
            addDetailRow(detailsPanel, gbc, row++, "Dirección:", supplier.getAddress());
        }

        String location = supplier.getLocation();
        if (!location.equals("No especificada")) {
            addDetailRow(detailsPanel, gbc, row++, "Ubicación:", location);
        }

        // Información fiscal y web
        if (supplier.getTaxId() != null && !supplier.getTaxId().trim().isEmpty()) {
            addDetailRow(detailsPanel, gbc, row++, "RFC/Tax ID:", supplier.getTaxId());
        }

        if (supplier.getWebsite() != null && !supplier.getWebsite().trim().isEmpty()) {
            addDetailRow(detailsPanel, gbc, row++, "Sitio Web:", supplier.getWebsite());
        }

        // Notas
        if (supplier.getNotes() != null && !supplier.getNotes().trim().isEmpty()) {
            addDetailRow(detailsPanel, gbc, row++, "Notas:", supplier.getNotes());
        }

        // Fechas
        if (supplier.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            addDetailRow(detailsPanel, gbc, row++, "Fecha de Registro:", sdf.format(supplier.getCreatedAt()));
        }

        if (supplier.getUpdatedAt() != null && !supplier.getUpdatedAt().equals(supplier.getCreatedAt())) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            addDetailRow(detailsPanel, gbc, row++, "Última Actualización:", sdf.format(supplier.getUpdatedAt()));
        }

        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("SansSerif", Font.BOLD, 12));
        labelComponent.setForeground(new Color(100, 100, 100));
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel valueLabel = new JLabel("<html>" + escapeHtml(value) + "</html>");
        valueLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        valueLabel.setVerticalAlignment(SwingConstants.TOP);
        panel.add(valueLabel, gbc);
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\n", "<br>");
    }

    private void setupEventListeners() {
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSupplier();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSupplier();
            }
        });

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void editSupplier() {
        SupplierFormDialog dialog = new SupplierFormDialog((JFrame) getOwner(), supplier);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            shouldRefresh = true;
            // Recargar datos del proveedor
            Supplier updatedSupplier = Supplier.getById(supplier.getId());
            if (updatedSupplier != null) {
                this.supplier = updatedSupplier;
                // Recrear el layout con los datos actualizados
                getContentPane().removeAll();
                setupLayout();
                revalidate();
                repaint();
            }
        }
    }

    private void deleteSupplier() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de que desea eliminar el proveedor '" + supplier.getName() + "'?\n" +
            "Esta acción no se puede deshacer.",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            if (supplier.delete()) {
                shouldRefresh = true;
                JOptionPane.showMessageDialog(this,
                    "Proveedor eliminado exitosamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar el proveedor. Por favor, inténtelo de nuevo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public boolean shouldRefresh() {
        return shouldRefresh;
    }
}
