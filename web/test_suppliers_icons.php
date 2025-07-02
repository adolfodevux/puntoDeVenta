<?php
// Test simple para verificar iconos de FontAwesome en proveedores
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Test de Iconos - Proveedores</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            padding: 20px;
            background: #f5f6fa;
        }
        
        .test-container {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            max-width: 800px;
            margin: 0 auto;
        }
        
        .icon-test {
            display: flex;
            align-items: center;
            gap: 10px;
            margin: 10px 0;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 8px;
        }
        
        .btn-test {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 12px 20px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 1rem;
            margin: 5px;
            text-decoration: none;
            font-weight: 500;
        }
        
        .btn-primary { background: #3498db; color: white; }
        .btn-success { background: #2ecc71; color: white; }
        .btn-warning { background: #f39c12; color: white; }
        .btn-danger { background: #e74c3c; color: white; }
        .btn-secondary { background: #95a5a6; color: white; }
    </style>
</head>
<body>
    <div class="test-container">
        <h1><i class="fas fa-truck"></i> Test de Iconos de Proveedores</h1>
        
        <h2>Iconos individuales:</h2>
        <div class="icon-test">
            <i class="fas fa-truck"></i> Icono de camión (proveedores)
        </div>
        <div class="icon-test">
            <i class="fas fa-user"></i> Icono de usuario (contacto)
        </div>
        <div class="icon-test">
            <i class="fas fa-envelope"></i> Icono de email
        </div>
        <div class="icon-test">
            <i class="fas fa-phone"></i> Icono de teléfono
        </div>
        <div class="icon-test">
            <i class="fas fa-search"></i> Icono de búsqueda
        </div>
        <div class="icon-test">
            <i class="fas fa-times"></i> Icono de limpiar
        </div>
        <div class="icon-test">
            <i class="fas fa-eye"></i> Icono de ver
        </div>
        <div class="icon-test">
            <i class="fas fa-edit"></i> Icono de editar
        </div>
        <div class="icon-test">
            <i class="fas fa-trash"></i> Icono de eliminar
        </div>
        <div class="icon-test">
            <i class="fas fa-plus"></i> Icono de agregar
        </div>
        <div class="icon-test">
            <i class="fas fa-arrow-left"></i> Icono de regresar
        </div>
        
        <h2>Botones con iconos:</h2>
        <button class="btn-test btn-primary">
            <i class="fas fa-search"></i>
            Buscar
        </button>
        
        <button class="btn-test btn-secondary">
            <i class="fas fa-times"></i>
            Limpiar
        </button>
        
        <a href="#" class="btn-test btn-success">
            <i class="fas fa-plus"></i>
            Agregar Proveedor
        </a>
        
        <a href="#" class="btn-test btn-primary">
            <i class="fas fa-eye"></i>
            Ver
        </a>
        
        <a href="#" class="btn-test btn-warning">
            <i class="fas fa-edit"></i>
            Editar
        </a>
        
        <button class="btn-test btn-danger">
            <i class="fas fa-trash"></i>
            Eliminar
        </button>
        
        <a href="../dashboard/index.php" class="btn-test btn-secondary">
            <i class="fas fa-arrow-left"></i>
            Regresar al Dashboard
        </a>
        
        <h2>Información de FontAwesome:</h2>
        <div class="icon-test">
            <strong>URL CDN:</strong> https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css
        </div>
        <div class="icon-test">
            <strong>Versión:</strong> 6.0.0
        </div>
        <div class="icon-test">
            <strong>Estado:</strong> 
            <span id="fontawesome-status" style="color: #e74c3c;">Verificando...</span>
        </div>
    </div>
    
    <script>
        // Verificar si FontAwesome se cargó correctamente
        document.addEventListener('DOMContentLoaded', function() {
            // Crear un elemento de prueba
            const testIcon = document.createElement('i');
            testIcon.className = 'fas fa-truck';
            testIcon.style.position = 'absolute';
            testIcon.style.visibility = 'hidden';
            document.body.appendChild(testIcon);
            
            // Verificar si el icono tiene content (indicando que FontAwesome se cargó)
            const computedStyle = window.getComputedStyle(testIcon, ':before');
            const content = computedStyle.getPropertyValue('content');
            
            const statusElement = document.getElementById('fontawesome-status');
            
            if (content && content !== 'none' && content !== '""') {
                statusElement.textContent = 'FontAwesome cargado correctamente ✓';
                statusElement.style.color = '#2ecc71';
            } else {
                statusElement.textContent = 'Error: FontAwesome no se pudo cargar ✗';
                statusElement.style.color = '#e74c3c';
            }
            
            // Limpiar elemento de prueba
            document.body.removeChild(testIcon);
        });
    </script>
</body>
</html>
