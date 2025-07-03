#!/bin/bash

# Script simplificado para compilar el proyecto
PROJECT_DIR="/home/adolfodevux/Escritorio/workspace/code/webs/docker-compose-lamp/www/puntoDeVenta/desktop/puntoDeVenta"
SRC_DIR="$PROJECT_DIR/src"
BIN_DIR="$PROJECT_DIR/bin"
LIB_DIR="$PROJECT_DIR/lib"

# Crear directorio bin si no existe
mkdir -p "$BIN_DIR"

echo "Compilando..."

# Compilar todos los archivos Java uno por uno para detectar errores específicos
javac -cp "$LIB_DIR/mysql-connector-j-8.2.0.jar" -d "$BIN_DIR" \
    "$SRC_DIR/config/DatabaseConfig.java" \
    "$SRC_DIR/models/User.java" \
    "$SRC_DIR/models/Product.java" \
    "$SRC_DIR/models/Category.java" \
    "$SRC_DIR/models/Cliente.java" \
    "$SRC_DIR/models/Sale.java" \
    "$SRC_DIR/utils/UIUtils.java" \
    "$SRC_DIR/utils/SessionManager.java" \
    "$SRC_DIR/utils/CartManager.java" \
    "$SRC_DIR/views/auth/LoginFrame.java" \
    "$SRC_DIR/views/auth/RegisterFrame.java" \
    "$SRC_DIR/views/dashboard/DashboardFrame.java" \
    "$SRC_DIR/Main.java"

if [ $? -eq 0 ]; then
    echo "✓ Compilación exitosa"
    
    # Copiar recursos
    if [ -d "$SRC_DIR/assets" ]; then
        cp -r "$SRC_DIR/assets" "$BIN_DIR/"
        echo "✓ Recursos copiados"
    fi
    
    echo "Ejecutando aplicación..."
    java -cp "$BIN_DIR:$LIB_DIR/mysql-connector-j-8.2.0.jar" Main
else
    echo "✗ Error en la compilación"
    exit 1
fi
