package models;

import config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private int categoryId;
    private String categoryName;
    private int supplierId;
    private String supplierName;
    private String barcode;
    private boolean isActive;

    // Constructores
    public Product() {}

    public Product(int id, String name, String description, double price, int stock, int categoryId, String categoryName, int supplierId, String supplierName, String barcode, boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.barcode = barcode;
        this.isActive = isActive;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // Métodos de base de datos
    public static List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name, s.name as supplier_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.id " +
                    "LEFT JOIN suppliers s ON p.supplier_id = s.id " +
                    "WHERE p.is_active = 1 ORDER BY p.name ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getDouble("price"));
                product.setStock(rs.getInt("stock"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setCategoryName(rs.getString("category_name"));
                product.setSupplierId(rs.getInt("supplier_id"));
                product.setSupplierName(rs.getString("supplier_name"));
                product.setBarcode(rs.getString("barcode"));
                product.setActive(rs.getBoolean("is_active"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public static List<Product> getProductsByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name, s.name as supplier_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.id " +
                    "LEFT JOIN suppliers s ON p.supplier_id = s.id " +
                    "WHERE p.is_active = 1 AND p.category_id = ? ORDER BY p.name ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setDescription(rs.getString("description"));
                    product.setPrice(rs.getDouble("price"));
                    product.setStock(rs.getInt("stock"));
                    product.setCategoryId(rs.getInt("category_id"));
                    product.setCategoryName(rs.getString("category_name"));
                    product.setSupplierId(rs.getInt("supplier_id"));
                    product.setSupplierName(rs.getString("supplier_name"));
                    product.setBarcode(rs.getString("barcode"));
                    product.setActive(rs.getBoolean("is_active"));
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public static List<Product> searchProducts(String searchTerm) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name, s.name as supplier_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.id " +
                    "LEFT JOIN suppliers s ON p.supplier_id = s.id " +
                    "WHERE p.is_active = 1 AND (p.name LIKE ? OR p.description LIKE ?) " +
                    "ORDER BY p.name ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setDescription(rs.getString("description"));
                    product.setPrice(rs.getDouble("price"));
                    product.setStock(rs.getInt("stock"));
                    product.setCategoryId(rs.getInt("category_id"));
                    product.setCategoryName(rs.getString("category_name"));
                    product.setSupplierId(rs.getInt("supplier_id"));
                    product.setSupplierName(rs.getString("supplier_name"));
                    product.setBarcode(rs.getString("barcode"));
                    product.setActive(rs.getBoolean("is_active"));
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public boolean updateStock(int newStock) {
        String sql = "UPDATE products SET stock = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newStock);
            stmt.setInt(2, this.id);
            int result = stmt.executeUpdate();
            if (result > 0) {
                this.stock = newStock;
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isLowStock() {
        return stock <= 30;
    }

    // Método para insertar un nuevo producto
    public boolean save() {
        String sql = "INSERT INTO products (name, description, price, stock, category_id, supplier_id, barcode, is_active) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, this.name);
            stmt.setString(2, this.description);
            stmt.setDouble(3, this.price);
            stmt.setInt(4, this.stock);
            stmt.setInt(5, this.categoryId);
            stmt.setInt(6, this.supplierId);
            stmt.setString(7, this.barcode);
            stmt.setBoolean(8, this.isActive);
            
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                // Obtener el ID generado
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

    // Método para verificar si un código de barras ya existe
    public static boolean barcodeExists(String barcode) {
        String sql = "SELECT COUNT(*) FROM products WHERE barcode = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, barcode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método para generar un código de barras único
    public static String generateUniqueBarcode() {
        String barcode;
        do {
            barcode = generateBarcode();
        } while (barcodeExists(barcode));
        return barcode;
    }

    // Método para generar código de barras aleatorio de 13 dígitos
    private static String generateBarcode() {
        StringBuilder barcode = new StringBuilder();
        for (int i = 0; i < 13; i++) {
            barcode.append((int) (Math.random() * 10));
        }
        return barcode.toString();
    }

    // Método para actualizar un producto existente
    public boolean update() {
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, stock = ?, " +
                    "category_id = ?, supplier_id = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, this.name);
            stmt.setString(2, this.description);
            stmt.setDouble(3, this.price);
            stmt.setInt(4, this.stock);
            stmt.setInt(5, this.categoryId);
            stmt.setInt(6, this.supplierId);
            stmt.setInt(7, this.id);
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método para eliminar un producto (eliminación lógica)
    public boolean delete() {
        String sql = "UPDATE products SET is_active = 0 WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, this.id);
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                this.isActive = false;
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        return name + " - $" + String.format("%.2f", price) + " (Stock: " + stock + ")";
    }
}
