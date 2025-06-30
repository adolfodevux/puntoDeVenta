<?php

// Incluir el archivo de configuración para acceder a las constantes de la BD
require_once __DIR__ . '/../Core/config.php';

class Database {
    private static $instance = null;
    private $connection;

    // El constructor es privado para implementar el patrón Singleton
    private function __construct() {
        // Habilitar reportes de errores de MySQLi
        mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

        try {
            $this->connection = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME, DB_PORT);

            // Establecer el conjunto de caracteres
            if (!$this->connection->set_charset(DB_CHARSET)) {
                throw new Exception("Error al establecer el conjunto de caracteres: " . $this->connection->error);
            }

        } catch (mysqli_sql_exception $e) {
            // Capturar errores específicos de MySQLi
            if (DEBUG_MODE) {
                die("Error de conexión a la base de datos: (" . $e->getCode() . ") " . $e->getMessage());
            } else {
                die("Error de conexión a la base de datos. Por favor, inténtelo más tarde.");
            }
        } catch (Exception $e) {
            // Capturar otros errores generales
            if (DEBUG_MODE) {
                die("Error en la base de datos: " . $e->getMessage());
            } else {
                die("Error en la base de datos. Por favor, inténtelo más tarde.");
            }
        }
    }

    // Método estático para obtener la única instancia de la clase Database
    public static function getInstance() {
        if (self::$instance === null) {
            self::$instance = new Database();
        }
        return self::$instance;
    }

    // Método para obtener la conexión MySQLi
    public function getConnection() {
        return $this->connection;
    }

    // Opcional: Cerrar la conexión cuando el objeto es destruido
    public function __destruct() {
        if ($this->connection && !$this->connection->connect_error) {
            $this->connection->close();
        }
    }
}

?>