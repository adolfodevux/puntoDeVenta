<?php
session_start();
if (!isset($_SESSION['user_id'])) {
    header('Location: ../auth/login.php');
    exit();
}

require_once '../../Models/Sales.php';
require_once '../../Models/Cliente.php';

$saleId = $_GET['id'] ?? null;
if (!$saleId) {
    header('Location: index.php');
    exit();
}

$salesModel = new Sales();
$clienteModel = new Cliente();

// Obtener detalles de la venta
$saleDetails = $salesModel->getSaleDetails($saleId);

if (!$saleDetails) {
    header('Location: index.php');
    exit();
}

// Los detalles de la venta incluyen la info general y los items
$sale = $saleDetails;
$saleItems = $saleDetails['items'] ?? [];

// Obtener información del cliente si existe
$cliente = null;
if ($sale['cliente_nombre'] && $sale['cliente_nombre'] !== 'null') {
    $cliente = $clienteModel->getByName($sale['cliente_nombre']);
}
?>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Comprobante de Venta #<?= $sale['id'] ?></title>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Roboto', sans-serif;
            background-color: #f5f5f5;
            padding: 20px;
        }

        .comprobante-container {
            max-width: 400px;
            margin: 0 auto;
            background: white;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }

        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            text-align: center;
        }

        .company-name {
            font-size: 1.5rem;
            font-weight: 700;
            margin-bottom: 5px;
        }

        .company-subtitle {
            font-size: 0.9rem;
            opacity: 0.9;
        }

        .receipt-content {
            padding: 20px;
        }

        .section {
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 1px dashed #ddd;
        }

        .section:last-child {
            border-bottom: none;
            margin-bottom: 0;
        }

        .section-title {
            font-weight: 600;
            color: #333;
            margin-bottom: 10px;
            font-size: 0.9rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .info-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 5px;
            font-size: 0.9rem;
        }

        .info-label {
            color: #666;
            font-weight: 500;
        }

        .info-value {
            color: #333;
            font-weight: 600;
        }

        .products-table {
            width: 100%;
            font-size: 0.85rem;
        }

        .products-table th {
            text-align: left;
            padding: 8px 4px;
            border-bottom: 1px solid #ddd;
            font-weight: 600;
            color: #333;
        }

        .products-table td {
            padding: 6px 4px;
            border-bottom: 1px solid #f0f0f0;
        }

        .product-name {
            font-weight: 500;
            color: #333;
        }

        .total-section {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 6px;
            margin-top: 10px;
        }

        .total-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 5px;
            font-size: 0.9rem;
        }

        .total-final {
            font-size: 1.1rem;
            font-weight: 700;
            color: #2c3e50;
            border-top: 2px solid #ddd;
            padding-top: 8px;
            margin-top: 8px;
        }

        .footer {
            text-align: center;
            padding: 15px 20px;
            background: #f8f9fa;
            font-size: 0.8rem;
            color: #666;
        }

        .thank-you {
            font-weight: 600;
            color: #333;
            margin-bottom: 5px;
        }

        .actions {
            text-align: center;
            padding: 20px;
            background: #f8f9fa;
            border-top: 1px solid #eee;
        }

        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 500;
            text-decoration: none;
            display: inline-block;
            margin: 0 5px;
            transition: all 0.3s ease;
        }

        .btn-print {
            background: #3498db;
            color: white;
        }

        .btn-print:hover {
            background: #2980b9;
        }

        .btn-back {
            background: #95a5a6;
            color: white;
        }

        .btn-back:hover {
            background: #7f8c8d;
        }

        @media print {
            body {
                padding: 0;
                background: white;
            }

            .comprobante-container {
                box-shadow: none;
                border-radius: 0;
                max-width: 100%;
            }

            .actions {
                display: none;
            }
        }

        .payment-method {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 12px;
            font-size: 0.75rem;
            font-weight: 500;
            text-transform: uppercase;
        }

        .payment-method.payment-cash,
        .payment-method.payment-efectivo {
            background: #d4edda;
            color: #155724;
        }

        .status-badge {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 12px;
            font-size: 0.75rem;
            font-weight: 500;
            text-transform: uppercase;
        }

        .status-badge.status-completed {
            background: #d4edda;
            color: #155724;
        }
    </style>
