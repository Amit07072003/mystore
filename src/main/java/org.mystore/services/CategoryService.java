package org.mystore.services;

import lombok.AllArgsConstructor;
import org.mystore.dtos.CategoryDTO;
import org.mystore.dtos.ProductDTO;
import org.mystore.exception.CategoryAlreadyExistsException;
import org.mystore.mapper.CategoryMapper;
import org.mystore.mapper.ProductMapper;
import org.mystore.models.Category;
import org.mystore.models.Product;
import org.mystore.repositories.CategoryRepository;
import org.mystore.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryService {
//getCategory
    //createCategory
    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    public CategoryDTO createCategory(CategoryDTO dto) {
      Optional<Category> optionalCategory=  categoryRepository. findByName(dto.getName());
      if(optionalCategory.isPresent()){
        throw  new CategoryAlreadyExistsException("Category: " + dto.getName() + " already exists");
      }
       Category category= CategoryMapper.toEntity(dto);
       if (category!=null){
         category=  categoryRepository.save(category);
       }
          return CategoryMapper.todto(category);
    }
    //get all categores
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        for (Category category : categories) {
            CategoryDTO dto = CategoryMapper.todto(category); // ✅ maps products too
            categoryDTOS.add(dto);
        }
        return categoryDTOS;
    }

    //updateCategory
    public CategoryDTO updateCategory(Long id,@RequestBody CategoryDTO dto) {
        Category category=categoryRepository.findById(id).orElseThrow(()->new RuntimeException("Category not found"));
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());
        categoryRepository.save(category);
        return CategoryMapper.todto(category);

    }


    //getCategory by id
    public CategoryDTO getCategotyById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(()->new RuntimeException("Category not found"));
        return CategoryMapper.todto(category);
    }
    //delete Category
    public String DeleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
        return "Category " +id+ "has been deleted successfully";
    }
}
