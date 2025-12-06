package org.mystore.repositories;

import org.mystore.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Search products by name containing the query (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);
    Page<Product> findByNameEquals(String query, Pageable pageable);

}
