# Industry 3.0 Manufacturing System - Testing Guide

## 🧪 Testing Overview

This document provides comprehensive testing procedures for the Industry 3.0 Manufacturing Management System to identify and resolve common issues.

## 🏗️ System Architecture Testing

### Backend (Spring Boot) Tests
- API Endpoint Testing
- Database Connection Testing  
- Security Configuration Testing
- Sample Data Validation

### Frontend (React) Tests
- Component Loading Tests
- API Integration Tests
- UI Functionality Tests
- Navigation Tests

---

## 🔧 Backend Testing

### 1. Server Startup Test

**Expected Result**: Spring Boot should start without errors on port 8080

**Test Steps**:
```bash
cd backend
mvn clean compile
mvn spring-boot:run
```

**Success Indicators**:
- ✅ "Started ManufacturingApplication in X.X seconds"
- ✅ "Tomcat started on port 8080"
- ✅ "Database initialization completed"

**Common Issues & Solutions**:

#### Database Connection Errors
```
Error: Could not connect to MySQL server
```
**Solution**:
1. Verify MySQL is running: `mysql -u manufacturing_user -p`
2. Check `application.properties` database credentials
3. Create database if missing:
```sql
CREATE DATABASE manufacturing_db;
CREATE USER 'manufacturing_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON manufacturing_db.* TO 'manufacturing_user'@'localhost';
```

#### Port Already in Use
```
Error: Port 8080 was already in use
```
**Solution**:
```bash
# Find process using port 8080
netstat -ano | findstr :8080
# Kill the process
taskkill /PID <process_id> /F
```

### 2. API Endpoint Tests

#### Test 1: Products API
```bash
# Test URL: http://localhost:8080/api/products
curl -X GET http://localhost:8080/api/products
```

**Expected Response**:
```json
[
  {
    "id": 1,
    "productCode": "WIDGET-001",
    "name": "Standard Widget",
    "status": "ACTIVE"
  }
]
```

#### Test 2: Work Orders API
```bash
# Test URL: http://localhost:8080/api/work-orders
curl -X GET http://localhost:8080/api/work-orders
```

#### Test 3: Swagger Documentation
```bash
# Test URL: http://localhost:8080/swagger-ui.html
# Should load interactive API documentation
```

### 3. Database Tests

#### Test Sample Data Loading
```sql
-- Connect to MySQL and verify data
USE manufacturing_db;
SELECT COUNT(*) as product_count FROM products;
SELECT COUNT(*) as user_count FROM users;
SELECT COUNT(*) as workorder_count FROM work_orders;
SELECT COUNT(*) as inventory_count FROM inventory;
```

**Expected Results**:
- Products: 4 records
- Users: 4 records  
- Work Orders: 3 records
- Inventory: 4 records

---

## 🎨 Frontend Testing

### 1. React App Startup Test

**Test Steps**:
```bash
cd frontend
npm install
npm start
```

**Success Indicators**:
- ✅ "Compiled successfully!"
- ✅ "You can now view manufacturing-frontend in the browser"
- ✅ "Local: http://localhost:3000"

**Common Issues & Solutions**:

#### Missing Dependencies
```
Error: Module not found: Can't resolve 'react-router-bootstrap'
```
**Solution**:
```bash
npm install react-router-bootstrap react-router-dom
```

#### Port 3000 In Use
```
Error: Something is already running on port 3000
```
**Solution**:
- Press `Y` to run on different port, or
- Kill process: `netstat -ano | findstr :3000`

### 2. Component Loading Tests

#### Test 1: Dashboard Load
- Navigate to: `http://localhost:3000`
- ✅ Should display manufacturing dashboard
- ✅ Should show navigation sidebar
- ✅ Should display metric cards

#### Test 2: Products Page
- Navigate to: `http://localhost:3000` → Click "Products"
- ✅ Should display products table
- ✅ Should show sample products (Widget, Gear, Bearing, Motor)
- ✅ Should have Add/Edit/Delete buttons

#### Test 3: Work Orders Page  
- Navigate to: `http://localhost:3000` → Click "Work Orders"
- ✅ Should display work orders table
- ✅ Should show status badges (PLANNED, IN_PROGRESS, etc.)

