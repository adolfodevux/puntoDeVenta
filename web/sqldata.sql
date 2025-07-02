-- --------------------------------------------------------
-- Host:                         40.76.137.253
-- Versión del servidor:        10.11.11-MariaDB-0+deb12u1 - Debian 12
-- SO del servidor:              debian-linux-gnu
-- HeidiSQL Versión:            12.11.1.167
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Volcando estructura de base de datos para puntoDeVenta
CREATE DATABASE IF NOT EXISTS `puntoDeVenta` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish2_ci */;
USE `puntoDeVenta`;

-- Volcando estructura para tabla puntoDeVenta.categories
CREATE TABLE IF NOT EXISTS `categories` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish2_ci;

-- Volcando datos para la tabla puntoDeVenta.categories: ~7 rows (aproximadamente)
INSERT INTO `categories` (`id`, `name`, `description`, `is_active`, `created_at`) VALUES
	(1, 'Bebidas', 'Bebidas refrescantes y gaseosas', 1, '2025-06-30 19:26:54'),
	(2, 'Snacks', 'Aperitivos y botanas', 1, '2025-06-30 19:26:54'),
	(3, 'Dulces', 'Dulces y golosinas', 1, '2025-06-30 19:26:54'),
	(4, 'Otros', 'Productos varios', 1, '2025-06-30 19:26:54'),
	(5, 'Comida', 'Categoria de comida', 1, '2025-07-02 02:55:37'),
	(7, 'Mercancia', 'Merch relacionado con nuestra mascota Tux', 1, '2025-07-02 02:56:30'),
	(11, 'Regalos', 'Mercancia para regalar', 1, '2025-07-02 17:12:47');

