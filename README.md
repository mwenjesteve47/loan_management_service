# Loan Management Service API

## Overview
The Loan Management Service API handles loan creation, retrieval, state updates, and repayments.

## Features
- **Loan Processing**
    - Create loans (either lump sum or installment-based)
    - Retrieve loans with filtering options
- **Loan State Management**
    - Update loan state
- **Loan Repayment Management**
    - Process loan repayments

## Technology Stack
- **Java 21**
- **Spring Boot 3.3.4**
- **MySQL**

## API Endpoints

### Loan Processing
- **Create Loan**
    - `POST /loans`
    - **Request Body:** `LoanRequest`
    - **Response:** Returns created loan details

- **Retrieve Loans**
    - `GET /loans`
    - **Query Params:** Filters from `LoanFilterRequest`
    - **Response:** List of loans

### Loan State Management
- **Update Loan State**
    - `PATCH /loans/{loanId}/state`
    - **Path Param:** `loanId` (Loan ID)
    - **Request Param:** `newState` (New loan state)
    - **Response:** Success message

### Loan Repayment Management
- **Repay Loan**
    - `POST /loans/{loanId}/repay`
    - **Path Param:** `loanId` (Loan ID)
    - **Request Body:** `LoanRepaymentRequest`
    - **Response:** Returns repayment details

## Sample Request & Response

### Create Loan Request
```json
{
  "customerId": 1,
  "amount": 5000,
  "loanType": "INSTALLMENT",
  "interestRate": 5.0,
  "duration": 12,
  "startDate": "2025-01-01"
}
```

### Running the Application
Ensure Java 21 is installed.
Run the application using:

`mvn spring-boot:run`

The service will start on `http://localhost:8083`.

## Docker Setup

To containerize the application, follow these steps:

### Build the JAR file:
```sh
mvn clean package -DskipTests
```

### Create a `Dockerfile` in the project root:
```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/loan-management-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build and run the Docker container:
```sh
docker build -t loan-management-service .
docker run -p 8080:8080 loan-management-service
```

