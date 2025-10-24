package com.industry3.manufacturing.repository;

import com.industry3.manufacturing.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findByProductCode(String productCode);
    
    List<Product> findByStatus(Product.ProductStatus status);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE'")
    List<Product> findActiveProducts();
    
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.productCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Product> searchActiveProducts(String searchTerm);
    
    boolean existsByProductCode(String productCode);
}
