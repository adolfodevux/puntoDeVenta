<?php
session_start();
require_once __DIR__ . '/../../Controllers/SuppliersController.php';

// Verificar autenticación
if (!isset($_SESSION['logged_in']) || !$_SESSION['logged_in']) {
    header('HTTP/1.1 401 Unauthorized');
    exit(json_encode(['success' => false, 'message' => 'No autorizado']));
}

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['id'])) {
    $controller = new SuppliersController();
    $result = $controller->delete($_POST['id']);
    
    if (isset($result['success'])) {
        echo json_encode([
            'success' => true,
            'message' => $result['success']
        ]);
    } else {
        echo json_encode([
            'success' => false,
            'message' => $result['error'] ?? 'Error al eliminar el proveedor'
        ]);
    }
} else {
    echo json_encode([
        'success' => false,
        'message' => 'Parámetros inválidos'
    ]);
}
?>