-- Volcando estructura para tabla puntoDeVenta.clientes
CREATE TABLE IF NOT EXISTS `clientes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `telefono` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish2_ci;

-- Volcando datos para la tabla puntoDeVenta.clientes: ~8 rows (aproximadamente)
INSERT INTO `clientes` (`id`, `nombre`, `telefono`) VALUES
	(1, 'Juan Jose', '555-1255'),
	(3, 'Carlos Sánchez', '555-8769'),
	(4, 'Ana Torres', '555-4321'),
	(5, 'Luis Ramírez', '555-2468'),
	(9, 'Adolfo Gustavo', '555'),
	(10, 'Joje', '124578'),
	(11, 'Dui', '7878787878'),
	(12, 'Gama', '333212313');

-- Volcando estructura para tabla puntoDeVenta.customers
CREATE TABLE IF NOT EXISTS `customers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `document_type` enum('dni','ruc','passport') DEFAULT 'dni',
  `document_number` varchar(20) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `document_number` (`document_number`),
  KEY `idx_name` (`name`),
  KEY `idx_email` (`email`),
  KEY `idx_document` (`document_number`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish2_ci;

-- Volcando datos para la tabla puntoDeVenta.customers: ~1 rows (aproximadamente)
INSERT INTO `customers` (`id`, `name`, `email`, `phone`, `address`, `document_type`, `document_number`, `is_active`, `created_at`) VALUES
	(1, 'Cliente General', 'general@cliente.com', NULL, NULL, 'dni', '00000000', 1, '2025-06-30 19:26:55');

-- Volcando estructura para tabla puntoDeVenta.suppliers
CREATE TABLE IF NOT EXISTS `suppliers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `contact_person` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `country` varchar(100) DEFAULT NULL,
  `tax_id` varchar(50) DEFAULT NULL,
  `website` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `tax_id` (`tax_id`),
  KEY `idx_name` (`name`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish2_ci;

-- Volcando datos para la tabla puntoDeVenta.suppliers: ~5 rows (aproximadamente)
INSERT INTO `suppliers` (`id`, `name`, `contact_person`, `email`, `phone`, `address`, `city`, `country`, `tax_id`, `website`, `notes`, `is_active`, `created_at`) VALUES
	(1, 'Coca-Cola Company', 'Juan Rodriguez', 'contacto@cocacola.com', '555-0001', 'Av. Principal 123', 'Ciudad de México', 'México', 'CCE123456789', 'https://www.coca-cola.com', 'Proveedor principal de bebidas gaseosas', 1, '2025-07-02 20:00:00'),
	(2, 'Frito-Lay', 'María González', 'ventas@fritolay.com', '555-0002', 'Industrial Park 456', 'Guadalajara', 'México', 'FLM987654321', 'https://www.fritolay.com', 'Snacks y botanas', 1, '2025-07-02 20:00:00'),
	(3, 'Hershey\'s México', 'Carlos Mendoza', 'mexico@hersheys.com', '555-0003', 'Zona Industrial 789', 'Monterrey', 'México', 'HSM456789123', 'https://www.hersheys.com.mx', 'Chocolates y dulces', 1, '2025-07-02 20:00:00'),
	(4, 'Distribuidora Local', 'Ana Pérez', 'info@distlocal.com', '555-0004', 'Calle Comercio 321', 'Ciudad Local', 'México', 'DL789123456', NULL, 'Distribuidora de productos varios', 1, '2025-07-02 20:00:00'),
	(5, 'Productos de Limpieza SA', 'Roberto Silva', 'contacto@limpiezasa.com', '555-0005', 'Parque Industrial 654', 'Puebla', 'México', 'PL321654987', 'https://www.limpiezasa.com', 'Productos de limpieza y mantenimiento', 1, '2025-07-02 20:00:00');

-- Volcando estructura para tabla puntoDeVenta.products
CREATE TABLE IF NOT EXISTS `products` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `description` text DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `cost` decimal(10,2) DEFAULT 0.00,
  `stock` int(11) DEFAULT 0,
  `min_stock` int(11) DEFAULT 0,
  `category_id` int(11) DEFAULT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  `barcode` varchar(50) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `barcode` (`barcode`),
  KEY `idx_name` (`name`),
  KEY `idx_barcode` (`barcode`),
  KEY `idx_category` (`category_id`),
  KEY `idx_supplier` (`supplier_id`),
  KEY `idx_stock` (`stock`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL,
  CONSTRAINT `products_ibfk_2` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish2_ci;

-- Volcando datos para la tabla puntoDeVenta.products: ~84 rows (aproximadamente)
INSERT INTO `products` (`id`, `name`, `description`, `price`, `cost`, `stock`, `min_stock`, `category_id`, `barcode`, `image_url`, `is_active`, `created_at`, `updated_at`) VALUES
	(1, 'Coca Cola 500ml', 'Gaseosa Coca Cola 500ml', 2.50, 1.50, 50, 10, 1, '7750182003919', NULL, 1, '2025-06-30 19:26:55', '2025-06-30 19:26:55'),
	(2, 'Pepsi 500ml', 'Gaseosa Pepsi 500ml', 2.30, 1.40, 35, 10, 1, '7750182003926', NULL, 1, '2025-06-30 19:26:55', '2025-06-30 19:26:55'),
	(3, 'Inca Kola 500ml', 'Gaseosa Inca Kola 500ml', 2.40, 1.45, 42, 10, 1, '7750182003933', NULL, 1, '2025-06-30 19:26:55', '2025-06-30 19:26:55'),
	(4, 'Doritos Nacho', 'Doritos sabor nacho cheese 150g', 1.80, 1.20, 25, 5, 2, '7750182004001', NULL, 0, '2025-06-30 19:26:55', '2025-07-01 02:58:55'),
	(5, 'Papas Lays', 'Papas Lays clásicas 150g', 1.70, 1.10, 30, 5, 2, '7750182004018', NULL, 1, '2025-06-30 19:26:55', '2025-06-30 19:26:55'),
	(6, 'Chicles Trident', 'Chicles Trident menta', 0.50, 0.30, 97, 20, 3, '7750182004025', NULL, 1, '2025-06-30 19:26:55', '2025-07-02 01:11:24'),
	(7, 'Chocolate semi', 'Chocolate sabor amargo', 1.00, 0.80, 47, 15, 3, '7750182004032', NULL, 1, '2025-06-30 19:26:55', '2025-07-02 01:11:24'),
	(8, 'Galletas Oreo', 'Galletas Oreo original', 2.00, 1.30, 40, 10, 3, '7750182004049', NULL, 1, '2025-06-30 19:26:55', '2025-06-30 19:26:55'),
	(9, 'Menta', 'Dulce sabor cereza', 0.50, 0.00, 300, 0, 3, '7822548120031', NULL, 1, '2025-07-01 02:57:47', '2025-07-01 15:40:13'),
	(10, 'Limoncito', 'Sabor acido', 0.50, 0.00, 7, 0, 3, '3320097165003', NULL, 1, '2025-07-01 03:25:20', '2025-07-01 15:58:17'),
	(11, 'galleta', 'de vainilla', 12.00, 0.00, 6, 0, 3, '0597825923693', NULL, 0, '2025-07-01 04:32:38', '2025-07-01 04:33:23'),
	(12, 'Palomitas de sal', 'Sabor mantequilla', 1.50, 0.00, 30, 0, 3, '7276923164501', NULL, 1, '2025-07-01 05:09:58', '2025-07-01 15:57:31'),
	(13, 'Gomitas', 'Sabor fresa', 2.00, 0.00, 49, 0, 2, '2104577656637', NULL, 1, '2025-07-01 05:16:52', '2025-07-01 15:39:19'),
	(14, 'Tux', 'Sabor uva', 2.00, 0.00, 50, 0, 4, '8852346571192', NULL, 1, '2025-07-01 13:29:41', '2025-07-01 13:29:41'),
	(15, 'Jugo de naranja (1 litro)', 'Zumo de frutas natural o procesado, rico en vitamina C, con sabor dulce y ligeramente ácido.', 2.00, 0.00, 50, 0, 1, '4673263363182', NULL, 1, '2025-07-01 15:36:43', '2025-07-01 15:39:34'),
	(16, 'Cerveza', 'Bebida alcohólica fermentada a base de cebada y lúpulo, con un sabor ligeramente amargo y refrescante.', 1.40, 0.00, 34, 0, 1, '2585088220171', NULL, 1, '2025-07-01 15:38:44', '2025-07-02 15:30:04'),
	(17, 'Snickers', 'Barra de chocolate con caramelo, turrón y cacahuates.', 1.50, 0.00, 50, 0, 3, '1443498051055', NULL, 1, '2025-07-01 15:40:16', '2025-07-01 15:40:16'),
	(18, 'Café americano (regular)', 'Café negro preparado diluyendo un espresso con agua caliente, de sabor suave y sin leche.', 1.65, 0.00, 43, 0, 1, '3141276583502', NULL, 1, '2025-07-01 15:42:18', '2025-07-02 01:11:23'),
	(19, 'Leche (1 litro)', 'Bebida nutritiva de origen animal (o vegetal como almendra, soya), rica en calcio y proteínas.', 2.00, 0.00, 50, 0, 1, '1509157378536', NULL, 1, '2025-07-01 15:42:59', '2025-07-01 15:42:59'),
	(20, 'M&M\'s', 'Chocolates recubiertos de caramelo de colores.', 1.50, 0.00, 50, 0, 3, '1452246150320', NULL, 1, '2025-07-01 15:43:00', '2025-07-01 15:43:00'),
	(21, 'Skittles', 'Caramelos masticables de frutas con colores brillantes.', 1.50, 0.00, 50, 0, 3, '2851878229679', NULL, 1, '2025-07-01 15:43:49', '2025-07-01 15:43:49'),
	(22, 'Twix', 'Barra de galleta con caramelo y cobertura de chocolate.', 1.50, 0.00, 50, 0, 3, '6729019417119', NULL, 0, '2025-07-01 15:44:18', '2025-07-01 16:11:11'),
	(23, 'Bebida energética (lata 500 ml)', 'Bebida estimulante con altos niveles de cafeína, taurina y vitaminas, diseñada para aumentar la energía.', 2.00, 0.00, 47, 0, 1, '1063660490244', NULL, 1, '2025-07-01 15:44:27', '2025-07-02 15:59:28'),
	(24, 'Kit Kat', 'Galleta crujiente cubierta de chocolate, ideal para partir en barras.', 1.50, 0.00, 50, 0, 3, '5076822748080', NULL, 1, '2025-07-01 15:44:52', '2025-07-01 15:44:52'),
	(25, 'Smoothie de frutas (pre-hecho)', 'Bebida espesa y cremosa hecha a base de frutas licuadas, a menudo con yogurt o leche.', 3.50, 0.00, 50, 0, 1, '9840658052572', NULL, 1, '2025-07-01 15:45:16', '2025-07-01 15:45:16'),
	(26, 'Hershey\'s Milk Chocolate', 'Clásica barra de chocolate con leche.', 1.50, 0.00, 48, 0, 3, '1552559492470', NULL, 1, '2025-07-01 15:45:57', '2025-07-02 16:02:56'),
	(27, 'Reese\'s', 'Tazas de mantequilla de maní cubiertas de chocolate.', 1.75, 0.00, 50, 0, 3, '6832206278229', NULL, 1, '2025-07-01 15:46:35', '2025-07-01 15:46:35'),
	(28, 'Agua mineral (600 ml)', 'Agua con gas natural o añadido, que contiene minerales disueltos y ofrece una sensación efervescente.', 2.40, 0.00, 4, 0, 1, '0110344446323', NULL, 1, '2025-07-01 15:46:36', '2025-07-02 16:50:13'),
	(29, 'Gummy Bears (Haribo)', 'Ositos de goma de sabores frutales.', 2.00, 0.00, 50, 0, 3, '3578795955442', NULL, 1, '2025-07-01 15:47:54', '2025-07-01 15:47:54'),
	(30, 'Licuado de plátano', 'Bebida nutritiva a base de plátano licuado con leche, a menudo con azúcar o miel, ideal para el desayuno.', 1.50, 0.00, 50, 0, 1, '7498340219801', NULL, 1, '2025-07-01 15:48:03', '2025-07-01 15:48:03'),
	(31, 'Horchata', 'tradicional dulce a base de arroz (en México), almendras o chufas, con canela y vainilla.', 1.50, 0.00, 50, 0, 1, '4310348594285', NULL, 1, '2025-07-01 15:48:50', '2025-07-01 15:48:50'),
	(32, 'Jolly Rancher', 'Caramelos duros con sabores intensos de frutas.', 2.00, 0.00, 50, 0, 3, '3597467167224', NULL, 1, '2025-07-01 15:49:00', '2025-07-01 15:49:00'),
	(33, 'Limonada', 'Bebida refrescante a base de jugo de limón, agua y azúcar, ideal para el calor.', 2.00, 0.00, 40, 0, 1, '4478830062028', NULL, 1, '2025-07-01 15:49:42', '2025-07-01 15:49:42'),
	(34, 'Sour Patch Kids', 'Gomitas con azúcar ácido al inicio y dulces al final.', 3.00, 0.00, 50, 0, 3, '4211111758083', NULL, 1, '2025-07-01 15:49:49', '2025-07-01 15:49:49'),
	(35, 'Milk Duds', 'Bolitas de caramelo cubiertas con chocolate.', 2.00, 0.00, 50, 0, 3, '5199548635172', NULL, 1, '2025-07-01 15:50:23', '2025-07-01 15:50:23'),
	(36, 'Tootsie Roll', 'Caramelo masticable con sabor a chocolate.', 1.00, 0.00, 50, 0, 3, '9573734108532', NULL, 1, '2025-07-01 15:51:01', '2025-07-01 15:51:01'),
	(37, 'Swedish Fish', 'Gomitas con forma de pez, sabor a fruta roja.', 3.00, 0.00, 50, 0, 3, '5576892878232', NULL, 1, '2025-07-01 15:51:38', '2025-07-01 15:51:38'),
	(38, 'Pop Rocks', 'Dulces efervescentes que explotan en la boca.', 1.50, 0.00, 50, 0, 3, '3641495035580', NULL, 1, '2025-07-01 15:57:27', '2025-07-01 15:57:27'),
	(39, 'Laffy Taffy', 'Caramelo blando y elástico con chistes impresos en el envoltorio.', 1.00, 0.00, 49, 0, 3, '7766967536612', NULL, 1, '2025-07-01 15:58:37', '2025-07-01 19:29:30'),
	(40, 'Guantes de Goma', 'Protegen tus manos de los químicos de limpieza.', 1.00, 0.00, 40, 0, 4, '9070984380162', NULL, 1, '2025-07-01 16:06:40', '2025-07-01 16:06:40'),
	(41, 'Trapos de Microfibra:', 'Excelentes para limpiar sin dejar pelusa y secar superficies.', 2.00, 0.00, 30, 0, 4, '5581827896971', NULL, 1, '2025-07-01 16:07:22', '2025-07-01 16:07:22'),
	(42, 'Esponjas y Fibras', 'Para fregar platos y limpiar diversas superficies. Puedes conseguir un paquete variado.', 2.50, 0.00, 50, 0, 4, '0319996041470', NULL, 1, '2025-07-01 16:08:15', '2025-07-01 16:08:15'),
	(43, 'Limpiacristales', 'Para dejar ventanas, espejos y superficies de vidrio sin rayas y brillantes.', 4.50, 0.00, 30, 0, 4, '4433292390007', NULL, 1, '2025-07-01 16:09:06', '2025-07-01 16:09:06'),
	(44, 'Airheads', 'Caramelo masticable de sabores intensos y frutales.', 1.00, 0.00, 42, 0, 3, '9204654135517', NULL, 1, '2025-07-01 16:09:36', '2025-07-02 15:59:28'),
	(45, 'Baby Ruth', 'Barra de turrón con cacahuates, caramelo y chocolate.', 1.50, 0.00, 44, 0, 3, '6034278291566', NULL, 1, '2025-07-01 16:10:43', '2025-07-02 15:30:04'),
	(46, 'Nerds', 'Pequeños caramelos crujientes de sabores frutales.', 1.75, 0.00, 50, 0, 3, '7391400239468', NULL, 1, '2025-07-01 16:11:13', '2025-07-01 16:11:13'),
	(47, 'Limpiador Desengrasante', 'Ideal para la estufa, campana extractora y superficies con acumulación de grasa.', 3.00, 0.00, 30, 0, 4, '8475627086435', NULL, 1, '2025-07-01 16:11:31', '2025-07-01 16:11:31'),
	(48, 'Now and Later', 'Caramelos masticables que se comen “ahora y después” por su duración.', 1.50, 0.00, 50, 0, 3, '7317466003232', NULL, 1, '2025-07-01 16:12:27', '2025-07-01 16:12:27'),
	(49, 'Ring Pop', 'Dulce en forma de anillo con una gema de caramelo duro.', 1.50, 0.00, 50, 0, 3, '8764619659187', NULL, 1, '2025-07-01 16:12:57', '2025-07-01 16:12:57'),
	(50, 'Jabón Líquido para Trastes', 'Indispensable para lavar platos, vasos y utensilios a mano.', 3.00, 0.00, 40, 0, 4, '0176701665202', NULL, 1, '2025-07-01 16:13:56', '2025-07-01 16:13:56'),
	(51, 'Peluche Tux', 'Peluche de Tex de felpa', 10.00, 0.00, 200, 0, 7, '1616259406720', NULL, 1, '2025-07-02 16:51:50', '2025-07-02 16:51:50'),
	(52, 'Alcancia de Tux', 'Alcancia de forma de nuestra mascota Tux', 7.00, 0.00, 30, 0, 7, '5711975665101', NULL, 1, '2025-07-02 16:58:41', '2025-07-02 16:58:41'),
	(53, 'Playera de niño M', 'Playera con la mascota Tux', 10.00, 0.00, 50, 0, 7, '7331315849655', NULL, 1, '2025-07-02 17:00:11', '2025-07-02 17:00:11'),
	(54, 'Playera de niño CH', 'Playera con la mascota Tux', 10.00, 0.00, 20, 0, 7, '0707977811943', NULL, 1, '2025-07-02 17:01:14', '2025-07-02 17:01:14'),
	(55, 'Playera de niña CH', 'Playera con la mascota Tux', 10.00, 0.00, 40, 0, 7, '8884776723092', NULL, 1, '2025-07-02 17:02:33', '2025-07-02 17:02:33'),
	(56, 'Playera de niña M', 'Playera con la mascota Tux', 10.00, 0.00, 49, 0, 7, '5512590910642', NULL, 1, '2025-07-02 17:02:59', '2025-07-02 17:02:59'),
	(57, 'Playera de Adulto M', 'Playera con la mascota Tux', 15.00, 0.00, 30, 0, 7, '3081158861076', NULL, 1, '2025-07-02 17:04:14', '2025-07-02 17:04:14'),
	(58, 'Playera de Adulto G', 'Playera con la mascota Tux', 15.00, 0.00, 30, 0, 7, '3651150844487', NULL, 1, '2025-07-02 17:04:57', '2025-07-02 17:05:55'),
	(59, 'Playera de Adulto XL', 'Playera con la mascota Tux', 15.00, 0.00, 40, 0, 7, '8200793171275', NULL, 1, '2025-07-02 17:05:41', '2025-07-02 17:05:41'),
	(60, 'Playera de Adulto XLL', 'Playera con la mascota Tux', 15.00, 0.00, 30, 0, 7, '6246081348641', NULL, 1, '2025-07-02 17:06:49', '2025-07-02 17:06:49'),
	(61, 'Gorra de Mujer', 'Gorra con la mascota Tux', 8.00, 0.00, 50, 0, 7, '0270703704532', NULL, 1, '2025-07-02 17:09:27', '2025-07-02 17:09:27'),
	(62, 'Gorra de Hombre', 'Gorra con la mascota Tux', 8.00, 0.00, 60, 0, 7, '4567392588628', NULL, 1, '2025-07-02 17:10:54', '2025-07-02 17:10:54'),
	(63, 'Gorra de niño', 'Gorra con la mascota Tux', 8.00, 0.00, 30, 0, 7, '8193553377784', NULL, 1, '2025-07-02 17:11:23', '2025-07-02 17:11:23'),
	(64, 'Gorra de niña', 'Gorra con la mascota Tux', 8.00, 0.00, 40, 0, 7, '5850311506485', NULL, 1, '2025-07-02 17:12:19', '2025-07-02 17:12:19'),
	(65, 'Pastilla de tos', 'Color azul', 1.00, 0.00, 3, 0, 11, '3863924218647', NULL, 0, '2025-07-02 17:13:23', '2025-07-02 17:14:28'),
	(66, 'Spaghetti (500g)', 'Pasta larga de trigo duro, básica para todo tipo de salsas.', 2.00, 0.00, 50, 0, 5, '9127846404681', NULL, 1, '2025-07-02 17:28:29', '2025-07-02 17:28:29'),
	(67, 'Salsa para pasta (tomate)', 'Salsa lista con tomate, ajo y especias, para usar directamente.', 3.50, 0.00, 50, 0, 5, '7706177249023', NULL, 1, '2025-07-02 17:29:15', '2025-07-02 17:29:15'),
	(68, 'Mac & Cheese (caja)', 'Pasta con mezcla de queso en polvo, lista en minutos.', 2.50, 0.00, 50, 0, 5, '6158557788849', NULL, 1, '2025-07-02 17:30:02', '2025-07-02 17:30:02'),
	(69, 'Lasaña congelada', 'Comida congelada, lista para horno o microondas.', 6.00, 0.00, 50, 0, 5, '5562133726098', NULL, 1, '2025-07-02 17:31:05', '2025-07-02 17:31:05'),
	(70, 'Pines', 'Pin de la mascota Tux', 2.00, 0.00, 40, 0, 11, '3065825624239', NULL, 1, '2025-07-02 17:31:43', '2025-07-02 17:31:43'),
	(71, 'Arroz instantáneo', 'Arroz precocido listo para calentar en microondas.', 2.50, 0.00, 50, 0, 5, '6161836031456', NULL, 1, '2025-07-02 17:32:14', '2025-07-02 17:32:14'),
	(72, 'Sticker', 'Sticker con la mascota Tux', 1.50, 0.00, 90, 0, 11, '1574637690578', NULL, 1, '2025-07-02 17:32:14', '2025-07-02 17:32:14'),
	(73, 'Parche', 'Parche de la mascota Tux', 2.00, 0.00, 60, 0, 11, '6112571097388', NULL, 1, '2025-07-02 17:33:08', '2025-07-02 17:33:08'),
	(74, 'Frijoles refritos enlatados', 'Frijoles cocidos y molidos, listos para calentar.', 2.00, 0.00, 50, 0, 5, '9931030968561', NULL, 1, '2025-07-02 17:34:15', '2025-07-02 17:34:15'),
	(75, 'Sopa enlatada (Campbell\'s)', 'Sopa lista para servir (pollo, verduras, tomate, etc.).', 2.50, 0.00, 50, 0, 5, '4109416700558', NULL, 1, '2025-07-02 17:34:46', '2025-07-02 17:34:46'),
	(76, 'Sopa instantánea (Cup Noodles)', 'Fideos deshidratados con saborizantes. Solo se agrega agua caliente.', 1.50, 0.00, 50, 0, 5, '2644869463861', NULL, 1, '2025-07-02 17:35:11', '2025-07-02 17:35:11'),
	(77, 'Tazas', 'Tazas de la mascota Tux', 5.00, 0.00, 60, 0, 11, '2724183155587', NULL, 1, '2025-07-02 17:35:18', '2025-07-02 17:35:18'),
	(78, 'Fundas para lap', 'Fundas con l mascota Tux', 4.00, 0.00, 40, 0, 7, '6263258390529', NULL, 1, '2025-07-02 17:35:59', '2025-07-02 17:35:59'),
	(79, 'Atún enlatado (140g)', 'Pescado en aceite o agua, excelente para ensaladas o tortas.', 2.00, 0.00, 50, 0, 5, '8294067653443', NULL, 1, '2025-07-02 17:36:26', '2025-07-02 17:36:26'),
	(80, 'Salchichas tipo Viena (lata)', 'Salchichas pequeñas en conserva, listas para comer o calentar.', 2.00, 0.00, 50, 0, 5, '1530656125552', NULL, 1, '2025-07-02 17:36:57', '2025-07-02 17:36:57'),
	(81, 'Vegetales enlatados', 'Elote, chícharos, zanahorias, etc. en salmuera o agua.', 1.80, 0.00, 50, 0, 5, '8216129376375', NULL, 1, '2025-07-02 17:37:26', '2025-07-02 17:37:26'),
	(82, 'Chili con carne enlatado', 'Platillo listo con frijoles, carne molida y salsa.', 3.50, 0.00, 50, 0, 5, '8049851313248', NULL, 1, '2025-07-02 17:41:38', '2025-07-02 17:41:38'),
	(83, 'Sudadera', 'Sudadera de mascota Tuz', 20.00, 0.00, 40, 0, 7, '6138408083991', NULL, 1, '2025-07-02 17:42:30', '2025-07-02 17:42:30'),
	(84, 'Pizza congelada', 'Pizza lista para hornear, de pepperoni, queso u otros sabores.', 6.00, 0.00, 50, 0, 5, '9774117850625', NULL, 1, '2025-07-02 17:51:10', '2025-07-02 17:51:10'),
	(85, 'Croquetas de pollo congeladas', 'Porciones empanizadas listas para freír o calentar.', 7.00, 0.00, 50, 0, 5, '2960422738590', NULL, 1, '2025-07-02 17:51:59', '2025-07-02 17:51:59');

-- Volcando estructura para tabla puntoDeVenta.sales
CREATE TABLE IF NOT EXISTS `sales` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `cliente_id` int(11) DEFAULT NULL,
  `cliente_nombre` varchar(200) DEFAULT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `tax_amount` decimal(10,2) DEFAULT 0.00,
  `discount_amount` decimal(10,2) DEFAULT 0.00,
  `payment_method` enum('cash','card','transfer') NOT NULL DEFAULT 'cash',
  `amount_paid` decimal(10,2) NOT NULL,
  `change_amount` decimal(10,2) DEFAULT 0.00,
  `sale_date` timestamp NULL DEFAULT current_timestamp(),
  `status` enum('pending','completed','cancelled') DEFAULT 'completed',
  `notes` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_cliente` (`cliente_id`),
  KEY `idx_date` (`sale_date`),
  KEY `idx_status` (`status`),
  CONSTRAINT `sales_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `sales_ibfk_2` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish2_ci;

-- Volcando datos para la tabla puntoDeVenta.sales: ~24 rows (aproximadamente)
INSERT INTO `sales` (`id`, `user_id`, `cliente_id`, `cliente_nombre`, `total_amount`, `subtotal`, `tax_amount`, `discount_amount`, `payment_method`, `amount_paid`, `change_amount`, `sale_date`, `status`, `notes`) VALUES
	(1, 1, NULL, NULL, 1.62, 1.40, 0.22, 0.00, 'cash', 2.00, 0.38, '2025-07-01 18:37:30', 'completed', NULL),
	(2, 1, 10, NULL, 1.74, 1.50, 0.24, 0.00, 'cash', 6.00, 4.26, '2025-07-01 18:39:06', 'completed', NULL),
	(3, 1, 1, NULL, 1.74, 1.50, 0.24, 0.00, 'cash', 2.00, 0.26, '2025-07-01 18:41:45', 'completed', NULL),
	(4, 1, 10, NULL, 3.25, 2.80, 0.45, 0.00, 'cash', 4.00, 0.75, '2025-07-01 18:42:11', 'completed', NULL),
	(5, 1, 10, 'Joje', 1.62, 1.40, 0.22, 0.00, 'cash', 3.00, 1.38, '2025-07-01 18:52:12', 'completed', NULL),
	(6, 1, NULL, NULL, 3.25, 2.80, 0.45, 0.00, 'cash', 4.00, 0.75, '2025-07-01 18:53:12', 'completed', NULL),
	(7, 1, NULL, 'null', 1.74, 1.50, 0.24, 0.00, 'cash', 4.00, 2.26, '2025-07-01 19:01:08', 'completed', NULL),
	(8, 1, 3, 'Carlos Sánchez', 1.62, 1.40, 0.22, 0.00, 'cash', 4.00, 2.38, '2025-07-01 19:05:40', 'completed', NULL),
	(9, 1, NULL, 'null', 1.16, 1.00, 0.16, 0.00, 'cash', 3.00, 1.84, '2025-07-01 19:10:42', 'completed', NULL),
	(10, 1, NULL, 'null', 0.58, 0.50, 0.08, 0.00, 'cash', 0.58, 0.00, '2025-07-01 19:12:10', 'completed', NULL),
	(11, 1, NULL, 'null', 13.92, 12.00, 1.92, 0.00, 'cash', 20.00, 6.08, '2025-07-01 19:16:00', 'completed', NULL),
	(12, 1, NULL, 'null', 1.16, 1.00, 0.16, 0.00, 'cash', 2.00, 0.84, '2025-07-01 19:16:21', 'completed', NULL),
	(13, 1, NULL, 'null', 1.16, 1.00, 0.16, 0.00, 'cash', 2.00, 0.84, '2025-07-01 19:29:30', 'completed', NULL),
	(14, 3, NULL, 'null', 1.74, 1.50, 0.24, 0.00, 'cash', 2.00, 0.26, '2025-07-01 19:44:35', 'completed', NULL),
	(15, 3, 10, 'Joje', 8.06, 6.95, 1.11, 0.00, 'cash', 9.00, 0.94, '2025-07-01 19:45:13', 'completed', NULL),
	(16, 1, NULL, 'null', 5.86, 5.05, 0.81, 0.00, 'cash', 30.00, 24.14, '2025-07-01 19:52:22', 'completed', NULL),
	(17, 1, 11, 'Dui', 2.90, 2.50, 0.40, 0.00, 'cash', 6.00, 3.10, '2025-07-01 19:53:47', 'completed', NULL),
	(18, 1, NULL, 'null', 1.16, 1.00, 0.16, 0.00, 'cash', 76.00, 74.84, '2025-07-02 01:10:54', 'completed', NULL),
	(19, 1, NULL, 'null', 12.93, 11.15, 1.78, 0.00, 'cash', 14.00, 1.07, '2025-07-02 01:11:23', 'completed', NULL),
	(20, 1, 11, 'Dui', 1.16, 1.00, 0.16, 0.00, 'cash', 22.00, 20.84, '2025-07-02 01:14:32', 'completed', NULL),
	(21, 3, 11, 'Dui', 9.16, 7.90, 1.26, 0.00, 'cash', 10.00, 0.84, '2025-07-02 15:30:04', 'completed', NULL),
	(22, 5, 11, 'Dui', 1.16, 1.00, 0.16, 0.00, 'cash', 2.00, 0.84, '2025-07-02 15:56:18', 'completed', NULL),
	(23, 3, 11, 'Dui', 4.64, 4.00, 0.64, 0.00, 'cash', 5.00, 0.36, '2025-07-02 15:59:28', 'completed', NULL),
	(24, 5, 1, 'Juan Pérez', 3.48, 3.00, 0.48, 0.00, 'cash', 5.00, 1.52, '2025-07-02 16:02:56', 'completed', NULL);

-- Volcando estructura para tabla puntoDeVenta.sale_items
CREATE TABLE IF NOT EXISTS `sale_items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sale_id` int(11) NOT NULL,
  `product_id` int(11) DEFAULT NULL,
  `product_name` varchar(200) NOT NULL,
  `quantity` int(11) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sale` (`sale_id`),
  KEY `idx_product` (`product_id`),
  CONSTRAINT `sale_items_ibfk_1` FOREIGN KEY (`sale_id`) REFERENCES `sales` (`id`) ON DELETE CASCADE,
  CONSTRAINT `sale_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish2_ci;

-- Volcando datos para la tabla puntoDeVenta.sale_items: ~37 rows (aproximadamente)
INSERT INTO `sale_items` (`id`, `sale_id`, `product_id`, `product_name`, `quantity`, `unit_price`, `total_price`) VALUES
	(1, 1, NULL, 'Cerveza', 1, 1.40, 1.40),
	(2, 2, NULL, 'Baby Ruth', 1, 1.50, 1.50),
	(3, 3, NULL, 'Baby Ruth', 1, 1.50, 1.50),
	(4, 4, NULL, 'Cerveza', 2, 1.40, 2.80),
	(5, 5, 16, 'Cerveza', 1, 1.40, 1.40),
	(6, 6, 16, 'Cerveza', 2, 1.40, 2.80),
	(7, 7, 45, 'Baby Ruth', 1, 1.50, 1.50),
	(8, 8, 16, 'Cerveza', 1, 1.40, 1.40),
	(9, 9, 7, 'Chocolate semi', 1, 1.00, 1.00),
	(10, 10, 6, 'Chicles Trident', 1, 0.50, 0.50),
	(11, 11, 28, 'Agua mineral (600 ml)', 5, 2.40, 12.00),
	(12, 12, 44, 'Airheads', 1, 1.00, 1.00),
	(13, 13, 39, 'Laffy Taffy', 1, 1.00, 1.00),
	(14, 14, 45, 'Baby Ruth', 1, 1.50, 1.50),
	(15, 15, 16, 'Cerveza', 2, 1.40, 2.80),
	(16, 15, 18, 'Café americano (regular)', 1, 1.65, 1.65),
	(17, 15, 44, 'Airheads', 1, 1.00, 1.00),
	(18, 15, 45, 'Baby Ruth', 1, 1.50, 1.50),
	(19, 16, 23, 'Bebida energética (lata 500 ml)', 1, 2.00, 2.00),
	(20, 16, 16, 'Cerveza', 1, 1.40, 1.40),
	(21, 16, 18, 'Café americano (regular)', 1, 1.65, 1.65),
	(22, 17, 44, 'Airheads', 1, 1.00, 1.00),
	(23, 17, 7, 'Chocolate semi', 1, 1.00, 1.00),
	(24, 17, 6, 'Chicles Trident', 1, 0.50, 0.50),
	(25, 18, 44, 'Airheads', 1, 1.00, 1.00),
	(26, 19, 16, 'Cerveza', 1, 1.40, 1.40),
	(27, 19, 18, 'Café americano (regular)', 5, 1.65, 8.25),
	(28, 19, 6, 'Chicles Trident', 1, 0.50, 0.50),
	(29, 19, 7, 'Chocolate semi', 1, 1.00, 1.00),
	(30, 20, 44, 'Airheads', 1, 1.00, 1.00),
	(31, 21, 45, 'Baby Ruth', 3, 1.50, 4.50),
	(32, 21, 23, 'Bebida energética (lata 500 ml)', 1, 2.00, 2.00),
	(33, 21, 16, 'Cerveza', 1, 1.40, 1.40),
	(34, 22, 44, 'Airheads', 1, 1.00, 1.00),
	(35, 23, 44, 'Airheads', 2, 1.00, 2.00),
	(36, 23, 23, 'Bebida energética (lata 500 ml)', 1, 2.00, 2.00),
	(37, 24, 26, 'Hershey\'s Milk Chocolate', 2, 1.50, 3.00);

-- Volcando estructura para tabla puntoDeVenta.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `last_login` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish2_ci;

-- Volcando datos para la tabla puntoDeVenta.users: ~7 rows (aproximadamente)
INSERT INTO `users` (`id`, `username`, `email`, `password`, `is_active`, `created_at`, `last_login`) VALUES
	(1, 'admin', 'admin@puntod.com', '0192023a7bbd73250516f069df18b500', 1, '2025-06-30 19:26:55', '2025-07-02 17:09:54'),
	(2, 'raf', 'rafaortega1616@gmail.com', 'db62a563b4126069ec9c577c7a979269', 1, '2025-06-30 19:46:50', '2025-07-01 12:45:30'),
	(3, 'Joje', 'Joje@gmail.com', '0d23bdc86d4b3af5becad60e919ac95d', 1, '2025-06-30 19:52:07', '2025-07-02 17:23:17'),
	(4, 'Gama', 'rafaortega1717@gmail.com', '33abdf560987016fec961ce06e4c159e', 1, '2025-06-30 19:56:08', '2025-07-02 17:37:37'),
	(5, 'Enrique', 'ctenrique@gmail.com', '25f9e794323b453885f5181f1b624d0b', 1, '2025-07-01 01:08:47', '2025-07-02 17:37:51'),
	(6, 'angel', 'angel@gmail.com', '25d55ad283aa400af464c76d713c07ad', 1, '2025-07-01 16:43:28', '2025-07-02 15:11:50'),
	(7, 'ElGamma', 'mr.blackfaceto@gmail.com', '25d55ad283aa400af464c76d713c07ad', 1, '2025-07-02 17:46:19', '2025-07-02 17:47:23');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
