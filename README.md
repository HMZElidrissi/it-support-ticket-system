# IT Support Ticket System

A simple ticket management application that allows employees to report and track IT issues.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Setup & Installation](#setup--installation)
    - [Backend Setup](#backend-setup)
    - [Frontend Setup](#frontend-setup)
    - [Database Setup](#database-setup)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Security](#security)
- [Contributing](#contributing)
- [License](#license)

## Overview

The IT Support Ticket System is a full-stack application designed to streamline the process of reporting and resolving IT issues within an organization. The system consists of a Spring Boot backend with a RESTful API and a Java Swing desktop client.

## Architecture

### Backend
- **Language**: Java 17
- **Framework**: Spring Boot 3.x
- **Security**: Spring Security with JWT authentication
- **Database**: Oracle Database
- **Migration**: Flyway
- **Build Tool**: Maven

### Frontend
- **Framework**: Java Swing
- **Layout Manager**: MigLayout for responsive UI
- **HTTP Client**: Java HTTP Client for API communication

## Prerequisites

- JDK 17 or higher
- Maven 3.6 or higher
- Oracle Database 19c or higher
- Git

## Setup & Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/HMZElidrissi/it-support-ticket-system.git
   cd it-support-ticket-system/backend
   ```
2. **Backend Setup**: The project includes a `docker-compose.yml` file that sets up both the Oracle database and the Spring Boot backend.
    ```bash
   docker-compose up -d
    ```
   This command will:
    - Start an Oracle database container
    - Build and start the Spring Boot backend container
    - Apply database migrations automatically via Flyway
    - Expose the backend API on port 8080
   
3. **Frontend Setup**: The frontend is a Java Swing application. You can run it directly from your IDE or package it into a JAR file.
    ```bash
   cd ../frontend
   mvn clean package
   java -jar target/frontend-1.0.0.jar
    ```

## Database Migration

The system uses Flyway for database schema migration. Migration scripts are located in:
```
backend/src/main/resources/db/migration
```
- `V1__init.sql` - Initial schema
- `V2__seed.sql` - Seed data

These migrations are automatically applied when the backend container starts.

## Usage

### User Authentication

1. **Register as an Employee**
    - Launch the desktop application
    - Click "Don't have an account? Sign up!"
    - Fill in your details and submit

2. **Login**
    - Enter your email and password
    - The system will automatically detect your role (Employee or IT Support)

### For Employees

1. **Creating a Ticket**
    - Click on "Create Ticket" button
    - Fill in the ticket details (title, description, priority, category)
    - Submit the ticket

2. **Viewing Your Tickets**
    - All your tickets are displayed in the main dashboard
    - Double-click on a ticket to view its details
    - You can see the status and any comments from IT Support

### For IT Support Staff

1. **Managing Tickets**
    - View all tickets in the system
    - Filter tickets by status (New, In Progress, Resolved)
    - Double-click on a ticket to view its details

2. **Updating Ticket Status**
    - From the ticket details view, use the action buttons to:
        - Start working on a ticket (change to In Progress)
        - Mark as Resolved
        - Reopen a ticket if needed

3. **Adding Comments**
    - Add comments to communicate with the employee who submitted the ticket
    - All comments are timestamped and tracked

## API Documentation

API documentation is available through Swagger UI at http://localhost:8080/swagger-ui/index.html when the backend is running.

### Key Endpoints

- **Authentication**
    - `POST /api/v1/auth/signup` - Register a new user
    - `POST /api/v1/auth/signin` - Authenticate a user

- **Tickets**
    - `GET /api/v1/tickets` - Get all tickets (IT Support)
    - `GET /api/v1/tickets/user` - Get user's tickets (Employee)
    - `GET /api/v1/tickets/{id}` - Get a specific ticket
    - `POST /api/v1/tickets` - Create a new ticket
    - `PUT /api/v1/tickets/{id}/status` - Update ticket status

- **Comments**
    - `GET /api/v1/tickets/{id}/comments` - Get comments for a ticket
    - `POST /api/v1/tickets/{id}/comments` - Add a comment to a ticket

## Security

- **JWT Authentication**: The system uses JWT tokens for secure authentication
- **Role-Based Access**: Different roles have different access levels
- **Password Encryption**: User passwords are securely hashed using BCrypt
