package com.industry3.manufacturing.service;

import com.industry3.manufacturing.entity.WorkOrder;
import com.industry3.manufacturing.entity.User;
import com.industry3.manufacturing.repository.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WorkOrderService {
    
    private final WorkOrderRepository workOrderRepository;
    
    @Autowired
    public WorkOrderService(WorkOrderRepository workOrderRepository) {
        this.workOrderRepository = workOrderRepository;
    }
    
    public List<WorkOrder> getAllWorkOrders() {
        return workOrderRepository.findAll();
    }
    
    public Optional<WorkOrder> getWorkOrderById(Long id) {
        return workOrderRepository.findById(id);
    }
    
    public Optional<WorkOrder> getWorkOrderByNumber(String workOrderNumber) {
        return workOrderRepository.findByWorkOrderNumber(workOrderNumber);
    }
    
    public List<WorkOrder> getActiveWorkOrders() {
        return workOrderRepository.findByStatusInOrderByPriorityAndPlannedStartDate(
            Arrays.asList(WorkOrder.WorkOrderStatus.PLANNED, WorkOrder.WorkOrderStatus.IN_PROGRESS)
        );
    }
    
    public List<WorkOrder> getWorkOrdersByStatus(WorkOrder.WorkOrderStatus status) {
        return workOrderRepository.findByStatus(status);
    }
    
    public List<WorkOrder> getWorkOrdersByOperator(User operator) {
        return workOrderRepository.findActiveWorkOrdersByOperator(operator);
    }
    
    public List<WorkOrder> getWorkOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return workOrderRepository.findWorkOrdersByDateRange(startDate, endDate);
    }
    
    public WorkOrder createWorkOrder(WorkOrder workOrder) {
        // Generate work order number if not provided
        if (workOrder.getWorkOrderNumber() == null || workOrder.getWorkOrderNumber().isEmpty()) {
            workOrder.setWorkOrderNumber(generateWorkOrderNumber());
        }
        
        // Validate work order number is unique
        if (workOrderRepository.existsByWorkOrderNumber(workOrder.getWorkOrderNumber())) {
            throw new IllegalArgumentException("Work order number already exists: " + workOrder.getWorkOrderNumber());
        }
        
        return workOrderRepository.save(workOrder);
    }
    
    public WorkOrder updateWorkOrder(Long id, WorkOrder workOrderDetails) {
        WorkOrder workOrder = workOrderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Work order not found with id: " + id));
        
        // Update fields
        workOrder.setProduct(workOrderDetails.getProduct());
        workOrder.setQuantityOrdered(workOrderDetails.getQuantityOrdered());
        workOrder.setQuantityCompleted(workOrderDetails.getQuantityCompleted());
        workOrder.setStatus(workOrderDetails.getStatus());
        workOrder.setPriority(workOrderDetails.getPriority());
        workOrder.setPlannedStartDate(workOrderDetails.getPlannedStartDate());
        workOrder.setPlannedEndDate(workOrderDetails.getPlannedEndDate());
        workOrder.setActualStartDate(workOrderDetails.getActualStartDate());
        workOrder.setActualEndDate(workOrderDetails.getActualEndDate());
        workOrder.setEstimatedHours(workOrderDetails.getEstimatedHours());
        workOrder.setActualHours(workOrderDetails.getActualHours());
        workOrder.setAssignedOperator(workOrderDetails.getAssignedOperator());
        workOrder.setNotes(workOrderDetails.getNotes());
        
        return workOrderRepository.save(workOrder);
    }
    
    public WorkOrder startWorkOrder(Long id, User operator) {
        WorkOrder workOrder = workOrderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Work order not found with id: " + id));
        
        if (workOrder.getStatus() != WorkOrder.WorkOrderStatus.PLANNED) {
            throw new IllegalStateException("Work order must be in PLANNED status to start");
        }
        
        workOrder.setStatus(WorkOrder.WorkOrderStatus.IN_PROGRESS);
        workOrder.setActualStartDate(LocalDateTime.now());
        workOrder.setAssignedOperator(operator);
        
        return workOrderRepository.save(workOrder);
    }
    
    public WorkOrder completeWorkOrder(Long id, Integer quantityCompleted) {
        WorkOrder workOrder = workOrderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Work order not found with id: " + id));
        
        if (workOrder.getStatus() != WorkOrder.WorkOrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Work order must be in IN_PROGRESS status to complete");
        }
        
        workOrder.setStatus(WorkOrder.WorkOrderStatus.COMPLETED);
        workOrder.setActualEndDate(LocalDateTime.now());
        workOrder.setQuantityCompleted(quantityCompleted);
        
        return workOrderRepository.save(workOrder);
    }
    
    public void cancelWorkOrder(Long id, String reason) {
        WorkOrder workOrder = workOrderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Work order not found with id: " + id));
        
        workOrder.setStatus(WorkOrder.WorkOrderStatus.CANCELLED);
        workOrder.setNotes((workOrder.getNotes() != null ? workOrder.getNotes() + " | " : "") + 
                           "Cancelled: " + reason);
        
        workOrderRepository.save(workOrder);
    }
    
    public Long getWorkOrderCountByStatus(WorkOrder.WorkOrderStatus status) {
        return workOrderRepository.countByStatus(status);
    }
    
    private String generateWorkOrderNumber() {
        // Generate work order number based on timestamp
        LocalDateTime now = LocalDateTime.now();
        String timestamp = String.format("%04d%02d%02d%02d%02d%02d", 
            now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
            now.getHour(), now.getMinute(), now.getSecond());
        
        return "WO" + timestamp;
    }
}
