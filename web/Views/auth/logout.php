<?php
// Archivo para manejar el logout
require_once __DIR__ . '/../../Controllers/AuthController.php';

$authController = new AuthController();
$authController->logout();
?>
