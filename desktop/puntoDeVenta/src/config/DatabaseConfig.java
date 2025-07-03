package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Configuración de la base de datos
 * Réplica exacta de la configuración PHP
 */
public class DatabaseConfig {
    public static final String DB_HOST = "40.76.137.253";
    public static final String DB_NAME = "puntoDeVenta";
    public static final String DB_USER = "debian";
    public static final String DB_PASS = "P@ss_d3bian-linux_Vps";
    public static final int DB_PORT = 3306;
    public static final String DB_CHARSET = "utf8";
    public static final String APP_NAME = "Sistema Punto de Venta";
    public static final boolean DEBUG_MODE = true;
    
    // URL de conexión completa
    public static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME +
     "?useSSL=false&serverTimezone=UTC&characterEncoding=utf8&useUnicode=true";
    
    /**
     * Obtiene una conexión a la base de datos
     * @return Connection
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        try {
            System.out.println("Intentando conectar a la base de datos...");
            System.out.println("URL: " + DB_URL);
            System.out.println("Usuario: " + DB_USER);
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            
            System.out.println("✓ Conexión exitosa a la base de datos");
            return conn;
            
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Driver MySQL no encontrado: " + e.getMessage());
            throw new SQLException("Driver MySQL no encontrado", e);
        } catch (SQLException e) {
            System.err.println("✗ Error de conexión a la base de datos: " + e.getMessage());
            System.err.println("Código de error: " + e.getErrorCode());
            System.err.println("Estado SQL: " + e.getSQLState());
            throw e;
        }
    }
}
