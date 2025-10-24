package com.industry3.manufacturing.controller;

import com.industry3.manufacturing.entity.WorkOrder;
import com.industry3.manufacturing.entity.User;
import com.industry3.manufacturing.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/work-orders")
@CrossOrigin(origins = "*")
@Tag(name = "Work Order Management", description = "APIs for managing manufacturing work orders")
public class WorkOrderController {
    
    private final WorkOrderService workOrderService;
    
    @Autowired
    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }
    
    @GetMapping
    @Operation(summary = "Get all work orders", description = "Retrieve all work orders in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved work orders"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<WorkOrder>> getAllWorkOrders() {
        List<WorkOrder> workOrders = workOrderService.getAllWorkOrders();
        return ResponseEntity.ok(workOrders);
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active work orders", description = "Retrieve all work orders that are currently active (PLANNED, IN_PROGRESS)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved active work orders"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<WorkOrder>> getActiveWorkOrders() {
        List<WorkOrder> workOrders = workOrderService.getActiveWorkOrders();
        return ResponseEntity.ok(workOrders);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get work order by ID", description = "Retrieve a specific work order by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Work order found and returned"),
        @ApiResponse(responseCode = "404", description = "Work order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<WorkOrder> getWorkOrderById(
            @Parameter(description = "Unique identifier of the work order", required = true)
            @PathVariable Long id) {
        return workOrderService.getWorkOrderById(id)
            .map(workOrder -> ResponseEntity.ok(workOrder))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/number/{workOrderNumber}")
    @Operation(summary = "Get work order by number", description = "Retrieve a specific work order by its work order number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Work order found and returned"),
        @ApiResponse(responseCode = "404", description = "Work order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<WorkOrder> getWorkOrderByNumber(
            @Parameter(description = "Work order number to search for", required = true, example = "WO20250830001")
            @PathVariable String workOrderNumber) {
        return workOrderService.getWorkOrderByNumber(workOrderNumber)
            .map(workOrder -> ResponseEntity.ok(workOrder))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<WorkOrder>> getWorkOrdersByStatus(@PathVariable WorkOrder.WorkOrderStatus status) {
        List<WorkOrder> workOrders = workOrderService.getWorkOrdersByStatus(status);
        return ResponseEntity.ok(workOrders);
    }
    
    @GetMapping("/status-count/{status}")
    public ResponseEntity<Long> getWorkOrderCountByStatus(@PathVariable WorkOrder.WorkOrderStatus status) {
        Long count = workOrderService.getWorkOrderCountByStatus(status);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping
    public ResponseEntity<WorkOrder> createWorkOrder(@Valid @RequestBody WorkOrder workOrder) {
        try {
            WorkOrder createdWorkOrder = workOrderService.createWorkOrder(workOrder);
            return new ResponseEntity<>(createdWorkOrder, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<WorkOrder> updateWorkOrder(@PathVariable Long id, 
                                                   @Valid @RequestBody WorkOrder workOrderDetails) {
        try {
            WorkOrder updatedWorkOrder = workOrderService.updateWorkOrder(id, workOrderDetails);
            return ResponseEntity.ok(updatedWorkOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/start")
    public ResponseEntity<WorkOrder> startWorkOrder(@PathVariable Long id, 
                                                  @RequestBody User operator) {
        try {
            WorkOrder workOrder = workOrderService.startWorkOrder(id, operator);
            return ResponseEntity.ok(workOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/complete")
    public ResponseEntity<WorkOrder> completeWorkOrder(@PathVariable Long id, 
                                                     @RequestBody Map<String, Integer> request) {
        try {
            Integer quantityCompleted = request.get("quantityCompleted");
            WorkOrder workOrder = workOrderService.completeWorkOrder(id, quantityCompleted);
            return ResponseEntity.ok(workOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelWorkOrder(@PathVariable Long id, 
                                              @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");
            workOrderService.cancelWorkOrder(id, reason);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
