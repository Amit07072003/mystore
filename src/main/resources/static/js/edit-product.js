// Get product ID from URL
const pathSegments = window.location.pathname.split('/');
const productId = pathSegments[pathSegments.length - 1];

const form = document.getElementById('editProductForm');
const messageEl = document.getElementById('message');

// Load existing product data
async function loadProduct() {
    try {
        const res = await fetch('/api/products/' + productId);
        if (!res.ok) throw new Error('Failed to fetch product');
        const product = await res.json();

        document.getElementById('productId').value = product.id;
        document.getElementById('name').value = product.name;
        document.getElementById('description').value = product.description;
        document.getElementById('price').value = product.price;
        document.getElementById('stockQuantity').value = product.stockQuantity;
        document.getElementById('categoryId').value = product.categoryId;
        document.getElementById('imageUrl').value = product.imageUrl;
    } catch (err) {
        console.error(err);
        messageEl.style.color = 'red';
        messageEl.innerText = 'Failed to load product data.';
    }
}

loadProduct();

// Submit form and send PUT request
form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const productDTO = {
        name: document.getElementById('name').value,
        description: document.getElementById('description').value,
        price: parseFloat(document.getElementById('price').value),
        stockQuantity: parseInt(document.getElementById('stockQuantity').value),
        categoryId: parseInt(document.getElementById('categoryId').value),
        imageUrl: document.getElementById('imageUrl').value
    };

    try {
        const res = await fetch('/api/products/' + productId, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(productDTO)
        });

        if (res.ok) {
            messageEl.style.color = 'green';
            messageEl.innerText = 'Product updated successfully!';
        } else {
            const errorText = await res.text();
            messageEl.style.color = 'red';
            messageEl.innerText = 'Error: ' + errorText;
        }
    } catch (err) {
        console.error(err);
        messageEl.style.color = 'red';
        messageEl.innerText = 'Error updating product.';
    }
});
