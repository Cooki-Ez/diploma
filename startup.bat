@echo off
REM Employee Management System Docker Startup Script for Windows
REM This script starts all services using Docker Compose

echo Starting Employee Management System...

REM Check if Docker is running
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Docker is not running. Please start Docker first.
    pause
    exit /b 1
)

REM Check if Docker Compose is available
docker-compose --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Docker Compose is not installed. Please install Docker Compose first.
    pause
    exit /b 1
)

REM Stop any existing containers
echo Stopping any existing containers...
docker-compose down

REM Build and start the services
echo Building and starting services...
docker-compose up --build -d

REM Wait for services to be ready
echo Waiting for services to be ready...
timeout /t 10 /nobreak >nul

REM Check if services are running
echo Checking service status...
docker-compose ps

echo.
echo Employee Management System is now running!
echo.
echo Application URLs:
echo   - Main application: http://localhost:8080
echo   - Database admin (Adminer): http://localhost:8081
echo.
echo Database connection details:
echo   - Host: localhost:5432
echo   - Database: baade
echo   - Username: baade
echo   - Password: passwd123!
echo.
echo To stop the services, run: docker-compose down
echo To view logs, run: docker-compose logs -f
pause