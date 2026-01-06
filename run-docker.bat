@echo off
REM Leave Management System - Docker Runner Script for Windows
REM This script builds and runs the application using Docker Compose

echo ==========================================
echo Leave Management System - Docker Runner
echo ==========================================
echo.

REM Check if docker-compose is available
where docker-compose >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: docker-compose is not installed or not in PATH
    echo Please install Docker and Docker Compose first
    echo Download from: https://docs.docker.com/compose/install/
    pause
    exit /b 1
)

REM Check if Docker is running
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Docker is not running
    echo Please start Docker first
    pause
    exit /b 1
)

:menu
echo Available commands:
echo 1. Start all services ^(postgres, backend, frontend^)
echo 2. Stop all services
echo 3. Restart all services
echo 4. View logs for all services
echo 5. View logs for backend service
echo 6. View logs for frontend service
echo 7. View logs for postgres service
echo 8. Stop and remove all containers and volumes
echo 9. Build and start all services
echo 0. Exit
echo.
set /p choice=Enter your choice [0-9]: 

if "%choice%"=="1" goto start
if "%choice%"=="2" goto stop
if "%choice%"=="3" goto restart
if "%choice%"=="4" goto logs_all
if "%choice%"=="5" goto logs_backend
if "%choice%"=="6" goto logs_frontend
if "%choice%"=="7" goto logs_postgres
if "%choice%"=="8" goto clean
if "%choice%"=="9" goto build_start
if "%choice%"=="0" goto exit_script

:start
echo Starting all services...
docker-compose up -d
echo.
echo Services started:
echo - PostgreSQL ^(port 5432^)
echo - Backend API ^(port 8080^)
echo - Frontend ^(port 3000^)
echo.
echo Frontend: http://localhost:3000
echo Backend API: http://localhost:8080
echo Adminer: http://localhost:8081
echo.
goto menu

:stop
echo Stopping all services...
docker-compose down
echo All services stopped
goto menu

:restart
echo Restarting all services...
docker-compose restart
echo All services restarted
goto menu

:logs_all
echo Showing logs for all services...
docker-compose logs -f
goto menu

:logs_backend
docker-compose logs -f backend
goto menu

:logs_frontend
docker-compose logs -f frontend
goto menu

:logs_postgres
docker-compose logs -f postgres
goto menu

:clean
echo Stopping and removing all containers and volumes...
docker-compose down -v
echo All containers and volumes removed
goto menu

:build_start
echo Building and starting all services...
docker-compose up -d --build
echo.
echo Services started:
echo - PostgreSQL ^(port 5432^)
echo - Backend API ^(port 8080^)
echo - Frontend ^(port 3000^)
echo.
echo Frontend: http://localhost:3000
echo Backend API: http://localhost:8080
echo Adminer: http://localhost:8081
echo.
goto menu

:exit_script
echo Exiting...
exit /b 0
