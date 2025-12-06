document.addEventListener("DOMContentLoaded", () => {
    console.log("Products page loaded ✅");

    const deleteButtons = document.querySelectorAll(".btn-delete");

    deleteButtons.forEach(btn => {
        btn.addEventListener("click", async () => {
            const card = btn.closest(".product-card");
            const productId = card.getAttribute("data-id");

            if (confirm("Are you sure you want to delete this product?")) {
                try {
                    const res = await fetch(`http://localhost:8080/products/${productId}`, {
                        method: "DELETE"
                    });

                    if (res.ok) {
                        alert("✅ Product deleted successfully!");
                        card.remove(); // Remove card from DOM
                    } else {
                        const errText = await res.text();
                        alert("❌ Failed to delete: " + errText);
                    }
                } catch (err) {
                    console.error(err);
                    alert("❌ Error deleting product.");
                }
            }
        });
    });
});
