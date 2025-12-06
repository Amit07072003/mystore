// ===== Get product ID from URL =====
const params = new URLSearchParams(window.location.search);
const productId = params.get('id');

let isLoggedIn = false;

// ===== Fetch User Info =====
async function fetchUserInfo() {
    try {
        const res = await fetch('http://localhost:8080/auth/validateToken', {
            method: 'POST',
            credentials: 'include'
        });
        if (!res.ok) throw new Error('Unauthorized');

        const email = await res.text();
        document.getElementById('userEmail').innerText = email;
        document.getElementById('dropdownEmail').innerText = email;
        isLoggedIn = true;

    } catch (e) {
        document.getElementById('userEmail').innerText = 'Guest';
        document.getElementById('dropdownEmail').innerText = 'Guest';
        isLoggedIn = false;
    }
}

// ===== Fetch Cart Count =====
async function updateCartCount() {
    try {
        const res = await fetch('http://localhost:8080/api/cart/count', {
            method: 'GET',
            credentials: 'include'
        });
        if (!res.ok) throw new Error('Failed to fetch cart count');

        const data = await res.json(); // { distinctCount: X, totalQuantity: Y }
        document.getElementById('cartCount').innerText = `${data.distinctCount}`;
    } catch (e) {
        document.getElementById('cartCount').innerText = '0';
        console.error(e);
    }
}

// ===== Fetch Product Data =====
async function fetchProduct() {
    try {
        const res = await fetch(`http://localhost:8080/api/products/${productId}`, { credentials: 'include' });
        if (!res.ok) throw new Error('Product not found');

        const product = await res.json();

        document.getElementById('productName').innerText = product.name;
        document.getElementById('productImage').src = product.imageUrl;
        document.getElementById('productPrice').innerText = `$${product.price}`;
        document.getElementById('productDescription').innerText = product.description;

    } catch (e) {
        console.error(e);
        document.getElementById('productName').innerText = 'Product not found';
        document.getElementById('productImage').src = 'https://via.placeholder.com/300';
        document.getElementById('productPrice').innerText = '$0.00';
        document.getElementById('productDescription').innerText = '';
    }
}

// ===== Add to Cart =====
document.getElementById('addToCartBtn').addEventListener('click', async () => {
    if (!isLoggedIn) {
        alert('Please login first to add items to cart!');
        window.location.href = '/login';
        return;
    }

    try {
        const res = await fetch(`http://localhost:8080/api/cart/add?productId=${productId}&quantity=1`, {
            method: 'POST',
            credentials: 'include'
        });
        if (!res.ok) throw new Error(await res.text());

        await updateCartCount(); // update cart count dynamically
        alert('Product added to cart!');
    } catch (e) {
        alert('Failed to add product to cart: ' + e.message);
    }
});

// ===== Logout =====
document.getElementById('logoutBtn')?.addEventListener('click', async () => {
    await fetch('http://localhost:8080/auth/logout', { method: 'POST', credentials: 'include' });
    isLoggedIn = false;
    document.getElementById('userEmail').innerText = 'Guest';
    document.getElementById('dropdownEmail').innerText = 'Guest';
    document.getElementById('cartCount').innerText = '0';
    window.location.href = "/home";
});

// ===== User Dropdown =====
const userBtn = document.getElementById('userBtn');
const dropdown = document.getElementById('userDropdown');
userBtn?.addEventListener('click', () => {
    dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';
});
window.addEventListener('click', e => {
    if (userBtn && !userBtn.contains(e.target)) dropdown.style.display = 'none';
});

// ===== Init =====
(async function init() {
    await fetchUserInfo();
    await updateCartCount(); // <- update cart count on page load
    await fetchProduct();
})();
