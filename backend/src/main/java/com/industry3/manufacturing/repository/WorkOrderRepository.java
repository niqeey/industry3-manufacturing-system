package com.industry3.manufacturing.repository;

import com.industry3.manufacturing.entity.WorkOrder;
import com.industry3.manufacturing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    
    Optional<WorkOrder> findByWorkOrderNumber(String workOrderNumber);
    
    List<WorkOrder> findByStatus(WorkOrder.WorkOrderStatus status);
    
    List<WorkOrder> findByPriority(WorkOrder.Priority priority);
    
    List<WorkOrder> findByAssignedOperator(User assignedOperator);
    
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.status IN :statuses ORDER BY wo.priority DESC, wo.plannedStartDate ASC")
    List<WorkOrder> findByStatusInOrderByPriorityAndPlannedStartDate(List<WorkOrder.WorkOrderStatus> statuses);
    
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.plannedStartDate BETWEEN :startDate AND :endDate")
    List<WorkOrder> findWorkOrdersByDateRange(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.status = 'IN_PROGRESS'")
    List<WorkOrder> findActiveWorkOrders();
    
    @Query("SELECT COUNT(wo) FROM WorkOrder wo WHERE wo.status = :status")
    Long countByStatus(@Param("status") WorkOrder.WorkOrderStatus status);
    
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.status IN ('PLANNED', 'IN_PROGRESS') AND wo.assignedOperator = :operator")
    List<WorkOrder> findActiveWorkOrdersByOperator(@Param("operator") User operator);
    
    boolean existsByWorkOrderNumber(String workOrderNumber);
}
