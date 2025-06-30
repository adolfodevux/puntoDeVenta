<?php

require_once __DIR__ . '/../Models/User.php';

class AuthController {
    private $userModel;

    public function __construct() {
        $this->userModel = new User();
        
        // Iniciar sesión si no está iniciada
        if (session_status() == PHP_SESSION_NONE) {
            session_start();
        }
    }

    /**
     * Manejar el registro de usuarios
     */
    public function register() {
        if ($_SERVER['REQUEST_METHOD'] == 'POST') {
            $username = trim($_POST['username'] ?? '');
            $email = trim($_POST['email'] ?? '');
            $password = $_POST['password'] ?? '';

            // Validaciones básicas
            $errors = $this->validateRegistrationData($username, $email, $password);
            
            if (empty($errors)) {
                $result = $this->userModel->register($username, $email, $password);
                
                if ($result['success']) {
                    // Registro exitoso
                    $_SESSION['success_message'] = $result['message'];
                    header('Location: login.php');
                    exit();
                } else {
                    $_SESSION['error_message'] = $result['message'];
                }
            } else {
                $_SESSION['validation_errors'] = $errors;
                $_SESSION['old_data'] = ['username' => $username, 'email' => $email];
            }
        }
        
        // Mostrar formulario de registro
        include __DIR__ . '/../Views/auth/register.php';
    }

    /**
     * Manejar el login de usuarios
     */
    public function login() {
        if ($_SERVER['REQUEST_METHOD'] == 'POST') {
            $username = trim($_POST['username'] ?? '');
            $password = $_POST['password'] ?? '';

            // Validaciones básicas
            if (empty($username) || empty($password)) {
                $_SESSION['error_message'] = 'Por favor, complete todos los campos';
            } else {
                $result = $this->userModel->login($username, $password);
                
                if ($result['success']) {
                    // Login exitoso - establecer sesión
                    $_SESSION['user_id'] = $result['user']['id'];
                    $_SESSION['username'] = $result['user']['username'];
                    $_SESSION['email'] = $result['user']['email'];
                    $_SESSION['logged_in'] = true;
                    
                    // Redirigir al dashboard
                    header('Location: ../dashboard/index.php');
                    exit();
                } else {
                    $_SESSION['error_message'] = $result['message'];
                }
            }
        }
        
        // Mostrar formulario de login
        include __DIR__ . '/../Views/auth/login.php';
    }

    /**
     * Cerrar sesión
     */
    public function logout() {
        if (session_status() == PHP_SESSION_NONE) {
            session_start();
        }
        
        // Destruir todas las variables de sesión
        $_SESSION = array();
        
        // Destruir la cookie de sesión
        if (ini_get("session.use_cookies")) {
            $params = session_get_cookie_params();
            setcookie(session_name(), '', time() - 42000,
                $params["path"], $params["domain"],
                $params["secure"], $params["httponly"]
            );
        }
        
        // Destruir la sesión
        session_destroy();
        
        // Redirigir al login
        header('Location: login.php');
        exit();
    }

    /**
     * Verificar si el usuario está autenticado
     */
    public function requireAuth() {
        if (session_status() == PHP_SESSION_NONE) {
            session_start();
        }
        
        if (!isset($_SESSION['logged_in']) || !$_SESSION['logged_in']) {
            header('Location: Views/auth/login.php');
            exit();
        }
    }

    /**
     * Validar datos de registro
     */
    private function validateRegistrationData($username, $email, $password) {
        $errors = [];

        // Validar username
        if (empty($username)) {
            $errors['username'] = 'El nombre de usuario es obligatorio';
        } elseif (strlen($username) < 3) {
            $errors['username'] = 'El nombre de usuario debe tener al menos 3 caracteres';
        } elseif (strlen($username) > 50) {
            $errors['username'] = 'El nombre de usuario no puede tener más de 50 caracteres';
        } elseif (!preg_match('/^[a-zA-Z0-9_]+$/', $username)) {
            $errors['username'] = 'El nombre de usuario solo puede contener letras, números y guiones bajos';
        }

        // Validar email
        if (empty($email)) {
            $errors['email'] = 'El email es obligatorio';
        } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            $errors['email'] = 'El formato del email no es válido';
        } elseif (strlen($email) > 100) {
            $errors['email'] = 'El email no puede tener más de 100 caracteres';
        }

        // Validar password
        if (empty($password)) {
            $errors['password'] = 'La contraseña es obligatoria';
        } elseif (strlen($password) < 6) {
            $errors['password'] = 'La contraseña debe tener al menos 6 caracteres';
        } elseif (strlen($password) > 255) {
            $errors['password'] = 'La contraseña no puede tener más de 255 caracteres';
        }

        return $errors;
    }

    /**
     * Obtener mensajes de la sesión y limpiarlos
     */
    public function getSessionMessage($type) {
        if (session_status() == PHP_SESSION_NONE) {
            session_start();
        }
        
        $message = $_SESSION[$type] ?? null;
        unset($_SESSION[$type]);
        return $message;
    }

    /**
     * Obtener datos antiguos del formulario
     */
    public function getOldData($field = null) {
        if (session_status() == PHP_SESSION_NONE) {
            session_start();
        }
        
        $oldData = $_SESSION['old_data'] ?? [];
        
        if ($field) {
            $value = $oldData[$field] ?? '';
            unset($_SESSION['old_data'][$field]);
            return $value;
        }
        
        unset($_SESSION['old_data']);
        return $oldData;
    }
}

?>
