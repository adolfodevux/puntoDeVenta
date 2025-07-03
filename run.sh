#!/bin/bash
# Script para compilar y ejecutar el Sistema de Punto de Venta

echo "=== Sistema de Punto de Venta - Compilación y Ejecución ==="

# Directorio del proyecto
PROJECT_DIR="/home/adolfodevux/Escritorio/workspace/code/webs/docker-compose-lamp/www/puntoDeVenta/desktop/puntoDeVenta"
SRC_DIR="$PROJECT_DIR/src"
BIN_DIR="$PROJECT_DIR/bin"
LIB_DIR="$PROJECT_DIR/lib"

# Crear directorio bin si no existe
mkdir -p "$BIN_DIR"

echo "1. Compilando código fuente..."

# Compilar con el MySQL Connector en el classpath
javac -cp "$LIB_DIR/mysql-connector-j-8.2.0.jar" \
      -d "$BIN_DIR" \
      --module-path "$LIB_DIR" \
      --add-modules java.sql,java.desktop \
      $(find "$SRC_DIR" -name "*.java")

if [ $? -eq 0 ]; then
    echo "✓ Compilación exitosa"
else
    echo "✗ Error en la compilación"
    exit 1
fi

echo "2. Ejecutando aplicación..."

# Ejecutar la aplicación
java -cp "$BIN_DIR:$LIB_DIR/mysql-connector-j-8.2.0.jar" \
     --module-path "$LIB_DIR" \
     --add-modules java.sql,java.desktop \
     main.Main

echo "=== Fin de la ejecución ==="
