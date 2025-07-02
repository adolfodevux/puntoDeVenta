package config;

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
}
