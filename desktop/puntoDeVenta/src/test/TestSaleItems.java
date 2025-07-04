package test;

import models.Sale;
import java.util.List;

public class TestSaleItems {
    public static void main(String[] args) {
        System.out.println("Probando obtención de items de venta...");
        
        // Obtener todas las ventas primero
        List<Sale> sales = Sale.getAllSales();
        System.out.println("Número de ventas encontradas: " + sales.size());
        
        if (!sales.isEmpty()) {
            Sale firstSale = sales.get(0);
            System.out.println("Probando con venta ID: " + firstSale.getId());
            
            // Obtener los items de la primera venta
            List<Sale.SaleItem> items = Sale.getSaleItems(firstSale.getId());
            System.out.println("Número de items encontrados: " + items.size());
            
            for (Sale.SaleItem item : items) {
                System.out.println("- Producto: " + item.getProductName() + 
                                 ", Cantidad: " + item.getQuantity() + 
                                 ", Precio: $" + item.getPrice() + 
                                 ", Total: $" + item.getTotal());
            }
        } else {
            System.out.println("No se encontraron ventas en la base de datos.");
        }
    }
}
