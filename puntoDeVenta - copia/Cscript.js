const user = " ";
document.getElementById("userName").textContent = user;

const btnAgregarCliente   = document.getElementById("btnAgregarCliente");
const btnBuscarCliente    = document.getElementById("btnBuscarCliente");
const btnEditarCliente    = document.getElementById("btnEditarCliente");
const btnEliminarCliente  = document.getElementById("btnEliminarCliente");
const cuerpoTabla         = document.getElementById("cuerpoTabla");

const formularioBusqueda     = document.getElementById("formulario-busqueda");
const btnEjecutarBusqueda    = document.getElementById("btnEjecutarBusqueda");
const btnCerrarBusqueda      = document.getElementById("btnCerrarBusqueda");
const inputBusqueda          = document.getElementById("terminoBusqueda");
const resultadoBusqueda      = document.getElementById("resultadoBusqueda");

let id_cliente = 1;
let filaSeleccionada = null;

btnAgregarCliente.addEventListener("click", () => {
  const nombre_cliente = document.getElementById("nombre_cliente").value.trim();
  const apellidos      = document.getElementById("apellidos").value.trim();
  const telefono       = document.getElementById("telefono_cliente").value.trim();

  if (!nombre_cliente || !apellidos || !telefono) {
    alert("Completa todos los campos arriba.");
    return;
  }

  const tr = document.createElement("tr");
  tr.innerHTML = `
    <td>${id_cliente}</td>
    <td>${nombre_cliente}</td>
    <td>${apellidos}</td>
    <td>${telefono}</td>
  `;
  cuerpoTabla.appendChild(tr);
  id_cliente++;

  tr.addEventListener("click", () => {
    filaSeleccionada = tr;
    document.getElementById("nombre_cliente").value = tr.children[1].textContent;
    document.getElementById("apellidos").value = tr.children[2].textContent;
    document.getElementById("telefono_cliente").value = tr.children[3].textContent;

    Array.from(cuerpoTabla.rows).forEach(r => r.classList.remove("selected"));
    tr.classList.add("selected");
  });

  limpiarFormulario();
});

btnEliminarCliente.addEventListener("click", () => {
  if (!filaSeleccionada) {
    alert("Selecciona una fila para eliminar.");
    return;
  }

  const nombre = filaSeleccionada.children[1].textContent;
  const confirmar = confirm(`¿Estás seguro que deseas eliminar al cliente "${nombre}"?`);
  if (confirmar) {
    filaSeleccionada.remove();
    filaSeleccionada = null;
    limpiarFormulario();
  }
});

btnEditarCliente.addEventListener("click", () => {
  if (!filaSeleccionada) {
    alert("Selecciona una fila para editar.");
    return;
  }

  const nuevoNombre     = document.getElementById("nombre_cliente").value.trim();
  const nuevosApellidos = document.getElementById("apellidos").value.trim();
  const nuevoTelefono   = document.getElementById("telefono_cliente").value.trim();

  if (!nuevoNombre || !nuevosApellidos || !nuevoTelefono) {
    alert("Completa todos los campos para editar.");
    return;
  }

  filaSeleccionada.children[1].textContent = nuevoNombre;
  filaSeleccionada.children[2].textContent = nuevosApellidos;
  filaSeleccionada.children[3].textContent = nuevoTelefono;

  alert("Cliente actualizado correctamente.");
  filaSeleccionada.classList.remove("selected");
  filaSeleccionada = null;
  limpiarFormulario();
});

btnBuscarCliente.addEventListener("click", () => {
  formularioBusqueda.style.display = "block";
  inputBusqueda.focus();
});

btnEjecutarBusqueda.addEventListener("click", () => {
  const termino = inputBusqueda.value.trim().toLowerCase();
  let coincidencias = 0;

  const filaVacia = document.getElementById("fila-vacia");
  if (filaVacia) filaVacia.remove();

  const filas = Array.from(cuerpoTabla.rows);
  filas.forEach(tr => {
    const nombre    = tr.children[1].textContent.toLowerCase();
    const apellidos = tr.children[2].textContent.toLowerCase();
    const telefono  = tr.children[3].textContent;

    const coincide =
      nombre.includes(termino) ||
      apellidos.includes(termino) ||
      telefono.includes(termino);

    tr.style.display = coincide ? "" : "none";
    if (coincide) coincidencias++;
  });

  if (coincidencias === 0) {
    const trVacio = document.createElement("tr");
    trVacio.id = "fila-vacia";
    trVacio.innerHTML = `
      <td colspan="4" style="text-align:center; color:gray; padding: 10px;">
        No se encontraron coincidencias.
      </td>
    `;
    cuerpoTabla.appendChild(trVacio);
  }

  resultadoBusqueda.textContent = `Coincidencias: ${coincidencias}`;
});
btnCerrarBusqueda.addEventListener("click", () => {
  formularioBusqueda.style.display = "none";
  inputBusqueda.value = "";
  resultadoBusqueda.textContent = "";

  Array.from(cuerpoTabla.rows).forEach(tr => {
    tr.style.display = "";
  });

  const filaVacia = document.getElementById("fila-vacia");
  if (filaVacia) filaVacia.remove();
});

function limpiarFormulario() {
  ["nombre_cliente", "apellidos", "telefono_cliente"].forEach(id => {
    document.getElementById(id).value = "";
  });
}