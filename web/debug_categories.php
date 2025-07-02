<?php
// Debug para categorías
session_start();

echo "<h1>Debug - Categorías</h1>";

// Verificar sesión
echo "<h2>Sesión:</h2>";
echo "<pre>";
print_r($_SESSION);
echo "</pre>";

// Verificar conexión a base de datos
try {
    require_once 'Core/config.php';
    echo "<h2>Conexión a BD:</h2>";
    echo "Conectado correctamente<br>";
    
    // Verificar tabla de categorías
    $pdo = new PDO("mysql:host=" . DB_HOST . ";dbname=" . DB_NAME, DB_USER, DB_PASS);
    $stmt = $pdo->query("SHOW TABLES LIKE 'categories'");
    if ($stmt->rowCount() > 0) {
        echo "Tabla 'categories' existe<br>";
        
        // Contar categorías
        $stmt = $pdo->query("SELECT COUNT(*) as total FROM categories");
        $total = $stmt->fetch(PDO::FETCH_ASSOC);
        echo "Total de categorías: " . $total['total'] . "<br>";
    } else {
        echo "Tabla 'categories' NO existe<br>";
    }
    
} catch (Exception $e) {
    echo "<h2>Error de BD:</h2>";
    echo $e->getMessage();
}

// Verificar si el archivo manage.php existe
echo "<h2>Archivo manage.php:</h2>";
if (file_exists('Views/categories/manage.php')) {
    echo "El archivo existe<br>";
    echo "Tamaño: " . filesize('Views/categories/manage.php') . " bytes<br>";
} else {
    echo "El archivo NO existe<br>";
}

// Test del controlador
echo "<h2>Test del controlador:</h2>";
if (file_exists('Controllers/CategoriesController.php')) {
    echo "CategoriesController.php existe<br>";
    // Incluir y probar
    try {
        include_once 'Controllers/CategoriesController.php';
        echo "Controlador incluido correctamente<br>";
    } catch (Exception $e) {
        echo "Error al incluir controlador: " . $e->getMessage() . "<br>";
    }
} else {
    echo "CategoriesController.php NO existe<br>";
}

echo "<hr>";
echo "<a href='Views/categories/manage.php'>Ir a manage.php</a>";
?>
