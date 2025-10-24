package com.industry3.manufacturing.service;

import com.industry3.manufacturing.entity.Product;
import com.industry3.manufacturing.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public List<Product> getActiveProducts() {
        return productRepository.findActiveProducts();
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public Optional<Product> getProductByCode(String productCode) {
        return productRepository.findByProductCode(productCode);
    }
    
    public List<Product> searchProducts(String searchTerm) {
        return productRepository.searchActiveProducts(searchTerm);
    }
    
    public Product createProduct(Product product) {
        if (productRepository.existsByProductCode(product.getProductCode())) {
            throw new IllegalArgumentException("Product code already exists: " + product.getProductCode());
        }
        return productRepository.save(product);
    }
    
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
            
        // Check if product code is being changed and if it already exists
        if (!product.getProductCode().equals(productDetails.getProductCode()) && 
            productRepository.existsByProductCode(productDetails.getProductCode())) {
            throw new IllegalArgumentException("Product code already exists: " + productDetails.getProductCode());
        }
        
        product.setProductCode(productDetails.getProductCode());
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setUnitPrice(productDetails.getUnitPrice());
        product.setStatus(productDetails.getStatus());
        product.setMinimumStockLevel(productDetails.getMinimumStockLevel());
        product.setMaximumStockLevel(productDetails.getMaximumStockLevel());
        product.setReorderPoint(productDetails.getReorderPoint());
        product.setLeadTimeDays(productDetails.getLeadTimeDays());
        
        return productRepository.save(product);
    }
    
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        // Instead of hard delete, mark as discontinued
        product.setStatus(Product.ProductStatus.DISCONTINUED);
        productRepository.save(product);
    }
    
    public List<Product> getProductsByStatus(Product.ProductStatus status) {
        return productRepository.findByStatus(status);
    }
}
