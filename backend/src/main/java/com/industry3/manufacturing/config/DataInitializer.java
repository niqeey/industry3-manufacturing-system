package com.industry3.manufacturing.config;

import com.industry3.manufacturing.entity.Product;
import com.industry3.manufacturing.entity.User;
import com.industry3.manufacturing.entity.WorkOrder;
import com.industry3.manufacturing.entity.Inventory;
import com.industry3.manufacturing.repository.ProductRepository;
import com.industry3.manufacturing.repository.UserRepository;
import com.industry3.manufacturing.repository.WorkOrderRepository;
import com.industry3.manufacturing.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WorkOrderRepository workOrderRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if database is empty
        if (productRepository.count() == 0) {
            initializeProducts();
            initializeUsers();
            initializeWorkOrders();
            initializeInventory();
            System.out.println("✅ Sample data initialized successfully!");
        } else {
            System.out.println("ℹ️ Database already contains data, skipping initialization.");
        }
    }

    private void initializeProducts() {
        // Sample Products
        Product widget = new Product();
        widget.setProductCode("WIDGET-001");
        widget.setName("Standard Widget");
        widget.setDescription("Basic manufacturing widget for general use");
        widget.setUnitPrice(BigDecimal.valueOf(12.50));
        widget.setStatus(Product.ProductStatus.ACTIVE);
        widget.setMinimumStockLevel(100);
        widget.setMaximumStockLevel(1000);
        widget.setReorderPoint(150);
        widget.setLeadTimeDays(7);
        
        Product gear = new Product();
        gear.setProductCode("GEAR-002");
        gear.setName("Precision Gear");
        gear.setDescription("High-precision gear for machinery applications");
        gear.setUnitPrice(BigDecimal.valueOf(45.00));
        gear.setStatus(Product.ProductStatus.ACTIVE);
        gear.setMinimumStockLevel(50);
        gear.setMaximumStockLevel(500);
        gear.setReorderPoint(75);
        gear.setLeadTimeDays(14);
        
        Product bearing = new Product();
        bearing.setProductCode("BEARING-003");
        bearing.setName("Industrial Bearing");
        bearing.setDescription("Heavy-duty bearing for industrial equipment");
        bearing.setUnitPrice(BigDecimal.valueOf(89.99));
        bearing.setStatus(Product.ProductStatus.ACTIVE);
        bearing.setMinimumStockLevel(25);
        bearing.setMaximumStockLevel(250);
        bearing.setReorderPoint(40);
        bearing.setLeadTimeDays(21);
        
        Product motor = new Product();
        motor.setProductCode("MOTOR-004");
        motor.setName("Electric Motor Assembly");
        motor.setDescription("Complete electric motor assembly");
        motor.setUnitPrice(BigDecimal.valueOf(299.95));
        motor.setStatus(Product.ProductStatus.ACTIVE);
        motor.setMinimumStockLevel(10);
        motor.setMaximumStockLevel(100);
        motor.setReorderPoint(15);
        motor.setLeadTimeDays(30);
        
        productRepository.save(widget);
        productRepository.save(gear);
        productRepository.save(bearing);
        productRepository.save(motor);
    }

    private void initializeUsers() {
        // Sample Users
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("$2a$10$N.wmz8M8UbBVlOhCl9dHruJ6lh4jnH.V5q.F6qEaMLRqHVc/v5DYm"); // password
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEmail("admin@manufacturing.com");
        admin.setRole(User.Role.ADMIN);
        
        User manager = new User();
        manager.setUsername("manager1");
        manager.setPassword("$2a$10$N.wmz8M8UbBVlOhCl9dHruJ6lh4jnH.V5q.F6qEaMLRqHVc/v5DYm"); // password
        manager.setFirstName("John");
        manager.setLastName("Manager");
        manager.setEmail("manager@manufacturing.com");
        manager.setRole(User.Role.MANAGER);
        
        User operator1 = new User();
        operator1.setUsername("operator1");
        operator1.setPassword("$2a$10$N.wmz8M8UbBVlOhCl9dHruJ6lh4jnH.V5q.F6qEaMLRqHVc/v5DYm"); // password
        operator1.setFirstName("Alice");
        operator1.setLastName("Smith");
        operator1.setEmail("alice@manufacturing.com");
        operator1.setRole(User.Role.OPERATOR);
        
        User operator2 = new User();
        operator2.setUsername("operator2");
        operator2.setPassword("$2a$10$N.wmz8M8UbBVlOhCl9dHruJ6lh4jnH.V5q.F6qEaMLRqHVc/v5DYm"); // password
        operator2.setFirstName("Bob");
        operator2.setLastName("Johnson");
        operator2.setEmail("bob@manufacturing.com");
        operator2.setRole(User.Role.OPERATOR);
        
        userRepository.save(admin);
        userRepository.save(manager);
        userRepository.save(operator1);
        userRepository.save(operator2);
    }

    private void initializeWorkOrders() {
        // Get products and users for work orders
        Product widget = productRepository.findByProductCode("WIDGET-001").orElse(null);
        Product gear = productRepository.findByProductCode("GEAR-002").orElse(null);
        Product bearing = productRepository.findByProductCode("BEARING-003").orElse(null);
        
        User operator1 = userRepository.findByUsername("operator1").orElse(null);
        User operator2 = userRepository.findByUsername("operator2").orElse(null);
        
        if (widget != null && operator1 != null) {
            WorkOrder wo1 = new WorkOrder();
            wo1.setWorkOrderNumber("WO20250830001");
            wo1.setProduct(widget);
            wo1.setQuantityOrdered(100);
            wo1.setQuantityCompleted(0);
            wo1.setStatus(WorkOrder.WorkOrderStatus.PLANNED);
            wo1.setPriority(WorkOrder.Priority.NORMAL);
            wo1.setPlannedStartDate(LocalDateTime.now().plusHours(2));
            wo1.setPlannedEndDate(LocalDateTime.now().plusHours(10));
            wo1.setAssignedOperator(operator1);
            wo1.setEstimatedHours(8.0);
            wo1.setNotes("Standard production run for widgets");
            
            workOrderRepository.save(wo1);
        }
        
        if (gear != null && operator2 != null) {
            WorkOrder wo2 = new WorkOrder();
            wo2.setWorkOrderNumber("WO20250830002");
            wo2.setProduct(gear);
            wo2.setQuantityOrdered(50);
            wo2.setQuantityCompleted(25);
            wo2.setStatus(WorkOrder.WorkOrderStatus.IN_PROGRESS);
            wo2.setPriority(WorkOrder.Priority.HIGH);
            wo2.setPlannedStartDate(LocalDateTime.now().minusHours(2));
            wo2.setPlannedEndDate(LocalDateTime.now().plusHours(6));
            wo2.setActualStartDate(LocalDateTime.now().minusHours(2));
            wo2.setAssignedOperator(operator2);
            wo2.setEstimatedHours(8.0);
            wo2.setActualHours(4.0);
            wo2.setNotes("High precision gear production - halfway complete");
            
            workOrderRepository.save(wo2);
        }
        
        if (bearing != null && operator1 != null) {
            WorkOrder wo3 = new WorkOrder();
            wo3.setWorkOrderNumber("WO20250830003");
            wo3.setProduct(bearing);
            wo3.setQuantityOrdered(25);
            wo3.setQuantityCompleted(0);
            wo3.setStatus(WorkOrder.WorkOrderStatus.PLANNED);
            wo3.setPriority(WorkOrder.Priority.URGENT);
            wo3.setPlannedStartDate(LocalDateTime.now().plusMinutes(30));
            wo3.setPlannedEndDate(LocalDateTime.now().plusHours(4));
            wo3.setAssignedOperator(operator1);
            wo3.setEstimatedHours(4.0);
            wo3.setNotes("Urgent order for industrial bearings");
            
            workOrderRepository.save(wo3);
        }
    }

    private void initializeInventory() {
        // Initialize inventory for all products
        productRepository.findAll().forEach(product -> {
            if (inventoryRepository.findByProduct(product).isEmpty()) {
                Inventory inventory = new Inventory();
                inventory.setProduct(product);
                
                // Set different stock levels for demonstration
                switch (product.getProductCode()) {
                    case "WIDGET-001":
                        inventory.setCurrentStock(850);
                        inventory.setReservedStock(0);
                        inventory.setLocation("Warehouse A");
                        inventory.setBinNumber("A1-001");
                        break;
                    case "GEAR-002":
                        inventory.setCurrentStock(425);
                        inventory.setReservedStock(50);
                        inventory.setLocation("Warehouse A");
                        inventory.setBinNumber("A2-015");
                        break;
                    case "BEARING-003":
                        inventory.setCurrentStock(35); // Low stock example
                        inventory.setReservedStock(0);
                        inventory.setLocation("Warehouse B");
                        inventory.setBinNumber("B1-008");
                        break;
                    case "MOTOR-004":
                        inventory.setCurrentStock(85);
                        inventory.setReservedStock(10);
                        inventory.setLocation("Warehouse B");
                        inventory.setBinNumber("B3-012");
                        break;
                    default:
                        inventory.setCurrentStock(100);
                        inventory.setReservedStock(0);
                        inventory.setLocation("Warehouse A");
                        inventory.setBinNumber("A1-000");
                }
                
                inventory.setAvailableStock(inventory.getCurrentStock() - inventory.getReservedStock());
                inventoryRepository.save(inventory);
            }
        });
    }
}
