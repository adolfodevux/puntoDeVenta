<?php
session_start();
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

// Verificar que el usuario esté logueado
if (!isset($_SESSION['logged_in']) || !$_SESSION['logged_in']) {
    echo json_encode(['success' => false, 'message' => 'Usuario no autenticado']);
    exit();
}

require_once '../../Models/database.php';

try {
    $db = Database::getInstance()->getConnection();
    
    // Obtener datos de la venta desde el POST
    $raw_input = file_get_contents('php://input');
    $input = json_decode($raw_input, true);
    
    if (!$input) {
        echo json_encode([
            'success' => false, 
            'message' => 'Datos de venta no válidos - JSON inválido'
        ]);
        exit();
    }
    
    $user_id = $_SESSION['user_id'];
    $cliente_id = isset($input['clienteId']) && $input['clienteId'] ? (int)$input['clienteId'] : null;
    $cliente_nombre = isset($input['clienteNombre']) && $input['clienteNombre'] ? $input['clienteNombre'] : 'null';
    $compra = $input['compra'] ?? null;
    
    // Validar datos requeridos
    if (!isset($compra['total']) || !isset($compra['productos']) || !is_array($compra['productos'])) {
        echo json_encode([
            'success' => false, 
            'message' => 'Datos de compra incompletos - faltan campos requeridos'
        ]);
        exit();
    }
    
    // Validar que hay productos en el carrito
    if (empty($compra['productos'])) {
        echo json_encode(['success' => false, 'message' => 'No hay productos en el carrito']);
        exit();
    }
    
    // Validar que el total sea válido
    $total = (float)$compra['total'];
    if ($total <= 0) {
        echo json_encode(['success' => false, 'message' => 'Total de venta inválido']);
        exit();
    }
    
    // Iniciar transacción
    $db->begin_transaction();
    
    try {
        // Calcular valores
        $total = (float)$compra['total'];
        $subtotal = $total / 1.16; // Restar IVA del 16%
        $tax_amount = $total - $subtotal;
        $payment_method = isset($compra['metodo_pago']) ? $compra['metodo_pago'] : 'cash';
        $amount_paid = isset($compra['monto_recibido']) ? (float)$compra['monto_recibido'] : $total;
        $change_amount = isset($compra['cambio']) ? (float)$compra['cambio'] : 0;
        
        // Insertar venta principal con nombre de cliente
        $sql_sale = "INSERT INTO sales (user_id, cliente_id, cliente_nombre, total_amount, subtotal, tax_amount, payment_method, amount_paid, change_amount) 
                     VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        $stmt_sale = $db->prepare($sql_sale);
        $stmt_sale->bind_param('iisdddsdd', $user_id, $cliente_id, $cliente_nombre, $total, $subtotal, $tax_amount, $payment_method, $amount_paid, $change_amount);
        
        if (!$stmt_sale->execute()) {
            throw new Exception('Error al insertar la venta: ' . $stmt_sale->error);
        }
        
        $sale_id = $db->insert_id;
        
        // Insertar items de la venta y actualizar stock de productos
        $sql_item = "INSERT INTO sale_items (sale_id, product_id, product_name, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?, ?)";
        $stmt_item = $db->prepare($sql_item);
        
        // Preparar consulta para actualizar stock
        $sql_update_stock = "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?";
        $stmt_update_stock = $db->prepare($sql_update_stock);
        
        foreach ($compra['productos'] as $producto) {
            $product_id = isset($producto['id']) ? (int)$producto['id'] : null;
            $product_name = $producto['nombre'];
            $quantity = (int)$producto['cantidad'];
            $unit_price = (float)$producto['precio'];
            $total_price = $quantity * $unit_price;
            
            // Insertar item de venta
            $stmt_item->bind_param('iisidd', $sale_id, $product_id, $product_name, $quantity, $unit_price, $total_price);
            
            if (!$stmt_item->execute()) {
                throw new Exception('Error al insertar item de venta: ' . $stmt_item->error);
            }
            
            // Descontar stock del producto si tiene ID (producto de la base de datos)
            if ($product_id) {
                $stmt_update_stock->bind_param('iii', $quantity, $product_id, $quantity);
                
                if (!$stmt_update_stock->execute()) {
                    throw new Exception('Error al actualizar stock del producto: ' . $stmt_update_stock->error);
                }
                
                // Verificar que se haya actualizado al menos una fila (que había stock suficiente)
                if ($stmt_update_stock->affected_rows === 0) {
                    throw new Exception("Stock insuficiente para el producto: {$product_name}");
                }
            }
        }
        
        // Confirmar transacción
        $db->commit();
        
        // Respuesta exitosa
        echo json_encode([
            'success' => true, 
            'message' => 'Venta registrada exitosamente',
            'sale_id' => $sale_id,
            'total' => $total,
            'cliente_id' => $cliente_id,
            'cliente_nombre' => $cliente_nombre
        ]);
        
    } catch (Exception $e) {
        // Revertir transacción en caso de error
        $db->rollback();
        throw $e;
    }
    
} catch (Exception $e) {
    echo json_encode([
        'success' => false, 
        'message' => 'Error al procesar la venta: ' . $e->getMessage()
    ]);
}
?>
