<?php
// Incluir el controlador para manejar mensajes
require_once __DIR__ . '/../../Controllers/AuthController.php';
$authController = new AuthController();

// Obtener mensajes de la sesión
$errorMessage = $authController->getSessionMessage('error_message');
$successMessage = $authController->getSessionMessage('success_message');
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Punto-D</title>
    <link rel="stylesheet" href="../../Assets/css/login.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
    <div class="login-background">
        <div class="login-particles"></div>
        <div class="container">
            <div class="login-card">
                <div class="header">
                    <div class="logo-container">
                        <img src="data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHZpZXdCb3g9IjAgMCA0MCA0MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTIwIDQwQzMxLjA0NTcgNDAgNDAgMzEuMDQ1NyA0MCAyMEM0MCA4Ljk1NDMgMzEuMDQ1NyAwIDIwIDBDOC45NTQzIDAgMCA4Ljk1NDMgMCAyMEMwIDMxLjA0NTcgOC45NTQzIDQwIDIwIDQwWiIgZmlsbD0iIzAwMDAwMCIvPgo8cGF0aCBkPSJNMjAgMzZDMjguODM2NiAzNiAzNiAyOC44MzY2IDM2IDIwQzM2IDExLjE2MzQgMjguODM2NiA0IDIwIDRDMTEuMTYzNCA0IDQgMTEuMTYzNCA0IDIwQzQgMjguODM2NiAxMS4xNjM0IDM2IDIwIDM2WiIgZmlsbD0iI0ZGRkZGRiIvPgo8ZWxsaXBzZSBjeD0iMTUiIGN5PSIxNS41IiByeD0iMyIgcnk9IjQiIGZpbGw9IiMwMDAwMDAiLz4KPGVsbGlwc2UgY3g9IjI1IiBjeT0iMTUuNSIgcng9IjMiIHJ5PSI0IiBmaWxsPSIjMDAwMDAwIi8+CjxwYXRoIGQ9Ik0xMiAyNEMxNCAxOCAyNiAxOCAyOCAyNEMyNiAyOCAxNCAyOCAxMiAyNCIgZmlsbD0iI0ZGQjAwMCIvPgo8cGF0aCBkPSJNOCAzMkM0IDI4IDggMjQgMTYgMjZDMTQgMzAgMTAgMzIgOCAzMloiIGZpbGw9IiNGRkIwMDAiLz4KPHBhdGggZD0iTTMyIDMyQzM2IDI4IDMyIDI0IDI0IDI2QzI2IDMwIDMwIDMyIDMyIDMyWiIgZmlsbD0iI0ZGQjAwMCIvPgo8L3N2Zz4K" alt="Tux Logo" class="tux-logo-img">
                    </div>
                    <h1>Punto-D</h1>
                    <p>Sistema de Punto de Venta</p>
                </div>
                
                <div class="profile-pic">
                    <div class="avatar-circle">
                        <i class="fas fa-user"></i>
                    </div>
                </div>
                
                <div class="login-box">
                    <form method="POST" action="login_handler.php" id="loginForm">
                        <div class="form-group">
                            <div class="input-group">
                                <i class="fas fa-user"></i>
                                <input type="text" name="username" placeholder="Usuario o Email" id="usernameInput" required>
                            </div>
                            <span class="error-message" id="usernameError"></span>
                        </div>

                        <div class="form-group">
                            <div class="input-group password-group">
                                <i class="fas fa-lock"></i>
                                <input type="password" name="password" placeholder="Contraseña" id="passwordInput" required>
                                <button type="button" class="password-toggle" id="passwordToggle" tabindex="-1">
                                    <i class="fas fa-eye"></i>
                                </button>
                            </div>
                            <span class="error-message" id="passwordError"></span>
                        </div>

                        <div class="checkbox-group">
                            <input type="checkbox" id="rememberMe" name="remember_me">
                            <label for="rememberMe">Recordarme</label>
                        </div>
                        
                        <button type="submit" class="btn btn-primary" id="loginBtn">
                            Iniciar Sesión
                        </button>
                    </form>

                    <div class="divider">
                        <span>¿No tienes cuenta?</span>
                    </div>

                    <div class="form-links">
                        <a href="register.php">Crear nueva cuenta</a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        // Mostrar mensajes PHP con SweetAlert2
        <?php if ($errorMessage): ?>
            Swal.fire({
                icon: 'error',
                title: 'Error de acceso',
                text: '<?php echo addslashes($errorMessage); ?>',
                confirmButtonText: 'Intentar de nuevo',
                background: 'rgba(255, 255, 255, 0.95)',
                backdrop: 'rgba(0, 0, 0, 0.4)',
                customClass: {
                    popup: 'swal2-popup'
                }
            });
        <?php endif; ?>

        <?php if ($successMessage): ?>
            Swal.fire({
                icon: 'success',
                title: '¡Éxito!',
                text: '<?php echo addslashes($successMessage); ?>',
                timer: 2000,
                showConfirmButton: false,
                background: 'rgba(255, 255, 255, 0.95)',
                backdrop: 'rgba(0, 0, 0, 0.4)',
                customClass: {
                    popup: 'swal2-popup'
                }
            });
        <?php endif; ?>

        // Validación del formulario
        const loginForm = document.getElementById('loginForm');
        const usernameInput = document.getElementById('usernameInput');
        const passwordInput = document.getElementById('passwordInput');
        const loginBtn = document.getElementById('loginBtn');
        const usernameError = document.getElementById('usernameError');
        const passwordError = document.getElementById('passwordError');

        // Validación en tiempo real
        usernameInput.addEventListener('input', function() {
            if (this.value.trim() === '') {
                usernameError.textContent = 'El usuario es requerido';
                this.style.borderColor = '#e74c3c';
            } else {
                usernameError.textContent = '';
                this.style.borderColor = '#27ae60';
            }
        });

        passwordInput.addEventListener('input', function() {
            if (this.value.length < 3) {
                passwordError.textContent = 'La contraseña debe tener al menos 3 caracteres';
                this.style.borderColor = '#e74c3c';
            } else {
                passwordError.textContent = '';
                this.style.borderColor = '#27ae60';
            }
        });

        // Envío del formulario
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const username = usernameInput.value.trim();
            const password = passwordInput.value;
            
            // Validaciones
            let hasErrors = false;
            
            if (username === '') {
                usernameError.textContent = 'El usuario es requerido';
                usernameInput.style.borderColor = '#e74c3c';
                hasErrors = true;
            }
            
            if (password.length < 3) {
                passwordError.textContent = 'La contraseña debe tener al menos 3 caracteres';
                passwordInput.style.borderColor = '#e74c3c';
                hasErrors = true;
            }
            
            if (hasErrors) {
                // Animación de error
                document.querySelector('.login-card').classList.add('shake');
                setTimeout(() => {
                    document.querySelector('.login-card').classList.remove('shake');
                }, 500);
                return;
            }
            
            // Loading state
            loginBtn.classList.add('loading');
            loginBtn.disabled = true;
            
            // Simular procesamiento y enviar
            setTimeout(() => {
                this.submit();
            }, 800);
        });

        // Animación de entrada
        document.addEventListener('DOMContentLoaded', function() {
            const loginCard = document.querySelector('.login-card');
            loginCard.style.transform = 'translateY(50px)';
            loginCard.style.opacity = '0';
            
            setTimeout(() => {
                loginCard.style.transition = 'all 0.8s ease';
                loginCard.style.transform = 'translateY(0)';
                loginCard.style.opacity = '1';
            }, 100);

            // Funcionalidad para mostrar/ocultar contraseña
            const passwordInput = document.getElementById('passwordInput');
            const passwordToggle = document.getElementById('passwordToggle');
            
            if (passwordToggle && passwordInput) {
                passwordToggle.addEventListener('click', function(e) {
                    e.preventDefault();
                    e.stopPropagation();
                    
                    const icon = this.querySelector('i');
                    
                    if (passwordInput.type === 'password') {
                        passwordInput.type = 'text';
                        icon.className = 'fas fa-eye-slash';
                        this.title = 'Ocultar contraseña';
                    } else {
                        passwordInput.type = 'password';
                        icon.className = 'fas fa-eye';
                        this.title = 'Mostrar contraseña';
                    }
                });
                
                // Configurar título inicial
                passwordToggle.title = 'Mostrar contraseña';
            }
        });

        // Animación de partículas adicionales
        function createParticles() {
            const particles = document.querySelector('.login-particles');
            
            for (let i = 0; i < 5; i++) {
                const particle = document.createElement('div');
                particle.style.position = 'absolute';
                particle.style.width = Math.random() * 6 + 2 + 'px';
                particle.style.height = particle.style.width;
                particle.style.background = 'rgba(255, 255, 255, 0.1)';
                particle.style.borderRadius = '50%';
                particle.style.left = Math.random() * 100 + '%';
                particle.style.top = Math.random() * 100 + '%';
                particle.style.animation = `float ${Math.random() * 3 + 2}s ease-in-out infinite`;
                particle.style.animationDelay = Math.random() * 2 + 's';
                
                particles.appendChild(particle);
            }
        }
        
        createParticles();
    </script>
