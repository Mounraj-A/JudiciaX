# 🏛️ Court AI Backend

> **AI-Powered Trusted Judicial Case Management & Prioritization System**  
> Enterprise-grade Spring Boot 3 backend foundation — production-ready

---

## 📋 Project Overview

The **Court AI Backend** is the server-side foundation for an intelligent judicial case management platform designed to:

- Manage judicial cases across their full lifecycle (filing → disposal)
- Provide **AI-powered case prioritization** (Phase 2)
- Enforce **role-based access control** for Judges, Clerks, Advocates, and Admins
- Maintain a full **audit trail** of all system events
- Support document management with cloud storage integration
- Deliver notifications via in-app, email, and SMS channels

---

## 🛠️ Technology Stack

| Category             | Technology                        | Version    |
|----------------------|-----------------------------------|------------|
| Language             | Java                              | 17 (LTS)   |
| Framework            | Spring Boot                       | 3.3.2      |
| Build Tool           | Apache Maven                      | 3.9+       |
| Database             | PostgreSQL                        | 15+        |
| ORM                  | Spring Data JPA + Hibernate       | 6.x        |
| Security             | Spring Security + JWT (JJWT)      | 0.12.6     |
| DB Migration         | Flyway                            | 10.x       |
| Code Generation      | Lombok + MapStruct                | Latest     |
| API Documentation    | SpringDoc OpenAPI (Swagger UI)    | 2.6.0      |
| Logging              | SLF4J + Logback                   | Bundled    |
| Validation           | Jakarta Bean Validation           | 3.x        |

---

## 📁 Project Structure

```
court-ai-backend/
├── src/
│   ├── main/
│   │   ├── java/com/courtai/
│   │   │   ├── CourtAiBackendApplication.java   ← Entry point
│   │   │   ├── config/                          ← Security, Swagger, CORS, Logging
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── SwaggerConfig.java
│   │   │   │   ├── AuditorAwareImpl.java
│   │   │   │   ├── JwtAuthenticationEntryPoint.java
│   │   │   │   ├── JwtAccessDeniedHandler.java
│   │   │   │   └── RequestLoggingFilter.java
│   │   │   ├── security/                        ← JWT infrastructure
│   │   │   │   ├── jwt/
│   │   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   │   └── JwtAuthenticationFilter.java
│   │   │   │   ├── UserPrincipal.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   ├── common/                          ← Shared classes
│   │   │   │   ├── entity/BaseEntity.java       ← Abstract base entity
│   │   │   │   ├── dto/ApiResponse.java         ← Uniform API wrapper
│   │   │   │   └── enums/                       ← UserRole, CaseStatus, etc.
│   │   │   ├── exception/                       ← Global error handling
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── DuplicateResourceException.java
│   │   │   │   └── BusinessRuleViolationException.java
│   │   │   ├── auth/                            ← Authentication module
│   │   │   │   ├── controller/AuthController.java
│   │   │   │   ├── service/{AuthService, AuthServiceImpl}.java
│   │   │   │   └── dto/{LoginRequest, AuthResponse}.java
│   │   │   ├── user/                            ← User management module
│   │   │   ├── judge/                           ← Judge profile module
│   │   │   ├── clerk/                           ← Clerk profile module
│   │   │   ├── advocate/                        ← Advocate profile module
│   │   │   ├── casefile/                        ← Case management module
│   │   │   ├── document/                        ← Document management module
│   │   │   ├── notification/                    ← Notification module
│   │   │   ├── audit/                           ← Audit trail module
│   │   │   ├── dashboard/                       ← Dashboard module (Phase 2)
│   │   │   └── util/AppUtils.java               ← Utility helpers
│   │   └── resources/
│   │       ├── application.yml                  ← Main config (env vars)
│   │       ├── logback-spring.xml               ← Logging config
│   │       └── db/migration/                    ← Flyway SQL migrations
│   │           ├── V1__Create_Users_Table.sql
│   │           ├── V2__Create_Profile_Tables.sql
│   │           ├── V3__Create_Case_And_Document_Tables.sql
│   │           └── V4__Create_Notification_And_Audit_Tables.sql
│   └── test/
│       ├── java/com/courtai/
│       │   └── CourtAiBackendApplicationTests.java
│       └── resources/
│           └── application-test.yml
├── logs/                                        ← Application log output
├── .env.example                                 ← Environment variable template
├── .gitignore
├── pom.xml
└── README.md
```

---

## 🔑 User Roles

| Role            | Description                                      |
|-----------------|--------------------------------------------------|
| `ROLE_ADMIN`    | Full system access — user management, config     |
| `ROLE_JUDGE`    | Case management, hearing scheduling, verdicts    |
| `ROLE_CLERK`    | Case filing, document management                 |
| `ROLE_ADVOCATE` | Case viewing, document submission, representation |

---

## 🔐 Authentication Flow

