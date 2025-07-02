package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestor de sesión para mantener el estado del usuario
 * Réplica de la funcionalidad de sesión PHP
 */
public class SessionManager {
    private static SessionManager instance;
    private Map<String, Object> sessionData;
    private boolean loggedIn;
    
    private SessionManager() {
        this.sessionData = new HashMap<>();
        this.loggedIn = false;
    }
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Iniciar sesión de usuario
     */
    public void login(Map<String, Object> userData) {
        this.loggedIn = true;
        this.sessionData.put("user_id", userData.get("id"));
        this.sessionData.put("username", userData.get("username"));
        this.sessionData.put("email", userData.get("email"));
        this.sessionData.put("logged_in", true);
    }
    
    /**
     * Cerrar sesión
     */
    public void logout() {
        this.loggedIn = false;
        this.sessionData.clear();
    }
    
    /**
     * Verificar si el usuario está logueado
     */
    public boolean isLoggedIn() {
        return this.loggedIn && (Boolean) this.sessionData.getOrDefault("logged_in", false);
    }
    
    /**
     * Obtener ID del usuario
     */
    public Integer getUserId() {
        return (Integer) this.sessionData.get("user_id");
    }
    
    /**
     * Obtener nombre de usuario
     */
    public String getUsername() {
        return (String) this.sessionData.getOrDefault("username", "Usuario");
    }
    
    /**
     * Obtener email del usuario
     */
    public String getEmail() {
        return (String) this.sessionData.getOrDefault("email", "");
    }
    
    /**
     * Establecer datos de sesión
     */
    public void setSessionData(String key, Object value) {
        this.sessionData.put(key, value);
    }
    
    /**
     * Obtener datos de sesión
     */
    public Object getSessionData(String key) {
        return this.sessionData.get(key);
    }
    
    /**
     * Obtener todos los datos de sesión
     */
    public Map<String, Object> getAllSessionData() {
        return new HashMap<>(this.sessionData);
    }
}
