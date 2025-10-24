-- Industry 3.0 Manufacturing Database Schema

-- Create database
CREATE DATABASE IF NOT EXISTS manufacturing_db;
USE manufacturing_db;

-- Create users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('ADMIN', 'MANAGER', 'OPERATOR', 'QUALITY_INSPECTOR', 'VIEWER') NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create products table
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    unit_price DECIMAL(10,2) NOT NULL,
    status ENUM('ACTIVE', 'DISCONTINUED', 'DRAFT') DEFAULT 'ACTIVE',
    minimum_stock_level INT,
    maximum_stock_level INT,
    reorder_point INT,
    lead_time_days INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create work_orders table
CREATE TABLE work_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    work_order_number VARCHAR(50) UNIQUE NOT NULL,
    product_id BIGINT,
    quantity_ordered INT NOT NULL,
    quantity_completed INT DEFAULT 0,
    status ENUM('PLANNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'ON_HOLD') DEFAULT 'PLANNED',
    priority ENUM('LOW', 'NORMAL', 'HIGH', 'URGENT') DEFAULT 'NORMAL',
    planned_start_date TIMESTAMP,
    planned_end_date TIMESTAMP,
    actual_start_date TIMESTAMP,
    actual_end_date TIMESTAMP,
    estimated_hours DECIMAL(8,2),
    actual_hours DECIMAL(8,2),
    assigned_operator_id BIGINT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (assigned_operator_id) REFERENCES users(id)
);

-- Create inventory table
CREATE TABLE inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT UNIQUE,
    current_stock INT NOT NULL DEFAULT 0,
    reserved_stock INT DEFAULT 0,
    available_stock INT NOT NULL DEFAULT 0,
    last_restock_date TIMESTAMP,
    last_restock_quantity INT,
    location VARCHAR(100),
    bin_number VARCHAR(50),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Create quality_inspections table
CREATE TABLE quality_inspections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    work_order_id BIGINT,
    inspector_id BIGINT,
    inspection_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PASS', 'FAIL', 'REWORK') NOT NULL,
    notes TEXT,
    defect_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (work_order_id) REFERENCES work_orders(id),
    FOREIGN KEY (inspector_id) REFERENCES users(id)
);

-- Create production_logs table
CREATE TABLE production_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    work_order_id BIGINT,
    operator_id BIGINT,
    log_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    quantity_produced INT,
    machine_id VARCHAR(50),
    notes TEXT,
    downtime_minutes INT DEFAULT 0,
    FOREIGN KEY (work_order_id) REFERENCES work_orders(id),
    FOREIGN KEY (operator_id) REFERENCES users(id)
);

-- Insert sample data
-- Sample users
INSERT INTO users (username, password, first_name, last_name, email, role) VALUES
('admin', '$2a$10$N.wmz8M8UbBVlOhCl9dHruJ6lh4jnH.V5q.F6qEaMLRqHVc/v5DYm', 'Admin', 'User', 'admin@manufacturing.com', 'ADMIN'),
('manager1', '$2a$10$N.wmz8M8UbBVlOhCl9dHruJ6lh4jnH.V5q.F6qEaMLRqHVc/v5DYm', 'John', 'Manager', 'manager@manufacturing.com', 'MANAGER'),
('operator1', '$2a$10$N.wmz8M8UbBVlOhCl9dHruJ6lh4jnH.V5q.F6qEaMLRqHVc/v5DYm', 'Alice', 'Smith', 'alice@manufacturing.com', 'OPERATOR'),
('operator2', '$2a$10$N.wmz8M8UbBVlOhCl9dHruJ6lh4jnH.V5q.F6qEaMLRqHVc/v5DYm', 'Bob', 'Johnson', 'bob@manufacturing.com', 'OPERATOR');

-- Sample products
INSERT INTO products (product_code, name, description, unit_price, minimum_stock_level, maximum_stock_level, reorder_point, lead_time_days) VALUES
('WIDGET-001', 'Standard Widget', 'Basic manufacturing widget for general use', 12.50, 100, 1000, 150, 7),
('GEAR-002', 'Precision Gear', 'High-precision gear for machinery applications', 45.00, 50, 500, 75, 14),
('BEARING-003', 'Industrial Bearing', 'Heavy-duty bearing for industrial equipment', 89.99, 25, 250, 40, 21),
('MOTOR-004', 'Electric Motor Assembly', 'Complete electric motor assembly', 299.95, 10, 100, 15, 30);

-- Sample work orders
INSERT INTO work_orders (work_order_number, product_id, quantity_ordered, priority, planned_start_date, planned_end_date, assigned_operator_id) VALUES
('WO20250830001', 1, 100, 'NORMAL', '2025-08-31 08:00:00', '2025-08-31 17:00:00', 3),
('WO20250830002', 2, 50, 'HIGH', '2025-09-01 08:00:00', '2025-09-02 17:00:00', 4),
('WO20250830003', 3, 25, 'URGENT', '2025-08-30 14:00:00', '2025-08-30 18:00:00', 3);

-- Sample inventory
INSERT INTO inventory (product_id, current_stock, available_stock, location, bin_number) VALUES
(1, 850, 850, 'Warehouse A', 'A1-001'),
(2, 425, 375, 'Warehouse A', 'A2-015'),
(3, 180, 155, 'Warehouse B', 'B1-008'),
(4, 85, 75, 'Warehouse B', 'B3-012');

-- Create indexes for better performance
CREATE INDEX idx_work_orders_status ON work_orders(status);
CREATE INDEX idx_work_orders_priority ON work_orders(priority);
CREATE INDEX idx_work_orders_assigned_operator ON work_orders(assigned_operator_id);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_code ON products(product_code);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_inventory_product ON inventory(product_id);
