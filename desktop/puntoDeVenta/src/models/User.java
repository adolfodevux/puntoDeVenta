package models;

import config.DatabaseConfig;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Modelo User - Réplica exacta del modelo PHP
 * Maneja autenticación y registro de usuarios
 */
public class User {
    private Connection connection;
    
    public User() {
        try {
            // Establecer conexión con la base de datos
            this.connection = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_USER,
                DatabaseConfig.DB_PASS
            );
            System.out.println("✓ Conexión a la base de datos establecida exitosamente");
        } catch (SQLException e) {
            System.err.println("✗ Error conectando a la base de datos: " + e.getMessage());
            if (DatabaseConfig.DEBUG_MODE) {
                e.printStackTrace();
            }
            this.connection = null;
        }
    }
    
    /**
     * Registrar un nuevo usuario
     * Réplica exacta del método register de PHP
     */
    public Map<String, Object> register(String username, String email, String password) {
        Map<String, Object> result = new HashMap<>();
        
        // Verificar conexión
        if (connection == null) {
            result.put("success", false);
            result.put("message", "Error de conexión a la base de datos");
            return result;
        }
        
        try {
            // Verificar si el usuario ya existe
            if (userExists(username, email)) {
                result.put("success", false);
                result.put("message", "El usuario o email ya existe");
                return result;
            }
            
            // Encriptar la contraseña con MD5 (igual que PHP)
            String hashedPassword = md5(password);
            
            // Preparar la consulta SQL
            String sql = "INSERT INTO users (username, email, password, created_at) VALUES (?, ?, ?, NOW())";
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                int userId = 0;
                if (keys.next()) {
                    userId = keys.getInt(1);
                }
                
                result.put("success", true);
                result.put("message", "Usuario registrado exitosamente");
                result.put("user_id", userId);
            } else {
                result.put("success", false);
                result.put("message", "Error al registrar usuario");
            }
            
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("Error en registro de usuario: " + e.getMessage());
            result.put("success", false);
            result.put("message", "Error interno del servidor");
        }
        
        return result;
    }
    
    /**
     * Autenticar usuario (login)
     * Réplica exacta del método login de PHP
     */
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> result = new HashMap<>();
        
        // Verificar conexión
        if (connection == null) {
            result.put("success", false);
            result.put("message", "Error de conexión a la base de datos");
            return result;
        }
        
        try {
            // Preparar la consulta para buscar el usuario
            String sql = "SELECT id, username, email, password, is_active FROM users WHERE username = ? OR email = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            
            stmt.setString(1, username);
            stmt.setString(2, username);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                boolean isActive = rs.getBoolean("is_active");
                
                // Verificar si la cuenta está activa
                if (!isActive) {
                    result.put("success", false);
                    result.put("message", "Cuenta desactivada. Contacte al administrador");
                    return result;
                }
                
                // Verificar la contraseña (MD5)
                String hashedPassword = md5(password);
                
                if (storedPassword.equals(hashedPassword)) {
                    // Login exitoso
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("id", rs.getInt("id"));
                    userData.put("username", rs.getString("username"));
                    userData.put("email", rs.getString("email"));
                    
                    result.put("success", true);
                    result.put("message", "Login exitoso");
                    result.put("user", userData);
                } else {
                    result.put("success", false);
                    result.put("message", "Credenciales incorrectas");
                }
            } else {
                result.put("success", false);
                result.put("message", "Usuario no encontrado");
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("Error en login de usuario: " + e.getMessage());
            result.put("success", false);
            result.put("message", "Error interno del servidor");
        }
        
        return result;
    }
    
    /**
     * Verificar si un usuario ya existe
     */
    private boolean userExists(String username, String email) {
        // Verificar conexión
        if (connection == null) {
            return false;
        }
        
        try {
            String sql = "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            
            stmt.setString(1, username);
            stmt.setString(2, email);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                rs.close();
                stmt.close();
                return count > 0;
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("Error verificando usuario existente: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Encriptar contraseña con MD5 (igual que PHP)
     */
    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Cerrar conexión
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error cerrando conexión: " + e.getMessage());
        }
    }
}
