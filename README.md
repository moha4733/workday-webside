# Workday - Professional Project Management System

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green.svg)
![Maven](https://img.shields.io/badge/Maven-3.11.0-blue.svg)
![Tests](https://img.shields.io/badge/Tests-41%20passing-brightgreen.svg)
![Coverage](https://img.shields.io/badge/Coverage-70%25%20%2B%2080%25-yellow.svg)

A professional project management system designed for construction companies, specifically tailored for the daily workflow of foremen (Svends) and project managers (Admins). Built with modern Java technologies and following industry best practices.

## 🚀 Features

### 📊 Dashboard & Overview
- **Real-time Dashboard**: Daily project overview, recent orders, and time tracking
- **Role-based Access**: Admin and Svend roles with appropriate permissions
- **Calendar Integration**: 5-day calendar view based on scheduled tasks
- **Google Maps Integration**: Clickable address links for project locations

### 🏗️ Project Management
- **Project Creation**: Admin creates projects with start dates, descriptions, addresses, priorities, and work types
- **Task Assignment**: Svends can only see assigned projects
- **Status Tracking**: Admin sets priority (Low/Medium/High), Svends updates status (Started/In Progress/Finished)
- **Photo Documentation**: Svends can upload project photos for documentation

### 🧮 Material Calculator
- **Comprehensive Calculations**: Flooring, Windows/Trim, Insulation, and Battens
- **Waste Calculation**: Automatic gross area calculation with configurable waste percentage
- **Order Generation**: Create material orders directly from calculations
- **Professional PDF Export**: Admin can export orders with company logo

### 📝 Time Tracking
- **Daily Time Registration**: Svends register hours per project and date
- **Automatic Summaries**: Daily hour totals displayed in dashboard
- **Work Log Management**: Track time spent on different projects
- **Approval Workflow**: Admin can review and approve time entries

### 📦 Order Management
- **Material Orders**: Svends create material requisitions
- **Approval Process**: Admin reviews, approves, or rejects orders
- **PDF Generation**: Professional order exports with company branding
- **Status Tracking**: Real-time order status updates

## 🛠️ Technology Stack

### Backend
- **Spring Boot 3.2.5**: Modern Java framework with auto-configuration
- **Spring Security**: Authentication and authorization with role-based access control
- **Spring Data JPA**: Database abstraction layer with Hibernate
- **Thymeleaf**: Server-side template engine for modern web applications
- **MySQL/H2**: Flexible database support (H2 for development, MySQL for production)

### Frontend
- **Thymeleaf Templates**: Clean, maintainable HTML templates
- **Bootstrap**: Responsive CSS framework for professional UI
- **JavaScript**: Interactive client-side functionality
- **AJAX**: Dynamic content updates without page reloads

### Testing & Quality
- **JUnit 5**: Modern testing framework
- **Mockito**: Mocking framework for unit tests
- **Spring Boot Test**: Integration testing utilities
- **Test Coverage**: 70-80% code coverage with comprehensive test suite

## 📋 Requirements

### System Requirements
- **Java 21**: Latest LTS version with modern features
- **Maven 3.11.0**: Build tool and dependency management
- **MySQL 8.0+**: Production database (optional, H2 for development)

### Development Environment
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions
- **Git**: Version control system
- **Node.js**: For E2E testing (optional)

## 🚀 Quick Start

### 1. Clone Repository
```bash
git clone https://github.com/moha4733/workday-webside.git
cd workday-webside
```

### 2. Run with H2 (Development)
The application runs out-of-the-box with H2 in-memory database:

```bash
# Set Java 21 environment (macOS)
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home

# Run the application
./mvnw spring-boot:run
```

Access the application at: http://localhost:8080

### 3. Default Users
Automatically created on first run:
- **Admin**: `admin@workday.dk` / `admin123`
- **Svend**: `svend@workday.dk` / `svend123`

### 4. MySQL Setup (Production)
1. Create MySQL database:
   ```sql
   CREATE DATABASE workday;
   ```

2. Configure database connection:
   ```bash
   cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
   ```

3. Edit `application-local.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/workday?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true
   spring.datasource.username=root
   spring.datasource.password=your_password
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
   ```

## 🧪 Testing

### Run All Tests
```bash
./mvnw test
```

### Test Coverage
Our comprehensive test suite includes:
- **41 Tests** with **70-80% code coverage**
- **Unit Tests**: Service layer, business logic
- **Integration Tests**: Repository layer, database operations
- **Entity Tests**: Domain model validation
- **E2E Tests**: Full application workflows

### Test Categories
```bash
# Unit tests only
./mvnw test -Dtest=*ServiceTest,*RepositoryTest,*EntityTest

# Integration tests
./mvnw test -Dtest=*E2ETest,*ApplicationTests

# Specific test
./mvnw test -Dtest=MaterialCalculatorServiceTest
```

## 📁 Project Structure

```
workday-webside/
├── src/main/java/dk/tommer/workday/
│   ├── controller/                 # MVC Controllers
│   │   ├── admin/                  # Admin controllers
│   │   ├── auth/                   # Authentication controllers
│   │   └── user/                   # User controllers
│   ├── Service/                   # Business Logic Layer
│   ├── repository/                # Data Access Layer
│   ├── entity/                    # Domain Models
│   ├── dto/                       # Data Transfer Objects
│   ├── config/                    # Configuration Classes
│   └── exception/                 # Custom Exceptions
├── src/main/resources/
│   ├── templates/                 # Thymeleaf Templates
│   │   ├── admin/                  # Admin templates
│   │   ├── auth/                   # Authentication templates
│   │   ├── user/                   # User templates
│   │   ├── components/             # Reusable components
│   │   └── layout/                 # Layout templates
│   ├── static/                     # Static Resources
│   │   ├── css/                     # Stylesheets
│   │   ├── js/                      # JavaScript files
│   │   └── images/                  # Image assets
│   └── application.properties       # Configuration
├── src/test/java/dk/tommer/workday/
│   ├── controller/                 # Controller Tests
│   ├── Service/                    # Service Tests
│   ├── repository/                # Repository Tests
│   ├── entity/                     # Entity Tests
│   └── e2e/                        # End-to-End Tests
├── STRUCTURE.md                    # Architecture Documentation
└── pom.xml                        # Maven Configuration
```

## 🏛️ Architecture

### Layered Architecture
- **Presentation Layer**: Controllers handle HTTP requests and responses
- **Service Layer**: Business logic and transaction management
- **Data Access Layer**: Repository pattern with Spring Data JPA
- **Domain Layer**: Entity models with JPA annotations

### Design Patterns
- **Repository Pattern**: Clean data access abstraction
- **DTO Pattern**: Data transfer objects for API communication
- **Service Pattern**: Business logic encapsulation
- **MVC Pattern**: Model-View-Controller architecture

### Security
- **Role-Based Access Control**: Admin and Svend roles
- **Spring Security**: Authentication and authorization
- **Password Encryption**: BCrypt password hashing
- **CSRF Protection**: Cross-site request forgery prevention

## 📊 Key Features in Detail

### Material Calculator
- **Flooring**: Calculates materials needed for flooring projects
- **Windows/Trim**: Window and trim material calculations
- **Insulation**: Insulation board calculations
- **Battens**: Structural batten calculations
- **Waste Management**: Configurable waste percentage
- **Package Optimization**: Rounds up to package sizes

### Time Management
- **Daily Registration**: Track hours worked per project
- **Project Allocation**: Assign time to specific projects
- **Approval Workflow**: Admin review and approval process
- **Reporting**: PDF export of approved time logs

### Order Management
- **Material Requisition**: Create material requests
- **Approval Process**: Multi-level approval workflow
- **PDF Generation**: Professional order documentation
- **Status Tracking**: Real-time order status updates

## 🔧 Configuration

### Application Properties
Key configuration options in `application.properties`:
```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Security Configuration
spring.security.user.name=admin
spring.security.user.password=admin123
```

### Profile-Based Configuration
- **Default**: H2 in-memory database for development
- **Production**: MySQL with persistent storage
- **Test**: Optimized for testing with H2

## 🚀 Deployment

### Development
```bash
./mvnw spring-boot:run
```

### Production Build
```bash
./mvnw clean package
java -jar target/workday-side-0.0.1-SNAPSHOT.jar
```

### Docker (Optional)
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/workday-side-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🧪 CI/CD Pipeline

### GitHub Actions
- **Build**: Maven compilation and testing
- **Test**: 41 tests with 70-80% coverage
- **Quality**: Code quality checks and static analysis
- **Deployment**: Automated deployment to production

### Test Results
- ✅ **41/41 tests passing**
- ✅ **70-80% code coverage**
- ✅ **0 failures, 0 errors**
- ✅ **Build time: ~7 seconds**

## 🔍 Monitoring & Logging

### Application Logging
- **Log Levels**: DEBUG, INFO, WARN, ERROR
- **Structured Logging**: JSON format for production
- **Performance Monitoring**: Request/response logging

### Health Checks
- **Actuator Endpoints**: `/actuator/health`, `/actuator/info`
- **Database Health**: Connection pool monitoring
- **Application Metrics**: Performance and usage statistics

## 🤝 Contributing

### Development Guidelines
1. Follow Java coding standards
2. Write tests for new features
3. Update documentation
4. Use meaningful commit messages
5. Ensure all tests pass before PR

### Code Quality
- **Test Coverage**: Maintain 70%+ coverage
- **Code Style**: Follow Google Java Style Guide
- **Documentation**: Update README and code comments
- **Security**: Follow security best practices

## 📞 Support

### Common Issues
- **Port Conflicts**: Change `server.port` in application.properties
- **Database Issues**: Check connection settings and credentials
- **Test Failures**: Ensure Java 21 is properly configured

### Troubleshooting
1. Check application logs for detailed error messages
2. Verify database connection and schema
3. Ensure all dependencies are properly configured
4. Run tests in development environment first

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Spring Boot Team**: For the excellent framework
- **Thymeleaf Team**: For the modern template engine
- **Hibernate Team**: For the robust ORM solution
- **Open Source Community**: For the amazing tools and libraries

---

**Built with ❤️ for the construction industry**
