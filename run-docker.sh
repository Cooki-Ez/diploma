#!/bin/bash

# Leave Management System - Docker Runner Script
# This script builds and runs the application using Docker Compose

set -e

echo "=========================================="
echo "Leave Management System - Docker Runner"
echo "=========================================="
echo ""

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null
then
    echo "Error: docker-compose is not installed or not in PATH"
    echo "Please install Docker and Docker Compose first"
    echo "Download from: https://docs.docker.com/compose/install/"
    exit 1
fi

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running"
    echo "Please start Docker first"
    exit 1
fi

# Function to show menu
show_menu() {
    echo "Available commands:"
    echo "  1) Start all services (postgres, backend, frontend)"
    echo "  2) Stop all services"
    echo "  3) Restart all services"
    echo "  4) View logs for all services"
    echo "  5) View logs for backend service"
    echo "  6) View logs for frontend service"
    echo "  7) View logs for postgres service"
    echo "  8) Stop and remove all containers and volumes"
    echo "  9) Build and start all services"
    echo "  0) Exit"
    echo ""
    read -p "Enter your choice [0-9]: " choice
}

# Start all services
start_services() {
    echo "Starting all services..."
    docker-compose up -d
    echo ""
    echo "Services started:"
    echo "  - PostgreSQL (port 5432)"
    echo "  - Backend API (port 8080)"
    echo "  - Frontend (port 3000)"
    echo ""
    echo "Frontend: http://localhost:3000"
    echo "Backend API: http://localhost:8080"
    echo "Adminer: http://localhost:8081"
    echo ""
}

# Stop all services
stop_services() {
    echo "Stopping all services..."
    docker-compose down
    echo "All services stopped"
}

# Restart all services
restart_services() {
    echo "Restarting all services..."
    docker-compose restart
    echo "All services restarted"
}

# View logs for all services
view_logs_all() {
    docker-compose logs -f
}

# View logs for specific service
view_logs_service() {
    docker-compose logs -f "$1"
}

# Stop and remove all containers and volumes
clean_all() {
    echo "Stopping and removing all containers and volumes..."
    docker-compose down -v
    echo "All containers and volumes removed"
}

# Build and start all services
build_and_start() {
    echo "Building and starting all services..."
    docker-compose up -d --build
    echo ""
    echo "Services started:"
    echo "  - PostgreSQL (port 5432)"
    echo "  - Backend API (port 8080)"
    echo "  - Frontend (port 3000)"
    echo ""
    echo "Frontend: http://localhost:3000"
    echo "Backend API: http://localhost:8080"
    echo "Adminer: http://localhost:8081"
    echo ""
}

# Main menu loop
while true; do
    clear
    echo "=========================================="
    echo "Leave Management System - Docker Runner"
    echo "=========================================="
    echo ""
    echo "Current status:"
    docker-compose ps
    echo ""
    show_menu

    case $choice in
        1)
            start_services
            ;;
        2)
            stop_services
            ;;
        3)
            restart_services
            ;;
        4)
            view_logs_all
            ;;
        5)
            view_logs_service backend
            ;;
        6)
            view_logs_service frontend
            ;;
        7)
            view_logs_service postgres
            ;;
        8)
            clean_all
            ;;
        9)
            build_and_start
            ;;
        0)
            echo "Exiting..."
            exit 0
            ;;
        *)
            echo "Invalid choice. Please try again."
            ;;
    esac

    read -p "Press Enter to continue..."
done