#### Test 4: Inventory Page
- Navigate to: `http://localhost:3000` → Click "Inventory"
- ✅ Should display inventory levels
- ✅ Should show stock status indicators

### 3. API Integration Tests

#### Test Frontend → Backend Connection
Open browser developer tools (F12) → Network tab:

1. **Products API Call**:
   - Navigate to Products page
   - Check Network tab for: `GET http://localhost:3000/api/products`
   - ✅ Status should be 200 OK
   - ✅ Response should contain product data

2. **CORS Issues Check**:
   - Look for CORS errors in Console tab
   - Should NOT see: "Access to XMLHttpRequest blocked by CORS policy"

---

## 🚨 Common Error Troubleshooting

### Backend Errors

#### 1. MySQL Connection Refused
```
Error: java.sql.SQLNonTransientConnectionException: Could not connect to address
```
**Fix Steps**:
1. Start MySQL service: `net start mysql80` (Windows)
2. Verify MySQL is running: `services.msc` → MySQL80 service
3. Test connection: `mysql -u root -p`

#### 2. Table Creation Errors
```
Error: Table 'manufacturing_db.products' doesn't exist
```
**Fix Steps**:
1. Check `spring.jpa.hibernate.ddl-auto=update` in `application.properties`
2. Verify database permissions
3. Clear and restart: Drop database and recreate

#### 3. Security Configuration Issues
```
Error: Access Denied / Unauthorized
```
**Fix Steps**:
1. Check `SecurityConfig.java` permits API endpoints
2. Verify CORS configuration
3. Test with Postman/curl first

### Frontend Errors

#### 1. API Connection Failed
```
Error: Network Error / ERR_CONNECTION_REFUSED
```
**Fix Steps**:
1. Verify backend is running on port 8080
2. Check `src/services/api.js` base URL
3. Verify no firewall blocking localhost:8080

#### 2. Component Render Errors
```
Error: Cannot read property 'map' of undefined
```
**Fix Steps**:
1. Check API response format
2. Add null checks in components
3. Verify state initialization

---

## 📋 Testing Checklist

### Pre-Startup Checklist
- [ ] MySQL server is running
- [ ] Database `manufacturing_db` exists
- [ ] User `manufacturing_user` has permissions
- [ ] Java 17+ is installed
- [ ] Node.js 16+ is installed
- [ ] Ports 3000 and 8080 are available

### Backend Testing Checklist
- [ ] Backend starts without errors
- [ ] Database tables are created
- [ ] Sample data is loaded
- [ ] API endpoints return 200 status
- [ ] Swagger UI loads successfully
- [ ] CORS is configured correctly

### Frontend Testing Checklist
- [ ] Frontend compiles successfully
- [ ] All pages load without errors
- [ ] Navigation works properly
- [ ] API calls succeed (check Network tab)
- [ ] Sample data displays correctly
- [ ] No console errors

### Integration Testing Checklist
- [ ] Frontend can fetch backend data
- [ ] CRUD operations work
- [ ] Error handling displays properly
- [ ] Authentication flow works (if enabled)
- [ ] Real-time updates function

---

## 🔍 Advanced Debugging

### Backend Debugging
1. **Enable Debug Logging**:
   ```properties
   logging.level.com.industry3.manufacturing=DEBUG
   logging.level.org.springframework.web=DEBUG
   ```

2. **Database Query Logging**:
   ```properties
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true
   ```

### Frontend Debugging
1. **Enable React DevTools**
2. **Check Browser Console** (F12 → Console)
3. **Monitor Network Requests** (F12 → Network)
4. **Use React Error Boundaries**

### Performance Testing
1. **Backend**: Use JMeter or curl scripts
2. **Frontend**: Use Lighthouse audit
3. **Database**: Monitor query performance

---

## 📞 Support & Next Steps

If issues persist after following this guide:

1. **Check Logs**: Review backend console output for specific errors
2. **Verify Environment**: Ensure all prerequisites are installed
3. **Test Isolation**: Test backend and frontend separately
4. **Database Check**: Verify database connectivity independently
5. **Clean Restart**: Stop all services, clear caches, restart fresh

For specific errors encountered, provide:
- Exact error message
- Steps to reproduce
- Environment details (OS, Java version, Node version)
- Console/log output