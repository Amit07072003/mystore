// Sidebar toggle for mobile
document.getElementById('sidebarToggle').onclick = () => {
    document.getElementById('sidebar').classList.toggle('active');
};

function showSection(sectionId) {
    // Switch active section
    document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
    document.getElementById(sectionId).classList.add('active');

    // Auto-close sidebar on mobile
    if (window.innerWidth <= 992) {
        document.getElementById('sidebar').classList.remove('active');
    }
}


// Logout
document.getElementById('logoutBtn').onclick = () => {
    fetch('/auth/logout', { method: 'POST', credentials: 'include' })
        .then(() => window.location.href = '/login');
};

// ------------------- Products -------------------
async function loadProducts() {
    const res = await fetch('/api/products', { credentials: 'include' });
    const products = await res.json();
    const tbody = document.getElementById('productsTable');
    tbody.innerHTML = '';
    products.forEach(p => {
        const row = `
        <tr>
          <td data-label="ID">${p.id}</td>
          <td data-label="Name">${p.name}</td>
          <td data-label="Category">${p.categoryName || ''}</td>
          <td data-label="Price">${p.price}</td>
          <td data-label="Stock">${p.stockQuantity}</td>
          <td data-label="Actions">
            <a href="/products/view/edit/${p.id}" class="btn btn-sm btn-warning">Edit</a>
            <button onclick="deleteProduct(${p.id})" class="btn btn-sm btn-danger">Delete</button>
          </td>
        </tr>`;
        tbody.insertAdjacentHTML('beforeend', row);
    });
}

function deleteProduct(id) {
    if (!confirm("Are you sure you want to delete this product?")) return;
    fetch(`/api/products/${id}`, { method: 'DELETE', credentials: 'include' })
        .then(res => {
            if (res.ok) {
                alert("Product deleted successfully");
                loadProducts();
            } else {
                res.text().then(text => alert("Failed to delete product: " + text));
            }
        })
        .catch(() => alert("Error deleting product"));
}

// ------------------- Categories -------------------
async function loadCategories() {
    const res = await fetch('/categories', { credentials: 'include' });
    const categories = await res.json();
    const tbody = document.getElementById('categoriesTable');
    tbody.innerHTML = '';
    categories.forEach(c => {
        const row = `
        <tr>
          <td data-label="ID">${c.id}</td>
          <td data-label="Name">${c.name}</td>
          <td data-label="Actions">
            <a href="/admin/category/edit/${c.id}" class="btn btn-sm btn-warning">Edit</a>
            <button onclick="deleteCategory(${c.id})" class="btn btn-sm btn-danger">Delete</button>
          </td>
        </tr>`;
        tbody.insertAdjacentHTML('beforeend', row);
    });
}

function deleteCategory(id) {
    if (!confirm("Are you sure you want to delete this category?")) return;
    fetch(`/categories/${id}`, { method: 'DELETE', credentials: 'include' })
        .then(res => {
            if (!res.ok) throw new Error("Failed to delete category");
            alert("Category deleted successfully");
            loadCategories();
        })
        .catch(err => alert(err.message));
}

// ------------------- Orders -------------------
async function loadOrders() {
    const res = await fetch('/admin/orders', { credentials: 'include' });
    const orders = await res.json();
    const tbody = document.getElementById('ordersTable');
    tbody.innerHTML = '';
    orders.forEach(o => {
        const row = `
        <tr>
          <td data-label="Order ID">${o.id}</td>
          <td data-label="User">${o.userEmail}</td>
          <td data-label="Total">${o.totalAmount}</td>
          <td data-label="Status">${o.status}</td>
          <td data-label="Actions">
            <button onclick="markDelivered(${o.id})" class="btn btn-sm btn-success">Mark Delivered</button>
          </td>
        </tr>`;
        tbody.insertAdjacentHTML('beforeend', row);
    });
}

async function markDelivered(id) {
    await fetch(`/admin/orders/${id}/deliver`, { method: 'PUT', credentials: 'include' });
    loadOrders();
}

// ------------------- Users -------------------
async function loadUsers() {
    const res = await fetch('/admin/user/roles', { credentials: 'include' });
    const users = await res.json();
    const tbody = document.getElementById('usersTable');
    tbody.innerHTML = '';
    users.forEach(u => {
        const row = `
        <tr>
          <td data-label="User ID">${u.id}</td>
          <td data-label="Email">${u.email}</td>
          <td data-label="Roles">${u.roles.join(', ')}</td>
          <td data-label="Actions">
            <button onclick="openUserRolesModal(${u.id}, '${u.roles.join(',')}')" class="btn btn-sm btn-primary">Edit Roles</button>
          </td>
        </tr>`;
        tbody.insertAdjacentHTML('beforeend', row);
    });
}

function openUserRolesModal(userId, roles) {
    document.querySelector("#userRolesForm input[name='userId']").value = userId;
    document.querySelector("#userRolesForm input[name='roles']").value = roles;
    document.getElementById('userRolesModal').style.display = 'block';
}

function closeUserRolesModal() {
    document.getElementById('userRolesModal').style.display = 'none';
}

document.querySelector('#userRolesForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const userId = this.userId.value;
    const roles = this.roles.value.split(',').map(r => r.trim());
    fetch(`/admin/user/roles/${userId}`, {
        method: 'PUT',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ roles })
    })
        .then(res => {
            if (!res.ok) throw new Error("Failed to update roles");
            return res.json();
        })
        .then(() => {
            alert("Roles updated successfully");
            closeUserRolesModal();
            loadUsers();
        })
        .catch(err => alert(err.message));
});

// ------------------- Initialize -------------------
document.addEventListener('DOMContentLoaded', () => {
    loadProducts();
    loadCategories();
    loadOrders();
    loadUsers();
});