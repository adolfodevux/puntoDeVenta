<?php
session_start();
require_once __DIR__ . '/../../Controllers/SuppliersController.php';

// Verificar autenticación
if (!isset($_SESSION['logged_in']) || !$_SESSION['logged_in']) {
    header('Location: ../auth/login.php');
    exit();
}

if (!isset($_GET['id'])) {
    header('Location: index.php');
    exit();
}

$controller = new SuppliersController();
$result = $controller->show($_GET['id']);

if (isset($result['error'])) {
    header('Location: index.php');
    exit();
}

$supplier = $result['supplier'];
$products = $result['products'];
?>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Detalles del Proveedor - <?php echo htmlspecialchars($supplier['name']); ?></title>
    <link rel="stylesheet" href="../../Assets/css/dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <!-- Fallback para FontAwesome -->
    <style>
        .fas::before, .far::before, .fab::before {
            display: inline-block;
            text-rendering: auto;
            -webkit-font-smoothing: antialiased;
        }
        
        .fas.fa-truck::before { content: "🚛"; }
        .fas.fa-arrow-left::before { content: "←"; }
        .fas.fa-edit::before { content: "✏"; }
        .fas.fa-trash::before { content: "🗑"; }
        .fas.fa-user::before { content: "👤"; }
        .fas.fa-envelope::before { content: "✉"; }
        .fas.fa-phone::before { content: "📞"; }
        .fas.fa-map-marker-alt::before { content: "📍"; }
        .fas.fa-globe::before { content: "🌐"; }
        .fas.fa-id-card::before { content: "🆔"; }
        .fas.fa-sticky-note::before { content: "📝"; }
        .fas.fa-calendar::before { content: "📅"; }
    </style>
    <style>
        .details-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }

        .details-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            flex-wrap: wrap;
            gap: 15px;
        }

        .header-left {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .back-btn {
            background: #95a5a6;
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            text-decoration: none;
            display: flex;
            align-items: center;
            gap: 8px;
            transition: background 0.3s ease;
        }

        .back-btn:hover {
            background: #7f8c8d;
        }

        .supplier-title {
            color: #2c3e50;
            font-size: 2.2rem;
            font-weight: bold;
            margin: 0;
        }

        .header-actions {
            display: flex;
            gap: 10px;
        }

        .btn-action {
            padding: 10px 20px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 1rem;
            font-weight: 600;
            text-decoration: none;
            display: flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;
        }

        .btn-edit {
            background: #f39c12;
            color: white;
        }

        .btn-edit:hover {
            background: #e67e22;
            transform: translateY(-2px);
        }

        .details-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 30px;
        }

        .info-card {
            background: white;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .card-title {
            color: #2c3e50;
            font-size: 1.3rem;
            font-weight: 600;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
            padding-bottom: 10px;
            border-bottom: 2px solid #ecf0f1;
        }

        .info-item {
            display: flex;
            align-items: flex-start;
            gap: 12px;
            margin-bottom: 15px;
            padding: 8px 0;
        }

        .info-icon {
            color: #3498db;
            width: 20px;
            text-align: center;
            margin-top: 2px;
        }

        .info-content {
            flex: 1;
        }

        .info-label {
            color: #7f8c8d;
            font-size: 0.9rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 4px;
        }

        .info-value {
            color: #2c3e50;
            font-size: 1rem;
            line-height: 1.4;
        }

        .website-link {
            color: #2ecc71;
            text-decoration: none;
            font-weight: 600;
        }

        .website-link:hover {
            text-decoration: underline;
        }

        .empty-value {
            color: #bdc3c7;
            font-style: italic;
        }

        .products-section {
            background: white;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }

        .products-header {
            background: #f8f9fa;
            padding: 20px 25px;
            border-bottom: 1px solid #e9ecef;
        }

        .products-title {
            color: #2c3e50;
            font-size: 1.4rem;
            font-weight: 600;
            margin: 0;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .products-count {
            background: #3498db;
            color: white;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.9rem;
            font-weight: 600;
        }

        .products-table {
            width: 100%;
            border-collapse: collapse;
        }

        .products-table th {
            background: #f8f9fa;
            padding: 15px;
            text-align: left;
            font-weight: 600;
            color: #2c3e50;
            border-bottom: 1px solid #e9ecef;
        }

        .products-table td {
            padding: 15px;
            border-bottom: 1px solid #e9ecef;
            vertical-align: middle;
        }

        .products-table tr:hover {
            background: #f8f9fa;
        }

        .product-name {
            font-weight: 600;
            color: #2c3e50;
        }

        .product-category {
            background: #e9ecef;
            color: #495057;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 0.85rem;
            font-weight: 500;
        }

        .price {
            font-weight: 600;
            color: #2ecc71;
        }

        .stock {
            font-weight: 600;
        }

        .stock.low {
            color: #e74c3c;
        }

        .stock.medium {
            color: #f39c12;
        }

        .stock.high {
            color: #2ecc71;
        }

        .no-products {
            text-align: center;
            padding: 40px;
            color: #7f8c8d;
        }

        .no-products-icon {
            font-size: 3rem;
            margin-bottom: 15px;
            color: #bdc3c7;
        }

        @media (max-width: 768px) {
            .details-grid {
                grid-template-columns: 1fr;
            }

            .details-header {
                flex-direction: column;
                align-items: stretch;
            }

            .header-left {
                justify-content: center;
            }

            .supplier-title {
                font-size: 1.8rem;
                text-align: center;
            }

            .header-actions {
                justify-content: center;
            }

            .info-card {
                padding: 20px;
            }

            .products-table-container {
                overflow-x: auto;
            }

            .products-table {
                min-width: 600px;
            }
        }
    </style>
</head>
<body>
    <div class="details-container">
        <div class="details-header">
            <div class="header-left">
                <a href="index.php" class="back-btn">
                    <i class="fas fa-arrow-left"></i>
                    Volver
                </a>
                <h1 class="supplier-title">
                    <i class="fas fa-truck"></i>
                    <?php echo htmlspecialchars($supplier['name']); ?>
                </h1>
            </div>
            <div class="header-actions">
                <a href="manage.php?id=<?php echo $supplier['id']; ?>" class="btn-action btn-edit">
                    <i class="fas fa-edit"></i>
                    Editar
                </a>
            </div>
        </div>

        <div class="details-grid">
            <!-- Información Básica -->
            <div class="info-card">
                <h3 class="card-title">
                    <i class="fas fa-info-circle"></i>
                    Información Básica
                </h3>
                
                <div class="info-item">
                    <i class="fas fa-building info-icon"></i>
                    <div class="info-content">
                        <div class="info-label">Nombre del Proveedor</div>
                        <div class="info-value"><?php echo htmlspecialchars($supplier['name']); ?></div>
                    </div>
                </div>

                <?php if (!empty($supplier['contact_person'])): ?>
                <div class="info-item">
                    <i class="fas fa-user info-icon"></i>
                    <div class="info-content">
                        <div class="info-label">Persona de Contacto</div>
                        <div class="info-value"><?php echo htmlspecialchars($supplier['contact_person']); ?></div>
                    </div>
                </div>
                <?php endif; ?>

                <?php if (!empty($supplier['tax_id'])): ?>
                <div class="info-item">
                    <i class="fas fa-file-invoice info-icon"></i>
                    <div class="info-content">
                        <div class="info-label">ID Fiscal/RUC</div>
                        <div class="info-value"><?php echo htmlspecialchars($supplier['tax_id']); ?></div>
                    </div>
                </div>
                <?php endif; ?>

                <div class="info-item">
                    <i class="fas fa-calendar info-icon"></i>
                    <div class="info-content">
                        <div class="info-label">Registrado</div>
                        <div class="info-value">
                            <?php echo date('d/m/Y H:i', strtotime($supplier['created_at'])); ?>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Información de Contacto -->
            <div class="info-card">
                <h3 class="card-title">
                    <i class="fas fa-address-book"></i>
                    Contacto y Ubicación
                </h3>

                <div class="info-item">
                    <i class="fas fa-envelope info-icon"></i>
                    <div class="info-content">
                        <div class="info-label">Email</div>
                        <div class="info-value">
                            <?php if (!empty($supplier['email'])): ?>
                                <a href="mailto:<?php echo htmlspecialchars($supplier['email']); ?>" style="color: #3498db;">
                                    <?php echo htmlspecialchars($supplier['email']); ?>
                                </a>
                            <?php else: ?>
                                <span class="empty-value">No especificado</span>
                            <?php endif; ?>
                        </div>
                    </div>
                </div>

                <div class="info-item">
                    <i class="fas fa-phone info-icon"></i>
                    <div class="info-content">
                        <div class="info-label">Teléfono</div>
                        <div class="info-value">
                            <?php if (!empty($supplier['phone'])): ?>
                                <a href="tel:<?php echo htmlspecialchars($supplier['phone']); ?>" style="color: #3498db;">
                                    <?php echo htmlspecialchars($supplier['phone']); ?>
                                </a>
                            <?php else: ?>
                                <span class="empty-value">No especificado</span>
                            <?php endif; ?>
                        </div>
                    </div>
                </div>

               

                <?php if (!empty($supplier['address']) || !empty($supplier['city']) || !empty($supplier['country'])): ?>
                <div class="info-item">
                    <i class="fas fa-map-marker-alt info-icon"></i>
                    <div class="info-content">
                        <div class="info-label">Ubicación</div>
                        <div class="info-value">
                            <?php if (!empty($supplier['address'])): ?>
                                <div><?php echo nl2br(htmlspecialchars($supplier['address'])); ?></div>
                            <?php endif; ?>
                            <?php if (!empty($supplier['city']) || !empty($supplier['country'])): ?>
                                <div style="margin-top: 5px;">
                                    <?php echo htmlspecialchars($supplier['city'] ?? ''); ?>
                                    <?php echo (!empty($supplier['city']) && !empty($supplier['country'])) ? ', ' : ''; ?>
                                    <?php echo htmlspecialchars($supplier['country'] ?? ''); ?>
                                </div>
                            <?php endif; ?>
                        </div>
                    </div>
                </div>
                <?php endif; ?>
            </div>
        </div>

        <?php if (!empty($supplier['notes'])): ?>
        <div class="info-card" style="margin-bottom: 30px;">
            <h3 class="card-title">
                <i class="fas fa-sticky-note"></i>
                Notas y Comentarios
            </h3>
            <div class="info-value">
                <?php echo nl2br(htmlspecialchars($supplier['notes'])); ?>
            </div>
        </div>
        <?php endif; ?>

        <!-- Productos del Proveedor -->
        <div class="products-section">
            <div class="products-header">
                <h3 class="products-title">
                    <i class="fas fa-box"></i>
                    Productos
                    <span class="products-count"><?php echo count($products); ?></span>
                </h3>
            </div>

            <?php if (empty($products)): ?>
                <div class="no-products">
                    <div class="no-products-icon">
                        <i class="fas fa-box-open"></i>
                    </div>
                    <h4>No hay productos registrados</h4>
                    <p>Este proveedor aún no tiene productos asociados</p>
                </div>
            <?php else: ?>
                <div class="products-table-container">
                    <table class="products-table">
                        <thead>
                            <tr>
                                <th>Producto</th>
                                <th>Categoría</th>
                                <th>Precio</th>
                                <th>Stock</th>
                                <th>Estado</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php foreach ($products as $product): ?>
                                <tr>
                                    <td>
                                        <div class="product-name"><?php echo htmlspecialchars($product['name']); ?></div>
                                        <?php if (!empty($product['description'])): ?>
                                            <div style="color: #7f8c8d; font-size: 0.9rem; margin-top: 4px;">
                                                <?php echo htmlspecialchars(substr($product['description'], 0, 80)); ?>
                                                <?php echo strlen($product['description']) > 80 ? '...' : ''; ?>
                                            </div>
                                        <?php endif; ?>
                                    </td>
                                    <td>
                                        <?php if (!empty($product['category_name'])): ?>
                                            <span class="product-category"><?php echo htmlspecialchars($product['category_name']); ?></span>
                                        <?php else: ?>
                                            <span class="empty-value">Sin categoría</span>
                                        <?php endif; ?>
                                    </td>
                                    <td>
                                        <span class="price">$<?php echo number_format($product['price'], 2); ?></span>
                                    </td>
                                    <td>
                                        <?php
                                        $stockClass = 'high';
                                        if ($product['stock'] <= $product['min_stock']) {
                                            $stockClass = 'low';
                                        } elseif ($product['stock'] <= $product['min_stock'] * 2) {
                                            $stockClass = 'medium';
                                        }
                                        ?>
                                        <span class="stock <?php echo $stockClass; ?>">
                                            <?php echo $product['stock']; ?> unidades
                                        </span>
                                    </td>
                                    <td>
                                        <?php if ($product['is_active']): ?>
                                            <span style="color: #2ecc71; font-weight: 600;">
                                                <i class="fas fa-check-circle"></i> Activo
                                            </span>
                                        <?php else: ?>
                                            <span style="color: #e74c3c; font-weight: 600;">
                                                <i class="fas fa-times-circle"></i> Inactivo
                                            </span>
                                        <?php endif; ?>
                                    </td>
                                </tr>
                            <?php endforeach; ?>
                        </tbody>
                    </table>
                </div>
            <?php endif; ?>
        </div>
    </div>

    <script>
        // Tecla ESC para volver
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                window.location.href = 'index.php';
            }
        });
    </script>
</body>
</html>
