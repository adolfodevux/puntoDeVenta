<?php
session_start();
header('Content-Type: application/json');
$data = json_decode(file_get_contents('php://input'), true);
if (!isset($data['clienteId']) || !isset($data['compra'])) {
    echo json_encode(['success' => false, 'error' => 'Datos incompletos']);
    exit;
}
$clienteId = intval($data['clienteId']);
$compra = $data['compra'];
if (!isset($_SESSION['compras_clientes'][$clienteId])) {
    $_SESSION['compras_clientes'][$clienteId] = [];
}
$_SESSION['compras_clientes'][$clienteId][] = $compra;
echo json_encode(['success' => true]);
