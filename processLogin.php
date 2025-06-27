<?php

$db_host = '40.76.137.253'; 
$db_port = 3306;             
$db_name = 'puntoDeVenta';   
$db_user = 'debian';         
$db_pass = 'P@ss_d3bian-linux_Vps';


$username = htmlspecialchars($_POST['username'] ?? '');
$password = htmlspecialchars($_POST['password'] ?? '');

$alert_message = "";
$alert_type = ""; 


if (empty($username) || empty($password)) {
    $alert_message = "Por favor, ingresa usuario y contraseña.";
    $alert_type = "error";
} else {
    
    $conn = new mysqli($db_host, $db_user, $db_pass, $db_name, $db_port);

   v
    if ($conn->connect_error) {
        $alert_message = "Error de conexión a la base de datos: " . $conn->connect_error;
        $alert_type = "error";
    } else {
        // 3. Preparar la consulta SQL con prepared statements para seguridad
        $sql = "SELECT id FROM vendedores WHERE username = ? AND password = ?";
        $stmt = $conn->prepare($sql);

        if ($stmt === false) {
            $alert_message = "Error al preparar la consulta: " . $conn->error;
            $alert_type = "error";
        } else {
            // 4. Vincular parámetros y ejecutar la consulta
            $stmt->bind_param("ss", $username, $password); // "ss" indica que ambos parámetros son strings
            $stmt->execute();

            // 5. Obtener el resultado
            $result = $stmt->get_result();

            if ($result->num_rows > 0) {
                // Usuario y contraseña son correctos
                $alert_message = "¡Inicio de sesión exitoso! Bienvenido, " . $username . ".";
                $alert_type = "success";
                // Aquí, si fuera una aplicación real, no solo mostrarías la alerta.
                // Podrías iniciar una sesión PHP real y redirigir a un dashboard.
            } else {
                // Usuario o contraseña incorrectos
                $alert_message = "Usuario o contraseña incorrectos.";
                $alert_type = "error";
            }

            // 6. Cerrar recursos (PreparedStatement)
            $stmt->close();
        }
        // Cerrar recursos (Connection)
        $conn->close();
    }
}

// Ahora, mostramos la alerta directamente en esta página
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Resultado del Login</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; flex-direction: column; }
        .container { background-color: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); text-align: center; }
        .container h2 { margin-bottom: 20px; color: #333; }
        .button-link {
            display: inline-block;
            margin-top: 20px;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-size: 16px;
        }
        .button-link:hover {
            background-color: #0056b3;
        }
    </style><!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login PHP</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
        .login-container { background-color: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); width: 300px; text-align: center; }
        .login-container h2 { margin-bottom: 20px; color: #333; }
        .login-container label { display: block; text-align: left; margin-bottom: 8px; color: #555; }
        .login-container input[type="text"],
        .login-container input[type="password"] { width: calc(100% - 20px); padding: 10px; margin-bottom: 15px; border: 1px solid #ddd; border-radius: 4px; }
        .login-container input[type="submit"] { background-color: #007bff; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; width: 100%; }
        .login-container input[type="submit"]:hover { background-color: #0056b3; }
        .message { margin-top: 15px; padding: 10px; border-radius: 4px; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
    </style>
</head>
<body>
    <div class="login-container">
        <h2>Iniciar Sesión</h2>
        <form action="process_login.php" method="POST">
            <label for="username">Usuario:</label>
            <input type="text" id="username" name="username" required>

            <label for="password">Contraseña:</label>
            <input type="password" id="password" name="password" required>

            <input type="submit" value="Iniciar Sesión">
        </form>
        <?php
            // Muestra mensajes de sesión si existen (ej. desde un intento de login fallido)
            session_start();
            if (isset($_SESSION['message'])) {
                $msg_class = isset($_SESSION['message_type']) && $_SESSION['message_type'] == 'error' ? 'error' : 'success';
                echo '<div class="message ' . $msg_class . '">' . $_SESSION['message'] . '</div>';
                unset($_SESSION['message']); // Limpiar el mensaje después de mostrarlo
                unset($_SESSION['message_type']);
            }
        ?>
    </div>
</body>
</html>
</head>
<body>
    <div class="container">
        <h2>Resultado del Login</h2>
        <p>Espere un momento, el resultado se mostrará en una alerta.</p>
        <a href="login.html" class="button-link">Volver al Login</a>
    </div>

    <script>
        // Muestra la alerta con el mensaje obtenido de PHP
        alert("<?php echo $alert_message; ?>");

        // Opcional: Redirigir al login.html después de que el usuario cierre la alerta
        // window.location.href = 'login.html';
    </script>
</body>
</html>