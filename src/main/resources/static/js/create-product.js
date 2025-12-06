// Grab the form and message container
const form = document.getElementById('productForm');
const message = document.getElementById('message');

form.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Get form values safely
    const name = document.getElementById('name').value.trim();
    const description = document.getElementById('description').value.trim();
    const price = parseFloat(document.getElementById('price').value);
    const stockQuantity = parseInt(document.getElementById('stockQuantity').value);
    const categoryId = parseInt(document.getElementById('categoryId').value);
    const imageUrl = document.getElementById('imageUrl').value.trim();

    // Debug logs
    console.log("Form Values:");
    console.log({ name, description, price, stockQuantity, categoryId, imageUrl });

    // Build payload exactly matching ProductDTO without sellerId
    const productDTO = {
        name,
        description,
        price,
        stockQuantity,
        categoryId,
        imageUrl
    };

    console.log("Payload sent to backend:", JSON.stringify(productDTO));

    try {
        const response = await fetch('/api/products', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(productDTO),
            credentials: 'include' // for cookies/session if required
        });

        if (response.ok) {
            message.textContent = 'Product created successfully!';
            message.className = 'message success';
            form.reset();
        } else {
            const error = await response.json();
            message.textContent = error.message || 'Failed to create product.';
            message.className = 'message error';
        }
    } catch (err) {
        message.textContent = 'Error: ' + err.message;
        message.className = 'message error';
        console.error("Fetch Error:", err);
    }
});
