# Loan Manager

This document covers installation and operation guide for Credit Manager project.

## Overview
The Loan Manager is a RESTful web application designed for managing loans for customers. The system supports creating, listing, and managing loans and loan installments with role-based authorization for `ADMIN` and `CUSTOMER` users.

- **ADMIN** users can manage all customers and loans.
- **CUSTOMER** users can only manage their own loans.

---

## Features
- **Loan Manager**: Create, list, and view loans.
- **Installment Manager**: Automatically generate loan installments.
- **Role-Based Authorization**:
    - `ADMIN` users can perform operations for all customers.
    - `CUSTOMER` users can only access their data.
- **Validation**: Prevents customers from exceeding their credit limit.

---

## Prerequisites
- Java 17 or higher
- Maven 3.6+
- H2 Database (in-memory)
- Postman [attached request](src/main/resources/postman/collections/Credit-Manager.postman_collection.json) or configured Swagger inside application for testing

---

## Setup
### 1. Clone the Repository
```bash
git clone https://github.com/kaydemir/credit-manager.git
cd credit-manager
```

### 2. Build the Application
```bash
mvn clean install
```

### 3. Run the Application
```bash
mvn spring-boot:run
```
The application will start on `http://localhost:8080`.

### 4. Test Database
The application uses an in-memory H2 database. To access the H2 console:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:credit_manager`
- Username: `sa`
- Password: `password`

---

## API Endpoints

### Loans
#### Create a Loan
- **URL**: `POST /api/v1/loans`
- **Description**: Create a loan for a customer.
- **Request Body**:
  ```json
    {
      "customerId": 2,
      "amount": 10000.00,
      "interestRate": 0.5,
      "installments": 6
    }
  ```
- **Response**:
  ```json
  {
    "loanCreated": {
        "id": 1,
        "customer": {
            "id": 2,
            "name": "Jane",
            "surname": "Smith",
            "creditLimit": 15000.00,
            "usedCreditLimit": 15000.00
        },
        "loanAmount": 15000.00,
        "numberOfInstallments": 6,
        "createDate": "2024-12-24",
        "isPaid": false
    }
  }
  ```

#### List Loans
- **URL**: `GET /api/v1/loans/{customerId}`
- **Description**: List loans for a specific customer.
- **Filters** (Optional Query Parameters):
    - `numberOfInstallments`
    - `isPaid`
- **Response**:
  ```json
    {
        "id": 1,
        "customer": {
            "id": 2,
            "name": "Jane",
            "surname": "Smith",
            "creditLimit": 15000.00,
            "usedCreditLimit": 15000.00
        },
        "loanAmount": 15000.00,
        "numberOfInstallments": 6,
        "createDate": "2024-12-24",
        "isPaid": false
    }
  ```

#### Pay a Loan
- **URL**: `POST /api/v1/loans/pay`
- **Description**: Create a loan for a customer.
- **Request Body**:
  ```json
    {
      "loanId": 1,
      "amount": 2500.00
    }
  ```
- **Response**:
  ```json
  {
    "payLoanResult": {
        "installmentsPaid": 3,
        "totalAmountSpent": 14430.00,
        "loanFullyPaid": true
    }
  }
  ```


### Loan Installments
#### List Loan Installments
- **URL**: `GET /api/v1/loans/installments/{loanId}`
- **Description**: Retrieve installments for a specific loan.
- **Response**:
  ```json
  [
     {
        "id": 1,
        "loan": {
            "id": 1,
            "customer": {
                "id": 2,
                "name": "Jane",
                "surname": "Smith",
                "creditLimit": 15000.00,
                "usedCreditLimit": 15000.00
            },
            "loanAmount": 15000.00,
            "numberOfInstallments": 6,
            "createDate": "2024-12-24",
            "isPaid": false
        },
        "amount": 2500.00,
        "paidAmount": 0.00,
        "dueDate": "2025-01-01",
        "paymentDate": null,
        "isPaid": false
    },
    {
        "id": 2,
        "loan": {
            "id": 1,
            "customer": {
                "id": 2,
                "name": "Jane",
                "surname": "Smith",
                "creditLimit": 15000.00,
                "usedCreditLimit": 15000.00
            },
            "loanAmount": 15000.00,
            "numberOfInstallments": 6,
            "createDate": "2024-12-24",
            "isPaid": false
        },
        "amount": 2500.00,
        "paidAmount": 0.00,
        "dueDate": "2025-02-01",
        "paymentDate": null,
        "isPaid": false
    },
    {
        "id": 3,
        "loan": {
            "id": 1,
            "customer": {
                "id": 2,
                "name": "Jane",
                "surname": "Smith",
                "creditLimit": 15000.00,
                "usedCreditLimit": 15000.00
            },
            "loanAmount": 15000.00,
            "numberOfInstallments": 6,
            "createDate": "2024-12-24",
            "isPaid": false
        },
        "amount": 2500.00,
        "paidAmount": 0.00,
        "dueDate": "2025-03-01",
        "paymentDate": null,
        "isPaid": false
    },
    {
        "id": 4,
        "loan": {
            "id": 1,
            "customer": {
                "id": 2,
                "name": "Jane",
                "surname": "Smith",
                "creditLimit": 15000.00,
                "usedCreditLimit": 15000.00
            },
            "loanAmount": 15000.00,
            "numberOfInstallments": 6,
            "createDate": "2024-12-24",
            "isPaid": false
        },
        "amount": 2500.00,
        "paidAmount": 0.00,
        "dueDate": "2025-04-01",
        "paymentDate": null,
        "isPaid": false
    },
    {
        "id": 5,
        "loan": {
            "id": 1,
            "customer": {
                "id": 2,
                "name": "Jane",
                "surname": "Smith",
                "creditLimit": 15000.00,
                "usedCreditLimit": 15000.00
            },
            "loanAmount": 15000.00,
            "numberOfInstallments": 6,
            "createDate": "2024-12-24",
            "isPaid": false
        },
        "amount": 2500.00,
        "paidAmount": 0.00,
        "dueDate": "2025-05-01",
        "paymentDate": null,
        "isPaid": false
    },
    {
        "id": 6,
        "loan": {
            "id": 1,
            "customer": {
                "id": 2,
                "name": "Jane",
                "surname": "Smith",
                "creditLimit": 15000.00,
                "usedCreditLimit": 15000.00
            },
            "loanAmount": 15000.00,
            "numberOfInstallments": 6,
            "createDate": "2024-12-24",
            "isPaid": false
        },
        "amount": 2500.00,
        "paidAmount": 0.00,
        "dueDate": "2025-06-01",
        "paymentDate": null,
        "isPaid": false
    }
  ]
  ```

---

## Security
The application uses role-based security:

### Roles
1. **ADMIN**: Full access to all endpoints and customer data.
2. **CUSTOMER**: Limited to accessing their own data.

### Default Credentials (In-Memory Authentication)
| Username | Password | Role       |
|----------|----------|------------|
| admin    | admin    | ADMIN      |
| customer | customer | CUSTOMER   |

### Security Configuration
The `SecurityConfig` class defines the access rules for endpoints:
- `/api/v1/admin/**` → Accessible only by `ADMIN`
- `/api/v1/customers/**` → Accessible only by `CUSTOMER`

---
