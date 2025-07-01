-- Script SQL para crear las tablas del sistema POS
-- Base de datos: puntoDeVenta

USE puntoDeVenta;

-- Crear tabla de usuarios
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_created_at (created_at)
);

-- Crear tabla de categorías
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla de productos
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    cost DECIMAL(10,2) DEFAULT 0.00,
    stock INT DEFAULT 0,
    min_stock INT DEFAULT 0,
    category_id INT,
    barcode VARCHAR(50) UNIQUE,
    image_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_name (name),
    INDEX idx_barcode (barcode),
    INDEX idx_category (category_id),
    INDEX idx_stock (stock)
);

-- Crear tabla de ventas
CREATE TABLE IF NOT EXISTS sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    payment_method ENUM('cash', 'card', 'transfer') NOT NULL,
    amount_paid DECIMAL(10,2) NOT NULL,
    change_amount DECIMAL(10,2) DEFAULT 0.00,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pending', 'completed', 'cancelled') DEFAULT 'pending',
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_user (user_id),
    INDEX idx_date (sale_date),
    INDEX idx_status (status)
);

-- Crear tabla de items de venta
CREATE TABLE IF NOT EXISTS sale_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    product_id INT,
    product_name VARCHAR(200) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL,
    INDEX idx_sale (sale_id),
    INDEX idx_product (product_id)
);

-- Crear tabla de clientes
CREATE TABLE IF NOT EXISTS customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    address TEXT,
    document_type ENUM('dni', 'ruc', 'passport') DEFAULT 'dni',
    document_number VARCHAR(20) UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_email (email),
    INDEX idx_document (document_number)
);

-- Tabla de clientes
CREATE TABLE IF NOT EXISTS clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL
);

-- Insertar categorías de ejemplo
INSERT INTO categories (name, description) VALUES 
('Bebidas', 'Bebidas refrescantes y gaseosas'),
('Snacks', 'Aperitivos y botanas'),
('Dulces', 'Dulces y golosinas'),
('Otros', 'Productos varios')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Insertar productos de ejemplo
INSERT INTO products (name, description, price, cost, stock, min_stock, category_id, barcode) VALUES 
('Coca Cola 500ml', 'Gaseosa Coca Cola 500ml', 2.50, 1.50, 50, 10, 1, '7750182003919'),
('Pepsi 500ml', 'Gaseosa Pepsi 500ml', 2.30, 1.40, 35, 10, 1, '7750182003926'),
('Inca Kola 500ml', 'Gaseosa Inca Kola 500ml', 2.40, 1.45, 42, 10, 1, '7750182003933'),
('Doritos Nacho', 'Doritos sabor nacho cheese 150g', 1.80, 1.20, 25, 5, 2, '7750182004001'),
('Papas Lays', 'Papas Lays clásicas 150g', 1.70, 1.10, 30, 5, 2, '7750182004018'),
('Chicles Trident', 'Chicles Trident menta', 0.50, 0.30, 100, 20, 3, '7750182004025'),
('Chocolate Sublime', 'Chocolate Sublime con maní', 1.20, 0.80, 60, 15, 3, '7750182004032'),
('Galletas Oreo', 'Galletas Oreo original', 2.00, 1.30, 40, 10, 3, '7750182004049')
ON DUPLICATE KEY UPDATE 
    price = VALUES(price),
    cost = VALUES(cost),
    stock = VALUES(stock);

-- Insertar usuario administrador (contraseña: admin123 - MD5: 0192023a7bbd73250516f069df18b500)
INSERT INTO users (username, email, password, created_at) VALUES 
('admin', 'admin@puntod.com', '0192023a7bbd73250516f069df18b500', NOW())
ON DUPLICATE KEY UPDATE password = '0192023a7bbd73250516f069df18b500';

-- Insertar cliente genérico
INSERT INTO customers (name, email, document_type, document_number) VALUES 
('Cliente General', 'general@cliente.com', 'dni', '00000000')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Verificar que las tablas se crearon correctamente
DESCRIBE users;
DESCRIBE categories;
DESCRIBE products;
DESCRIBE sales;
DESCRIBE sale_items;
DESCRIBE customers;
DESCRIBE clientes;

-- Mostrar datos de ejemplo
SELECT 'USUARIOS:' as 'TABLA';
SELECT id, username, email, is_active, created_at FROM users;

SELECT 'CATEGORÍAS:' as 'TABLA';
SELECT * FROM categories;

SELECT 'PRODUCTOS:' as 'TABLA';
SELECT p.id, p.name, p.price, p.stock, c.name as category 
FROM products p 
LEFT JOIN categories c ON p.category_id = c.id;

SELECT 'CLIENTES:' as 'TABLA';
SELECT id, name, email, document_number FROM customers;
