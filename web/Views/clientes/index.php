<?php
// Vista principal de clientes
// Aquí se listarán los clientes y se podrá agregar/editar/eliminar
session_start();
require_once __DIR__ . '/../../../web/Models/database.php';
// Conexión a la base de datos
$db = Database::getInstance()->getConnection();

// --- CRUD: Agregar, Editar, Eliminar ---
$mensaje = '';
// Agregar cliente
if (isset($_POST['accion']) && $_POST['accion'] === 'agregar') {
    $nombre = trim($_POST['nombre'] ?? '');
    $telefono = trim($_POST['telefono'] ?? '');
    if ($nombre && $telefono) {
        $stmt = $db->prepare('INSERT INTO clientes (nombre, telefono) VALUES (?, ?)');
        $stmt->bind_param('ss', $nombre, $telefono);
        if ($stmt->execute()) {
            $mensaje = 'Cliente agregado correctamente.';
        } else {
            $mensaje = 'Error al agregar cliente.';
        }
        $stmt->close();
        header('Location: ' . $_SERVER['PHP_SELF']);
        exit;
    } else {
        $mensaje = 'Nombre y teléfono requeridos.';
    }
}
// Editar cliente
if (isset($_POST['accion']) && $_POST['accion'] === 'editar' && isset($_POST['id'])) {
    $id = intval($_POST['id']);
    $nombre = trim($_POST['nombre'] ?? '');
    $telefono = trim($_POST['telefono'] ?? '');
    if ($nombre && $telefono) {
        $stmt = $db->prepare('UPDATE clientes SET nombre=?, telefono=? WHERE id=?');
        $stmt->bind_param('ssi', $nombre, $telefono, $id);
        if ($stmt->execute()) {
            $mensaje = 'Cliente actualizado correctamente.';
        } else {
            $mensaje = 'Error al actualizar cliente.';
        }
        $stmt->close();
        header('Location: ' . $_SERVER['PHP_SELF']);
        exit;
    } else {
        $mensaje = 'Nombre y teléfono requeridos.';
    }
}
// Eliminar cliente
if (isset($_GET['eliminar'])) {
    $id = intval($_GET['eliminar']);
    $stmt = $db->prepare('DELETE FROM clientes WHERE id=?');
    $stmt->bind_param('i', $id);
    if ($stmt->execute()) {
        $mensaje = 'Cliente eliminado correctamente.';
    } else {
        $mensaje = 'Error al eliminar cliente.';
    }
    $stmt->close();
    header('Location: ' . $_SERVER['PHP_SELF']);
    exit;
}
// Obtener clientes de la base de datos con información de ventas
$clientes = [];
$sql = "
    SELECT 
        c.id,
        c.nombre,
        c.telefono,
        COALESCE(COUNT(s.id), 0) as total_compras,
        COALESCE(SUM(s.total_amount), 0) as monto_total_compras,
        MAX(s.sale_date) as ultima_compra
    FROM clientes c 
    LEFT JOIN sales s ON c.id = s.cliente_id 
    GROUP BY c.id, c.nombre, c.telefono
    ORDER BY c.id ASC
