<?php
// Página independiente de gestión de categorías
session_start();

// Verificar si el usuario está logueado
if (!isset($_SESSION['logged_in']) || !$_SESSION['logged_in']) {
    header('Location: ../auth/login.php');
    exit();
}

// Obtener datos del usuario de la sesión
$username = $_SESSION['username'] ?? 'Usuario';
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Categorías - Punto-D</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <style>
        /* Reset y estilos base */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #ffffff;
            background-image: 
                radial-gradient(circle at 25px 25px, rgba(52, 152, 219, 0.03) 2px, transparent 0),
                radial-gradient(circle at 75px 75px, rgba(44, 62, 80, 0.02) 1px, transparent 0);
            background-size: 100px 100px, 50px 50px;
            background-position: 0 0, 25px 25px;
            min-height: 100vh;
            color: #2c3e50;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 2rem;
        }

        /* Header */
        .page-header {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            border-radius: 15px;
            padding: 2rem;
            margin-bottom: 2rem;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08), 0 2px 10px rgba(0, 0, 0, 0.04);
            border: 1px solid rgba(52, 152, 219, 0.1);
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 1rem;
        }

        .header-left h1 {
            color: #2c3e50;
            font-size: 2.2rem;
            font-weight: 700;
            margin-bottom: 0.5rem;
        }

        .header-left p {
            color: #7f8c8d;
            font-size: 1.1rem;
        }

        .header-right {
            display: flex;
            gap: 1rem;
            align-items: center;
        }

        .btn-back {
            background: linear-gradient(135deg, #95a5a6, #7f8c8d);
            color: white;
            padding: 0.75rem 1.5rem;
            border: none;
            border-radius: 10px;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .btn-back:hover {
            background: linear-gradient(135deg, #7f8c8d, #6c7b7d);
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(127, 140, 141, 0.3);
        }

        .btn-primary {
            background: linear-gradient(135deg, #3498db, #2980b9);
            color: white;
            padding: 0.75rem 1.5rem;
            border: none;
            border-radius: 10px;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .btn-primary:hover {
            background: linear-gradient(135deg, #2980b9, #1f5f8b);
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(52, 152, 219, 0.3);
        }

        /* Sección de filtros */
        .filters-section {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            border-radius: 15px;
            padding: 1.5rem;
            margin-bottom: 2rem;
            box-shadow: 0 6px 20px rgba(0, 0, 0, 0.06), 0 2px 8px rgba(0, 0, 0, 0.03);
            border: 1px solid rgba(52, 152, 219, 0.1);
        }

        .search-box {
            position: relative;
            max-width: 400px;
        }

        .search-box i {
            position: absolute;
            left: 1rem;
            top: 50%;
            transform: translateY(-50%);
            color: #7f8c8d;
            font-size: 1.1rem;
        }

        .search-box input {
            width: 100%;
            padding: 1rem 1rem 1rem 3rem;
            border: 2px solid #ecf0f1;
            border-radius: 10px;
            font-size: 1rem;
            transition: all 0.3s ease;
            background: white;
        }

        .search-box input:focus {
            outline: none;
            border-color: #3498db;
            box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
        }

        /* Estadísticas */
        .stats-cards {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2rem;
        }

        .stat-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            border-radius: 15px;
            padding: 2rem;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08), 0 2px 10px rgba(0, 0, 0, 0.04);
            border: 1px solid rgba(52, 152, 219, 0.1);
            display: flex;
            align-items: center;
            gap: 1.5rem;
            transition: all 0.3s ease;
        }

        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 12px 35px rgba(0, 0, 0, 0.12), 0 4px 15px rgba(0, 0, 0, 0.06);
        }

        .stat-icon {
            width: 70px;
            height: 70px;
            background: linear-gradient(135deg, #3498db, #2980b9);
            border-radius: 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 1.8rem;
        }

        .stat-info h3 {
            margin: 0;
            font-size: 2.5rem;
            font-weight: 700;
            color: #2c3e50;
        }

        .stat-info p {
            margin: 0.5rem 0 0 0;
            color: #7f8c8d;
            font-size: 1rem;
            font-weight: 500;
        }

        /* Tabla */
        .table-container {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            border-radius: 15px;
            overflow: hidden;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08), 0 2px 10px rgba(0, 0, 0, 0.04);
            border: 1px solid rgba(52, 152, 219, 0.1);
            margin-bottom: 2rem;
        }

        .data-table {
            width: 100%;
            border-collapse: collapse;
        }

        .data-table th {
            background: #f8f9fa;
            color: #2c3e50;
            font-weight: 700;
            padding: 1.5rem;
            text-align: left;
            border-bottom: 2px solid #ecf0f1;
            font-size: 1rem;
        }

        .data-table td {
            padding: 1.5rem;
            border-bottom: 1px solid #ecf0f1;
            color: #2c3e50;
        }

        .data-table tbody tr:hover {
            background: rgba(52, 152, 219, 0.05);
        }

        .badge {
            padding: 0.5rem 1rem;
            border-radius: 25px;
            font-size: 0.9rem;
            font-weight: 600;
        }

        .badge-info {
            background: linear-gradient(135deg, #e3f2fd, #bbdefb);
            color: #1976d2;
        }

        .action-buttons {
            display: flex;
            gap: 0.75rem;
        }

        .btn-sm {
            padding: 0.75rem;
            border-radius: 8px;
            border: none;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            width: 40px;
            height: 40px;
            font-size: 1rem;
        }

        .btn-outline {
            background: #f8f9fa;
            color: #6c757d;
            border: 2px solid #dee2e6;
        }

        .btn-outline:hover {
            background: #3498db;
            color: white;
            border-color: #3498db;
            transform: translateY(-2px);
        }

        .btn-danger {
            background: linear-gradient(135deg, #e74c3c, #c0392b);
            color: white;
        }

        .btn-danger:hover {
            background: linear-gradient(135deg, #c0392b, #a93226);
            transform: translateY(-2px);
        }

        .no-data {
            text-align: center;
            padding: 4rem;
            color: #7f8c8d;
        }

        .no-data i {
            font-size: 4rem;
            margin-bottom: 1.5rem;
            color: #bdc3c7;
        }

        .no-data p {
            font-size: 1.2rem;
            font-weight: 500;
        }

        /* Paginación */
        .pagination-container {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 1.5rem 2rem;
            background: rgba(248, 249, 250, 0.8);
            border-top: 1px solid #ecf0f1;
        }

        .pagination-info {
            color: #7f8c8d;
            font-size: 1rem;
            font-weight: 500;
        }

        .pagination-controls {
            display: flex;
            align-items: center;
            gap: 1.5rem;
        }

        .page-info {
            color: #2c3e50;
            font-weight: 600;
            font-size: 1rem;
        }

        .btn-secondary {
            background: linear-gradient(135deg, #f8f9fa, #e9ecef);
            color: #6c757d;
            border: 2px solid #dee2e6;
            padding: 0.75rem 1.5rem;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s ease;
            font-weight: 600;
        }

        .btn-secondary:hover:not(:disabled) {
            background: linear-gradient(135deg, #e9ecef, #dee2e6);
            border-color: #adb5bd;
            transform: translateY(-2px);
        }

        .btn-secondary:disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }

        /* Modal */
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.6);
            align-items: center;
            justify-content: center;
        }

        .modal-content {
            background: white;
            border-radius: 20px;
            width: 90%;
            max-width: 500px;
            max-height: 90vh;
            overflow: hidden;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            transform: scale(0.7);
            transition: transform 0.3s ease;
        }

        .modal.show .modal-content {
            transform: scale(1);
        }

        .modal-sm {
            max-width: 400px;
        }

        .modal-header {
            padding: 2rem;
            border-bottom: 2px solid #ecf0f1;
            display: flex;
            justify-content: space-between;
            align-items: center;
            background: #f8f9fa;
        }

        .modal-header h3 {
            margin: 0;
            color: #2c3e50;
            font-size: 1.5rem;
            font-weight: 700;
        }

        .modal-close {
            background: none;
            border: none;
            font-size: 1.8rem;
            color: #7f8c8d;
            cursor: pointer;
            transition: all 0.3s ease;
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .modal-close:hover {
            color: #e74c3c;
            background: rgba(231, 76, 60, 0.1);
        }

        .modal-body {
            padding: 2rem;
            max-height: 60vh;
            overflow-y: auto;
        }

        .modal-footer {
            padding: 1.5rem 2rem;
            border-top: 2px solid #ecf0f1;
            display: flex;
            justify-content: flex-end;
            gap: 1rem;
            background: #f8f9fa;
        }

        .form-group {
            margin-bottom: 2rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.75rem;
            color: #2c3e50;
            font-weight: 600;
            font-size: 1rem;
        }

        .form-control {
            width: 100%;
            padding: 1rem;
            border: 2px solid #ecf0f1;
            border-radius: 10px;
            font-size: 1rem;
            transition: all 0.3s ease;
            background: white;
        }

        .form-control:focus {
            outline: none;
            border-color: #3498db;
            box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
        }

        .form-text {
            font-size: 0.9rem;
            color: #7f8c8d;
            margin-top: 0.5rem;
            font-style: italic;
        }

        .delete-confirmation {
            text-align: center;
            padding: 1rem 0;
        }

        .delete-confirmation i {
            font-size: 4rem;
            color: #f39c12;
            margin-bottom: 1.5rem;
        }

        .delete-confirmation p {
            font-size: 1.1rem;
            margin-bottom: 1rem;
        }

        .warning-text {
            color: #e74c3c;
            font-weight: 600;
            font-size: 1rem;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .container {
                padding: 1rem;
            }

            .page-header {
                flex-direction: column;
                text-align: center;
            }

            .header-left h1 {
                font-size: 1.8rem;
            }

            .stats-cards {
                grid-template-columns: 1fr;
            }

            .data-table {
                font-size: 0.9rem;
            }

            .data-table th,
            .data-table td {
                padding: 1rem 0.5rem;
            }

            .pagination-container {
                flex-direction: column;
                gap: 1rem;
            }

            .modal-content {
                width: 95%;
                margin: 1rem;
            }

            .modal-header,
            .modal-body,
            .modal-footer {
                padding: 1.5rem;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- Header -->
        <div class="page-header">
            <div class="header-left">
                <h1><i class="fas fa-tags"></i> Gestión de Categorías</h1>
                <p>Administra las categorías de productos de tu tienda</p>
            </div>
            <div class="header-right">
                <a href="../dashboard/index.php" class="btn-back">
                    <i class="fas fa-arrow-left"></i> Volver al Dashboard
                </a>
                <button class="btn-primary" id="addCategoryBtn">
                    <i class="fas fa-plus"></i> Nueva Categoría
                </button>
            </div>
        </div>

        <!-- Filtros -->
        <div class="filters-section">
            <div class="search-box">
                <i class="fas fa-search"></i>
                <input type="text" id="categorySearchInput" placeholder="Buscar categorías por nombre o descripción...">
            </div>
        </div>

        <!-- Estadísticas -->
        <div class="stats-cards">
            <div class="stat-card">
                <div class="stat-icon">
                    <i class="fas fa-tags"></i>
                </div>
                <div class="stat-info">
                    <h3 id="totalCategories">0</h3>
                    <p>Total de Categorías</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">
                    <i class="fas fa-star"></i>
                </div>
                <div class="stat-info">
                    <h3 id="mostUsedCategory">-</h3>
                    <p>Categoría Más Popular</p>
                </div>
            </div>
        </div>

        <!-- Tabla de Categorías -->
        <div class="table-container">
            <table class="data-table" id="categoriesTable">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Descripción</th>
                        <th>Productos</th>
                        <th>Fecha Creación</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody id="categoriesTableBody">
                    <tr>
                        <td colspan="6" style="text-align: center; padding: 2rem;">
                            <i class="fas fa-spinner fa-spin" style="font-size: 2rem; color: #3498db;"></i>
                            <p style="margin-top: 1rem; color: #7f8c8d;">Cargando categorías...</p>
                        </td>
                    </tr>
                </tbody>
            </table>

            <!-- Paginación -->
            <div class="pagination-container">
                <div class="pagination-info">
                    <span>Mostrando <span id="showingFrom">0</span> a <span id="showingTo">0</span> de <span id="totalRecords">0</span> registros</span>
                </div>
                <div class="pagination-controls">
                    <button class="btn-secondary" id="prevPage" disabled>
                        <i class="fas fa-chevron-left"></i> Anterior
                    </button>
                    <span class="page-info">
                        Página <span id="currentPage">1</span> de <span id="totalPages">1</span>
                    </span>
                    <button class="btn-secondary" id="nextPage" disabled>
                        Siguiente <i class="fas fa-chevron-right"></i>
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal para Agregar/Editar Categoría -->
    <div class="modal" id="categoryModal">
        <div class="modal-content">
            <div class="modal-header">
                <h3 id="modalTitle">Nueva Categoría</h3>
                <button class="modal-close" id="closeCategoryModal">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <div class="modal-body">
                <form id="categoryForm">
                    <input type="hidden" id="categoryId" name="categoryId">
                    
                    <div class="form-group">
                        <label for="categoryName">Nombre de la Categoría *</label>
                        <input type="text" id="categoryName" name="name" class="form-control" required maxlength="100" placeholder="Ej: Bebidas, Comida, Electrónicos...">
                        <small class="form-text">Ingrese un nombre único para la categoría</small>
                    </div>
                    
                    <div class="form-group">
                        <label for="categoryDescription">Descripción</label>
                        <textarea id="categoryDescription" name="description" class="form-control" rows="4" maxlength="255" placeholder="Descripción opcional de la categoría..."></textarea>
                        <small class="form-text">Descripción opcional que ayude a identificar mejor la categoría</small>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-secondary" id="cancelCategoryBtn">Cancelar</button>
                <button type="submit" form="categoryForm" class="btn-primary" id="saveCategoryBtn">
                    <i class="fas fa-save"></i> Guardar Categoría
                </button>
            </div>
        </div>
    </div>

    <!-- Modal para Confirmar Eliminación -->
    <div class="modal" id="deleteConfirmModal">
        <div class="modal-content modal-sm">
            <div class="modal-header">
                <h3>Confirmar Eliminación</h3>
                <button class="modal-close" id="closeDeleteModal">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <div class="modal-body">
                <div class="delete-confirmation">
                    <i class="fas fa-exclamation-triangle"></i>
                    <p>¿Estás seguro de que deseas eliminar la categoría <strong id="categoryToDelete"></strong>?</p>
                    <p class="warning-text">Esta acción no se puede deshacer.</p>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-secondary" id="cancelDeleteBtn">Cancelar</button>
                <button type="button" class="btn-danger" id="confirmDeleteBtn">
                    <i class="fas fa-trash"></i> Eliminar
                </button>
            </div>
        </div>
    </div>

    <script>
        class CategoriesModule {
            constructor() {
                this.currentPage = 1;
                this.itemsPerPage = 10;
                this.categories = [];
                this.filteredCategories = [];
                this.currentEditingId = null;
                
                this.init();
            }

            init() {
                this.bindEvents();
                this.loadCategories();
            }

            bindEvents() {
                // Modal para agregar categoría
                document.getElementById('addCategoryBtn').addEventListener('click', () => {
                    this.openModal();
                });

                // Cerrar modales
                document.getElementById('closeCategoryModal').addEventListener('click', () => {
                    this.closeModal();
                });
                
                document.getElementById('cancelCategoryBtn').addEventListener('click', () => {
                    this.closeModal();
                });

                document.getElementById('closeDeleteModal').addEventListener('click', () => {
                    this.closeDeleteModal();
                });

                document.getElementById('cancelDeleteBtn').addEventListener('click', () => {
                    this.closeDeleteModal();
                });

                // Formulario de categoría
                document.getElementById('categoryForm').addEventListener('submit', (e) => {
                    e.preventDefault();
                    this.saveCategory();
                });

                // Búsqueda
                document.getElementById('categorySearchInput').addEventListener('input', (e) => {
                    this.filterCategories(e.target.value);
                });

                // Paginación
                document.getElementById('prevPage').addEventListener('click', () => {
                    if (this.currentPage > 1) {
                        this.currentPage--;
                        this.renderTable();
                    }
                });

                document.getElementById('nextPage').addEventListener('click', () => {
                    const totalPages = Math.ceil(this.filteredCategories.length / this.itemsPerPage);
                    if (this.currentPage < totalPages) {
                        this.currentPage++;
                        this.renderTable();
                    }
                });

                // Confirmar eliminación
                document.getElementById('confirmDeleteBtn').addEventListener('click', () => {
                    this.confirmDelete();
                });

                // Cerrar modal al hacer clic fuera
                window.addEventListener('click', (e) => {
                    if (e.target.classList.contains('modal')) {
                        this.closeModal();
                        this.closeDeleteModal();
                    }
                });
            }

            async loadCategories() {
                // Mostrar loader
                document.getElementById('categoriesTableBody').innerHTML = `
                    <tr>
                        <td colspan="6" style="text-align: center; padding: 2rem;">
                            <i class="fas fa-spinner fa-spin" style="font-size: 2rem; color: #3498db;"></i>
                            <p style="margin-top: 1rem; color: #7f8c8d;">Cargando categorías...</p>
                        </td>
                    </tr>
                `;
                
                try {
                    console.log('Cargando categorías...');
                    const response = await fetch('../../Controllers/CategoriesController.php?action=getAll');
                    
                    if (!response.ok) {
                        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                    }
                    
                    const result = await response.json();
                    console.log('Respuesta del servidor:', result);
                    
                    if (result.success) {
                        this.categories = result.data;
                        this.filteredCategories = [...this.categories];
                        this.renderTable();
                        this.updateStats();
                        
                        // Ocultar loader
                        console.log('Categorías cargadas correctamente:', this.categories.length);
                    } else {
                        console.error('Error loading categories:', result.message);
                        this.showError('No se pudieron cargar las categorías: ' + (result.message || 'Error desconocido'));
                    }
                } catch (error) {
                    console.error('Error loading categories:', error);
                    this.showError('Error de conexión al cargar las categorías: ' + error.message);
                    
                    // Mostrar mensaje de error en la tabla
                    document.getElementById('categoriesTableBody').innerHTML = `
                        <tr>
                            <td colspan="6" style="text-align: center; padding: 2rem;">
                                <i class="fas fa-exclamation-triangle" style="font-size: 2rem; color: #e74c3c;"></i>
                                <p style="margin-top: 1rem; color: #e74c3c;">Error al cargar las categorías</p>
                                <button onclick="categoriesModule.loadCategories()" class="btn-primary" style="margin-top: 1rem;">
                                    <i class="fas fa-retry"></i> Reintentar
                                </button>
                            </td>
                        </tr>
                    `;
                }
            }

            filterCategories(searchTerm) {
                if (!searchTerm.trim()) {
                    this.filteredCategories = [...this.categories];
                } else {
                    const term = searchTerm.toLowerCase();
                    this.filteredCategories = this.categories.filter(category => 
                        category.name.toLowerCase().includes(term) ||
                        (category.description && category.description.toLowerCase().includes(term))
                    );
                }
                this.currentPage = 1;
                this.renderTable();
            }

            renderTable() {
                const tbody = document.getElementById('categoriesTableBody');
                const startIndex = (this.currentPage - 1) * this.itemsPerPage;
                const endIndex = startIndex + this.itemsPerPage;
                const pageCategories = this.filteredCategories.slice(startIndex, endIndex);

                tbody.innerHTML = '';

                if (pageCategories.length === 0) {
                    tbody.innerHTML = `
                        <tr>
                            <td colspan="6" style="text-align: center;">
                                <div class="no-data">
                                    <i class="fas fa-tags"></i>
                                    <p>No se encontraron categorías</p>
                                </div>
                            </td>
                        </tr>
                    `;
                } else {
                    pageCategories.forEach(category => {
                        const row = document.createElement('tr');
                        row.innerHTML = `
                            <td><strong>#${category.id}</strong></td>
                            <td>
                                <strong style="color: #3498db;">${category.name}</strong>
                            </td>
                            <td>${category.description || '<em style="color: #7f8c8d;">Sin descripción</em>'}</td>
                            <td>
                                <span class="badge badge-info">${category.product_count || 0} productos</span>
                            </td>
                            <td>${this.formatDate(category.created_at)}</td>
                            <td>
                                <div class="action-buttons">
                                    <button class="btn-sm btn-outline" onclick="categoriesModule.editCategory(${category.id})" title="Editar categoría">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <button class="btn-sm btn-danger" onclick="categoriesModule.deleteCategory(${category.id}, '${category.name}')" title="Eliminar categoría">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </div>
                            </td>
                        `;
                        tbody.appendChild(row);
                    });
                }

                this.updatePagination();
            }

            updatePagination() {
                const totalPages = Math.ceil(this.filteredCategories.length / this.itemsPerPage);
                const startIndex = (this.currentPage - 1) * this.itemsPerPage;
                const endIndex = Math.min(startIndex + this.itemsPerPage, this.filteredCategories.length);

                document.getElementById('currentPage').textContent = this.currentPage;
                document.getElementById('totalPages').textContent = totalPages;
                document.getElementById('showingFrom').textContent = this.filteredCategories.length > 0 ? startIndex + 1 : 0;
                document.getElementById('showingTo').textContent = endIndex;
                document.getElementById('totalRecords').textContent = this.filteredCategories.length;

                document.getElementById('prevPage').disabled = this.currentPage <= 1;
                document.getElementById('nextPage').disabled = this.currentPage >= totalPages;
            }

            updateStats() {
                const totalCategories = this.categories.length;
                const mostUsed = this.categories.length > 0 
                    ? this.categories.reduce((max, cat) => (cat.product_count || 0) > (max.product_count || 0) ? cat : max)
                    : null;

                document.getElementById('totalCategories').textContent = totalCategories;
                document.getElementById('mostUsedCategory').textContent = mostUsed ? mostUsed.name : '-';
            }

            openModal(category = null) {
                const modal = document.getElementById('categoryModal');
                const modalTitle = document.getElementById('modalTitle');
                const form = document.getElementById('categoryForm');

                if (category) {
                    modalTitle.textContent = 'Editar Categoría';
                    document.getElementById('categoryId').value = category.id;
                    document.getElementById('categoryName').value = category.name;
                    document.getElementById('categoryDescription').value = category.description || '';
                    this.currentEditingId = category.id;
                } else {
                    modalTitle.textContent = 'Nueva Categoría';
                    form.reset();
                    document.getElementById('categoryId').value = '';
                    this.currentEditingId = null;
                }

                modal.style.display = 'flex';
                setTimeout(() => modal.classList.add('show'), 10);
                
                // Focus en el campo nombre
                document.getElementById('categoryName').focus();
            }

            closeModal() {
                const modal = document.getElementById('categoryModal');
                modal.classList.remove('show');
                setTimeout(() => {
                    modal.style.display = 'none';
                    document.getElementById('categoryForm').reset();
                    this.currentEditingId = null;
                }, 300);
            }

            async editCategory(id) {
                try {
                    const response = await fetch(`../../Controllers/CategoriesController.php?action=getById&id=${id}`);
                    const result = await response.json();
                    
                    if (result.success) {
                        this.openModal(result.data);
                    } else {
                        this.showError('No se pudo cargar la categoría: ' + result.message);
                    }
                } catch (error) {
                    console.error('Error loading category:', error);
                    this.showError('Error de conexión al cargar la categoría');
                }
            }

            deleteCategory(id, name) {
                this.categoryToDeleteId = id;
                document.getElementById('categoryToDelete').textContent = name;
                const modal = document.getElementById('deleteConfirmModal');
                modal.style.display = 'flex';
                setTimeout(() => modal.classList.add('show'), 10);
            }

            closeDeleteModal() {
                const modal = document.getElementById('deleteConfirmModal');
                modal.classList.remove('show');
                setTimeout(() => {
                    modal.style.display = 'none';
                    this.categoryToDeleteId = null;
                }, 300);
            }

            async confirmDelete() {
                if (!this.categoryToDeleteId) return;

                try {
                    const response = await fetch(`../../Controllers/CategoriesController.php?action=delete&id=${this.categoryToDeleteId}`, {
                        method: 'DELETE'
                    });
                    const result = await response.json();
                    
                    if (result.success) {
                        this.showSuccess('Categoría eliminada correctamente');
                        this.closeDeleteModal();
                        this.loadCategories();
                    } else {
                        this.showError('No se pudo eliminar la categoría: ' + result.message);
                    }
                } catch (error) {
                    console.error('Error deleting category:', error);
                    this.showError('Error de conexión al eliminar la categoría');
                }
            }

            async saveCategory() {
                const form = document.getElementById('categoryForm');
                const formData = new FormData(form);
                
                const categoryData = {
                    name: formData.get('name').trim(),
                    description: formData.get('description').trim()
                };

                if (!categoryData.name) {
                    this.showError('El nombre de la categoría es requerido');
                    return;
                }

                try {
                    const isEdit = this.currentEditingId !== null;
                    const url = isEdit 
                        ? `../../Controllers/CategoriesController.php?action=update&id=${this.currentEditingId}`
                        : '../../Controllers/CategoriesController.php?action=create';
                    
                    const method = isEdit ? 'PUT' : 'POST';

                    const response = await fetch(url, {
                        method: method,
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify(categoryData)
                    });

                    const result = await response.json();
                    
                    if (result.success) {
                        this.showSuccess(result.message);
                        this.closeModal();
                        this.loadCategories();
                    } else {
                        this.showError(result.message);
                    }
                } catch (error) {
                    console.error('Error saving category:', error);
                    this.showError('Error de conexión al guardar la categoría');
                }
            }

            formatDate(dateString) {
                const date = new Date(dateString);
                return date.toLocaleDateString('es-ES', {
                    year: 'numeric',
                    month: '2-digit',
                    day: '2-digit',
                    hour: '2-digit',
                    minute: '2-digit'
                });
            }

            showSuccess(message) {
                Swal.fire('¡Éxito!', message, 'success');
            }

            showError(message) {
                Swal.fire('Error', message, 'error');
            }
        }

        // Inicializar el módulo cuando se carga la página
        let categoriesModule;
        document.addEventListener('DOMContentLoaded', function() {
            console.log('DOM cargado, inicializando módulo de categorías...');
            categoriesModule = new CategoriesModule();
        });
    </script>
</body>
</html>
