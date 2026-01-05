# Employee Management System

A web application for managing employees, departments, projects, and leave requests using Java Spring Boot, PostgreSQL, Thymeleaf, HTML, CSS, and JavaScript.

## Project Structure

```
diploma/
├── backend/                  # Spring Boot backend application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/        # Java source code
│   │   │   └── resources/   # Configuration and frontend files
│   │   │       ├── static/  # Static CSS and JS files
│   │   │       └── templates/  # Thymeleaf HTML templates
│   ├── Dockerfile           # Backend Docker image
│   ├── pom.xml            # Maven dependencies
│   ├── mvnw              # Maven wrapper (Unix)
│   └── mvnw.cmd          # Maven wrapper (Windows)
├── docker/              # Database initialization scripts
│   └── init.sql         # Database schema and sample data
└── docker-compose.yml    # Multi-service orchestration
```

## Quick Start

### Prerequisites

- Docker and Docker Compose installed on your system

### Using Docker Compose

1. Clone this repository
2. Run the following command to build and start all services:
   ```bash
   docker-compose up --build
   ```
3. The application will be available at:
   - **Application**: http://localhost/
   - **Database Admin (Adminer)**: http://localhost:8081/

4. To stop all services:
   ```bash
   docker-compose down
   ```

5. To remove volumes (including database data):
   ```bash
   docker-compose down -v
   ```

## Services

### Backend Service
- **Port**: 80 (external), 8080 (internal)
- **Technology**: Spring Boot 3.5.7 with Java 17 and Thymeleaf
- **Description**: Full-stack application serving both API endpoints and web pages using Thymeleaf templates

### PostgreSQL Database
- **Port**: 5432
- **Technology**: PostgreSQL 16
- **Database**: baade
- **User/Password**: baade / passwd123!
- **Description**: Stores all application data

### Adminer
- **Port**: 8081
- **Technology**: Adminer (database management tool)
- **Description**: Web-based database administration interface

## Application Features

- **User Authentication**: JWT-based authentication with role-based access control
- **Employee Management**: CRUD operations for employees
- **Department Organization**: Department structure for employees
- **Project Assignment**: Project tracking with employee assignments
- **Leave Request System**:
  - Create leave requests with date selection
  - Display user's projects and points
  - Option to use or not use points
  - Automatic point calculation based on days
- **Leave Evaluation**: Managers and admins can approve/reject requests with comments
- **Performance Points System**: Points for leave requests, monthly increments

## Default Users

The following users are created automatically on first run (see `docker/init.sql`):

1. **Admin User**
   - Email: `admin@company.com`
   - Password: `Admin123!`
   - Roles: ADMIN
   - Points: 100

2. **Manager User**
   - Email: `manager@company.com`
   - Password: `Manager123!`
   - Roles: MANAGER
   - Points: 50

3. **Regular User**
   - Email: `user@company.com`
   - Password: `User123!`
   - Roles: EMPLOYEE
   - Points: 25

## Frontend Pages

### 1. Login Page (`/login`)
- Authenticates users with email and password
- Stores JWT token in localStorage
- Redirects to leave request creation page on success

### 2. Create Leave Request (`/create-leave-request`)
- Select start and end dates
- Displays user's assigned projects
- Provide reasoning for leave request
- Option to not use points for this request
- Shows points that will be deducted (if using points)
- Request (green) and Cancel (red) buttons

### 3. Evaluate Leave Request (`/evaluate-leave-request?id={id}`)
- Displays leave request details (dates with day counts, project, reasoning)
- Comment textbox for manager feedback
- Approve (green) and Reject (red) buttons
- Pre-fills default message on button click ("Approved!" or "Rejected!")

## API Endpoints

### Authentication
- `POST /auth/login` - User login, returns JWT token
- `POST /auth/registration` - User registration

### Employees
- `GET /employees` - Get all employees (filtered by role)
- `GET /employees/{id}` - Get employee by ID
- `GET /employees/current` - Get current logged-in employee
- `POST /employees` - Create new employee (MANAGER/ADMIN only)
- `PATCH /employees/{id}` - Update employee (MANAGER/ADMIN only)
- `DELETE /employees/{id}` - Delete employee (MANAGER/ADMIN only)
- `POST /employees/{id}/add-points` - Add points to employee (MANAGER/ADMIN only)

