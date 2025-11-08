# Error Diagnosis & Resolution Guide

## 🚨 Common Errors and Solutions

This document provides step-by-step solutions for the most common errors encountered in the Industry 3.0 Manufacturing System.

---

## Backend Errors (Spring Boot)

### 1. Database Connection Errors

#### Error: `Communications link failure`
```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
The last packet sent successfully to the server was 0 milliseconds ago.
```

**Diagnosis Steps**:
1. Check if MySQL is running:
```bash
# Windows
net start mysql80
services.msc

# Check process
tasklist | findstr mysql
```

2. Test MySQL connection:
```bash
mysql -u manufacturing_user -p -h localhost
```

**Solutions**:
```sql
-- 1. Create database and user if missing
CREATE DATABASE IF NOT EXISTS manufacturing_db;
CREATE USER IF NOT EXISTS 'manufacturing_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON manufacturing_db.* TO 'manufacturing_user'@'localhost';
FLUSH PRIVILEGES;

-- 2. Test connection
USE manufacturing_db;
SHOW TABLES;
```

#### Error: `Access denied for user`
```
java.sql.SQLException: Access denied for user 'manufacturing_user'@'localhost' (using password: YES)
```

**Solutions**:
1. Reset user password:
```sql
ALTER USER 'manufacturing_user'@'localhost' IDENTIFIED BY 'password';
FLUSH PRIVILEGES;
```

2. Check application.properties:
```properties
spring.datasource.username=manufacturing_user
spring.datasource.password=password
spring.datasource.url=jdbc:mysql://localhost:3306/manufacturing_db?useSSL=false&serverTimezone=UTC
```

### 2. Port Binding Errors

#### Error: `Port 8080 was already in use`
```
org.springframework.boot.web.server.PortInUseException: Port 8080 is already in use
```

**Solutions**:
```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID)
taskkill /PID <process_id> /F

# Or use different port in application.properties
server.port=8081
```

### 3. Bean Creation Errors

#### Error: `Field repository required a bean of type`
```
Field productRepository in ProductService required a bean of type 'ProductRepository' that could not be found
```

**Solutions**:
1. Add `@Repository` annotation:
```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // repository methods
}
```

2. Enable JPA repositories:
```java
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.industry3.manufacturing.repository")
public class ManufacturingApplication {
    // main method
}
```

### 4. Hibernate/JPA Errors

#### Error: `Table doesn't exist`
```
Table 'manufacturing_db.products' doesn't exist
```

**Solutions**:
1. Check ddl-auto setting:
```properties
spring.jpa.hibernate.ddl-auto=update
# or for fresh start:
spring.jpa.hibernate.ddl-auto=create-drop
```

2. Manual table creation:
```sql
USE manufacturing_db;

CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    unit_price DECIMAL(10,2),
    status ENUM('ACTIVE', 'INACTIVE', 'DISCONTINUED') DEFAULT 'ACTIVE',
    minimum_stock_level INT DEFAULT 0,
    maximum_stock_level INT DEFAULT 1000,
    reorder_point INT DEFAULT 50,
    lead_time_days INT DEFAULT 7,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 5. Security Configuration Errors

#### Error: `Access denied` or `401 Unauthorized`
```
HTTP 401 Unauthorized - Full authentication is required
```

**Solutions**:
1. Check SecurityConfig.java:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
```

---

## Frontend Errors (React)

### 1. Module Not Found Errors

#### Error: `Module not found: Can't resolve 'react-router-bootstrap'`
```
Module not found: Error: Can't resolve 'react-router-bootstrap' in 'C:\...\src\components'
```

**Solutions**:
```bash
cd frontend
npm install react-router-bootstrap react-router-dom
npm install bootstrap react-bootstrap
```

#### Error: `Can't resolve './components/Sidebar'`
**Solution**: Check file path and import:
```javascript
// Correct import
import Sidebar from './components/Sidebar';

// Check file exists at: src/components/Sidebar.js
```

### 2. API Connection Errors

#### Error: `Network Error` or `ERR_CONNECTION_REFUSED`
```
Error: Network Error
    at createError (createError.js:16)
```

**Diagnosis**:
1. Check if backend is running:
```bash
curl http://localhost:8080/api/products
```

2. Check browser console (F12 → Console) for CORS errors

**Solutions**:
1. Verify API base URL in `src/services/api.js`:
```javascript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export default api;
```

