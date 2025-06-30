<?php
// Archivo para manejar el login
require_once __DIR__ . '/../../Controllers/AuthController.php';

$authController = new AuthController();
$authController->login();
?>
