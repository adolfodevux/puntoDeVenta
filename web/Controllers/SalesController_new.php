<?php
// Controlador para manejo de ventas
require_once '../Models/database.php';
require_once '../Models/Sales.php';

class SalesController {
    private $db;
    private $salesModel;
    
    public function __construct() {
        $this->db = Database::getInstance();
        $this->salesModel = new Sales();
    }
    
    public function index() {
        $sales = $this->salesModel->getAllSales();
        $stats = $this->salesModel->getSalesStats();
        include __DIR__ . '/../Views/sales/index.php';
    }
    
    public function getStats() {
        header('Content-Type: application/json');
        $stats = $this->salesModel->getSalesStats();
        echo json_encode(['success' => true, 'data' => $stats]);
    }
    
    public function filter() {
        $paymentMethod = $_GET['payment_method'] ?? '';
        $startDate = $_GET['start_date'] ?? '';
        $endDate = $_GET['end_date'] ?? '';
        
        if ($paymentMethod && $paymentMethod !== 'todos') {
            $sales = $this->salesModel->getSalesByPaymentMethod($paymentMethod);
        } elseif ($startDate && $endDate) {
            $sales = $this->salesModel->getSalesByDateRange($startDate, $endDate);
        } else {
            $sales = $this->salesModel->getAllSales();
        }
        
        $stats = $this->salesModel->getSalesStats();
        include __DIR__ . '/../Views/sales/index.php';
    }
    
    public function details() {
        $id = $_GET['id'] ?? null;
        if (!$id) {
            header('Location: ?action=sales');
            exit;
        }
        
        $sale = $this->salesModel->getSaleDetails($id);
        if (!$sale) {
            header('Location: ?action=sales');
            exit;
        }
        
        include __DIR__ . '/../Views/sales/details.php';
    }
    
    public function delete() {
        $id = $_GET['id'] ?? null;
        if ($id) {
            $this->salesModel->deleteSale($id);
        }
        header('Location: ?action=sales');
        exit;
    }
    
    public function processSale() {
        header('Content-Type: application/json');
        
        try {
            // Obtener datos de la venta del POST
            $input = json_decode(file_get_contents('php://input'), true);
            
            if (!$input) {
                throw new Exception('No se recibieron datos válidos');
            }
            
            // Validar datos requeridos
            if (!isset($input['items']) || empty($input['items']) || 
                !isset($input['total']) || !isset($input['paymentMethod'])) {
                throw new Exception('Datos de venta incompletos');
            }
            
            $conn = $this->db->getConnection();
            
            // Comenzar transacción
            $conn->autocommit(false);
            
            try {
                // Insertar venta principal
                $saleQuery = "INSERT INTO sales (user_id, cliente_id, total_amount, subtotal, tax_amount, payment_method, amount_paid, change_amount, sale_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), 'completed')";
                $saleStmt = $conn->prepare($saleQuery);
                
                $userId = $_SESSION['user_id'] ?? 1; // Usar ID de sesión o 1 por defecto
                $clienteId = !empty($input['clienteId']) ? $input['clienteId'] : null;
                
                $saleStmt->bind_param('iidddsdd', 
                    $userId,
                    $clienteId,
                    $input['total'],
                    $input['subtotal'],
                    $input['tax'],
                    $input['paymentMethod'],
                    $input['amountPaid'],
                    $input['change']
                );
                
                if (!$saleStmt->execute()) {
                    throw new Exception('Error al insertar venta: ' . $saleStmt->error);
                }
                
                $saleId = $conn->insert_id;
                
                // Insertar items de la venta
                $itemQuery = "INSERT INTO sale_items (sale_id, product_id, product_name, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?, ?)";
                $itemStmt = $conn->prepare($itemQuery);
                
                foreach ($input['items'] as $item) {
                    $itemStmt->bind_param('iisidd',
                        $saleId,
                        $item['id'],
                        $item['name'],
                        $item['quantity'],
                        $item['price'],
                        $item['quantity'] * $item['price']
                    );
                    
                    if (!$itemStmt->execute()) {
                        throw new Exception('Error al insertar item de venta: ' . $itemStmt->error);
                    }
                    
                    // Actualizar stock del producto
                    $updateStockQuery = "UPDATE products SET stock = stock - ? WHERE id = ?";
                    $updateStockStmt = $conn->prepare($updateStockQuery);
                    $updateStockStmt->bind_param('ii', $item['quantity'], $item['id']);
                    
                    if (!$updateStockStmt->execute()) {
                        throw new Exception('Error al actualizar stock: ' . $updateStockStmt->error);
                    }
                }
                
                // Confirmar transacción
                $conn->commit();
                
                echo json_encode([
                    'success' => true,
                    'message' => 'Venta procesada exitosamente',
                    'saleId' => $saleId
                ]);
                
            } catch (Exception $e) {
                // Rollback en caso de error
                $conn->rollback();
                throw $e;
            }
            
        } catch (Exception $e) {
            echo json_encode([
                'success' => false,
                'message' => 'Error al procesar venta: ' . $e->getMessage()
            ]);
        }
    }
}

// Manejo de rutas básico
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    session_start();
    $controller = new SalesController();
    $controller->processSale();
} elseif ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $action = $_GET['action'] ?? 'stats';
    $controller = new SalesController();
    
    switch ($action) {
        case 'stats':
            $controller->getStats();
            break;
        case 'details':
            $controller->details();
            break;
        default:
            $controller->getStats();
            break;
    }
}
?>
