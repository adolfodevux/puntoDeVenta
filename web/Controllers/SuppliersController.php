<?php
require_once __DIR__ . '/../Models/Supplier.php';
require_once __DIR__ . '/../Models/database.php';

class SuppliersController {
    private $supplier;

    public function __construct() {
        $this->supplier = new Supplier();
    }

    public function index() {
        $suppliers = $this->supplier->getAll();
        $stats = $this->supplier->getStats();
        
        return [
            'suppliers' => $suppliers,
            'stats' => $stats
        ];
    }

    public function show($id) {
        $supplier = $this->supplier->getById($id);
        if (!$supplier) {
            return ['error' => 'Proveedor no encontrado'];
        }
        
        $products = $this->supplier->getSupplierProducts($id);
        
        return [
            'supplier' => $supplier,
            'products' => $products
        ];
    }

    public function create($data) {
        // Validaciones
        $errors = $this->validateSupplierData($data);
        if (!empty($errors)) {
            return ['errors' => $errors];
        }

        if ($this->supplier->create($data)) {
            return ['success' => 'Proveedor creado exitosamente'];
        } else {
            return ['error' => 'Error al crear el proveedor'];
        }
    }

    public function update($id, $data) {
        // Validaciones
        $errors = $this->validateSupplierData($data, $id);
        if (!empty($errors)) {
            return ['errors' => $errors];
        }

        if ($this->supplier->update($id, $data)) {
            return ['success' => 'Proveedor actualizado exitosamente'];
        } else {
            return ['error' => 'Error al actualizar el proveedor'];
        }
    }

    public function delete($id) {
        if ($this->supplier->delete($id)) {
            return ['success' => 'Proveedor eliminado exitosamente'];
        } else {
            return ['error' => 'Error al eliminar el proveedor'];
        }
    }

    public function search($term) {
        $suppliers = $this->supplier->search($term);
        return ['suppliers' => $suppliers];
    }

    private function validateSupplierData($data, $excludeId = null) {
        $errors = [];

        // Validar nombre (requerido)
        if (empty($data['name']) || strlen(trim($data['name'])) < 2) {
            $errors['name'] = 'El nombre del proveedor es requerido y debe tener al menos 2 caracteres';
        }

        // Validar email si se proporciona
        if (!empty($data['email']) && !filter_var($data['email'], FILTER_VALIDATE_EMAIL)) {
            $errors['email'] = 'El formato del email no es válido';
        }

        // Validar teléfono si se proporciona
        if (!empty($data['phone'])) {
            $phone = preg_replace('/[^0-9+\-\s()]/', '', $data['phone']);
            if (strlen($phone) < 8) {
                $errors['phone'] = 'El teléfono debe tener al menos 8 dígitos';
            }
        }

        // Validar URL del sitio web si se proporciona
        if (!empty($data['website'])) {
            if (!filter_var($data['website'], FILTER_VALIDATE_URL)) {
                $errors['website'] = 'El formato de la URL del sitio web no es válido';
            }
        }

        // Validar unicidad del email
        if (!empty($data['email'])) {
            $existing = $this->checkEmailExists($data['email'], $excludeId);
            if ($existing) {
                $errors['email'] = 'Ya existe un proveedor con este email';
            }
        }

        // Validar unicidad del tax_id si se proporciona
        if (!empty($data['tax_id'])) {
            $existing = $this->checkTaxIdExists($data['tax_id'], $excludeId);
            if ($existing) {
                $errors['tax_id'] = 'Ya existe un proveedor con este ID fiscal';
            }
        }

        return $errors;
    }

    private function checkEmailExists($email, $excludeId = null) {
        $db = Database::getInstance()->getConnection();
        $query = "SELECT id FROM suppliers WHERE email = ? AND is_active = 1";
        
        if ($excludeId) {
            $query .= " AND id != ?";
            $stmt = $db->prepare($query);
            $stmt->bind_param('si', $email, $excludeId);
        } else {
            $stmt = $db->prepare($query);
            $stmt->bind_param('s', $email);
        }
        
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_assoc() !== null;
    }

    private function checkTaxIdExists($taxId, $excludeId = null) {
        $db = Database::getInstance()->getConnection();
        $query = "SELECT id FROM suppliers WHERE tax_id = ? AND is_active = 1";
        
        if ($excludeId) {
            $query .= " AND id != ?";
            $stmt = $db->prepare($query);
            $stmt->bind_param('si', $taxId, $excludeId);
        } else {
            $stmt = $db->prepare($query);
            $stmt->bind_param('s', $taxId);
        }
        
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_assoc() !== null;
    }

    public function getForSelect() {
        $suppliers = $this->supplier->getAll();
        $options = [];
        foreach ($suppliers as $supplier) {
            $options[] = [
                'value' => $supplier['id'],
                'text' => $supplier['name']
            ];
        }
        return $options;
    }
}
?>
