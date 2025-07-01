<?php
// Formulario para crear o editar cliente
$esEditar = isset($cliente);
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title><?= $esEditar ? 'Editar' : 'Agregar' ?> Cliente</title>
    <link rel="stylesheet" href="/Assets/css/dashboard.css">
</head>
<body>
    <h1><?= $esEditar ? 'Editar' : 'Agregar' ?> Cliente</h1>
    <form method="post" action="">
        <input type="hidden" name="id" value="<?= $esEditar ? $cliente['id'] : '' ?>">
        <label>Nombre:<input type="text" name="nombre" value="<?= $esEditar ? $cliente['nombre'] : '' ?>" required></label><br>
        <label>Teléfono:<input type="text" name="telefono" value="<?= $esEditar ? $cliente['telefono'] : '' ?>" required></label><br>
        <button type="submit">Guardar</button>
        <a href="?action=clientes">Cancelar</a>
    </form>
</body>
</html>
