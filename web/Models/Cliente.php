<?php
require_once 'database.php';
class Cliente {
    private $db;
    public function __construct() {
        $this->db = Database::getInstance()->getConnection();
    }
    public function obtenerTodos() {
        $result = $this->db->query("SELECT * FROM clientes");
        $clientes = [];
        while ($row = $result->fetch_assoc()) {
            $clientes[] = $row;
        }
        return $clientes;
    }
    public function obtenerPorId($id) {
        $stmt = $this->db->prepare("SELECT * FROM clientes WHERE id = ?");
        $stmt->bind_param('i', $id);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_assoc();
    }
    public function crear($nombre, $telefono) {
        $stmt = $this->db->prepare("INSERT INTO clientes (nombre, telefono) VALUES (?, ?)");
        $stmt->bind_param('ss', $nombre, $telefono);
        return $stmt->execute();
    }
    public function actualizar($id, $nombre, $telefono) {
        $stmt = $this->db->prepare("UPDATE clientes SET nombre=?, telefono=? WHERE id=?");
        $stmt->bind_param('ssi', $nombre, $telefono, $id);
        return $stmt->execute();
    }
    public function eliminar($id) {
        $stmt = $this->db->prepare("DELETE FROM clientes WHERE id=?");
        $stmt->bind_param('i', $id);
        return $stmt->execute();
    }
}
