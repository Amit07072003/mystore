package org.mystore.dtos;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
@Schema(
        name = "Product",
        description = "Represents a product in the store"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Long price;
    private int stockQuantity;
    private String imageUrl;
    private Long categoryId;   // Instead of whole Category object
    private Long sellerId;  // Optional: include seller reference
}