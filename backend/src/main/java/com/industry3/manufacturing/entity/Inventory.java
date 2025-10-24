package com.industry3.manufacturing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    
    @NotNull
    @Min(0)
    @Column(name = "current_stock")
    private Integer currentStock;
    
    @Column(name = "reserved_stock")
    private Integer reservedStock = 0;
    
    @Column(name = "available_stock")
    private Integer availableStock;
    
    @Column(name = "last_restock_date")
    private LocalDateTime lastRestockDate;
    
    @Column(name = "last_restock_quantity")
    private Integer lastRestockQuantity;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "bin_number")
    private String binNumber;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Inventory() {}
    
    public Inventory(Product product, Integer currentStock, String location) {
        this.product = product;
        this.currentStock = currentStock;
        this.location = location;
        this.availableStock = currentStock;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { 
        this.currentStock = currentStock;
        this.calculateAvailableStock();
    }

    public Integer getReservedStock() { return reservedStock; }
    public void setReservedStock(Integer reservedStock) { 
        this.reservedStock = reservedStock;
        this.calculateAvailableStock();
    }

    public Integer getAvailableStock() { return availableStock; }
    public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }

    public LocalDateTime getLastRestockDate() { return lastRestockDate; }
    public void setLastRestockDate(LocalDateTime lastRestockDate) { this.lastRestockDate = lastRestockDate; }

    public Integer getLastRestockQuantity() { return lastRestockQuantity; }
    public void setLastRestockQuantity(Integer lastRestockQuantity) { this.lastRestockQuantity = lastRestockQuantity; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getBinNumber() { return binNumber; }
    public void setBinNumber(String binNumber) { this.binNumber = binNumber; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Business Logic
    private void calculateAvailableStock() {
        this.availableStock = this.currentStock - (this.reservedStock != null ? this.reservedStock : 0);
    }

    public void addStock(Integer quantity) {
        this.currentStock += quantity;
        this.lastRestockDate = LocalDateTime.now();
        this.lastRestockQuantity = quantity;
        this.calculateAvailableStock();
    }

    public boolean canReserve(Integer quantity) {
        return this.availableStock >= quantity;
    }

    public void reserveStock(Integer quantity) {
        if (canReserve(quantity)) {
            this.reservedStock += quantity;
            this.calculateAvailableStock();
        } else {
            throw new IllegalArgumentException("Insufficient stock to reserve");
        }
    }

    public void releaseStock(Integer quantity) {
        this.reservedStock = Math.max(0, this.reservedStock - quantity);
        this.calculateAvailableStock();
    }

    public void consumeStock(Integer quantity) {
        if (this.reservedStock >= quantity) {
            this.reservedStock -= quantity;
            this.currentStock -= quantity;
            this.calculateAvailableStock();
        } else {
            throw new IllegalArgumentException("Cannot consume more than reserved stock");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
