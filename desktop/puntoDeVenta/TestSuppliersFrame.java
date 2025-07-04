import models.Supplier;

public class TestSuppliersFrame {
    public static void main(String[] args) {
        System.out.println("Probando datos de proveedores...");
        
        // Probar el modelo Supplier
        System.out.println("Total proveedores: " + Supplier.getAllSuppliers().size());
        System.out.println("Proveedores con productos: " + Supplier.getSuppliersWithProductsCount());
        
        // Crear la ventana
        javax.swing.SwingUtilities.invokeLater(() -> {
            views.suppliers.SuppliersFrame frame = new views.suppliers.SuppliersFrame();
            frame.setVisible(true);
        });
    }
}
