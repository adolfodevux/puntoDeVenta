<?php

// Definición de constantes para la configuración de la base de datos (usando MySQLi)
// Es una buena práctica usar variables de entorno o un archivo .env
// para la información sensible, pero para este ejemplo básico, usaremos constantes.

define('DB_HOST', '40.76.137.253'); // Host de la base de datos
define('DB_NAME', 'puntoDeVenta'); // Nombre de tu base de datos
define('DB_USER', 'debian');     // Usuario de la base de datos
define('DB_PASS', 'P@ss_d3bian-linux_Vps');         // Contraseña del usuario de la base de datos
define('DB_PORT', 3306);       // Puerto de MySQL (el predeterminado es 3306)
define('DB_CHARSET', 'utf8mb4'); // Conjunto de caracteres (importante para emojis, etc.)
define('APP_NAME', 'Sistema Punto de Venta');
define('APP_URL', 'http://localhost/puntoDeVenta/public/'); // URL base de tu aplicación
define('DEBUG_MODE', true); // Cambiar a false en producción para ocultar errores detallados

?>