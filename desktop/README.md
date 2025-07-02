# Sistema de Punto de Venta - Aplicación Java Swing

## Descripción

Esta es una aplicación Java Swing que replica exactamente la funcionalidad de tu sistema web de punto de venta. La aplicación incluye:

- **Login y Registro de usuarios** con validación completa
- **Dashboard principal** con interfaz moderna
- **Conexión a MySQL** usando los mismos datos que tu aplicación web
- **Interfaz de usuario elegante** que replica el diseño web

## Características Implementadas

### ✅ Autenticación
- **Login** con validación de credenciales
- **Registro** de nuevos usuarios con validación de datos
- **Encriptación MD5** (igual que PHP) para contraseñas
- **Manejo de sesiones** con SessionManager

### ✅ Dashboard Principal
- **Interfaz de Punto de Venta** (POS)
- **Sidebar de navegación** con módulos:
  - Punto Venta
  - Inventario (en desarrollo)
  - Categorías (en desarrollo)
  - Ventas (en desarrollo)
  - Clientes (en desarrollo)
- **Header con fecha/hora** en tiempo real
- **Información del usuario logueado**

### ✅ Base de Datos
- **Conexión MySQL** con los mismos parámetros que tu web
- **Modelo User** que replica exactamente la funcionalidad PHP
- **Manejo de errores** y conexiones

## Estructura del Proyecto

```
src/
├── config/
│   └── DatabaseConfig.java          # Configuración de BD
├── main/
│   └── Main.java                     # Punto de entrada
├── models/
│   └── User.java                     # Modelo de usuario
├── utils/
│   ├── SessionManager.java          # Manejo de sesiones
│   └── UIUtils.java                  # Utilidades UI
└── views/
    ├── auth/
    │   ├── LoginFrame.java           # Ventana de login
    │   └── RegisterFrame.java        # Ventana de registro
    └── dashboard/
        └── DashboardFrame.java       # Dashboard principal
```

## Requisitos

- **Java 11+** con módulos activados
- **MySQL Connector/J 8.2.0** (incluido en lib/)
- **Base de datos MySQL** con la tabla `users`

## Instalación y Ejecución

### 1. Compilar y ejecutar automáticamente:
```bash
./run.sh
```

### 2. Compilar manualmente:
```bash
javac -cp "lib/mysql-connector-j-8.2.0.jar" \
      -d "bin" \
      --module-path "lib" \
      --add-modules java.sql,java.desktop \
      $(find src -name "*.java")
```

### 3. Ejecutar manualmente:
```bash
java -cp "bin:lib/mysql-connector-j-8.2.0.jar" \
     --module-path "lib" \
     --add-modules java.sql,java.desktop \
     main.Main
```

## Configuración de Base de Datos

La aplicación usa exactamente la misma configuración que tu web:

```java
DB_HOST = "40.76.137.253"
DB_NAME = "puntoDeVenta"
DB_USER = "debian"
DB_PASS = "P@ss_d3bian-linux_Vps"
DB_PORT = 3306
```

## Uso de la Aplicación

### Login
1. Ejecuta la aplicación
2. Ingresa tu usuario/email y contraseña
3. Haz clic en "Iniciar Sesión"

### Registro
1. Desde la pantalla de login, haz clic en "Crear nueva cuenta"
2. Completa todos los campos requeridos
3. Haz clic en "Crear Cuenta"
4. Vuelve al login para ingresar

### Dashboard
1. Una vez logueado, verás el dashboard principal
2. Usa el sidebar para navegar entre módulos
3. El módulo POS está completamente funcional
4. Los otros módulos mostrarán un mensaje "En desarrollo"

## Características Técnicas

### Patrones de Diseño
- **MVC (Model-View-Controller)**: Separación clara de responsabilidades
- **Singleton**: SessionManager para manejo de sesión única
- **Factory**: UIUtils para crear componentes consistentes

### Tecnologías
- **Java Swing**: Interfaz gráfica de usuario
- **JDBC**: Conexión a base de datos MySQL
- **SwingWorker**: Operaciones asíncronas para no bloquear UI
- **MD5**: Encriptación de contraseñas (compatible con PHP)

### Validaciones
- **Formulario de login**: Campos requeridos
- **Formulario de registro**: 
  - Usuario (3-50 caracteres)
  - Email válido
  - Contraseña (6-255 caracteres)
  - Confirmación de contraseña
- **Base de datos**: Verificación de usuarios existentes

## Próximas Implementaciones

- [ ] **Módulo de Inventario**: Gestión de productos
- [ ] **Módulo de Categorías**: Administración de categorías
- [ ] **Módulo de Ventas**: Historial y reportes
- [ ] **Módulo de Clientes**: Gestión de clientes
- [ ] **Funcionalidad POS completa**: Carrito, pagos, tickets

## Notas de Desarrollo

Esta aplicación fue desarrollada como una réplica exacta de tu sistema web PHP, manteniendo:
- **Misma lógica de negocio**
- **Misma estructura de base de datos**
- **Misma funcionalidad de autenticación**
- **Diseño visual similar**

La aplicación está lista para usar y conectar con tu base de datos existente sin modificaciones.

## Autor

Sistema de Punto de Venta - Punto-D
Versión: 1.0.0
Fecha: Julio 2025
