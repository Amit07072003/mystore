package org.mystore.controllers;

import lombok.RequiredArgsConstructor;
import org.mystore.dtos.CartDTO;
import org.mystore.dtos.UserDTO;
import org.mystore.services.AuthServiceImpl;
import org.mystore.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final AuthServiceImpl authService;

    // ===== Add Item =====
    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(@CookieValue(value = "token", required = false) String token,
                                             @RequestParam Long productId,
                                             @RequestParam(defaultValue = "1") int quantity) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(401).build(); // BLOCK UNAUTHORIZED
        }

        UserDTO userDTO = authService.getUserFromToken(token);
        if (userDTO == null) return ResponseEntity.status(401).build();

        CartDTO cartDTO = cartService.addItemToCart(userDTO, productId, quantity);
        return ResponseEntity.ok(cartDTO);
    }

    // ===== Remove Item =====
    @DeleteMapping("/remove")
    public ResponseEntity<CartDTO> removeFromCart(@CookieValue(value = "token", required = false) String token,
                                                  @RequestParam Long productId) {
        if (token == null || token.isEmpty()) return ResponseEntity.status(401).build();

        UserDTO userDTO = authService.getUserFromToken(token);
        if (userDTO == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(cartService.removeItemFromCart(userDTO, productId));
    }

    // ===== Get Cart =====
    @GetMapping
    public ResponseEntity<CartDTO> getCart(@CookieValue(value = "token", required = false) String token) {
        if (token == null || token.isEmpty()) return ResponseEntity.status(401).build();

        UserDTO userDTO = authService.getUserFromToken(token);
        if (userDTO == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(cartService.getCart(userDTO));
    }

    // ===== Get Cart Count =====
    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getCartCount(@CookieValue(value = "token", required = false) String token) {
        if (token == null || token.isEmpty())
            return ResponseEntity.ok(Map.of("distinctCount", 0, "totalQuantity", 0));

        UserDTO userDTO = authService.getUserFromToken(token);
        if (userDTO == null) return ResponseEntity.ok(Map.of("distinctCount", 0, "totalQuantity", 0));

        Map<String, Integer> response = new HashMap<>();
        response.put("distinctCount", cartService.getCartCount(userDTO));
        response.put("totalQuantity", cartService.getCartTotalQuantity(userDTO));
        return ResponseEntity.ok(response);
    }

    // ===== Increase / Decrease Quantity =====
    @PostMapping("/increase")
    public ResponseEntity<CartDTO> increaseQuantity(@CookieValue(value = "token", required = false) String token,
                                                    @RequestParam Long productId) {
        if (token == null || token.isEmpty()) return ResponseEntity.status(401).build();
        UserDTO userDTO = authService.getUserFromToken(token);
        if (userDTO == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(cartService.updateItemQuantity(userDTO, productId, 1));
    }

    @PostMapping("/decrease")
    public ResponseEntity<CartDTO> decreaseQuantity(@CookieValue(value = "token", required = false) String token,
                                                    @RequestParam Long productId) {
        if (token == null || token.isEmpty()) return ResponseEntity.status(401).build();
        UserDTO userDTO = authService.getUserFromToken(token);
        if (userDTO == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(cartService.updateItemQuantity(userDTO, productId, -1));
    }
}
