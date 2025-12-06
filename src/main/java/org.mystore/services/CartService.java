package org.mystore.services;

import lombok.RequiredArgsConstructor;
import org.mystore.dtos.CartDTO;
import org.mystore.dtos.UserDTO;
import org.mystore.mapper.CartMapper;
import org.mystore.models.Cart;
import org.mystore.models.CartItem;
import org.mystore.models.Product;
import org.mystore.models.User;
import org.mystore.repositories.CartItemRepository;
import org.mystore.repositories.CartRepository;
import org.mystore.repositories.ProductRepository;
import org.mystore.repositories.UserRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepo userRepository;

    // Get or create Cart entity
    public Cart getOrCreateCartEntity(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart = cartRepository.save(cart);
        }
        return cart;
    }

    // Add item to cart
    public CartDTO addItemToCart(UserDTO userDTO, Long productId, int quantity) {
        Cart cart = getOrCreateCartEntity(userDTO.getId());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQty = item.getQuantity() + quantity;
            if (newQty > product.getStockQuantity()) {
                newQty = product.getStockQuantity();
            }
            item.setQuantity(newQty);
            cartItemRepository.save(item);
        } else {
            int qtyToAdd = Math.min(quantity, product.getStockQuantity());
            CartItem newItem = CartItem.builder()
                    .product(product)
                    .quantity(qtyToAdd)
                    .cart(cart)
                    .build();
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        return CartMapper.toDTO(cartRepository.findById(cart.getId()).get());
    }

    // Remove item from cart
    public CartDTO removeItemFromCart(UserDTO userDTO, Long productId) {
        Cart cart = getOrCreateCartEntity(userDTO.getId());
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cartRepository.save(cart);
        return CartMapper.toDTO(cart);
    }

    // Get Cart DTO
    public CartDTO getCart(UserDTO userDTO) {
        Cart cart = getOrCreateCartEntity(userDTO.getId());
        return CartMapper.toDTO(cart);
    }

    // Get cart count
    public int getCartCount(UserDTO userDTO) {
        Cart cart = getOrCreateCartEntity(userDTO.getId());
        return cart.getItems().size();
    }

    // Get total quantity
    public int getCartTotalQuantity(UserDTO userDTO) {
        Cart cart = getOrCreateCartEntity(userDTO.getId());
        return cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();
    }

    // Update item quantity (+ or -)
    public CartDTO updateItemQuantity(UserDTO userDTO, Long productId, int delta) {
        Cart cart = getOrCreateCartEntity(userDTO.getId());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        for (int i = 0; i < cart.getItems().size(); i++) {
            CartItem item = cart.getItems().get(i);
            if (item.getProduct().getId().equals(productId)) {
                int newQty = item.getQuantity() + delta;

                // Prevent going below 0
                if (newQty <= 0) {
                    cart.getItems().remove(i); // remove item completely
                    cartItemRepository.delete(item); // delete from DB
                    i--; // adjust index after removal
                }
                // Prevent exceeding stock
                else if (newQty > product.getStockQuantity()) {
                    item.setQuantity(product.getStockQuantity()); // set max stock
                    cartItemRepository.save(item);
                }
                else {
                    item.setQuantity(newQty); // normal increment/decrement
                    cartItemRepository.save(item);
                }
            }
        }

        return CartMapper.toDTO(cartRepository.findById(cart.getId()).get());
    }
}
