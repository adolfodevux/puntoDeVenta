package puntoDeVenta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class conexiondb {
	Connection conexion = null;

	public Connection conectar() {
		try {

			Class.forName("com.mysql.cj.jdbc.Driver");

			String server = "jdbc:mysql://40.76.137.253:3306/puntoDeVenta?"
					+ "characterEncoding=utf8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&"
					+ "serverTimezone=UTC";

			String user = "debian";
			String psw = "P@ss_d3bian-linux_Vps";

			this.conexion = DriverManager.getConnection(server, user, psw);
			return this.conexion;

		} catch (ClassNotFoundException e) {

			JOptionPane.showMessageDialog(null, "Error #1: Driver JDBC no encontrado. " + e.getMessage(),
					"Error de Conexión", JOptionPane.ERROR_MESSAGE);
			this.conexion = null;
			return this.conexion;

		} catch (SQLException e) {

			JOptionPane.showMessageDialog(null,
					"Error #2: Error de SQL al conectar. Código: " + e.getErrorCode() + "\nMensaje: " + e.getMessage(),
					"Error de Conexión", JOptionPane.ERROR_MESSAGE);
			this.conexion = null;
			return this.conexion;

		} catch (Exception e) {

			JOptionPane.showMessageDialog(null, "Error #3: Error desconocido al conectar. " + e.getMessage(),
					"Error de Conexión", JOptionPane.ERROR_MESSAGE);
			this.conexion = null;
			return this.conexion;
		}
	}
}