package models;

import config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class Sale {
    private int id;
    private int userId;
    private String username;
    private int clienteId;
    private String clienteNombre;
    private double totalAmount;
    private double subtotal;
    private double taxAmount;
    private double discountAmount;
    private String paymentMethod;
    private double amountPaid;
    private double changeAmount;
    private LocalDateTime saleDate;
    private String status;
    private String notes;
    private List<SaleItem> items;

    // Constructores
    public Sale() {
        this.items = new ArrayList<>();
        this.saleDate = LocalDateTime.now();
        this.status = "completed";
        this.paymentMethod = "cash";
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }

    public double getChangeAmount() { return changeAmount; }
    public void setChangeAmount(double changeAmount) { this.changeAmount = changeAmount; }

    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<SaleItem> getItems() { return items; }
    public void setItems(List<SaleItem> items) { this.items = items; }

    // Métodos de cálculo
    public void calculateTotals() {
        subtotal = items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        taxAmount = subtotal * 0.16; // IVA 16%
        totalAmount = subtotal + taxAmount - discountAmount;
        changeAmount = amountPaid - totalAmount;
    }

    // Métodos de base de datos
    public boolean save() {
        calculateTotals();
        
        String saleSql = "INSERT INTO sales (user_id, cliente_id, total_amount, subtotal, tax_amount, " +
                        "discount_amount, payment_method, amount_paid, change_amount, sale_date, status, notes) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement saleStmt = conn.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS)) {
                saleStmt.setInt(1, userId);
                saleStmt.setObject(2, clienteId > 0 ? clienteId : null);
                saleStmt.setDouble(3, totalAmount);
                saleStmt.setDouble(4, subtotal);
                saleStmt.setDouble(5, taxAmount);
                saleStmt.setDouble(6, discountAmount);
                saleStmt.setString(7, paymentMethod);
                saleStmt.setDouble(8, amountPaid);
                saleStmt.setDouble(9, changeAmount);
                saleStmt.setTimestamp(10, Timestamp.valueOf(saleDate));
                saleStmt.setString(11, status);
                saleStmt.setString(12, notes);

                int result = saleStmt.executeUpdate();
                if (result > 0) {
                    try (ResultSet generatedKeys = saleStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            this.id = generatedKeys.getInt(1);

                            // Guardar items de venta
                            String itemSql = "INSERT INTO sale_items (sale_id, product_id, product_name, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?, ?)";
                            try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                                for (SaleItem item : items) {
                                    itemStmt.setInt(1, this.id);
                                    itemStmt.setInt(2, item.getProductId());
                                    itemStmt.setString(3, item.getProductName());
                                    itemStmt.setInt(4, item.getQuantity());
                                    itemStmt.setDouble(5, item.getPrice());
                                    itemStmt.setDouble(6, item.getPrice() * item.getQuantity());
                                    itemStmt.addBatch();
                                }
                                itemStmt.executeBatch();
                            }

                            // Actualizar stock de productos
                            String updateStockSql = "UPDATE products SET stock = stock - ? WHERE id = ?";
                            try (PreparedStatement stockStmt = conn.prepareStatement(updateStockSql)) {
                                for (SaleItem item : items) {
                                    stockStmt.setInt(1, item.getQuantity());
                                    stockStmt.setInt(2, item.getProductId());
                                    stockStmt.addBatch();
                                }
                                stockStmt.executeBatch();
                            }

                            conn.commit();
                            return true;
                        }
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Sale> getAllSales() {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT s.id, s.user_id, u.username, s.cliente_id, c.nombre as cliente_nombre, " +
                    "s.total_amount, s.subtotal, s.tax_amount, s.discount_amount, s.payment_method, " +
                    "s.amount_paid, s.change_amount, s.sale_date, s.status, s.notes, " +
                    "COUNT(si.id) as items_count " +
                    "FROM sales s " +
                    "LEFT JOIN users u ON s.user_id = u.id " +
                    "LEFT JOIN clientes c ON s.cliente_id = c.id " +
                    "LEFT JOIN sale_items si ON s.id = si.sale_id " +
                    "GROUP BY s.id " +
                    "ORDER BY s.sale_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Sale sale = new Sale();
                sale.setId(rs.getInt("id"));
                sale.setUserId(rs.getInt("user_id"));
                sale.setUsername(rs.getString("username"));
                sale.setClienteId(rs.getInt("cliente_id"));
                sale.setClienteNombre(rs.getString("cliente_nombre"));
                sale.setTotalAmount(rs.getDouble("total_amount"));
                sale.setSubtotal(rs.getDouble("subtotal"));
                sale.setTaxAmount(rs.getDouble("tax_amount"));
                sale.setDiscountAmount(rs.getDouble("discount_amount"));
                sale.setPaymentMethod(rs.getString("payment_method"));
                sale.setAmountPaid(rs.getDouble("amount_paid"));
                sale.setChangeAmount(rs.getDouble("change_amount"));
                
                Timestamp timestamp = rs.getTimestamp("sale_date");
                if (timestamp != null) {
                    sale.setSaleDate(timestamp.toLocalDateTime());
                }
                
                sale.setStatus(rs.getString("status"));
                sale.setNotes(rs.getString("notes"));
                sales.add(sale);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }

    // Método para obtener los items de una venta específica
    public static List<SaleItem> getSaleItems(int saleId) {
        List<SaleItem> items = new ArrayList<>();
        String sql = "SELECT si.id, si.sale_id, si.product_id, si.product_name, " +
                    "si.quantity, si.unit_price " +
                    "FROM sale_items si " +
                    "WHERE si.sale_id = ? " +
                    "ORDER BY si.id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, saleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SaleItem item = new SaleItem();
                    item.setId(rs.getInt("id"));
                    item.setSaleId(rs.getInt("sale_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setProductName(rs.getString("product_name"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPrice(rs.getDouble("unit_price"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    // Clase interna para items de venta
    public static class SaleItem {
        private int id;
        private int saleId;
        private int productId;
        private String productName;
        private int quantity;
        private double price;

        public SaleItem() {}

        public SaleItem(int productId, String productName, int quantity, double price) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }

        // Getters y Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public int getSaleId() { return saleId; }
        public void setSaleId(int saleId) { this.saleId = saleId; }

        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public double getTotal() { return price * quantity; }
    }
}
