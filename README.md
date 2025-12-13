# Employee Management System

A web application for managing employees, departments, projects, and leave requests using Java Spring Boot and PostgreSQL.

## Architecture

For ease of accessibility the platform was decided to be a web application. The backend has been implemented with Java and Spring Boot, which is a widely-used framework for building enterprise applications. The frontend has been implemented with Thymeleaf, a modern server-side Java template engine. Database of choice is PostgreSQL.

As mentioned before, the focus was put into accessibility and ease of development of the platform. Therefore, all of the crucial parts - backend and database has been containerized using Docker. Docker allows for packaging and running applications regardless of the host environment.

By creating isolated environments for the application developers can run them on any device without the need to set the environment up manually. As Docker comes with a wide variety of so-called images - packages that can be downloaded into an isolated docker system, it is very easy to set up such a reusable system with a single script.

Using Docker, the author prepared scripts with such dedicated environments that can be reused by developers and ultimately ease both the development process and developer collaboration. During the initialization of the platform it is sufficient to run a single startup.sh script to start all services.

## Quick Start

### Prerequisites

- Docker and Docker Compose installed on your system
- Java 25 (for local development without Docker)

### Using Docker (Recommended)

1. Clone this repository
2. Run the startup script:
   
   **For Linux/Mac:**
   ```bash
   ./startup.sh
   ```
   
   **For Windows:**
   ```cmd
   startup.bat
   ```
   
3. The application will be available at:
   - Main application: http://localhost:8080
   - Database admin (Adminer): http://localhost:8081

### Manual Docker Setup

1. Build and start all services:
   ```bash
   docker-compose up --build
   ```

2. To stop the services:
   ```bash
   docker-compose down
   ```

### Local Development

1. Install PostgreSQL and create a database named "baade"
2. Run the initialization script:
   ```sql
   \i docker/init.sql
   ```
3. Update database credentials in `src/main/resources/application.properties`
4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Application Features

- Employee management with role-based access
- Department organization
- Project assignment and tracking
- Leave request system with approval workflow
- Performance evaluation system

## API Endpoints

The application provides RESTful APIs for:

- `/employees` - Employee CRUD operations
- `/departments` - Department management
- `/projects` - Project management
- `/leave-requests` - Leave request operations

## Database Schema

The application uses PostgreSQL with the following main tables:
- Employee: Stores employee information
- Department: Stores department information
- Project: Stores project details
- LeaveRequest: Stores leave request information
- LeaveEvaluation: Stores leave evaluation data
- Project_Employee: Join table for many-to-many relationship

## Technology Stack

- **Backend**: Java 25, Spring Boot 3.5.7
- **Frontend**: Thymeleaf
- **Database**: PostgreSQL 16
- **ORM**: Hibernate/JPA
- **Containerization**: Docker
- **Database Administration**: Adminer
- **Build Tool**: Maven
- **Security**: Spring Security

## Development

### Project Structure

```
src/
├── main/
│   ├── java/pjatk/diploma/s22673/
│   │   ├── config/         # Configuration classes
│   │   ├── controllers/     # REST controllers
│   │   ├── models/          # JPA entities
│   │   ├── repositories/    # Data access layer
│   │   ├── services/        # Business logic
│   │   ├── security/        # Security configuration
│   │   └── util/            # Utility classes
│   └── resources/
│       └── application.properties
└── test/                    # Test classes
```

### Running Tests

```bash
./mvnw test
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is part of a diploma thesis.