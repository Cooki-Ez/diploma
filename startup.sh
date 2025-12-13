#!/bin/bash

# Employee Management System Docker Startup Script
# This script starts all services using Docker Compose

echo "Starting Employee Management System..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker first."
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose > /dev/null 2>&1; then
    echo "Error: Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Stop any existing containers
echo "Stopping any existing containers..."
docker-compose down

# Build and start the services
echo "Building and starting services..."
docker-compose up --build -d

# Wait for services to be ready
echo "Waiting for services to be ready..."
sleep 10

# Check if services are running
echo "Checking service status..."
docker-compose ps

echo ""
echo "Employee Management System is now running!"
echo ""
echo "Application URLs:"
echo "  - Main application: http://localhost:8080"
echo "  - Database admin (Adminer): http://localhost:8081"
echo ""
echo "Database connection details:"
echo "  - Host: localhost:5432"
echo "  - Database: baade"
echo "  - Username: baade"
echo "  - Password: passwd123!"
echo ""
echo "To stop the services, run: docker-compose down"
echo "To view logs, run: docker-compose logs -f"