</body>
</html>
            
            if (show) {
                buttonText.style.display = 'none';
                buttonIcon.style.display = 'none';
                loadingIcon.style.display = 'block';
                loginButton.disabled = true;
                loginButton.classList.add('loading');
            } else {
                buttonText.style.display = 'inline';
                buttonIcon.style.display = 'inline';
                loadingIcon.style.display = 'none';
                loginButton.disabled = false;
                loginButton.classList.remove('loading');
            }
        }

        // Event listeners para limpiar errores
        usernameInput.addEventListener('input', () => hideError(usernameError));
        passwordInput.addEventListener('input', () => hideError(passwordError));
        usernameInput.addEventListener('focus', () => hideError(usernameError));
        passwordInput.addEventListener('focus', () => hideError(passwordError));

        // Funcionalidad para mostrar/ocultar contraseña
        togglePassword.addEventListener('click', function() {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            this.classList.toggle('fa-eye');
            this.classList.toggle('fa-eye-slash');
        });

        showPasswordCheckbox.addEventListener('change', function() {
            const type = this.checked ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            
            if (this.checked) {
                togglePassword.classList.remove('fa-eye');
                togglePassword.classList.add('fa-eye-slash');
            } else {
                togglePassword.classList.remove('fa-eye-slash');
                togglePassword.classList.add('fa-eye');
            }
        });

        // Manejo del formulario
        loginForm.addEventListener('submit', function(e) {
            showLoading(true);
            
            // Simular pequeña demora para mostrar loading
            setTimeout(() => {
                // El formulario se enviará normalmente después del delay
            }, 500);
        });

        // Animaciones de entrada
        document.addEventListener('DOMContentLoaded', function() {
            createParticles();
            
            // Animación de entrada de la tarjeta
            const loginCard = document.querySelector('.login-card');
            loginCard.style.opacity = '0';
            loginCard.style.transform = 'translateY(30px) scale(0.95)';
            
            setTimeout(() => {
                loginCard.style.transition = 'all 0.6s cubic-bezier(0.4, 0, 0.2, 1)';
                loginCard.style.opacity = '1';
                loginCard.style.transform = 'translateY(0) scale(1)';
            }, 100);

            // Focus automático en el campo de usuario
            setTimeout(() => {
                usernameInput.focus();
            }, 700);
        });

        // Efectos de hover en inputs
        const inputs = document.querySelectorAll('input[type="text"], input[type="password"]');
        inputs.forEach(input => {
            input.addEventListener('focus', function() {
                this.closest('.input-group').classList.add('focused');
            });
            
            input.addEventListener('blur', function() {
                if (!this.value) {
                    this.closest('.input-group').classList.remove('focused');
                }
            });
        });
    </script>
</body>
</html>