# ⚙️ EventHub – Backend

This repository contains the backend of **EventHub – College Event Management and Registration System**.

The backend provides REST APIs for authentication, event management, student registrations, and administrative operations.

## 🚀 Features

- User authentication.
- Role-based access control for Student, Organizer, and Admin.
- Event creation and management.
- Event approval and rejection.
- Student event registration and cancellation.
- Registration information retrieval.
- MySQL database integration.

## 🛠️ Technology Stack

- **Language:** Java
- **Framework:** Spring Boot
- **Build Tool:** Maven
- **Database:** MySQL
- **Persistence:** Spring Data JPA / Hibernate
- **Security:** Spring Security and JWT
- **API Style:** REST

## 📁 Project Structure

```text
eventhub-backend/
├── .mvn/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/eventhub/backend/
│   │   │       ├── controller/
│   │   │       ├── entity/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       └── security/
│   │   └── resources/
│   └── test/
├── mvnw
├── mvnw.cmd
└── pom.xml
```

*Package names and directory organization may vary slightly in the actual implementation.*

## ⚙️ Prerequisites

Install the following:

- Java 21
- MySQL Server
- Git

Maven can be run using the included Maven wrapper.

## 💻 Installation and Setup

### 1. Clone the repository

```bash
git clone https://github.com/Sudheer-pi/eventhub-backend.git
```

### 2. Navigate to the project directory

```bash
cd eventhub-backend
```

### 3. Configure MySQL

Create a MySQL database named:

```sql
CREATE DATABASE eventhub;
```

Configure the database connection using your local `application.properties` file or suitable environment variables.

Example local configuration:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/eventhub
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

Replace the environment variables with the appropriate local database credentials.

**Never commit real database passwords or JWT signing secrets to GitHub.**

### 4. Run the backend

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

On Linux or macOS:

```bash
./mvnw spring-boot:run
```

The application normally starts on:

```text
http://localhost:8080
```

The exact startup configuration depends on the application settings.

## 🔗 Related Repository

**Frontend:** https://github.com/Sudheer-pi/eventhub-frontend

## 🔐 Security

- API access is restricted according to configured user roles.
- Authentication uses JWT-based security.
- Database credentials and signing secrets should be supplied through environment variables or private configuration.
- Production deployments should use HTTPS and a properly secured database.

## 🎯 Project Objective

The backend provides the application logic and data access required to manage college events, student registrations, organizer operations, and administrative activities through REST APIs.

## 👨‍💻 Project Information

**Project Name:** EventHub – College Event Management and Registration System

**Backend Framework:** Spring Boot

**Database:** MySQL
