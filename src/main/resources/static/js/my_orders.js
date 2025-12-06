const BASE_URL = window.location.origin;

const ordersContainer = document.getElementById("ordersContainer");
const emptyState = document.getElementById("emptyState");

// ===============================
// ✅ FETCH LOGGED-IN USER ORDERS
// JWT is sent automatically via HttpOnly cookie
// ===============================
async function fetchMyOrders() {
    try {
        const res = await fetch(`${BASE_URL}/user/orders/my`, {
            method: "GET",
            credentials: "include"   // ✅ REQUIRED for HttpOnly cookie
        });

        if (res.status === 401 || res.status === 403) {
            window.location.href = "/login";
            return;
        }

        if (!res.ok) throw new Error("Failed to load orders");

        const orders = await res.json();

        if (!orders || orders.length === 0) {
            emptyState.style.display = "block";
            return;
        }

        renderOrders(orders);

    } catch (err) {
        console.error(err);
        ordersContainer.innerHTML =
            "<p style='color:red;text-align:center;'>Failed to load orders</p>";
    }
}

// ===============================
// ✅ RENDER ORDERS
// ===============================
function renderOrders(orders) {
    ordersContainer.innerHTML = "";

    orders.forEach(order => {

        let itemsHtml = "";
        order.items.forEach(item => {
            itemsHtml += `
                <div style="display:flex;justify-content:space-between;padding:6px 0;border-bottom:1px dashed #ccc;">
                    <span>${item.productName} (x${item.quantity})</span>
                    <span>₹${item.price}</span>
                </div>
            `;
        });

        const div = document.createElement("div");
        div.style.cssText =
            "background:#fff;padding:16px;border-radius:8px;margin-bottom:16px;box-shadow:0 2px 6px rgba(0,0,0,0.1);";

        div.innerHTML = `
            <div style="display:flex;justify-content:space-between;">
                <div>
                    <strong>Order #${order.id}</strong><br>
                    <small>${formatDate(order.orderDate)}</small>
                </div>
                <span style="font-weight:bold;">${order.status}</span>
            </div>

            <div style="margin-top:10px;">
                ${itemsHtml}
            </div>

            <div style="display:flex;justify-content:space-between;margin-top:10px;">
                <small>Payment: ${order.razorpayOrderId}</small>
                <strong>Total: ₹${order.totalAmount}</strong>
            </div>
        `;

        ordersContainer.appendChild(div);
    });
}

// ===============================
// ✅ DATE FORMAT
// ===============================
function formatDate(dateStr) {
    return new Date(dateStr).toLocaleString("en-IN");
}

// ===============================
// ✅ INIT
// ===============================
fetchMyOrders();
