<?php
// Controlador para manejo de productos
require_once __DIR__ . '/../Models/database.php';

header('Content-Type: application/json');

class ProductsController {
    private $db;
    
    public function __construct() {
        $this->db = Database::getInstance();
    }
    
    public function getProducts() {
        try {
            $conn = $this->db->getConnection();
            
            $query = "SELECT p.*, c.name as category_name, s.name as supplier_name 
                     FROM products p 
                     LEFT JOIN categories c ON p.category_id = c.id 
                     LEFT JOIN suppliers s ON p.supplier_id = s.id 
                     WHERE p.is_active = 1 ORDER BY p.name";
            $result = $conn->query($query);
            
            if (!$result) {
                throw new Exception('Error al ejecutar consulta');
            }
            
            $products = [];
            while ($row = $result->fetch_assoc()) {
                $products[] = $row;
            }
            
            echo json_encode([
                'success' => true,
                'data' => $products
            ]);
            
        } catch (Exception $e) {
            echo json_encode([
                'success' => false,
                'message' => 'Error al obtener productos: ' . $e->getMessage()
            ]);
        }
    }
    
    public function getCategories() {
        try {
            $conn = $this->db->getConnection();
            
            $query = "SELECT * FROM categories WHERE is_active = 1 ORDER BY name";
            $result = $conn->query($query);
            
            if (!$result) {
                throw new Exception('Error al ejecutar consulta');
            }
            
            $categories = [];
            while ($row = $result->fetch_assoc()) {
                $categories[] = $row;
            }
            
            echo json_encode([
                'success' => true,
                'data' => $categories
            ]);
            
        } catch (Exception $e) {
            echo json_encode([
                'success' => false,
                'message' => 'Error al obtener categorías: ' . $e->getMessage()
            ]);
        }
    }
    
    public function getSuppliers() {
        try {
            $conn = $this->db->getConnection();
            
            $query = "SELECT * FROM suppliers WHERE is_active = 1 ORDER BY name";
            $result = $conn->query($query);
            
            if (!$result) {
                throw new Exception('Error al ejecutar consulta');
            }
            
            $suppliers = [];
            while ($row = $result->fetch_assoc()) {
                $suppliers[] = $row;
            }
            
            echo json_encode([
                'success' => true,
                'data' => $suppliers
            ]);
            
        } catch (Exception $e) {
            echo json_encode([
                'success' => false,
                'message' => 'Error al obtener proveedores: ' . $e->getMessage()
            ]);
        }
    }
    
    // Método para uso interno que devuelve el array directamente
    public function getSuppliersArray() {
        try {
            $conn = $this->db->getConnection();
            
            $query = "SELECT * FROM suppliers WHERE is_active = 1 ORDER BY name";
            $result = $conn->query($query);
            
            if (!$result) {
                return [];
            }
            
            $suppliers = [];
            while ($row = $result->fetch_assoc()) {
                $suppliers[] = $row;
            }
            
            return $suppliers;
            
        } catch (Exception $e) {
            return [];
        }
    }
    
    public function searchProducts($searchTerm) {
        try {
            $conn = $this->db->getConnection();
            
            $query = "SELECT p.*, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE p.is_active = 1 AND (p.name LIKE ? OR p.barcode LIKE ?) ORDER BY p.name";
            $stmt = $conn->prepare($query);
            $searchPattern = '%' . $searchTerm . '%';
            $stmt->bind_param('ss', $searchPattern, $searchPattern);
            $stmt->execute();
            $result = $stmt->get_result();
            
            $products = [];
            while ($row = $result->fetch_assoc()) {
                $products[] = $row;
            }
            $stmt->close();
            
            echo json_encode([
                'success' => true,
                'data' => $products
            ]);
            
        } catch (Exception $e) {
            echo json_encode([
                'success' => false,
                'message' => 'Error al buscar productos: ' . $e->getMessage()
            ]);
        }
    }
    
    public function getProductsByCategory($categoryId) {
        try {
            $conn = $this->db->getConnection();
            
            if ($categoryId === 'all') {
                return $this->getProducts();
            }
            
            $query = "SELECT p.*, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE p.is_active = 1 AND p.category_id = ? ORDER BY p.name";
            $stmt = $conn->prepare($query);
            $stmt->bind_param('i', $categoryId);
            $stmt->execute();
            $result = $stmt->get_result();
            
            $products = [];
            while ($row = $result->fetch_assoc()) {
                $products[] = $row;
            }
            $stmt->close();
            
            echo json_encode([
                'success' => true,
                'data' => $products
            ]);
            
        } catch (Exception $e) {
            echo json_encode([
                'success' => false,
                'message' => 'Error al obtener productos por categoría: ' . $e->getMessage()
            ]);
        }
    }
    
