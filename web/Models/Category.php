<?php
require_once 'database.php';

class Category {
    private $db;

    public function __construct() {
        $this->db = Database::getInstance()->getConnection();
    }

    public function getAllCategories() {
        $sql = "SELECT * FROM categories ORDER BY name ASC";
        $result = $this->db->query($sql);
        
        $categories = [];
        while ($row = $result->fetch_assoc()) {
            $categories[] = $row;
        }
        return $categories;
    }

    public function getCategoryById($id) {
        $stmt = $this->db->prepare("SELECT * FROM categories WHERE id = ?");
        $stmt->bind_param('i', $id);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_assoc();
    }

    public function createCategory($name, $description = '') {
        $stmt = $this->db->prepare("INSERT INTO categories (name, description) VALUES (?, ?)");
        $stmt->bind_param('ss', $name, $description);
        return $stmt->execute();
    }

    public function updateCategory($id, $name, $description = '') {
        $stmt = $this->db->prepare("UPDATE categories SET name = ?, description = ? WHERE id = ?");
        $stmt->bind_param('ssi', $name, $description, $id);
        return $stmt->execute();
    }

    public function deleteCategory($id) {
        // Verificar si la categoría tiene productos activos asignados
        $stmt = $this->db->prepare("SELECT COUNT(*) as count FROM products WHERE category_id = ? AND is_active = 1");
        $stmt->bind_param('i', $id);
        $stmt->execute();
        $result = $stmt->get_result();
        $count = $result->fetch_assoc()['count'];
        
        if ($count > 0) {
            throw new Exception("No se puede eliminar la categoría porque tiene productos activos asignados");
        }
        
        $stmt = $this->db->prepare("DELETE FROM categories WHERE id = ?");
        $stmt->bind_param('i', $id);
        return $stmt->execute();
    }

    public function getCategoriesWithProductCount() {
        $sql = "
            SELECT 
                c.id,
                c.name,
                c.description,
                c.created_at,
                COUNT(p.id) as product_count
            FROM categories c
            LEFT JOIN products p ON c.id = p.category_id AND p.is_active = 1
            GROUP BY c.id, c.name, c.description, c.created_at
            ORDER BY c.name ASC
        ";
        
        $result = $this->db->query($sql);
        $categories = [];
        while ($row = $result->fetch_assoc()) {
            $categories[] = $row;
        }
        return $categories;
    }

    public function searchCategories($searchTerm) {
        $searchTerm = '%' . $searchTerm . '%';
        $stmt = $this->db->prepare("
            SELECT 
                c.id,
                c.name,
                c.description,
                c.created_at,
                COUNT(p.id) as product_count
            FROM categories c
            LEFT JOIN products p ON c.id = p.category_id AND p.is_active = 1
            WHERE c.name LIKE ? OR c.description LIKE ?
            GROUP BY c.id, c.name, c.description, c.created_at
            ORDER BY c.name ASC
        ");
        $stmt->bind_param('ss', $searchTerm, $searchTerm);
        $stmt->execute();
        $result = $stmt->get_result();
        
        $categories = [];
        while ($row = $result->fetch_assoc()) {
            $categories[] = $row;
        }
        return $categories;
    }
}
?>
