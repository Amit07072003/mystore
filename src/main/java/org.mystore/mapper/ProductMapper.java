package org.mystore.mapper;

import org.mystore.dtos.ProductDTO;
import org.mystore.models.Category;
import org.mystore.models.Product;
import org.mystore.models.User;


public class ProductMapper {

    public static ProductDTO toDto(Product product) {
        if (product == null) return null;

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .sellerId(product.getSeller() != null ? product.getSeller().getId() : null)

                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)

                .build();
    }

    public static Product toEntity(ProductDTO dto, Category category,    User seller) {
        if (dto == null) return null;

        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .imageUrl(dto.getImageUrl())
                .category(category)
                .seller(seller)
                .build();
    }
}
