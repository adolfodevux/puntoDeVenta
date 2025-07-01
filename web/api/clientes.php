<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE');
header('Access-Control-Allow-Headers: Content-Type');

require_once '../Models/database.php';
require_once '../Models/Cliente.php';

try {
    $clienteModel = new Cliente();
    $method = $_SERVER['REQUEST_METHOD'];
    
    switch ($method) {
        case 'GET':
            if (isset($_GET['search'])) {
                // Búsqueda de clientes por nombre o ID
                $search = $_GET['search'];
                $limit = isset($_GET['limit']) ? (int)$_GET['limit'] : 10; // Límite por defecto: 10
                
                $db = Database::getInstance()->getConnection();
                
                // Consulta que busca por ID o nombre
                $sql = "SELECT id, nombre, telefono FROM clientes 
                       WHERE id LIKE ? OR nombre LIKE ? 
                       ORDER BY nombre ASC 
                       LIMIT ?";
                
                $stmt = $db->prepare($sql);
                $searchParam = "%$search%";
                $stmt->bind_param('ssi', $searchParam, $searchParam, $limit);
                $stmt->execute();
                $result = $stmt->get_result();
                
                $clientes = [];
                while ($row = $result->fetch_assoc()) {
                    $clientes[] = $row;
                }
                
                echo json_encode([
                    'success' => true,
                    'data' => $clientes,
                    'count' => count($clientes)
                ]);
                
            } elseif (isset($_GET['id'])) {
                // Obtener cliente específico por ID
                $cliente = $clienteModel->obtenerPorId($_GET['id']);
                if ($cliente) {
                    echo json_encode(['success' => true, 'data' => $cliente]);
                } else {
                    echo json_encode(['success' => false, 'message' => 'Cliente no encontrado']);
                }
                
            } else {
                // Obtener todos los clientes (con límite)
                $limit = isset($_GET['limit']) ? (int)$_GET['limit'] : 20;
                $offset = isset($_GET['offset']) ? (int)$_GET['offset'] : 0;
                
                $db = Database::getInstance()->getConnection();
                $sql = "SELECT id, nombre, telefono FROM clientes ORDER BY nombre ASC LIMIT ? OFFSET ?";
                $stmt = $db->prepare($sql);
                $stmt->bind_param('ii', $limit, $offset);
                $stmt->execute();
                $result = $stmt->get_result();
                
                $clientes = [];
                while ($row = $result->fetch_assoc()) {
                    $clientes[] = $row;
                }
                
                echo json_encode([
                    'success' => true,
                    'data' => $clientes,
                    'count' => count($clientes)
                ]);
            }
            break;
            
        case 'POST':
            // Crear nuevo cliente
            $data = json_decode(file_get_contents('php://input'), true);
            
            if (!isset($data['nombre']) || !isset($data['telefono'])) {
                echo json_encode(['success' => false, 'message' => 'Datos incompletos']);
                break;
            }
            
            $resultado = $clienteModel->crear($data['nombre'], $data['telefono']);
            
            if ($resultado) {
                echo json_encode(['success' => true, 'message' => 'Cliente creado exitosamente']);
            } else {
                echo json_encode(['success' => false, 'message' => 'Error al crear cliente']);
            }
            break;
            
        default:
            echo json_encode(['success' => false, 'message' => 'Método no permitido']);
            break;
    }
    
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Error interno del servidor: ' . $e->getMessage()
    ]);
}
?>