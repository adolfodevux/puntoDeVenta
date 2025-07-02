<?php
// Vista de gestión de ventas
session_start();

// Configurar zona horaria
date_default_timezone_set('America/Mexico_City'); // Ajusta según tu ubicación

if (!isset($_SESSION['user_id'])) {
    header('Location: ../auth/login.php');
    exit;
}

require_once __DIR__ . '/../../Models/database.php';
require_once __DIR__ . '/../../Models/Sales.php';

// Instanciar el controlador si no se ha hecho
if (!isset($sales) || !isset($stats)) {
    $salesModel = new Sales();
    
    // Parámetros de paginación
    $page = max(1, intval($_GET['page'] ?? 1));
    $limit = 10;
    
    // Filtros
    $paymentMethodFilter = ''; // Solo efectivo, no necesitamos filtro
    $dateFilter = $_GET['date'] ?? '';
    $searchFilter = $_GET['search'] ?? '';
    
    // Obtener ventas con paginación y filtros
    $sales = $salesModel->getAllSalesWithPagination($page, $limit, $paymentMethodFilter, $dateFilter, $searchFilter);
    $totalSales = $salesModel->getTotalSalesCount($paymentMethodFilter, $dateFilter, $searchFilter);
    $totalPages = ceil($totalSales / $limit);
    
    // Estadísticas
    $stats = $salesModel->getSalesStats();
} else {
    // Si las variables ya están definidas (viniendo del controller), definir las variables de paginación
    $page = max(1, intval($_GET['page'] ?? 1));
    $limit = 10;
    $paymentMethodFilter = $_GET['payment_method'] ?? '';
    $dateFilter = $_GET['date'] ?? '';
    $searchFilter = $_GET['search'] ?? '';
    
    if (!isset($totalSales) || !isset($totalPages)) {
        $salesModel = new Sales();
        $totalSales = $salesModel->getTotalSalesCount($paymentMethodFilter, $dateFilter, $searchFilter);
        $totalPages = ceil($totalSales / $limit);
    }
}

