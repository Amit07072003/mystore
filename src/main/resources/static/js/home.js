const BASE_URL = window.location.origin;

let isLoggedIn = false;
let allProducts = [];
let baseProducts = []; // products filtered by search
let currentProducts = []; // currently displayed products
let slides = [];
let currentSlide = 0;

// ===== Fetch User Info & Update Navbar =====
async function fetchUserInfo() {
    try {
        const res = await fetch(`${BASE_URL}/auth/validateToken`, {
            method: 'POST',
            credentials: 'include'
        });

        // If token invalid, treat as guest
        if (!res.ok) throw new Error('Invalid token');

        const email = await res.text();

        // If backend somehow returns "Invalid token" string or empty, treat as guest
        if (!email || email.toLowerCase() === 'invalid token') {
            throw new Error('Invalid token');
        }

        // Valid user
        document.getElementById('userEmail').innerText = email;
        document.getElementById('dropdownEmail').innerText = email;
        isLoggedIn = true;

        // Update cart count for logged-in user
        await updateCartCount();

    } catch {
        // Treat as guest
        document.cookie = "token=;path=/;max-age=0"; // remove stale/invalid token
        document.getElementById('userEmail').innerText = 'Guest';
        document.getElementById('dropdownEmail').innerText = 'Guest';
        document.getElementById('cartCount').innerText = '0';
        isLoggedIn = false;
    }
}


// ===== Fetch Cart Count =====
async function updateCartCount() {
    try {
        const res = await fetch(`${BASE_URL}/api/cart/count`, { credentials: 'include' });
        const data = await res.json();
        document.getElementById('cartCount').innerText = `${data.distinctCount}`;
    } catch {
        document.getElementById('cartCount').innerText = '0';
    }
}

// ===== Fetch Products =====
async function fetchProducts() {
    try {
        const res = await fetch(`${BASE_URL}/api/products`, { credentials: 'include' });
        allProducts = await res.json();

        // Apply search if query exists
        baseProducts = applySearch(allProducts);
        currentProducts = [...baseProducts]; // initially display search results

        displayProductsFiltered(currentProducts);
        loadHeroCarousel();

        localStorage.removeItem('searchQuery');
    } catch {
        document.getElementById('productsSection').innerHTML = '<p>Failed to load products.</p>';
    }
}

// ===== Display Products =====
function displayProductsFiltered(filtered) {
    const section = document.getElementById('productsSection');
    section.innerHTML = '';

    if (!filtered.length) {
        section.innerHTML = '<p>No products found.</p>';
        return;
    }

    filtered.forEach(p => {
        const div = document.createElement('div');
        div.className = 'product';
        div.innerHTML = `
            <img src="${p.imageUrl}" alt="${p.name}" onclick="goToProductDetail(${p.id})">
            <h3 onclick="goToProductDetail(${p.id})">${p.name}</h3>
            <span>₹${p.price}</span>
            ${isLoggedIn
            ? `<button onclick="addToCart('${p.id}', '${p.name}')">Add to Cart</button>`
            : `<button onclick="showLoginPopup()" class="disabled-btn">Add to Cart</button>`}
        `;
        section.appendChild(div);
    });
}

// ===== Go To Product Detail =====
function goToProductDetail(id) {
    window.location.href = `/product-detail?id=${id}`;
}

// ===== Add To Cart =====
async function addToCart(productId, name) {
    if (!isLoggedIn) return showLoginPopup();

    try {
        const res = await fetch(`${BASE_URL}/api/cart/add?productId=${productId}&quantity=1`, {
            method: 'POST',
            credentials: 'include'
        });
        if (!res.ok) throw new Error('Failed to add item');
        await updateCartCount();
        showToast(`${name} added to cart!`);
    } catch {
        showToast('Failed to add item', true);
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

// ===== Fetch Categories for Filter Panel =====
async function fetchCategories() {
    try {
        const res = await fetch(`${BASE_URL}/categories`, { credentials: 'include' });
        const categories = await res.json();

        const filterCat = document.getElementById('filterCategory');
        if (filterCat) {
            filterCat.innerHTML = '<option value="">All Categories</option>';
            categories.forEach(cat => {
                const option = document.createElement('option');
                option.value = cat.id;
                option.innerText = cat.name;
                filterCat.appendChild(option);
            });
        }

    } catch (e) {
        console.error('Failed to fetch categories', e);
    }
}

// ===== Apply Filters =====
function applyFilters() {
    let filtered = [...baseProducts]; // always start from search results

    // Category filter
    const catId = document.getElementById('filterCategory').value;
    if (catId) filtered = filtered.filter(p => p.categoryId == catId);

    // Price filter
    const min = parseFloat(document.getElementById('minPrice').value) || 0;
    const max = parseFloat(document.getElementById('maxPrice').value) || Infinity;
    filtered = filtered.filter(p => p.price >= min && p.price <= max);

    // Sort
    const sort = document.getElementById('sortBy').value;
    if (sort === 'priceAsc') filtered.sort((a,b) => a.price - b.price);
    else if (sort === 'priceDesc') filtered.sort((a,b) => b.price - a.price);
    else if (sort === 'newest') filtered.sort((a,b) => b.id - a.id);

    currentProducts = filtered;
    displayProductsFiltered(currentProducts);

    // ===== Close Filters Panel =====
    const filtersContent = document.querySelector('.filters-content');
    if (filtersContent) {
        filtersContent.classList.remove('active'); // hide content
    }
}




document.addEventListener("DOMContentLoaded", function () {
    const toggleBtn = document.getElementById("filtersToggle");
    const filtersContent = document.getElementById("filtersContent");

    if (toggleBtn && filtersContent) {
        toggleBtn.addEventListener("click", function () {
            toggleBtn.classList.toggle("active");
            filtersContent.classList.toggle("active");
        });
    }
});

// ===== Hero Carousel =====
function loadHeroCarousel() {
    const hero = document.getElementById('heroSection');
    hero.innerHTML = '';
    const banners = [
        { imageUrl: 'https://rukminim2.flixcart.com/fk-p-flap/3240/540/image/d1743320c28e67ba.jpg?q=60' },
        { imageUrl: 'https://rukminim2.flixcart.com/fk-p-flap/3240/540/image/e7dcd564dcff5707.jpg?q=60' }
    ];
    banners.forEach(b => {
        const img = document.createElement('img');
        img.src = b.imageUrl;
        img.className = 'carousel-slide';
        hero.appendChild(img);
    });

    slides = document.querySelectorAll('.carousel-slide');
    if (slides.length) slides[0].classList.add('active');

    setInterval(() => {
        if (!slides.length) return;
        slides[currentSlide].classList.remove('active');
        currentSlide = (currentSlide + 1) % slides.length;
        slides[currentSlide].classList.add('active');
    }, 5000);
}

// ===== Apply Search =====
function applySearch(products) {
    const query = localStorage.getItem('searchQuery')?.toLowerCase();
    if (!query) return products;
    return products.filter(p => p.name.toLowerCase().includes(query));
}

// ===== Toast =====
function showToast(msg, isError = false) {
    const toast = document.createElement('div');
    toast.innerText = msg;
    toast.className = 'toast-message';
    toast.style.background = isError ? '#e74c3c' : '#2ecc71';
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 2000);
}

// ===== Event Listeners =====
document.getElementById('applyFilters')?.addEventListener('click', applyFilters);

// ===== Init =====
(async function init() {
    await fetchUserInfo();
    await fetchCategories();
    await fetchProducts();
})();
