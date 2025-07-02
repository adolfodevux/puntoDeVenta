<?php
// Vista de detalles de venta
session_start();
if (!isset($_SESSION['user_id'])) {
    header('Location: ../auth/login.php');
    exit;
}

require_once __DIR__ . '/../../Models/database.php';
require_once __DIR__ . '/../../Models/Sales.php';

$saleId = $_GET['id'] ?? null;
if (!$saleId) {
    header('Location: index.php');
    exit;
}

$salesModel = new Sales();
$sale = $salesModel->getSaleDetails($saleId);

if (!$sale) {
    header('Location: index.php');
    exit;
}
?>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Detalles de Venta #<?= $sale['id'] ?> - POS</title>
    <link rel="stylesheet" href="../../Assets/css/dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            font-family: 'Inter', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f7fa;
            margin: 0;
            padding: 0;
        }
        
        .details-container {
            max-width: 900px;
            margin: 0 auto;
            padding: 2rem;
        }
        
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
        }
        
        .page-title {
            display: flex;
            align-items: center;
            gap: 1rem;
            color: #2c3e50;
        }
        
        .page-title h1 {
            font-size: 1.8rem;
            font-weight: 700;
            margin: 0;
        }
        
        .btn-back {
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            background: #3498db;
            color: white;
            padding: 0.75rem 1.5rem;
            border-radius: 8px;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        
        .btn-back:hover {
            background: #2980b9;
            transform: translateY(-2px);
        }
        
        .sale-info-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 2rem;
            margin-bottom: 2rem;
        }
        
        .info-card {
            background: white;
            border-radius: 12px;
            padding: 1.5rem;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
        
        .info-card h3 {
            color: #2c3e50;
            margin-top: 0;
            margin-bottom: 1rem;
            font-size: 1.2rem;
            border-bottom: 2px solid #3498db;
            padding-bottom: 0.5rem;
        }
        
        .info-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.75rem;
            padding: 0.5rem 0;
            border-bottom: 1px solid #f1f2f6;
        }
        
        .info-label {
            font-weight: 600;
            color: #7f8c8d;
        }
        
        .info-value {
            color: #2c3e50;
            font-weight: 500;
        }
        
        .status-badge {
            padding: 0.25rem 0.75rem;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 600;
            text-transform: uppercase;
        }
        
        .status-completed {
            background: #d4edda;
            color: #155724;
        }
        
        .payment-method {
            padding: 0.25rem 0.75rem;
            border-radius: 6px;
            font-size: 0.8rem;
            font-weight: 500;
        }
        
        .payment-cash {
            background: #e8f5e8;
            color: #2e7d32;
        }
        
        .payment-efectivo {
            background: #e8f5e8;
            color: #2e7d32;
        }
        
        .items-section {
            background: white;
            border-radius: 12px;
            padding: 1.5rem;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
        
        .items-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
        }
        
        .items-table th {
            background: #f8f9fa;
            color: #495057;
            font-weight: 600;
            padding: 1rem;
            text-align: left;
            border-bottom: 2px solid #dee2e6;
        }
        
        .items-table td {
            padding: 1rem;
            border-bottom: 1px solid #dee2e6;
        }
        
        .total-section {
            background: #f8f9fa;
            border-radius: 8px;
            padding: 1rem;
            margin-top: 1rem;
        }
        
        .total-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.5rem;
        }
        
        .total-row.final {
            font-size: 1.2rem;
            font-weight: 700;
            color: #27ae60;
            border-top: 2px solid #dee2e6;
            padding-top: 0.5rem;
            margin-top: 0.5rem;
        }
        
        @media (max-width: 768px) {
            .details-container {
                padding: 1rem;
            }
            
            .page-header {
                flex-direction: column;
                gap: 1rem;
                align-items: stretch;
            }
            
            .sale-info-grid {
                grid-template-columns: 1fr;
                gap: 1rem;
            }
            
            .items-table {
                font-size: 0.8rem;
            }
        }
    </style>
