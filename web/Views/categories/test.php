<?php
session_start();

// Verificar si el usuario está logueado
if (!isset($_SESSION['logged_in']) || !$_SESSION['logged_in']) {
    header('Location: ../auth/login.php');
    exit();
}
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Test Categorías - Punto-D</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 2rem; }
        .test-box { border: 1px solid #ccc; padding: 1rem; margin: 1rem 0; }
        .success { background: #d4edda; border-color: #c3e6cb; }
        .error { background: #f8d7da; border-color: #f5c6cb; }
        .loading { background: #fff3cd; border-color: #ffeaa7; }
    </style>
</head>
<body>
    <h1>Test de Categorías</h1>
    
    <div id="test-session" class="test-box">
        <h3>1. Test de Sesión</h3>
        <p>Usuario: <?php echo $_SESSION['username'] ?? 'No definido'; ?></p>
        <p>Logueado: <?php echo $_SESSION['logged_in'] ? 'Sí' : 'No'; ?></p>
    </div>

    <div id="test-controller" class="test-box loading">
        <h3>2. Test del Controlador</h3>
        <p id="controller-status">Probando conexión...</p>
        <div id="controller-result"></div>
    </div>

    <div id="test-data" class="test-box loading">
        <h3>3. Test de Datos</h3>
        <p id="data-status">Cargando datos...</p>
        <div id="data-result"></div>
    </div>

    <hr>
    <p><a href="manage.php">Ir a la página de gestión de categorías</a></p>

    <script>
        async function testController() {
            const statusEl = document.getElementById('controller-status');
            const resultEl = document.getElementById('controller-result');
            const testBox = document.getElementById('test-controller');
            
            try {
                statusEl.textContent = 'Conectando al controlador...';
                
                const response = await fetch('../../Controllers/CategoriesController.php?action=getAll');
                
                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                }
                
                const text = await response.text();
                console.log('Respuesta raw:', text);
                
                let result;
                try {
                    result = JSON.parse(text);
                } catch (e) {
                    throw new Error('Respuesta no es JSON válido: ' + text.substring(0, 100));
                }
                
                if (result.success) {
                    statusEl.textContent = 'Controlador funcionando correctamente';
                    testBox.className = 'test-box success';
                    resultEl.innerHTML = `<strong>Datos recibidos:</strong><br><pre>${JSON.stringify(result, null, 2)}</pre>`;
                    
                    // Test de datos
                    testData(result.data);
                } else {
                    throw new Error(result.message || 'Error desconocido del controlador');
                }
                
            } catch (error) {
                statusEl.textContent = 'Error en el controlador';
                testBox.className = 'test-box error';
                resultEl.innerHTML = `<strong>Error:</strong> ${error.message}`;
                console.error('Error testing controller:', error);
            }
        }

        function testData(categories) {
            const statusEl = document.getElementById('data-status');
            const resultEl = document.getElementById('data-result');
            const testBox = document.getElementById('test-data');
            
            try {
                statusEl.textContent = 'Procesando datos...';
                
                if (!Array.isArray(categories)) {
                    throw new Error('Los datos no son un array');
                }
                
                statusEl.textContent = `Datos procesados: ${categories.length} categorías encontradas`;
                testBox.className = 'test-box success';
                
                if (categories.length > 0) {
                    resultEl.innerHTML = `
                        <strong>Categorías encontradas:</strong><br>
                        <ul>
                            ${categories.map(cat => `<li>${cat.name} (ID: ${cat.id}) - ${cat.product_count || 0} productos</li>`).join('')}
                        </ul>
                    `;
                } else {
                    resultEl.innerHTML = '<p>No hay categorías registradas.</p>';
                }
                
            } catch (error) {
                statusEl.textContent = 'Error procesando datos';
                testBox.className = 'test-box error';
                resultEl.innerHTML = `<strong>Error:</strong> ${error.message}`;
                console.error('Error testing data:', error);
            }
        }

        // Ejecutar tests al cargar la página
        document.addEventListener('DOMContentLoaded', () => {
            console.log('Iniciando tests...');
            testController();
        });
    </script>
</body>
</html>
