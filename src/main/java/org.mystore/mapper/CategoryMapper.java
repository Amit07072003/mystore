package org.mystore.mapper;

import org.mystore.dtos.CategoryDTO;
import org.mystore.dtos.ProductDTO;
import org.mystore.models.Category;
import org.mystore.models.Product;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CategoryMapper {
    public  static CategoryDTO todto(Category category){
        if(category == null) return null;
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .products(category.getProducts()!=null
                ? category.getProducts().stream()
                        .map(CategoryMapper::mapProductToDto)
                        .collect(Collectors.toList()):new ArrayList<>()
                )
                .build();


    }


    public  static Category toEntity(CategoryDTO dto){
        if(dto == null) return null;
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .build();


    }
    private static ProductDTO mapProductToDto(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategory().getId())
                .build();
    }
}
