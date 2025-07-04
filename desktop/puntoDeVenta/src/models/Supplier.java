package models;

import config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Supplier {
    private int id;
    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String country;
    private String taxId;
    private String website;
    private String notes;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructores
    public Supplier() {}

    public Supplier(int id, String name) {
        this.id = id;
        this.name = name;
        this.isActive = true;
    }

    public Supplier(String name, String contactPerson, String email, String phone, 
                   String address, String city, String country, String taxId, 
                   String website, String notes) {
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.country = country;
        this.taxId = taxId;
        this.website = website;
        this.notes = notes;
        this.isActive = true;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // Método auxiliar para obtener la ubicación completa
    public String getLocation() {
        StringBuilder location = new StringBuilder();
        if (city != null && !city.trim().isEmpty()) {
            location.append(city);
        }
        if (country != null && !country.trim().isEmpty()) {
            if (location.length() > 0) location.append(", ");
            location.append(country);
        }
        return location.length() > 0 ? location.toString() : "No especificada";
    }

    // Métodos de base de datos
    public static List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT id, name, contact_person, email, phone, address, city, country, " +
                    "tax_id, website, notes, is_active, created_at, updated_at " +
                    "FROM suppliers WHERE is_active = 1 ORDER BY name ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setId(rs.getInt("id"));
                supplier.setName(rs.getString("name"));
                supplier.setContactPerson(rs.getString("contact_person"));
                supplier.setEmail(rs.getString("email"));
                supplier.setPhone(rs.getString("phone"));
                supplier.setAddress(rs.getString("address"));
                supplier.setCity(rs.getString("city"));
                supplier.setCountry(rs.getString("country"));
                supplier.setTaxId(rs.getString("tax_id"));
                supplier.setWebsite(rs.getString("website"));
                supplier.setNotes(rs.getString("notes"));
                supplier.setActive(rs.getBoolean("is_active"));
                supplier.setCreatedAt(rs.getTimestamp("created_at"));
                supplier.setUpdatedAt(rs.getTimestamp("updated_at"));
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    // Obtener proveedor por ID
    public static Supplier getById(int id) {
        String sql = "SELECT id, name, contact_person, email, phone, address, city, country, " +
                    "tax_id, website, notes, is_active, created_at, updated_at " +
                    "FROM suppliers WHERE id = ? AND is_active = 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Supplier supplier = new Supplier();
                    supplier.setId(rs.getInt("id"));
                    supplier.setName(rs.getString("name"));
                    supplier.setContactPerson(rs.getString("contact_person"));
                    supplier.setEmail(rs.getString("email"));
                    supplier.setPhone(rs.getString("phone"));
                    supplier.setAddress(rs.getString("address"));
                    supplier.setCity(rs.getString("city"));
                    supplier.setCountry(rs.getString("country"));
                    supplier.setTaxId(rs.getString("tax_id"));
                    supplier.setWebsite(rs.getString("website"));
                    supplier.setNotes(rs.getString("notes"));
                    supplier.setActive(rs.getBoolean("is_active"));
                    supplier.setCreatedAt(rs.getTimestamp("created_at"));
                    supplier.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return supplier;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Guardar proveedor (CREATE)
    public boolean save() {
        String sql = "INSERT INTO suppliers (name, contact_person, email, phone, address, city, " +
                    "country, tax_id, website, notes, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, this.name);
            stmt.setString(2, this.contactPerson);
            stmt.setString(3, this.email);
            stmt.setString(4, this.phone);
            stmt.setString(5, this.address);
            stmt.setString(6, this.city);
            stmt.setString(7, this.country);
            stmt.setString(8, this.taxId);
            stmt.setString(9, this.website);
            stmt.setString(10, this.notes);
            stmt.setBoolean(11, this.isActive);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        this.id = generatedKeys.getInt(1);
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Actualizar proveedor (UPDATE)
    public boolean update() {
        String sql = "UPDATE suppliers SET name = ?, contact_person = ?, email = ?, phone = ?, " +
                    "address = ?, city = ?, country = ?, tax_id = ?, website = ?, notes = ? " +
                    "WHERE id = ? AND is_active = 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, this.name);
            stmt.setString(2, this.contactPerson);
            stmt.setString(3, this.email);
            stmt.setString(4, this.phone);
            stmt.setString(5, this.address);
            stmt.setString(6, this.city);
            stmt.setString(7, this.country);
            stmt.setString(8, this.taxId);
            stmt.setString(9, this.website);
            stmt.setString(10, this.notes);
            stmt.setInt(11, this.id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Eliminar proveedor (DELETE - soft delete)
    public boolean delete() {
        String sql = "UPDATE suppliers SET is_active = 0 WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, this.id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Eliminar proveedor por ID (static)
    public static boolean deleteById(int id) {
        String sql = "UPDATE suppliers SET is_active = 0 WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Buscar proveedores por nombre o contacto
    public static List<Supplier> search(String searchTerm) {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT id, name, contact_person, email, phone, address, city, country, " +
                    "tax_id, website, notes, is_active, created_at, updated_at " +
                    "FROM suppliers WHERE is_active = 1 AND " +
                    "(name LIKE ? OR contact_person LIKE ? OR email LIKE ?) ORDER BY name ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Supplier supplier = new Supplier();
                    supplier.setId(rs.getInt("id"));
                    supplier.setName(rs.getString("name"));
                    supplier.setContactPerson(rs.getString("contact_person"));
                    supplier.setEmail(rs.getString("email"));
                    supplier.setPhone(rs.getString("phone"));
                    supplier.setAddress(rs.getString("address"));
                    supplier.setCity(rs.getString("city"));
                    supplier.setCountry(rs.getString("country"));
                    supplier.setTaxId(rs.getString("tax_id"));
                    supplier.setWebsite(rs.getString("website"));
                    supplier.setNotes(rs.getString("notes"));
                    supplier.setActive(rs.getBoolean("is_active"));
                    supplier.setCreatedAt(rs.getTimestamp("created_at"));
                    supplier.setUpdatedAt(rs.getTimestamp("updated_at"));
                    suppliers.add(supplier);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    // Contar proveedores activos
    public static int getActiveCount() {
        String sql = "SELECT COUNT(*) FROM suppliers WHERE is_active = 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Contar proveedores que tienen productos asociados
    public static int getSuppliersWithProductsCount() {
        String sql = "SELECT COUNT(DISTINCT s.id) FROM suppliers s " +
                    "INNER JOIN products p ON s.id = p.supplier_id " +
                    "WHERE s.is_active = 1 AND p.is_active = 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
