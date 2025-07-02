<?php
session_start();
require_once __DIR__ . '/../../Controllers/SuppliersController.php';

// Verificar autenticación
if (!isset($_SESSION['logged_in']) || !$_SESSION['logged_in']) {
    header('Location: ../auth/login.php');
    exit();
}

$controller = new SuppliersController();
$isEdit = isset($_GET['id']);
$supplier = null;
$errors = [];
$success = '';

if ($isEdit) {
    $result = $controller->show($_GET['id']);
    if (isset($result['error'])) {
        header('Location: index.php');
        exit();
    }
    $supplier = $result['supplier'];
}

// Procesar formulario
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = [
        'name' => $_POST['name'] ?? '',
        'contact_person' => $_POST['contact_person'] ?? '',
        'email' => $_POST['email'] ?? '',
        'phone' => $_POST['phone'] ?? '',
        'address' => $_POST['address'] ?? '',
        'city' => $_POST['city'] ?? '',
        'country' => $_POST['country'] ?? '',
        'tax_id' => $_POST['tax_id'] ?? '',
        'website' => $_POST['website'] ?? '',
        'notes' => $_POST['notes'] ?? ''
    ];

    if ($isEdit) {
        $result = $controller->update($_GET['id'], $data);
    } else {
        $result = $controller->create($data);
    }

    if (isset($result['success'])) {
        $success = $result['success'];
        if (!$isEdit) {
            // Limpiar formulario después de crear
            $data = array_fill_keys(array_keys($data), '');
        }
    } elseif (isset($result['errors'])) {
        $errors = $result['errors'];
    } elseif (isset($result['error'])) {
        $errors['general'] = $result['error'];
    }
}

