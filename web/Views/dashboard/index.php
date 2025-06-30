<?php
// Dashboard principal del sistema Punto-D
session_start();

// Verificar si el usuario está logueado
if (!isset($_SESSION['logged_in']) || !$_SESSION['logged_in']) {
    header('Location: ../auth/login.php');
    exit();
}

// Obtener datos del usuario de la sesión
$username = $_SESSION['username'] ?? 'Usuario';
$email = $_SESSION['email'] ?? '';
$userId = $_SESSION['user_id'] ?? '';

// Obtener fecha y hora actual
$currentDate = date('d/m/Y');
$currentTime = date('H:i:s');
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Punto de Venta - Punto-D</title>
    <link rel="stylesheet" href="../../Assets/css/dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
    <div class="pos-container">
        <!-- Mobile Menu Toggle -->
        <button class="mobile-menu-toggle" id="mobileMenuToggle">
            <i class="fas fa-bars"></i>
        </button>

        <!-- Sidebar -->
        <div class="sidebar" id="sidebar">
            <div class="sidebar-header">
                <div class="logo-header">
                    <img src="data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAiIGhlaWdodD0iMzAiIHZpZXdCb3g9IjAgMCA0MCA0MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTIwIDQwQzMxLjA0NTcgNDAgNDAgMzEuMDQ1NyA0MCAyMEM0MCA4Ljk1NDMgMzEuMDQ1NyAwIDIwIDBDOC45NTQzIDAgMCA4Ljk1NDMgMCAyMEMwIDMxLjA0NTcgOC45NTQzIDQwIDIwIDQwWiIgZmlsbD0iIzAwMDAwMCIvPgo8cGF0aCBkPSJNMjAgMzZDMjguODM2NiAzNiAzNiAyOC44MzY2IDM2IDIwQzM2IDExLjE2MzQgMjguODM2NiA0IDIwIDRDMTEuMTYzNCA0IDQgMTEuMTYzNCA0IDIwQzQgMjguODM2NiAxMS4xNjM0IDM2IDIwIDM2WiIgZmlsbD0iI0ZGRkZGRiIvPgo8ZWxsaXBzZSBjeD0iMTUiIGN5PSIxNS41IiByeD0iMyIgcnk9IjQiIGZpbGw9IiMwMDAwMDAiLz4KPGVsbGlwc2UgY3g9IjI1IiBjeT0iMTUuNSIgcng9IjMiIHJ5PSI0IiBmaWxsPSIjMDAwMDAwIi8+CjxwYXRoIGQ9Ik0xMiAyNEMxNCAxOCAyNiAxOCAyOCAyNEMyNiAyOCAxNCAyOCAxMiAyNCIgZmlsbD0iI0ZGQjAwMCIvPgo8cGF0aCBkPSJNOCAzMkM0IDI4IDggMjQgMTYgMjZDMTQgMzAgMTAgMzIgOCAzMloiIGZpbGw9IiNGRkIwMDAiLz4KPHBhdGggZD0iTTMyIDMyQzM2IDI4IDMyIDI0IDI0IDI2QzI2IDMwIDMwIDMyIDMyIDMyWiIgZmlsbD0iI0ZGQjAwMCIvPgo8L3N2Zz4K" alt="Tux Logo" class="tux-logo-dashboard">
                    <h2>Punto de venta</h2>
                </div>
                <div class="user-info">
                    <div class="user-avatar">
                        <i class="fas fa-user"></i>
                    </div>
                    <span class="username"><?php echo htmlspecialchars($username); ?></span>
                </div>
            </div>
            
            <nav class="sidebar-nav">
                <ul>
                    <li class="nav-item active">
                        <a href="#" data-module="pos">
                            <i class="fas fa-cash-register"></i>
                            <span>Punto Venta</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="#" data-module="products">
                            <i class="fas fa-box"></i>
                            <span>Productos</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="#" data-module="inventory">
                            <i class="fas fa-warehouse"></i>
                            <span>Inventario</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="#" data-module="sales">
                            <i class="fas fa-chart-line"></i>
                            <span>Ventas</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="#" data-module="customers">
                            <i class="fas fa-users"></i>
                            <span>Clientes</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="#" data-module="reports">
                            <i class="fas fa-chart-bar"></i>
                            <span>Reportes</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="#" data-module="settings">
                            <i class="fas fa-cog"></i>
                            <span>Configuración</span>
                        </a>
                    </li>
                </ul>
            </nav>
            
            <div class="sidebar-footer">
                <a href="../auth/logout.php" class="logout-btn">
                    <i class="fas fa-sign-out-alt" style="width: 20px; margin-right: 0.8rem;"></i>
                    <span>Cerrar Sesión</span>
                </a>
            </div>
        </div>

        <!-- Mobile Overlay -->
        <div class="mobile-overlay" id="mobileOverlay"></div>

        <!-- Main Content -->
        <div class="main-content">
            <!-- Header -->
            <div class="content-header">
                <div class="header-left">
                    <h1>Punto de Venta</h1>
                    <div class="breadcrumb">
                        <span>Dashboard</span> > <span>Punto de Venta</span>
                    </div>
                </div>
                <div class="header-right">
                    <div class="datetime">
                        <span class="date"><?php echo $currentDate; ?></span>
                        <span class="time" id="currentTime"><?php echo $currentTime; ?></span>
                    </div>
                    <div class="user-actions">
                        <button class="btn-icon" title="Configuración">
                            <i class="fas fa-cog"></i>
                        </button>
                        <button class="btn-icon" title="Ayuda">
                            <i class="fas fa-question-circle"></i>
                        </button>
                    </div>
                </div>
            </div>

            <!-- POS Interface -->
            <div class="pos-interface">
                <!-- Products Section -->
                <div class="products-section">
                    <div class="products-header">
                        <h3>Productos</h3>
                        <div class="search-box">
                            <i class="fas fa-search"></i>
                            <input type="text" placeholder="Buscar productos..." id="productSearch">
                        </div>
                    </div>
                    
                    <div class="categories" id="categoriesContainer">
                        <button class="category-btn active" data-category="all">Todos</button>
                        <!-- Las categorías se cargarán dinámicamente -->
                    </div>
                    
                    <div class="products-grid" id="productsGrid">
                        <!-- Los productos se cargarán dinámicamente -->
                        <div class="loading-message">
                            <i class="fas fa-spinner fa-spin"></i>
                            <p>Cargando productos...</p>
                        </div>
                    </div>
                    
                    <!-- Paginación de productos -->
                    <div class="pagination-container" id="paginationContainer" style="display: none;">
                        <div class="pagination-info">
                            <span id="paginationInfo">Página 1 de 1</span>
                        </div>
                        <div class="pagination-controls">
                            <button class="pagination-btn" id="prevPageBtn" disabled>
                                <i class="fas fa-chevron-left"></i>
                                Anterior
                            </button>
                            <div class="page-numbers" id="pageNumbers">
                                <!-- Los números de página se generarán dinámicamente -->
                            </div>
                            <button class="pagination-btn" id="nextPageBtn" disabled>
                                Siguiente
                                <i class="fas fa-chevron-right"></i>
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Cart Section -->
                <div class="cart-section" id="cartSection">
                    <div class="cart-header" id="cartHeader">
                        <div class="cart-title">
                            <h3>Carrito de Compras</h3>
                            <button class="cart-expand-btn" id="cartExpandBtn" title="Expandir/Contraer carrito">
                                <i class="fas fa-expand-alt"></i>
                            </button>
                        </div>
                        <button class="clear-cart" id="clearCart">
                            <i class="fas fa-trash"></i>
                            Limpiar
                        </button>
                    </div>
                    
                    <div class="cart-items" id="cartItems">
                        <div class="empty-cart">
                            <i class="fas fa-shopping-cart"></i>
                            <p>Carrito vacío</p>
                            <span>Agrega productos para comenzar</span>
                        </div>
                    </div>
                    
                    <div class="cart-summary">
                        <div class="summary-row">
                            <span>Subtotal:</span>
                            <span id="subtotal">$0.00</span>
                        </div>
                        <div class="summary-row">
                            <span>IVA (16%):</span>
                            <span id="tax">$0.00</span>
                        </div>
                        <div class="summary-row total">
                            <span>Total:</span>
                            <span id="total">$0.00</span>
                        </div>
                        
                        <div class="payment-section">
                            <h4>Método de Pago</h4>
                            <div class="payment-methods">
                                <button class="payment-btn active" data-method="cash">
                                    <i class="fas fa-money-bill"></i>
                                    Efectivo
                                </button>
                                <button class="payment-btn" data-method="card">
                                    <i class="fas fa-credit-card"></i>
                                    Tarjeta
                                </button>
                            </div>
                            
                            <div class="payment-input">
                                <label for="amountPaid">Monto recibido:</label>
                                <input type="number" id="amountPaid" placeholder="0.00" step="0.01">
                                <div class="change-display">
                                    <span>Cambio: $</span><span id="change">0.00</span>
                                </div>
                            </div>
                        </div>
                        
                        <button class="checkout-btn" id="checkoutBtn" disabled>
                            <i class="fas fa-cash-register"></i>
                            Procesar Venta
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        // Variables globales
        let cart = [];
        let selectedPaymentMethod = 'cash';
        let products = [];
        let categories = [];
        let cartExpanded = false; // Estado del carrito expandido
        
        // Variables de paginación
        let currentPage = 1;
        let itemsPerPage = 9;
        let totalPages = 1;
        let filteredProducts = [];

        // === FUNCIONES DE UTILIDAD (DEFINIDAS PRIMERO) ===
        
        // Toast notifications para feedback rápido
        function showSuccessToast(message) {
            // Crear toast element
            const toast = document.createElement('div');
            toast.className = 'toast-notification success';
            toast.innerHTML = `
                <i class="fas fa-check-circle"></i>
                <span>${message}</span>
            `;
            
            document.body.appendChild(toast);
            
            // Mostrar toast
            setTimeout(() => toast.classList.add('show'), 100);
            
            // Ocultar y remover toast
            setTimeout(() => {
                toast.classList.remove('show');
                setTimeout(() => {
                    if (document.body.contains(toast)) {
                        document.body.removeChild(toast);
                    }
                }, 300);
            }, 2000);
        }
        
        function showErrorToast(message) {
            const toast = document.createElement('div');
            toast.className = 'toast-notification error';
            toast.innerHTML = `
                <i class="fas fa-exclamation-circle"></i>
                <span>${message}</span>
            `;
            
            document.body.appendChild(toast);
            
            setTimeout(() => toast.classList.add('show'), 100);
            
            setTimeout(() => {
                toast.classList.remove('show');
                setTimeout(() => {
                    if (document.body.contains(toast)) {
                        document.body.removeChild(toast);
                    }
                }, 300);
            }, 2000);
        }

        // Funciones de notificación con SweetAlert2
        function showError(message) {
            Swal.fire({
                title: 'Error',
                text: message,
                icon: 'error',
                confirmButtonText: 'Entendido',
                confirmButtonColor: '#e74c3c',
                background: '#fff',
                color: '#333',
                customClass: {
                    container: 'swal-container'
                }
            });
        }

        function showSuccess(message) {
            Swal.fire({
                title: '¡Éxito!',
                text: message,
                icon: 'success',
                confirmButtonText: 'Perfecto',
                confirmButtonColor: '#27ae60',
                timer: 3000,
                timerProgressBar: true,
                showConfirmButton: true,
                background: '#fff',
                color: '#333',
                customClass: {
                    container: 'swal-container'
                }
            });
        }

        function showInfo(message) {
            Swal.fire({
                title: 'Información',
                text: message,
                icon: 'info',
                confirmButtonText: 'Entendido',
                confirmButtonColor: '#3498db',
                background: '#fff',
                color: '#333',
                customClass: {
                    container: 'swal-container'
                }
            });
        }

        function showConfirmation(title, text, callback) {
            Swal.fire({
                title: title,
                text: text,
                icon: 'question',
                showCancelButton: true,
                confirmButtonText: 'Sí, continuar',
                cancelButtonText: 'Cancelar',
                confirmButtonColor: '#3498db',
                cancelButtonColor: '#95a5a6',
                background: '#fff',
                color: '#333',
                customClass: {
                    container: 'swal-container'
                }
            }).then((result) => {
                if (result.isConfirmed && callback) {
                    callback();
                }
            });
        }

        // Función para expandir/contraer carrito en móvil y tablet
        function toggleCartExpansion() {
            const cartSection = document.getElementById('cartSection');
            const productsSection = document.querySelector('.products-section');
            const cartExpandBtn = document.getElementById('cartExpandBtn');
            const expandIcon = cartExpandBtn.querySelector('i');
            
            // Funciona en móvil y tablet (hasta 768px)
            if (window.innerWidth <= 768) {
                cartExpanded = !cartExpanded;
                
                if (cartExpanded) {
                    cartSection.classList.add('expanded');
                    productsSection.classList.add('minimized');
                    cartExpandBtn.classList.add('expanded');
                    cartExpandBtn.title = 'Contraer carrito';
                    expandIcon.className = 'fas fa-compress-alt';
                    showSuccessToast('📈 Carrito expandido');
                } else {
                    cartSection.classList.remove('expanded');
                    productsSection.classList.remove('minimized');
                    cartExpandBtn.classList.remove('expanded');
                    cartExpandBtn.title = 'Expandir carrito';
                    expandIcon.className = 'fas fa-expand-alt';
                    showSuccessToast('📉 Carrito contraído');
                }
            }
        }

        // Listener para el botón de expansión del carrito (móvil y tablet)
        function setupCartExpansion() {
            const cartExpandBtn = document.getElementById('cartExpandBtn');
            
            // Mostrar en móvil y tablet (hasta 768px)
            if (window.innerWidth <= 768) {
                cartExpandBtn.style.display = 'flex';
                cartExpandBtn.addEventListener('click', (e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    toggleCartExpansion();
                });
            } else {
                cartExpandBtn.style.display = 'none';
            }
        }

        // Configurar responsividad al cambiar tamaño de ventana
        window.addEventListener('resize', () => {
            if (window.innerWidth > 768) {
                // Resetear en pantallas grandes (desktop)
                const cartSection = document.getElementById('cartSection');
                const productsSection = document.querySelector('.products-section');
                const cartExpandBtn = document.getElementById('cartExpandBtn');
                
                cartSection.classList.remove('expanded');
                productsSection.classList.remove('minimized');
                cartExpandBtn.classList.remove('expanded');
                cartExpandBtn.style.display = 'none';
                cartExpanded = false;
            }
            setupCartExpansion();
        });

        // Actualizar la hora cada segundo
        function updateTime() {
            const now = new Date();
            const timeString = now.toLocaleTimeString('es-ES', {
                hour12: false,
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            });
            document.getElementById('currentTime').textContent = timeString;
        }

        // Actualizar cada segundo
        setInterval(updateTime, 1000);

        // Función simple de bienvenida como fallback
        function showSimpleWelcome() {
            const username = '<?php echo addslashes($username); ?>';
            const currentHour = new Date().getHours();
            let greeting = currentHour < 12 ? 'Buenos días' : currentHour < 18 ? 'Buenas tardes' : 'Buenas noches';
            
            Swal.fire({
                title: greeting + ', ' + username + '!',
                text: 'Bienvenido al Sistema Punto-D',
                icon: 'success',
                confirmButtonText: 'Empezar',
                confirmButtonColor: '#667eea',
                timer: 3000,
                timerProgressBar: true
            });
        }

        // Animación de bienvenida mejorada
        function showWelcomeAnimation() {
            const username = '<?php echo addslashes($username); ?>';
            const currentHour = new Date().getHours();
            let greeting, icon;
            
            if (currentHour < 12) {
                greeting = 'Buenos días';
                icon = '🌅';
            } else if (currentHour < 18) {
                greeting = 'Buenas tardes';
                icon = '☀️';
            } else {
                greeting = 'Buenas noches';
                icon = '🌙';
            }
            
            Swal.fire({
                title: greeting + ', ' + username + '!',
                html: '<div style="text-align: center; padding: 2rem;"><div style="font-size: 3rem; margin-bottom: 1rem;">' + icon + '</div><p style="font-size: 1.2rem; color: #667eea; font-weight: 600;">Bienvenido al Sistema Punto-D</p><p style="color: #7f8c8d; margin-top: 1rem;">¡Que tengas un día productivo!</p></div>',
                icon: 'success',
                confirmButtonText: '¡Empezar a vender!',
                confirmButtonColor: '#667eea',
                timer: 4000,
                timerProgressBar: true,
                allowOutsideClick: true
            }).then((result) => {
                if (result.isConfirmed) {
                    if (typeof showSuccessToast === 'function') {
                        showSuccessToast('¡Sistema listo para usar! 🎉');
                    }
                }
            });
        }

        // Configurar menú móvil
        function setupMobileMenu() {
            const mobileToggle = document.getElementById('mobileMenuToggle');
            const sidebar = document.getElementById('sidebar');
            const overlay = document.getElementById('mobileOverlay');
            
            // Toggle menú móvil
            mobileToggle.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                toggleMobileMenu();
            });
            
            // Cerrar menú al tocar overlay
            overlay.addEventListener('click', function() {
                closeMobileMenu();
            });
            
            // Cerrar menú al hacer click en un link del menú (solo en móvil)
            const navLinks = sidebar.querySelectorAll('.nav-item a');
            navLinks.forEach(link => {
                link.addEventListener('click', function() {
                    // Solo cerrar en móvil/tablet
                    if (window.innerWidth <= 992) {
                        setTimeout(() => closeMobileMenu(), 150); // Pequeño delay para mejor UX
                    }
                });
            });
            
            // Cerrar menú al redimensionar ventana (si cambia a desktop)
            window.addEventListener('resize', function() {
                if (window.innerWidth > 992) {
                    closeMobileMenu();
                }
            });
            
            // Cerrar menú con tecla Escape
            document.addEventListener('keydown', function(e) {
                if (e.key === 'Escape' && sidebar.classList.contains('active')) {
                    closeMobileMenu();
                }
            });
            
            // Soporte para gestos de swipe en móvil
            let touchStartX = null;
            let touchStartY = null;
            
            sidebar.addEventListener('touchstart', function(e) {
                touchStartX = e.touches[0].clientX;
                touchStartY = e.touches[0].clientY;
            }, { passive: true });
            
            sidebar.addEventListener('touchmove', function(e) {
                if (!touchStartX || !touchStartY) return;
                
                const touchEndX = e.touches[0].clientX;
                const touchEndY = e.touches[0].clientY;
                
                const deltaX = touchStartX - touchEndX;
                const deltaY = touchStartY - touchEndY;
                
                // Solo procesar si es un swipe horizontal
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    // Swipe hacia la izquierda para cerrar
                    if (deltaX > 50) {
                        closeMobileMenu();
                    }
                }
            }, { passive: true });
            
            sidebar.addEventListener('touchend', function() {
                touchStartX = null;
                touchStartY = null;
            }, { passive: true });
            
            // Función para toggle del menú móvil
            function toggleMobileMenu() {
                const isActive = sidebar.classList.contains('active');
                
                if (isActive) {
                    closeMobileMenu();
                } else {
                    openMobileMenu();
                }
            }
            
            // Función para abrir el menú móvil
            function openMobileMenu() {
                sidebar.classList.add('active');
                overlay.classList.add('active');
                mobileToggle.classList.add('active');
                document.body.style.overflow = 'hidden'; // Prevenir scroll del body
                
                const icon = mobileToggle.querySelector('i');
                icon.className = 'fas fa-times';
            }
            
            // Función para cerrar el menú móvil
            function closeMobileMenu() {
                sidebar.classList.remove('active');
                overlay.classList.remove('active');
                mobileToggle.classList.remove('active');
                document.body.style.overflow = ''; // Restaurar scroll del body
                
                const icon = mobileToggle.querySelector('i');
                icon.className = 'fas fa-bars';
            }
        }

        // Funcionalidad del POS
        document.addEventListener('DOMContentLoaded', function() {
            // Cargar datos iniciales primero
            loadCategories();
            loadProducts();
            
            // Configurar menú móvil
            setupMobileMenu();
            
            // Configurar funcionalidad del carrito expandible
            setupCartExpansion();
            
            // Configurar paginación
            setupPagination();
            
            // Mostrar animación de bienvenida después de un pequeño delay
            setTimeout(function() {
                try {
                    showWelcomeAnimation();
                } catch (error) {
                    console.error('Error en animación avanzada:', error);
                    showSimpleWelcome();
                }
            }, 1500);
            
            // Event listener para limpiar carrito
            document.getElementById('clearCart').addEventListener('click', clearCart);

            // Event listeners para métodos de pago
            const paymentBtns = document.querySelectorAll('.payment-btn');
            paymentBtns.forEach(btn => {
                btn.addEventListener('click', function() {
                    paymentBtns.forEach(b => b.classList.remove('active'));
                    this.classList.add('active');
                    selectedPaymentMethod = this.dataset.method;
                });
            });

            // Event listener para monto recibido
            document.getElementById('amountPaid').addEventListener('input', calculateChange);

            // Event listener para botón de checkout
            document.getElementById('checkoutBtn').addEventListener('click', processCheckout);

            // Event listener para búsqueda
            document.getElementById('productSearch').addEventListener('input', function() {
                searchProducts(this.value);
            });

            // Event listeners para navegación del sidebar
            const navItems = document.querySelectorAll('.nav-item');
            navItems.forEach(item => {
                item.addEventListener('click', function(e) {
                    e.preventDefault();
                    navItems.forEach(i => i.classList.remove('active'));
                    this.classList.add('active');
                    
                    const module = this.querySelector('a').dataset.module;
                    switchModule(module);
                });
            });
        });

        // Cargar categorías desde la base de datos
        async function loadCategories() {
            try {
                const response = await fetch('../../Controllers/ProductsController.php?action=categories');
                const data = await response.json();
                
                if (data.success) {
                    categories = data.data;
                    renderCategories();
                } else {
                    console.error('Error al cargar categorías:', data.message);
                }
            } catch (error) {
                console.error('Error de red al cargar categorías:', error);
            }
        }

        // Renderizar categorías
        function renderCategories() {
            const container = document.getElementById('categoriesContainer');
            const allButton = container.querySelector('[data-category="all"]');
            
            // Limpiar categorías existentes excepto "Todos"
            const existingCategories = container.querySelectorAll('.category-btn:not([data-category="all"])');
            existingCategories.forEach(btn => btn.remove());
            
            // Agregar nuevas categorías
            categories.forEach(category => {
                const button = document.createElement('button');
                button.className = 'category-btn';
                button.dataset.category = category.id;
                button.textContent = category.name;
                button.addEventListener('click', function() {
                    document.querySelectorAll('.category-btn').forEach(b => b.classList.remove('active'));
                    this.classList.add('active');
                    filterProductsByCategory(category.id);
                });
                container.appendChild(button);
            });
            
            // Event listener para "Todos"
            allButton.addEventListener('click', function() {
                document.querySelectorAll('.category-btn').forEach(b => b.classList.remove('active'));
                this.classList.add('active');
                renderProducts(products);
            });
        }

        // Cargar productos desde la base de datos
        async function loadProducts() {
            try {
                const response = await fetch('../../Controllers/ProductsController.php?action=list');
                const data = await response.json();
                
                if (data.success) {
                    products = data.data;
                    renderProducts(products);
                } else {
                    console.error('Error al cargar productos:', data.message);
                    showError('Error al cargar productos');
                }
            } catch (error) {
                console.error('Error de red al cargar productos:', error);
                showError('Error de conexión al cargar productos');
            }
        }

        // Renderizar productos con paginación
        function renderProducts(productList) {
            filteredProducts = productList;
            totalPages = Math.ceil(filteredProducts.length / itemsPerPage);
            
            // Asegurar que currentPage esté en rango válido
            if (currentPage > totalPages) currentPage = 1;
            if (currentPage < 1) currentPage = 1;
            
            const startIndex = (currentPage - 1) * itemsPerPage;
            const endIndex = startIndex + itemsPerPage;
            const currentProducts = filteredProducts.slice(startIndex, endIndex);
            
            const grid = document.getElementById('productsGrid');
            
            if (filteredProducts.length === 0) {
                grid.innerHTML = `
                    <div class="no-products">
                        <i class="fas fa-box-open"></i>
                        <p>No hay productos disponibles</p>
                    </div>
                `;
                hidePagination();
                return;
            }
            
            grid.innerHTML = currentProducts.map(product => `
                <div class="product-card" data-id="${product.id}" data-name="${product.name}" data-price="${product.price}" data-category="${product.category_id}" data-stock="${product.stock}">
                    <div class="product-image">
                        ${getProductIcon(product.category_name)}
                    </div>
                    <div class="product-info">
                        <h4>${product.name}</h4>
                        <p class="price">$${parseFloat(product.price).toFixed(2)}</p>
                        <p class="stock ${product.stock <= product.min_stock ? 'low-stock' : ''}">
                            Stock: ${product.stock}
                            ${product.stock <= product.min_stock ? '<i class="fas fa-exclamation-triangle"></i>' : ''}
                        </p>
                    </div>
                </div>
            `).join('');
            
            // Agregar event listeners a las tarjetas
            const productCards = grid.querySelectorAll('.product-card');
            productCards.forEach(card => {
                card.addEventListener('click', function() {
                    const productId = this.dataset.id;
                    const productName = this.dataset.name;
                    const productPrice = parseFloat(this.dataset.price);
                    const productStock = parseInt(this.dataset.stock);
                    
                    if (productStock > 0) {
                        addToCart(productId, productName, productPrice);
                    } else {
                        showError('Producto sin stock disponible');
                    }
                });
            });
            
            // Actualizar paginación
            updatePagination();
        }

        // Obtener icono según la categoría
        function getProductIcon(categoryName) {
            const icons = {
                'Bebidas': '<i class="fas fa-wine-bottle"></i>',
                'Snacks': '<i class="fas fa-cookie-bite"></i>',
                'Dulces': '<i class="fas fa-candy-cane"></i>',
                'Otros': '<i class="fas fa-box"></i>'
            };
            return icons[categoryName] || '<i class="fas fa-box"></i>';
        }

        // Filtrar productos por categoría
        async function filterProductsByCategory(categoryId) {
            currentPage = 1; // Resetear a primera página
            
            try {
                const response = await fetch(`../../Controllers/ProductsController.php?action=by_category&category_id=${categoryId}`);
                const data = await response.json();
                
                if (data.success) {
                    renderProducts(data.data);
                } else {
                    console.error('Error al filtrar productos:', data.message);
                }
            } catch (error) {
                console.error('Error de red al filtrar productos:', error);
            }
        }

        // Buscar productos
        async function searchProducts(searchTerm) {
            currentPage = 1; // Resetear a primera página
            
            if (searchTerm.trim() === '') {
                renderProducts(products);
                // Resetear categoría a "Todos"
                document.querySelectorAll('.category-btn').forEach(b => b.classList.remove('active'));
                document.querySelector('.category-btn[data-category="all"]').classList.add('active');
                return;
            }
            
            try {
                const response = await fetch(`../../Controllers/ProductsController.php?action=search&q=${encodeURIComponent(searchTerm)}`);
                const data = await response.json();
                
                if (data.success) {
                    renderProducts(data.data);
                    // Resetear categoría a "Todos" cuando se busca
                    document.querySelectorAll('.category-btn').forEach(b => b.classList.remove('active'));
                    document.querySelector('.category-btn[data-category="all"]').classList.add('active');
                } else {
                    console.error('Error al buscar productos:', data.message);
                }
            } catch (error) {
                console.error('Error de red al buscar productos:', error);
            }
        }

        // Agregar producto al carrito con feedback visual
        function addToCart(id, name, price) {
            const existingItem = cart.find(item => item.id === id);
            const product = products.find(p => p.id == id);
            
            if (!product) {
                showError('Producto no encontrado');
                return;
            }
            
            // Verificar stock disponible
            const currentQuantityInCart = existingItem ? existingItem.quantity : 0;
            if (currentQuantityInCart >= product.stock) {
                showError('No hay suficiente stock disponible');
                return;
            }
            
            // Agregar al carrito
            if (existingItem) {
                existingItem.quantity++;
            } else {
                cart.push({
                    id: id,
                    name: name,
                    price: price,
                    quantity: 1
                });
            }
            
            // Feedback visual - encontrar la tarjeta del producto
            const productCard = document.querySelector(`[data-id="${id}"]`);
            if (productCard) {
                productCard.style.transform = 'scale(0.95)';
                productCard.style.backgroundColor = '#e8f5e8';
                
                setTimeout(() => {
                    productCard.style.transform = '';
                    productCard.style.backgroundColor = '';
                }, 200);
            }
            
            // Mostrar toast de éxito
            showSuccessToast(`${name} agregado al carrito`);
            
            updateCartDisplay();
            updateCartSummary();
        }
        // Actualizar visualización del carrito
        function updateCartDisplay() {
            const cartItems = document.getElementById('cartItems');
            
            if (cart.length === 0) {
                cartItems.innerHTML = `
                    <div class="empty-cart">
                        <i class="fas fa-shopping-cart"></i>
                        <p>Carrito vacío</p>
                        <span>Agrega productos para comenzar</span>
                    </div>
                `;
                return;
            }
            
            cartItems.innerHTML = cart.map(item => `
                <div class="cart-item">
                    <div class="item-info">
                        <h5>${item.name}</h5>
                        <div class="item-price">$${item.price.toFixed(2)} c/u</div>
                    </div>
                    <div class="item-controls">
                        <div class="qty-controls">
                            <button class="qty-btn" onclick="updateQuantity('${item.id}', -1)">-</button>
                            <span class="quantity">${item.quantity}</span>
                            <button class="qty-btn" onclick="updateQuantity('${item.id}', 1)">+</button>
                        </div>
                        <button class="qty-btn remove-btn" onclick="removeFromCart('${item.id}')" title="Eliminar producto">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
            `).join('');
        }

        // Actualizar cantidad con validaciones mejoradas
        function updateQuantity(id, change) {
            const item = cart.find(item => item.id === id);
            const product = products.find(p => p.id == id);
            
            if (item && product) {
                const newQuantity = item.quantity + change;
                
                if (newQuantity <= 0) {
                    removeFromCart(id);
                } else if (newQuantity > product.stock) {
                    showErrorToast(`Stock máximo disponible: ${product.stock}`);
                } else {
                    item.quantity = newQuantity;
                    updateCartDisplay();
                    updateCartSummary();
                    
                    // Feedback visual
                    if (change > 0) {
                        showSuccessToast(`Cantidad aumentada: ${item.name}`);
                    }
                }
            }
        }

        // Remover del carrito con confirmación
        function removeFromCart(id) {
            const item = cart.find(item => item.id === id);
            if (!item) return;
            
            showConfirmation(
                '¿Eliminar producto?',
                `¿Estás seguro de que quieres eliminar "${item.name}" del carrito?`,
                function() {
                    cart = cart.filter(item => item.id !== id);
                    updateCartDisplay();
                    updateCartSummary();
                    showSuccessToast('Producto eliminado del carrito');
                }
            );
        }

        // Limpiar carrito con confirmación
        function clearCart() {
            if (cart.length === 0) {
                showInfo('El carrito ya está vacío');
                return;
            }
            
            showConfirmation(
                '¿Limpiar carrito?',
                'Se eliminarán todos los productos del carrito. Esta acción no se puede deshacer.',
                function() {
                    cart = [];
                    updateCartDisplay();
                    updateCartSummary();
                    document.getElementById('amountPaid').value = '';
                    document.getElementById('change').textContent = '0.00';
                    showSuccess('Carrito limpiado correctamente');
                }
            );
        }

        // Actualizar resumen del carrito
        function updateCartSummary() {
            const subtotal = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
            const tax = subtotal * 0.16;
            const total = subtotal + tax;
            
            document.getElementById('subtotal').textContent = `$${subtotal.toFixed(2)}`;
            document.getElementById('tax').textContent = `$${tax.toFixed(2)}`;
            document.getElementById('total').textContent = `$${total.toFixed(2)}`;
            
            // Habilitar/deshabilitar botón de checkout
            const checkoutBtn = document.getElementById('checkoutBtn');
            checkoutBtn.disabled = cart.length === 0;
            
            calculateChange();
        }

        // Calcular cambio con mejores validaciones móviles
        function calculateChange() {
            const total = parseFloat(document.getElementById('total').textContent.replace('$', ''));
            const amountPaidInput = document.getElementById('amountPaid');
            const amountPaid = parseFloat(amountPaidInput.value) || 0;
            const change = Math.max(0, amountPaid - total); // Nunca negativo
            
            document.getElementById('change').textContent = change.toFixed(2);
            
            // Actualizar el estado del botón de checkout con validaciones mejoradas
            const checkoutBtn = document.getElementById('checkoutBtn');
            if (!checkoutBtn) return;
            
            let shouldEnable = cart.length > 0;
            
            if (selectedPaymentMethod === 'cash') {
                shouldEnable = shouldEnable && amountPaid >= total;
                
                // Feedback visual para el input en efectivo
                if (amountPaid > 0) {
                    if (amountPaid < total) {
                        amountPaidInput.style.borderColor = '#e74c3c';
                        amountPaidInput.style.backgroundColor = '#fdf2f2';
                    } else {
                        amountPaidInput.style.borderColor = '#27ae60';
                        amountPaidInput.style.backgroundColor = '#f2fdf2';
                    }
                } else {
                    amountPaidInput.style.borderColor = '#ddd';
                    amountPaidInput.style.backgroundColor = 'white';
                }
            } else {
                // Para tarjeta, resetear estilos del input
                amountPaidInput.style.borderColor = '#ddd';
                amountPaidInput.style.backgroundColor = '#f8f9fa';
            }
            
            checkoutBtn.disabled = !shouldEnable;
            
            // Cambiar texto del botón según estado
            if (cart.length === 0) {
                checkoutBtn.innerHTML = '<i class="fas fa-shopping-cart"></i> Carrito Vacío';
            } else if (selectedPaymentMethod === 'cash' && amountPaid < total) {
                checkoutBtn.innerHTML = '<i class="fas fa-exclamation-triangle"></i> Monto Insuficiente';
            } else {
                checkoutBtn.innerHTML = '<i class="fas fa-cash-register"></i> Procesar Venta';
            }
        }

        // Procesar venta con loading state y mejor compatibilidad móvil
        async function processCheckout() {
            try {
                if (cart.length === 0) {
                    showError('El carrito está vacío');
                    return;
                }
                
                const total = parseFloat(document.getElementById('total').textContent.replace('$', ''));
                const amountPaidInput = document.getElementById('amountPaid');
                const amountPaid = parseFloat(amountPaidInput.value) || 0;
                
                // Validaciones específicas para Android/móvil
                if (selectedPaymentMethod === 'cash' && amountPaid < total) {
                    showError('El monto recibido es insuficiente');
                    // Enfocar el input en móvil
                    amountPaidInput.focus();
                    amountPaidInput.select();
                    return;
                }
                
                // Obtener referencia al botón
                const checkoutBtn = document.getElementById('checkoutBtn');
                if (!checkoutBtn) {
                    showError('Error interno: Botón no encontrado');
                    return;
                }
                
                // Prevenir doble click
                if (checkoutBtn.disabled) {
                    return;
                }
                
                // Mostrar loading state
                const originalText = checkoutBtn.innerHTML;
                checkoutBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Procesando...';
                checkoutBtn.disabled = true;
                checkoutBtn.classList.add('btn-loading');
                
                // Deshabilitar inputs durante procesamiento
                amountPaidInput.disabled = true;
                
                // Preparar datos de la venta
                const saleData = {
                    items: cart.map(item => ({
                        id: item.id,
                        name: item.name,
                        price: parseFloat(item.price),
                        quantity: parseInt(item.quantity),
                        total: parseFloat(item.price) * parseInt(item.quantity)
                    })),
                    subtotal: parseFloat(document.getElementById('subtotal').textContent.replace('$', '')),
                    tax: parseFloat(document.getElementById('tax').textContent.replace('$', '')),
                    total: total,
                    paymentMethod: selectedPaymentMethod,
                    amountPaid: selectedPaymentMethod === 'cash' ? amountPaid : total,
                    change: selectedPaymentMethod === 'cash' ? Math.max(0, amountPaid - total) : 0,
                    timestamp: new Date().toISOString()
                };
                
                // Agregar timeout para conexiones lentas en Android
                const controller = new AbortController();
                const timeoutId = setTimeout(() => controller.abort(), 15000);
                
                // Enviar venta al servidor
                const response = await fetch('../../Controllers/SalesController.php', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify(saleData),
                    signal: controller.signal
                });
                
                clearTimeout(timeoutId);
                
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                
                const result = await response.json();
                
                
                if (result && result.success) {
                    // Mostrar mensaje de éxito personalizado
                    const paymentMethodText = selectedPaymentMethod === 'cash' ? 'Efectivo' : 'Tarjeta';
                    
                    await Swal.fire({
                        title: '¡Venta Procesada!',
                        html: `
                            <div style="text-align: left; margin: 1rem 0; background: #f8f9fa; padding: 1rem; border-radius: 8px;">
                                <p style="margin: 0.5rem 0;"><strong>Ticket:</strong> #${result.sale_id || 'N/A'}</p>
                                <p style="margin: 0.5rem 0;"><strong>Total:</strong> $${total.toFixed(2)}</p>
                                <p style="margin: 0.5rem 0;"><strong>Método:</strong> ${paymentMethodText}</p>
                                ${selectedPaymentMethod === 'cash' ? `<p style="margin: 0.5rem 0;"><strong>Recibido:</strong> $${amountPaid.toFixed(2)}</p>` : ''}
                                ${selectedPaymentMethod === 'cash' ? `<p style="margin: 0.5rem 0;"><strong>Cambio:</strong> $${saleData.change.toFixed(2)}</p>` : ''}
                            </div>
                        `,
                        icon: 'success',
                        confirmButtonText: 'Imprimir Ticket',
                        confirmButtonColor: '#27ae60',
                        showCancelButton: true,
                        cancelButtonText: 'Continuar',
                        cancelButtonColor: '#3498db',
                        background: '#fff',
                        color: '#333',
                        allowOutsideClick: false,
                        allowEscapeKey: false,
                        customClass: {
                            popup: 'mobile-optimized-popup'
                        }
                    }).then((result) => {
                        if (result.isConfirmed) {
                            // Aquí podrías implementar la funcionalidad de impresión
                            showInfo('Funcionalidad de impresión - Próximamente');
                        }
                    });
                    
                    // Limpiar carrito
                    cart = [];
                    updateCartDisplay();
                    updateCartSummary();
                    amountPaidInput.value = '';
                    document.getElementById('change').textContent = '0.00';
                    
                    // Recargar productos para actualizar stock
                    await loadProducts();
                    
                } else {
                    throw new Error(result?.message || 'Error desconocido al procesar venta');
                }
                
            } catch (error) {
                console.error('Error al procesar venta:', error);
                
                if (error.name === 'AbortError') {
                    showError('Tiempo de espera agotado. Verifica tu conexión e intenta nuevamente.');
                } else if (error.message.includes('NetworkError') || error.message.includes('fetch')) {
                    showError('Error de conexión. Verifica tu internet e intenta nuevamente.');
                } else {
                    showError('Error al procesar venta: ' + error.message);
                }
                
            } finally {
                // Restaurar botón y inputs siempre
                try {
                    checkoutBtn.innerHTML = originalText;
                    checkoutBtn.disabled = cart.length === 0;
                    checkoutBtn.classList.remove('btn-loading');
                    amountPaidInput.disabled = false;
                } catch (e) {
                    console.error('Error al restaurar botón:', e);
                }
            }
        }

        // Cambiar módulo (placeholder para funcionalidad futura)
        function switchModule(module) {
            console.log('Switching to module:', module);
            
            // Placeholder para diferentes módulos
            switch(module) {
                case 'pos':
                    // Ya estamos en el módulo POS
                    break;
                case 'products':
                    showInfo('Módulo de Productos - Próximamente');
                    break;
                case 'inventory':
                    showInfo('Módulo de Inventario - Próximamente');
                    break;
                case 'sales':
                    showInfo('Módulo de Ventas - Próximamente');
                    break;
                case 'customers':
                    showInfo('Módulo de Clientes - Próximamente');
                    break;
                case 'reports':
                    showInfo('Módulo de Reportes - Próximamente');
                    break;
                case 'settings':
                    showInfo('Módulo de Configuración - Próximamente');
                    break;
            }
        }

        // === FUNCIONES DE PAGINACIÓN ===
        
        function updatePagination() {
            const container = document.getElementById('paginationContainer');
            const info = document.getElementById('paginationInfo');
            const prevBtn = document.getElementById('prevPageBtn');
            const nextBtn = document.getElementById('nextPageBtn');
            const pageNumbers = document.getElementById('pageNumbers');
            
            if (totalPages <= 1) {
                hidePagination();
                return;
            }
            
            // Mostrar contenedor
            container.style.display = 'block';
            
            // Actualizar información
            info.textContent = `Página ${currentPage} de ${totalPages} (${filteredProducts.length} productos)`;
            
            // Actualizar botones prev/next
            prevBtn.disabled = currentPage <= 1;
            nextBtn.disabled = currentPage >= totalPages;
            
            // Generar números de página
            generatePageNumbers();
        }
        
        function generatePageNumbers() {
            const pageNumbers = document.getElementById('pageNumbers');
            pageNumbers.innerHTML = '';
            
            const maxVisiblePages = 5;
            let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
            let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);
            
            // Ajustar si estamos cerca del final
            if (endPage - startPage < maxVisiblePages - 1) {
                startPage = Math.max(1, endPage - maxVisiblePages + 1);
            }
            
            // Botón primera página
            if (startPage > 1) {
                addPageButton(1);
                if (startPage > 2) {
                    addEllipsis();
                }
            }
            
            // Páginas visibles
            for (let i = startPage; i <= endPage; i++) {
                addPageButton(i);
            }
            
            // Botón última página
            if (endPage < totalPages) {
                if (endPage < totalPages - 1) {
                    addEllipsis();
                }
                addPageButton(totalPages);
            }
        }
        
        function addPageButton(pageNum) {
            const pageNumbers = document.getElementById('pageNumbers');
            const btn = document.createElement('button');
            btn.className = 'page-number';
            btn.textContent = pageNum;
            
            if (pageNum === currentPage) {
                btn.classList.add('active');
            }
            
            btn.addEventListener('click', () => goToPage(pageNum));
            pageNumbers.appendChild(btn);
        }
        
        function addEllipsis() {
            const pageNumbers = document.getElementById('pageNumbers');
            const ellipsis = document.createElement('div');
            ellipsis.className = 'page-ellipsis';
            ellipsis.textContent = '...';
            pageNumbers.appendChild(ellipsis);
        }
        
        function goToPage(page) {
            if (page < 1 || page > totalPages) return;
            
            currentPage = page;
            renderProducts(filteredProducts);
            
            // Scroll suave al inicio de productos
            const productsSection = document.querySelector('.products-section');
            productsSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
        
        function hidePagination() {
            const container = document.getElementById('paginationContainer');
            container.style.display = 'none';
        }
        
        // Eventos de paginación
        function setupPagination() {
            const prevBtn = document.getElementById('prevPageBtn');
            const nextBtn = document.getElementById('nextPageBtn');
            
            prevBtn.addEventListener('click', () => {
                if (currentPage > 1) {
                    goToPage(currentPage - 1);
                }
            });
            
            nextBtn.addEventListener('click', () => {
                if (currentPage < totalPages) {
                    goToPage(currentPage + 1);
                }
            });
        }

        // Función simple de bienvenida como fallback final
        function showSimpleWelcome() {
            const username = '<?php echo addslashes($username); ?>';
            const currentHour = new Date().getHours();
            let greeting = currentHour < 12 ? 'Buenos días' : currentHour < 18 ? 'Buenas tardes' : 'Buenas noches';
            
            Swal.fire({
                title: `¡${greeting}, ${username}!`,
                text: 'Bienvenido al Sistema Punto-D',
                icon: 'success',
                confirmButtonText: 'Empezar',
                confirmButtonColor: '#667eea',
                timer: 3000,
                timerProgressBar: true
            });
        }
    </script>
</body>
</html>
