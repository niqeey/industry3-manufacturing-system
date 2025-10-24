package com.industry3.manufacturing.controller;

import com.industry3.manufacturing.entity.Product;
import com.industry3.manufacturing.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
@Tag(name = "Product Management", description = "APIs for managing products in the manufacturing system")
public class ProductController {
    
    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve a list of all products in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all products",
                content = @Content(schema = @Schema(implementation = Product.class)))
    })
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active products", description = "Retrieve a list of all active products")
    public ResponseEntity<List<Product>> getActiveProducts() {
        List<Product> products = productService.getActiveProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found",
                content = @Content(schema = @Schema(implementation = Product.class))),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id) {
        return productService.getProductById(id)
            .map(product -> ResponseEntity.ok(product))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/code/{productCode}")
    @Operation(summary = "Get product by code", description = "Retrieve a specific product by its product code")
    public ResponseEntity<Product> getProductByCode(
            @Parameter(description = "Product Code", required = true) @PathVariable String productCode) {
        return productService.getProductByCode(productCode)
            .map(product -> ResponseEntity.ok(product))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search for products by name, code, or description")
    public ResponseEntity<List<Product>> searchProducts(
            @Parameter(description = "Search term", required = true) @RequestParam String term) {
        List<Product> products = productService.searchProducts(term);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get products by status", description = "Retrieve products with a specific status")
    public ResponseEntity<List<Product>> getProductsByStatus(
            @Parameter(description = "Product status") @PathVariable Product.ProductStatus status) {
        List<Product> products = productService.getProductsByStatus(status);
        return ResponseEntity.ok(products);
    }
    
    @PostMapping
    @Operation(summary = "Create product", description = "Create a new product in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully",
                content = @Content(schema = @Schema(implementation = Product.class))),
        @ApiResponse(responseCode = "400", description = "Invalid product data or product code already exists")
    })
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully",
                content = @Content(schema = @Schema(implementation = Product.class))),
        @ApiResponse(responseCode = "400", description = "Invalid product data"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id, 
            @Valid @RequestBody Product productDetails) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDetails);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Mark a product as discontinued (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product marked as discontinued"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