2. Add CORS configuration in backend:
```java
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/products")
public class ProductController {
    // controller methods
}
```

### 3. Compilation Errors

#### Error: `Failed to compile` with ESLint warnings
```
Failed to compile.

./src/pages/Inventory.js
  Line 3:24:  'productAPI' is defined but never used  no-unused-vars
```

**Solutions**:
1. Remove unused imports:
```javascript
// Before
import { inventoryAPI, productAPI } from '../services/api';

// After  
import { inventoryAPI } from '../services/api';
```

2. Or disable ESLint for specific lines:
```javascript
// eslint-disable-next-line no-unused-vars
import { inventoryAPI, productAPI } from '../services/api';
```

### 4. State Management Errors

#### Error: `Cannot read property 'map' of undefined`
```
TypeError: Cannot read property 'map' of undefined
```

**Solution**: Initialize state properly and add null checks:
```javascript
const [products, setProducts] = useState([]);

// In render
{products && products.length > 0 ? (
  products.map(product => (
    <tr key={product.id}>
      <td>{product.name}</td>
    </tr>
  ))
) : (
  <tr><td colSpan="5">No products found</td></tr>
)}
```

---

## Integration Errors

### 1. CORS Policy Errors

#### Error: `Access blocked by CORS policy`
```
Access to XMLHttpRequest at 'http://localhost:8080/api/products' from origin 'http://localhost:3000' 
has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header
```

**Solutions**:
1. Backend CORS configuration:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

2. Global CORS in Application class:
```java
@SpringBootApplication
public class ManufacturingApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManufacturingApplication.class, args);
    }
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
```

### 2. JSON Parsing Errors

#### Error: `JSON parse error: Unexpected character`
**Solution**: Verify API response format and error handling:
```javascript
const fetchProducts = async () => {
  try {
    const response = await api.get('/api/products');
    console.log('Raw response:', response); // Debug log
    setProducts(response.data);
  } catch (error) {
    console.error('API Error:', error);
    if (error.response) {
      console.error('Response data:', error.response.data);
      console.error('Response status:', error.response.status);
    }
  }
};
```

---

## Environment Setup Errors

### 1. Java Version Issues

#### Error: `UnsupportedClassVersionError`
```
Exception in thread "main" java.lang.UnsupportedClassVersionError: 
com/industry3/manufacturing/ManufacturingApplication has been compiled by a more recent version of the Java Runtime
```

**Solutions**:
```bash
# Check Java version
java -version

# Should be Java 17 or higher
# Install Java 17 if needed, then set JAVA_HOME
```

### 2. Maven Issues

#### Error: `mvn command not found`
**Solutions**:
```bash
# Windows - Install Maven and add to PATH
# Or use Maven wrapper
./mvnw spring-boot:run
```

### 3. Node.js/NPM Issues

#### Error: `npm command not found`
**Solutions**:
```bash
# Install Node.js 16+ from nodejs.org
# Verify installation
node --version
npm --version
```

---

## Quick Diagnostic Script

Create this script to quickly check system status:

```bash
#!/bin/bash
# diagnosis.sh

echo "=== System Diagnostic ==="

echo "1. Checking Java version..."
java -version

echo "2. Checking Node.js..."
node --version
npm --version

echo "3. Checking MySQL..."
mysql --version
mysqladmin -u manufacturing_user -p ping

echo "4. Checking ports..."
netstat -an | findstr :8080
netstat -an | findstr :3000

echo "5. Testing API..."
curl -s http://localhost:8080/api/products | head -5

echo "=== Diagnostic Complete ==="
```

---

## Emergency Reset Procedure

If all else fails, use this complete reset:

### 1. Stop All Services
```bash
# Stop any running Java processes
taskkill /F /IM java.exe
# Stop any running Node processes  
taskkill /F /IM node.exe
```

### 2. Clean Database
```sql
DROP DATABASE IF EXISTS manufacturing_db;
CREATE DATABASE manufacturing_db;
-- Recreate user and permissions
```

### 3. Clean Maven/NPM
```bash
# Backend
cd backend
mvn clean
rm -rf target/

# Frontend  
cd frontend
rm -rf node_modules/
npm install
```

### 4. Restart Fresh
```bash
# Backend
cd backend
mvn spring-boot:run

# Frontend (new terminal)
cd frontend
npm start
```

This should resolve most common errors and get your system running again.