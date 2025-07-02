<?php
session_start();

// Simular usuario logueado para debug
$_SESSION['user_id'] = 1;

require_once 'Models/database.php';
require_once 'Models/Sales.php';

echo "<h2>Debug de Ventas y Comprobantes</h2>";

try {
    $salesModel = new Sales();
    
    // Obtener las últimas 5 ventas
    echo "<h3>Últimas ventas en la base de datos:</h3>";
    $db = Database::getInstance()->getConnection();
    $result = $db->query('SELECT id, total_amount, sale_date FROM sales ORDER BY id DESC LIMIT 5');
    
    echo "<table border='1' style='border-collapse: collapse; margin-bottom: 20px;'>";
    echo "<tr><th>ID</th><th>Total</th><th>Fecha</th><th>Acciones</th></tr>";
    
    while ($row = $result->fetch_assoc()) {
        echo "<tr>";
        echo "<td>" . $row['id'] . "</td>";
        echo "<td>$" . number_format($row['total_amount'], 2) . "</td>";
        echo "<td>" . $row['sale_date'] . "</td>";
        echo "<td><a href='?test_sale=" . $row['id'] . "'>Probar</a></td>";
        echo "</tr>";
    }
    echo "</table>";
    
    // Si se selecciona una venta para probar
    if (isset($_GET['test_sale'])) {
        $saleId = intval($_GET['test_sale']);
        echo "<h3>Probando venta ID: $saleId</h3>";
        
        $saleDetails = $salesModel->getSaleDetails($saleId);
        
        if ($saleDetails) {
            echo "<h4>Datos de la venta:</h4>";
            echo "<pre style='background: #f5f5f5; padding: 10px; overflow: auto;'>";
            print_r($saleDetails);
            echo "</pre>";
            
            echo "<h4>Items de la venta:</h4>";
            if (!empty($saleDetails['items'])) {
                echo "<table border='1' style='border-collapse: collapse;'>";
                echo "<tr><th>Producto</th><th>Cantidad</th><th>Precio</th><th>Total</th></tr>";
                foreach ($saleDetails['items'] as $item) {
                    echo "<tr>";
                    echo "<td>" . htmlspecialchars($item['product_name'] ?? 'N/A') . "</td>";
                    echo "<td>" . ($item['quantity'] ?? 0) . "</td>";
                    echo "<td>$" . number_format($item['price'] ?? 0, 2) . "</td>";
                    echo "<td>$" . number_format($item['total_price'] ?? 0, 2) . "</td>";
                    echo "</tr>";
                }
                echo "</table>";
            } else {
                echo "<p>No hay items en esta venta</p>";
            }
            
            echo "<h4>Enlaces de prueba:</h4>";
            echo "<p><a href='Views/sales/details.php?id=$saleId' target='_blank'>Ver Detalles</a></p>";
            echo "<p><a href='Views/sales/comprobante.php?id=$saleId' target='_blank'>Ver Comprobante</a></p>";
            
        } else {
            echo "<p>No se encontró la venta con ID $saleId</p>";
        }
    }
    
} catch (Exception $e) {
    echo "<p style='color: red;'>Error: " . $e->getMessage() . "</p>";
}
?>