// Si es edición, usar datos del proveedor existente si no hay POST data
if ($isEdit && !$_POST) {
    $data = $supplier;
}
?>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><?php echo $isEdit ? 'Editar' : 'Agregar'; ?> Proveedor - Punto-D</title>
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
        .fas.fa-save::before { content: "💾"; }
        .fas.fa-arrow-left::before { content: "←"; }
        .fas.fa-user::before { content: "👤"; }
        .fas.fa-envelope::before { content: "✉"; }
        .fas.fa-phone::before { content: "📞"; }
        .fas.fa-map-marker-alt::before { content: "📍"; }
        .fas.fa-globe::before { content: "🌐"; }
        .fas.fa-id-card::before { content: "🆔"; }
        .fas.fa-sticky-note::before { content: "📝"; }
    </style>
    <style>
        .form-container {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
        }

        .form-header {
            display: flex;
            align-items: center;
            gap: 15px;
            margin-bottom: 30px;
        }

        .form-title {
            color: #2c3e50;
            font-size: 2.2rem;
            font-weight: bold;
            margin: 0;
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

        .form-card {
            background: white;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .form-section {
            margin-bottom: 30px;
        }

        .section-title {
            color: #2c3e50;
            font-size: 1.3rem;
            font-weight: 600;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #ecf0f1;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .form-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
        }

        .form-group.full-width {
            grid-column: 1 / -1;
        }

        .form-label {
            color: #2c3e50;
            font-weight: 600;
            margin-bottom: 8px;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .required {
            color: #e74c3c;
        }

        .form-input, .form-textarea {
            padding: 12px 16px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 1rem;
            transition: border-color 0.3s ease;
            background: white;
        }

        .form-input:focus, .form-textarea:focus {
            outline: none;
            border-color: #3498db;
        }

        .form-textarea {
            min-height: 100px;
            resize: vertical;
        }

        .error-input {
            border-color: #e74c3c !important;
        }

        .error-message {
            color: #e74c3c;
            font-size: 0.85rem;
            margin-top: 5px;
            display: flex;
            align-items: center;
            gap: 5px;
        }

        .success-message {
            background: #d4edda;
            color: #155724;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
            border: 1px solid #c3e6cb;
        }

        .general-error {
            background: #f8d7da;
            color: #721c24;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            border: 1px solid #f5c6cb;
        }

        .form-actions {
            display: flex;
            gap: 15px;
            justify-content: flex-end;
            padding-top: 20px;
            border-top: 1px solid #ecf0f1;
        }

        .btn-submit {
            background: linear-gradient(135deg, #2ecc71, #27ae60);
            color: white;
            padding: 12px 30px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 1rem;
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;
        }

        .btn-submit:hover {
            background: linear-gradient(135deg, #27ae60, #2ecc71);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(46, 204, 113, 0.3);
        }

        .btn-cancel {
            background: #95a5a6;
            color: white;
            padding: 12px 30px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 1rem;
            font-weight: 600;
            text-decoration: none;
            display: flex;
            align-items: center;
            gap: 8px;
            transition: background 0.3s ease;
        }

        .btn-cancel:hover {
            background: #7f8c8d;
        }

        .input-with-icon {
            position: relative;
        }

        .input-icon {
            position: absolute;
            left: 12px;
            top: 50%;
            transform: translateY(-50%);
            color: #7f8c8d;
            z-index: 1;
        }

        .input-with-icon .form-input {
            padding-left: 45px;
        }

        @media (max-width: 768px) {
            .form-container {
                padding: 15px;
            }

            .form-card {
                padding: 20px;
            }

            .form-grid {
                grid-template-columns: 1fr;
            }

            .form-actions {
                flex-direction: column;
            }

            .form-header {
                flex-direction: column;
                align-items: stretch;
            }

            .form-title {
                font-size: 1.8rem;
                text-align: center;
            }
        }
    </style>
</head>
<body>
    <div class="form-container">
        <div class="form-header">
            <a href="index.php" class="back-btn">
                <i class="fas fa-arrow-left"></i>
                Volver
            </a>
            <h1 class="form-title">
                <i class="fas fa-truck"></i>
                <?php echo $isEdit ? 'Editar Proveedor' : 'Agregar Nuevo Proveedor'; ?>
            </h1>
        </div>

        <div class="form-card">
            <?php if ($success): ?>
                <div class="success-message">
                    <i class="fas fa-check-circle"></i>
                    <?php echo htmlspecialchars($success); ?>
                </div>
            <?php endif; ?>

            <?php if (isset($errors['general'])): ?>
                <div class="general-error">
                    <i class="fas fa-exclamation-triangle"></i>
                    <?php echo htmlspecialchars($errors['general']); ?>
                </div>
            <?php endif; ?>

            <form method="POST" id="supplierForm">
                <!-- Información Básica -->
                <div class="form-section">
                    <h3 class="section-title">
                        <i class="fas fa-info-circle"></i>
                        Información Básica
                    </h3>
                    <div class="form-grid">
                        <div class="form-group">
                            <label class="form-label" for="name">
                                <i class="fas fa-building"></i>
                                Nombre del Proveedor <span class="required">*</span>
                            </label>
                            <input type="text" 
                                   id="name" 
                                   name="name" 
                                   class="form-input <?php echo isset($errors['name']) ? 'error-input' : ''; ?>"
                                   value="<?php echo htmlspecialchars($data['name'] ?? ''); ?>"
                                   required>
                            <?php if (isset($errors['name'])): ?>
                                <div class="error-message">
                                    <i class="fas fa-exclamation-circle"></i>
                                    <?php echo htmlspecialchars($errors['name']); ?>
                                </div>
                            <?php endif; ?>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="contact_person">
                                <i class="fas fa-user"></i>
                                Persona de Contacto
                            </label>
                            <input type="text" 
                                   id="contact_person" 
                                   name="contact_person" 
                                   class="form-input"
                                   value="<?php echo htmlspecialchars($data['contact_person'] ?? ''); ?>">
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="tax_id">
                                <i class="fas fa-file-invoice"></i>
                                ID Fiscal/RUC
                            </label>
                            <input type="text" 
                                   id="tax_id" 
                                   name="tax_id" 
                                   class="form-input <?php echo isset($errors['tax_id']) ? 'error-input' : ''; ?>"
                                   value="<?php echo htmlspecialchars($data['tax_id'] ?? ''); ?>">
                            <?php if (isset($errors['tax_id'])): ?>
                                <div class="error-message">
                                    <i class="fas fa-exclamation-circle"></i>
                                    <?php echo htmlspecialchars($errors['tax_id']); ?>
                                </div>
                            <?php endif; ?>
                        </div>
                    </div>
                </div>

                <!-- Información de Contacto -->
                <div class="form-section">
                    <h3 class="section-title">
                        <i class="fas fa-address-book"></i>
                        Información de Contacto
                    </h3>
                    <div class="form-grid">
                        <div class="form-group">
                            <label class="form-label" for="email">
                                <i class="fas fa-envelope"></i>
                                Email
                            </label>
                            <input type="email" 
                                   id="email" 
                                   name="email" 
                                   class="form-input <?php echo isset($errors['email']) ? 'error-input' : ''; ?>"
                                   value="<?php echo htmlspecialchars($data['email'] ?? ''); ?>">
                            <?php if (isset($errors['email'])): ?>
                                <div class="error-message">
                                    <i class="fas fa-exclamation-circle"></i>
                                    <?php echo htmlspecialchars($errors['email']); ?>
                                </div>
                            <?php endif; ?>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="phone">
                                <i class="fas fa-phone"></i>
                                Teléfono
                            </label>
                            <input type="tel" 
                                   id="phone" 
                                   name="phone" 
                                   class="form-input <?php echo isset($errors['phone']) ? 'error-input' : ''; ?>"
                                   value="<?php echo htmlspecialchars($data['phone'] ?? ''); ?>">
                            <?php if (isset($errors['phone'])): ?>
                                <div class="error-message">
                                    <i class="fas fa-exclamation-circle"></i>
                                    <?php echo htmlspecialchars($errors['phone']); ?>
                                </div>
                            <?php endif; ?>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="website">
                                <i class="fas fa-globe"></i>
                                Sitio Web
                            </label>
                            <input type="url" 
                                   id="website" 
                                   name="website" 
                                   class="form-input <?php echo isset($errors['website']) ? 'error-input' : ''; ?>"
                                   value="<?php echo htmlspecialchars($data['website'] ?? ''); ?>"
                                   placeholder="https://www.ejemplo.com">
                            <?php if (isset($errors['website'])): ?>
                                <div class="error-message">
                                    <i class="fas fa-exclamation-circle"></i>
                                    <?php echo htmlspecialchars($errors['website']); ?>
                                </div>
                            <?php endif; ?>
                        </div>
                    </div>
                </div>

                <!-- Ubicación -->
                <div class="form-section">
                    <h3 class="section-title">
                        <i class="fas fa-map-marker-alt"></i>
                        Ubicación
                    </h3>
                    <div class="form-grid">
                        <div class="form-group full-width">
                            <label class="form-label" for="address">
                                <i class="fas fa-map"></i>
                                Dirección
                            </label>
                            <textarea id="address" 
                                      name="address" 
                                      class="form-textarea"
                                      rows="3"><?php echo htmlspecialchars($data['address'] ?? ''); ?></textarea>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="city">
                                <i class="fas fa-city"></i>
                                Ciudad
                            </label>
                            <input type="text" 
                                   id="city" 
                                   name="city" 
                                   class="form-input"
                                   value="<?php echo htmlspecialchars($data['city'] ?? ''); ?>">
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="country">
                                <i class="fas fa-flag"></i>
                                País
                            </label>
                            <input type="text" 
                                   id="country" 
                                   name="country" 
                                   class="form-input"
                                   value="<?php echo htmlspecialchars($data['country'] ?? ''); ?>">
                        </div>
                    </div>
                </div>

                <!-- Notas Adicionales -->
                <div class="form-section">
                    <h3 class="section-title">
                        <i class="fas fa-sticky-note"></i>
                        Notas Adicionales
                    </h3>
                    <div class="form-group">
                        <label class="form-label" for="notes">
                            <i class="fas fa-comment"></i>
                            Notas y Comentarios
                        </label>
                        <textarea id="notes" 
                                  name="notes" 
                                  class="form-textarea"
                                  rows="4"
                                  placeholder="Información adicional sobre el proveedor, términos de pago, condiciones especiales, etc."><?php echo htmlspecialchars($data['notes'] ?? ''); ?></textarea>
                    </div>
                </div>

                <div class="form-actions">
                    <a href="index.php" class="btn-cancel">
                        <i class="fas fa-times"></i>
                        Cancelar
                    </a>
                    <button type="submit" class="btn-submit">
                        <i class="fas fa-save"></i>
                        <?php echo $isEdit ? 'Actualizar Proveedor' : 'Crear Proveedor'; ?>
                    </button>
                </div>
            </form>
        </div>
    </div>

    <script>
        // Validación del formulario
        document.getElementById('supplierForm').addEventListener('submit', function(e) {
            const name = document.getElementById('name').value.trim();
            
            if (!name) {
                e.preventDefault();
                Swal.fire('Error', 'El nombre del proveedor es requerido', 'error');
                return;
            }

            // Validar email si está presente
            const email = document.getElementById('email').value.trim();
            if (email && !isValidEmail(email)) {
                e.preventDefault();
                Swal.fire('Error', 'El formato del email no es válido', 'error');
                return;
            }

            // Validar URL si está presente
            const website = document.getElementById('website').value.trim();
            if (website && !isValidUrl(website)) {
                e.preventDefault();
                Swal.fire('Error', 'El formato de la URL no es válido', 'error');
                return;
            }
        });

        function isValidEmail(email) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            return emailRegex.test(email);
        }

        function isValidUrl(url) {
            try {
                new URL(url);
                return true;
            } catch (e) {
                return false;
            }
        }

        // Auto-formato para URL
        document.getElementById('website').addEventListener('blur', function() {
            let url = this.value.trim();
            if (url && !url.startsWith('http://') && !url.startsWith('https://')) {
                this.value = 'https://' + url;
            }
        });

        // Mostrar alerta de éxito y redirigir si es creación exitosa
        <?php if ($success && !$isEdit): ?>
            setTimeout(() => {
                Swal.fire({
                    title: 'Éxito',
                    text: '<?php echo $success; ?>',
                    icon: 'success',
                    showCancelButton: true,
                    confirmButtonText: 'Agregar otro',
                    cancelButtonText: 'Ver proveedores',
                    confirmButtonColor: '#2ecc71',
                    cancelButtonColor: '#3498db'
                }).then((result) => {
                    if (!result.isConfirmed) {
                        window.location.href = 'index.php';
                    }
                });
            }, 100);
        <?php endif; ?>

        // Tecla ESC para volver
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                window.location.href = 'index.php';
            }
        });
    </script>
</body>
</html>
