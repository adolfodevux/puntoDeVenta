<?php
session_start();
require_once __DIR__ . '/../../Controllers/SuppliersController.php';

// Verificar autenticación
if (!isset($_SESSION['logged_in']) || !$_SESSION['logged_in']) {
    header('Location: ../auth/login.php');
    exit();
}

$controller = new SuppliersController();
$data = $controller->index();
$suppliers = $data['suppliers'];
$stats = $data['stats'];
?>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Proveedores - Punto-D</title>
    <link rel="stylesheet" href="../../Assets/css/dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <!-- Fallback para FontAwesome en caso de que el CDN falle -->
    <style>
        /* Asegurar que los iconos se muestren incluso si FontAwesome no carga */
        .fas::before, .far::before, .fab::before {
            display: inline-block;
            text-rendering: auto;
            -webkit-font-smoothing: antialiased;
        }
        
        /* Si FontAwesome no carga, mostrar texto alternativo */
        .fas.fa-truck::before { content: "🚛"; }
        .fas.fa-search::before { content: "🔍"; }
        .fas.fa-times::before { content: "✖"; }
        .fas.fa-eye::before { content: "👁"; }
        .fas.fa-edit::before { content: "✏"; }
        .fas.fa-trash::before { content: "🗑"; }
        .fas.fa-plus::before { content: "+"; }
        .fas.fa-arrow-left::before { content: "←"; }
        .fas.fa-user::before { content: "👤"; }
        .fas.fa-envelope::before { content: "✉"; }
        .fas.fa-phone::before { content: "📞"; }
        .fas.fa-external-link-alt::before { content: "🔗"; }
    </style>
    <style>
        .suppliers-container {
            padding: 20px;
            max-width: 1200px;
            margin: 0 auto;
        }

        .suppliers-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            flex-wrap: wrap;
            gap: 15px;
        }

        .suppliers-title {
            color: #2c3e50;
            font-size: 2.5rem;
            font-weight: bold;
            margin: 0;
        }

        .btn-add-supplier {
            background: linear-gradient(135deg, #2ecc71, #27ae60);
            color: white;
            padding: 12px 24px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 1rem;
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;
            text-decoration: none;
        }

        .btn-add-supplier:hover {
            background: linear-gradient(135deg, #27ae60, #2ecc71);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(46, 204, 113, 0.3);
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .stat-card {
            background: white;
            padding: 20px;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            border-left: 4px solid #3498db;
        }

        .stat-card.suppliers {
            border-left-color: #2ecc71;
        }

        .stat-card.with-products {
            border-left-color: #f39c12;
        }

        .stat-number {
            font-size: 2rem;
            font-weight: bold;
            color: #2c3e50;
            margin-bottom: 5px;
        }

        .stat-label {
            color: #7f8c8d;
            font-size: 0.9rem;
        }

        .search-section {
            background: white;
            padding: 20px;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }

        .search-box {
            display: flex;
            gap: 10px;
            align-items: center;
        }

        .search-input {
            flex: 1;
            padding: 12px 16px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 1rem;
            transition: border-color 0.3s ease;
        }

        .search-input:focus {
            outline: none;
            border-color: #3498db;
        }

        .search-btn {
            background: #3498db;
            color: white;
            border: none;
            padding: 12px 20px;
            border-radius: 8px;
            cursor: pointer;
            font-size: 1rem;
            transition: background 0.3s ease;
            display: flex;
            align-items: center;
            gap: 8px;
            white-space: nowrap;
        }

        .search-btn:hover {
            background: #2980b9;
        }

        .search-btn i {
            font-size: 0.9rem;
        }

        .suppliers-table-container {
            background: white;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }

        .suppliers-table {
            width: 100%;
            border-collapse: collapse;
        }

        .suppliers-table th {
            background: #f8f9fa;
            padding: 15px;
            text-align: left;
            font-weight: 600;
            color: #2c3e50;
            border-bottom: 2px solid #e9ecef;
        }

        .suppliers-table td {
            padding: 15px;
            border-bottom: 1px solid #e9ecef;
            vertical-align: middle;
        }

        .suppliers-table tr:hover {
            background: #f8f9fa;
        }

        .supplier-info {
            display: flex;
            flex-direction: column;
            gap: 4px;
        }

        .supplier-name {
            font-weight: 600;
            color: #2c3e50;
            font-size: 1.1rem;
        }

        .supplier-contact {
            color: #7f8c8d;
            font-size: 0.9rem;
        }

        .contact-info {
            display: flex;
            flex-direction: column;
            gap: 2px;
            font-size: 0.9rem;
        }

        .contact-email {
            color: #3498db;
        }

        .contact-phone {
            color: #7f8c8d;
        }

        .website-link {
            color: #2ecc71;
            text-decoration: none;
            font-size: 0.9rem;
        }

        .website-link:hover {
            text-decoration: underline;
        }

        .actions {
            display: flex;
            gap: 8px;
        }

        .btn-action {
            padding: 8px 12px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 0.9rem;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 6px;
            font-weight: 500;
            min-width: 80px;
        }

        .btn-action i {
            font-size: 0.85rem;
        }

        .btn-view {
            background: #3498db;
            color: white;
        }

        .btn-view:hover {
            background: #2980b9;
        }

        .btn-edit {
            background: #f39c12;
            color: white;
        }

        .btn-edit:hover {
            background: #e67e22;
        }

        .btn-delete {
            background: #e74c3c;
            color: white;
        }

        .btn-delete:hover {
            background: #c0392b;
        }

        .empty-state {
            text-align: center;
            padding: 40px;
            color: #7f8c8d;
        }

        .empty-icon {
            font-size: 4rem;
            margin-bottom: 20px;
            color: #bdc3c7;
        }

        @media (max-width: 768px) {
            .suppliers-header {
                flex-direction: column;
                align-items: stretch;
            }

            .suppliers-header > div {
                flex-direction: column;
                gap: 10px;
                margin-top: 15px;
            }

            .suppliers-title {
                font-size: 2rem;
                text-align: center;
            }

            .search-box {
                flex-direction: column;
            }

            .suppliers-table-container {
                overflow-x: auto;
            }

            .suppliers-table {
                min-width: 600px;
            }

            .actions {
                flex-direction: column;
                gap: 5px;
            }

            .btn-action {
                justify-content: center;
                min-width: 100px;
            }

            .search-btn {
                justify-content: center;
                width: 100%;
            }
        }
    </style>
</head>
<body>
    <div class="suppliers-container">
        <div class="suppliers-header">
            <h1 class="suppliers-title">
                <i class="fas fa-truck"></i> Gestión de Proveedores
            </h1>
            <div style="display: flex; gap: 10px;">
                <a href="../dashboard/index.php" class="btn-add-supplier" style="background: linear-gradient(135deg, #6c757d, #5a6268);">
                    <i class="fas fa-arrow-left"></i>
                    Regresar al Dashboard
                </a>
                <a href="manage.php" class="btn-add-supplier">
                    <i class="fas fa-plus"></i>
                    Agregar Proveedor
                </a>
            </div>
        </div>

        <!-- Estadísticas -->
        <div class="stats-grid">
            <div class="stat-card suppliers">
                <div class="stat-number"><?php echo $stats['total_suppliers']; ?></div>
                <div class="stat-label">Total Proveedores</div>
            </div>
            <div class="stat-card with-products">
                <div class="stat-number"><?php echo $stats['suppliers_with_products']; ?></div>
                <div class="stat-label">Con Productos</div>
            </div>
        </div>

        <!-- Búsqueda -->
        <div class="search-section">
            <div class="search-box">
                <input type="text" class="search-input" id="searchInput" placeholder="Buscar proveedores por nombre, contacto, email o ciudad...">
                <button class="search-btn" id="searchBtn">
                    <i class="fas fa-search"></i>
                    Buscar
                </button>
                <button class="search-btn" id="clearBtn" style="background: #95a5a6;">
                    <i class="fas fa-times"></i>
                    Limpiar
                </button>
            </div>
        </div>

        <!-- Tabla de Proveedores -->
        <div class="suppliers-table-container">
            <?php if (empty($suppliers)): ?>
                <div class="empty-state">
                    <div class="empty-icon">
                        <i class="fas fa-truck"></i>
                    </div>
                    <h3>No hay proveedores registrados</h3>
                    <p>Comienza agregando tu primer proveedor</p>
                    <a href="manage.php" class="btn-add-supplier" style="margin-top: 20px;">
                        <i class="fas fa-plus"></i>
                        Agregar Primer Proveedor
                    </a>
                </div>
            <?php else: ?>
                <table class="suppliers-table">
                    <thead>
                        <tr>
                            <th>Proveedor</th>
                            <th>Contacto</th>
                            <th>Ubicación</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody id="suppliersTableBody">
                        <?php foreach ($suppliers as $supplier): ?>
                            <tr>
                                <td>
                                    <div class="supplier-info">
                                        <div class="supplier-name"><?php echo htmlspecialchars($supplier['name']); ?></div>
                                        <?php if (!empty($supplier['contact_person'])): ?>
                                            <div class="supplier-contact">
                                                <i class="fas fa-user"></i>
                                                <?php echo htmlspecialchars($supplier['contact_person']); ?>
                                            </div>
                                        <?php endif; ?>
                                    </div>
                                </td>
                                <td>
                                    <div class="contact-info">
                                        <?php if (!empty($supplier['email'])): ?>
                                            <div class="contact-email">
                                                <i class="fas fa-envelope"></i>
                                                <?php echo htmlspecialchars($supplier['email']); ?>
                                            </div>
                                        <?php endif; ?>
                                        <?php if (!empty($supplier['phone'])): ?>
                                            <div class="contact-phone">
                                                <i class="fas fa-phone"></i>
                                                <?php echo htmlspecialchars($supplier['phone']); ?>
                                            </div>
                                        <?php endif; ?>
                                    </div>
                                </td>
                                <td>
                                    <?php if (!empty($supplier['city']) || !empty($supplier['country'])): ?>
                                        <div>
                                            <?php if (!empty($supplier['city'])): ?>
                                                <?php echo htmlspecialchars($supplier['city']); ?>
                                            <?php endif; ?>
                                            <?php if (!empty($supplier['country'])): ?>
                                                <?php echo !empty($supplier['city']) ? ', ' : ''; ?>
                                                <?php echo htmlspecialchars($supplier['country']); ?>
                                            <?php endif; ?>
                                        </div>
                                    <?php else: ?>
                                        <span style="color: #bdc3c7;">No especificado</span>
                                    <?php endif; ?>
                                </td>
                                <td>
                                    <div class="actions">
                                        <a href="details.php?id=<?php echo $supplier['id']; ?>" 
                                           class="btn-action btn-view">
                                            <i class="fas fa-eye"></i>
                                            Ver
                                        </a>
                                        <a href="manage.php?id=<?php echo $supplier['id']; ?>" 
                                           class="btn-action btn-edit">
                                            <i class="fas fa-edit"></i>
                                            Editar
                                        </a>
                                        <button class="btn-action btn-delete" 
                                                onclick="deleteSupplier(<?php echo $supplier['id']; ?>, '<?php echo htmlspecialchars($supplier['name']); ?>')">
                                            <i class="fas fa-trash"></i>
                                            Eliminar
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>
            <?php endif; ?>
        </div>
    </div>

    <script>
        // Búsqueda de proveedores
        document.getElementById('searchBtn').addEventListener('click', searchSuppliers);
        document.getElementById('searchInput').addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                searchSuppliers();
            }
        });

        document.getElementById('clearBtn').addEventListener('click', function() {
            document.getElementById('searchInput').value = '';
            loadAllSuppliers();
        });

        function searchSuppliers() {
            const searchTerm = document.getElementById('searchInput').value.trim();
            if (searchTerm === '') {
                loadAllSuppliers();
                return;
            }

            fetch('search.php', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'search_term=' + encodeURIComponent(searchTerm)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    updateSuppliersTable(data.suppliers);
                } else {
                    Swal.fire('Error', 'Error al buscar proveedores', 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                Swal.fire('Error', 'Error de conexión', 'error');
            });
        }

        function loadAllSuppliers() {
            location.reload();
        }

        function updateSuppliersTable(suppliers) {
            const tbody = document.getElementById('suppliersTableBody');
            
            if (suppliers.length === 0) {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="5" style="text-align: center; padding: 40px; color: #7f8c8d;">
                            <i class="fas fa-search" style="font-size: 2rem; margin-bottom: 10px; display: block;"></i>
                            No se encontraron proveedores
                        </td>
                    </tr>
                `;
                return;
            }

            tbody.innerHTML = suppliers.map(supplier => `
                <tr>
                    <td>
                        <div class="supplier-info">
                            <div class="supplier-name">${supplier.name}</div>
                            ${supplier.contact_person ? `
                                <div class="supplier-contact">
                                    <i class="fas fa-user"></i>
                                    ${supplier.contact_person}
                                </div>
                            ` : ''}
                        </div>
                    </td>
                    <td>
                        <div class="contact-info">
                            ${supplier.email ? `
                                <div class="contact-email">
                                    <i class="fas fa-envelope"></i>
                                    ${supplier.email}
                                </div>
                            ` : ''}
                            ${supplier.phone ? `
                                <div class="contact-phone">
                                    <i class="fas fa-phone"></i>
                                    ${supplier.phone}
                                </div>
                            ` : ''}
                        </div>
                    </td>
                    <td>
                        ${supplier.city || supplier.country ? 
                            `${supplier.city || ''}${supplier.city && supplier.country ? ', ' : ''}${supplier.country || ''}` : 
                            '<span style="color: #bdc3c7;">No especificado</span>'
                        }
                    </td>
                    <td>
                        ${supplier.website ? `
                            <a href="${supplier.website}" target="_blank" class="website-link">
                                <i class="fas fa-external-link-alt"></i>
                                Visitar sitio
                            </a>
                        ` : '<span style="color: #bdc3c7;">No disponible</span>'}
                    </td>
                    <td>
                        <div class="actions">
                            <a href="details.php?id=${supplier.id}" class="btn-action btn-view">
                                <i class="fas fa-eye"></i>
                                Ver
                            </a>
                            <a href="manage.php?id=${supplier.id}" class="btn-action btn-edit">
                                <i class="fas fa-edit"></i>
                                Editar
                            </a>
                            <button class="btn-action btn-delete" onclick="deleteSupplier(${supplier.id}, '${supplier.name}')">
                                <i class="fas fa-trash"></i>
                                Eliminar
                            </button>
                        </div>
                    </td>
                </tr>
            `).join('');
        }

        function deleteSupplier(id, name) {
            Swal.fire({
                title: '¿Estás seguro?',
                text: `Se eliminará el proveedor "${name}"`,
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#e74c3c',
                cancelButtonColor: '#95a5a6',
                confirmButtonText: 'Sí, eliminar',
                cancelButtonText: 'Cancelar'
            }).then((result) => {
                if (result.isConfirmed) {
                    fetch('delete.php', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: 'id=' + id
                    })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            Swal.fire('Eliminado', data.message, 'success').then(() => {
                                location.reload();
                            });
                        } else {
                            Swal.fire('Error', data.message, 'error');
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        Swal.fire('Error', 'Error de conexión', 'error');
                    });
                }
            });
        }

        // Volver al dashboard
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                window.location.href = '../dashboard/index.php';
            }
        });
        
        // Verificar carga de FontAwesome
        function checkFontAwesome() {
            const testIcon = document.createElement('i');
            testIcon.className = 'fas fa-truck';
            testIcon.style.position = 'absolute';
            testIcon.style.visibility = 'hidden';
            document.body.appendChild(testIcon);
            
            setTimeout(() => {
                const computedStyle = window.getComputedStyle(testIcon, ':before');
                const content = computedStyle.getPropertyValue('content');
                
                if (!content || content === 'none' || content === '""') {
                    console.warn('FontAwesome no se cargó correctamente. Usando iconos de respaldo.');
                    // Agregar clase para mostrar que hay un problema
                    document.body.classList.add('fontawesome-fallback');
                } else {
                    console.log('FontAwesome cargado correctamente ✓');
                }
                
                document.body.removeChild(testIcon);
            }, 100);
        }
        
        // Verificar FontAwesome al cargar la página
        document.addEventListener('DOMContentLoaded', checkFontAwesome);
    </script>
</body>
</html>
