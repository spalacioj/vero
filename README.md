# Vero

Vero is a personal finance tracker for managing accounts, transactions, and spending insights.

## Run locally

### Prerequisites

- Java 21
- Docker Desktop

### 1. Create your local environment file

From the repository root:

```powershell
Copy-Item .env.example .env
```

Open `.env` and replace the placeholder database name, user, and password.

### 2. Start the database

```powershell
docker compose up -d
```

### 3. Start the application

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

The API starts at `http://localhost:8080`.

### Run tests

From the `backend` folder:

```powershell
.\mvnw.cmd clean test
```

## Project structure

```text
backend/  Spring Boot API and database migrations
docs/     Product requirements and backlog
frontend/ Future React application
```

`frontend/` will be created when the first user-interface slice begins.
