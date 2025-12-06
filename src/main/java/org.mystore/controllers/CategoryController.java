package org.mystore.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.mystore.dtos.CategoryDTO;
import org.mystore.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/categories")
@AllArgsConstructor
@Tag(name = "Category APIs", description = "Operations related to product categories")
public class CategoryController {

    private final CategoryService categoryService;

    // ✅ Only ADMIN can create new categories
//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> createCategory(@ModelAttribute CategoryDTO dto) {
        CategoryDTO categoryDTO = categoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryDTO);
    }


//    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public String createCategory(@ModelAttribute CategoryDTO dto) {
//        categoryService.createCategory(dto);
//        return "redirect:/admin/dashboard"; // or wherever you want
//    }

    // ✅ All authenticated users can view categories
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // ✅ All authenticated users can view a category by ID
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategotyById(id));
    }

    // ✅ Only ADMIN can update a category
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, dto));
    }

    // ✅ Only ADMIN can delete a category
//    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable Long id) {
        categoryService.DeleteCategoryById(id);
        return ResponseEntity.noContent().build();
    }
}
