package com.industry3.manufacturing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_orders")
public class WorkOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "work_order_number", unique = true)
    private String workOrderNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    
    @NotNull
    @Min(1)
    @Column(name = "quantity_ordered")
    private Integer quantityOrdered;
    
    @Column(name = "quantity_completed")
    private Integer quantityCompleted = 0;
    
    @Enumerated(EnumType.STRING)
    private WorkOrderStatus status;
    
    @Enumerated(EnumType.STRING)
    private Priority priority;
    
    @Column(name = "planned_start_date")
    private LocalDateTime plannedStartDate;
    
    @Column(name = "planned_end_date")
    private LocalDateTime plannedEndDate;
    
    @Column(name = "actual_start_date")
    private LocalDateTime actualStartDate;
    
    @Column(name = "actual_end_date")
    private LocalDateTime actualEndDate;
    
    @Column(name = "estimated_hours")
    private Double estimatedHours;
    
    @Column(name = "actual_hours")
    private Double actualHours;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_operator_id")
    private User assignedOperator;
    
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public WorkOrder() {}
    
    public WorkOrder(String workOrderNumber, Product product, Integer quantityOrdered, Priority priority) {
        this.workOrderNumber = workOrderNumber;
        this.product = product;
        this.quantityOrdered = quantityOrdered;
        this.priority = priority;
        this.status = WorkOrderStatus.PLANNED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getWorkOrderNumber() { return workOrderNumber; }
    public void setWorkOrderNumber(String workOrderNumber) { this.workOrderNumber = workOrderNumber; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantityOrdered() { return quantityOrdered; }
    public void setQuantityOrdered(Integer quantityOrdered) { this.quantityOrdered = quantityOrdered; }

    public Integer getQuantityCompleted() { return quantityCompleted; }
    public void setQuantityCompleted(Integer quantityCompleted) { this.quantityCompleted = quantityCompleted; }

    public WorkOrderStatus getStatus() { return status; }
    public void setStatus(WorkOrderStatus status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public LocalDateTime getPlannedStartDate() { return plannedStartDate; }
    public void setPlannedStartDate(LocalDateTime plannedStartDate) { this.plannedStartDate = plannedStartDate; }

    public LocalDateTime getPlannedEndDate() { return plannedEndDate; }
    public void setPlannedEndDate(LocalDateTime plannedEndDate) { this.plannedEndDate = plannedEndDate; }

    public LocalDateTime getActualStartDate() { return actualStartDate; }
    public void setActualStartDate(LocalDateTime actualStartDate) { this.actualStartDate = actualStartDate; }

    public LocalDateTime getActualEndDate() { return actualEndDate; }
    public void setActualEndDate(LocalDateTime actualEndDate) { this.actualEndDate = actualEndDate; }

    public Double getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(Double estimatedHours) { this.estimatedHours = estimatedHours; }

    public Double getActualHours() { return actualHours; }
    public void setActualHours(Double actualHours) { this.actualHours = actualHours; }

    public User getAssignedOperator() { return assignedOperator; }
    public void setAssignedOperator(User assignedOperator) { this.assignedOperator = assignedOperator; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum WorkOrderStatus {
        PLANNED, IN_PROGRESS, COMPLETED, CANCELLED, ON_HOLD
    }

    public enum Priority {
        LOW, NORMAL, HIGH, URGENT
    }
}
