<?php
require_once '../../../Models/database.php';
$db = Database::getInstance()->getConnection();
$productos = [];
$sql = "SELECT p.*, c.name as categoria FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE p.is_active = 1 ORDER BY p.name";
$res = $db->query($sql);
while ($row = $res->fetch_assoc()) {
    $productos[] = $row;
}
$categorias = [];
$resCat = $db->query("SELECT * FROM categories WHERE is_active = 1 ORDER BY name");
while ($row = $resCat->fetch_assoc()) {
    $categorias[] = $row;
}
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Inventario</title>
    <link rel="stylesheet" href="/Assets/css/dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" crossorigin="anonymous" />
    <style>
        body { background: #e0eafc; min-height: 100vh; }
        .inventario-container { max-width: 1200px; margin: 2.5rem auto; padding: 2rem 1rem; background: rgba(255,255,255,0.25); border-radius: 2rem; box-shadow: 0 8px 32px rgba(44,62,80,0.13); backdrop-filter: blur(6px); }
        .inventario-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-end;
            margin-bottom: 2.5rem;
            gap: 1.2rem;
            border-bottom: 2.5px solid #43cea2;
            padding-bottom: 1.1rem;
        }
        .inventario-header h2 {
            color: #1a2947;
            font-size: 2.1rem;
            font-family: 'Segoe UI',sans-serif;
            letter-spacing: 1px;
            font-weight: 800;
            text-shadow: 0 2px 8px #b2c9e6;
            margin: 0;
        }
        .btn-add { background: #43cea2; color: #fff; border: none; border-radius: 50px; padding: 0.8rem 2.2rem; font-size: 1.2rem; font-weight: 700; box-shadow: 0 4px 16px #43cea233; transition: all 0.18s; }
        .btn-add:hover { background: #185a9d; transform: scale(1.06); }
        /* Tabla de inventario */
        .table-responsive { width: 100%; overflow-x: auto; }
        .table-inventario { width: 100%; border-collapse: collapse; background: #fff; border-radius: 1.2rem; box-shadow: 0 4px 16px #b2c9e622; font-size: 1.08rem; min-width: 700px; }
        .table-inventario th, .table-inventario td { padding: 1em 0.7em; text-align: left; border-bottom: 1px solid #e0eafc; }
        .table-inventario th { background: #185a9d; color: #fff; font-weight: 800; border-top-left-radius: 1.2rem; border-top-right-radius: 1.2rem; }
        .table-inventario tr:last-child td { border-bottom: none; }
        .table-inventario td { color: #1a2947; }
        .table-inventario td .badge-stock { background: #43cea2; color: #fff; border-radius: 1em; padding: 0.3em 1.1em; font-weight: 700; font-size: 1em; display: inline-flex; align-items: center; gap: 0.4em; }
        .table-inventario td .badge-price { background: #fffbe6; color: #b7950b; border-radius: 1em; padding: 0.3em 1.1em; font-weight: 700; font-size: 1em; display: inline-flex; align-items: center; gap: 0.4em; }
        .table-inventario td .btn-edit, .table-inventario td .btn-delete { border-radius: 50%; width: 36px; height: 36px; display: inline-flex; align-items: center; justify-content: center; font-size: 1.1rem; border: none; cursor: pointer; margin-right: 0.3em; }
        .table-inventario td .btn-edit { background: #f1c40f; color: #7d6608; }
        .table-inventario td .btn-edit:hover { background: #f9e79f; }
        .table-inventario td .btn-delete { background: #e74c3c; color: #fff; }
        .table-inventario td .btn-delete:hover { background: #c0392b; color: #fff; }
        @media (max-width: 900px) {
            .table-inventario { font-size: 0.98rem; min-width: 600px; }
            .inventario-header h2 { font-size: 1.3rem; }
            .btn-add { padding: 0.6rem 1.5rem; font-size: 1rem; }
        }
        @media (max-width: 700px) {
            .inventario-container { padding: 0.5rem; border-radius: 0.7rem; margin: 1rem auto; }
            .inventario-header { 
                flex-direction: column; 
                gap: 1.2rem; 
                align-items: stretch;
                text-align: center;
            }
            .inventario-header h2 { font-size: 1.1rem; }
            .inventario-header > div { 
                justify-content: center; 
                flex-wrap: wrap;
            }
            .table-inventario { font-size: 0.92rem; min-width: 480px; }
            .table-inventario th, .table-inventario td { padding: 0.6em 0.3em; }
            .search-box input { width: 180px !important; font-size: 0.9rem !important; }
            .btn-add { padding: 0.5rem 1.2rem; font-size: 0.9rem; }
        }
        @media (max-width: 520px) {
            .inventario-container { 
                margin: 0.5rem; 
                padding: 0.3rem; 
                border-radius: 0.5rem;
            }
            .inventario-header { gap: 1rem; }
            .inventario-header h2 { font-size: 1rem; }
            .table-inventario { font-size: 0.85rem; min-width: 350px; }
            .table-inventario th, .table-inventario td { padding: 0.4em 0.1em; }
            .table-inventario th { font-size: 0.8rem; }
            .search-box input { 
                width: 150px !important; 
                font-size: 0.8rem !important;
                padding: 0.5em 1.8em 0.5em 0.8em !important;
            }
            .btn-add { 
                padding: 0.4rem 1rem; 
                font-size: 0.8rem;
                border-radius: 30px;
            }
            .badge-stock, .badge-price { 
                font-size: 0.7rem;
                padding: 0.2em 0.6em;
            }
            .btn-edit, .btn-delete { 
                width: 28px !important; 
                height: 28px !important; 
                font-size: 0.9rem !important;
            }
        }
        @media (max-width: 480px) {
            body { background: #e0eafc; }
            .inventario-container { 
                margin: 0.2rem; 
                padding: 0.2rem;
                max-width: 100%;
            }
            .inventario-header { 
                padding-bottom: 0.8rem;
                margin-bottom: 1.5rem;
            }
            .inventario-header h2 { font-size: 0.9rem; }
            .inventario-header > div { 
                flex-direction: column;
                gap: 0.8rem;
                align-items: center;
            }
            .search-box { 
                width: 100%;
                max-width: 200px;
            }
            .search-box input { 
                width: 100% !important;
                font-size: 0.75rem !important;
            }
            .btn-add { 
                padding: 0.3rem 0.8rem; 
                font-size: 0.75rem;
                min-width: 80px;
            }
            .table-responsive { 
                margin-top: 1rem !important;
                border-radius: 0.5rem;
                overflow-x: auto;
            }
            .table-inventario { 
                min-width: 320px;
                font-size: 0.7rem;
            }
            .table-inventario th, .table-inventario td { 
                padding: 0.3em 0.1em;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                max-width: 80px;
            }
            .table-inventario th:first-child,
            .table-inventario td:first-child { 
                max-width: 60px;
            }
            .table-inventario th:nth-child(3),
            .table-inventario td:nth-child(3) { 
                max-width: 50px;
            }
            .badge-stock, .badge-price { 
                font-size: 0.6rem;
                padding: 0.1em 0.4em;
            }
            .btn-edit, .btn-delete { 
                width: 24px !important; 
                height: 24px !important; 
                font-size: 0.7rem !important;
                margin-right: 0.1em !important;
            }
        }
        @media (max-width: 360px) {
            .inventario-container { 
                margin: 0.1rem; 
                padding: 0.1rem;
            }
            .inventario-header h2 { font-size: 0.8rem; }
            .btn-add { 
                padding: 0.25rem 0.6rem; 
                font-size: 0.7rem;
            }
            .table-inventario { 
                min-width: 280px;
                font-size: 0.65rem;
            }
            .table-inventario th, .table-inventario td { 
                padding: 0.2em 0.05em;
                max-width: 60px;
            }
            .btn-edit, .btn-delete { 
                width: 20px !important; 
                height: 20px !important; 
                font-size: 0.6rem !important;
            }
            /* Ocultar algunas columnas en pantallas muy pequeñas */
            .table-inventario th:nth-child(3),
            .table-inventario td:nth-child(3),
            .table-inventario th:nth-child(4),
            .table-inventario td:nth-child(4) { 
                display: none;
            }
        }
        /* Paginación moderna */
        #paginationNav {
            display: flex;
            gap: 0.5rem;
            align-items: center;
            justify-content: center;
            margin-top: 2rem;
            flex-wrap: wrap;
            padding: 1rem;
            background: rgba(255, 255, 255, 0.8);
            backdrop-filter: blur(10px);
            border-radius: 1rem;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
        #paginationNav button {
            border: none;
            border-radius: 50%;
            width: 2.2em;
            height: 2.2em;
            font-weight: 700;
            cursor: pointer;
            font-size: 1rem;
            transition: all 0.18s ease;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        #paginationNav button:disabled {
            opacity: 0.4;
            cursor: not-allowed;
        }
        #paginationNav button:not(:disabled):hover {
            background: #43cea2 !important;
            color: #fff !important;
            transform: scale(1.12);
        }
        #paginationNav .nav-arrow {
            border-radius: 50%;
            width: 2.5em;
            height: 2.5em;
            background: none;
            font-size: 1.5rem;
        }
        
        /* Responsive para paginación */
        @media (max-width: 768px) {
            #paginationNav {
                margin-top: 1.5rem;
                gap: 0.3rem;
                padding: 0.8rem;
            }
            #paginationNav button {
                width: 2em;
                height: 2em;
                font-size: 0.9rem;
            }
            #paginationNav .nav-arrow {
                width: 2.2em;
                height: 2.2em;
                font-size: 1.2rem;
            }
        }
        @media (max-width: 480px) {
            #paginationNav {
                margin-top: 1rem;
                gap: 0.2rem;
                padding: 0.5rem;
                flex-direction: row;
                justify-content: space-between;
            }
            #paginationNav button {
                width: 1.8em;
                height: 1.8em;
                font-size: 0.8rem;
            }
            #paginationNav .nav-arrow {
                width: 2em;
                height: 2em;
                font-size: 1rem;
                flex: 0 0 auto;
            }
            /* Contenedor para números de página */
            #paginationNav button:not(.nav-arrow) {
                flex: 0 0 auto;
                margin: 0 0.1rem;
            }
        }
        @media (max-width: 360px) {
            #paginationNav {
                gap: 0.1rem;
                padding: 0.3rem;
            }
            #paginationNav button {
                width: 1.5em;
                height: 1.5em;
                font-size: 0.7rem;
            }
            #paginationNav .nav-arrow {
                width: 1.8em;
                height: 1.8em;
                font-size: 0.9rem;
            }
        }
        
        /* Estilos para SweetAlert2 - Notificaciones */
        .swal2-popup {
            border-radius: 2rem !important;
            backdrop-filter: blur(10px) !important;
            background: rgba(255, 255, 255, 0.95) !important;
            box-shadow: 0 8px 32px rgba(44, 62, 80, 0.15) !important;
        }
        .swal2-title {
            color: #1a2947 !important;
            font-weight: 800 !important;
            font-size: 1.8rem !important;
        }
        .swal2-content {
            color: #1a2947 !important;
            font-size: 1.1rem !important;
        }
        .swal2-confirm {
            background: #43cea2 !important;
            border-radius: 2rem !important;
            padding: 0.8rem 2rem !important;
            font-weight: 700 !important;
            font-size: 1.1rem !important;
            box-shadow: 0 4px 16px rgba(67, 206, 162, 0.3) !important;
        }
        .swal2-cancel {
            background: #6c757d !important;
            border-radius: 2rem !important;
            padding: 0.8rem 2rem !important;
            font-weight: 700 !important;
            font-size: 1.1rem !important;
        }
        .swal2-confirm:hover {
            background: #185a9d !important;
            transform: scale(1.05) !important;
        }
        .swal2-icon.swal2-success {
            border-color: #43cea2 !important;
            color: #43cea2 !important;
        }
        .swal2-icon.swal2-error {
            border-color: #e74c3c !important;
            color: #e74c3c !important;
        }
        .swal2-icon.swal2-warning {
            border-color: #f39c12 !important;
            color: #f39c12 !important;
        }
        /* Modal glassmorphism */
        .modal-bg { display: none; position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(30,60,90,0.18); align-items: center; justify-content: center; z-index: 9999; }
        .modal { background: rgba(255,255,255,0.85); padding: 2.5rem 2rem 2rem 2rem; border-radius: 2rem; min-width: 320px; max-width: 95vw; box-shadow: 0 8px 32px #185a9d33; backdrop-filter: blur(8px); position: relative; }
        .modal h3 { margin-bottom: 1.5rem; color: #185a9d; font-size: 1.5rem; font-weight: 800; }
        .modal input, .modal select, .modal textarea { width: 100%; padding: 0.7rem; margin-bottom: 1.2rem; border-radius: 1em; border: 1.5px solid #b2c9e6; font-size: 1.08rem; background: #f8fafc; transition: border 0.18s; }
        .modal input:focus, .modal select:focus, .modal textarea:focus { border: 1.5px solid #43cea2; outline: none; }
        .modal .btn { width: 100%; margin-top: 0.7rem; border-radius: 1em; font-size: 1.1rem; }
        .close-modal { position: absolute; top: 1.2rem; right: 1.5rem; font-size: 1.7rem; color: #e74c3c; cursor: pointer; font-weight: 700; transition: color 0.18s; }
        .close-modal:hover { color: #c0392b; }
    </style>
</head>
<body>
<div class="inventario-container">
    <div class="inventario-header">
        <div style="display:flex;align-items:center;gap:1.1rem;">
            <h2 style="margin:0;">Inventario de Productos</h2>
        </div>
        <div style="display:flex;align-items:center;gap:1.2rem;">
            <div class="search-box" style="position:relative;">
                <input type="text" id="searchInput" placeholder="Buscar producto..." style="padding:0.6em 2.2em 0.6em 1.1em;border-radius:2em;border:1.5px solid #b2c9e6;font-size:1.08em;outline:none;box-shadow:0 2px 8px #b2c9e622;transition:border 0.18s;width:210px;">
                <span style="position:absolute;right:0.8em;top:50%;transform:translateY(-50%);color:#43cea2;font-size:1.2em;"><i class="fa fa-search"></i></span>
            </div>
            <button class="btn-add" onclick="openAddModal()"><i class="fa fa-plus"></i> Agregar Producto</button>
            <button class="btn-add" onclick="window.location.href='../index.php'"><i class="fa fa-arrow-left"></i> Regresar</button>
        </div>
    </div>
    <div class="table-responsive" style="margin-top:2rem;">
        <table class="table-inventario" id="productosTable">
            <thead>
                <tr>
                    <th>Nombre</th>
                    <th>Categoría</th>
                    <th>Descripción</th>
                    <th>Código de Barras</th>
                    <th>Stock</th>
                    <th>Precio</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody id="productosTbody">
                <!-- Aquí se renderizan los productos -->
            </tbody>
        </table>
    </div>
    <nav id="paginationNav"></nav>
</div>
<!-- Modal para agregar/editar -->
<div class="modal-bg" id="modalBg">
    <div class="modal" id="modalForm">
        <span class="close-modal" onclick="closeModal()">&times;</span>
        <h3 id="modalTitle">Agregar Producto</h3>
        <form id="productForm" autocomplete="off">
            <input type="hidden" name="id" id="prodId">
            <div class="form-group">
                <label>Nombre</label>
                <input type="text" name="name" id="prodName" required placeholder="Nombre del producto">
            </div>
            <div class="form-group">
                <label>Categoría</label>
                <select name="category_id" id="prodCategory" required>
                    <option value="">Selecciona una categoría</option>
                    <?php foreach($categorias as $cat): ?>
                    <option value="<?= $cat['id'] ?>"><?= htmlspecialchars($cat['name']) ?></option>
                    <?php endforeach; ?>
                </select>
            </div>
            <div class="form-group">
                <label>Descripción</label>
                <textarea name="description" id="prodDescription" rows="2" placeholder="Descripción del producto"></textarea>
            </div>
            <div class="form-group">
                <label>Código de Barras</label>
                <input type="text" name="barcode" id="prodBarcode" placeholder="Código de barras" readonly>
            </div>
            <div class="form-group">
                <label>Stock</label>
                <input type="number" name="stock" id="prodStock" min="0" required placeholder="Cantidad en stock">
            </div>
            <div class="form-group">
                <label>Precio</label>
                <input type="number" name="price" id="prodPrice" min="0" step="0.01" required placeholder="Precio unitario">
            </div>
            <button type="submit" class="btn btn-add" id="submitBtn"><i class="fa fa-save"></i> Guardar</button>
        </form>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script>
let productosData = <?php echo json_encode($productos); ?>;
let currentPage = 1;
const pageSize = 8;

function renderProductosPage(page, dataOverride) {
    const tbody = document.getElementById('productosTbody');
    const data = dataOverride || productosData;
    if (!data || data.length === 0) {
        tbody.innerHTML = `<tr><td colspan='7' style='text-align:center;color:#888;font-size:1.2rem;padding:2rem;'>No hay productos en inventario</td></tr>`;
        document.getElementById('paginationNav').innerHTML = '';
        return;
    }
    const totalPages = Math.ceil(data.length / pageSize);
    currentPage = Math.max(1, Math.min(page, totalPages));
    const start = (currentPage - 1) * pageSize;
    const end = start + pageSize;
    const pageItems = data.slice(start, end);
    tbody.innerHTML = pageItems.map(prod => {
        const name = (prod.name||'').replace(/'/g, "&#39;").replace(/\"/g, '&quot;');
        const desc = (prod.description||'').replace(/'/g, "&#39;").replace(/\"/g, '&quot;');
        const barcode = (prod.barcode||'').replace(/'/g, "&#39;").replace(/\"/g, '&quot;');
        return `
        <tr data-id="${prod.id}">
            <td>${name}</td>
            <td>${prod.categoria || ''}</td>
            <td>${desc}</td>
            <td>${barcode}</td>
            <td><span class='badge-stock'><i class='fa fa-box'></i> ${prod.stock}</span></td>
            <td><span class='badge-price'><i class='fa fa-dollar-sign'></i> $${parseFloat(prod.price).toFixed(2)}</span></td>
            <td>
                <button class='btn-edit' title='Editar' onclick="openEditModal(${prod.id}, '${name}', ${prod.category_id}, '${desc}', '${barcode}', ${prod.stock}, ${prod.price})"><i class='fa-solid fa-pen-to-square'></i></button>
                <button class='btn-delete' title='Eliminar' onclick='deleteProduct(${prod.id})'><i class='fa-solid fa-trash'></i></button>
            </td>
        </tr>
        `;
    }).join('');
    renderPagination(totalPages, dataOverride);
}

function renderPagination(totalPages, dataOverride) {
    const nav = document.getElementById('paginationNav');
    if (totalPages <= 1) { nav.innerHTML = ''; return; }
    let html = '';
    
    // Botón anterior
    html += `<button class='nav-arrow' onclick='goToPage(${Math.max(1, currentPage-1)}, ${!!dataOverride})' ${currentPage===1?'disabled':''}><i class='fa fa-chevron-left'></i></button>`;
    
    // Botones de páginas
    for(let i=1;i<=totalPages;i++) {
        const isActive = i===currentPage;
        html += `<button onclick='goToPage(${i}, ${!!dataOverride})' style='background:${isActive?'#185a9d':'#e0eafc'};color:${isActive?'#fff':'#185a9d'};'>${i}</button>`;
    }
    
    // Botón siguiente  
    html += `<button class='nav-arrow' onclick='goToPage(${Math.min(totalPages, currentPage+1)}, ${!!dataOverride})' ${currentPage===totalPages?'disabled':''}><i class='fa fa-chevron-right'></i></button>`;
    
    nav.innerHTML = html;
}
function goToPage(page, filtered) {
    if (filtered) {
        const term = searchInput.value.trim().toLowerCase();
        const filteredData = productosData.filter(prod => {
            return (
                (prod.name && prod.name.toLowerCase().includes(term)) ||
                (prod.categoria && prod.categoria.toLowerCase().includes(term)) ||
                (prod.category_name && prod.category_name.toLowerCase().includes(term)) ||
                (prod.barcode && prod.barcode.toLowerCase().includes(term)) ||
                (prod.description && prod.description.toLowerCase().includes(term))
            );
        });
        renderProductosPage(page, filteredData);
    } else {
        renderProductosPage(page);
    }
}
function openAddModal() {
    document.getElementById('modalTitle').innerText = 'Agregar Producto';
    document.getElementById('productForm').reset();
    document.getElementById('prodId').value = '';
    document.getElementById('prodBarcode').value = generarBarcode();
    document.getElementById('modalBg').style.display = 'flex';
}
function generarBarcode() {
    let code = '';
    for(let i=0; i<13; i++) code += Math.floor(Math.random()*10);
    return code;
}
function openEditModal(id, name, category_id, description, barcode, stock, price) {
    document.getElementById('modalTitle').innerText = 'Editar Producto';
    document.getElementById('prodId').value = id;
    document.getElementById('prodName').value = name;
    document.getElementById('prodCategory').value = category_id;
    document.getElementById('prodDescription').value = description;
    document.getElementById('prodBarcode').value = barcode;
    document.getElementById('prodStock').value = stock;
    document.getElementById('prodPrice').value = price;
    document.getElementById('modalBg').style.display = 'flex';
}
function closeModal() {
    document.getElementById('modalBg').style.display = 'none';
}
// --- CRUD DINÁMICO PARA INVENTARIO (sin recargar la página, con cards) ---
async function refreshInventoryGrid() {
    const res = await fetch('../../../Controllers/ProductsController.php?action=list');
    const data = await res.json();
    if (!data.success) {
        document.getElementById('productosGrid').innerHTML = `<div style='grid-column:1/-1;text-align:center;color:#e74c3c;font-size:1.2rem;padding:2rem;'>Error al cargar productos</div>`;
        Swal.fire({ icon: 'error', title: 'Error', text: data.message || 'No se pudo actualizar el inventario' });
        return;
    }
    productosData = data.data;
    renderProductosPage(1);
}
const productForm = document.getElementById('productForm');
if (productForm) {
    productForm.onsubmit = async function(e) {
        e.preventDefault();
        const submitBtn = document.getElementById('submitBtn');
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fa fa-spinner fa-spin"></i> Guardando...';
        const id = document.getElementById('prodId').value;
        const name = document.getElementById('prodName').value.trim();
        const category_id = document.getElementById('prodCategory').value;
        const description = document.getElementById('prodDescription').value.trim();
        const barcode = document.getElementById('prodBarcode').value.trim();
        const stock = document.getElementById('prodStock').value;
        const price = document.getElementById('prodPrice').value;
        let action = id ? 'edit' : 'add';
        const payload = { action, id, name, category_id, description, barcode, stock, price };
        try {
            const res = await fetch('../../../Controllers/ProductsController.php', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            const data = await res.json();
            if(data.success) {
                closeModal();
                Swal.fire({
                    icon: 'success',
                    title: action === 'add' ? 'Producto agregado' : 'Producto actualizado',
                    showConfirmButton: false,
                    timer: 1200
                });
                await refreshInventoryGrid();
                productForm.reset();
            } else {
                Swal.fire({ icon: 'error', title: 'Error', text: data.message || 'Error al guardar' });
            }
        } catch (err) {
            Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo conectar con el servidor.' });
        } finally {
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i class="fa fa-save"></i> Guardar';
        }
    };
}
async function deleteProduct(id) {
    Swal.fire({
        title: '¿Eliminar producto?',
        text: 'Esta acción no se puede deshacer',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#e74c3c',
        cancelButtonColor: '#6c757d',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar'
    }).then(async (result) => {
        if(result.isConfirmed) {
            const res = await fetch('../../../Controllers/ProductsController.php', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ action: 'delete', id })
            });
            const data = await res.json();
            if(data.success) {
                Swal.fire({ icon: 'success', title: 'Eliminado', showConfirmButton: false, timer: 1200 });
                await refreshInventoryGrid();
            } else {
                Swal.fire({ icon: 'error', title: 'Error', text: data.message || 'Error al eliminar' });
            }
        }
    });
}
document.addEventListener('DOMContentLoaded', function() {
    renderProductosPage(1);
});
// Búsqueda interactiva
const searchInput = document.getElementById('searchInput');
if (searchInput) {
    searchInput.addEventListener('input', function() {
        const term = this.value.trim().toLowerCase();
        if (!term) {
            renderProductosPage(1);
            return;
        }
        const filtered = productosData.filter(prod => {
            return (
                (prod.name && prod.name.toLowerCase().includes(term)) ||
                (prod.categoria && prod.categoria.toLowerCase().includes(term)) ||
                (prod.category_name && prod.category_name.toLowerCase().includes(term)) ||
                (prod.barcode && prod.barcode.toLowerCase().includes(term)) ||
                (prod.description && prod.description.toLowerCase().includes(term))
            );
        });
        renderProductosPage(1, filtered);
    });
}
</script>
</body>
</html>