</head>
<body>
    <div class="details-container">
        <!-- Header -->
        <div class="page-header">
            <div class="page-title">
                <i class="fas fa-receipt"></i>
                <h1>Venta #<?= $sale['id'] ?></h1>
            </div>
            <a href="index.php" class="btn-back">
                <i class="fas fa-arrow-left"></i>
                Volver a Ventas
            </a>
        </div>

        <!-- Sale Information -->
        <div class="sale-info-grid">
            <!-- General Info -->
            <div class="info-card">
                <h3><i class="fas fa-info-circle"></i> Información General</h3>
                
                <div class="info-row">
                    <span class="info-label">ID de Venta:</span>
                    <span class="info-value">#<?= $sale['id'] ?></span>
                </div>
                
                <div class="info-row">
                    <span class="info-label">Fecha:</span>
                    <span class="info-value"><?= date('d/m/Y H:i:s', strtotime($sale['sale_date'])) ?></span>
                </div>
                
                <div class="info-row">
                    <span class="info-label">Usuario:</span>
                    <span class="info-value"><?= htmlspecialchars($sale['username'] ?? 'N/A') ?></span>
                </div>
                
                <div class="info-row">
                    <span class="info-label">Cliente:</span>
                    <span class="info-value">
                        <?php if (!empty($sale['cliente_nombre']) && $sale['cliente_nombre'] !== 'null'): ?>
                            <?= htmlspecialchars($sale['cliente_nombre']) ?>
                            <?php if (!empty($sale['cliente_telefono'])): ?>
                                <br><small><?= htmlspecialchars($sale['cliente_telefono']) ?></small>
                            <?php endif; ?>
                        <?php else: ?>
                            Sin cliente
                        <?php endif; ?>
                    </span>
                </div>
                
                <div class="info-row">
                    <span class="info-label">Estado:</span>
                    <span class="info-value">
                        <span class="status-badge status-<?= $sale['status'] ?>">
                            <?php
                            $statuses = [
                                'completed' => 'Completada',
                                'pending' => 'Pendiente',
                                'cancelled' => 'Cancelada'
                            ];
                            echo $statuses[$sale['status']] ?? $sale['status'];
                            ?>
                        </span>
                    </span>
                </div>
            </div>

            <!-- Payment Info -->
            <div class="info-card">
                <h3><i class="fas fa-credit-card"></i> Información de Pago</h3>
                
                <div class="info-row">
                    <span class="info-label">Método de Pago:</span>
                    <span class="info-value">
                        <span class="payment-method payment-<?= $sale['payment_method'] ?>">
                            <?php
                            $paymentMethods = [
                                'cash' => 'Efectivo',
                                'efectivo' => 'Efectivo',
                                'card' => 'Tarjeta',
                                'transfer' => 'Transferencia'
                            ];
                            echo $paymentMethods[$sale['payment_method']] ?? ucfirst($sale['payment_method']);
                            ?>
                        </span>
                    </span>
                </div>
                
                <div class="info-row">
                    <span class="info-label">Subtotal:</span>
                    <span class="info-value">$<?= number_format($sale['subtotal'] ?? 0, 2) ?></span>
                </div>
                
                <div class="info-row">
                    <span class="info-label">IVA (16%):</span>
                    <span class="info-value">$<?= number_format($sale['tax_amount'] ?? 0, 2) ?></span>
                </div>
                
                <?php if (($sale['discount_amount'] ?? 0) > 0): ?>
                <div class="info-row">
                    <span class="info-label">Descuento:</span>
                    <span class="info-value">-$<?= number_format($sale['discount_amount'], 2) ?></span>
                </div>
                <?php endif; ?>
                
                <div class="info-row">
                    <span class="info-label">Total:</span>
                    <span class="info-value"><strong>$<?= number_format($sale['total_amount'] ?? 0, 2) ?></strong></span>
                </div>
                
                <?php if ($sale['payment_method'] === 'cash' || $sale['payment_method'] === 'efectivo'): ?>
                <div class="info-row">
                    <span class="info-label">Monto Recibido:</span>
                    <span class="info-value">$<?= number_format($sale['amount_paid'] ?? 0, 2) ?></span>
                </div>
                
                <div class="info-row">
                    <span class="info-label">Cambio:</span>
                    <span class="info-value">$<?= number_format($sale['change_amount'] ?? 0, 2) ?></span>
                </div>
                <?php endif; ?>
            </div>
        </div>

        <!-- Items Section -->
        <div class="items-section">
            <h3><i class="fas fa-shopping-basket"></i> Productos Vendidos</h3>
            
            <?php if (!empty($sale['items'])): ?>
                <table class="items-table">
                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th>Cantidad</th>
                            <th>Precio Unitario</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php foreach ($sale['items'] as $item): ?>
                            <tr>
                                <td><?= htmlspecialchars($item['product_name'] ?? 'Producto sin nombre') ?></td>
                                <td><?= $item['quantity'] ?? 0 ?></td>
                                <td>$<?= number_format($item['price'] ?? 0, 2) ?></td>
                                <td><strong>$<?= number_format($item['total_price'] ?? 0, 2) ?></strong></td>
                            </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>
                
                <div class="total-section">
                    <div class="total-row">
                        <span>Subtotal:</span>
                        <span>$<?= number_format($sale['subtotal'] ?? 0, 2) ?></span>
                    </div>
                    <div class="total-row">
                        <span>IVA (16%):</span>
                        <span>$<?= number_format($sale['tax_amount'] ?? 0, 2) ?></span>
                    </div>
                    <?php if (($sale['discount_amount'] ?? 0) > 0): ?>
                    <div class="total-row">
                        <span>Descuento:</span>
                        <span>-$<?= number_format($sale['discount_amount'], 2) ?></span>
                    </div>
                    <?php endif; ?>
                    <div class="total-row final">
                        <span>TOTAL:</span>
                        <span>$<?= number_format($sale['total_amount'] ?? 0, 2) ?></span>
                    </div>
                </div>
            <?php else: ?>
                <p style="text-align: center; color: #7f8c8d; padding: 2rem;">No se encontraron productos para esta venta.</p>
            <?php endif; ?>
        </div>

        <?php if (!empty($sale['notes'])): ?>
        <!-- Notes Section -->
        <div class="info-card" style="margin-top: 2rem;">
            <h3><i class="fas fa-sticky-note"></i> Notas</h3>
            <p><?= htmlspecialchars($sale['notes']) ?></p>
        </div>
        <?php endif; ?>
    </div>
</body>
</html>
