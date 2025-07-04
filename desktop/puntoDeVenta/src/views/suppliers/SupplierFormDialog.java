package views.suppliers;

import models.Supplier;
import utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SupplierFormDialog extends JDialog {
    private Supplier supplier;
    private boolean isEditMode;
    private boolean confirmed;

    // Campos del formulario
    private JTextField nameField;
    private JTextField contactPersonField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JTextField cityField;
    private JTextField countryField;
    private JTextField taxIdField;
    private JTextField websiteField;
    private JTextArea notesArea;

    // Botones
    private JButton saveButton;
    private JButton cancelButton;

    public SupplierFormDialog(JFrame parent, Supplier supplier) {
        super(parent, supplier == null ? "Agregar Proveedor" : "Editar Proveedor", true);
        this.supplier = supplier;
        this.isEditMode = supplier != null;
        this.confirmed = false;

        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadSupplierData();
        
        // Configuración del diálogo
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        // Campos de texto
        nameField = new JTextField(20);
        contactPersonField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        cityField = new JTextField(20);
        countryField = new JTextField(20);
        taxIdField = new JTextField(20);
        websiteField = new JTextField(20);

        // Áreas de texto
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        
        notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        // Botones
        saveButton = new JButton(isEditMode ? "Actualizar" : "Guardar");
        saveButton.setBackground(UIUtils.PRIMARY_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        saveButton.setBorderPainted(false);
        saveButton.setFocusPainted(false);

        cancelButton = new JButton("Cancelar");
        cancelButton.setBackground(Color.GRAY);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel principal con formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Información básica
        addFormField(formPanel, gbc, 0, "Nombre:", nameField, true);
        addFormField(formPanel, gbc, 1, "Persona de Contacto:", contactPersonField, false);
        addFormField(formPanel, gbc, 2, "Email:", emailField, false);
        addFormField(formPanel, gbc, 3, "Teléfono:", phoneField, false);

        // Dirección
        addFormField(formPanel, gbc, 4, "Dirección:", new JScrollPane(addressArea), false);
        addFormField(formPanel, gbc, 5, "Ciudad:", cityField, false);
        addFormField(formPanel, gbc, 6, "País:", countryField, false);

        // Información fiscal y web
        addFormField(formPanel, gbc, 7, "RFC/Tax ID:", taxIdField, false);
        addFormField(formPanel, gbc, 8, "Sitio Web:", websiteField, false);

        // Notas
        addFormField(formPanel, gbc, 9, "Notas:", new JScrollPane(notesArea), false);

        add(formPanel, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, 
                             JComponent field, boolean required) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;

        JLabel labelComponent = new JLabel(label);
        if (required) {
            labelComponent.setText(label + " *");
            labelComponent.setForeground(new Color(200, 0, 0));
        }
        labelComponent.setFont(new Font("SansSerif", Font.BOLD, 12));
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private void setupEventListeners() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSupplier();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Enter para guardar
        nameField.addActionListener(e -> saveSupplier());
    }

    private void loadSupplierData() {
        if (supplier != null) {
            nameField.setText(supplier.getName());
            contactPersonField.setText(supplier.getContactPerson());
            emailField.setText(supplier.getEmail());
            phoneField.setText(supplier.getPhone());
            addressArea.setText(supplier.getAddress());
            cityField.setText(supplier.getCity());
            countryField.setText(supplier.getCountry());
            taxIdField.setText(supplier.getTaxId());
            websiteField.setText(supplier.getWebsite());
            notesArea.setText(supplier.getNotes());
        }
    }

    private void saveSupplier() {
        // Validación básica
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "El nombre del proveedor es obligatorio.", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return;
        }

        // Validación de email (si se proporciona)
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, 
                "El formato del email no es válido.", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return;
        }

        try {
            if (isEditMode) {
                // Actualizar proveedor existente
                supplier.setName(nameField.getText().trim());
                supplier.setContactPerson(contactPersonField.getText().trim());
                supplier.setEmail(email.isEmpty() ? null : email);
                supplier.setPhone(phoneField.getText().trim());
                supplier.setAddress(addressArea.getText().trim());
                supplier.setCity(cityField.getText().trim());
                supplier.setCountry(countryField.getText().trim());
                supplier.setTaxId(taxIdField.getText().trim());
                supplier.setWebsite(websiteField.getText().trim());
                supplier.setNotes(notesArea.getText().trim());

                if (supplier.update()) {
                    confirmed = true;
                    JOptionPane.showMessageDialog(this, 
                        "Proveedor actualizado exitosamente.", 
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al actualizar el proveedor. Por favor, inténtelo de nuevo.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Crear nuevo proveedor
                supplier = new Supplier(
                    nameField.getText().trim(),
                    contactPersonField.getText().trim(),
                    email.isEmpty() ? null : email,
                    phoneField.getText().trim(),
                    addressArea.getText().trim(),
                    cityField.getText().trim(),
                    countryField.getText().trim(),
                    taxIdField.getText().trim(),
                    websiteField.getText().trim(),
                    notesArea.getText().trim()
                );

                if (supplier.save()) {
                    confirmed = true;
                    JOptionPane.showMessageDialog(this, 
                        "Proveedor agregado exitosamente.", 
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al guardar el proveedor. Por favor, inténtelo de nuevo.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error inesperado: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Supplier getSupplier() {
        return supplier;
    }
}
