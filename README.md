# BudgetWise - Personal Finance Management Platform

A cloud-native microservices backend built with Java 21 and Spring Boot, designed to manage budgets, track transactions, and provide financial insights.

## Architecture

```
Client → API Gateway (8080)
↓ RateLimiter + Routing
┌─────┴──────────────────┐
Eureka (8761)           Services
└─────┬──────────────────┘
├── user-service (8081)
├── account-service (8082)
└── transaction-service (8083)
```

## Tech Stack

- **Java 21** + **Spring Boot 3.5**
- **Spring Cloud Gateway** — API Gateway with rate limiting (Bucket4j)
- **Netflix Eureka** — Service discovery and registration
- **OpenFeign** — Declarative inter-service HTTP client
- **Resilience4j** — Circuit breaker with fallback
- **Spring Data JPA** + **MySQL** — Persistence (independent DB per service)
- **Lombok** — Boilerplate reduction

## Key Features

- `@Transactional` — Atomic fund transfers with rollback
- `CompletableFuture` + custom `ThreadPool` — Parallel account aggregation
- `Stream API` — Spending analytics and monthly summaries
- `OpenFeign` — Service-to-service calls via Eureka load balancing
- `CircuitBreaker` — Graceful degradation with fallback responses

## Services

| Service | Port | Database | Responsibility |
|---|---|---|---|
| eureka-server | 8761 | — | Service registry |
| gateway | 8080 | — | Routing, rate limiting |
| user-service | 8081 | budgetwise_user | User management |
| account-service | 8082 | budgetwise_account | Account management, financial summary |
| transaction-service | 8083 | budgetwise_transaction | Transaction tracking, spending analytics |

## Getting Started

### Prerequisites
- Java 21
- MySQL 8+
- Maven

### Setup

1. Create databases:
```sql
CREATE DATABASE budgetwise_user;
CREATE DATABASE budgetwise_account;
CREATE DATABASE budgetwise_transaction;
```

2. Update `application.yml` in each service with your MySQL credentials.

3. Start services in order:
```bash
# 1. Eureka Server
cd eureka && ./mvnw spring-boot:run

# 2. Gateway
cd gateway && ./mvnw spring-boot:run

# 3. Microservices
cd user-service && ./mvnw spring-boot:run
cd account-service && ./mvnw spring-boot:run
cd transaction-service && ./mvnw spring-boot:run
```

## API Endpoints

### User Service
```
POST   /api/users              Create user
GET    /api/users/{id}         Get user by ID
```

### Account Service
```
POST   /api/accounts                        Create account
GET    /api/accounts/user/{userId}          Get user accounts
GET    /api/accounts/user/{userId}/summary  Financial summary (CompletableFuture)
```

### Transaction Service
```
POST   /api/transactions                           Add transaction
GET    /api/transactions/account/{id}/spending     Spending by category (Stream)
GET    /api/transactions/account/{id}/monthly      Monthly summary (Stream)
GET    /api/transactions/user/{userId}/summary     Cross-service summary (OpenFeign)
```

## Future Plans

- Kafka event-driven transaction processing
- Redis caching for account balances
- Spring Security + JWT authentication
- Docker + Kubernetes deployment