$currentDate = date('d/m/Y');
$currentTime = date('H:i:s');
?>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Ventas - POS</title>
    <link rel="stylesheet" href="../../Assets/css/dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <style>
        body {
            font-family: 'Inter', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f7fa;
            margin: 0;
            padding: 0;
        }
        
        .sales-container {
            max-width: 1400px;
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
            font-size: 2rem;
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
        
        /* Stats Cards */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2rem;
        }
        
        .stat-card {
            background: white;
            border-radius: 16px;
            padding: 1.5rem;
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
            border: 1px solid rgba(0, 0, 0, 0.05);
            transition: transform 0.3s ease;
        }
        
        .stat-card:hover {
            transform: translateY(-4px);
        }
        
        .stat-card.blue {
            border-left: 4px solid #3498db;
        }
        
        .stat-card.green {
            border-left: 4px solid #27ae60;
        }
        
        .stat-card.orange {
            border-left: 4px solid #f39c12;
        }
        
        .stat-card.red {
            border-left: 4px solid #e74c3c;
        }
        
        .stat-icon {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            color: white;
            margin-bottom: 1rem;
        }
        
        .stat-card.blue .stat-icon {
            background: linear-gradient(135deg, #3498db, #5dade2);
        }
        
        .stat-card.green .stat-icon {
            background: linear-gradient(135deg, #27ae60, #2ecc71);
        }
        
        .stat-card.orange .stat-icon {
            background: linear-gradient(135deg, #f39c12, #f1c40f);
        }
        
        .stat-card.red .stat-icon {
            background: linear-gradient(135deg, #e74c3c, #c0392b);
        }
        
        .stat-value {
            font-size: 2rem;
            font-weight: 700;
            color: #2c3e50;
            margin-bottom: 0.5rem;
        }
        
        .stat-label {
            color: #7f8c8d;
            font-size: 0.9rem;
            font-weight: 500;
        }
        
        /* Filters */
        .filters-section {
            background: white;
            border-radius: 12px;
            padding: 1.5rem;
            margin-bottom: 2rem;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
        
        .filters-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            align-items: end;
        }
        
        .filter-group {
            display: flex;
            flex-direction: column;
            gap: 0.5rem;
        }
        
        .filter-label {
            font-weight: 600;
            color: #2c3e50;
            font-size: 0.9rem;
        }
        
        .filter-input {
            padding: 0.75rem;
            border: 2px solid #e9ecef;
            border-radius: 8px;
            font-size: 0.9rem;
            transition: border-color 0.3s ease;
        }
        
        .filter-input:focus {
            outline: none;
            border-color: #3498db;
        }
        
        .btn-filter {
            background: #17a2b8;
            color: white;
            border: none;
            padding: 0.75rem 1.5rem;
            border-radius: 8px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .btn-filter:hover {
            background: #138496;
            transform: translateY(-2px);
        }
        
        .btn-clear {
            background: #dc3545;
            color: white;
            border: none;
            padding: 0.75rem 1.5rem;
            border-radius: 8px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            gap: 0.5rem;
            text-decoration: none;
        }
        
        .btn-clear:hover {
            background: #c82333;
            transform: translateY(-2px);
            color: white;
        }
        
        /* Sales Table */
        .sales-table-container {
            background: white;
            border-radius: 12px;
            padding: 1.5rem;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            overflow-x: auto;
        }
        
        .sales-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.9rem;
        }
        
        .sales-table th {
            background: #f8f9fa;
            color: #495057;
            font-weight: 600;
            padding: 1rem;
            text-align: left;
            border-bottom: 2px solid #dee2e6;
        }
        
        .sales-table td {
            padding: 1rem;
            border-bottom: 1px solid #dee2e6;
            vertical-align: middle;
        }
        
        .sales-table tbody tr:hover {
            background: #f8f9fa;
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
        
        .status-pending {
            background: #fff3cd;
            color: #856404;
        }
        
        .status-cancelled {
            background: #f8d7da;
            color: #721c24;
        }
        
        .payment-method {
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
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
        
        .payment-card {
            background: #e3f2fd;
            color: #1565c0;
        }
        
        .actions-btn {
            background: none;
            border: none;
            color: #6c757d;
            font-size: 1rem;
            cursor: pointer;
            padding: 0.5rem;
            border-radius: 4px;
            transition: all 0.3s ease;
        }
        
        .actions-btn:hover {
            background: #f8f9fa;
            color: #495057;
        }
        
        .btn-view {
            color: #3498db;
        }
        
        .btn-delete {
            color: #e74c3c;
        }
        
        .btn-view:hover {
            background: rgba(52, 152, 219, 0.1);
        }
        
        .btn-delete:hover {
            background: rgba(231, 76, 60, 0.1);
        }
        
        .btn-report {
            color: #f39c12;
        }
        
        .btn-report:hover {
            background: rgba(243, 156, 18, 0.1);
        }
        
        .no-sales {
            text-align: center;
            padding: 3rem;
            color: #7f8c8d;
        }
        
        .no-sales i {
            font-size: 4rem;
            margin-bottom: 1rem;
            color: #bdc3c7;
        }
        
        /* Pagination */
        .pagination-container {
            display: flex;
            justify-content: center;
            align-items: center;
            margin-top: 2rem;
            gap: 1rem;
        }
        
        .pagination {
            display: flex;
            gap: 0.5rem;
            align-items: center;
        }
        
        .pagination a,
        .pagination span {
            display: flex;
            align-items: center;
            justify-content: center;
            min-width: 40px;
            height: 40px;
            padding: 0.5rem;
            border-radius: 8px;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        
        .pagination a {
            background: white;
            color: #495057;
            border: 1px solid #dee2e6;
        }
        
        .pagination a:hover {
            background: #3498db;
            color: white;
            border-color: #3498db;
            transform: translateY(-2px);
        }
        
        .pagination .current {
            background: #3498db;
            color: white;
            border: 1px solid #3498db;
        }
        
        .pagination-info {
            color: #6c757d;
            font-size: 0.9rem;
        }
        
        /* Responsive */
        @media (max-width: 768px) {
            .sales-container {
                padding: 1rem;
            }
            
            .page-header {
                flex-direction: column;
                gap: 1rem;
                align-items: stretch;
            }
            
            .stats-grid {
                grid-template-columns: 1fr;
            }
            
            .filters-grid {
                grid-template-columns: 1fr;
            }
            
            .sales-table-container {
                overflow-x: auto;
            }
            
            .sales-table {
                min-width: 600px;
            }
        }
    </style>
</head>
<body>
    <div class="sales-container">
        <!-- Header -->
        <div class="page-header">
            <div class="page-title">
                <i class="fas fa-chart-line"></i>
                <h1>Gestión de Ventas</h1>
            </div>
            <a href="../dashboard/index.php" class="btn-back">
                <i class="fas fa-arrow-left"></i>
                Volver al Dashboard
            </a>
        </div>

        <!-- Stats Cards -->
        <div class="stats-grid">
            <div class="stat-card blue">
                <div class="stat-icon">
                    <i class="fas fa-shopping-cart"></i>
                </div>
                <div class="stat-value"><?= $stats['ventas_totales'] ?></div>
                <div class="stat-label">Ventas Totales</div>
            </div>
            
            <div class="stat-card green">
                <div class="stat-icon">
                    <i class="fas fa-dollar-sign"></i>
                </div>
                <div class="stat-value">$<?= number_format($stats['ingresos_totales'], 2) ?></div>
                <div class="stat-label">Ingresos Totales</div>
            </div>
            
            <div class="stat-card orange">
                <div class="stat-icon">
                    <i class="fas fa-shopping-bag"></i>
                </div>
                <div class="stat-value"><?= $stats['ventas_hoy'] ?></div>
                <div class="stat-label">Ventas de Hoy</div>
            </div>
            
            <div class="stat-card red">
                <div class="stat-icon">
                    <i class="fas fa-cash-register"></i>
                </div>
                <div class="stat-value">$<?= number_format($stats['ingresos_hoy'], 2) ?></div>
                <div class="stat-label">Ingresos de Hoy</div>
            </div>
        </div>

        <!-- Filters -->
        <div class="filters-section">
            <form method="GET" action="">
                <div class="filters-grid">
                    <div class="filter-group">
                        <label class="filter-label">Fecha:</label>
                        <input type="month" name="date" value="<?= $dateFilter ?>" class="filter-input">
                    </div>
                    
                    <div class="filter-group">
                        <label class="filter-label">Buscar Venta:</label>
                        <input type="text" name="search" value="<?= $_GET['search'] ?? '' ?>" 
                               placeholder="ID, cliente o total..." class="filter-input">
                    </div>
                    
                    <div class="filter-group">
                        <button type="submit" class="btn-filter">
                            <i class="fas fa-search"></i>
                            Buscar
                        </button>
                    </div>
                    
                    <?php if (!empty($_GET['search']) || !empty($dateFilter)): ?>
                    <div class="filter-group">
                        <a href="index.php" class="btn-clear">
                            <i class="fas fa-times"></i>
                            Limpiar
                        </a>
                    </div>
                    <?php endif; ?>
                </div>
            </form>
        </div>

        <!-- Sales Table -->
        <div class="sales-table-container">
            <?php if (empty($sales)): ?>
                <div class="no-sales">
                    <i class="fas fa-chart-line"></i>
                    <h3>No hay ventas registradas</h3>
                    <p>Las ventas aparecerán aquí una vez que se realicen desde el punto de venta.</p>
                </div>
            <?php else: ?>
                <table class="sales-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Usuario</th>
                            <th>Cliente</th>
                            <th>Total</th>
                            <th>Método de Pago</th>
                            <th>Fecha</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php 
                        $counter = (($page - 1) * $limit) + 1;
                        foreach ($sales as $sale): 
                        ?>
                            <tr>
                                <td><strong><?= $counter++ ?></strong></td>
                                <td><?= htmlspecialchars($sale['username'] ?? 'N/A') ?></td>
                                <td><?= htmlspecialchars($sale['cliente_nombre'] ?? 'Sin cliente') ?></td>
                                <td><strong>$<?= number_format($sale['total_amount'], 2) ?></strong></td>
                                <td>
                                    <span class="payment-method payment-<?= $sale['payment_method'] ?>">
                                        <?php
                                        $paymentMethods = [
                                            'cash' => 'Efectivo',
                                            'efectivo' => 'Efectivo',
                                            'card' => 'Tarjeta',
                                            'transfer' => 'Transferencia'
                                        ];
                                        echo $paymentMethods[$sale['payment_method']] ?? $sale['payment_method'];
                                        ?>
                                    </span>
                                </td>
                                <td><?= date('d/m/Y H:i', strtotime($sale['sale_date'])) ?></td>
                                <td>
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
                                </td>
                                <td>
                                    <button class="actions-btn btn-view" onclick="viewSale(<?= $sale['id'] ?>)" title="Ver detalles">
                                        <i class="fas fa-eye"></i>
                                    </button>
                                    <button class="actions-btn btn-report" onclick="generateReport(<?= $sale['id'] ?>)" title="Generar comprobante">
                                        <i class="fas fa-file-invoice"></i>
                                    </button>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>
                
                <!-- Pagination -->
                <?php if ($totalPages > 1): ?>
                    <div class="pagination-container">
                        <div class="pagination-info">
                            Mostrando <?= count($sales) ?> de <?= $totalSales ?> ventas
                        </div>
                        
                        <div class="pagination">
                            <?php if ($page > 1): ?>
                                <a href="?<?= http_build_query(array_merge($_GET, ['page' => $page - 1])) ?>">
                                    <i class="fas fa-chevron-left"></i>
                                </a>
                            <?php endif; ?>
                            
                            <?php
                            $startPage = max(1, $page - 2);
                            $endPage = min($totalPages, $page + 2);
                            
                            for ($i = $startPage; $i <= $endPage; $i++):
                            ?>
                                <?php if ($i == $page): ?>
                                    <span class="current"><?= $i ?></span>
                                <?php else: ?>
                                    <a href="?<?= http_build_query(array_merge($_GET, ['page' => $i])) ?>"><?= $i ?></a>
                                <?php endif; ?>
                            <?php endfor; ?>
                            
                            <?php if ($page < $totalPages): ?>
                                <a href="?<?= http_build_query(array_merge($_GET, ['page' => $page + 1])) ?>">
                                    <i class="fas fa-chevron-right"></i>
                                </a>
                            <?php endif; ?>
                        </div>
                    </div>
                <?php endif; ?>
            <?php endif; ?>
        </div>
    </div>

    <script>
        function viewSale(saleId) {
            // Redirigir a la página de detalles
            window.location.href = 'details.php?id=' + saleId;
        }

        function generateReport(saleId) {
            // Abrir el comprobante en una nueva ventana
            window.open('comprobante.php?id=' + saleId, '_blank', 'width=500,height=700,scrollbars=yes,resizable=yes');
        }

        // Auto-actualizar stats cada 30 segundos
        setInterval(function() {
            fetch('../../Controllers/SalesController.php?action=stats')
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        document.querySelector('.stat-card.blue .stat-value').textContent = data.data.ventas_totales;
                        document.querySelector('.stat-card.green .stat-value').textContent = '$' + parseFloat(data.data.ingresos_totales).toFixed(2);
                        document.querySelector('.stat-card.orange .stat-value').textContent = data.data.ventas_hoy;
                        document.querySelector('.stat-card.red .stat-value').textContent = '$' + parseFloat(data.data.ingresos_hoy).toFixed(2);
                    }
                })
                .catch(error => console.log('Error actualizando stats:', error));
        }, 30000);
    </script>
</body>
</html>
