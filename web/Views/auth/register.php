<?php
// Incluir el controlador para manejar mensajes
require_once __DIR__ . '/../../Controllers/AuthController.php';
$authController = new AuthController();

// Obtener mensajes de la sesión
$errorMessage = $authController->getSessionMessage('error_message');
$validationErrors = $authController->getSessionMessage('validation_errors') ?? [];
$oldUsername = $authController->getOldData('username');
$oldEmail = $authController->getOldData('email');
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro Punto-D</title>
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
                    <p>Crear Nueva Cuenta</p>
                </div>
                
                <div class="profile-pic">
                    <div class="avatar-circle">
                        <i class="fas fa-user-plus"></i>
                    </div>
                </div>
                
                <div class="login-box">
                    <form method="POST" action="register_handler.php" id="registerForm">
                        <div class="form-group">
                            <div class="input-group">
                                <i class="fas fa-user"></i>
                                <input type="text" name="username" placeholder="Usuario" id="usernameInput" 
                                       value="<?php echo htmlspecialchars($oldUsername); ?>" required>
                            </div>
                            <span class="error-message" id="usernameError">
                                <?php echo $validationErrors['username'] ?? ''; ?>
                            </span>
                        </div>

                        <div class="form-group">
                            <div class="input-group">
                                <i class="fas fa-envelope"></i>
                                <input type="email" name="email" placeholder="Correo Electrónico" id="emailInput" 
                                       value="<?php echo htmlspecialchars($oldEmail); ?>" required>
                            </div>
                            <span class="error-message" id="emailError">
                                <?php echo $validationErrors['email'] ?? ''; ?>
                            </span>
                        </div>

                        <div class="form-group">
                            <div class="input-group password-group">
                                <i class="fas fa-lock"></i>
                                <input type="password" name="password" placeholder="Contraseña" id="passwordInput" required>
                                <button type="button" class="password-toggle" id="passwordToggle" tabindex="-1">
                                    <i class="fas fa-eye"></i>
                                </button>
                            </div>
                            <span class="error-message" id="passwordError">
                                <?php echo $validationErrors['password'] ?? ''; ?>
                            </span>
                        </div>

                        <div class="form-group">
                            <div class="input-group password-group">
                                <i class="fas fa-lock"></i>
                                <input type="password" name="confirm_password" placeholder="Confirmar Contraseña" id="confirmPasswordInput" required>
                                <button type="button" class="password-toggle" id="confirmPasswordToggle" tabindex="-1">
                                    <i class="fas fa-eye"></i>
                                </button>
                            </div>
                            <span class="error-message" id="confirmPasswordError">
                                <?php echo $validationErrors['confirm_password'] ?? ''; ?>
                            </span>
                        </div>

                        <div class="checkbox-group">
                            <input type="checkbox" id="termsAccepted" required>
                            <label for="termsAccepted">Acepto los términos y condiciones</label>
                        </div>
                        
                        <button type="submit" class="btn btn-primary" id="registerBtn">
                            Crear Cuenta
                        </button>
                    </form>

                    <div class="divider">
                        <span>¿Ya tienes cuenta?</span>
                    </div>

                    <div class="form-links">
                        <a href="login.php">Iniciar sesión</a>
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
                title: 'Error en el registro',
                text: '<?php echo addslashes($errorMessage); ?>',
                confirmButtonText: 'Intentar de nuevo',
                background: 'rgba(255, 255, 255, 0.95)',
                backdrop: 'rgba(0, 0, 0, 0.4)',
                customClass: {
                    popup: 'swal2-popup'
                }
            });
        <?php endif; ?>

        // Validación del formulario
        const registerForm = document.getElementById('registerForm');
        const usernameInput = document.getElementById('usernameInput');
        const emailInput = document.getElementById('emailInput');
        const passwordInput = document.getElementById('passwordInput');
        const confirmPasswordInput = document.getElementById('confirmPasswordInput');
        const registerBtn = document.getElementById('registerBtn');
        const usernameError = document.getElementById('usernameError');
        const emailError = document.getElementById('emailError');
        const passwordError = document.getElementById('passwordError');
        const confirmPasswordError = document.getElementById('confirmPasswordError');

        // Funcionalidad para mostrar/ocultar contraseñas
        function setupPasswordToggle(inputId, toggleId) {
            const input = document.getElementById(inputId);
            const toggle = document.getElementById(toggleId);
            
            if (toggle && input) {
                toggle.addEventListener('click', function(e) {
                    e.preventDefault();
                    e.stopPropagation();
                    
                    const icon = this.querySelector('i');
                    
                    if (input.type === 'password') {
                        input.type = 'text';
                        icon.className = 'fas fa-eye-slash';
                        this.title = 'Ocultar contraseña';
                    } else {
                        input.type = 'password';
                        icon.className = 'fas fa-eye';
                        this.title = 'Mostrar contraseña';
                    }
                });
                
                // Configurar título inicial
                toggle.title = 'Mostrar contraseña';
            }
        }
        
        // Configurar toggles para ambas contraseñas
        setupPasswordToggle('passwordInput', 'passwordToggle');
        setupPasswordToggle('confirmPasswordInput', 'confirmPasswordToggle');

        // Validación en tiempo real
        usernameInput.addEventListener('input', function() {
            if (this.value.trim().length < 3) {
                usernameError.textContent = 'El usuario debe tener al menos 3 caracteres';
                this.style.borderColor = '#e74c3c';
            } else {
                usernameError.textContent = '';
                this.style.borderColor = '#27ae60';
            }
        });

        emailInput.addEventListener('input', function() {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(this.value)) {
                emailError.textContent = 'Ingresa un email válido';
                this.style.borderColor = '#e74c3c';
            } else {
                emailError.textContent = '';
                this.style.borderColor = '#27ae60';
            }
        });

        passwordInput.addEventListener('input', function() {
            if (this.value.length < 6) {
                passwordError.textContent = 'La contraseña debe tener al menos 6 caracteres';
                this.style.borderColor = '#e74c3c';
            } else {
                passwordError.textContent = '';
                this.style.borderColor = '#27ae60';
            }
            
            // Verificar confirmación si ya se escribió
            if (confirmPasswordInput.value && this.value !== confirmPasswordInput.value) {
                confirmPasswordError.textContent = 'Las contraseñas no coinciden';
                confirmPasswordInput.style.borderColor = '#e74c3c';
            } else if (confirmPasswordInput.value) {
                confirmPasswordError.textContent = '';
                confirmPasswordInput.style.borderColor = '#27ae60';
            }
        });

        confirmPasswordInput.addEventListener('input', function() {
            if (this.value !== passwordInput.value) {
                confirmPasswordError.textContent = 'Las contraseñas no coinciden';
                this.style.borderColor = '#e74c3c';
            } else {
                confirmPasswordError.textContent = '';
                this.style.borderColor = '#27ae60';
            }
        });

        // Envío del formulario
        registerForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const username = usernameInput.value.trim();
            const email = emailInput.value.trim();
            const password = passwordInput.value;
            const confirmPassword = confirmPasswordInput.value;
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            
            // Validaciones
            let hasErrors = false;
            
            if (username.length < 3) {
                usernameError.textContent = 'El usuario debe tener al menos 3 caracteres';
                usernameInput.style.borderColor = '#e74c3c';
                hasErrors = true;
            }
            
            if (!emailRegex.test(email)) {
                emailError.textContent = 'Ingresa un email válido';
                emailInput.style.borderColor = '#e74c3c';
                hasErrors = true;
            }
            
            if (password.length < 6) {
                passwordError.textContent = 'La contraseña debe tener al menos 6 caracteres';
                passwordInput.style.borderColor = '#e74c3c';
                hasErrors = true;
            }
            
            if (password !== confirmPassword) {
                confirmPasswordError.textContent = 'Las contraseñas no coinciden';
                confirmPasswordInput.style.borderColor = '#e74c3c';
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
            registerBtn.classList.add('loading');
            registerBtn.disabled = true;
            
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
