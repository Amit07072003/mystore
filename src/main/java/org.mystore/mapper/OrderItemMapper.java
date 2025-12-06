package org.mystore.mapper;

import org.mystore.dtos.OrderItemDTO;
import org.mystore.models.OrderItem;
import org.mystore.models.Product;
import org.mystore.models.Order;

public class OrderItemMapper {

    // Entity -> DTO
    public static OrderItemDTO toDTO(OrderItem item) {
        if (item == null) return null;

        return OrderItemDTO.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getProduct().getPrice())
                .build();
    }

    // DTO -> Entity
    public static OrderItem toEntity(OrderItemDTO dto, Product product, Order order) {
        if (dto == null) return null;

        return OrderItem.builder()
                .id(dto.getId())
                .product(product)  // Assign existing product entity
                .order(order)      // Assign parent order
                .quantity(dto.getQuantity())
                .build();
    }
}
