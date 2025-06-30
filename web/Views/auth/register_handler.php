<?php
// Archivo para manejar el registro
require_once __DIR__ . '/../../Controllers/AuthController.php';

$authController = new AuthController();
$authController->register();
?>
