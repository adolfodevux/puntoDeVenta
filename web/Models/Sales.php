<?php
require_once 'database.php';

class Sales {
    private $db;

    public function __construct() {
        $this->db = Database::getInstance()->getConnection();
    }

    public function getAllSales() {
        $sql = "
            SELECT 
                s.id,
                s.user_id,
                u.username,
                s.cliente_id,
                c.nombre as cliente_nombre,
                s.total_amount,
                s.subtotal,
                s.tax_amount,
                s.discount_amount,
                s.payment_method,
                s.amount_paid,
                s.change_amount,
                s.sale_date,
                s.status,
                s.notes,
                COUNT(si.id) as items_count
            FROM sales s
            LEFT JOIN users u ON s.user_id = u.id
            LEFT JOIN clientes c ON s.cliente_id = c.id
            LEFT JOIN sale_items si ON s.id = si.sale_id
            GROUP BY s.id
            ORDER BY s.sale_date DESC
        ";
        
        $result = $this->db->query($sql);
        $sales = [];
        while ($row = $result->fetch_assoc()) {
            $sales[] = $row;
        }
        return $sales;
    }

    public function getSalesStats() {
        $stats = [];
        
        // Configurar zona horaria para asegurar que coincida con la del usuario
        date_default_timezone_set('America/Mexico_City'); // Ajusta según tu ubicación
        
        // Ventas totales (nuevo card)
        $result = $this->db->query("SELECT COUNT(*) as count FROM sales WHERE status = 'completed'");
        $stats['ventas_totales'] = $result->fetch_assoc()['count'];
        
        // Ingresos totales
        $result = $this->db->query("SELECT COALESCE(SUM(total_amount), 0) as total FROM sales WHERE status = 'completed'");
        $stats['ingresos_totales'] = $result->fetch_assoc()['total'];
        
        // Ventas de "hoy" - buscar en las últimas 24 horas para evitar problemas de zona horaria
        $result = $this->db->query("
            SELECT COUNT(*) as count 
            FROM sales 
            WHERE sale_date >= DATE_SUB(NOW(), INTERVAL 24 HOUR) 
            AND status = 'completed'
        ");
        $stats['ventas_hoy'] = $result->fetch_assoc()['count'];
        
        // Ingresos de "hoy" - buscar en las últimas 24 horas
        $result = $this->db->query("
            SELECT COALESCE(SUM(total_amount), 0) as total 
            FROM sales 
            WHERE sale_date >= DATE_SUB(NOW(), INTERVAL 24 HOUR) 
            AND status = 'completed'
        ");
        $stats['ingresos_hoy'] = $result->fetch_assoc()['total'];
        
        // Ventas de esta semana
        $result = $this->db->query("SELECT COUNT(*) as count FROM sales WHERE WEEK(sale_date) = WEEK(NOW()) AND YEAR(sale_date) = YEAR(NOW()) AND status = 'completed'");
        $stats['ventas_semana'] = $result->fetch_assoc()['count'];
        
        // Ventas de este mes
        $result = $this->db->query("SELECT COUNT(*) as count FROM sales WHERE MONTH(sale_date) = MONTH(NOW()) AND YEAR(sale_date) = YEAR(NOW()) AND status = 'completed'");
        $stats['ventas_mes'] = $result->fetch_assoc()['count'];
        
        return $stats;
    }

    public function getSalesByDateRange($startDate, $endDate) {
        $stmt = $this->db->prepare("
            SELECT 
                s.id,
                s.user_id,
                u.username,
                s.cliente_id,
                c.nombre as cliente_nombre,
                s.total_amount,
                s.payment_method,
                s.sale_date,
                s.status,
                COUNT(si.id) as items_count
            FROM sales s
            LEFT JOIN users u ON s.user_id = u.id
            LEFT JOIN clientes c ON s.cliente_id = c.id
            LEFT JOIN sale_items si ON s.id = si.sale_id
            WHERE DATE(s.sale_date) BETWEEN ? AND ?
            GROUP BY s.id
            ORDER BY s.sale_date DESC
        ");
        
        $stmt->bind_param('ss', $startDate, $endDate);
        $stmt->execute();
        $result = $stmt->get_result();
        
        $sales = [];
        while ($row = $result->fetch_assoc()) {
            $sales[] = $row;
        }
        return $sales;
    }

    public function getSalesByPaymentMethod($paymentMethod) {
        $stmt = $this->db->prepare("
            SELECT 
                s.id,
                s.user_id,
                u.username,
                s.cliente_id,
                c.nombre as cliente_nombre,
                s.total_amount,
                s.payment_method,
                s.sale_date,
                s.status,
                COUNT(si.id) as items_count
            FROM sales s
            LEFT JOIN users u ON s.user_id = u.id
            LEFT JOIN clientes c ON s.cliente_id = c.id
            LEFT JOIN sale_items si ON s.id = si.sale_id
            WHERE s.payment_method = ?
            GROUP BY s.id
            ORDER BY s.sale_date DESC
        ");
        
        $stmt->bind_param('s', $paymentMethod);
        $stmt->execute();
        $result = $stmt->get_result();
        
        $sales = [];
        while ($row = $result->fetch_assoc()) {
            $sales[] = $row;
        }
        return $sales;
    }

    public function getSaleById($saleId) {
        $stmt = $this->db->prepare("
            SELECT 
                s.id,
                s.user_id,
                u.username,
                s.cliente_id,
                c.nombre as customer_name,
                s.total_amount as total,
                s.subtotal,
                s.tax_amount,
                s.discount_amount,
                s.payment_method,
                s.amount_paid,
                s.change_amount,
                s.sale_date,
                s.status,
                s.notes
            FROM sales s
            LEFT JOIN users u ON s.user_id = u.id
            LEFT JOIN clientes c ON s.cliente_id = c.id
            WHERE s.id = ?
        ");
        
        $stmt->bind_param("i", $saleId);
        $stmt->execute();
        $result = $stmt->get_result();
        
        return $result->fetch_assoc();
    }

    public function getSaleDetails($saleId) {
        $stmt = $this->db->prepare("
            SELECT 
                s.*,
                u.username,
                c.nombre as cliente_nombre,
                c.telefono as cliente_telefono
            FROM sales s
            LEFT JOIN users u ON s.user_id = u.id
            LEFT JOIN clientes c ON s.cliente_id = c.id
            WHERE s.id = ?
        ");
        
        $stmt->bind_param('i', $saleId);
        $stmt->execute();
        $result = $stmt->get_result();
        $sale = $result->fetch_assoc();
        
        if ($sale) {
            // Obtener items de la venta
            $itemsStmt = $this->db->prepare("
                SELECT 
                    si.id,
                    si.product_id,
                    si.product_name,
                    si.quantity,
                    si.unit_price as price,
                    si.total_price
                FROM sale_items si
                WHERE si.sale_id = ?
                ORDER BY si.id
            ");
            
            $itemsStmt->bind_param('i', $saleId);
            $itemsStmt->execute();
            $itemsResult = $itemsStmt->get_result();
            
            $items = [];
            while ($item = $itemsResult->fetch_assoc()) {
                $items[] = $item;
            }
            
            $sale['items'] = $items;
        }
        
        return $sale;
    }

    public function deleteSale($id) {
        // Primero eliminar los items de la venta
        $stmt1 = $this->db->prepare("DELETE FROM sale_items WHERE sale_id = ?");
        $stmt1->bind_param('i', $id);
        $stmt1->execute();
        
        // Luego eliminar la venta
        $stmt2 = $this->db->prepare("DELETE FROM sales WHERE id = ?");
        $stmt2->bind_param('i', $id);
        return $stmt2->execute();
    }

    public function getAllSalesWithPagination($page = 1, $limit = 10, $paymentMethod = '', $dateFilter = '', $searchFilter = '') {
        $offset = ($page - 1) * $limit;
        
        $whereClause = "WHERE 1=1";
        $params = [];
        $types = "";
        
        if ($dateFilter) {
            // Si el filtro de fecha está en formato YYYY-MM, filtrar por mes
            if (preg_match('/^\d{4}-\d{2}$/', $dateFilter)) {
                $whereClause .= " AND DATE_FORMAT(s.sale_date, '%Y-%m') = ?";
                $params[] = $dateFilter;
                $types .= "s";
            } else {
                // Si es una fecha completa, filtrar por día
                $whereClause .= " AND DATE(s.sale_date) = ?";
                $params[] = $dateFilter;
                $types .= "s";
            }
        }
        
        if ($searchFilter) {
            // Buscar por ID de venta, nombre de cliente o total de venta
            $whereClause .= " AND (
                s.id LIKE ? OR 
                c.nombre LIKE ? OR 
                s.total_amount LIKE ?
            )";
            $searchParam = '%' . $searchFilter . '%';
            $params[] = $searchParam;
            $params[] = $searchParam;
            $params[] = $searchParam;
            $types .= "sss";
        }
        
        $sql = "
            SELECT 
                s.id,
                s.user_id,
                u.username,
                s.cliente_id,
                c.nombre as cliente_nombre,
                s.total_amount,
                s.subtotal,
                s.tax_amount,
                s.discount_amount,
                s.payment_method,
                s.amount_paid,
                s.change_amount,
                s.sale_date,
                s.status,
                s.notes,
                COUNT(si.id) as items_count
            FROM sales s
            LEFT JOIN users u ON s.user_id = u.id
            LEFT JOIN clientes c ON s.cliente_id = c.id
            LEFT JOIN sale_items si ON s.id = si.sale_id
            {$whereClause}
            GROUP BY s.id
            ORDER BY s.sale_date DESC
            LIMIT ? OFFSET ?
        ";
        
        $params[] = $limit;
        $params[] = $offset;
        $types .= "ii";
        
        $stmt = $this->db->prepare($sql);
        if (!empty($params)) {
            $stmt->bind_param($types, ...$params);
        }
        $stmt->execute();
        $result = $stmt->get_result();
        
        $sales = [];
        while ($row = $result->fetch_assoc()) {
            $sales[] = $row;
        }
        
        return $sales;
    }
    
    public function getTotalSalesCount($paymentMethod = '', $dateFilter = '', $searchFilter = '') {
        $whereClause = "WHERE 1=1";
        $params = [];
        $types = "";
        
        if ($dateFilter) {
            // Si el filtro de fecha está en formato YYYY-MM, filtrar por mes
            if (preg_match('/^\d{4}-\d{2}$/', $dateFilter)) {
                $whereClause .= " AND DATE_FORMAT(sale_date, '%Y-%m') = ?";
                $params[] = $dateFilter;
                $types .= "s";
            } else {
                // Si es una fecha completa, filtrar por día
                $whereClause .= " AND DATE(sale_date) = ?";
                $params[] = $dateFilter;
                $types .= "s";
            }
        }
        
        if ($searchFilter) {
            // Buscar por ID de venta, nombre de cliente o total de venta
            // Necesitamos hacer JOIN con clientes para buscar por nombre
            $sql = "
                SELECT COUNT(DISTINCT s.id) as total 
                FROM sales s
                LEFT JOIN clientes c ON s.cliente_id = c.id
                {$whereClause}
                AND (
                    s.id LIKE ? OR 
                    c.nombre LIKE ? OR 
                    s.total_amount LIKE ?
                )
            ";
            $searchParam = '%' . $searchFilter . '%';
            $params[] = $searchParam;
            $params[] = $searchParam;
            $params[] = $searchParam;
            $types .= "sss";
        } else {
            $sql = "SELECT COUNT(*) as total FROM sales {$whereClause}";
        }
        
        if (!empty($params)) {
            $stmt = $this->db->prepare($sql);
            $stmt->bind_param($types, ...$params);
            $stmt->execute();
            $result = $stmt->get_result();
        } else {
            $result = $this->db->query($sql);
        }
        
        return $result->fetch_assoc()['total'];
    }
}