```
Client → POST /api/v1/auth/login  { email, password }
       ← 200 OK { accessToken, refreshToken, expiresIn, ... }

Client → Any protected endpoint
       → Header: Authorization: Bearer <accessToken>
       ← 200 OK (if authorized)
       ← 401 Unauthorized (missing/invalid token)
       ← 403 Forbidden (insufficient role)
```

---

## ⚙️ Environment Variables

| Variable       | Description                          | Default       |
|----------------|--------------------------------------|---------------|
| `DB_HOST`      | PostgreSQL host                      | `localhost`   |
| `DB_PORT`      | PostgreSQL port                      | `5432`        |
| `DB_NAME`      | Database name                        | `courtai_db`  |
| `DB_USERNAME`  | Database username                    | `postgres`    |
| `DB_PASSWORD`  | Database password                    | _(required)_  |
| `JWT_SECRET`   | JWT signing secret (256-bit min)     | _(required)_  |

---

## 🚀 How to Run

### Prerequisites

| Software       | Minimum Version | Download                                              |
|----------------|-----------------|-------------------------------------------------------|
| Java JDK       | 17 (LTS)        | [adoptium.net](https://adoptium.net)                  |
| Apache Maven   | 3.9+            | [maven.apache.org](https://maven.apache.org)          |
| PostgreSQL     | 15+             | [postgresql.org](https://www.postgresql.org)          |
| Git            | 2.x+            | [git-scm.com](https://git-scm.com)                   |

### Step 1 — Create PostgreSQL Database

```sql
CREATE DATABASE courtai_db;
CREATE USER courtai_user WITH ENCRYPTED PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE courtai_db TO courtai_user;
```

### Step 2 — Configure Environment Variables

```bash
# Copy the template
cp .env.example .env

# Edit .env with your actual values
# Generate JWT secret:
openssl rand -hex 32
```

### Step 3 — Run the Application

**Option A — Using environment variables directly:**
```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=courtai_db
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
export JWT_SECRET=your_256_bit_hex_secret

mvn spring-boot:run
```

**Option B — IntelliJ IDEA / VS Code:**
1. Create a run configuration
2. Set environment variables in the run config
3. Run `CourtAiBackendApplication.main()`

### Step 4 — Verify Startup

```bash
# Health check
curl http://localhost:8080/api/v1/actuator/health

# Swagger UI
open http://localhost:8080/api/v1/swagger-ui.html
```

---

## 📊 API Documentation

Once running, the interactive Swagger UI is available at:

```
http://localhost:8080/api/v1/swagger-ui.html
```

OpenAPI JSON spec:
```
http://localhost:8080/api/v1/api-docs
```

---

## 🗄️ Database Migrations

Flyway automatically runs pending migrations on application startup.

| Migration | Description                                    |
|-----------|------------------------------------------------|
| V1        | Users table                                    |
| V2        | Judge, Clerk, Advocate profile tables          |
| V3        | Case files and document tables                 |
| V4        | Notifications, audit logs, default admin user  |

**Default Admin Credentials (change immediately in production):**
- Email: `admin@courtai.com`
- Password: `Admin@123!`

---

## 🧪 Running Tests

```bash
# Run all tests (uses H2 in-memory database)
mvn test

# Skip tests during build
mvn clean install -DskipTests

# Run specific test class
mvn test -Dtest=CourtAiBackendApplicationTests
```

---

## 📝 Logging

Logs are written to:

| File                              | Content          | Retention |
|-----------------------------------|------------------|-----------|
| `logs/court-ai-backend.log`       | All application  | 30 days   |
| `logs/court-ai-backend-error.log` | Errors only      | 90 days   |
| `logs/court-ai-backend-audit.log` | Audit events     | 365 days  |

---

## 🏗️ Architecture Principles

- **SOLID Principles** — Single responsibility, interface-based service contracts
- **Constructor Injection** — No field injection (`@Autowired` on fields) anywhere
- **DTO Pattern** — Entities never exposed directly in API responses
- **Repository Pattern** — Data access only through repositories
- **Service Layer** — All business logic in services, controllers are thin
- **Soft Delete** — Records are never physically deleted (`isDeleted` flag)
- **Audit Trail** — All significant events recorded in `audit_logs`

---

## 🛣️ Development Roadmap

| Phase | Description                                      | Status    |
|-------|--------------------------------------------------|-----------|
| 1     | Backend Foundation (this release)                | ✅ Complete |
| 2     | Case Management CRUD APIs                        | 🔜 Planned |
| 3     | AI Prioritization Engine                         | 🔜 Planned |
| 4     | Document Management (Cloud Storage)              | 🔜 Planned |
| 5     | Notification Service                             | 🔜 Planned |
| 6     | Dashboard & Analytics                            | 🔜 Planned |
| 7     | Frontend (React/Angular)                         | 🔜 Planned |

---

## 📄 License

This project is licensed under the MIT License.

---

*Developed as a Final Year Project — AI-Powered Trusted Judicial Case Management & Prioritization System*
