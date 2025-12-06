const BASE_URL = window.location.origin; // dynamic origin
const cartItemsContainer = document.getElementById('cartItems');
const cartCountEl = document.getElementById('cartCount');
const subtotalEl = document.getElementById('subtotal');
const taxEl = document.getElementById('tax');
const totalEl = document.getElementById('total');
const userEmailEl = document.getElementById('userEmail');

// ===== Fetch User Email =====
async function fetchUserEmail() {
    try {
        const res = await fetch(`${BASE_URL}/auth/validateToken`, {
            method: 'POST',
            credentials: 'include'
        });

        if (!res.ok) throw new Error('Invalid token');

        const email = await res.text();

        // Treat invalid token as guest
        if (!email || email.toLowerCase() === 'invalid token') throw new Error('Invalid token');

        userEmailEl.innerText = email;
        document.getElementById('dropdownEmail').innerText = email;
        isLoggedIn = true;

    } catch {
        document.cookie = "token=;path=/;max-age=0"; // delete invalid token
        userEmailEl.innerText = 'Guest';
        document.getElementById('dropdownEmail').innerText = 'Guest';
        cartCountEl.innerText = '0';
        isLoggedIn = false;
    }
}

// ===== Fetch Cart =====
async function fetchCart() {
    try {
        const res = await fetch(`${BASE_URL}/api/cart`, { credentials: 'include' });
        if (!res.ok) throw new Error('Failed to fetch cart');
        const cart = await res.json();
        renderCart(cart.items);
    } catch (err) {
        console.error(err);
        cartItemsContainer.innerHTML = '<p>Failed to load cart.</p>';
    }
}

// ===== Render Cart =====
function renderCart(items) {
    cartItemsContainer.innerHTML = '';
    let subtotal = 0;

    items.forEach(item => {
        subtotal += item.price * item.quantity;

        const div = document.createElement('div');
        div.className = 'cart-item';
        div.innerHTML = `
            <img src="${item.imageUrl || 'https://via.placeholder.com/80'}" alt="${item.productName}">
            <div class="cart-item-details">
                <h3>${item.productName}</h3>
                <p>Price: ₹${item.price.toFixed(2)}</p>
            </div>
            <div class="cart-item-actions">
                <div class="qty-controls">
                    <button class="decrease" data-id="${item.productId}">−</button>
                    <span class="qty">${item.quantity}</span>
                    <button class="increase" data-id="${item.productId}">+</button>
                    <button class="remove" data-id="${item.productId}">Remove</button>
                </div>
            </div>
        `;
        cartItemsContainer.appendChild(div);

        const userDTO = { email: userEmailEl.innerText };

        div.querySelector('.increase').addEventListener('click', async () => {
            if (!isLoggedIn) return showLoginPopup();
            await updateQuantity(item.productId, 'increase', userDTO);
        });
        div.querySelector('.decrease').addEventListener('click', async () => {
            if (!isLoggedIn) return showLoginPopup();
            await updateQuantity(item.productId, 'decrease', userDTO);
        });
        div.querySelector('.remove').addEventListener('click', async () => {
            if (!isLoggedIn) return showLoginPopup();
            await removeItem(item.productId);
        });
    });

    cartCountEl.innerText = `🛒 ${items.length}`;
    const tax = +(subtotal * 0.08).toFixed(2);
    subtotalEl.innerText = `₹${subtotal.toFixed(2)}`;
    taxEl.innerText = `₹${tax.toFixed(2)}`;
    totalEl.innerText = `₹${(subtotal + tax).toFixed(2)}`;
}

// ===== Update Quantity =====
async function updateQuantity(productId, action, userDTO) {
    try {
        const res = await fetch(`${BASE_URL}/api/cart/${action}?productId=${productId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userDTO),
            credentials: 'include'
        });
        if (!res.ok) throw new Error('Failed to update quantity');
        const cart = await res.json();
        renderCart(cart.items);
    } catch (err) {
        console.error(err);
        alert('Failed to update quantity');
    }
}

// ===== Remove Item =====
async function removeItem(productId) {
    try {
        await fetch(`${BASE_URL}/api/cart/remove?productId=${productId}`, {
            method: 'DELETE',
            credentials: 'include'
        });
        fetchCart();
    } catch (err) {
        console.error(err);
    }
}

// ===== Login Popup =====
function showLoginPopup() {
    if (document.getElementById('loginPopup')) return;
    const popup = document.createElement('div');
    popup.id = 'loginPopup';
    popup.className = 'login-popup-overlay';
    popup.innerHTML = `
        <div class="login-popup-content">
            <p>Please login first.</p>
            <button onclick="window.location.href='/login'">Login</button>
            <button onclick="document.body.removeChild(document.getElementById('loginPopup'))">Cancel</button>
        </div>
    `;
    document.body.appendChild(popup);
}

// ===== Checkout (Razorpay Integration) =====
document.getElementById("checkoutBtn").addEventListener("click", async () => {
    if (!isLoggedIn) return showLoginPopup();
    try {
        const totalInRupees = parseFloat(totalEl.innerText.replace('₹',''));
        const backendOrderId = Date.now();
        const payload = { amount: Math.round(totalInRupees), orderId: backendOrderId, phone: "9876543210", email: userEmailEl.innerText };
        const res = await fetch(`${BASE_URL}/api/payment/checkout`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
            credentials: "include"
        });
        if(!res.ok) throw new Error("Payment initiation failed");
        const paymentData = await res.json();
        const paymentLink = JSON.parse(paymentData.paymentResponse).short_url;
        window.open(paymentLink, "_blank");
    } catch(err) {
        console.error(err);
        alert("Checkout failed, please try again.");
    }
});

// ===== User Dropdown =====
const userBtn = document.getElementById('userBtn');
const dropdown = document.getElementById('userDropdown');
userBtn.addEventListener('click', () => {
    dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';
});
window.addEventListener('click', e => {
    if (!userBtn.contains(e.target)) dropdown.style.display = 'none';
});

// ===== Logout =====
document.getElementById('logoutBtn').addEventListener('click', async () => {
    await fetch(`${BASE_URL}/auth/logout`, { method: 'POST', credentials: 'include' });
    window.location.href = "/login";
});

// ===== Init =====
let isLoggedIn = false;
fetchUserEmail();
fetchCart();
