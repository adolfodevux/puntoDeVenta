<?php
session_start();
require_once __DIR__ . '/../../Controllers/SuppliersController.php';

// Verificar autenticación
if (!isset($_SESSION['logged_in']) || !$_SESSION['logged_in']) {
    header('HTTP/1.1 401 Unauthorized');
    exit(json_encode(['success' => false, 'message' => 'No autorizado']));
}

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['search_term'])) {
    $controller = new SuppliersController();
    $result = $controller->search($_POST['search_term']);
    
    echo json_encode([
        'success' => true,
        'suppliers' => $result['suppliers']
    ]);
} else {
    echo json_encode([
        'success' => false,
        'message' => 'Parámetros inválidos'
    ]);
}
?>
