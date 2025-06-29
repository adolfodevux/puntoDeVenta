const user = " ";
document.getElementById("userName").textContent = user;

const fechaInput = document.getElementById("fecha");
fechaInput.valueAsDate = new Date();

const btnAgregar   = document.getElementById("btnAgregar");
const cuerpoTabla  = document.getElementById("cuerpoTabla");
const totalEl      = document.getElementById("total");
let total = 0;

btnAgregar.addEventListener("click", () => {
  const codigo   = document.getElementById("codigo").value.trim();
  const producto = document.getElementById("producto").value.trim();
  const cantidad = parseInt(document.getElementById("cantidad").value, 10);
  const precio   = parseFloat(document.getElementById("precio").value);

  if (!codigo || !producto || isNaN(cantidad) || isNaN(precio)) {
    alert("Completa todos los campos arriba.");
    return;
  }

  const linea = cantidad * precio;

  const tr = document.createElement("tr");
  tr.innerHTML = `
    <td>${codigo}</td>
    <td>${producto}</td>
    <td>${cantidad}</td>
    <td>$${precio.toFixed(2)}</td>
    <td>$${linea.toFixed(2)}</td>
    <td>
      <button class="btn-del">
        <i class="fa-solid fa-trash"></i>
      </button>
    </td>
  `;
  cuerpoTabla.appendChild(tr);

  total += linea;
  totalEl.textContent = `$${total.toFixed(2)}`;

  ["codigo", "producto", "cantidad", "precio"].forEach(id => {
    const el = document.getElementById(id);
    el.value = id === "cantidad" ? 1 : "";
  });
});

cuerpoTabla.addEventListener("click", e => {
  if (e.target.closest(".btn-del")) {
    const fila = e.target.closest("tr");
    const producto = fila.children[1].textContent;
    const confirmacion = confirm(`¿Deseas eliminar el producto "${producto}" de la tabla?`);

    if (confirmacion) {
      const valor = parseFloat(fila.children[4].textContent.replace("$", ""));
      total -= valor;
      totalEl.textContent = `$${total.toFixed(2)}`;
      fila.remove();
    }
  }
});