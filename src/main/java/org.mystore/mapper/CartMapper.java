package org.mystore.mapper;

import org.mystore.dtos.CartDTO;
import org.mystore.dtos.CartItemDTO;
import org.mystore.models.Cart;
import org.mystore.models.CartItem;

import java.util.List;
import java.util.stream.Collectors;

public class CartMapper {

    // Convert Entity → DTO
    public static CartDTO toDTO(Cart cart) {
        if (cart == null) return null;

        List<CartItemDTO> items = cart.getItems().stream()
                .map(CartMapper::toCartItemDTO)
                .collect(Collectors.toList());

        return CartDTO.builder()
                .id(cart.getId())
                .items(items)
                .build();
    }

    // Convert DTO → Entity
    public static Cart toEntity(CartDTO cartDTO) {
        if (cartDTO == null) return null;

        Cart cart = new Cart();
        cart.setId(cartDTO.getId());
        if (cartDTO.getItems() != null) {
            List<CartItem> items = cartDTO.getItems().stream()
                    .map(CartMapper::toCartItemEntity)
                    .collect(Collectors.toList());
            cart.setItems(items);
        }
        return cart;
    }

    // Convert CartItem Entity → DTO
    private static CartItemDTO toCartItemDTO(CartItem item) {
        if (item == null) return null;

        return CartItemDTO.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .price(item.getProduct().getPrice())
                .quantity(item.getQuantity())
                .imageUrl(item.getProduct().getImageUrl())
                .build();
    }

    // Convert CartItem DTO → Entity
    private static CartItem toCartItemEntity(CartItemDTO dto) {
        if (dto == null) return null;

        CartItem item = new CartItem();
        item.setId(dto.getId());
        item.setQuantity(dto.getQuantity());
        // ⚠️ Product reference should be set in service layer after fetching from DB
        return item;
    }
}
