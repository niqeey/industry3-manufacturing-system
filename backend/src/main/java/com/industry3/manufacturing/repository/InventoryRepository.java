package com.industry3.manufacturing.repository;

import com.industry3.manufacturing.entity.Inventory;
import com.industry3.manufacturing.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    Optional<Inventory> findByProduct(Product product);
    
    List<Inventory> findByLocation(String location);
    
    @Query("SELECT i FROM Inventory i WHERE i.currentStock <= i.product.reorderPoint")
    List<Inventory> findLowStockItems();
    
    @Query("SELECT i FROM Inventory i WHERE i.availableStock < :threshold")
    List<Inventory> findItemsWithAvailableStockBelow(@Param("threshold") Integer threshold);
    
    @Query("SELECT i FROM Inventory i WHERE i.currentStock = 0")
    List<Inventory> findOutOfStockItems();
    
    @Query("SELECT i FROM Inventory i JOIN i.product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.productCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Inventory> searchInventory(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT SUM(i.currentStock * i.product.unitPrice) FROM Inventory i")
    Double getTotalInventoryValue();
    
    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId")
    Optional<Inventory> findByProductId(@Param("productId") Long productId);
}
