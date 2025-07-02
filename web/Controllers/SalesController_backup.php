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
    
    public function processSale() {
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
                $saleQuery = "INSERT INTO sales (user_id, total_amount, subtotal, tax_amount, payment_method, amount_paid, change_amount, sale_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), 'completed')";
                $saleStmt = $conn->prepare($saleQuery);
                
                $userId = $_SESSION['user_id'] ?? 1; // Usar ID de sesión o 1 por defecto
                $saleStmt->bind_param('idddsdd', 
                    $userId,
                    $input['total'],
                    $input['subtotal'],
                    $input['tax'],
                    $input['paymentMethod'],
                    $input['amountPaid'],
                    $input['change']
                );
                
                if (!$saleStmt->execute()) {
                    throw new Exception('Error al insertar venta');
                }
                
                $saleId = $conn->insert_id;
                $saleStmt->close();
                
                // Insertar items de la venta
                $itemQuery = "INSERT INTO sale_items (sale_id, product_id, product_name, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?, ?)";
                $itemStmt = $conn->prepare($itemQuery);
                
                foreach ($input['items'] as $item) {
                    $totalPrice = $item['price'] * $item['quantity'];
                    $itemStmt->bind_param('iisidd',
                        $saleId,
                        $item['id'],
                        $item['name'],
                        $item['quantity'],
                        $item['price'],
                        $totalPrice
                    );
                    
                    if (!$itemStmt->execute()) {
                        throw new Exception('Error al insertar item de venta');
                    }
                }
                
                $itemStmt->close();
                
                // Confirmar transacción
                $conn->commit();
                $conn->autocommit(true);
                
                // Respuesta exitosa
                echo json_encode([
                    'success' => true,
                    'message' => 'Venta procesada exitosamente',
                    'sale_id' => $saleId,
                    'data' => $input
                ]);
                
            } catch (Exception $e) {
                // Revertir transacción en caso de error
                $conn->rollback();
                $conn->autocommit(true);
                throw $e;
            }
            
        } catch (Exception $e) {
            echo json_encode([
                'success' => false,
                'message' => 'Error al procesar venta: ' . $e->getMessage()
            ]);
        }
    }
    
    public function getSales() {
        try {
            $conn = $this->db->getConnection();
            
            $query = "SELECT s.*, u.username FROM sales s LEFT JOIN users u ON s.user_id = u.id ORDER BY s.sale_date DESC LIMIT 50";
            $result = $conn->query($query);
            
            if (!$result) {
                throw new Exception('Error al ejecutar consulta');
            }
            
            $sales = [];
            while ($row = $result->fetch_assoc()) {
                $sales[] = $row;
            }
            
            echo json_encode([
                'success' => true,
                'data' => $sales
            ]);
            
        } catch (Exception $e) {
            echo json_encode([
                'success' => false,
                'message' => 'Error al obtener ventas: ' . $e->getMessage()
            ]);
        }
    }
    
    public function getSaleDetails($saleId) {
        try {
            $conn = $this->db->getConnection();
            
            // Obtener datos de la venta
            $saleQuery = "SELECT s.*, u.username FROM sales s LEFT JOIN users u ON s.user_id = u.id WHERE s.id = ?";
            $saleStmt = $conn->prepare($saleQuery);
            $saleStmt->bind_param('i', $saleId);
            $saleStmt->execute();
            $saleResult = $saleStmt->get_result();
            $sale = $saleResult->fetch_assoc();
            $saleStmt->close();
            
            if (!$sale) {
                throw new Exception('Venta no encontrada');
            }
            
            // Obtener items de la venta
            $itemsQuery = "SELECT * FROM sale_items WHERE sale_id = ?";
            $itemsStmt = $conn->prepare($itemsQuery);
            $itemsStmt->bind_param('i', $saleId);
            $itemsStmt->execute();
            $itemsResult = $itemsStmt->get_result();
            
            $items = [];
            while ($row = $itemsResult->fetch_assoc()) {
                $items[] = $row;
            }
            $itemsStmt->close();
            
            $sale['items'] = $items;
            
            echo json_encode([
                'success' => true,
                'data' => $sale
            ]);
            
        } catch (Exception $e) {
            echo json_encode([
                'success' => false,
                'message' => 'Error al obtener detalles de venta: ' . $e->getMessage()
            ]);
        }
    }
    
    public function index() {
        $sales = $this->salesModel->getAllSales();
        if (empty($sales)) {
            include '../Views/dashboard/no_sales.php';
        } else {
            include '../Views/dashboard/sales.php';
        }
    }

    public function create($data) {
        $result = $this->salesModel->addSale($data);
        if ($result) {
            header('Location: /dashboard/sales');
        } else {
            echo "Error creating sale.";
        }
    }

    public function delete($id) {
        $result = $this->salesModel->deleteSale($id);
        if ($result) {
            header('Location: /dashboard/sales');
        } else {
            echo "Error deleting sale.";
        }
    }
}

// Manejo de rutas
session_start();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $controller = new SalesController();
    $controller->processSale();
} elseif ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $controller = new SalesController();
    
    if (isset($_GET['action'])) {
        switch ($_GET['action']) {
            case 'list':
                $controller->getSales();
                break;
            case 'details':
                if (isset($_GET['id'])) {
                    $controller->getSaleDetails($_GET['id']);
                } else {
                    echo json_encode(['success' => false, 'message' => 'ID de venta requerido']);
                }
                break;
            default:
                echo json_encode(['success' => false, 'message' => 'Acción no válida']);
        }
    } else {
        $controller->getSales();
    }
} else {
    echo json_encode([
        'success' => false,
        'message' => 'Método no permitido'
    ]);
}
?>
