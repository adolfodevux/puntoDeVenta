-- Script SQL para crear las tablas de ventas en la base de datos puntoDeVenta
-- Ejecutar este script en MySQL/MariaDB

USE puntoDeVenta;

-- Crear tabla de ventas
CREATE TABLE IF NOT EXISTS sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    cliente_id INT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    payment_method ENUM('cash', 'card', 'transfer') NOT NULL DEFAULT 'cash',
    amount_paid DECIMAL(10,2) NOT NULL,
    change_amount DECIMAL(10,2) DEFAULT 0.00,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pending', 'completed', 'cancelled') DEFAULT 'completed',
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE SET NULL,
    INDEX idx_user (user_id),
    INDEX idx_cliente (cliente_id),
    INDEX idx_date (sale_date),
    INDEX idx_status (status)
);

-- Crear tabla de items de venta
CREATE TABLE IF NOT EXISTS sale_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    product_id INT NULL,
    product_name VARCHAR(200) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL,
    INDEX idx_sale (sale_id),
    INDEX idx_product (product_id)
);

-- Insertar algunos clientes de ejemplo si no existen
INSERT INTO clientes (nombre, telefono) VALUES 
('Juan Pérez', '555-1234'),
('María López', '555-5678'),
('Carlos Sánchez', '555-8765'),
('Ana Torres', '555-4321'),
('Luis Ramírez', '555-2468'),
('Sofia González', '555-1357'),
('Miguel Herrera', '555-9642'),
('Carmen Morales', '555-7531'),
('Roberto Silva', '555-8642'),
('Patricia Vargas', '555-9753'),
('Adolfo Gustavo', '555'),
('Joje', '124578')
ON DUPLICATE KEY UPDATE telefono = VALUES(telefono);

-- Verificar que las tablas se crearon correctamente
SELECT 'TABLAS CREADAS EXITOSAMENTE' as RESULTADO;

DESCRIBE sales;
DESCRIBE sale_items;

SELECT 'CLIENTES DISPONIBLES:' as INFO;
SELECT id, nombre, telefono FROM clientes ORDER BY id;

SELECT 'TOTAL DE CLIENTES:' as INFO, COUNT(*) as cantidad FROM clientes;
