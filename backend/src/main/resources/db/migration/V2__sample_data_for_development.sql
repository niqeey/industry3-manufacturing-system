-- Sample data for development and testing
-- This file can be executed manually or through Flyway migration
-- Remove or modify this file for production deployment

-- Note: Only run this in development environments
-- For production, use proper data management processes

-- Sample Products (optional for development)
-- INSERT INTO products (product_code, name, description, unit_price, status, minimum_stock_level, maximum_stock_level, reorder_point, lead_time_days, created_at, updated_at) 
-- VALUES 
-- ('DEV-001', 'Development Widget', 'Sample widget for development testing', 10.00, 'ACTIVE', 50, 500, 75, 7, NOW(), NOW()),
-- ('DEV-002', 'Test Gear', 'Sample gear for testing purposes', 25.00, 'ACTIVE', 25, 250, 50, 14, NOW(), NOW());

-- Default Admin User (commented out for security)
-- Remember to use proper password hashing and secure defaults
-- INSERT INTO users (username, password, first_name, last_name, email, role, is_active, created_at, updated_at)
-- VALUES ('admin', '$2a$10$encrypted_password_here', 'System', 'Administrator', 'admin@company.com', 'ADMIN', true, NOW(), NOW());

-- Instructions for proper data management:
-- 1. Use environment-specific configuration
-- 2. Implement admin interfaces for data entry
-- 3. Use CSV import functionality for bulk data
-- 4. Integrate with external systems (ERP, WMS, etc.)
-- 5. Use proper data validation and business rules