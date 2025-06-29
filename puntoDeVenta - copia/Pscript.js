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

let id_proveedor = 1;
let filaSeleccionada = null;

btnAgregarCliente.addEventListener("click", () => {
  const rfc      = document.getElementById("rfc").value.trim();
  const empresa  = document.getElementById("empresa").value.trim();
  const telefono = document.getElementById("telefono_proveedor").value.trim();

  if (!rfc || !empresa || !telefono) {
    alert("Completa todos los campos arriba.");
    return;
  }

  const esDuplicado = Array.from(cuerpoTabla.rows).some(tr => {
    const existeRFC      = tr.children[1].textContent.trim().toLowerCase() === rfc.toLowerCase();
    const existeEmpresa  = tr.children[2].textContent.trim().toLowerCase() === empresa.toLowerCase();
    const existeTelefono = tr.children[3].textContent.trim() === telefono;
    return existeRFC || existeEmpresa || existeTelefono;
  });

  if (esDuplicado) {
    alert("Hay datos similares con alguna información similar, le pedimos cambiarlos.");
    return;
  }

  const tr = document.createElement("tr");
  tr.innerHTML = `
    <td>${id_proveedor}</td>
    <td>${rfc}</td>
    <td>${empresa}</td>
    <td>${telefono}</td>
  `;
  cuerpoTabla.appendChild(tr);
  id_proveedor++;

  tr.addEventListener("click", () => {
    filaSeleccionada = tr;
    document.getElementById("rfc").value = tr.children[1].textContent;
    document.getElementById("empresa").value = tr.children[2].textContent;
    document.getElementById("telefono_proveedor").value = tr.children[3].textContent;

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

  const nombre = filaSeleccionada.children[2].textContent;
  const confirmar = confirm(`¿Estás seguro que deseas eliminar a la empresa "${nombre}"?`);
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

  const nuevoRFC      = document.getElementById("rfc").value.trim();
  const nuevaEmpresa  = document.getElementById("empresa").value.trim();
  const nuevoTelefono = document.getElementById("telefono_proveedor").value.trim();

  if (!nuevoRFC || !nuevaEmpresa || !nuevoTelefono) {
    alert("Completa todos los campos para editar.");
    return;
  }

  filaSeleccionada.children[1].textContent = nuevoRFC;
  filaSeleccionada.children[2].textContent = nuevaEmpresa;
  filaSeleccionada.children[3].textContent = nuevoTelefono;

  alert("Proveedor actualizado correctamente.");
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
    const rfc      = tr.children[1].textContent.toLowerCase();
    const empresa  = tr.children[2].textContent.toLowerCase();
    const telefono = tr.children[3].textContent;

    const coincide =
      rfc.includes(termino) ||
      empresa.includes(termino) ||
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
  ["rfc", "empresa", "telefono_proveedor"].forEach(id => {
    document.getElementById(id).value = "";
  });
}