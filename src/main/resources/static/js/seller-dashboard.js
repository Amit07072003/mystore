let categoriesMap = {};

document.addEventListener("DOMContentLoaded", () => {
    loadProducts();
});

// Sidebar toggle
document.getElementById('sidebarToggle').onclick = () => {
    document.getElementById('sidebar').classList.toggle('active');
};

function showSection(id) {
    document.querySelectorAll(".section").forEach(sec => sec.classList.remove("active"));
    document.getElementById(id).classList.add("active");

    // Auto-close sidebar on mobile
    if (window.innerWidth <= 992) {
        document.getElementById('sidebar').classList.remove('active');
    }

    if (id === "manageProducts") loadProducts();
    if (id === "viewOrders") loadOrders();
    if (id === "viewCategories") loadCategoryTable();
}

// =============================
// Load Categories for products
// =============================
async function loadCategories() {
    const res = await fetch("/categories");
    if (!res.ok) throw new Error("Failed to load categories");
    const categories = await res.json();

    categoriesMap = {};
    categories.forEach(cat => {
        categoriesMap[cat.id] = cat.name;
    });
}

// =============================
// Load Product Table
// =============================
async function loadProducts() {
    try {
        await loadCategories();
        const res = await fetch("/api/products");
        if (!res.ok) throw new Error("Failed to load products");
        const products = await res.json();

        const tbody = document.getElementById("productTableBody");
        tbody.innerHTML = "";

        if (products.length === 0) {
            tbody.innerHTML = "<tr><td colspan='6' style='text-align:center;'>No products found</td></tr>";
            return;
        }

        products.forEach(product => {
            const categoryName =
                product.category?.name ||
                categoriesMap[product.categoryId] ||
                "N/A";

            const stock = product.stockQuantity != null ? product.stockQuantity : 0;

            const row = `
          <tr>
            <td data-label="ID">${product.id}</td>
            <td data-label="Name">${product.name}</td>
            <td data-label="Category">${categoryName}</td>
            <td data-label="Price">₹${product.price}</td>
            <td data-label="Stock">${stock}</td>
            <td data-label="Actions">
              <button class="edit-btn" onclick="editProduct(${product.id})">Edit</button>
              <button class="delete-btn" onclick="deleteProduct(${product.id})">Delete</button>
            </td>
          </tr>`;
            tbody.innerHTML += row;
        });

    } catch (error) {
        console.error(error);
        document.getElementById("productTableBody").innerHTML =
            "<tr><td colspan='6' style='text-align:center;color:red;'>Failed to load products</td></tr>";
    }
}

// =============================
// Load Category Table
// =============================
async function loadCategoryTable() {
    try {
        const res = await fetch("/categories");
        if (!res.ok) throw new Error("Failed to load categories");

        const categories = await res.json();
        const tbody = document.getElementById("categoryTableBody");
        tbody.innerHTML = "";

        if (categories.length === 0) {
            tbody.innerHTML = "<tr><td colspan='2' style='text-align:center;'>No categories found</td></tr>";
            return;
        }

        categories.forEach(cat => {
            const row = `
          <tr>
            <td data-label="ID">${cat.id}</td>
            <td data-label="Category Name">${cat.name}</td>
          </tr>`;
            tbody.innerHTML += row;
        });

    } catch (error) {
        console.error(error);
        document.getElementById("categoryTableBody").innerHTML =
            "<tr><td colspan='2' style='text-align:center;color:red;'>Failed to load categories</td></tr>";
    }
}

// =============================
// Load Orders
// =============================
async function loadOrders() {
    const sellerId = document.getElementById("sellerId").value;

    try {
        const res = await fetch(`/seller/orders?sellerId=${sellerId}`, { credentials: 'include' });
        if (!res.ok) throw new Error("Failed to load orders");

        const orders = await res.json();
        const tbody = document.getElementById("ordersTableBody");
        tbody.innerHTML = "";

        if (orders.length === 0) {
            tbody.innerHTML = "<tr><td colspan='6' style='text-align:center;'>No orders found</td></tr>";
            return;
        }

        orders.forEach(order => {
            const date = new Date(order.orderDate).toLocaleString();
            const products = order.items?.length
                ? order.items.map(i => `${i.productName} (x${i.quantity})`).join(", ")
                : "N/A";

            const statusOptions = ["PENDING", "PAID", "SHIPPED", "DELIVERED", "CANCELLED"];
            const optionsHtml = statusOptions
                .map(opt => `<option value="${opt}" ${order.status === opt ? "selected" : ""}>${opt}</option>`)
                .join("");

            const row = `
          <tr>
            <td data-label="Order ID">${order.id}</td>
            <td data-label="Products">${products}</td>
            <td data-label="Buyer">${order.userEmail || "N/A"}</td>
            <td data-label="Amount">₹${order.totalAmount}</td>
            <td data-label="Date">${date}</td>
            <td data-label="Status">
              <select id="status-${order.id}">${optionsHtml}</select>
              <button onclick="updateOrderStatus(${order.id})">Update</button>
            </td>
          </tr>`;
            tbody.innerHTML += row;
        });

    } catch (error) {
        console.error(error);
        document.getElementById("ordersTableBody").innerHTML =
            "<tr><td colspan='6' style='text-align:center;color:red;'>Failed to load orders</td></tr>";
    }
}

// =============================
// Update Order Status
// =============================
async function updateOrderStatus(orderId) {
    const status = document.getElementById(`status-${orderId}`).value;

    try {
        const res = await fetch(`/seller/orders/${orderId}/status?status=${status}`, {
            method: "PUT",
            credentials: "include"
        });

        if (!res.ok) throw new Error("Failed to update order");

        alert("Order status updated!");
        loadOrders();

    } catch (err) {
        console.error(err);
        alert("Error updating status");
    }
}

// =============================
// Product Actions
// =============================
function editProduct(id) {
    window.location.href = `/products/view/edit/${id}`;
}

function deleteProduct(id) {
    if (!confirm("Are you sure you want to delete this product?")) return;

    fetch(`/api/products/${id}`, { method: "DELETE" })
        .then(res => {
            if (res.ok) {
                alert("Product deleted successfully!");
                loadProducts();
            } else {
                alert("Failed to delete product.");
            }
        })
        .catch(() => alert("Error deleting product"));
}