<?php

class Sales {
    private $db;

    public function __construct($database) {
        $this->db = $database;
    }

    public function getAllSales() {
        $query = "SELECT * FROM sales";
        return $this->db->query($query)->fetchAll();
    }

    public function addSale($data) {
        $query = "INSERT INTO sales (product_id, quantity, total_price, sale_date) VALUES (:product_id, :quantity, :total_price, :sale_date)";
        $stmt = $this->db->prepare($query);
        return $stmt->execute($data);
    }

    public function getSaleById($id) {
        $query = "SELECT * FROM sales WHERE id = :id";
        $stmt = $this->db->prepare($query);
        $stmt->execute(['id' => $id]);
        return $stmt->fetch();
    }

    public function deleteSale($id) {
        $query = "DELETE FROM sales WHERE id = :id";
        $stmt = $this->db->prepare($query);
        return $stmt->execute(['id' => $id]);
    }
}
