<?php
require_once __DIR__ . '/../Models/Cliente.php';

class ClientesController {
    private $clienteModel;
    public function __construct() {
        $this->clienteModel = new Cliente();
    }
    public function index() {
        $clientes = $this->clienteModel->obtenerTodos();
        include __DIR__ . '/../Views/clientes/index.php';
    }
    public function crear() {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $this->clienteModel->crear($_POST['nombre'], $_POST['telefono']);
            header('Location: ?action=clientes');
            exit;
        }
        include __DIR__ . '/../Views/clientes/form.php';
    }
    public function editar() {
        $id = $_GET['id'] ?? null;
        if (!$id) { header('Location: ?action=clientes'); exit; }
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $this->clienteModel->actualizar($id, $_POST['nombre'], $_POST['telefono']);
            header('Location: ?action=clientes');
            exit;
        }
        $cliente = $this->clienteModel->obtenerPorId($id);
        include __DIR__ . '/../Views/clientes/form.php';
    }
    public function eliminar() {
        $id = $_GET['id'] ?? null;
        if ($id) {
            $this->clienteModel->eliminar($id);
        }
        header('Location: ?action=clientes');
        exit;
    }
}
