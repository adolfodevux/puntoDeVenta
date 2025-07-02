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

// Controlar mensaje de bienvenida - solo una vez por sesión
$showWelcome = !isset($_SESSION['welcome_shown']);
if ($showWelcome) {
    $_SESSION['welcome_shown'] = true;
}

// Obtener clientes registrados en sesión para el selector del POS
$clientes_pos = isset($_SESSION['clientes']) ? $_SESSION['clientes'] : [];

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
                         <img src="../../Assets/img/tux.png" alt="Tux Logo" class="tux-logo-img" width="40px">
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
                        <a href="#" data-module="inventory">
                           <i class="fas fa-box"></i>
                            <span>Inventario</span>
                        </a>
                    </li>
                     <li class="nav-item">
                        <a href="#" data-module="suppliers">
                            <i class="fas fa-truck"></i>
                            <span>Proveedores</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="#" data-module="categories">
                           <i class="fas fa-tags"></i>
                            <span>Categorías</span>
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
                   
                    
                    <li class="nav-item logout-nav-item">
                        <a href="../auth/logout.php" class="logout-btn quick-logout">
                            <i class="fas fa-sign-out-alt"></i>
                            <span>Cerrar Sesión</span>
                        </a>
                    </li>
                </ul>
            </nav>
            
            <!-- Eliminar el div.sidebar-footer que contiene el botón de logout duplicado -->
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
                            <label for="clienteSearch" style="font-weight:600;">Cliente <span style="color:#888;font-weight:normal;">(opcional)</span>:</label>
                            <div class="cliente-search-container" style="position: relative;">
                                <input 
                                    type="text" 
                                    id="clienteSearch" 
                                    placeholder="Buscar por nombre o ID (opcional)..." 
                                    style="width:100%;padding:7px 10px;border-radius:6px;border:1.5px solid #b2c9e6;margin-bottom:5px;"
                                    autocomplete="off"
                                >
                                <div id="clienteSuggestions" class="cliente-suggestions" style="display:none;"></div>
                                <div id="selectedClienteDisplay" style="display:none; padding:5px; background:#e8f5e8; border-radius:4px; font-size:0.85rem; margin-bottom:5px;">
                                    <span id="selectedClienteText"></span>
                                    <button type="button" onclick="clearSelectedCliente()" style="float:right; background:none; border:none; color:#666; cursor:pointer;">✕</button>
                                </div>
                            </div>
                        </div>
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
                            <h4>Pago en Efectivo</h4>
                            
                            <div class="payment-input" id="cashPaymentInput" style="display: block;">
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
        // === PUNTO DE VENTA - SCRIPT INICIADO ===
        console.log('=== PUNTO DE VENTA - SCRIPT INICIADO ===');
        
        // Variables globales
        let cart = [];
        let selectedPaymentMethod = 'cash'; // Solo efectivo
        let products = [];
        let categories = [];
        let cartExpanded = false; // Estado del carrito expandido
        let selectedCliente = '';
        
        // Variables de paginación
        let currentPage = 1;
        let itemsPerPage = 9; // Cambiado a 9 productos por página
        let totalPages = 1;
        let filteredProducts = [];
        
        console.log('Variables globales inicializadas');

        // === FUNCIONES DE UTILIDAD (DEFINIDAS PRIMERO) ===
        
        // Escuchar cambios de productos desde inventario y recargar productos automáticamente
        window.addEventListener('storage', function(e) {
            if (e.key === 'pos_products_updated') {
                // Recargar productos y categorías en el POS
                loadCategories();
                loadProducts();
                showSuccessToast('🆕 Productos actualizados');
            }
        });

        // Toast notifications para feedback rápido
        function showSuccessToast(message) {
            console.log('showSuccessToast:', message);
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
            console.log('showErrorToast:', message);
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

        function showWarningToast(message) {
            console.log('showWarningToast:', message);
            const toast = document.createElement('div');
            toast.className = 'toast-notification warning';
            toast.innerHTML = `
                <i class="fas fa-exclamation-triangle"></i>
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

        // Función simple de bienvenida mejorada
        function showSimpleWelcome() {
            const username = '<?php echo addslashes($username); ?>';
            const currentHour = new Date().getHours();
            const currentDate = new Date().toLocaleDateString('es-ES', { 
                weekday: 'long', 
                year: 'numeric', 
                month: 'long', 
                day: 'numeric' 
            });
            
            let greeting, icon, bgGradient, timeMessage;
            
            if (currentHour >= 5 && currentHour < 12) {
                greeting = 'Buenos días';
                icon = '🌅';
                bgGradient = 'linear-gradient(135deg, #2c3e50 0%, #34495e 100%)';
                timeMessage = 'Que tengas un excelente día';
            } else if (currentHour >= 12 && currentHour < 18) {
                greeting = 'Buenas tardes';
                icon = '☀️';
                bgGradient = 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)';
                timeMessage = 'Espero que estés teniendo un gran día';
            } else {
                greeting = 'Buenas noches';
                icon = '🌙';
                bgGradient = 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)';
                timeMessage = 'Que tengas una productiva noche';
            }
            
            Swal.fire({
                title: `${greeting}, ${username}! ${icon}`,
                html: `
                    <div style="text-align: center; padding: 1.5rem 0;">
                        <div style="font-size: 4rem; margin-bottom: 1rem; line-height: 1;">
                            🎯
                        </div>
                        <h3 style="color: #2c3e50; margin: 0 0 0.5rem 0; font-weight: 600;">
                            ¡Bienvenido al Sistema POS!
                        </h3>
                        <p style="color: #7f8c8d; margin: 0 0 1rem 0; font-size: 0.95rem;">
                            ${timeMessage}
                        </p>
                        <div style="background: ${bgGradient}; color: white; padding: 1rem; border-radius: 12px; margin: 1rem 0;">
                            <p style="margin: 0; font-weight: 500; font-size: 0.9rem;">
                                📅 ${currentDate}
                            </p>
                        </div>
                        <div style="background: #f8f9fa; padding: 1rem; border-radius: 8px; border-left: 4px solid #3498db;">
                            <p style="margin: 0; color: #2c3e50; font-size: 0.85rem;">
                                💡 <strong>Todo listo para comenzar</strong><br>
                                El sistema está funcionando perfectamente
                            </p>
                        </div>
                    </div>
                `,
                icon: 'success',
                confirmButtonText: '🚀 ¡Empezar a vender!',
                confirmButtonColor: '#3498db',
                timer: 4000,
                timerProgressBar: true,
                allowOutsideClick: true,
                customClass: {
                    popup: 'welcome-popup',
                    title: 'welcome-title',
                    confirmButton: 'welcome-confirm-btn'
                },
                showClass: {
                    popup: 'animate__animated animate__fadeInDown animate__faster'
                },
                hideClass: {
                    popup: 'animate__animated animate__fadeOutUp animate__faster'
                }
            }).then(() => {
                if (typeof showSuccessToast === 'function') {
                    showSuccessToast('🎉 ¡Sistema listo para vender!');
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
            console.log('=== DOM CARGADO - INICIANDO APLICACIÓN ===');
            
            try {
                // Mostrar bienvenida solo una vez por sesión
                <?php if ($showWelcome): ?>
                console.log('Mostrando bienvenida...');
                showSimpleWelcome();
                <?php else: ?>
                console.log('Bienvenida ya mostrada en esta sesión');
                <?php endif; ?>
                
                // Configurar menú móvil
                console.log('Configurando menú móvil...');
                setupMobileMenu();
                
                // Configurar funcionalidad del carrito expandible
                console.log('Configurando carrito...');
                setupCartExpansion();
                
                // Configurar paginación
                console.log('Configurando paginación...');
                setupPagination();
                
                // Cargar datos iniciales
                console.log('Cargando categorías y productos...');
                loadCategories();
                loadProducts();
                
                // Event listeners básicos
                console.log('Configurando event listeners...');
                
                // Event listener para limpiar carrito
                const clearCartBtn = document.getElementById('clearCart');
                if (clearCartBtn) {
                    console.log('✅ clearCartBtn encontrado');
                    clearCartBtn.addEventListener('click', function(e) {
                        e.preventDefault();
                        console.log('clearCart clicked');
                        clearCart();
                    });
                } else {
                    console.error('❌ clearCartBtn NO encontrado');
                }

                // Solo efectivo - sin event listeners para métodos de pago

                // Event listener para monto recibido
                const amountPaidEl = document.getElementById('amountPaid');
                if (amountPaidEl) {
                    console.log('✅ amountPaidEl encontrado');
                    amountPaidEl.addEventListener('input', function() {
                        console.log('Amount paid changed:', this.value);
                        calculateChange();
                    });
                } else {
                    console.error('❌ amountPaidEl NO encontrado');
                }

                // Event listener para botón de checkout
                const checkoutBtn = document.getElementById('checkoutBtn');
                if (checkoutBtn) {
                    console.log('✅ checkoutBtn encontrado');
                    checkoutBtn.addEventListener('click', function(e) {
                        e.preventDefault();
                        console.log('Checkout button clicked');
                        processCheckout();
                    });
                } else {
                    console.error('❌ checkoutBtn NO encontrado');
                }

                // Event listener para búsqueda
                const searchEl = document.getElementById('productSearch');
                if (searchEl) {
                    console.log('✅ productSearch encontrado');
                    searchEl.addEventListener('input', function() {
                        console.log('Search input:', this.value);
                        searchProducts(this.value);
                    });
                } else {
                    console.error('❌ productSearch NO encontrado');
                }

                // Event listener para búsqueda de clientes
                const clienteSearchEl = document.getElementById('clienteSearch');
                if (clienteSearchEl) {
                    console.log('✅ clienteSearch encontrado');
                    setupClienteSearch();
                } else {
                    console.error('❌ clienteSearch NO encontrado');
                }

                // Event listeners para navegación del sidebar (excluyendo logout)
                const navItems = document.querySelectorAll('.nav-item:not(.logout-nav-item)');
                console.log('🧭 nav-items encontrados (sin logout):', navItems.length);
                navItems.forEach(item => {
                    item.addEventListener('click', function(e) {
                        e.preventDefault();
                        console.log('Nav item clicked');
                        navItems.forEach(i => i.classList.remove('active'));
                        this.classList.add('active');
                        
                        const module = this.querySelector('a').dataset.module;
                        console.log('Switching to module:', module);
                        switchModule(module);
                    });
                });
                
                // Event listener específico para el botón de logout
                const logoutNavItem = document.querySelector('.logout-nav-item');
                if (logoutNavItem) {
                    console.log('✅ logoutNavItem encontrado');
                    logoutNavItem.addEventListener('click', function(e) {
                        e.preventDefault();
                        console.log('Logout nav item clicked');
                        handleLogout();
                    });
                } else {
                    console.error('❌ logoutNavItem NO encontrado');
                }
                
                console.log('=== APLICACIÓN INICIADA CORRECTAMENTE ===');
                
                // Verificar elementos críticos después de un momento
                setTimeout(() => {
                    console.log('=== VERIFICACIÓN DE ELEMENTOS ===');
                    console.log('clearCart:', document.getElementById('clearCart') ? '✅' : '❌');
                    console.log('amountPaid:', document.getElementById('amountPaid') ? '✅' : '❌');
                    console.log('checkoutBtn:', document.getElementById('checkoutBtn') ? '✅' : '❌');
                    console.log('productSearch:', document.getElementById('productSearch') ? '✅' : '❌');
                    console.log('payment-btn count:', document.querySelectorAll('.payment-btn').length);
                    
                    // Ejecutar prueba automática del sistema
                    const systemWorking = testSystemFunctionality();
                    if (!systemWorking) {
                        console.warn('⚠️ Sistema con problemas, intentando reinicializar...');
                        setTimeout(reinitializeSystem, 1000);
                    }
                }, 2000);
                
            } catch (error) {
                console.error('Error en inicialización:', error);
                showErrorToast('Error en inicialización: ' + error.message);
            }
        });

        // Cargar categorías desde la base de datos
        async function loadCategories() {
            console.log('loadCategories() iniciada');
            try {
                const response = await fetch('../../Controllers/ProductsController.php?action=categories');
                if (!response.ok) throw new Error('No se pudo conectar al servidor');
                const data = await response.json();
                console.log('Categorías recibidas:', data);
                if (data.success) {
                    categories = data.data;
                    renderCategories();
                } else {
                    showErrorToast('Error al cargar categorías: ' + (data.message || 'Respuesta inválida'));
                }
            } catch (error) {
                showErrorToast('Error de conexión al cargar categorías: ' + error.message);
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
            console.log('loadProducts() iniciada');
            try {
                const response = await fetch('../../Controllers/ProductsController.php?action=list');
                if (!response.ok) throw new Error('No se pudo conectar al servidor');
                const data = await response.json();
                console.log('Productos recibidos:', data);
                if (data.success) {
                    products = data.data;
                    renderProducts(products);
                } else {
                    showErrorToast('Error al cargar productos: ' + (data.message || 'Respuesta inválida'));
                }
            } catch (error) {
                console.error('Error en loadProducts:', error);
                showErrorToast('Error de conexión al cargar productos: ' + error.message);
            }
        }

        // --- Renderizado de productos mejorado y seguro ---
        function renderProducts(productList) {
            filteredProducts = productList;
            totalPages = Math.ceil(filteredProducts.length / itemsPerPage);
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
            const isMobile = window.innerWidth <= 768;
            grid.innerHTML = currentProducts.map(product => {
                const stock = parseInt(product.stock) || 0;
                const isLowStock = stock <= 30; // Advertencia cuando stock es 30 o menos
                const stockBadge = `<span class="stock-badge${isLowStock ? ' low-stock' : ''}">Stock: ${stock} ${isLowStock ? '<i class=\'fas fa-exclamation-triangle\'></i>' : ''}</span>`;
                return `
                    <div class="product-card" data-id="${product.id}" data-name="${product.name}" data-price="${product.price}" data-category="${product.category_id}" data-stock="${stock}">
                        <div class="product-image">
                            ${getProductIcon(product.category_name)}
                        </div>
                        <div class="product-info">
                            <h4>${product.name}</h4>
                            <div class="product-info-row">
                                <span class="price">$${parseFloat(product.price).toFixed(2)}</span>
                                ${stockBadge}
                            </div>
                        </div>
                    </div>
                `;
            }).join('');
            // Agregar event listeners a las tarjetas
            const productCards = grid.querySelectorAll('.product-card');
            productCards.forEach(card => {
                card.addEventListener('click', function() {
                    const id = this.getAttribute('data-id');
                    const name = this.getAttribute('data-name');
                    const price = this.getAttribute('data-price');
                    const category = this.getAttribute('data-category');
                    const stock = this.getAttribute('data-stock');
                    addToCart(id, name, price, category, stock);
                });
            });
            renderPagination();
        }

        // Configurar paginación
        function setupPagination() {
            console.log('setupPagination() iniciada');
            const prevBtn = document.getElementById('prevPageBtn');
            const nextBtn = document.getElementById('nextPageBtn');
            
            if (prevBtn) {
                prevBtn.addEventListener('click', () => {
                    if (currentPage > 1) {
                        currentPage--;
                        renderProducts(filteredProducts);
                    }
                });
            }
            
            if (nextBtn) {
                nextBtn.addEventListener('click', () => {
                    if (currentPage < totalPages) {
                        currentPage++;
                        renderProducts(filteredProducts);
                    }
                });
            }
        }

        // Renderizar paginación
        function renderPagination() {
            const paginationContainer = document.getElementById('paginationContainer');
            const paginationInfo = document.getElementById('paginationInfo');
            const prevBtn = document.getElementById('prevPageBtn');
            const nextBtn = document.getElementById('nextPageBtn');
            const pageNumbers = document.getElementById('pageNumbers');
            
            if (!paginationContainer) return;
            
            if (totalPages <= 1) {
                paginationContainer.style.display = 'none';
                return;
            }
            
            paginationContainer.style.display = 'block';
            
            // Actualizar información
            if (paginationInfo) {
                paginationInfo.textContent = `Página ${currentPage} de ${totalPages}`;
            }
            
            // Actualizar botones
            if (prevBtn) {
                prevBtn.disabled = currentPage === 1;
            }
            if (nextBtn) {
                nextBtn.disabled = currentPage === totalPages;
            }
            
            // Renderizar números de página
            if (pageNumbers) {
                pageNumbers.innerHTML = '';
                const maxVisiblePages = 5;
                let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
                let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);
                
                if (endPage - startPage + 1 < maxVisiblePages) {
                    startPage = Math.max(1, endPage - maxVisiblePages + 1);
                }
                
                for (let i = startPage; i <= endPage; i++) {
                    const pageBtn = document.createElement('button');
                    pageBtn.className = `page-number ${i === currentPage ? 'active' : ''}`;
                    pageBtn.textContent = i;
                    pageBtn.addEventListener('click', () => {
                        currentPage = i;
                        renderProducts(filteredProducts);
                    });
                    pageNumbers.appendChild(pageBtn);
                }
            }
        }

        // Ocultar paginación
        function hidePagination() {
            const paginationContainer = document.getElementById('paginationContainer');
            if (paginationContainer) {
                paginationContainer.style.display = 'none';
            }
        }

        // Obtener icono del producto basado en la categoría
        function getProductIcon(categoryName) {
            const iconMap = {
                'bebidas': '<i class="fas fa-wine-bottle product-icon"></i>',
                'comida': '<i class="fas fa-hamburger product-icon"></i>',
                'snacks': '<i class="fas fa-cookie-bite product-icon"></i>',
                'dulces': '<i class="fas fa-candy-cane product-icon"></i>',
                'lacteos': '<i class="fas fa-cheese product-icon"></i>',
                'carnes': '<i class="fas fa-drumstick-bite product-icon"></i>',
                'frutas': '<i class="fas fa-apple-alt product-icon"></i>',
                'verduras': '<i class="fas fa-carrot product-icon"></i>',
                'panaderia': '<i class="fas fa-bread-slice product-icon"></i>',
                'limpieza': '<i class="fas fa-spray-can product-icon"></i>',
                'higiene': '<i class="fas fa-soap product-icon"></i>',
                'electronica': '<i class="fas fa-mobile-alt product-icon"></i>',
                'hogar': '<i class="fas fa-home product-icon"></i>',
                'ropa': '<i class="fas fa-tshirt product-icon"></i>',
                'juguetes': '<i class="fas fa-puzzle-piece product-icon"></i>',
                'libros': '<i class="fas fa-book product-icon"></i>',
                'deportes': '<i class="fas fa-football-ball product-icon"></i>',
                'salud': '<i class="fas fa-pills product-icon"></i>',
                'belleza': '<i class="fas fa-spa product-icon"></i>',
                'oficina': '<i class="fas fa-paperclip product-icon"></i>'
            };
            
            if (!categoryName) {
                return '<i class="fas fa-box product-icon"></i>';
            }
            
            const normalizedCategory = categoryName.toLowerCase().trim();
            return iconMap[normalizedCategory] || '<i class="fas fa-box product-icon"></i>';
        }

        // Buscar productos
        function searchProducts(query = '') {
            console.log('Buscando productos:', query);
            if (!query.trim()) {
                renderProducts(products);
                return;
            }
            
            const filtered = products.filter(product => 
                product.name.toLowerCase().includes(query.toLowerCase()) ||
                (product.category_name && product.category_name.toLowerCase().includes(query.toLowerCase()))
            );
            
            currentPage = 1;
            renderProducts(filtered);
        }

        // Filtrar productos por categoría
        function filterProductsByCategory(categoryId) {
            console.log('Filtrando por categoría:', categoryId);
            if (categoryId === 'all') {
                renderProducts(products);
                return;
            }
            
            const filtered = products.filter(product => product.category_id == categoryId);
            currentPage = 1;
            renderProducts(filtered);
        }

        // Función para agregar productos al carrito
        function addToCart(id, name, price, category, stock) {
            // Asegurar tipos numéricos
            price = parseFloat(price);
            stock = parseInt(stock);
            // Verificar si el producto ya está en el carrito
            const existingItem = cart.find(item => item.id === id);
            if (existingItem) {
                if (existingItem.stock > 0) {
                    existingItem.quantity = parseInt(existingItem.quantity) + 1;
                    existingItem.stock = parseInt(existingItem.stock) - 1;
                    showSuccessToast(`✔️ ${name} agregado al carrito`);
                } else {
                    showErrorToast(`❌ ${name} sin stock disponible`);
                }
            } else {
                if (stock > 0) {
                    cart.push({
                        id,
                        name,
                        price: price,
                        category,
                        stock: stock - 1,
                        quantity: 1
                    });
                    showSuccessToast(`✔️ ${name} agregado al carrito`);
                } else {
                    showErrorToast(`❌ ${name} sin stock disponible`);
                }
            }
            renderCart();
            calculateSubtotal();
            updateCheckoutBtn();
        }

        // Función para limpiar el carrito
        function clearCart() {
            if (cart.length === 0) {
                showWarningToast('🛒 El carrito ya está vacío');
                return;
            }
            
            // Limpiar directamente sin confirmación
            cart = [];
            renderCart();
            calculateSubtotal();
            
            // Limpiar campos de pago
            document.getElementById('amountPaid').value = '';
            document.getElementById('change').textContent = '0.00';
            
            updateCheckoutBtn();
            showSuccessToast('🗑️ Carrito limpiado correctamente');
        }

        // Función para eliminar un producto específico del carrito
        function removeFromCart(id) {
            const itemIndex = cart.findIndex(item => item.id === id);
            if (itemIndex !== -1) {
                const item = cart[itemIndex];
                cart.splice(itemIndex, 1);
                renderCart();
                calculateSubtotal();
                updateCheckoutBtn();
                showSuccessToast(`🗑️ ${item.name} eliminado del carrito`);
            }
        }

       
        function renderCart() {
            const cartItemsContainer = document.getElementById('cartItems');
            cartItemsContainer.innerHTML = '';
            
            if (cart.length === 0) {
                cartItemsContainer.innerHTML = `
                    <div class="empty-cart">
                        <i class="fas fa-shopping-cart"></i>
                        <p>Carrito vacío</p>
                        <span>Agrega productos para comenzar</span>
                    </div>
                `;
                updateCheckoutBtn();
                return;
            }
            
            cart.forEach(item => {
                const cartItem = document.createElement('div');
                cartItem.className = 'cart-item';
                cartItem.dataset.id = item.id;
                cartItem.innerHTML = `
                    <div class="cart-item-header">
                        <div class="cart-item-info">
                            <span class="cart-item-name" title="${item.name}">${item.name}</span>
                            <span class="cart-item-price">$${item.price.toFixed(2)} c/u</span>
                        </div>
                        <button class="remove-btn" title="Eliminar del carrito">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                    <div class="cart-item-body">
                        <div class="cart-item-quantity">
                            <button class="quantity-btn decrease" data-action="decrease" title="Disminuir cantidad">
                                <i class="fas fa-minus"></i>
                            </button>
                            <div class="quantity-display">
                                <span class="quantity-value">${item.quantity}</span>
                                <span class="quantity-label">unidades</span>
                            </div>
                            <button class="quantity-btn increase" data-action="increase" title="Aumentar cantidad">
                                <i class="fas fa-plus"></i>
                            </button>
                        </div>
                        <div class="cart-item-total">
                            <span class="total-label">Subtotal:</span>
                            <span class="total-price">$${(item.price * item.quantity).toFixed(2)}</span>
                        </div>
                    </div>
                    <div class="cart-item-footer">
                        <div class="stock-info">
                            <i class="fas fa-box"></i>
                            <span>Stock disponible: ${item.stock}</span>
                        </div>
                    </div>
                `;
                
                // Agregar listener para eliminar item del carrito
                const removeBtn = cartItem.querySelector('.remove-btn');
                removeBtn.addEventListener('click', function() {
                    const itemId = this.closest('.cart-item').dataset.id;
                    removeFromCart(itemId);
                });
                
                // Agregar listeners para aumentar/disminuir cantidad
                const quantityBtns = cartItem.querySelectorAll('.quantity-btn');
                quantityBtns.forEach(btn => {
                    btn.addEventListener('click', function() {
                        const action = this.dataset.action;
                        const itemId = this.closest('.cart-item').dataset.id;
                        if (action === 'increase') {
                            updateCartItemQuantity(itemId, 'increase');
                        } else if (action === 'decrease') {
                            updateCartItemQuantity(itemId, 'decrease');
                        }
                    });
                });
                
                cartItemsContainer.appendChild(cartItem);
            });
            updateCheckoutBtn();
        }

        function updateCartItemQuantity(id, action) {
            const item = cart.find(item => item.id === id);
            if (!item) return;
            item.quantity = parseInt(item.quantity);
            item.stock = parseInt(item.stock);
            if (action === 'increase') {
                if (item.stock > 0) {
                    item.quantity++;
                    item.stock--;
                    showSuccessToast('✔️ Cantidad aumentada');
                } else {
                    showErrorToast('❌ Sin stock disponible');
                }
            } else if (action === 'decrease') {
                if (item.quantity > 1) {
                    item.quantity--;
                    item.stock++;
                    showSuccessToast('✔️ Cantidad disminuida');
                } else {
                    // Si la cantidad es 1, eliminar el item del carrito
                    removeFromCart(id);
                }
            }
            renderCart();
            calculateSubtotal();
            updateCheckoutBtn();
        }

        // Calcular subtotal, IVA y total
        function calculateSubtotal() {
            let subtotal = 0;
            let totalItems = 0;
            cart.forEach(item => {
                subtotal += item.price * item.quantity;
                totalItems += item.quantity;
            });
            const tax = subtotal * 0.16;
            const total = subtotal + tax;
            
            const subtotalEl = document.getElementById('subtotal');
            const taxEl = document.getElementById('tax');
            const totalEl = document.getElementById('total');
            
            if (subtotalEl) subtotalEl.textContent = `$${subtotal.toFixed(2)}`;
            if (taxEl) taxEl.textContent = `$${tax.toFixed(2)}`;
            if (totalEl) totalEl.textContent = `$${total.toFixed(2)}`;
            
            // Retornar el total calculado
            return total;
        }

        // Calcular y mostrar el cambio al ingresar el monto recibido
        function calculateChange() {
            const amountPaidEl = document.getElementById('amountPaid');
            const totalEl = document.getElementById('total');
            const changeEl = document.getElementById('change');
            
            if (!amountPaidEl || !totalEl || !changeEl) return;
            
            const amountPaid = parseFloat(amountPaidEl.value || 0);
            const total = parseFloat(totalEl.textContent?.replace('$', '') || 0);
            let change = 0;
            
            if (!isNaN(amountPaid) && !isNaN(total)) {
                change = amountPaid - total;
            }
            
            changeEl.textContent = change >= 0 ? change.toFixed(2) : '0.00';
            updateCheckoutBtn();
        }

        // Habilitar/deshabilitar el botón de procesar venta
        function updateCheckoutBtn() {
            const checkoutBtn = document.getElementById('checkoutBtn');
            const totalEl = document.getElementById('total');
            const amountPaidEl = document.getElementById('amountPaid');
            
            if (!checkoutBtn || !totalEl || !amountPaidEl) return;
            
            const amountPaid = parseFloat(amountPaidEl.value || 0);
            const total = parseFloat(totalEl.textContent?.replace('$', '') || 0);
            
            // Solo efectivo - requiere monto suficiente
            const canCheckout = cart.length > 0 && total > 0 && !isNaN(amountPaid) && amountPaid >= total;
            
            if (canCheckout) {
                checkoutBtn.disabled = false;
                checkoutBtn.classList.remove('disabled');
            } else {
                checkoutBtn.disabled = true;
                checkoutBtn.classList.add('disabled');
            }
        }

        // Función para procesar la venta (checkout)
        function processCheckout() {
            console.log('processCheckout() iniciada');
            
            // Verificar que el carrito no esté vacío
            if (cart.length === 0) {
                showErrorToast('El carrito está vacío');
                return;
            }
            
            const total = calculateSubtotal();
            const amountPaid = parseFloat(document.getElementById('amountPaid').value) || 0;
            
            // Validar monto para efectivo
            if (amountPaid < total) {
                showErrorToast('El monto recibido es insuficiente');
                return;
            }
            
            // Mostrar confirmación antes de procesar la venta
            const clienteTexto = selectedClienteData ? selectedClienteData.nombre : 'Sin cliente';
            const cambio = amountPaid - total;
            
            Swal.fire({
                title: '¿Procesar esta venta?',
                html: `
                    <div style="text-align: left; padding: 1rem;">
                        <p><strong>Cliente:</strong> ${clienteTexto}</p>
                        <p><strong>Total:</strong> $${total.toFixed(2)}</p>
                        <p><strong>Monto recibido:</strong> $${amountPaid.toFixed(2)}</p>
                        <p><strong>Cambio:</strong> $${cambio.toFixed(2)}</p>
                        <p><strong>Productos:</strong> ${cart.length} artículo(s)</p>
                    </div>
                `,
                icon: 'question',
                showCancelButton: true,
                confirmButtonText: '✅ Sí, procesar venta',
                cancelButtonText: '❌ Cancelar',
                confirmButtonColor: '#27ae60',
                cancelButtonColor: '#e74c3c',
                reverseButtons: true
            }).then((result) => {
                if (result.isConfirmed) {
                    // Proceder con la venta
                    executeCheckout(total, amountPaid);
                }
            });
        }
        
        // Función separada para ejecutar el checkout después de la confirmación
        function executeCheckout(total, amountPaid) {
            // Preparar datos de la compra
            const compra = {
                productos: cart.map(item => ({
                    id: item.id, // Agregar ID del producto para descuento de stock
                    nombre: item.name,
                    cantidad: item.quantity,
                    precio: item.price
                })),
                total: total,
                metodo_pago: 'cash', // Solo efectivo
                monto_recibido: amountPaid,
                cambio: amountPaid - total,
                fecha: new Date().toLocaleString('es-ES')
            };
            
            // Cliente es opcional - puede ser null
            const clienteId = selectedClienteData ? selectedClienteData.id : null;
            const clienteNombre = selectedClienteData ? selectedClienteData.nombre : null;
            
            console.log('Procesando venta:', {
                cliente: clienteNombre || 'Sin cliente',
                total: total,
                metodo: selectedPaymentMethod,
                productos: compra.productos.length
            });
            
            // Enviar por fetch al nuevo endpoint para guardar en la base de datos
            fetch('guardar_venta.php', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ 
                    clienteId: clienteId, 
                    clienteNombre: clienteNombre,
                    compra: compra 
                })
            }).then(res => {
                if (!res.ok) {
                    throw new Error(`Error HTTP: ${res.status}`);
                }
                return res.json();
            }).then(data => {
                if (data.success) {
                    // Mostrar SweetAlert de venta completada
                    Swal.fire({
                        title: '🎉 ¡Venta Completada!',
                        html: `
                            <div style="text-align: center; padding: 1rem;">
                                <div style="font-size: 3rem; margin-bottom: 1rem;">✅</div>
                                <h3 style="color: #27ae60; margin-bottom: 1rem;">Venta #${data.saleId}</h3>
                                <div style="background: #f8f9fa; padding: 1rem; border-radius: 8px; margin: 1rem 0;">
                                    <p style="margin: 0.5rem 0;"><strong>Total:</strong> $${data.total.toFixed(2)}</p>
                                    <p style="margin: 0.5rem 0;"><strong>Cliente:</strong> ${data.cliente_nombre || 'Sin cliente'}</p>
                                    <p style="margin: 0.5rem 0;"><strong>Método:</strong> Efectivo</p>
                                    <p style="margin: 0.5rem 0;"><strong>Fecha:</strong> ${new Date().toLocaleString('es-ES')}</p>
                                </div>
                                <p style="color: #666; font-size: 0.9rem;">La venta ha sido registrada exitosamente en el sistema</p>
                            </div>
                        `,
                        icon: 'success',
                        showDenyButton: true,
                        showCancelButton: true,
                        confirmButtonText: '🛒 Nueva Venta',
                        denyButtonText: '🧾 Ver Comprobante',
                        cancelButtonText: '📊 Ver Ventas',
                        confirmButtonColor: '#27ae60',
                        denyButtonColor: '#f39c12',
                        cancelButtonColor: '#3498db',
                        reverseButtons: true,
                        allowOutsideClick: false,
                        customClass: {
                            confirmButton: 'btn-nueva-venta',
                            denyButton: 'btn-comprobante',
                            cancelButton: 'btn-ver-ventas'
                        }
                    }).then((result) => {
                        if (result.isConfirmed) {
                            // Nueva venta - recargar la página
                            showSuccessToast('🛒 Recargando para nueva venta...');
                            setTimeout(() => {
                                window.location.reload();
                            }, 1000);
                        } else if (result.isDenied) {
                            // Ver comprobante - abrir en nueva ventana
                            window.open('../sales/comprobante.php?id=' + data.saleId, '_blank', 'width=500,height=700,scrollbars=yes,resizable=yes');
                            // Luego preguntar qué hacer
                            setTimeout(() => {
                                Swal.fire({
                                    title: '¿Qué deseas hacer ahora?',
                                    showCancelButton: true,
                                    confirmButtonText: '🛒 Nueva Venta',
                                    cancelButtonText: '📊 Ver Ventas',
                                    confirmButtonColor: '#27ae60',
                                    cancelButtonColor: '#3498db'
                                }).then((result2) => {
                                    if (result2.isConfirmed) {
                                        window.location.reload();
                                    } else {
                                        window.location.href = '../sales/index.php';
                                    }
                                });
                            }, 500);
                        } else if (result.dismiss === Swal.DismissReason.cancel) {
                            // Ver ventas - redirigir al módulo de ventas
                            showSuccessToast('📊 Redirigiendo a ventas...');
                            setTimeout(() => {
                                window.location.href = '../sales/index.php';
                            }, 1000);
                        }
                    });
                    
                    // Limpiar campos inmediatamente después de procesar la venta
                    clearCart();
                    if (selectedClienteData) {
                        clearSelectedCliente(); // Limpiar cliente seleccionado solo si había uno
                    }
                    document.getElementById('amountPaid').value = '';
                    document.getElementById('change').textContent = '0.00';
                } else {
                    showErrorToast('❌ ' + data.message);
                }
            }).catch(error => {
                console.error('Error al procesar venta:', error);
                showErrorToast('❌ Error de conexión: ' + error.message);
            });
        }
        
        // Función para cambiar entre módulos del sistema
        function switchModule(module) {
            console.log('switchModule() iniciada:', module);
            
            // Funcionalidad básica de módulos
            const moduleMessages = {
                'pos': '🛒 Módulo Punto de Venta - Ya estás aquí',
                'sales': '📈 Redirigiendo al módulo de Ventas...',
                'categories': '🏷️ Redirigiendo al módulo de Categorías...',
            };
            
            if (module === 'customers') {
                window.location.href = '../../Views/clientes/index.php';
                return;
            }
            
            if (module === 'suppliers') {
                window.location.href = '../suppliers/index.php';
                return;
            }
            
            if (module === 'sales') {
                window.location.href = '../sales/index.php';
                return;
            }
            
            if (module === 'categories') {
                window.location.href = '../categories/manage.php';
                return;
            }
            
            const message = moduleMessages[module] || '🔧 Módulo no disponible';
            
            if (module === 'inventory') {
                window.location.href = 'inventario/index.php'; // Redirigir a la vista de inventario
                return;
                
            }
            
            if (module === 'pos') {
                showSuccessToast(message);
            } else {
                showInfo(message);
            }
        }
        
        // Función para manejar el módulo de configuración
        function openSettingsModule() {
            console.log('openSettingsModule() iniciada');
            
            Swal.fire({
                title: '⚙️ Configuración',
                html: `
                    <div style="text-align: left; padding: 1rem;">
                        <h4>🎨 Opciones Disponibles:</h4>
                        <ul style="list-style: none; padding: 0;">
                            <li style="padding: 0.5rem 0;">🌙 Tema oscuro/claro</li>
                            <li style="padding: 0.5rem 0;">🔔 Notificaciones</li>
                            <li style="padding: 0.5rem 0;">💰 Configuración de impuestos</li>
                            <li style="padding: 0.5rem 0;">🖨️ Configuración de impresora</li>
                            <li style="padding: 0.5rem 0;">📱 Preferencias de usuario</li>
                        </ul>
                        <div style="margin-top: 1rem; padding: 1rem; background: #f8f9fa; border-radius: 8px;">
                            <p style="margin: 0; color: #6c757d; font-size: 0.9rem;">
                                🚧 Módulo en desarrollo. Funcionalidades próximamente disponibles.
                            </p>
                        </div>
                    </div>
                `,
                icon: 'info',
                confirmButtonText: 'Entendido',
                confirmButtonColor: '#6c757d'
            });
        }
        
        // Agregar listener para el botón de configuración y logout
        document.addEventListener('DOMContentLoaded', function() {
            const settingsBtn = document.getElementById('settingsBtn');
            if (settingsBtn) {
                settingsBtn.addEventListener('click', function(e) {
                    e.preventDefault();
                    openSettingsModule();
                });
            }
        });

        // Mejor manejo de errores JS global
        window.addEventListener('error', function(e) {
            console.error('Error JS global:', e);
            if (typeof showErrorToast === 'function') {
                showErrorToast('Error: ' + e.message);
            } else {
                alert('Error: ' + e.message);
            }
        });

        // Función de prueba para verificar que el sistema funciona
        function testSystemFunctionality() {
            console.log('=== EJECUTANDO PRUEBAS DEL SISTEMA ===');
            
            // Verificar elementos críticos
            const criticalElements = [
                { id: 'clearCart', name: 'Botón Limpiar Carrito' },
                { id: 'amountPaid', name: 'Campo Monto Recibido' },
                { id: 'checkoutBtn', name: 'Botón Procesar Venta' },
                { id: 'productSearch', name: 'Campo Búsqueda' },
                { id: 'cartItems', name: 'Contenedor Carrito' },
                { id: 'subtotal', name: 'Subtotal' },
                { id: 'total', name: 'Total' },
                { id: 'change', name: 'Cambio' }
            ];
            
            let missingElements = [];
            
            criticalElements.forEach(element => {
                const el = document.getElementById(element.id);
                if (el) {
                    console.log(`✅ ${element.name} - OK`);
                } else {
                    console.error(`❌ ${element.name} - FALTA`);
                    missingElements.push(element.name);
                }
            });
            
            // Verificar botones de pago
            const paymentBtns = document.querySelectorAll('.payment-btn');
            console.log(`💳 Botones de pago encontrados: ${paymentBtns.length}`);
            
            if (missingElements.length > 0) {
                showErrorToast(`❌ Elementos faltantes: ${missingElements.join(', ')}`);
                return false;
            } else {
                showSuccessToast('✅ Todos los elementos están presentes');
                return true;
            }
        }
        
        // Función para reinicializar el sistema si hay problemas
        function reinitializeSystem() {
            console.log('🔄 Reinicializando sistema...');
            
            try {
                // Limpiar todo
                cart = [];
                selectedPaymentMethod = 'cash';
                currentPage = 1;
                
                // Resetear UI
                renderCart();
                calculateSubtotal();
                updateCheckoutBtn();
                
                // Reconfigurar event listeners
                setupEventListeners();
                
                showSuccessToast('🔄 Sistema reinicializado correctamente');
                
            } catch (error) {
                console.error('Error al reinicializar:', error);
                showErrorToast('❌ Error al reinicializar: ' + error.message);
            }
        }
        
        // Función separada para configurar event listeners
        function setupEventListeners() {
            console.log('🔧 Configurando event listeners...');
            
            // Event listener para limpiar carrito
            const clearCartBtn = document.getElementById('clearCart');
            if (clearCartBtn) {
                clearCartBtn.onclick = function(e) {
                    e.preventDefault();
                    showSuccessToast('🧹 clearCart ejecutado');
                    clearCart();
                };
            }
            
            // Solo efectivo - sin event listeners para métodos de pago
            
            // Event listener para monto recibido
            const amountPaidEl = document.getElementById('amountPaid');
            if (amountPaidEl) {
                amountPaidEl.oninput = calculateChange;
            }
            
            // Event listener para botón de checkout eliminado - ya está en la función principal
            
            console.log('✅ Event listeners configurados');
        }
        
        // Agregar comando de consola para pruebas
        window.testPOS = testSystemFunctionality;
        window.reinitPOS = reinitializeSystem;
        
        console.log('🎮 Comandos de consola disponibles:');
        console.log('  - testPOS() : Probar funcionalidad del sistema');
        console.log('  - reinitPOS() : Reinicializar sistema');

        // === FUNCIONES PARA BÚSQUEDA DE CLIENTES ===
        
        // Variable para almacenar el cliente seleccionado
        let selectedClienteData = null;
        let clienteSearchTimeout = null;
        
        // Configurar la funcionalidad de búsqueda de clientes
        function setupClienteSearch() {
            const clienteSearchEl = document.getElementById('clienteSearch');
            const clienteSuggestionsEl = document.getElementById('clienteSuggestions');
            
            if (!clienteSearchEl || !clienteSuggestionsEl) {
                console.error('Elementos de búsqueda de clientes no encontrados');
                return;
            }
            
            // Event listener para búsqueda
            clienteSearchEl.addEventListener('input', function() {
                const query = this.value.trim();
                
                // Limpiar timeout anterior
                if (clienteSearchTimeout) {
                    clearTimeout(clienteSearchTimeout);
                }
                
                if (query.length < 1) {
                    hideclienteSuggestions();
                    return;
                }
                
                // Búsqueda con debounce de 300ms
                clienteSearchTimeout = setTimeout(() => {
                    searchClientes(query);
                }, 300);
            });
            
            // Ocultar sugerencias al hacer clic fuera
            document.addEventListener('click', function(e) {
                if (!e.target.closest('.cliente-search-container')) {
                    hideclienteSuggestions();
                }
            });
            
            console.log('✅ Búsqueda de clientes configurada correctamente');
        }
        
        // Buscar clientes en la base de datos
        async function searchClientes(query) {
            try {
                console.log('Buscando clientes:', query);
                
                const response = await fetch(`../../api/clientes.php?search=${encodeURIComponent(query)}&limit=8`);
                
                if (!response.ok) {
                    throw new Error('Error en la respuesta del servidor');
                }
                
                const data = await response.json();
                
                if (data.success) {
                    displayClienteSuggestions(data.data);
                } else {
                    console.error('Error al buscar clientes:', data.message);
                    showErrorToast('Error al buscar clientes');
                }
                
            } catch (error) {
                console.error('Error en búsqueda de clientes:', error);
                showErrorToast('Error de conexión al buscar clientes');
            }
        }
        
        // Mostrar las sugerencias de clientes
        function displayClienteSuggestions(clientes) {
            const clienteSuggestionsEl = document.getElementById('clienteSuggestions');
            
            if (!clienteSuggestionsEl) return;
            
            if (clientes.length === 0) {
                clienteSuggestionsEl.innerHTML = '<div class="cliente-suggestion-item" style="text-align:center; color:#666; font-style:italic;">No se encontraron clientes</div>';
                clienteSuggestionsEl.style.display = 'block';
                return;
            }
            
            let html = '';
            clientes.forEach(cliente => {
                html += `
                    <div class="cliente-suggestion-item" onclick="selectCliente(${cliente.id}, '${cliente.nombre}', '${cliente.telefono}')">
                        <div class="cliente-name">${cliente.nombre}</div>
                        <div style="display: flex; gap: 8px; align-items: center;">
                            <span class="cliente-id">#${cliente.id}</span>
                            <span class="cliente-phone">${cliente.telefono}</span>
                        </div>
                    </div>
                `;
            });
            
            clienteSuggestionsEl.innerHTML = html;
            clienteSuggestionsEl.style.display = 'block';
        }
        
        // Seleccionar un cliente
        function selectCliente(id, nombre, telefono) {
            selectedClienteData = { id, nombre, telefono };
            selectedCliente = id; // Variable global para compatibilidad
            
            // Actualizar interfaz
            const clienteSearchEl = document.getElementById('clienteSearch');
            const selectedClienteDisplayEl = document.getElementById('selectedClienteDisplay');
            const selectedClienteTextEl = document.getElementById('selectedClienteText');
            
            if (clienteSearchEl) {
                clienteSearchEl.value = '';
                clienteSearchEl.placeholder = 'Cliente seleccionado...';
            }
            
            if (selectedClienteDisplayEl && selectedClienteTextEl) {
                selectedClienteTextEl.textContent = `#${id} - ${nombre} (${telefono})`;
                selectedClienteDisplayEl.style.display = 'block';
            }
            
            hideclienteSuggestions();
            
            console.log('Cliente seleccionado:', selectedClienteData);
            showSuccessToast(`Cliente seleccionado: ${nombre}`);
        }
        
        // Limpiar selección de cliente
        function clearSelectedCliente() {
            selectedClienteData = null;
            selectedCliente = ''; // Variable global para compatibilidad
            
            const clienteSearchEl = document.getElementById('clienteSearch');
            const selectedClienteDisplayEl = document.getElementById('selectedClienteDisplay');
            
            if (clienteSearchEl) {
                clienteSearchEl.value = '';
                clienteSearchEl.placeholder = 'Buscar por nombre o ID...';
            }
            
            if (selectedClienteDisplayEl) {
                selectedClienteDisplayEl.style.display = 'none';
            }
            
            console.log('Cliente deseleccionado');
            showWarningToast('Cliente deseleccionado');
        }
        
        // Ocultar sugerencias de clientes
        function hideclienteSuggestions() {
            const clienteSuggestionsEl = document.getElementById('clienteSuggestions');
            if (clienteSuggestionsEl) {
                clienteSuggestionsEl.style.display = 'none';
            }
        }

        // Función para manejar el logout con confirmación
        function handleLogout() {
            console.log('handleLogout() iniciada');
            
            Swal.fire({
                title: '👋 ¿Cerrar Sesión?',
                html: `
                    <div style="text-align: center; padding: 1rem;">
                        <div style="font-size: 3rem; margin-bottom: 1rem;">🔐</div>
                        <p style="margin-bottom: 1rem; color: #666;">¿Estás seguro de que quieres cerrar tu sesión?</p>
                        <div style="padding: 1rem; background: #f8f9fa; border-radius: 8px; margin: 1rem 0;">
                            <p style="margin: 0; color: #6c757d; font-size: 0.9rem;">
                                <i class="fas fa-info-circle"></i> Se perderán los datos no guardados del carrito
                            </p>
                        </div>
                    </div>
                `,
                icon: 'question',
                showCancelButton: true,
                confirmButtonText: '🚪 Sí, Cerrar Sesión',
                cancelButtonText: '❌ Cancelar',
                confirmButtonColor: '#e74c3c',
                cancelButtonColor: '#6c757d',
                reverseButtons: true,
                focusCancel: true,
                customClass: {
                    confirmButton: 'btn-logout-confirm',
                    cancelButton: 'btn-logout-cancel'
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    // Mostrar mensaje de despedida
                    Swal.fire({
                        title: '👋 ¡Hasta pronto!',
                        text: 'Cerrando sesión...',
                        icon: 'success',
                        timer: 1500,
                        timerProgressBar: true,
                        showConfirmButton: false,
                        allowOutsideClick: false,
                        didOpen: () => {
                            // Redirigir después de mostrar el mensaje
                            setTimeout(() => {
                                window.location.href = '../auth/logout.php';
                            }, 1000);
                        }
                    });
                } else {
                    // Usuario canceló
                    showSuccessToast('🔄 Sesión mantenida activa');
                }
            });
        }

        // === AUTOCOMPLETADO DE PRODUCTOS ===
        // Agrega un input de búsqueda con autocompletado y mejora visual

        document.addEventListener('DOMContentLoaded', function() {
            // Autocompletado visual para el campo de búsqueda
            const searchEl = document.getElementById('productSearch');
            if (searchEl) {
                // Crear contenedor de sugerencias
                let suggestionBox = document.createElement('div');
                suggestionBox.className = 'autocomplete-suggestions';
                suggestionBox.style.display = 'none';
                searchEl.parentNode.appendChild(suggestionBox);

                searchEl.addEventListener('input', function() {
                    const query = this.value.trim().toLowerCase();
                    if (!query) {
                        suggestionBox.style.display = 'none';
                        suggestionBox.innerHTML = '';
                        return;
                    }
                    // Filtrar productos por nombre o categoría
                    const matches = products.filter(p =>
                        p.name.toLowerCase().includes(query) ||
                        (p.category_name && p.category_name.toLowerCase().includes(query))
                    ).slice(0, 8); // Máximo 8 sugerencias
                    if (matches.length === 0) {
                        suggestionBox.style.display = 'none';
                        suggestionBox.innerHTML = '';
                        return;
                    }
                    suggestionBox.innerHTML = matches.map(prod =>
                        `<div class="autocomplete-item" data-id="${prod.id}" data-name="${prod.name}" data-price="${prod.price}" data-category="${prod.category_id}" data-stock="${prod.stock}">
                            <span class="autocomplete-name">${prod.name}</span>
                            <span class="autocomplete-category">${prod.category_name || ''}</span>
                            <span class="autocomplete-price">$${parseFloat(prod.price).toFixed(2)}</span>
                        </div>`
                    ).join('');
                    suggestionBox.style.display = 'block';
                });
                // Click en sugerencia
                suggestionBox.addEventListener('click', function(e) {
                    const item = e.target.closest('.autocomplete-item');
                    if (item) {
                        addToCart(item.dataset.id, item.dataset.name, item.dataset.price, item.dataset.category, item.dataset.stock);
                        searchEl.value = '';
                        suggestionBox.style.display = 'none';
                    }
                });
                // Ocultar sugerencias al perder foco
                searchEl.addEventListener('blur', function() {
                    setTimeout(() => { suggestionBox.style.display = 'none'; }, 150);
                });
                // Mostrar sugerencias al enfocar si hay texto
                searchEl.addEventListener('focus', function() {
                    if (this.value.trim()) {
                        searchEl.dispatchEvent(new Event('input'));
                    }
                });
            }
        });

        // === MEJORA DE DISEÑO PARA AUTOCOMPLETADO DE PRODUCTOS Y CLIENTES ===
        const style = document.createElement('style');
        style.innerHTML = `
        /* Estilos para búsqueda de clientes */
        .cliente-search-container {
            position: relative;
        }
        .cliente-suggestions {
            position: absolute;
            z-index: 1002;
            background: #fff;
            border: 1px solid #d1d5db;
            border-radius: 8px;
            box-shadow: 0 4px 16px rgba(44, 62, 80, 0.1);
            width: 100%;
            max-height: 200px;
            overflow-y: auto;
            margin-top: 2px;
            font-size: 0.9rem;
        }
        .cliente-suggestion-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 8px 12px;
            cursor: pointer;
            border-bottom: 1px solid #f0f0f0;
            transition: background 0.15s;
        }
        .cliente-suggestion-item:last-child { 
            border-bottom: none; 
        }
        .cliente-suggestion-item:hover {
            background: #f8f9fa;
        }
        .cliente-name {
            font-weight: 600;
            color: #2c3e50;
            flex: 1;
            font-size: 0.9rem;
        }
        .cliente-id {
            color: #666;
            font-size: 0.8em;
            margin-right: 8px;
        }
        .cliente-phone {
            color: #888;
            font-size: 0.85em;
        }
        
        /* Responsive para búsqueda de clientes */
        @media (max-width: 768px) {
            .cliente-suggestions {
                font-size: 0.85rem;
                max-height: 150px;
            }
            .cliente-name {
                font-size: 0.85rem;
            }
            .cliente-id, .cliente-phone {
                font-size: 0.75em;
            }
            .cliente-suggestion-item {
                padding: 6px 10px;
            }
        }
        
        /* Estilos personalizados para SweetAlert de venta completada */
        .btn-nueva-venta {
            background: #27ae60 !important;
            border-color: #27ae60 !important;
            font-weight: 600 !important;
            padding: 12px 24px !important;
            font-size: 1rem !important;
        }
        
        .btn-nueva-venta:hover {
            background: #219a52 !important;
            border-color: #219a52 !important;
            transform: translateY(-1px) !important;
        }
        
        .btn-ver-ventas {
            background: #3498db !important;
            border-color: #3498db !important;
            font-weight: 600 !important;
            padding: 12px 24px !important;
            font-size: 1rem !important;
        }
        
        .btn-ver-ventas:hover {
            background: #2980b9 !important;
            border-color: #2980b9 !important;
            transform: translateY(-1px) !important;
        }
        
        /* Estilos para autocompletado de productos */
        .autocomplete-suggestions {
            position: absolute;
            z-index: 1001;
            background: #fff;
            border: 1px solid #d1d5db;
            border-radius: 8px;
            box-shadow: 0 4px 16px #2c3e501a;
            width: 100%;
            max-height: 260px;
            overflow-y: auto;
            margin-top: 2px;
            font-size: 1rem;
        }
        .autocomplete-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0.7rem 1rem;
            cursor: pointer;
            border-bottom: 1px solid #f0f0f0;
            transition: background 0.15s;
        }
        .autocomplete-item:last-child { border-bottom: none; }
        .autocomplete-item:hover {
            background: #eaf6fb;
        }
        .autocomplete-name {
            font-weight: 600;
            color: #3498db;
            flex: 1;
        }
        .autocomplete-category {
            color: #888;
            font-size: 0.95em;
            margin-left: 1.2em;
            flex: 0 0 110px;
            text-align: right;
        }
        .autocomplete-price {
            background: #eaf6fb;
            color: #217dbb;
            border-radius: 6px;
            padding: 0.2em 0.7em;
            margin-left: 1em;
            font-weight: 500;
            font-size: 0.98em;
        }
        @media (max-width: 700px) {
            .autocomplete-suggestions { font-size: 0.97em; }
            .autocomplete-category { flex-basis: 70px; }
        }
        `;
        document.head.appendChild(style);
    </script>
</body>
</html>