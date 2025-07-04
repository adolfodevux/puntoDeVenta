import models.Supplier;
import java.util.List;

public class TestSuppliersData {
    public static void main(String[] args) {
        try {
            List<Supplier> suppliers = Supplier.getAllSuppliers();
            System.out.println("Número de proveedores cargados: " + suppliers.size());
            for (Supplier s : suppliers) {
                System.out.println("ID: " + s.getId() + 
                                 ", Nombre: " + s.getName() + 
                                 ", Email: " + s.getEmail() +
                                 ", Teléfono: " + s.getPhone() +
                                 ", Ubicación: " + s.getLocation());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
