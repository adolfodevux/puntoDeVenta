package utils;

import models.Product;
import models.Sale.SaleItem;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class CartManager {
    private List<CartItem> items;
    private CartTableModel tableModel;
    
    public CartManager() {
        this.items = new ArrayList<>();
        this.tableModel = new CartTableModel();
    }
    
    public void addProduct(Product product, int quantity) {
        // Buscar si el producto ya está en el carrito
        for (CartItem item : items) {
            if (item.getProductId() == product.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                tableModel.fireTableDataChanged();
                return;
            }
        }
        
        // Si no está, agregarlo como nuevo item
        CartItem newItem = new CartItem(product.getId(), product.getName(), quantity, product.getPrice());
        items.add(newItem);
        tableModel.fireTableDataChanged();
    }
    
    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            tableModel.fireTableDataChanged();
        }
    }
    
    public void updateQuantity(int index, int newQuantity) {
        if (index >= 0 && index < items.size() && newQuantity > 0) {
            items.get(index).setQuantity(newQuantity);
            tableModel.fireTableDataChanged();
        }
    }
    
    public void clearCart() {
        items.clear();
        tableModel.fireTableDataChanged();
    }
    
    public double getSubtotal() {
        return items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }
    
    public double getTax() {
        return getSubtotal() * 0.16; // IVA 16%
    }
    
    public double getTotal() {
        return getSubtotal() + getTax();
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    public int getItemCount() {
        return items.size();
    }
    
    public List<SaleItem> getSaleItems() {
        List<SaleItem> saleItems = new ArrayList<>();
        for (CartItem item : items) {
            saleItems.add(new SaleItem(item.getProductId(), item.getProductName(), 
                                     item.getQuantity(), item.getPrice()));
        }
        return saleItems;
    }
    
    public CartTableModel getTableModel() {
        return tableModel;
    }
    
    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }
    
    // Clase para representar un item del carrito
    public static class CartItem {
        private int productId;
        private String productName;
        private int quantity;
        private double price;
        
        public CartItem(int productId, String productName, int quantity, double price) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }
        
        // Getters y Setters
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
    
    // Modelo de tabla para el carrito
    public class CartTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Producto", "Cantidad", "Precio", "Total"};
        
        @Override
        public int getRowCount() {
            return items.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            CartItem item = items.get(rowIndex);
            switch (columnIndex) {
                case 0: return item.getProductName();
                case 1: return item.getQuantity();
                case 2: return String.format("$%.2f", item.getPrice());
                case 3: return String.format("$%.2f", item.getTotal());
                default: return null;
            }
        }
        
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 1; // Solo la cantidad es editable
        }
        
        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            if (columnIndex == 1) {
                try {
                    int newQuantity = Integer.parseInt(value.toString());
                    if (newQuantity > 0) {
                        items.get(rowIndex).setQuantity(newQuantity);
                        fireTableDataChanged();
                    }
                } catch (NumberFormatException e) {
                    // Ignorar valores inválidos
                }
            }
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0: return String.class;
                case 1: return Integer.class;
                case 2:
                case 3: return String.class;
                default: return Object.class;
            }
        }
    }
}
