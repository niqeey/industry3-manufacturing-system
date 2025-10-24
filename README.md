# Industry 3.0 Manufacturing Management System

A comprehensive manufacturing management application built with modern technologies to support Industry 3.0 operations.

## 🏭 Overview

This system provides a complete solution for manufacturing management including:

- **Production Management** - Work order tracking and scheduling
- **Inventory Management** - Real-time stock monitoring and control
- **Product Management** - Comprehensive product catalog management
- **Quality Control** - Quality inspection workflows
- **Analytics & Reporting** - Production KPIs and dashboards

## 🚀 Technology Stack

### Backend (Spring Boot)
- **Java 17+** with Spring Boot 3.x
- **Spring Data JPA** for database operations
- **Spring Security** with JWT authentication
- **MySQL** database for data persistence
- **Maven** for dependency management
- **OpenAPI/Swagger** for API documentation

### Frontend (React)
- **React 18+** with modern hooks
- **React Bootstrap** for responsive UI components
- **React Router** for navigation
- **Axios** for API communication
- **Chart.js** for data visualization

### Database
- **MySQL 8.x** with optimized schemas
- **Automated migrations** via Hibernate
- **Sample data** for testing and development

## 📁 Project Structure

```
/
├── backend/                 # Spring Boot API
│   ├── src/main/java/      # Java source code
│   ├── src/main/resources/ # Configuration files
│   └── pom.xml             # Maven dependencies
├── frontend/               # React application
│   ├── public/            # Static assets
│   ├── src/               # React source code
│   └── package.json       # npm dependencies
├── database/              # Database schemas
│   └── schema.sql         # MySQL database schema
└── docs/                  # Documentation
```

## 🛠️ Setup Instructions

### Prerequisites
- **Java 17+**
- **Node.js 18+** 
- **MySQL 8.x**
- **Maven 3.8+**
- **npm/yarn**

### Database Setup
1. Install MySQL and create a database:
```sql
CREATE DATABASE manufacturing_db;
CREATE USER 'manufacturing_user'@'localhost' IDENTIFIED BY 'manufacturing_pass';
GRANT ALL PRIVILEGES ON manufacturing_db.* TO 'manufacturing_user'@'localhost';
```

2. Run the database schema:
```bash
mysql -u manufacturing_user -p manufacturing_db < database/schema.sql
```

### Backend Setup
1. Navigate to the backend directory:
```bash
cd backend
```

2. Install dependencies and run:
```bash
mvn clean install
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

### Frontend Setup
1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

The application will be available at `http://localhost:3000`

## 🔧 Configuration

### Backend Configuration
Edit `backend/src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/manufacturing_db
spring.datasource.username=manufacturing_user
spring.datasource.password=manufacturing_pass

# Server Configuration  
server.port=8080

# JWT Configuration
app.jwt.secret=your-secret-key
app.jwt.expiration=86400000
```

### Frontend Configuration
The frontend automatically proxies API requests to `http://localhost:8080`.

For production, set the `REACT_APP_API_BASE_URL` environment variable.

## 📊 Features

### Dashboard
- Real-time production metrics
- Work order status overview
- Recent activity tracking
- Key performance indicators

### Product Management
- Complete product catalog
- Product lifecycle management
- Inventory level configuration
- Search and filtering capabilities

### Work Order Management
- Production planning and scheduling
- Work order lifecycle tracking
- Priority management
- Operator assignment
- Progress monitoring

### Inventory Management
- Real-time stock monitoring
- Low stock alerts
- Stock movement tracking
- Location management
- Reorder point automation

## 🔐 Security

- JWT-based authentication
- Role-based access control (RBAC)
- API endpoint security
- SQL injection protection
- XSS protection

## 👥 User Roles

- **Admin** - Full system access
- **Manager** - Production oversight and reporting
- **Operator** - Work order execution
- **Quality Inspector** - Quality control operations
- **Viewer** - Read-only access

## 🧪 Testing

### Backend Testing
```bash
cd backend
mvn test
```

### Frontend Testing
```bash
cd frontend
npm test
```

## 📈 API Documentation

API documentation is available via Swagger UI when the backend is running:
`http://localhost:8080/swagger-ui.html`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the documentation in the `/docs` folder
- Review the API documentation

## 🚀 Deployment

### Production Deployment

1. **Database**: Set up MySQL instance
2. **Backend**: Build and deploy JAR file
3. **Frontend**: Build and deploy to web server

```bash
# Backend
cd backend
mvn clean package
java -jar target/manufacturing-api-1.0.0.jar

# Frontend  
cd frontend
npm run build
# Deploy build/ directory to web server
```

## 📝 Changelog

### v1.0.0 (2025-08-30)
- Initial release
- Core manufacturing management features
- User authentication and authorization
- Responsive web interface
- MySQL database integration
- RESTful API design

---

**Built for Industry 3.0 - Bridging Digital Manufacturing Excellence** 🏭
