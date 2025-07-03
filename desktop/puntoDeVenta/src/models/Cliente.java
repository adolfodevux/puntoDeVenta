package models;

import config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private int id;
    private String nombre;
    private String telefono;
    private int totalCompras;
    private double montoTotalCompras;
    private String ultimaCompra;

    // Constructores
    public Cliente() {}

    public Cliente(int id, String nombre, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public int getTotalCompras() { return totalCompras; }
    public void setTotalCompras(int totalCompras) { this.totalCompras = totalCompras; }

    public double getMontoTotalCompras() { return montoTotalCompras; }
    public void setMontoTotalCompras(double montoTotalCompras) { this.montoTotalCompras = montoTotalCompras; }

    public String getUltimaCompra() { return ultimaCompra; }
    public void setUltimaCompra(String ultimaCompra) { this.ultimaCompra = ultimaCompra; }

    // Métodos de base de datos
    public static List<Cliente> obtenerTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY nombre ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setTelefono(rs.getString("telefono"));
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }

    public static List<Cliente> obtenerTodosConVentas() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT c.id, c.nombre, c.telefono, " +
                    "COALESCE(COUNT(s.id), 0) as total_compras, " +
                    "COALESCE(SUM(s.total_amount), 0) as monto_total_compras, " +
                    "MAX(s.sale_date) as ultima_compra " +
                    "FROM clientes c " +
                    "LEFT JOIN sales s ON c.id = s.cliente_id " +
                    "GROUP BY c.id, c.nombre, c.telefono " +
                    "ORDER BY c.id ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setTotalCompras(rs.getInt("total_compras"));
                cliente.setMontoTotalCompras(rs.getDouble("monto_total_compras"));
                cliente.setUltimaCompra(rs.getString("ultima_compra"));
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }

    public static Cliente obtenerPorId(int id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setId(rs.getInt("id"));
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setTelefono(rs.getString("telefono"));
                    return cliente;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Cliente> buscarPorNombre(String nombre) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE nombre LIKE ? ORDER BY nombre ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nombre + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setId(rs.getInt("id"));
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setTelefono(rs.getString("telefono"));
                    clientes.add(cliente);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }

    public boolean guardar() {
        if (id == 0) {
            return crear();
        } else {
            return actualizar();
        }
    }

    private boolean crear() {
        String sql = "INSERT INTO clientes (nombre, telefono) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, nombre);
            stmt.setString(2, telefono);
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        this.id = generatedKeys.getInt(1);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean actualizar() {
        String sql = "UPDATE clientes SET nombre = ?, telefono = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            stmt.setString(2, telefono);
            stmt.setInt(3, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar() {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        return nombre + " - " + telefono;
    }
}
