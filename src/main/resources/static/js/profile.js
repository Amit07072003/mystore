document.addEventListener("DOMContentLoaded", fetchProfile);

async function fetchProfile() {
    try {
        const res = await fetch("/profile/me", {
            method: "GET",
            credentials: "include" // ✅ sends HttpOnly JWT cookie
        });

        if (!res.ok) {
            throw new Error("Unauthorized");
        }

        const user = await res.json();

        document.getElementById("name").textContent = user.name;
        document.getElementById("email").textContent = user.email;
        document.getElementById("roles").textContent = user.roles.join(", ");

    } catch (err) {
        console.error(err);
        alert("Session expired. Please login again.");
        window.location.href = "/login";
    }
}

// ✅ Logout helper
function logout() {
    fetch("/auth/logout", {
        method: "POST",
        credentials: "include"
    }).then(() => {
        window.location.href = "/login";
    });
}