    public function updateStock($productId, $quantity) {
        try {
            $conn = $this->db->getConnection();
            
            // Verificar stock actual
            $checkQuery = "SELECT stock FROM products WHERE id = ? AND is_active = 1";
            $checkStmt = $conn->prepare($checkQuery);
            $checkStmt->bind_param('i', $productId);
            $checkStmt->execute();
            $result = $checkStmt->get_result();
            $product = $result->fetch_assoc();
            $checkStmt->close();
            
            if (!$product) {
                throw new Exception('Producto no encontrado');
            }
            
            $newStock = $product['stock'] - $quantity;
            if ($newStock < 0) {
                throw new Exception('Stock insuficiente');
            }
            
            // Actualizar stock
            $updateQuery = "UPDATE products SET stock = ? WHERE id = ?";
            $updateStmt = $conn->prepare($updateQuery);
            $updateStmt->bind_param('ii', $newStock, $productId);
            
            if (!$updateStmt->execute()) {
                throw new Exception('Error al actualizar stock');
            }
            $updateStmt->close();
            
            echo json_encode([
                'success' => true,
                'message' => 'Stock actualizado correctamente',
                'new_stock' => $newStock
            ]);
            
        } catch (Exception $e) {
            echo json_encode([
                'success' => false,
                'message' => 'Error al actualizar stock: ' . $e->getMessage()
            ]);
        }
    }
    
    // AGREGAR PRODUCTO
    public function addProduct($name, $category_id, $description, $barcode, $stock, $price, $supplier_id = null) {
        try {
            $conn = $this->db->getConnection();
            $stmt = $conn->prepare("INSERT INTO products (name, category_id, description, barcode, stock, price, supplier_id, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, 1)");
            $stmt->bind_param('sissidi', $name, $category_id, $description, $barcode, $stock, $price, $supplier_id);
            $stmt->execute();
            $stmt->close();
            echo json_encode(['success' => true, 'message' => 'Producto agregado']);
        } catch (Exception $e) {
            echo json_encode(['success' => false, 'message' => 'Error al agregar: ' . $e->getMessage()]);
        }
    }
    // EDITAR PRODUCTO
    public function editProduct($id, $name, $category_id, $description, $barcode, $stock, $price, $supplier_id = null) {
        try {
            $conn = $this->db->getConnection();
            $stmt = $conn->prepare("UPDATE products SET name=?, category_id=?, description=?, barcode=?, stock=?, price=?, supplier_id=? WHERE id=?");
            $stmt->bind_param('sissidii', $name, $category_id, $description, $barcode, $stock, $price, $supplier_id, $id);
            $stmt->execute();
            $stmt->close();
            echo json_encode(['success' => true, 'message' => 'Producto actualizado']);
        } catch (Exception $e) {
            echo json_encode(['success' => false, 'message' => 'Error al editar: ' . $e->getMessage()]);
        }
    }
    // ELIMINAR PRODUCTO
    public function deleteProduct($id) {
        try {
            $conn = $this->db->getConnection();
            $stmt = $conn->prepare("UPDATE products SET is_active=0 WHERE id=?");
            $stmt->bind_param('i', $id);
            $stmt->execute();
            $stmt->close();
            echo json_encode(['success' => true, 'message' => 'Producto eliminado']);
        } catch (Exception $e) {
            echo json_encode(['success' => false, 'message' => 'Error al eliminar: ' . $e->getMessage()]);
        }
    }
}

// Manejo de rutas
session_start();

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $controller = new ProductsController();
    
    if (isset($_GET['action'])) {
        switch ($_GET['action']) {
            case 'list':
                $controller->getProducts();
                break;
            case 'categories':
                $controller->getCategories();
                break;
            case 'suppliers':
                $controller->getSuppliers();
                break;
            case 'search':
                if (isset($_GET['q'])) {
                    $controller->searchProducts($_GET['q']);
                } else {
                    echo json_encode(['success' => false, 'message' => 'Término de búsqueda requerido']);
                }
                break;
            case 'by_category':
                if (isset($_GET['category_id'])) {
                    $controller->getProductsByCategory($_GET['category_id']);
                } else {
                    echo json_encode(['success' => false, 'message' => 'ID de categoría requerido']);
                }
                break;
            default:
                $controller->getProducts();
        }
    } else {
        $controller->getProducts();
    }
} elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $controller = new ProductsController();
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (isset($input['action'])) {
        switch ($input['action']) {
            case 'add':
                $controller->addProduct(
                    $input['name'],
                    $input['category_id'],
                    $input['description'],
                    $input['barcode'],
                    $input['stock'],
                    $input['price'],
                    $input['supplier_id'] ?? null
                );
                break;
            case 'edit':
                $controller->editProduct(
                    $input['id'],
                    $input['name'],
                    $input['category_id'],
                    $input['description'],
                    $input['barcode'],
                    $input['stock'],
                    $input['price'],
                    $input['supplier_id'] ?? null
                );
                break;
            case 'delete':
                $controller->deleteProduct($input['id']);
                break;
            case 'update_stock':
                if (isset($input['product_id']) && isset($input['quantity'])) {
                    $controller->updateStock($input['product_id'], $input['quantity']);
                } else {
                    echo json_encode(['success' => false, 'message' => 'Datos incompletos']);
                }
                break;
            default:
                echo json_encode(['success' => false, 'message' => 'Acción no válida']);
        }
    } else {
        echo json_encode(['success' => false, 'message' => 'Acción requerida']);
    }
} else {
    echo json_encode([
        'success' => false,
        'message' => 'Método no permitido'
    ]);
}
?>
