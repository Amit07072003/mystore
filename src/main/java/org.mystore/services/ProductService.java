package org.mystore.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.mystore.dtos.CategoryDTO;
import org.mystore.dtos.ProductDTO;
import org.mystore.mapper.CategoryMapper;
import org.mystore.mapper.ProductMapper;
import org.mystore.models.Category;
import org.mystore.models.Product;
import org.mystore.models.User;
import org.mystore.repositories.CartItemRepository;
import org.mystore.repositories.CategoryRepository;
import org.mystore.repositories.ProductRepository;
import org.mystore.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {
private ProductRepository productRepository;
private CategoryRepository categoryRepository;
private CartItemRepository cartItemRepository;
private UserRepo userRepository;
@Autowired
private RedisTemplate<String, Object> redisTemplate;

public ProductDTO createProduct(ProductDTO dto){


  Category category= categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(()-> new RuntimeException("Category Not Found"));
  User seller=userRepository.findById(dto.getSellerId())
          .orElseThrow(()->new RuntimeException("Seller Not Found"));
    Product product= ProductMapper.toEntity(dto,category,seller);
    product.setCategory(category);
    product.setSeller(seller);
    productRepository.save(product);

    return ProductMapper.toDto(product);

}

//if found in redis
    // then return
    // else
    //call db and
    //cache it and
    // return it

//getAllProducts
public List<ProductDTO> getAllProducts(){
//    List<Product> products= productRepository.findAll();
//    List<ProductDTO> productDTOs=new ArrayList<>();
//    for (Product product : products) {
//        ProductDTO dto = ProductMapper.toDto(product); // ✅ maps products too
//        productDTOs.add(dto);
//    }
//    return   productDTOs;

    String key = "products:all";

    // 1. Check Redis
    Object cachedData = redisTemplate.opsForValue().get(key);
    if (cachedData != null) {
        System.out.println("Fetched all products from Redis");
        return (List<ProductDTO>) cachedData;
    }

    // 2. Fetch from DB
    List<Product> products = productRepository.findAll();
    List<ProductDTO> productDTOs = new ArrayList<>();
    for (Product product : products) {
        productDTOs.add(ProductMapper.toDto(product));
    }

    // 3. Cache result in Redis for 10 minutes
    redisTemplate.opsForValue().set(key, productDTOs, Duration.ofMinutes(10));
    System.out.println("Fetched all products from Database");

    return productDTOs;
}

//getProductsById
    public ProductDTO getProductById(Long id){
    Product product = productRepository.findById(id).orElseThrow(()->new RuntimeException("Product Not Found"));
    return ProductMapper.toDto(product);

    }
  public ProductDTO updateProduct(long id, @RequestBody ProductDTO dto){
      Product product = productRepository.findById(id).orElseThrow(()->new RuntimeException("Product Not Found"));
      Category category = categoryRepository.findById(id).orElseThrow(()->new RuntimeException("Category not found"));

      product.setCategory(category);
      product.setName(dto.getName());
      product.setDescription(dto.getDescription());
      product.setPrice(dto.getPrice());
      product.setImageUrl(dto.getImageUrl());
      product.setStockQuantity(dto.getStockQuantity());


      productRepository.save(product);
      return ProductMapper.toDto(product);
  }

    @Transactional
  public String deleteProductById(Long id){
     cartItemRepository.deleteAllByProductId(id);
    productRepository.deleteById(id);
    return "Product"+ id+ "Deleted Successfully";
  }


    // Method to search products by name
    public List<Product> searchByName(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }
}
