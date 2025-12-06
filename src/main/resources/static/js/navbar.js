document.addEventListener('DOMContentLoaded', () => {
    // ===== Search =====
    const searchForm = document.getElementById('searchForm');
    const searchInput = document.getElementById('searchInput');
    if (searchForm && searchInput) {
        searchForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const query = searchInput.value.trim();
            if (query) {
                localStorage.setItem('searchQuery', query);
                window.location.href = '/home';
            }
        });
    }

    // ===== Cart =====
    const cartBtn = document.getElementById('cartBtn');
    if (cartBtn) {
        cartBtn.addEventListener('click', () => {
            window.location.href = '/cart';
        });
    }

    // ===== User Dropdown =====
    const userBtn = document.getElementById('userBtn');
    const userDropdown = document.getElementById('userDropdown');
    if (userBtn && userDropdown) {
        userBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            userDropdown.style.display =
                userDropdown.style.display === 'block' ? 'none' : 'block';
        });

        // Close dropdown only if click is outside userBtn
        document.addEventListener('click', (e) => {
            if (!userBtn.contains(e.target)) {
                userDropdown.style.display = 'none';
            }
        });
    }


    // ===== Logout =====
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async (e) => {
            e.stopPropagation(); // prevent dropdown close handler interfering
            try {
                const res = await fetch('/auth/logout', {
                    method: 'POST',
                    credentials: 'include'
                });
                if (!res.ok) throw new Error('Logout failed');
                window.location.href = '/login';
            } catch (err) {
                alert('Logout failed. Try again.');
                console.error(err);
            }
        });
    }
});
