# Workday Application - Package Structure

This document outlines the professional package structure following Spring Boot best practices.

## 📁 Package Organization

```
dk.tommer.workday/
├── 📂 config/                    # Configuration classes
│   ├── SecurityConfig.java       # Spring Security configuration
│   ├── DataInitializer.java      # Database initialization
│   └── WebConfig.java           # Web configuration
│
├── 📂 controller/                # REST Controllers
│   ├── 📂 admin/                # Admin-specific controllers
│   │   ├── AdminOrderController.java
│   │   ├── AdminWorkLogController.java
│   │   └── EmployeeController.java
│   ├── 📂 auth/                 # Authentication controllers
│   │   └── RegistrationController.java
│   ├── 📂 user/                 # User/Svend controllers
│   │   ├── SvendCalculatorController.java
│   │   ├── SvendDashboardController.java
│   │   ├── SvendOrderController.java
│   │   ├── SvendProjectController.java
│   │   ├── SvendSettingsController.java
│   │   └── WorkLogController.java
│   ├── HomeController.java      # Main dashboard
│   ├── ProfileController.java   # Profile management
│   ├── ProjectController.java   # Project management
│   └── SettingsController.java  # General settings
│
├── 📂 service/                   # Business logic layer
│   ├── EmployeeService.java      # Employee business logic
│   ├── MaterialCalculatorService.java
│   ├── PdfService.java           # PDF generation
│   ├── ProjectService.java       # Project business logic
│   └── UserService.java         # User business logic
│
├── 📂 repository/                # Data access layer
│   ├── CompanyRepository.java
│   ├── DayPlanRepository.java
│   ├── MaterialOrderRepository.java
│   ├── ProjectRepository.java
│   ├── UserRepository.java
│   └── WorkLogRepository.java
│
├── 📂 entity/                    # Domain entities
│   ├── Company.java
│   ├── DayPlan.java
│   ├── MaterialOrder.java
│   ├── Project.java
│   ├── Role.java
│   ├── User.java
│   ├── WorkLog.java
│   └── WorkLogStatus.java
│
├── 📂 dto/                       # Data Transfer Objects
│   ├── CalculationResultDTO.java
│   ├── DayPlanDTO.java
│   ├── MaterialOrderDTO.java
│   └── ProjectSummaryDTO.java
│
├── 📂 exception/                 # Custom exceptions
│   └── GlobalExceptionHandler.java
│
├── 📂 util/                      # Utility classes
│   └── (future utilities)
│
└── TommerApplication.java        # Main application class
```

## 📁 Resources Structure

```
src/main/resources/
├── 📂 static/                    # Static web assets
│   ├── 📂 css/                   # Stylesheets
│   │   ├── 📂 admin/            # Admin-specific styles
│   │   │   └── admin.css
│   │   ├── 📂 auth/             # Authentication styles
│   │   │   ├── login.css
│   │   │   └── register.css
│   │   └── 📂 common/           # Shared styles
│   │       ├── user.css
│   │       └── welcome.css
│   ├── 📂 js/                    # JavaScript files
│   │   └── common.js            # Common utilities
│   ├── 📂 images/                # Images and icons
│   └── 📂 uploads/               # User uploaded files
│       └── profiles/           # Profile pictures
│
├── 📂 templates/                # Thymeleaf templates
│   ├── 📂 admin/                # Admin pages
│   │   ├── admin-dashboard.html
│   │   ├── admin-orders.html
│   │   ├── employees.html
│   │   ├── edit-employee-hours.html
│   │   └── settings.html
│   ├── 📂 auth/                 # Authentication pages
│   │   ├── login.html
│   │   └── register.html
│   ├── 📂 user/                 # User/Svend pages
│   │   ├── svend-dashboard.html
│   │   ├── svend-calculator.html
│   │   ├── svend-orders.html
│   │   ├── svend-project-photo.html
│   │   ├── svend-projects.html
│   │   ├── svend-settings.html
│   │   └── worklogs.html
│   ├── 📂 components/            # Reusable components
│   │   ├── profile-settings.html
│   │   └── profile-section.html
│   ├── 📂 layout/                # Layout templates
│   │   ├── error.html
│   │   └── welcome.html
│   ├── 📂 error/                 # Error pages
│   │   └── error.html
│   ├── assign-project.html
│   ├── create-project.html
│   ├── edit-project.html
│   ├── employees.html
│   ├── projects.html
│   └── svend-log-hours.html
│
├── application.properties         # Main configuration
├── application-local.properties.example
└── schema.sql                    # Database schema
```

## 🎯 Design Principles

### **Separation of Concerns**
- **Controllers**: Handle HTTP requests and responses
- **Services**: Contain business logic and orchestration
- **Repositories**: Handle data access operations
- **Entities**: Represent domain models
- **DTOs**: Transfer data between layers

### **Resource Organization**
- **static/**: Static web assets organized by type and purpose
- **templates/**: Thymeleaf templates organized by functionality
- **components/**: Reusable template fragments
- **layout/**: Base layouts and common pages

### **Package Responsibilities**

#### **🎮 Controllers**
- **admin/**: Administrative functions (employee management, order approval, worklog approval)
- **auth/**: Authentication and registration
- **user/**: User-specific functionality (dashboard, projects, settings)

#### **⚙️ Services**
- Business logic implementation
- Transaction management
- Cross-cutting concerns

#### **🗄️ Repository**
- Data access abstraction
- Custom queries
- Database interactions

#### **📋 Entity**
- JPA entities
- Domain model definitions
- Database table mappings

#### **🎨 Resources**
- **css/**: Stylesheets organized by feature
- **js/**: JavaScript utilities and interactions
- **templates/**: Views organized by user role and functionality
- **components/**: Reusable UI components

## 🚀 Benefits

1. **Scalability**: Easy to add new features in appropriate packages
2. **Maintainability**: Clear separation makes code easier to maintain
3. **Testability**: Each layer can be tested independently
4. **Readability**: Developers can quickly locate relevant code
5. **Team Collaboration**: Different teams can work on different packages
6. **Resource Management**: Organized static assets and templates
7. **Performance**: Optimized CSS and JS loading

## 📝 Naming Conventions

### **Java Classes**
- **Controllers**: End with `Controller` (e.g., `ProjectController`)
- **Services**: End with `Service` (e.g., `ProjectService`)
- **Repositories**: End with `Repository` (e.g., `ProjectRepository`)
- **Entities**: Singular nouns (e.g., `Project`, `User`)
- **DTOs**: End with `DTO` (e.g., `ProjectDTO`)

### **Resources**
- **CSS files**: Lowercase with hyphens (e.g., `admin-dashboard.css`)
- **JS files**: Lowercase with hyphens (e.g., `common-utilities.js`)
- **Templates**: Lowercase with hyphens (e.g., `svend-dashboard.html`)
- **Images**: Descriptive names (e.g., `logo-primary.png`)

## 🔧 Best Practices

1. **Keep packages focused**: Each package should have a single responsibility
2. **Minimize dependencies**: Avoid circular dependencies between packages
3. **Use interfaces**: Program to interfaces, not implementations
4. **Consistent naming**: Follow established naming conventions
5. **Document boundaries**: Clear separation between layers
6. **Resource optimization**: Minify CSS/JS in production
7. **Template organization**: Group related templates together

## 📈 Future Enhancements

- **util/**: Common utilities and helpers
- **security/**: Custom security implementations
- **integration/**: External service integrations
- **notification/**: Email and notification services
- **static/images/icons/**: Icon library
- **static/js/components/**: Reusable JS components
