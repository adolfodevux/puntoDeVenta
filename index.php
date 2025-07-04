<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Punto de Venta - Sistema Integral de Gestión</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #2c3e50 0%, #3498db 50%, #ecf0f1 100%);
            min-height: 100vh;
            color: #333;
            scroll-behavior: smooth;
        }

        .header {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            padding: 20px 0;
            box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px;
        }

        .nav {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .logo {
            display: flex;
            align-items: center;
            font-size: 28px;
            font-weight: bold;
            color: #2c3e50;
        }

        .logo i {
            margin-right: 15px;
            font-size: 32px;
        }

        .hero {
            text-align: center;
            padding: 80px 20px;
            color: white;
        }

        .hero h1 {
            font-size: 3.5em;
            margin-bottom: 20px;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
        }

        .hero p {
            font-size: 1.3em;
            margin-bottom: 40px;
            max-width: 600px;
            margin-left: auto;
            margin-right: auto;
            opacity: 0.95;
        }

        .features {
            background: white;
            padding: 80px 20px;
        }

        .features-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 40px;
            margin-top: 50px;
            padding: 0 10px;
        }

        .feature-card {
            background: linear-gradient(145deg, #ffffff, #f8f9fa);
            padding: 40px 30px;
            border-radius: 20px;
            text-align: center;
            box-shadow: 0 10px 30px rgba(44, 62, 80, 0.1);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            border: 1px solid #e9ecef;
        }

        .feature-card:hover {
            transform: translateY(-10px);
            box-shadow: 0 20px 40px rgba(44, 62, 80, 0.15);
        }

        .feature-icon {
            font-size: 3em;
            margin-bottom: 20px;
            background: linear-gradient(135deg, #2c3e50, #3498db);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }

        .feature-card h3 {
            font-size: 1.5em;
            margin-bottom: 15px;
            color: #2c3e50;
        }

        .feature-card p {
            color: #5a6c7d;
            line-height: 1.6;
            margin-bottom: 20px;
        }

        .downloads {
            background: linear-gradient(135deg, #2c3e50, #3498db);
            padding: 80px 20px;
            color: white;
        }

        .downloads h2 {
            text-align: center;
            font-size: 2.5em;
            margin-bottom: 20px;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
        }

        .downloads-subtitle {
            text-align: center;
            font-size: 1.2em;
            margin-bottom: 50px;
            opacity: 0.9;
        }

        .download-options {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
            gap: 30px;
            margin-top: 40px;
            padding: 0 10px;
        }

        .download-card {
            background: rgba(255, 255, 255, 0.1);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            border-radius: 20px;
            padding: 40px 30px;
            text-align: center;
            transition: transform 0.3s ease, background 0.3s ease, box-shadow 0.3s ease;
            position: relative;
            overflow: hidden;
        }

        .download-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
            transition: left 0.6s ease;
        }

        .download-card:hover::before {
            left: 100%;
        }

        .download-card:hover {
            transform: translateY(-8px);
            background: rgba(255, 255, 255, 0.18);
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
        }

        .download-icon {
            font-size: 3.5em;
            margin-bottom: 20px;
            opacity: 0.9;
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 15px;
            flex-wrap: wrap;
        }

        .download-icon i {
            transition: all 0.3s ease;
            padding: 10px;
            border-radius: 10px;
        }

        .download-icon .fa-windows {
            color: #0078d4;
        }

        .download-icon .fa-apple {
            color: #ffffff;
        }

        .download-icon .fa-linux {
            color: #fcc624;
        }

        .download-icon .fa-android {
            color: #3ddc84;
        }

        .download-icon i:hover {
            transform: scale(1.2) rotate(5deg);
            background: rgba(255, 255, 255, 0.2);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
        }

        .download-card h3 {
            font-size: 1.8em;
            margin-bottom: 15px;
        }

        .download-card p {
            margin-bottom: 25px;
            opacity: 0.9;
            line-height: 1.6;
        }

        .download-btn {
            display: inline-block;
            background: linear-gradient(135deg, #2c3e50, #3498db);
            color: white;
            padding: 15px 30px;
            border-radius: 50px;
            text-decoration: none;
            font-weight: bold;
            font-size: 1.1em;
            transition: all 0.3s ease;
            box-shadow: 0 5px 15px rgba(44, 62, 80, 0.3);
        }

        .download-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(44, 62, 80, 0.4);
            background: linear-gradient(135deg, #3498db, #2c3e50);
        }

        .web-access {
            background: white;
            padding: 80px 20px;
            text-align: center;
        }

        .web-access h2 {
            font-size: 2.5em;
            margin-bottom: 20px;
            color: #2c3e50;
        }

        .web-access p {
            font-size: 1.2em;
            color: #5a6c7d;
            margin-bottom: 40px;
            max-width: 600px;
            margin-left: auto;
            margin-right: auto;
        }

        .web-btn {
            display: inline-block;
            background: linear-gradient(135deg, #3498db, #2980b9);
            color: white;
            padding: 20px 40px;
            border-radius: 50px;
            text-decoration: none;
            font-weight: bold;
            font-size: 1.3em;
            transition: all 0.3s ease;
            box-shadow: 0 5px 20px rgba(52, 152, 219, 0.3);
        }

        .web-btn:hover {
            transform: translateY(-3px);
            box-shadow: 0 10px 30px rgba(52, 152, 219, 0.4);
            background: linear-gradient(135deg, #2980b9, #3498db);
        }

        .footer {
            background: #2c3e50;
            color: white;
            padding: 40px 20px;
            text-align: center;
        }

        .footer p {
            opacity: 0.8;
        }

        .tech-stack {
            display: flex;
            justify-content: center;
            gap: 20px;
            margin-top: 20px;
        }

        .tech-badge {
            background: rgba(255, 255, 255, 0.1);
            padding: 8px 16px;
            border-radius: 20px;
            font-size: 0.9em;
        }

        .creator-card {
            background: white;
            padding: 30px;
            border-radius: 20px;
            box-shadow: 0 10px 30px rgba(44, 62, 80, 0.1);
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }

        .creator-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(52, 152, 219, 0.1), transparent);
            transition: left 0.6s ease;
        }

        .creator-card:hover::before {
            left: 100%;
        }

        .creator-card:hover {
            transform: translateY(-10px) scale(1.02);
            box-shadow: 0 20px 40px rgba(44, 62, 80, 0.2);
        }

        .creators-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 30px;
            max-width: 1000px;
            margin: 0 auto;
            padding: 0 10px;
        }

        @media (max-width: 1200px) {
            .container {
                max-width: 95%;
                padding: 0 15px;
            }
            
            .download-options {
                grid-template-columns: 1fr;
                gap: 25px;
            }
        }

        @media (max-width: 992px) {
            .hero h1 {
                font-size: 3em;
            }
            
            .hero p {
                font-size: 1.2em;
            }
            
            .features-grid {
                grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
                gap: 30px;
            }
            
            .feature-card {
                padding: 30px 25px;
            }
            
            .download-card {
                padding: 35px 25px;
            }
        }

        @media (max-width: 768px) {
            .hero {
                padding: 60px 20px;
            }
            
            .hero h1 {
                font-size: 2.5em;
            }
            
            .hero p {
                font-size: 1.1em;
            }
            
            .features {
                padding: 60px 20px;
            }
            
            .downloads {
                padding: 60px 20px;
            }
            
            .web-access {
                padding: 60px 20px;
            }
            
            .features-grid {
                grid-template-columns: 1fr;
                gap: 25px;
            }
            
            .feature-card {
                padding: 25px 20px;
            }
            
            .download-card {
                padding: 30px 20px;
            }
            
            .download-icon {
                font-size: 3em;
                gap: 12px;
            }
            
            .web-btn, .download-btn {
                padding: 15px 25px;
                font-size: 1em;
            }
            
            .web-btn {
                padding: 18px 30px;
                font-size: 1.1em;
            }
        }

        @media (max-width: 576px) {
            .header {
                padding: 15px 0;
            }
            
            .logo {
                font-size: 24px;
            }
            
            .logo i {
                font-size: 28px;
            }
            
            .hero {
                padding: 40px 15px;
            }
            
            .hero h1 {
                font-size: 2em;
                margin-bottom: 15px;
            }
            
            .hero p {
                font-size: 1em;
                margin-bottom: 30px;
            }
            
            .features, .downloads, .web-access {
                padding: 40px 15px;
            }
            
            .feature-card, .download-card {
                padding: 20px 15px;
            }
            
            .download-icon {
                font-size: 2.5em;
                gap: 10px;
                margin-bottom: 15px;
            }
            
            .download-card h3, .feature-card h3 {
                font-size: 1.3em;
            }
            
            .web-btn, .download-btn {
                padding: 12px 20px;
                font-size: 0.95em;
            }
            
            .web-btn {
                padding: 15px 25px;
                font-size: 1em;
            }
            
            .tech-stack {
                flex-wrap: wrap;
                gap: 10px;
            }
            
            .tech-badge {
                font-size: 0.8em;
                padding: 6px 12px;
            }
        }

        @media (max-width: 480px) {
            .hero h1 {
                font-size: 1.8em;
            }
            
            .download-icon {
                font-size: 2.2em;
                flex-direction: column;
                gap: 8px;
            }
            
            .download-icon i {
                font-size: 0.8em;
            }
            
            .creators-grid {
                grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                gap: 20px;
                padding: 0 10px;
            }
            
            .creator-card {
                padding: 20px 15px;
            }
        }
    </style>
</head>
<body>
    <!-- Header -->
    <header class="header">
        <div class="container">
            <nav class="nav">
                <div class="logo">
                    <i class="fas fa-cash-register"></i>
                    Punto de Venta
                </div>
            </nav>
        </div>
    </header>

    <!-- Hero Section -->
    <section class="hero">
        <div class="container">
            <h1>🛍️ Sistema Punto de Venta</h1>
            <p>Solución integral para la gestión de tu negocio. Disponible en múltiples plataformas para adaptarse a tus necesidades.</p>
        </div>
    </section>

    <!-- Features Section -->
    <section class="features">
        <div class="container">
            <h2 style="text-align: center; font-size: 2.5em; margin-bottom: 20px; color: #2c3e50;">Características Principales</h2>
            <p style="text-align: center; font-size: 1.2em; color: #5a6c7d; margin-bottom: 40px;">Un sistema completo diseñado para optimizar la gestión de tu punto de venta</p>
            
            <div class="features-grid">
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-users"></i>
                    </div>
                    <h3>Gestión de Clientes</h3>
                    <p>Administra tu base de clientes con facilidad. Historial de compras, datos de contacto y seguimiento personalizado.</p>
                </div>
                
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-boxes"></i>
                    </div>
                    <h3>Control de Inventario</h3>
                    <p>Mantén control total de tu inventario. Alertas de stock bajo, categorización de productos y reportes detallados.</p>
                </div>
                
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-truck"></i>
                    </div>
                    <h3>Gestión de Proveedores</h3>
                    <p>Administra tus proveedores eficientemente. Contactos, historial de compras y gestión de relaciones comerciales.</p>
                </div>
                
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-chart-line"></i>
                    </div>
                    <h3>Reportes y Análisis</h3>
                    <p>Toma decisiones informadas con reportes detallados de ventas, inventario y rendimiento del negocio.</p>
                </div>
                
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-receipt"></i>
                    </div>
                    <h3>Facturación Inteligente</h3>
                    <p>Sistema de facturación completo con generación automática de comprobantes y seguimiento de pagos.</p>
                </div>
                
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-shield-alt"></i>
                    </div>
                    <h3>Seguridad Avanzada</h3>
                    <p>Protección de datos con autenticación segura, control de acceso por roles y respaldos automáticos.</p>
                </div>
            </div>
        </div>
    </section>

    <!-- Downloads Section -->
    <section class="downloads">
        <div class="container">
            <h2>Descargas Disponibles</h2>
            <p class="downloads-subtitle">Elige la versión que mejor se adapte a tu dispositivo y necesidades</p>
            
            <div class="download-options">
                <div class="download-card">
                    <div class="download-icon">
                        <i class="fab fa-windows" title="Windows"></i>
                        <i class="fab fa-apple" title="macOS"></i>
                        <i class="fab fa-linux" title="Linux"></i>
                    </div>
                    <h3>Versión Escritorio</h3>
                    <p>Aplicación completa para Windows, macOS y Linux. Incluye todas las funcionalidades avanzadas y trabaja sin conexión a internet.</p>
                    <a href="desktop/puntoDeVenta/#" class="download-btn" download>
                        <i class="fas fa-download"></i> Descargar JAR
                    </a>
                </div>
                
                <div class="download-card">
                    <div class="download-icon">
                        <i class="fab fa-android"></i>
                    </div>
                    <h3>Versión Móvil</h3>
                    <p>Aplicación nativa para Android. Perfecta para tablets y dispositivos móviles. Sincronización en tiempo real con el sistema principal.</p>
                    <a href="app/PuntoDeVenta.apk" class="download-btn" download>
                        <i class="fas fa-download"></i> Descargar APK
                    </a>
                </div>
            </div>
        </div>
    </section>

    <!-- Web Access Section -->
    <section class="web-access">
        <div class="container">
            <h2>🌐 Acceso Web</h2>
            <p>Accede al sistema directamente desde tu navegador. No requiere instalación y está disponible desde cualquier dispositivo con conexión a internet.</p>
            <a href="web/" class="web-btn">
                <i class="fas fa-globe"></i> Ingresar al Sistema Web
            </a>
        </div>
    </section>

    <!-- Creators Section -->
    <section style="background: linear-gradient(135deg, #ecf0f1, #bdc3c7); padding: 80px 20px; text-align: center;">
        <div class="container">
            <h2 style="font-size: 2.5em; margin-bottom: 20px; color: #2c3e50;">Equipo de Desarrollo</h2>
            <p style="font-size: 1.2em; color: #5a6c7d; margin-bottom: 50px;">Conoce a los talentosos desarrolladores que hicieron posible este sistema</p>
            
            <div class="creators-grid">
                <div class="creator-card">
                    <div style="width: 80px; height: 80px; background: linear-gradient(135deg, #2c3e50, #3498db); border-radius: 50%; margin: 0 auto 20px; display: flex; align-items: center; justify-content: center; color: white; font-size: 2em; font-weight: bold;">A</div>
                    <h3 style="font-size: 1.4em; margin-bottom: 10px; color: #2c3e50;">Adolfo</h3>
                    <p style="color: #5a6c7d; font-size: 0.95em;">Desarrollador</p>
                </div>
                
                <div class="creator-card">
                    <div style="width: 80px; height: 80px; background: linear-gradient(135deg, #3498db, #2980b9); border-radius: 50%; margin: 0 auto 20px; display: flex; align-items: center; justify-content: center; color: white; font-size: 2em; font-weight: bold;">J</div>
                    <h3 style="font-size: 1.4em; margin-bottom: 10px; color: #2c3e50;">Jorge</h3>
                    <p style="color: #5a6c7d; font-size: 0.95em;">Desarrollador</p>
                </div>
                
                <div class="creator-card">
                    <div style="width: 80px; height: 80px; background: linear-gradient(135deg, #34495e, #2c3e50); border-radius: 50%; margin: 0 auto 20px; display: flex; align-items: center; justify-content: center; color: white; font-size: 2em; font-weight: bold;">R</div>
                    <h3 style="font-size: 1.4em; margin-bottom: 10px; color: #2c3e50;">Rafael</h3>
                    <p style="color: #5a6c7d; font-size: 0.95em;">Desarrollador</p>
                </div>
                
                <div class="creator-card">
                    <div style="width: 80px; height: 80px; background: linear-gradient(135deg, #2980b9, #3498db); border-radius: 50%; margin: 0 auto 20px; display: flex; align-items: center; justify-content: center; color: white; font-size: 2em; font-weight: bold;">E</div>
                    <h3 style="font-size: 1.4em; margin-bottom: 10px; color: #2c3e50;">Enrique</h3>
                    <p style="color: #5a6c7d; font-size: 0.95em;">Desarrollador</p>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; <?php echo date('Y'); ?> Sistema Punto de Venta. Desarrollado con tecnologías modernas para optimizar tu negocio.</p>
            
            <div class="tech-stack">
                <span class="tech-badge"><i class="fab fa-java"></i> Java Swing</span>
                <span class="tech-badge"><i class="fab fa-android"></i> Android</span>
                <span class="tech-badge"><i class="fab fa-php"></i> PHP</span>
                <span class="tech-badge"><i class="fas fa-database"></i> MySQL</span>
                <span class="tech-badge"><i class="fab fa-html5"></i> HTML5</span>
                <span class="tech-badge"><i class="fab fa-js"></i> JavaScript</span>
            </div>
        </div>
    </footer>

    <script>
        // Animaciones suaves al hacer scroll
        document.addEventListener('DOMContentLoaded', function() {
            const observerOptions = {
                threshold: 0.1,
                rootMargin: '0px 0px -50px 0px'
            };

            const observer = new IntersectionObserver(function(entries) {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        entry.target.style.opacity = '1';
                        entry.target.style.transform = 'translateY(0)';
                    }
                });
            }, observerOptions);

            // Observar las tarjetas de características
            document.querySelectorAll('.feature-card, .download-card, .creator-card').forEach(card => {
                card.style.opacity = '0';
                card.style.transform = 'translateY(20px)';
                card.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
                observer.observe(card);
            });

            // Efecto de partículas en el hero (opcional)
            createFloatingElements();
        });

        function createFloatingElements() {
            const hero = document.querySelector('.hero');
            const elements = ['💰', '📊', '🛒', '📱', '💻', '📈'];
            
            for (let i = 0; i < 6; i++) {
                const element = document.createElement('div');
                element.textContent = elements[i];
                element.style.position = 'absolute';
                element.style.fontSize = '2em';
                element.style.opacity = '0.1';
                element.style.pointerEvents = 'none';
                element.style.left = Math.random() * 100 + '%';
                element.style.top = Math.random() * 100 + '%';
                element.style.animation = `float ${3 + Math.random() * 4}s ease-in-out infinite alternate`;
                hero.appendChild(element);
            }
        }

        // CSS para la animación de flotación
        const style = document.createElement('style');
        style.textContent = `
            @keyframes float {
                0% { transform: translateY(0px) rotate(0deg); }
                100% { transform: translateY(-20px) rotate(10deg); }
            }
        `;
        document.head.appendChild(style);
    </script>
</body>
</html>
