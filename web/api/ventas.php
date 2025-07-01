<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE');
header('Access-Control-Allow-Headers: Content-Type');

require_once '../Models/database.php';

try {
    $db = Database::getInstance()->getConnection();
    $method = $_SERVER['REQUEST_METHOD'];
    
    switch ($method) {
        case 'GET':
            if (isset($_GET['id'])) {
                // Obtener venta específica con sus items
                $sale_id = (int)$_GET['id'];
                
                // Consulta de la venta
                $sql_sale = "SELECT s.*, c.nombre as cliente_nombre, c.telefono as cliente_telefono, u.username 
                            FROM sales s 
                            LEFT JOIN clientes c ON s.cliente_id = c.id 
                            LEFT JOIN users u ON s.user_id = u.id 
                            WHERE s.id = ?";
                
                $stmt = $db->prepare($sql_sale);
                $stmt->bind_param('i', $sale_id);
                $stmt->execute();
                $sale = $stmt->get_result()->fetch_assoc();
                
                if (!$sale) {
                    echo json_encode(['success' => false, 'message' => 'Venta no encontrada']);
                    break;
                }
                
                // Consulta de los items de la venta
                $sql_items = "SELECT * FROM sale_items WHERE sale_id = ?";
                $stmt_items = $db->prepare($sql_items);
                $stmt_items->bind_param('i', $sale_id);
                $stmt_items->execute();
                $items_result = $stmt_items->get_result();
                
                $items = [];
                while ($item = $items_result->fetch_assoc()) {
                    $items[] = $item;
                }
                
                $sale['items'] = $items;
                
                echo json_encode(['success' => true, 'data' => $sale]);
                
            } else {
                // Obtener todas las ventas con paginación
                $limit = isset($_GET['limit']) ? (int)$_GET['limit'] : 20;
                $offset = isset($_GET['offset']) ? (int)$_GET['offset'] : 0;
                $fecha_inicio = isset($_GET['fecha_inicio']) ? $_GET['fecha_inicio'] : null;
                $fecha_fin = isset($_GET['fecha_fin']) ? $_GET['fecha_fin'] : null;
                
                $where_clause = "";
                $params = [];
                $param_types = "";
                
                if ($fecha_inicio && $fecha_fin) {
                    $where_clause = " WHERE DATE(s.sale_date) BETWEEN ? AND ?";
                    $params[] = $fecha_inicio;
                    $params[] = $fecha_fin;
                    $param_types .= "ss";
                }
                
                $sql = "SELECT s.id, s.total_amount, s.payment_method, s.sale_date, s.status, 
                              c.nombre as cliente_nombre, u.username,
                              (SELECT COUNT(*) FROM sale_items si WHERE si.sale_id = s.id) as total_items
                        FROM sales s 
                        LEFT JOIN clientes c ON s.cliente_id = c.id 
                        LEFT JOIN users u ON s.user_id = u.id 
                        $where_clause
                        ORDER BY s.sale_date DESC 
                        LIMIT ? OFFSET ?";
                
                $params[] = $limit;
                $params[] = $offset;
                $param_types .= "ii";
                
                $stmt = $db->prepare($sql);
                
                if (!empty($params)) {
                    $stmt->bind_param($param_types, ...$params);
                }
                
                $stmt->execute();
                $result = $stmt->get_result();
                
                $ventas = [];
                while ($row = $result->fetch_assoc()) {
                    $ventas[] = $row;
                }
                
                // Contar total de ventas
                $count_sql = "SELECT COUNT(*) as total FROM sales s $where_clause";
                $count_stmt = $db->prepare($count_sql);
                
                if ($fecha_inicio && $fecha_fin) {
                    $count_stmt->bind_param("ss", $fecha_inicio, $fecha_fin);
                }
                
                $count_stmt->execute();
                $total_count = $count_stmt->get_result()->fetch_assoc()['total'];
                
                echo json_encode([
                    'success' => true,
                    'data' => $ventas,
                    'total' => $total_count,
                    'limit' => $limit,
                    'offset' => $offset
                ]);
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
