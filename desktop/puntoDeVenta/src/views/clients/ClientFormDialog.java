package views.clients;

import models.Cliente;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ClientFormDialog extends JDialog {
    private JTextField nameField;
    private JTextField phoneField;
    private JButton saveButton;
    private JButton cancelButton;
    private Cliente client;
    private boolean confirmed = false;

    public ClientFormDialog(JFrame parent, Cliente client) {
        super(parent, client == null ? "Agregar Cliente" : "Editar Cliente", true);
        this.client = client;
        
        initializeComponents();
        setupEventListeners();
        
        if (client != null) {
            populateFields();
        }
        
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setSize(400, 300);
        getContentPane().setBackground(Color.WHITE);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(Color.WHITE);

        // Título
        JLabel titleLabel = new JLabel(client == null ? "Agregar Nuevo Cliente" : "Editar Cliente");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(52, 144, 220));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Panel de campos
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Campo Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Nombre:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(new Color(73, 80, 87));
        fieldsPanel.add(nameLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        fieldsPanel.add(nameField, gbc);

        // Campo Teléfono
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel phoneLabel = new JLabel("Teléfono:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 14));
        phoneLabel.setForeground(new Color(73, 80, 87));
        fieldsPanel.add(phoneLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        phoneField = new JTextField(20);
        phoneField.setFont(new Font("Arial", Font.PLAIN, 14));
        phoneField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        fieldsPanel.add(phoneField, gbc);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttonPanel.setBackground(Color.WHITE);

        cancelButton = createStyledButton("Cancelar", new Color(108, 117, 125));
        saveButton = createStyledButton(client == null ? "Agregar" : "Guardar", new Color(40, 167, 69));

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Agregar componentes
        mainPanel.add(titleLabel);
        mainPanel.add(fieldsPanel);
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        saveButton.addActionListener(e -> saveClient());
        cancelButton.addActionListener(e -> dispose());

        // Enter para guardar
        ActionListener saveAction = e -> saveClient();
        nameField.addActionListener(saveAction);
        phoneField.addActionListener(saveAction);

        // ESC para cancelar
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke("ESCAPE"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private void populateFields() {
        if (client != null) {
            nameField.setText(client.getNombre());
            phoneField.setText(client.getTelefono());
        }
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 40));
        
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

    private void saveClient() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();

        // Validaciones
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

        // Crear o actualizar cliente
        if (client == null) {
            client = new Cliente();
        }

        client.setNombre(name);
        client.setTelefono(phone);

        if (client.guardar()) {
            confirmed = true;
            JOptionPane.showMessageDialog(this,
                client.getId() == 0 ? "Cliente agregado exitosamente." : "Cliente actualizado exitosamente.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error al guardar el cliente. Por favor, inténtelo de nuevo.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Cliente getClient() {
        return client;
    }
}
