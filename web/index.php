<?php

// Realiza la redirección usando el encabezado Location
// IMPORTANTE: Esta línea DEBE ser ejecutada ANTES de que se envíe CUALQUIER salida HTML al navegador.
header("Location: Views/auth/login.php");

// Opcional: Para asegurar que el script se detenga inmediatamente después de la redirección.
exit();
?>