### Leave Requests
- `GET /leaves` - Get all leave requests (filtered by role)
- `GET /leaves/{id}` - Get leave request by ID
- `POST /leaves` - Create new leave request
- `PATCH /leaves/{id}` - Update leave request (MANAGER/ADMIN only)
- `DELETE /leaves/{id}` - Delete leave request (MANAGER/ADMIN only)
- `GET /leaves/resolved` - Get resolved leave requests

### Projects
- `GET /projects` - Get all projects
- `GET /projects/{id}` - Get project by ID
- `POST /projects` - Create project (MANAGER/ADMIN only)
- `PATCH /projects/{id}` - Update project (MANAGER/ADMIN only)
- `DELETE /projects/{id}` - Delete project (MANAGER/ADMIN only)

### Departments
- `GET /departments` - Get all departments
- `GET /departments/{id}` - Get department by ID
- `POST /departments` - Create department (MANAGER/ADMIN only)
- `PATCH /departments/{id}` - Update department (MANAGER/ADMIN only)
- `DELETE /departments/{id}` - Delete department (MANAGER/ADMIN only)

## Database Schema

The application uses PostgreSQL with the following main tables:

- **employee**: Stores employee information, credentials, and role
- **department**: Stores department information
- **project**: Stores project details and importance levels
- **leave_request**: Stores leave request information with status tracking
- **leave_evaluation**: Stores manager evaluation data
- **project_employee**: Join table for many-to-many relationship
- **employee_project**: Alternative join table (deprecated, use project_employee)

See `docker/init.sql` for complete schema and sample data.

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.5.7, Thymeleaf
- **Frontend**: HTML5, CSS3, JavaScript (ES6+)
- **Database**: PostgreSQL 16
- **ORM**: Hibernate/JPA
- **Containerization**: Docker, Docker Compose
- **Database Administration**: Adminer
- **Build Tool**: Maven
- **Security**: Spring Security with JWT authentication

## Authorization Changes

To simplify the application for frontend implementation, the following changes were made to authorization:

1. **Public Access to UI Pages**: Added `permitAll()` for `/login`, `/create-leave-request`, `/evaluate-leave-request` in `SecurityConfig.java`
2. **Public Access to Static Resources**: Added `permitAll()` for `/css/**` and `/js/**` to serve static files
3. **Current Employee Retrieval**: Added `/employees/current` endpoint to fetch currently logged-in employee's data

**Important**: While UI pages are publicly accessible, all API endpoints still require JWT authentication and enforce role-based access control.

## Frontend Documentation

The frontend uses Thymeleaf templates located in `backend/src/main/resources/templates/`:
- `login.html` - Login page
- `create-leave-request.html` - Leave request creation page
- `evaluate-leave-request.html` - Leave request evaluation page

Static CSS and JavaScript files are located in `backend/src/main/resources/static/`.

## Development

### Backend Development

To develop the backend locally:

1. Ensure Java 17+ and Maven are installed
2. Install PostgreSQL and create database named "baade"
3. Run initialization script:
   ```sql
   \i docker/init.sql
   ```
4. Update database credentials in `backend/src/main/resources/application.properties` if needed
5. Run application:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

### Frontend Development

The frontend is served directly by the Spring Boot application using Thymeleaf templates. No separate development setup is needed - just run the backend and access the web interface.

## Testing

### Backend Tests

Run backend tests:
```bash
cd backend
./mvnw test
```

### Manual Testing

1. Start all services with `docker-compose up --build`
2. Navigate to http://localhost/
3. Login with any default user
4. Create a leave request
5. Note the leave request ID
6. Login as manager/admin
7. Navigate to http://localhost/evaluate-leave-request?id={id}
8. Approve or reject the request

## Troubleshooting

### Port Already in Use

If you see "port is already allocated" error:
- Check what's using the port: `netstat -ano | findstr :80` (Windows) or `lsof -i :80` (Linux/Mac)
- Change the port in `docker-compose.yml` or stop the conflicting service

### Database Connection Issues

- Ensure PostgreSQL container is healthy: `docker-compose ps`
- Check logs: `docker-compose logs postgres`
- Verify database credentials in `docker-compose.yml`

### Backend Compilation Errors

- Ensure Java 17+ is installed: `java -version`
- Clean and rebuild: `docker-compose up --build --force-recreate`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is part of a diploma thesis.