";
$result = $db->query($sql);
while ($row = $result->fetch_assoc()) {
    $clientes[] = $row;
}
// Paginación
$porPagina = 10;
$totalClientes = count($clientes);
$totalPaginas = ceil($totalClientes / $porPagina);
$paginaActual = isset($_GET['pagina']) ? max(1, intval($_GET['pagina'])) : 1;
$inicio = ($paginaActual - 1) * $porPagina;
$clientesPagina = array_slice($clientes, $inicio, $porPagina);
// Manejar edición visual
$editando = false;
$clienteEditar = null;
if (isset($_GET['editar'])) {
    $editando = true;
    $idEditar = intval($_GET['editar']);
    foreach ($clientes as $c) {
        if ($c['id'] == $idEditar) {
            $clienteEditar = $c;
            break;
        }
    }
}
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Clientes</title>
    <link rel="stylesheet" href="/Assets/css/dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            background: linear-gradient(120deg, #e0eafc 0%, #cfdef3 100%);
            font-family: 'Segoe UI', Arial, sans-serif;
            margin: 0;
            padding: 0;
        }
        .clientes-container {
            max-width: 950px;
            margin: 48px auto 0 auto;
            background: #fff;
            border-radius: 18px;
            box-shadow: 0 6px 32px rgba(52, 152, 219, 0.10), 0 1.5px 6px rgba(44,62,80,0.07);
            padding: 38px 48px 48px 48px;
            position: relative;
        }
        h1 {
            color: #2471a3;
            margin-bottom: 28px;
            font-size: 2.5rem;
            letter-spacing: 1.5px;
            text-shadow: 0 2px 8px #eaf6fb;
            display: flex;
            align-items: center;
            gap: 12px;
        }
        h1 .fa-users {
            color: #3498db;
            font-size: 2.1rem;
        }
        .add-form {
            margin-bottom: 28px;
            background: #fafdff;
            border-radius: 10px;
            padding: 20px 24px 12px 24px;
            box-shadow: 0 1px 8px rgba(52,152,219,0.07);
            display: flex;
            gap: 18px;
            align-items: flex-end;
        }
        .add-form label {
            display: flex;
            flex-direction: column;
            font-weight: 500;
            color: #2d3e50;
            font-size: 1.08rem;
        }
        .add-form input[type="text"] {
            padding: 8px 12px;
            border: 1.5px solid #b2c9e6;
            border-radius: 6px;
            font-size: 1.05rem;
            margin-top: 4px;
            background: #f4f8fb;
            transition: border 0.2s;
        }
        .add-form input[type="text"]:focus {
            border: 1.5px solid #3498db;
            outline: none;
        }
        .add-form button {
            background: linear-gradient(90deg, #27ae60 60%, #2ecc71 100%);
            color: #fff;
            border: none;
            padding: 10px 22px;
            border-radius: 7px;
            font-size: 1.08rem;
            font-weight: 600;
            cursor: pointer;
            box-shadow: 0 2px 8px #eaf6fb;
            transition: background 0.2s, transform 0.1s;
            display: flex;
            align-items: center;
            gap: 7px;
        }
        .add-form button:hover {
            background: linear-gradient(90deg, #219150 60%, #27ae60 100%);
            transform: translateY(-2px) scale(1.03);
        }
        table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            background: #fafdff;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 1px 8px rgba(52,152,219,0.07);
        }
        th, td {
            padding: 15px 14px;
            text-align: left;
        }
        th {
            background: #eaf1f8;
            color: #2d3e50;
            font-weight: 700;
            border-bottom: 2.5px solid #b2c9e6;
            font-size: 1.08rem;
        }
        tr:nth-child(even) {
            background: #f4f8fb;
        }
        tr:hover {
            background: #d6eaf8;
            transition: background 0.2s;
        }
        .action-links {
            display: flex;
            gap: 10px;
        }
        .action-links a {
            display: inline-flex;
            align-items: center;
            gap: 5px;
            color: #3498db;
            text-decoration: none;
            font-weight: 600;
            padding: 6px 12px;
            border-radius: 5px;
            background: #eaf6fb;
            transition: background 0.2s, color 0.2s;
        }
        .action-links a .fa-edit {
            color: #2980b9;
        }
        .action-links a:last-child {
            color: #e74c3c;
            background: #fdecea;
        }
        .action-links a:last-child .fa-trash {
            color: #e74c3c;
        }
        .action-links a:hover {
            background: #d6eaf8;
            text-decoration: underline;
        }
        .action-links a:last-child:hover {
            background: #fadbd8;
        }
        .pagination {
            display: flex;
            justify-content: center;
            margin-top: 24px;
            gap: 6px;
        }
        .pagination a, .pagination span {
            display: inline-block;
            padding: 7px 14px;
            border-radius: 5px;
            background: #eaf1f8;
            color: #2471a3;
            text-decoration: none;
            font-weight: 500;
            transition: background 0.2s, color 0.2s;
        }
        .pagination a:hover {
            background: #3498db;
            color: #fff;
        }
        .pagination .active {
            background: #3498db;
            color: #fff;
            pointer-events: none;
        }
        /* Estilos para información de compras */
        .compras-info {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 0.3rem;
        }
        
        .compras-badge {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            min-width: 2rem;
            height: 2rem;
            border-radius: 50%;
            font-weight: 600;
            font-size: 0.9rem;
            color: white;
            transition: all 0.3s ease;
        }
        
        .compras-badge.has-purchases {
            background: linear-gradient(135deg, #27ae60, #2ecc71);
            box-shadow: 0 2px 8px rgba(39, 174, 96, 0.3);
        }
        
        .compras-badge.no-purchases {
            background: linear-gradient(135deg, #95a5a6, #bdc3c7);
            color: #fff;
        }
        
        .compras-details {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 0.1rem;
            font-size: 0.75rem;
            color: #7f8c8d;
            text-align: center;
        }
        
        .compras-details small {
            display: block;
            line-height: 1.2;
        }
        
        /* Responsive para móvil */
        @media (max-width: 768px) {
            .compras-badge {
                min-width: 1.8rem;
                height: 1.8rem;
                font-size: 0.8rem;
            }
            
            .compras-details {
                font-size: 0.7rem;
            }
        }
        @media (max-width: 700px) {
            .clientes-container {
                padding: 12px 4px;
            }
            table, th, td {
                font-size: 0.97rem;
            }
            .add-form {
                flex-direction: column;
                gap: 8px;
                padding: 10px 6px 6px 6px;
            }
            h1 {
                font-size: 1.5rem;
            }
        }
    </style>
</head>
<body>
    <div class="clientes-container">
        <h1><i class="fa fa-users"></i> Clientes</h1>
        <a href="/puntoDeVenta/web/Views/dashboard/index.php" class="btn-back" style="display:inline-block;margin-bottom:18px;background:#eaf6fb;color:#3498db;padding:10px 20px;border-radius:7px;font-weight:600;text-decoration:none;box-shadow:0 1px 6px #eaf6fb;transition:background 0.2s;">
            <i class="fa fa-arrow-left"></i> Volver al Panel Principal
        </a>
        <?php if ($mensaje): ?>
            <div style="background:#eaf6fb;color:#2471a3;padding:10px 18px;border-radius:7px;margin-bottom:18px;">
                <?= htmlspecialchars($mensaje) ?>
            </div>
        <?php endif; ?>
        <?php if ($editando && $clienteEditar): ?>
        <form class="add-form" method="post" action="">
            <input type="hidden" name="accion" value="editar">
            <input type="hidden" name="id" value="<?= $clienteEditar['id'] ?>">
            <label>Nombre
                <input type="text" name="nombre" value="<?= htmlspecialchars($clienteEditar['nombre']) ?>" required>
            </label>
            <label>Teléfono
                <input type="text" name="telefono" value="<?= htmlspecialchars($clienteEditar['telefono']) ?>" required>
            </label>
            <button type="submit"><i class="fa fa-save"></i> Guardar</button>
            <a href="<?= $_SERVER['PHP_SELF'] ?>" style="margin-left:10px;color:#e74c3c;text-decoration:none;font-weight:600;">Cancelar</a>
        </form>
        <?php else: ?>
        <form class="add-form" method="post" action="">
            <input type="hidden" name="accion" value="agregar">
            <label>Nombre
                <input type="text" name="nombre" required>
            </label>
            <label>Teléfono
                <input type="text" name="telefono" required>
            </label>
            <button type="submit"><i class="fa fa-plus"></i> Agregar</button>
        </form>
        <?php endif; ?>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Teléfono</th>
                    <th>Compras</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
            <?php foreach ($clientesPagina as $cliente): ?>
                <tr>
                    <td><?= $cliente['id'] ?></td>
                    <td><?= htmlspecialchars($cliente['nombre']) ?></td>
                    <td><?= htmlspecialchars($cliente['telefono']) ?></td>
                    <td>
                        <?php 
                        $numCompras = intval($cliente['total_compras']);
                        $montoTotal = floatval($cliente['monto_total_compras']);
                        $ultimaCompra = $cliente['ultima_compra'];
                        ?>
                        <div class="compras-info">
                            <span class="compras-badge <?= $numCompras > 0 ? 'has-purchases' : 'no-purchases' ?>">
                                <?= $numCompras ?>
                            </span>
                            <?php if ($numCompras > 0): ?>
                                <div class="compras-details">
                                    <small>Total: $<?= number_format($montoTotal, 2) ?></small>
                                    <?php if ($ultimaCompra): ?>
                                        <small>Última: <?= date('d/m/Y', strtotime($ultimaCompra)) ?></small>
                                    <?php endif; ?>
                                </div>
                            <?php endif; ?>
                        </div>
                    </td>
                    <td class="action-links">
                        <a href="?editar=<?= $cliente['id'] ?>"><i class="fa fa-edit"></i> Editar</a>
                        <a href="?eliminar=<?= $cliente['id'] ?>" onclick="return confirm('¿Eliminar este cliente?');"><i class="fa fa-trash"></i> Eliminar</a>
                    </td>
                </tr>
            <?php endforeach; ?>
            </tbody>
        </table>
        <?php if ($totalPaginas > 1): ?>
        <div class="pagination">
            <?php for ($i = 1; $i <= $totalPaginas; $i++): ?>
                <?php if ($i == $paginaActual): ?>
                    <span class="active" style="background:#3498db;color:#fff;padding:7px 14px;border-radius:5px;"> <?= $i ?> </span>
                <?php else: ?>
                    <a href="?pagina=<?= $i ?>" style="padding:7px 14px;border-radius:5px;color:#3498db;background:#eaf6fb;text-decoration:none;"> <?= $i ?> </a>
                <?php endif; ?>
            <?php endfor; ?>
        </div>
        <?php endif; ?>
    </div>
    <script>
    // JavaScript para funcionalidades adicionales si es necesario
    console.log('Gestión de clientes cargada - Información de ventas integrada');
    </script>
</body>
</html>
