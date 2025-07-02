<?php
require_once __DIR__ . '/database.php';

class Supplier {
    private $db;

    public function __construct() {
        $this->db = Database::getInstance()->getConnection();
    }

    public function getAll() {
        $query = "SELECT * FROM suppliers WHERE is_active = 1 ORDER BY name ASC";
        $result = $this->db->query($query);
        
        if (!$result) {
            return [];
        }
        
        $suppliers = [];
        while ($row = $result->fetch_assoc()) {
            $suppliers[] = $row;
        }
        return $suppliers;
    }

    public function getById($id) {
        $stmt = $this->db->prepare("SELECT * FROM suppliers WHERE id = ? AND is_active = 1");
        $stmt->bind_param('i', $id);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_assoc();
    }

    public function create($data) {
        $stmt = $this->db->prepare("INSERT INTO suppliers (name, contact_person, email, phone, address, city, country, tax_id, website, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        
        return $stmt->bind_param('ssssssssss',
            $data['name'],
            $data['contact_person'],
            $data['email'],
            $data['phone'],
            $data['address'],
            $data['city'],
            $data['country'],
            $data['tax_id'],
            $data['website'],
            $data['notes']
        ) && $stmt->execute();
    }

    public function update($id, $data) {
        $stmt = $this->db->prepare("UPDATE suppliers SET name = ?, contact_person = ?, email = ?, phone = ?, address = ?, city = ?, country = ?, tax_id = ?, website = ?, notes = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?");
        
        return $stmt->bind_param('ssssssssssi',
            $data['name'],
            $data['contact_person'],
            $data['email'],
            $data['phone'],
            $data['address'],
            $data['city'],
            $data['country'],
            $data['tax_id'],
            $data['website'],
            $data['notes'],
            $id
        ) && $stmt->execute();
    }

    public function delete($id) {
        // Soft delete
        $stmt = $this->db->prepare("UPDATE suppliers SET is_active = 0 WHERE id = ?");
        $stmt->bind_param('i', $id);
        return $stmt->execute();
    }

    public function getSupplierProducts($supplierId) {
        $stmt = $this->db->prepare("SELECT p.*, c.name as category_name 
                  FROM products p 
                  LEFT JOIN categories c ON p.category_id = c.id 
                  WHERE p.supplier_id = ? AND p.is_active = 1 
                  ORDER BY p.name ASC");
        $stmt->bind_param('i', $supplierId);
        $stmt->execute();
        $result = $stmt->get_result();
        
        $products = [];
        while ($row = $result->fetch_assoc()) {
            $products[] = $row;
        }
        return $products;
    }

    public function search($term) {
        $searchTerm = '%' . $term . '%';
        $stmt = $this->db->prepare("SELECT * FROM suppliers 
                  WHERE is_active = 1 AND (
                      name LIKE ? OR 
                      contact_person LIKE ? OR 
                      email LIKE ? OR 
                      city LIKE ?
                  ) ORDER BY name ASC");
        $stmt->bind_param('ssss', $searchTerm, $searchTerm, $searchTerm, $searchTerm);
        $stmt->execute();
        $result = $stmt->get_result();
        
        $suppliers = [];
        while ($row = $result->fetch_assoc()) {
            $suppliers[] = $row;
        }
        return $suppliers;
    }

    public function getStats() {
        $stats = [];
        
        // Total de proveedores activos
        $result = $this->db->query("SELECT COUNT(*) as total_suppliers FROM suppliers WHERE is_active = 1");
        $stats['total_suppliers'] = $result->fetch_assoc()['total_suppliers'];
        
        // Proveedores con productos
        $result = $this->db->query("SELECT COUNT(DISTINCT s.id) as suppliers_with_products 
                  FROM suppliers s 
                  INNER JOIN products p ON s.id = p.supplier_id 
                  WHERE s.is_active = 1 AND p.is_active = 1");
        $stats['suppliers_with_products'] = $result->fetch_assoc()['suppliers_with_products'];
        
        // Top 5 proveedores por cantidad de productos
        $result = $this->db->query("SELECT s.name, COUNT(p.id) as product_count 
                  FROM suppliers s 
                  INNER JOIN products p ON s.id = p.supplier_id 
                  WHERE s.is_active = 1 AND p.is_active = 1 
                  GROUP BY s.id, s.name 
                  ORDER BY product_count DESC 
                  LIMIT 5");
        
        $topSuppliers = [];
        if ($result) {
            while ($row = $result->fetch_assoc()) {
                $topSuppliers[] = $row;
            }
        }
        $stats['top_suppliers'] = $topSuppliers;
        
        return $stats;
    }
}
?>
