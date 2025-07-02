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
    public function obtenerTodosConVentas() {
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
        
        $result = $this->db->query($sql);
        $clientes = [];
        while ($row = $result->fetch_assoc()) {
            $clientes[] = $row;
        }
        return $clientes;
    }

    public function obtenerVentasCliente($clienteId) {
        $stmt = $this->db->prepare("
            SELECT 
                s.id,
                s.total_amount,
                s.sale_date,
                s.payment_method,
                COUNT(si.id) as items_count
            FROM sales s 
            LEFT JOIN sale_items si ON s.id = si.sale_id
            WHERE s.cliente_id = ?
            GROUP BY s.id, s.total_amount, s.sale_date, s.payment_method
            ORDER BY s.sale_date DESC
        ");
        $stmt->bind_param('i', $clienteId);
        $stmt->execute();
        $result = $stmt->get_result();
        
        $ventas = [];
        while ($row = $result->fetch_assoc()) {
            $ventas[] = $row;
        }
        return $ventas;
    }

    public function getByName($nombre) {
        $stmt = $this->db->prepare("SELECT * FROM clientes WHERE nombre = ?");
        $stmt->bind_param('s', $nombre);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_assoc();
    }
}
