# API Testing with Postman/cURL

## 🧪 Backend API Test Collection

### Base URL: `http://localhost:8080`

---

## Products API Tests

### 1. Get All Products
```bash
curl -X GET "http://localhost:8080/api/products" \
  -H "Accept: application/json"
```

**Expected Response (200 OK)**:
```json
[
  {
    "id": 1,
    "productCode": "WIDGET-001",
    "name": "Standard Widget",
    "description": "Basic manufacturing widget for general use",
    "unitPrice": 12.50,
    "status": "ACTIVE",
    "minimumStockLevel": 100,
    "maximumStockLevel": 1000,
    "reorderPoint": 150,
    "leadTimeDays": 7
  }
]
```

### 2. Get Product by ID
```bash
curl -X GET "http://localhost:8080/api/products/1" \
  -H "Accept: application/json"
```

### 3. Create New Product
```bash
curl -X POST "http://localhost:8080/api/products" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "productCode": "TEST-001",
    "name": "Test Product",
    "description": "Test product for API validation",
    "unitPrice": 25.99,
    "status": "ACTIVE",
    "minimumStockLevel": 50,
    "maximumStockLevel": 500,
    "reorderPoint": 75,
    "leadTimeDays": 14
  }'
```

### 4. Update Product
```bash
curl -X PUT "http://localhost:8080/api/products/1" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "id": 1,
    "productCode": "WIDGET-001",
    "name": "Updated Widget",
    "description": "Updated description",
    "unitPrice": 15.00,
    "status": "ACTIVE",
    "minimumStockLevel": 100,
    "maximumStockLevel": 1000,
    "reorderPoint": 150,
    "leadTimeDays": 7
  }'
```

### 5. Delete Product
```bash
curl -X DELETE "http://localhost:8080/api/products/1" \
  -H "Accept: application/json"
```

---

## Work Orders API Tests

### 1. Get All Work Orders
```bash
curl -X GET "http://localhost:8080/api/work-orders" \
  -H "Accept: application/json"
```

**Expected Response**:
```json
[
  {
    "id": 1,
    "workOrderNumber": "WO20250830001",
    "product": {
      "id": 1,
      "productCode": "WIDGET-001",
      "name": "Standard Widget"
    },
    "quantityOrdered": 100,
    "quantityCompleted": 0,
    "status": "PLANNED",
    "priority": "NORMAL"
  }
]
```

### 2. Get Active Work Orders
```bash
curl -X GET "http://localhost:8080/api/work-orders/active" \
  -H "Accept: application/json"
```

### 3. Get Work Order by ID
```bash
curl -X GET "http://localhost:8080/api/work-orders/1" \
  -H "Accept: application/json"
```

### 4. Get Work Order by Number
```bash
curl -X GET "http://localhost:8080/api/work-orders/number/WO20250830001" \
  -H "Accept: application/json"
```

---

## Inventory API Tests

### 1. Get All Inventory
```bash
curl -X GET "http://localhost:8080/api/inventory" \
  -H "Accept: application/json"
```

### 2. Get Low Stock Items
```bash
curl -X GET "http://localhost:8080/api/inventory/low-stock" \
  -H "Accept: application/json"
```

---

## Health Check Tests

### 1. Application Health
```bash
curl -X GET "http://localhost:8080/actuator/health" \
  -H "Accept: application/json"
```

**Expected Response**:
```json
{
  "status": "UP"
}
```

---

## Error Testing

### 1. Test 404 - Product Not Found
```bash
curl -X GET "http://localhost:8080/api/products/999" \
  -H "Accept: application/json"
```

**Expected Response (404 NOT FOUND)**:
```json
{
  "timestamp": "2025-10-25T10:30:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/api/products/999"
}
```

### 2. Test 400 - Invalid JSON
```bash
curl -X POST "http://localhost:8080/api/products" \
  -H "Content-Type: application/json" \
  -d '{ invalid json }'
```

### 3. Test CORS
```bash
curl -X GET "http://localhost:8080/api/products" \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: X-Requested-With"
```

---

## Database Validation Queries

Connect to MySQL and run these queries to verify data:

```sql
-- Check sample data exists
SELECT 'Products' as table_name, COUNT(*) as count FROM products
UNION ALL
SELECT 'Users', COUNT(*) FROM users  
UNION ALL
SELECT 'Work Orders', COUNT(*) FROM work_orders
UNION ALL
SELECT 'Inventory', COUNT(*) FROM inventory;

-- Verify product data
SELECT id, product_code, name, status, unit_price 
FROM products 
ORDER BY id;

-- Check work order statuses
SELECT work_order_number, status, priority, quantity_ordered, quantity_completed
FROM work_orders
ORDER BY id;

-- Inventory levels
SELECT p.product_code, p.name, i.current_stock, i.available_stock, 
       p.minimum_stock_level, p.reorder_point
FROM inventory i
JOIN products p ON i.product_id = p.id
ORDER BY i.current_stock;
```

---

## Load Testing Scripts

### Simple Load Test (10 concurrent requests)
```bash
#!/bin/bash
echo "Starting load test..."
for i in {1..10}; do
  curl -X GET "http://localhost:8080/api/products" &
done
wait
echo "Load test completed"
```

### PowerShell Load Test
```powershell
# PowerShell load test
$jobs = @()
for ($i = 1; $i -le 10; $i++) {
    $jobs += Start-Job -ScriptBlock {
        Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method GET
    }
}
$jobs | Wait-Job | Receive-Job
```

---

## Swagger UI Testing

1. **Open Swagger UI**: http://localhost:8080/swagger-ui.html
2. **Test Each Endpoint**:
   - Click "Try it out" button
   - Fill in parameters
   - Click "Execute"
   - Verify response

### Expected Swagger Sections:
- **Product Management**: CRUD operations for products
- **Work Order Management**: Work order operations
- **Inventory Management**: Stock level operations

---

## Frontend API Integration Tests

### JavaScript Console Tests (Run in browser at localhost:3000)

```javascript
// Test API connectivity
fetch('http://localhost:8080/api/products')
  .then(response => response.json())
  .then(data => console.log('Products:', data))
  .catch(error => console.error('Error:', error));

// Test CORS
fetch('http://localhost:8080/api/work-orders', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
  }
})
.then(response => response.json())
.then(data => console.log('Work Orders:', data));
```

---

## Performance Benchmarks

### Expected Response Times:
- **GET /api/products**: < 200ms
- **GET /api/work-orders**: < 300ms  
- **POST /api/products**: < 500ms
- **Database queries**: < 100ms

### Memory Usage:
- **Backend JVM**: < 512MB at startup
- **Frontend**: < 100MB bundle size

### Concurrent Users:
- **Target**: 50+ concurrent users
- **Database connections**: Max 20 pool size