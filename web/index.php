<?php
/**
 * Punto de Venta - Página Principal
 * 
 * Este archivo actúa como punto de entrada principal del sistema web.
 * Redirecciona automáticamente a la página de login para la autenticación del usuario.
 * 
 * @author Sistema Punto de Venta
 * @version 1.0
 */

// Iniciar sesión para verificar si el usuario ya está autenticado
session_start();

// Verificar si el usuario ya está logueado
if (isset($_SESSION['user_id']) && isset($_SESSION['user_name'])) {
    // Si ya está logueado, redirigir al dashboard
    header('Location: Views/dashboard/index.php');
    exit();
} else {
    // Si no está logueado, redirigir al login
    header('Location: Views/auth/login.php');
    exit();
}
?>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Punto de Venta - Cargando...</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: Arial, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            color: white;
        }
        
        .loading-container {
            text-align: center;
        }
        
        .loading-spinner {
            border: 4px solid rgba(255, 255, 255, 0.3);
            border-radius: 50%;
            border-top: 4px solid white;
            width: 50px;
            height: 50px;
            animation: spin 1s linear infinite;
            margin: 0 auto 20px;
        }
        
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        
        h1 {
            margin: 0;
            font-size: 2.5em;
            margin-bottom: 10px;
        }
        
        p {
            margin: 0;
            font-size: 1.2em;
            opacity: 0.9;
        }
        
        .redirect-link {
            margin-top: 20px;
        }
        
        .redirect-link a {
            color: white;
            Views/dashboard/index.phptext-decoration: none;
            background: rgba(255, 255, 255, 0.2);
            padding: 10px 20px;
            border-radius: 25px;
            transition: background 0.3s ease;
        }
        
        .redirect-link a:hover {
            background: rgba(255, 255, 255, 0.3);
        }
    </style>
    <script>
        // JavaScript de respaldo para redirección si el PHP no funciona
        setTimeout(function() {
            window.location.href = 'Views/auth/login.php';
        }, 2000);
    </script>
</head>
<body>
    <div class="loading-container">
        <div class="loading-spinner"></div>
        <h1>🛍️ Punto de Venta</h1>
        <p>Redirigiendo al sistema...</p>
        <div class="redirect-link">
            <a href="Views/auth/login.php">Ir al Login</a>
        </div>
    </div>
</body>
</html>
