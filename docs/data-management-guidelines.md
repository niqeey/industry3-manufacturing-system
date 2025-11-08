# Data Management Guidelines

## Overview

This document outlines the recommended approaches for managing data in the Manufacturing System, replacing the previous hardcoded data initialization.

## Why Remove Hardcoded Data?

Hardcoded data in the application has several disadvantages:

- **Security Risk**: Exposes default passwords and sensitive information
- **Inflexibility**: Cannot be easily modified without code changes
- **Environment Issues**: Same data appears in all environments (dev, test, prod)
- **Maintenance Burden**: Requires code updates for data changes
- **Version Control Pollution**: Business data mixed with application code

## Recommended Data Management Approaches

### 1. Environment-Specific Configuration Files

Use YAML/properties files that can be customized per environment:

```yaml
# application-dev.yml
app:
  data:
    auto-initialize: true
    sample-data: enabled

# application-prod.yml  
app:
  data:
    auto-initialize: false
    sample-data: disabled
```

### 2. Database Migration Scripts

Use Flyway or Liquibase for versioned data changes:

```sql
-- V2__initial_system_setup.sql
INSERT INTO system_settings (key, value) VALUES ('version', '1.0.0');

-- V3__add_default_categories.sql
INSERT INTO categories (name, description) VALUES 
('Electronics', 'Electronic components and devices'),
('Mechanical', 'Mechanical parts and assemblies');
```

### 3. Admin Interface for Data Management

Implement web-based administrative interfaces for:

- User management
- Product catalog management
- System configuration
- Data import/export

### 4. CSV/Excel Import Functionality

Allow data to be imported from standard file formats:

```java
@PostMapping("/api/admin/import/products")
public ResponseEntity<?> importProducts(@RequestParam("file") MultipartFile file) {
    // Parse and validate CSV/Excel data
    // Import with proper error handling
}
```

### 5. External System Integration

Connect to external data sources:

- ERP systems for product data
- HR systems for user information  
- Warehouse management systems for inventory
- Supplier systems for purchasing data

### 6. API-Based Data Seeding

Create dedicated endpoints for data initialization:

```java
@PostMapping("/api/admin/seed/initial-data")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> seedInitialData(@RequestBody SeedDataRequest request) {
    // Controlled data seeding with validation
}
```

## Implementation Examples

### Environment Variables for Sensitive Data

```bash
# Development environment
export ADMIN_USERNAME=dev_admin
export ADMIN_PASSWORD=dev_password_123
export DB_SEED_DATA=true

# Production environment  
export ADMIN_USERNAME=prod_admin
export ADMIN_PASSWORD=secure_generated_password
export DB_SEED_DATA=false
```

### Configuration-Driven Initialization

```java
@ConfigurationProperties(prefix = "app.data")
public class DataConfiguration {
    private boolean autoInitialize = false;
    private AdminConfig admin = new AdminConfig();
    private DefaultsConfig defaults = new DefaultsConfig();
    
    // getters/setters
}
```

### Conditional Data Loading

```java
@Component
@ConditionalOnProperty(name = "app.data.auto-initialize", havingValue = "true")
public class ConditionalDataLoader implements CommandLineRunner {
    
    @Override
    public void run(String... args) throws Exception {
        // Load data only when explicitly configured
        if (shouldLoadSampleData()) {
            loadFromConfigurationFiles();
        }
    }
}
```

## Best Practices

### 1. Security First
- Never commit passwords or sensitive data
- Use environment variables for secrets
- Implement proper access controls for data management

### 2. Environment Separation
- Different data sets for different environments
- Clear separation between test and production data
- Automated data refresh for development environments

### 3. Validation and Error Handling
- Validate all imported data
- Provide clear error messages
- Implement rollback capabilities

### 4. Audit and Tracking
- Log all data changes
- Track who made changes and when
- Maintain data lineage and history

### 5. Documentation
- Document data schemas and relationships
- Provide import templates and examples
- Maintain data governance policies

## Migration Path

To transition from hardcoded data:

1. **Phase 1**: Disable hardcoded initialization (✅ Complete)
2. **Phase 2**: Implement configuration-based approach
3. **Phase 3**: Add admin interface for data management
4. **Phase 4**: Implement import/export functionality
5. **Phase 5**: Add external system integrations

## Tools and Technologies

- **Flyway/Liquibase**: Database migration management
- **Spring Boot Configuration**: Environment-specific properties
- **Apache POI**: Excel file processing
- **OpenCSV**: CSV file processing
- **Spring Security**: Access control for admin functions
- **Spring Boot Admin**: Application monitoring and management

## Conclusion

By removing hardcoded data and implementing proper data management practices, the Manufacturing System becomes more secure, flexible, and maintainable. This approach supports better development workflows, easier deployment processes, and more robust production operations.