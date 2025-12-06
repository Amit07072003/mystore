package org.mystore.dtos;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
    private Long id;
    private List<CartItemDTO> items;

    // Return total amount in paise as long
    public long getTotalAmount() {
        if (items == null || items.isEmpty()) {
            return 0L;
        }

        return items.stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}
