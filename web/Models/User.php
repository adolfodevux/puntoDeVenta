<?php

require_once __DIR__ . '/database.php';

class User {
    private $db;
    private $connection;

    public function __construct() {
        $this->db = Database::getInstance();
        $this->connection = $this->db->getConnection();
    }

    /**
     * Registrar un nuevo usuario
     */
    public function register($username, $email, $password) {
        try {
            // Verificar si el usuario ya existe
            if ($this->userExists($username, $email)) {
                return [
                    'success' => false,
                    'message' => 'El usuario o email ya existe'
                ];
            }

            // Encriptar la contraseña con MD5
            $hashedPassword = md5($password);

            // Preparar la consulta SQL
            $stmt = $this->connection->prepare("INSERT INTO users (username, email, password, created_at) VALUES (?, ?, ?, NOW())");
            
            if (!$stmt) {
                throw new Exception("Error en la preparación de la consulta: " . $this->connection->error);
            }

            $stmt->bind_param("sss", $username, $email, $hashedPassword);
            
            if ($stmt->execute()) {
                $userId = $this->connection->insert_id;
                $stmt->close();
                
                return [
                    'success' => true,
                    'message' => 'Usuario registrado exitosamente',
                    'user_id' => $userId
                ];
            } else {
                throw new Exception("Error al ejecutar la consulta: " . $stmt->error);
            }

        } catch (Exception $e) {
            error_log("Error en registro de usuario: " . $e->getMessage());
            return [
                'success' => false,
                'message' => 'Error interno del servidor'
            ];
        }
    }

    /**
     * Autenticar usuario (login)
     */
    public function login($username, $password) {
        try {
            // Preparar la consulta para buscar el usuario
            $stmt = $this->connection->prepare("SELECT id, username, email, password, is_active FROM users WHERE username = ? OR email = ?");
            
            if (!$stmt) {
                throw new Exception("Error en la preparación de la consulta: " . $this->connection->error);
            }

            $stmt->bind_param("ss", $username, $username);
            $stmt->execute();
            $result = $stmt->get_result();

            if ($result->num_rows === 1) {
                $user = $result->fetch_assoc();
                $stmt->close();

                // Verificar si la cuenta está activa
                if (!$user['is_active']) {
                    return [
                        'success' => false,
                        'message' => 'Cuenta desactivada. Contacte al administrador'
                    ];
                }

                // Verificar la contraseña
                if (md5($password) === $user['password']) {
                    // Actualizar último login
                    $this->updateLastLogin($user['id']);
                    
                    return [
                        'success' => true,
                        'message' => 'Login exitoso',
                        'user' => [
                            'id' => $user['id'],
                            'username' => $user['username'],
                            'email' => $user['email']
                        ]
                    ];
                } else {
                    return [
                        'success' => false,
                        'message' => 'Contraseña incorrecta'
                    ];
                }
            } else {
                $stmt->close();
                return [
                    'success' => false,
                    'message' => 'Usuario no encontrado'
                ];
            }

        } catch (Exception $e) {
            error_log("Error en login de usuario: " . $e->getMessage());
            return [
                'success' => false,
                'message' => 'Error interno del servidor'
            ];
        }
    }

    /**
     * Verificar si un usuario ya existe
     */
    private function userExists($username, $email) {
        try {
            $stmt = $this->connection->prepare("SELECT id FROM users WHERE username = ? OR email = ?");
            $stmt->bind_param("ss", $username, $email);
            $stmt->execute();
            $result = $stmt->get_result();
            $exists = $result->num_rows > 0;
            $stmt->close();
            
            return $exists;
        } catch (Exception $e) {
            error_log("Error verificando existencia de usuario: " . $e->getMessage());
            return true; // En caso de error, asumimos que existe para evitar duplicados
        }
    }

    /**
     * Actualizar la fecha del último login
     */
    private function updateLastLogin($userId) {
        try {
            $stmt = $this->connection->prepare("UPDATE users SET last_login = NOW() WHERE id = ?");
            $stmt->bind_param("i", $userId);
            $stmt->execute();
            $stmt->close();
        } catch (Exception $e) {
            error_log("Error actualizando último login: " . $e->getMessage());
        }
    }

    /**
     * Obtener información de un usuario por ID
     */
    public function getUserById($userId) {
        try {
            $stmt = $this->connection->prepare("SELECT id, username, email, created_at, last_login FROM users WHERE id = ? AND is_active = 1");
            $stmt->bind_param("i", $userId);
            $stmt->execute();
            $result = $stmt->get_result();
            
            if ($result->num_rows === 1) {
                $user = $result->fetch_assoc();
                $stmt->close();
                return $user;
            } else {
                $stmt->close();
                return null;
            }
        } catch (Exception $e) {
            error_log("Error obteniendo usuario por ID: " . $e->getMessage());
            return null;
        }
    }

    /**
     * Cambiar contraseña
     */
    public function changePassword($userId, $currentPassword, $newPassword) {
        try {
            // Verificar contraseña actual
            $stmt = $this->connection->prepare("SELECT password FROM users WHERE id = ?");
            $stmt->bind_param("i", $userId);
            $stmt->execute();
            $result = $stmt->get_result();
            
            if ($result->num_rows === 1) {
                $user = $result->fetch_assoc();
                $stmt->close();
                
                if (md5($currentPassword) === $user['password']) {
                    // Actualizar contraseña
                    $hashedNewPassword = md5($newPassword);
                    $stmt = $this->connection->prepare("UPDATE users SET password = ? WHERE id = ?");
                    $stmt->bind_param("si", $hashedNewPassword, $userId);
                    
                    if ($stmt->execute()) {
                        $stmt->close();
                        return [
                            'success' => true,
                            'message' => 'Contraseña actualizada exitosamente'
                        ];
                    } else {
                        throw new Exception("Error al actualizar contraseña");
                    }
                } else {
                    return [
                        'success' => false,
                        'message' => 'Contraseña actual incorrecta'
                    ];
                }
            } else {
                $stmt->close();
                return [
                    'success' => false,
                    'message' => 'Usuario no encontrado'
                ];
            }
        } catch (Exception $e) {
            error_log("Error cambiando contraseña: " . $e->getMessage());
            return [
                'success' => false,
                'message' => 'Error interno del servidor'
            ];
        }
    }
}

?>
