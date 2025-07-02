<?php
require_once __DIR__ . '/../Models/Category.php';

class CategoriesController {
    private $categoryModel;

    public function __construct() {
        $this->categoryModel = new Category();
    }

    public function getAllCategories() {
        try {
            $categories = $this->categoryModel->getCategoriesWithProductCount();
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

    public function getCategoryById($id) {
        try {
            $category = $this->categoryModel->getCategoryById($id);
            if ($category) {
                echo json_encode([
                    'success' => true,
                    'data' => $category
                ]);
            } else {
                echo json_encode([
                    'success' => false,
                    'message' => 'Categoría no encontrada'
                ]);
            }
        } catch (Exception $e) {
            echo json_encode([
                'success' => false,
                'message' => 'Error al obtener categoría: ' . $e->getMessage()
            ]);
        }
    }

    public function createCategory() {
        try {
            $input = json_decode(file_get_contents('php://input'), true);
            
            if (!isset($input['name']) || empty(trim($input['name']))) {
                throw new Exception('El nombre de la categoría es requerido');
            }

            $name = trim($input['name']);
            $description = trim($input['description'] ?? '');

            $result = $this->categoryModel->createCategory($name, $description);
            
            if ($result) {
                echo json_encode([
                    'success' => true,
                    'message' => 'Categoría creada exitosamente'
                ]);
            } else {
                throw new Exception('Error al crear la categoría');
            }
        } catch (Exception $e) {
            echo json_encode([
                'success' => false,
                'message' => $e->getMessage()
            ]);
        }
    }

    public function updateCategory($id) {
        try {
            $input = json_decode(file_get_contents('php://input'), true);
            
            if (!isset($input['name']) || empty(trim($input['name']))) {
                throw new Exception('El nombre de la categoría es requerido');
            }

            $name = trim($input['name']);
            $description = trim($input['description'] ?? '');

            $result = $this->categoryModel->updateCategory($id, $name, $description);
            
            if ($result) {
                echo json_encode([
                    'success' => true,
                    'message' => 'Categoría actualizada exitosamente'
                ]);
            } else {
                throw new Exception('Error al actualizar la categoría');
            }
        } catch (Exception $e) {
            echo json_encode([
                'success' => false,
                'message' => $e->getMessage()
            ]);
        }
    }

    public function deleteCategory($id) {
        try {
            $result = $this->categoryModel->deleteCategory($id);
            
            if ($result) {
                echo json_encode([
                    'success' => true,
                    'message' => 'Categoría eliminada exitosamente'
                ]);
            } else {
                throw new Exception('Error al eliminar la categoría');
            }
        } catch (Exception $e) {
            echo json_encode([
                'success' => false,
                'message' => $e->getMessage()
            ]);
        }
    }

    public function searchCategories() {
        try {
            $searchTerm = $_GET['search'] ?? '';
            
            if (empty(trim($searchTerm))) {
                $categories = $this->categoryModel->getCategoriesWithProductCount();
            } else {
                $categories = $this->categoryModel->searchCategories($searchTerm);
            }
            
            echo json_encode([
                'success' => true,
                'data' => $categories
            ]);
        } catch (Exception $e) {
            echo json_encode([
                'success' => false,
                'message' => 'Error al buscar categorías: ' . $e->getMessage()
            ]);
        }
    }
}

// Manejo de rutas
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $controller = new CategoriesController();
    
    if (isset($_GET['action'])) {
        switch ($_GET['action']) {
            case 'getAll':
                $controller->getAllCategories();
                break;
            case 'getById':
                if (isset($_GET['id'])) {
                    $controller->getCategoryById($_GET['id']);
                } else {
                    echo json_encode(['success' => false, 'message' => 'ID requerido']);
                }
                break;
            case 'search':
                $controller->searchCategories();
                break;
            default:
                echo json_encode(['success' => false, 'message' => 'Acción no válida']);
        }
    } else {
        $controller->getAllCategories();
    }
} elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $controller = new CategoriesController();
    $controller->createCategory();
} elseif ($_SERVER['REQUEST_METHOD'] === 'PUT') {
    $controller = new CategoriesController();
    
    if (isset($_GET['id'])) {
        $controller->updateCategory($_GET['id']);
    } else {
        echo json_encode(['success' => false, 'message' => 'ID requerido']);
    }
} elseif ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
    $controller = new CategoriesController();
    
    if (isset($_GET['id'])) {
        $controller->deleteCategory($_GET['id']);
    } else {
        echo json_encode(['success' => false, 'message' => 'ID requerido']);
    }
} else {
    echo json_encode(['success' => false, 'message' => 'Método no permitido']);
}
?>