</head>
<body>
    <div class="comprobante-container">
        <!-- Header -->
        <div class="header">
            <div class="company-name">Punto de Venta</div>
            <div class="company-subtitle">Sistema de Gestión Comercial</div>
        </div>

        <!-- Content -->
        <div class="receipt-content">
            <!-- Sale Info -->
            <div class="section">
                <div class="section-title">Información de Venta</div>
                <div class="info-row">
                    <span class="info-label">Comprobante #:</span>
                    <span class="info-value"><?= str_pad($sale['id'], 6, '0', STR_PAD_LEFT) ?></span>
                </div>
                <div class="info-row">
                    <span class="info-label">Fecha:</span>
                    <span class="info-value"><?= date('d/m/Y H:i', strtotime($sale['sale_date'])) ?></span>
                </div>
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
                    <span class="info-label">Estado:</span>
                    <span class="info-value">
                        <span class="status-badge status-<?= $sale['status'] ?>">
                            <?php
                            $statuses = [
                                'completed' => 'Completada',
                                'pending' => 'Pendiente',
                                'cancelled' => 'Cancelada'
                            ];
                            echo $statuses[$sale['status']] ?? ucfirst($sale['status']);
                            ?>
                        </span>
                    </span>
                </div>
            </div>

            <!-- Customer Info -->
            <?php if ($sale['cliente_nombre'] && $sale['cliente_nombre'] !== 'null'): ?>
            <div class="section">
                <div class="section-title">Cliente</div>
                <div class="info-row">
                    <span class="info-label">Nombre:</span>
                    <span class="info-value"><?= htmlspecialchars($sale['cliente_nombre']) ?></span>
                </div>
                <?php if ($cliente && !empty($cliente['telefono'])): ?>
                <div class="info-row">
                    <span class="info-label">Teléfono:</span>
                    <span class="info-value"><?= htmlspecialchars($cliente['telefono']) ?></span>
                </div>
                <?php endif; ?>
            </div>
            <?php endif; ?>

            <!-- Products -->
            <div class="section">
                <div class="section-title">Productos</div>
                <table class="products-table">
                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th style="text-align: center;">Cant.</th>
                            <th style="text-align: right;">Precio</th>
                            <th style="text-align: right;">Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php 
                        $subtotal = 0;
                        if (!empty($saleItems)) {
                            foreach ($saleItems as $detail): 
                                // Validar que los campos necesarios existen
                                $quantity = floatval($detail['quantity'] ?? 0);
                                $price = floatval($detail['price'] ?? 0);
                                $productName = htmlspecialchars($detail['product_name'] ?? 'Producto sin nombre');
                                
                                $lineTotal = $quantity * $price;
                                $subtotal += $lineTotal;
                        ?>
                        <tr>
                            <td class="product-name"><?= $productName ?></td>
                            <td style="text-align: center;"><?= $quantity ?></td>
                            <td style="text-align: right;">$<?= number_format($price, 2) ?></td>
                            <td style="text-align: right;">$<?= number_format($lineTotal, 2) ?></td>
                        </tr>
                        <?php 
                            endforeach;
                        } else {
                            echo '<tr><td colspan="4" style="text-align: center; color: #999;">No hay productos en esta venta</td></tr>';
                        }
                        ?>
                    </tbody>
                </table>
            </div>

            <!-- Totals -->
            <div class="section">
                <div class="total-section">
                    <div class="total-row">
                        <span>Subtotal:</span>
                        <span>$<?= number_format($subtotal, 2) ?></span>
                    </div>
                    <div class="total-row">
                        <span>Impuestos:</span>
                        <span>$0.00</span>
                    </div>
                    <div class="total-row total-final">
                        <span>TOTAL:</span>
                        <span>$<?= number_format(floatval($sale['total_amount'] ?? 0), 2) ?></span>
                    </div>
                </div>
            </div>
        </div>

        <!-- Footer -->
        <div class="footer">
            <div class="thank-you">¡Gracias por su compra!</div>
            <div>Este comprobante es válido como factura simplificada</div>
            <div>Conserve este comprobante para cualquier reclamo</div>
        </div>

        <!-- Actions -->
        <div class="actions">
            <button class="btn btn-print" onclick="window.print()">
                <i class="fas fa-print"></i> Imprimir
            </button>
            <a href="index.php" class="btn btn-back">
                <i class="fas fa-arrow-left"></i> Volver
            </a>
        </div>
    </div>

    <script>
        // Auto focus for print on load (optional)
        document.addEventListener('DOMContentLoaded', function() {
            // Uncomment the line below if you want to auto-print when opening
            // window.print();
        });
    </script>
</body>
</html>
