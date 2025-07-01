-- Script para agregar la columna cliente_nombre a la tabla sales
-- Esto permite guardar el nombre del cliente incluso si no se selecciona uno (null)

USE puntoDeVenta;

-- Agregar columna cliente_nombre a la tabla sales
ALTER TABLE sales ADD COLUMN cliente_nombre VARCHAR(200) NULL AFTER cliente_id;

-- Opcional: Actualizar registros existentes con el nombre del cliente
-- UPDATE sales s 
-- JOIN clientes c ON s.cliente_id = c.id 
-- SET s.cliente_nombre = c.nombre 
-- WHERE s.cliente_id IS NOT NULL;

-- Mostrar estructura actualizada de la tabla
DESCRIBE sales;
