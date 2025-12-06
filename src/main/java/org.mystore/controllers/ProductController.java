package org.mystore.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.mystore.dtos.ProductDTO;
import org.mystore.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
@Tag(name = "Product APIs", description = "Operations related to products")
public class ProductController {

    private final ProductService productService;

    // ✅ ADMIN and SELLER can create products

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO dto) {
        System.out.println("sellerid from product controller: "+ dto.getSellerId());
        return new ResponseEntity<>(productService.createProduct(dto), HttpStatus.CREATED);
    }

    // ✅ All authenticated users (ADMIN, SELLER, USER) can view products

    @GetMapping

    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    // ✅ All authenticated users can view product details

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
    }

    // ✅ ADMIN and SELLER can update their products

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        return new ResponseEntity<>(productService.updateProduct(id, dto), HttpStatus.CREATED);
    }

    // ✅ ADMIN and SELLER can delete products

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public String deleteProductById(@PathVariable Long id) {
        System.out.println("Delete product by id " + id);
        return productService.deleteProductById(id);
    }

}
