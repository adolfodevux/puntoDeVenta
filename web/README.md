# 🛍️ Sistema Web - Punto de Venta

Este directorio contiene la aplicación web del sistema Punto de Venta desarrollado en PHP.

## 📁 Estructura del Proyecto

```
web/
├── index.php              # Página principal con redirección automática
├── .htaccess              # Configuración del servidor Apache
├── sqldata.sql            # Base de datos del sistema
├── Assets/                # Recursos estáticos
│   ├── css/              # Hojas de estilo
│   ├── js/               # Scripts JavaScript
│   └── img/              # Imágenes del sistema
├── Controllers/          # Controladores MVC
├── Core/                 # Configuración central
├── Models/               # Modelos de datos
├── Views/                # Vistas del sistema
│   ├── auth/            # Autenticación (login/register)
│   ├── dashboard/       # Panel principal
│   ├── categories/      # Gestión de categorías
│   ├── clientes/        # Gestión de clientes
│   ├── sales/           # Gestión de ventas
│   └── suppliers/       # Gestión de proveedores
└── api/                  # API endpoints
```

## 🚀 Acceso al Sistema

### Punto de Entrada Principal
- **URL**: `http://localhost/puntoDeVenta/web/`
- **Redirección automática**: Al login si no está autenticado
- **Dashboard**: Si ya está logueado

### Rutas Principales
- **Login**: `/Views/auth/login.php`
- **Registro**: `/Views/auth/register.php`
- **Dashboard**: `/Views/dashboard/index.php`
- **Logout**: `/Views/auth/logout.php`

## 🔧 Configuración

### Requisitos
- PHP 7.4+
- Apache/Nginx
- MySQL/MariaDB
- Extensiones PHP: PDO, mysqli

### Base de Datos
- Importar `sqldata.sql` en MySQL
- Configurar conexión en `Core/config.php`

### Apache
- El archivo `.htaccess` está configurado para:
  - Redirección automática
  - Seguridad básica
  - Compresión y caché
  - URLs limpias

## 🔐 Seguridad

- Sesiones PHP para autenticación
- Prevención de acceso directo a archivos sensibles
- Validación de entrada en formularios
- Protección contra SQL injection

## 📱 Funcionalidades

### ✅ Módulos Implementados
- 🔐 **Autenticación**: Login/Logout/Registro
- 📊 **Dashboard**: Panel principal con estadísticas
- 📦 **Inventario**: Gestión de productos
- 🛍️ **Ventas**: Punto de venta y histórico
- 👥 **Clientes**: Gestión de clientes
- 🚚 **Proveedores**: Gestión de proveedores
- 📂 **Categorías**: Organización de productos

### 🎨 Características
- Diseño responsive
- Interfaz moderna y intuitiva
- Búsqueda y filtrado
- Paginación automática
- Reportes y estadísticas

## 🔄 Integración

Este sistema web se integra con:
- **Aplicación Desktop**: Java Swing (mismo sistema)
- **Base de Datos**: MySQL compartida
- **API REST**: Endpoints para comunicación

## 📞 Soporte

Para soporte técnico o dudas sobre el sistema:
- Revisar la documentación en cada módulo
- Verificar logs de errores en Apache
- Comprobar configuración de base de datos
