package org.mystore.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        name = "Category",
        description = "Represents a product category in the store"
)
public class CategoryDTO {

    private Long id;

    private String name;

    private String description;

    @Schema(description = "URL or path to the category image")
    private String imageUrl;

    @Builder.Default
    private List<ProductDTO> products = new ArrayList<>();
}
