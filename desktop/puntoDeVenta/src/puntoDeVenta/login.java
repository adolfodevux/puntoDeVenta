package puntoDeVenta;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class login extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					login frame = new login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public login() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 400, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 400, 400);
		contentPane.add(panel);
		panel.setLayout(null);

		JLabel lblInicioDeSesion = new JLabel("Inicio de Sesion");
		lblInicioDeSesion.setBounds(84, 22, 277, 35);
		lblInicioDeSesion.setFont(new Font("Dialog", Font.BOLD, 30));
		panel.add(lblInicioDeSesion);

		JLabel lblUsuario = new JLabel("Usuario");
		lblUsuario.setFont(new Font("Dialog", Font.BOLD, 24));
		lblUsuario.setBounds(141, 69, 139, 35);
		panel.add(lblUsuario);

		JLabel lblContrasea = new JLabel("Contraseña");
		lblContrasea.setFont(new Font("Dialog", Font.BOLD, 24));
		lblContrasea.setBounds(122, 173, 169, 17);
		panel.add(lblContrasea);

		textField = new JTextField();
		textField.setBounds(128, 116, 114, 21);
		panel.add(textField);
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(128, 217, 114, 21);
		panel.add(textField_1);

		JButton btnIniciarSesion = new JButton("Iniciar sesion");
		btnIniciarSesion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String usuario = textField.getText();
				String password = textField_1.getText();

				conexiondb miConexionDb = new conexiondb();
				Connection conexion = null;
				PreparedStatement pstmt = null;
				ResultSet rs = null;

				try {

					conexion = miConexionDb.conectar();

					if (conexion != null) {

						String sql = "SELECT COUNT(*) FROM vendedores WHERE username = ? AND password = ?";
						pstmt = conexion.prepareStatement(sql);

						pstmt.setString(1, usuario);
						pstmt.setString(2, password);

						rs = pstmt.executeQuery();

						if (rs.next()) {
							int count = rs.getInt(1);

							if (count > 0) {

								JOptionPane.showMessageDialog(null,
										"¡Inicio de sesión exitosoa! Bienvenido, " + usuario + ".", "Login OK",
										JOptionPane.INFORMATION_MESSAGE);
								

							} else {

								JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos.",
										"Error de Login", JOptionPane.WARNING_MESSAGE);
							}
						}
					} else {

						JOptionPane.showMessageDialog(null, "No se pudo establecer conexión con la base de datos.",
								"Error de Conexión", JOptionPane.ERROR_MESSAGE);
					}

				} catch (SQLException ex) {

					JOptionPane
							.showMessageDialog(
									null, "Error SQL al verificar credenciales: " + ex.getMessage()
											+ "\nCódigo de error: " + ex.getErrorCode(),
									"Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				} catch (Exception ex) {

					JOptionPane.showMessageDialog(null, "Ocurrió un error inesperado: " + ex.getMessage(),
							"Error General", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				} finally {

					try {
						if (rs != null)
							rs.close();
						if (pstmt != null)
							pstmt.close();
						if (conexion != null)
							conexion.close();
					} catch (SQLException exCierre) {
						JOptionPane.showMessageDialog(null,
								"Error al cerrar los recursos de la base de datos: " + exCierre.getMessage(),
								"Error de Cierre", JOptionPane.ERROR_MESSAGE);
					}
				}

			}
		});
		btnIniciarSesion.setBounds(141, 281, 117, 27);
		panel.add(btnIniciarSesion);

	}
}