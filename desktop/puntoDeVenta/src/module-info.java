/**
 * Módulo del Sistema de Punto de Venta
 */
module puntoDeVenta {
    requires java.sql;
    requires java.desktop;
    requires java.base;
    
    // Exportar packages para que sean accesibles
    exports main;
    exports views.auth;
    exports views.dashboard;
    exports models;
    exports utils;
    exports config;
}