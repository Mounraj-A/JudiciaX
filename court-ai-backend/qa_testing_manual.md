# JudiciaX — Backend QA Testing Manual

> **Project**: AI-Powered Trusted Judicial Case Management & Prioritization System
> **Base URL**: `http://localhost:8080/api/v1`
> **Swagger UI**: `http://localhost:8080/api/v1/swagger-ui/index.html`
> **Authentication**: ⚠️ **DISABLED** — `app.security.enabled=false` — all endpoints publicly accessible, no JWT required
> **Database**: PostgreSQL 18.4 @ `localhost:5432/courtai_db`
> **DB Access**: `psql -U <user> -d courtai_db`

---

## Global Rules Before Testing

```sql
-- Confirm dev mode (security disabled)
SELECT current_timestamp;  -- just verifying connection

-- Count all tables
SELECT schemaname, count(*) FROM pg_tables WHERE schemaname = 'public' GROUP BY schemaname;

-- Verify Flyway migrations
SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;
-- Expected: 17 rows, all success = true
```

**Standard API Response Envelope:**
```json
{
  "success": true,
  "message": "...",
  "data": { ... },
  "timestamp": "2026-07-11T02:30:00Z"
}
```

---

---

# MODULE 1 — AUTHENTICATION

## 1. Purpose
Handles user registration, login, JWT token management, email/OTP verification, and password reset.

## 2. Preconditions
- Application running on port 8080
- PostgreSQL accessible
- No auth token needed (security disabled)
- SMTP server not required (verification tokens returned in response body during dev mode)

## 3. Required Database Records
```sql
-- Before starting: verify tables exist
\dt users
\dt refresh_tokens
\dt user_sessions
\dt password_reset_tokens
\dt email_verification_tokens
\dt mobile_otps
\dt login_history
```

## 4. API Execution Order

```
1. POST /auth/register          → create account
2. POST /auth/verify-email      → verify email
3. POST /auth/login             → get JWT tokens
4. POST /auth/send-otp          → send mobile OTP
5. POST /auth/verify-otp        → verify OTP
6. POST /auth/refresh           → rotate tokens
7. POST /auth/forgot-password   → get reset token
8. POST /auth/reset-password    → change password
9. POST /auth/logout            → invalidate session
```

---

## 5. API: POST /auth/register

| Field | Value |
|---|---|
| **Endpoint** | `POST /api/v1/auth/register` |
| **Purpose** | Register a new advocate account |
| **HTTP Status** | 201 Created |
| **DB Tables** | `users`, `email_verification_tokens` |

### Request
```json
{
  "fullName": "Ravi Kumar",
  "email": "ravi.kumar@lawfirm.in",
  "username": "ravi.kumar",
  "phoneNumber": "+919876543210",
  "password": "SecurePass@123",
  "role": "ADVOCATE"
}
```

### Expected Response (201)
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "uuid": "550e8400-e29b-41d4-a716-446655440000",
    "email": "ravi.kumar@lawfirm.in",
    "username": "ravi.kumar",
    "fullName": "Ravi Kumar",
    "role": "ADVOCATE"
  }
}
```

### Before Insert (PostgreSQL)
```sql
SELECT count(*) FROM users WHERE email = 'ravi.kumar@lawfirm.in';
-- Expected: 0
```

### After Insert Verification
```sql
-- Verify user created
SELECT id, uuid, full_name, email, username, role, account_status, is_deleted, created_at
FROM users WHERE email = 'ravi.kumar@lawfirm.in';

-- Expected columns:
-- role = 'ADVOCATE'
-- account_status = 'PENDING_VERIFICATION' (or 'ACTIVE' if auto-verified)
-- is_deleted = false
-- password NOT stored in plain text (starts with '$2a$' for BCrypt)

-- Verify password is hashed
SELECT SUBSTRING(password, 1, 4) AS hash_prefix FROM users WHERE email = 'ravi.kumar@lawfirm.in';
-- Expected: '$2a$'

-- Verify verification token created
SELECT token, expires_at, is_used FROM email_verification_tokens
WHERE user_id = (SELECT id FROM users WHERE email = 'ravi.kumar@lawfirm.in');
```

### Validation Rules
| Rule | Value |
|---|---|
| email | Valid format, unique |
| username | 3-50 chars, unique |
| password | Min 8 chars, must contain uppercase + number + special char |
| phoneNumber | Valid format, unique |
| role | Must be valid `UserRole` enum value |

### Negative Test Cases

| Test | Input | Expected Response |
|---|---|---|
| Duplicate email | Same email twice | 409 Conflict |
| Invalid email | `notanemail` | 400 Bad Request — "Email must be valid" |
| Weak password | `password` | 400 Bad Request — validation error |
| Missing fields | `{}` | 400 Bad Request — field errors |
| Duplicate username | Same username | 409 Conflict |

### Swagger Steps
1. Open Swagger UI → Tag: **Authentication**
2. Click `POST /auth/register` → Try it out
3. Paste request body → Execute
4. Verify 201 status
5. Copy `uuid` from response

---

## 6. API: POST /auth/verify-email

| Field | Value |
|---|---|
| **Endpoint** | `POST /api/v1/auth/verify-email` |
| **Purpose** | Confirm email address using verification token |
| **HTTP Status** | 200 OK |

### Get Token from DB
```sql
SELECT token FROM email_verification_tokens
WHERE user_id = (SELECT id FROM users WHERE email = 'ravi.kumar@lawfirm.in')
  AND is_used = false
ORDER BY created_at DESC LIMIT 1;
```

### Request
```json
{ "token": "<token-from-db>" }
```

### After Verify
```sql
-- Token marked used
SELECT is_used FROM email_verification_tokens WHERE token = '<token>';
-- Expected: true

-- User status updated
SELECT account_status FROM users WHERE email = 'ravi.kumar@lawfirm.in';
-- Expected: 'ACTIVE'
```

### Negative Tests
| Test | Expected |
|---|---|
| Expired token | 400 — "Token has expired" |
| Already used token | 400 — "Token already used" |
| Invalid token | 400 — "Invalid token" |

---

## 7. API: POST /auth/login

| Field | Value |
|---|---|
| **Endpoint** | `POST /api/v1/auth/login` |
| **Purpose** | Authenticate and receive JWT token pair |
| **HTTP Status** | 200 OK |
| **DB Tables** | `refresh_tokens`, `user_sessions`, `login_history` |

### Request
```json
{
  "identifier": "ravi.kumar@lawfirm.in",
  "password": "SecurePass@123"
}
```

### Expected Response
```json
{
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 900
  }
}
```

### After Login Verification
```sql
-- Refresh token stored
SELECT token_hash, is_revoked, expires_at FROM refresh_tokens
WHERE user_id = (SELECT id FROM users WHERE email = 'ravi.kumar@lawfirm.in')
ORDER BY created_at DESC LIMIT 1;

-- Session created
SELECT session_token_hash, is_active, ip_address FROM user_sessions
WHERE user_id = (SELECT id FROM users WHERE email = 'ravi.kumar@lawfirm.in')
ORDER BY created_at DESC LIMIT 1;

-- Login history recorded
SELECT ip_address, success, created_at FROM login_history
WHERE user_id = (SELECT id FROM users WHERE email = 'ravi.kumar@lawfirm.in')
ORDER BY created_at DESC LIMIT 1;
-- Expected: success = true
```

### Negative Tests
| Test | Expected |
|---|---|
| Wrong password | 401 — "Invalid credentials" |
| Non-existent email | 401 — "Invalid credentials" |
| Locked account | 401 — "Account is locked" |
| Unverified email | 401 — "Email not verified" |

---

## 8. API: POST /auth/refresh

| Field | Value |
|---|---|
| **Endpoint** | `POST /api/v1/auth/refresh` |
| **Purpose** | Rotate refresh token, get new token pair |
| **HTTP Status** | 200 OK |

### Request
```json
{ "refreshToken": "<refresh-token-from-login>" }
```

### DB Verification
```sql
-- Old token should be revoked
SELECT is_revoked FROM refresh_tokens ORDER BY created_at DESC LIMIT 2;
-- Expected: [false (new), true (old)]
```

### Negative Tests
| Test | Expected |
|---|---|
| Revoked token | 401 — "Token revoked" |
| Expired token | 401 — "Token expired" |
| Invalid format | 400 |

---

## 9. API: POST /auth/forgot-password

```json
{ "email": "ravi.kumar@lawfirm.in" }
```

```sql
-- Get reset token
SELECT token, expires_at, is_used FROM password_reset_tokens
WHERE user_id = (SELECT id FROM users WHERE email = 'ravi.kumar@lawfirm.in')
ORDER BY created_at DESC LIMIT 1;
```

---

## 10. API: POST /auth/reset-password

```json
{
  "token": "<reset-token-from-db>",
  "newPassword": "NewSecure@456",
  "confirmPassword": "NewSecure@456"
}
```

### Negative Tests
| Test | Expected |
|---|---|
| Passwords don't match | 400 — "Passwords do not match" |
| Expired token | 400 |
| Weak password | 400 |

---

## 11. API: POST /auth/logout

```json
{}
```
No body required. Invalidates current session.

```sql
-- Session should be inactive
SELECT is_active FROM user_sessions
WHERE user_id = (SELECT id FROM users WHERE email = 'ravi.kumar@lawfirm.in')
ORDER BY created_at DESC LIMIT 1;
-- Expected: false
```

---

## 12. Auth Success Criteria
- [x] User created with hashed password
- [x] Email verification token generated
- [x] Login returns two valid JWTs
- [x] Refresh token rotated on use
- [x] Old refresh token revoked after rotation
- [x] Session created and tracked
- [x] Login history recorded
- [x] Logout invalidates session

## 13. Common Failure Reasons
- Email SMTP not configured → verification token visible only in DB
- Rate limiting triggered → wait 60s or restart app
- Password validation not passing → must include uppercase + number + special char

---

---

# MODULE 2 — USER

## 1. Purpose
Manage user profiles, sessions, password changes, and privacy settings.

## 2. Base Path
`/api/v1/users`

## 3. Preconditions
- At least one user registered and verified (from Module 1)
- Admin user must exist for admin operations

## 4. Required DB Records
```sql
-- Ensure admin user exists
SELECT id, uuid, email, role FROM users WHERE role = 'ADMIN' AND is_deleted = false LIMIT 1;

-- If no admin: create one via register with role=ADMIN
```

---

## 5. API: POST /users (Admin — Create User)

| Field | Value |
|---|---|
| **Endpoint** | `POST /api/v1/users` |
| **Auth Required** | ROLE_ADMIN |
| **HTTP Status** | 201 Created |

### Request
```json
{
  "fullName": "Priya Sharma",
  "email": "priya.clerk@court.in",
  "username": "priya.clerk",
  "phoneNumber": "+919888777666",
  "password": "ClerkPass@123",
  "role": "CLERK"
}
```

### Before/After
```sql
-- Before
SELECT count(*) FROM users WHERE email = 'priya.clerk@court.in';

-- After
SELECT uuid, full_name, email, role, account_status FROM users
WHERE email = 'priya.clerk@court.in';
```

---

## 6. API: GET /users/{uuid}

| Field | Value |
|---|---|
| **Endpoint** | `GET /api/v1/users/{uuid}` |
| **Auth** | ADMIN, JUDGE, CLERK |
| **HTTP Status** | 200 OK |

### Swagger Steps
1. Copy UUID from any user in DB:
```sql
SELECT uuid FROM users WHERE is_deleted = false LIMIT 1;
```
2. Paste in `{uuid}` path param → Execute

### Negative Tests
| Test | Expected |
|---|---|
| Non-existent UUID | 404 — "User not found" |
| Soft-deleted user UUID | 404 |

---

## 7. API: GET /users (Admin — All Users)

Returns all non-deleted users.

```sql
-- Verify count matches
SELECT count(*) FROM users WHERE is_deleted = false;
```

---

## 8. API: GET /users/me

Returns authenticated user's profile. Since auth is disabled, may return error — use Admin endpoint instead.

---

## 9. API: PUT /users/me (Update Profile)

### Request
```json
{
  "fullName": "Ravi Kumar Updated",
  "phoneNumber": "+919876543211"
}
```

### Before/After
```sql
-- Before
SELECT full_name, phone_number, updated_at FROM users WHERE email = 'ravi.kumar@lawfirm.in';

-- After
SELECT full_name, phone_number, updated_at FROM users WHERE email = 'ravi.kumar@lawfirm.in';
-- updated_at should be newer timestamp
```

---

## 10. API: PUT /users/change-password

### Request
```json
{
  "currentPassword": "SecurePass@123",
  "newPassword": "NewPass@789",
  "confirmPassword": "NewPass@789"
}
```

### Validation
```sql
-- Password should be re-hashed
SELECT SUBSTRING(password, 1, 4) FROM users WHERE email = 'ravi.kumar@lawfirm.in';
-- Expected: '$2a$'
```

### Negative Tests
| Test | Expected |
|---|---|
| Wrong current password | 400 |
| New passwords don't match | 400 |
| Same as current password | 400 |

---

## 11. API: DELETE /users/{uuid} (Admin — Soft Delete)

### Before Delete
```sql
SELECT is_deleted, deleted_at FROM users WHERE uuid = '<uuid>';
-- Expected: is_deleted = false
```

### After Delete
```sql
SELECT is_deleted, deleted_at FROM users WHERE uuid = '<uuid>';
-- Expected: is_deleted = true, deleted_at IS NOT NULL
```

### Negative Tests
| Test | Expected |
|---|---|
| Delete own admin account | 400 or 403 |
| Already deleted | 404 |

---

## 12. Session Management APIs

```
GET    /users/me/sessions              → list my sessions
DELETE /users/me/sessions/{sessionUuid} → revoke one session
DELETE /users/me/sessions              → revoke all sessions
```

```sql
-- Verify session after revoke
SELECT session_token_hash, is_active FROM user_sessions
WHERE user_id = (SELECT id FROM users WHERE email = 'ravi.kumar@lawfirm.in');
-- is_active should be false after revoke
```

---

## 13. User Module Success Criteria
- [x] Admin can create users with any role
- [x] Profile update changes `full_name`, `phone_number`, `updated_at`
- [x] Soft delete sets `is_deleted = true`, does NOT remove row
- [x] Password change updates bcrypt hash
- [x] Session revoke sets `is_active = false`

---

---

# MODULE 3 — RBAC (Roles & Permissions)

## 1. Purpose
Manage roles and permission assignments for fine-grained access control.

## 2. Base Path
`/api/v1/rbac/roles`

## 3. Required DB Records
```sql
-- Verify system roles exist
SELECT id, uuid, name, display_name, is_system FROM roles WHERE is_deleted = false;
-- Expected: ADMIN, JUDGE, CLERK, ADVOCATE (at minimum)

-- Verify permissions exist
SELECT code, name FROM permissions WHERE is_deleted = false ORDER BY code;
-- Expected: MANAGE_USERS, VIEW_CASES, REGISTER_CASES, SYSTEM_CONFIGURATION, etc.
```

---

## 4. API: GET /rbac/roles

### Request
```
GET /api/v1/rbac/roles
```

### Expected Response
```json
{
  "data": [
    { "uuid": "...", "name": "ADMIN", "displayName": "Administrator", "isSystem": true },
    { "uuid": "...", "name": "CLERK", "displayName": "Court Clerk", "isSystem": true }
  ]
}
```

### DB Verification
```sql
SELECT uuid, name, display_name, is_system, is_deleted FROM roles ORDER BY name;
```

---

## 5. API: GET /rbac/roles/{uuid}

```sql
-- Get role UUID first
SELECT uuid, name FROM roles WHERE name = 'ADVOCATE' AND is_deleted = false;
```

Expected: Full role object with all permissions array.

---

## 6. API: GET /rbac/roles/{uuid}/permissions

```sql
-- Verify permissions linked to role
SELECT p.code, p.name FROM role_permissions rp
JOIN permissions p ON rp.permission_id = p.id
JOIN roles r ON rp.role_id = r.id
WHERE r.name = 'ADVOCATE' AND rp.is_deleted = false;
```

---

## 7. API: POST /rbac/roles (Create Custom Role)

### Request
```json
{
  "name": "SENIOR_CLERK",
  "displayName": "Senior Court Clerk",
  "description": "Senior clerk with extended permissions"
}
```

### Before/After
```sql
-- Before
SELECT count(*) FROM roles WHERE name = 'SENIOR_CLERK';

-- After
SELECT id, uuid, name, display_name, is_system FROM roles WHERE name = 'SENIOR_CLERK';
-- is_system should be false (custom role)
```

### Negative Tests
| Test | Expected |
|---|---|
| Duplicate role name | 409 Conflict |
| Empty name | 400 |
| Name > 50 chars | 400 |

---

## 8. API: PUT /rbac/roles/{uuid}

### Request
```json
{
  "name": "SENIOR_CLERK",
  "displayName": "Senior Clerk — Updated",
  "description": "Updated description"
}
```

### Negative Tests
| Test | Expected |
|---|---|
| Update system role name | 400 — system roles protected |
| Non-existent UUID | 404 |

---

## 9. API: POST /rbac/roles/{uuid}/permissions (Assign Permission)

### Request
```json
{ "permissionCode": "VIEW_CASES" }
```

```sql
-- Verify assignment
SELECT r.name, p.code FROM role_permissions rp
JOIN roles r ON rp.role_id = r.id
JOIN permissions p ON rp.permission_id = p.id
WHERE r.uuid = '<role-uuid>' AND p.code = 'VIEW_CASES';
```

### Negative Tests
| Test | Expected |
|---|---|
| Duplicate assignment | 200 (idempotent) |
| Invalid permission code | 404 |

---

## 10. API: DELETE /rbac/roles/{uuid}/permissions/{permissionCode}

```sql
-- After removal, permission should not appear
SELECT p.code FROM role_permissions rp
JOIN permissions p ON rp.permission_id = p.id
WHERE rp.role_id = (SELECT id FROM roles WHERE uuid = '<role-uuid>')
  AND p.code = 'VIEW_CASES';
-- Expected: 0 rows
```

---

## 11. API: DELETE /rbac/roles/{uuid} (Delete Custom Role)

### Soft Delete
```sql
SELECT is_deleted FROM roles WHERE name = 'SENIOR_CLERK';
-- After delete: true
```

### Negative Tests
| Test | Expected |
|---|---|
| Delete system role (ADMIN) | 400 — "System roles cannot be deleted" |

---

## 12. RBAC Success Criteria
- [x] System roles (ADMIN, CLERK, JUDGE, ADVOCATE) exist on startup
- [x] Custom roles can be created, updated, deleted (soft)
- [x] Permissions can be assigned and removed
- [x] Duplicate permission assignment is idempotent (no error)
- [x] System roles cannot be hard-deleted

---

---

# MODULE 4 — ADMIN

## 1. Purpose
Administrative user management — approve/reject accounts, lock/unlock, assign roles, dashboard.

## 2. Base Path
`/api/v1/admin`

## 3. Required DB Records
```sql
-- Ensure at least one PENDING user exists
INSERT INTO users (uuid, full_name, email, username, password, role, account_status, is_deleted, created_at, updated_at)
VALUES (
  gen_random_uuid()::text,
  'Test Advocate',
  'test.advocate@test.in',
  'test.advocate',
  '$2a$10$xyz',   -- bcrypt hash placeholder
  'ADVOCATE',
  'PENDING_VERIFICATION',
  false,
  now(),
  now()
);

-- Or just register a new user via /auth/register and skip email verification
```

---

## 4. API: GET /admin/users (Paginated)

```
GET /api/v1/admin/users?page=0&size=20
```

### Expected Response
```json
{
  "data": {
    "content": [ ... ],
    "totalElements": 5,
    "totalPages": 1,
    "page": 0,
    "size": 20
  }
}
```

### Pagination Testing
| Parameter | Value | Expected |
|---|---|---|
| `page=0&size=5` | First 5 users | 200 |
| `page=999&size=20` | Empty page | 200, content=[] |
| `size=0` | Bad request | 400 or empty |

### DB Verification
```sql
SELECT count(*) FROM users WHERE is_deleted = false;
-- Should match totalElements
```

---

## 5. API: GET /admin/users/{uuid}

```sql
-- Get a user UUID
SELECT uuid, email, role, account_status FROM users WHERE is_deleted = false LIMIT 3;
```

---

## 6. API: POST /admin/users/{uuid}/approve

### Before Approve
```sql
SELECT account_status FROM users WHERE uuid = '<uuid>';
-- Expected: 'PENDING_VERIFICATION'
```

### Execute
```
POST /api/v1/admin/users/{uuid}/approve
```

### After Approve
```sql
SELECT account_status FROM users WHERE uuid = '<uuid>';
-- Expected: 'ACTIVE'

-- Audit log (if implemented)
SELECT action, entity_type, entity_uuid FROM audit_logs
WHERE entity_uuid = '<uuid>' ORDER BY created_at DESC LIMIT 1;
```

### Negative Tests
| Test | Expected |
|---|---|
| Approve already active user | 400 — "User is already active" |
| Non-existent UUID | 404 |

---

## 7. API: POST /admin/users/{uuid}/reject

```
POST /api/v1/admin/users/{uuid}/reject?reason=Document%20not%20valid
```

### After Reject
```sql
SELECT account_status FROM users WHERE uuid = '<uuid>';
-- Expected: 'SUSPENDED' or 'REJECTED'
```

---

## 8. API: POST /admin/users/{uuid}/lock

```
POST /api/v1/admin/users/{uuid}/lock?reason=Suspicious+activity
```

### After Lock
```sql
SELECT account_status, account_locked_until FROM users WHERE uuid = '<uuid>';
-- account_status = 'LOCKED'
```

---

## 9. API: POST /admin/users/{uuid}/unlock

```
POST /api/v1/admin/users/{uuid}/unlock
```

### After Unlock
```sql
SELECT account_status, account_locked_until FROM users WHERE uuid = '<uuid>';
-- account_status = 'ACTIVE', account_locked_until = NULL
```

---

## 10. API: PUT /admin/users/{uuid}/assign-role

```
PUT /api/v1/admin/users/{uuid}/assign-role?role=JUDGE
```

### After Role Change
```sql
SELECT role FROM users WHERE uuid = '<uuid>';
-- Expected: 'JUDGE'
```

### Negative Tests
| Test | Expected |
|---|---|
| Invalid role enum | 400 |
| Non-existent user | 404 |

---

## 11. API: POST /admin/users/{uuid}/reset-password

Returns a reset token string in `data` field.

```sql
-- Verify token created
SELECT token, is_used, expires_at FROM password_reset_tokens
WHERE user_id = (SELECT id FROM users WHERE uuid = '<uuid>')
ORDER BY created_at DESC LIMIT 1;
```

---

## 12. API: DELETE /admin/users/{uuid} (Soft Delete)

```sql
-- After delete
SELECT is_deleted, deleted_at FROM users WHERE uuid = '<uuid>';
-- Expected: is_deleted = true
```

---

## 13. API: GET /admin/dashboard

Returns aggregated system stats:
```json
{
  "data": {
    "totalUsers": 10,
    "pendingApprovals": 2,
    "activeUsers": 7,
    "lockedUsers": 1,
    "totalCases": 15
  }
}
```

```sql
-- Verify stats manually
SELECT account_status, count(*) FROM users WHERE is_deleted = false GROUP BY account_status;
SELECT count(*) FROM case_files WHERE is_deleted = false;
```

---

## 14. Admin Success Criteria
- [x] Approve transitions status PENDING → ACTIVE
- [x] Reject transitions status → SUSPENDED
- [x] Lock sets account_status = LOCKED
- [x] Unlock resets account_status = ACTIVE
- [x] Soft delete sets is_deleted = true
- [x] Role assignment updates `role` column
- [x] Dashboard counts match DB counts

---

---

# MODULE 5 — ADVOCATE

## 1. Purpose
Advocate portal — profile management, case filing, document/evidence upload, hearings, notifications, AI recommendations.

## 2. Base Path
`/api/v1/advocate`

## 3. Required DB Records
```sql
-- 1. Verify advocate user exists
SELECT u.uuid, u.email, a.uuid as advocate_uuid, a.bar_council_number
FROM users u
LEFT JOIN advocate_profiles a ON a.user_id = u.id
WHERE u.role = 'ADVOCATE' AND u.is_deleted = false LIMIT 1;

-- 2. Verify court exists
SELECT id, uuid, court_code, court_name, state, district FROM courts
WHERE is_deleted = false LIMIT 3;

-- 3. Verify case categories exist
SELECT id, uuid, category_name FROM case_categories WHERE is_deleted = false LIMIT 5;

-- 4. Insert court if missing
INSERT INTO courts (uuid, court_code, court_name, state, district, is_active, is_deleted, created_at, updated_at)
VALUES (gen_random_uuid()::text, 'TN-DC-001', 'Coimbatore District Court', 'Tamil Nadu', 'Coimbatore', true, false, now(), now());
```

---

## 5. API Execution Order

```
1. GET  /advocate/profile              → check profile
2. GET  /advocate/cases                → list my cases (empty initially)
3. POST /advocate/cases                → create new case
4. GET  /advocate/cases/{uuid}         → get case detail
5. PUT  /advocate/cases/{uuid}         → update case
6. POST /advocate/cases/{uuid}/submit  → submit case
7. POST /advocate/cases/{uuid}/documents → upload document
8. POST /advocate/cases/{uuid}/evidence  → upload evidence
9. GET  /advocate/cases/{uuid}/hearings  → list hearings
10. GET /advocate/notifications          → list notifications
```

---

## 6. API: GET /advocate/profile

```
GET /api/v1/advocate/profile
```

### DB Verification
```sql
SELECT a.uuid, a.bar_council_number, a.specialization, a.years_of_experience,
       u.full_name, u.email
FROM advocate_profiles a
JOIN users u ON u.id = a.user_id
WHERE u.is_deleted = false LIMIT 1;
```

---

## 7. API: POST /advocate/cases (Create Case)

| Field | Value |
|---|---|
| **Endpoint** | `POST /api/v1/advocate/cases` |
| **HTTP Status** | 201 Created |
| **DB Tables** | `case_files` |

### Before Insert
```sql
SELECT count(*) FROM case_files WHERE is_deleted = false;
```

### Request
```json
{
  "caseTitle": "Ravi vs State of Tamil Nadu",
  "caseDescription": "Motor accident compensation claim",
  "caseType": "CIVIL",
  "priority": "NORMAL",
  "petitionerName": "Ravi Kumar",
  "respondentName": "State of Tamil Nadu",
  "policeStation": "Gandhipuram PS",
  "actSection": "Motor Vehicles Act Section 166",
  "filingDate": "2026-07-11",
  "courtUuid": "<court-uuid-from-db>",
  "caseCategoryUuid": "<category-uuid-from-db>"
}
```

### After Insert Verification
```sql
-- Verify case created in DRAFT status
SELECT uuid, case_number, case_title, status, petitioner_name, respondent_name,
       official_case_number, is_deleted, created_at
FROM case_files ORDER BY created_at DESC LIMIT 1;

-- Expected:
-- status = 'DRAFT'
-- official_case_number = NULL (assigned by clerk after registration)
-- is_deleted = false
-- case_number auto-generated

-- Verify FK to court
SELECT c.case_title, ct.court_name FROM case_files c
JOIN courts ct ON ct.id = c.court_id
ORDER BY c.created_at DESC LIMIT 1;

-- Verify FK to petitioner advocate
SELECT cf.case_title, a.bar_council_number FROM case_files cf
JOIN advocate_profiles a ON a.id = cf.petitioner_advocate_id
ORDER BY cf.created_at DESC LIMIT 1;
```

### Validation Rules
| Field | Rule |
|---|---|
| caseTitle | NotBlank, max 500 chars |
| caseType | Valid enum: CIVIL, CRIMINAL, FAMILY, COMMERCIAL, CONSTITUTIONAL, WRIT, REVENUE, LABOUR, OTHER |
| priority | Valid enum: LOW, NORMAL, HIGH, URGENT |
| courtUuid | Must exist in courts table |
| filingDate | Not future date |

### Negative Tests
| Test | Expected |
|---|---|
| Invalid courtUuid | 404 — "Court not found" |
| Missing caseTitle | 400 |
| Invalid caseType | 400 |
| Blank petitionerName | 400 |

---

## 8. API: GET /advocate/cases (List My Cases)

```
GET /api/v1/advocate/cases?page=0&size=10
```

### Pagination Test
| Param | Expected |
|---|---|
| `page=0&size=5` | Max 5 results |
| `status=DRAFT` | Only DRAFT cases |
| `status=SUBMITTED` | Only SUBMITTED cases |

```sql
-- Verify only this advocate's cases returned
SELECT cf.case_title, cf.status, cf.petitioner_advocate_id
FROM case_files cf
JOIN advocate_profiles a ON a.id = cf.petitioner_advocate_id
JOIN users u ON u.id = a.user_id
WHERE u.uuid = '<advocate-user-uuid>'
ORDER BY cf.created_at DESC;
```

---

## 9. API: GET /advocate/cases/search

```
GET /api/v1/advocate/cases/search?keyword=Ravi
```

```sql
-- Verify keyword matches
SELECT case_title, case_number, petitioner_name, respondent_name
FROM case_files WHERE is_deleted = false
AND (
  LOWER(case_title) LIKE '%ravi%' OR
  LOWER(petitioner_name) LIKE '%ravi%' OR
  LOWER(respondent_name) LIKE '%ravi%' OR
  LOWER(case_number) LIKE '%ravi%'
);
```

---

## 10. API: GET /advocate/cases/{uuid} (Detail)

```sql
-- Get case UUID
SELECT uuid, case_number, status FROM case_files ORDER BY created_at DESC LIMIT 1;
```

---

## 11. API: PUT /advocate/cases/{uuid} (Update Case)

### Before Update
```sql
SELECT case_title, status, updated_at FROM case_files WHERE uuid = '<case-uuid>';
```

### Request
```json
{
  "caseTitle": "Ravi vs State of Tamil Nadu — Amended",
  "caseDescription": "Updated description with additional facts"
}
```

### After Update
```sql
SELECT case_title, updated_at FROM case_files WHERE uuid = '<case-uuid>';
-- case_title should be updated, updated_at should be newer
```

### Negative Tests
| Test | Expected |
|---|---|
| Update SUBMITTED/REGISTERED case | 400 — "Cannot update submitted case" |
| Update another advocate's case | 403 |

---

## 12. API: POST /advocate/cases/{uuid}/submit

Transitions case from DRAFT → SUBMITTED.

```
POST /api/v1/advocate/cases/{uuid}/submit
```

### Before/After
```sql
-- Before: status = 'DRAFT'
-- After:  status = 'SUBMITTED'
SELECT status FROM case_files WHERE uuid = '<case-uuid>';

-- Status history created
SELECT from_status, to_status, changed_by_role, changed_at FROM case_status_history
WHERE case_file_id = (SELECT id FROM case_files WHERE uuid = '<case-uuid>')
ORDER BY changed_at DESC LIMIT 1;
```

### Negative Tests
| Test | Expected |
|---|---|
| Submit already submitted case | 400 — BusinessRuleViolation |
| Submit REGISTERED case | 400 |

---

## 13. API: POST /advocate/cases/{uuid}/documents (Upload Document)

### Before Insert
```sql
SELECT count(*) FROM documents WHERE case_id = (SELECT id FROM case_files WHERE uuid = '<case-uuid>');
```

### Request (multipart/form-data)
```
documentType: PETITION
description: Main petition document
file: [attached PDF file]
```

### After Insert
```sql
SELECT uuid, original_file_name, document_type, file_size_bytes, is_verified, uploaded_by_uuid
FROM documents WHERE case_id = (SELECT id FROM case_files WHERE uuid = '<case-uuid>')
ORDER BY created_at DESC LIMIT 1;

-- Expected:
-- is_verified = false (clerk hasn't verified yet)
-- document_type = 'PETITION'
-- uploaded_by_uuid = advocate user UUID
```

---

## 14. API: POST /advocate/cases/{uuid}/evidence (Upload Evidence)

### Request (multipart/form-data)
```
evidenceType: DOCUMENTARY
title: Medical Certificate
description: Hospital discharge report
collectedAt: 2026-07-01
collectedBy: Dr. Rajan
location: Coimbatore General Hospital
```

### After Insert
```sql
SELECT uuid, evidence_type, title, is_admitted, is_verified
FROM evidence WHERE case_id = (SELECT id FROM case_files WHERE uuid = '<case-uuid>');
-- is_admitted = false, is_verified = false
```

---

## 15. API: GET /advocate/cases/{uuid}/hearings

```sql
-- Verify hearings (may be empty if no hearings scheduled yet)
SELECT h.uuid, h.hearing_date, h.hearing_time, h.status
FROM hearings h WHERE h.case_id = (SELECT id FROM case_files WHERE uuid = '<case-uuid>');
```

---

## 16. API: GET /advocate/notifications

```
GET /api/v1/advocate/notifications?page=0&size=10
```

```sql
-- Verify in DB
SELECT n.title, n.message, n.is_read, n.notification_type, n.created_at
FROM notifications n
JOIN users u ON u.id = n.recipient_id
WHERE u.uuid = '<advocate-user-uuid>'
ORDER BY n.created_at DESC;
```

---

## 17. API: GET /advocate/cases/{uuid}/ai-analysis

Returns AI score and recommendations (read-only for advocate).

```sql
-- Check if AI data exists (may be empty in dev)
SELECT * FROM ai_analysis WHERE case_id = (SELECT id FROM case_files WHERE uuid = '<case-uuid>');
```

---

## 18. Advocate Success Criteria
- [x] Case created with status DRAFT
- [x] Case linked to correct advocate via FK
- [x] Submit transitions DRAFT → SUBMITTED
- [x] Document upload creates row in `documents` table with `is_verified = false`
- [x] Evidence upload creates row in `evidence` table with `is_admitted = false, is_verified = false`
- [x] Advocate can only see their own cases (court-scoped when auth enabled)
- [x] Status history entry created on every transition
- [x] Notifications delivered to advocate

---

---

# MODULE 6 — CLERK

## 1. Purpose
Clerk Portal — case scrutiny, document/evidence verification, objection management, duplicate detection, and official case registration with auto-generated case numbers.

## 2. Base Path
`/api/v1/clerk`

## 3. Authentication Status
⚠️ **Auth DISABLED** — but `ClerkSecurityUtil` requires a JWT principal. **For dev testing with auth disabled, `ClerkSecurityUtil.getCurrentClerk()` will throw `UnauthorizedActionException`.**

### Workaround for Dev Testing
Either:
- **Option A**: Temporarily inject a mock clerk UUID in `ClerkSecurityUtil` (not recommended)
- **Option B**: Enable auth (`app.security.enabled=true`) and use a real CLERK JWT
- **Option C**: Test via DB direct — create clerk, case, verify SQL, confirm logic works

### Quick Setup: Create Test Clerk via SQL
```sql
-- Step 1: Create clerk user
INSERT INTO users (uuid, full_name, email, username, password, role, account_status, is_deleted, created_at, updated_at)
VALUES (
  'clerk-uuid-0001-000000000001',
  'Priya Clerk',
  'priya.clerk@court.in',
  'priya.clerk',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lHuu', -- "password"
  'CLERK',
  'ACTIVE',
  false, now(), now()
);

-- Step 2: Get court ID
SELECT id, uuid, court_name FROM courts WHERE is_deleted = false LIMIT 1;

-- Step 3: Create clerk profile linked to court
INSERT INTO clerk_profiles (uuid, user_id, employee_id, court_section, department, court_id, is_deleted, created_at, updated_at)
VALUES (
  'clerkprofile-uuid-000001',
  (SELECT id FROM users WHERE email = 'priya.clerk@court.in'),
  'EMP-001',
  'Filing Section',
  'Civil Department',
  (SELECT id FROM courts WHERE is_deleted = false LIMIT 1),
  false, now(), now()
);

-- Step 4: Create a SUBMITTED case in that court
SELECT id, uuid FROM case_files WHERE status = 'SUBMITTED' AND is_deleted = false LIMIT 1;
-- If none: update a DRAFT case
UPDATE case_files SET status = 'SUBMITTED'
WHERE id = (SELECT id FROM case_files WHERE status = 'DRAFT' AND is_deleted = false LIMIT 1);
```

---

## 4. API Execution Order

```
1. GET  /clerk/profile                              → verify profile + court
2. GET  /clerk/dashboard                            → dashboard stats
3. GET  /clerk/cases/pending                        → pending SUBMITTED cases
4. PUT  /clerk/cases/{uuid}/open-scrutiny           → SUBMITTED → UNDER_SCRUTINY
5. GET  /clerk/cases/{uuid}                         → full case detail
6. GET  /clerk/cases/{uuid}/documents               → list documents
7. PUT  /clerk/cases/{uuid}/documents/{docUuid}/verify → verify document
8. GET  /clerk/cases/{uuid}/evidence                → list evidence
9. PUT  /clerk/cases/{uuid}/evidence/{evUuid}/verify   → verify evidence
10. POST /clerk/cases/{uuid}/duplicate-check        → check duplicates
11. PUT  /clerk/cases/{uuid}/verify                 → set jurisdiction verified
12. PUT  /clerk/cases/{uuid}/register               → UNDER_SCRUTINY → REGISTERED
    OR
12. POST /clerk/cases/{uuid}/objections             → raise objection
13. PUT  /clerk/cases/{uuid}/return                 → UNDER_SCRUTINY → RETURNED
14. GET  /clerk/cases/{uuid}/timeline               → status audit trail
```

---

## 5. API: GET /clerk/profile

```
GET /api/v1/clerk/profile
```

### Expected Response
```json
{
  "data": {
    "uuid": "clerkprofile-uuid-000001",
    "fullName": "Priya Clerk",
    "email": "priya.clerk@court.in",
    "employeeId": "EMP-001",
    "courtSection": "Filing Section",
    "department": "Civil Department",
    "courtName": "Coimbatore District Court",
    "courtCode": "TN-DC-001"
  }
}
```

### DB Verification
```sql
SELECT a.uuid, a.employee_id, a.court_section, a.department,
       u.full_name, u.email,
       c.court_name, c.court_code
FROM clerk_profiles a
JOIN users u ON u.id = a.user_id
LEFT JOIN courts c ON c.id = a.court_id
WHERE u.email = 'priya.clerk@court.in';
```

---

## 6. API: GET /clerk/dashboard

```
GET /api/v1/clerk/dashboard
```

### Expected Response
```json
{
  "data": {
    "clerkName": "Priya Clerk",
    "courtName": "Coimbatore District Court",
    "pendingScrutinyCount": 2,
    "underScrutinyCount": 1,
    "returnedCasesCount": 0,
    "registeredTodayCount": 0,
    "pendingDocumentVerificationCount": 3,
    "pendingEvidenceVerificationCount": 1,
    "pendingJudgeAssignmentCount": 0,
    "unreadNotificationsCount": 5,
    "welcomeMessage": "Welcome, Priya Clerk. You have 2 case(s) awaiting scrutiny in Coimbatore District Court."
  }
}
```

### DB Verification
```sql
-- Verify pending scrutiny count
SELECT count(*) FROM case_files cf
JOIN courts c ON c.id = cf.court_id
WHERE cf.status = 'SUBMITTED' AND cf.is_deleted = false
  AND c.id = (SELECT court_id FROM clerk_profiles WHERE user_id = (SELECT id FROM users WHERE email = 'priya.clerk@court.in'));

-- Verify under scrutiny count
SELECT count(*) FROM case_files cf
JOIN courts c ON c.id = cf.court_id
WHERE cf.status = 'UNDER_SCRUTINY' AND cf.is_deleted = false
  AND c.id = (SELECT court_id FROM clerk_profiles WHERE user_id = (SELECT id FROM users WHERE email = 'priya.clerk@court.in'));
```

---

## 7. API: GET /clerk/cases/pending

```
GET /api/v1/clerk/cases/pending?page=0&size=10
```

### Pagination Test
| Param | Expected |
|---|---|
| `page=0&size=5` | Max 5 SUBMITTED cases |
| `page=999` | Empty page |

### DB Verification
```sql
SELECT cf.uuid, cf.case_number, cf.case_title, cf.status, c.court_name
FROM case_files cf
JOIN courts c ON c.id = cf.court_id
WHERE cf.status = 'SUBMITTED' AND cf.is_deleted = false
  AND c.id = (SELECT court_id FROM clerk_profiles WHERE user_id = (SELECT id FROM users WHERE email = 'priya.clerk@court.in'))
ORDER BY cf.created_at ASC;
```

---

## 8. API: GET /clerk/cases (All Cases with Status Filter)

```
GET /api/v1/clerk/cases?status=SUBMITTED,UNDER_SCRUTINY&page=0&size=10
GET /api/v1/clerk/cases?status=RETURNED&page=0&size=10
```

---

## 9. API: GET /clerk/cases/search

```
GET /api/v1/clerk/cases/search?keyword=Ravi&page=0&size=10
```

### Filtering Verification
```sql
SELECT cf.case_title, cf.case_number, cf.official_case_number, cf.status
FROM case_files cf
JOIN courts c ON c.id = cf.court_id
WHERE cf.is_deleted = false
  AND c.id = (SELECT court_id FROM clerk_profiles WHERE user_id = (SELECT id FROM users WHERE email = 'priya.clerk@court.in'))
  AND (
    LOWER(cf.case_title) LIKE '%ravi%' OR
    LOWER(cf.petitioner_name) LIKE '%ravi%' OR
    LOWER(cf.respondent_name) LIKE '%ravi%' OR
    LOWER(cf.case_number) LIKE '%ravi%' OR
    LOWER(cf.official_case_number) LIKE '%ravi%'
  );
```

---

## 10. API: PUT /clerk/cases/{uuid}/open-scrutiny

| Field | Value |
|---|---|
| **Transition** | `SUBMITTED` → `UNDER_SCRUTINY` |
| **HTTP Status** | 200 OK |

### Before
```sql
SELECT status, scrutiny_clerk_uuid FROM case_files WHERE uuid = '<case-uuid>';
-- Expected: status = 'SUBMITTED', scrutiny_clerk_uuid = NULL
```

### Request
```json
{ "remarks": "Opening case for formal scrutiny review" }
```

### After
```sql
SELECT status, scrutiny_clerk_uuid, verification_remarks
FROM case_files WHERE uuid = '<case-uuid>';
-- status = 'UNDER_SCRUTINY'
-- scrutiny_clerk_uuid = clerk's UUID
-- verification_remarks = 'Opening case for formal scrutiny review'

-- Status history created
SELECT from_status, to_status, changed_by_role, remarks, changed_at
FROM case_status_history
WHERE case_file_id = (SELECT id FROM case_files WHERE uuid = '<case-uuid>')
ORDER BY changed_at DESC LIMIT 1;
-- from_status = 'SUBMITTED', to_status = 'UNDER_SCRUTINY', changed_by_role = 'CLERK'
```

### Negative Tests
| Test | Input | Expected |
|---|---|---|
| Case not SUBMITTED | DRAFT case UUID | 422 — "Only SUBMITTED cases can be opened" |
| Case from another court | Different court's case UUID | 403 — "Does not belong to your court" |
| Non-existent UUID | `999-fake-uuid` | 404 |
| Missing remarks | `{}` | 400 — validation error |

---

## 11. API: GET /clerk/cases/{uuid} (Case Detail)

### Expected Response
```json
{
  "data": {
    "uuid": "...",
    "caseNumber": "CASE-2026-000001",
    "officialCaseNumber": null,
    "status": "UNDER_SCRUTINY",
    "petitionerName": "Ravi Kumar",
    "respondentName": "State of Tamil Nadu",
    "isDuplicateChecked": false,
    "isJurisdictionVerified": false,
    "documentCount": 2,
    "unverifiedDocumentCount": 2,
    "evidenceCount": 1,
    "unverifiedEvidenceCount": 1,
    "openObjectionCount": 0
  }
}
```

---

## 12. API: GET /clerk/cases/{uuid}/documents

```
GET /api/v1/clerk/cases/{uuid}/documents?page=0&size=20
```

```sql
SELECT d.uuid, d.original_file_name, d.document_type, d.is_verified,
       d.verified_by_uuid, d.verified_at, d.rejection_reason
FROM documents d WHERE d.case_id = (SELECT id FROM case_files WHERE uuid = '<case-uuid>')
  AND d.is_deleted = false;
```

---

## 13. API: PUT /clerk/cases/{uuid}/documents/{docUuid}/verify

### Request (Approve)
```json
{
  "approved": true,
  "remarks": "Document is clear and complete"
}
```

### Request (Reject)
```json
{
  "approved": false,
  "remarks": "Document is illegible",
  "rejectionReason": "Petition is blurred and cannot be read"
}
```

### Before/After
```sql
-- Before
SELECT is_verified, verified_by_uuid, verified_at, rejection_reason
FROM documents WHERE uuid = '<doc-uuid>';
-- is_verified = false

-- After (approve)
SELECT is_verified, verified_by_uuid, verified_at
FROM documents WHERE uuid = '<doc-uuid>';
-- is_verified = true, verified_by_uuid = clerk UUID, verified_at NOT NULL

-- After (reject)
SELECT is_verified, rejection_reason
FROM documents WHERE uuid = '<doc-uuid>';
-- is_verified = false, rejection_reason populated

-- Notification sent to advocate on rejection
SELECT title, message, is_read FROM notifications
WHERE reference_uuid = '<case-uuid>' AND reference_type = 'Document'
ORDER BY created_at DESC LIMIT 1;
```

### Negative Tests
| Test | Expected |
|---|---|
| Reject without rejectionReason | 422 — "Rejection reason required" |
| Non-existent docUuid | 404 |
| Document from different case | 404 |

---

## 14. API: GET/PUT /clerk/cases/{uuid}/evidence

Same pattern as document verification.

### PUT /clerk/cases/{uuid}/evidence/{evUuid}/verify

```sql
-- After verify
SELECT is_verified, verified_by_uuid, verified_at, rejection_reason
FROM evidence WHERE uuid = '<evidence-uuid>';
```

---

## 15. API: POST /clerk/cases/{uuid}/duplicate-check

```
POST /api/v1/clerk/cases/{uuid}/duplicate-check
```

### Expected Response
```json
{
  "data": {
    "caseUuid": "...",
    "duplicatesFound": false,
    "duplicateCount": 0,
    "potentialDuplicates": [],
    "message": "No duplicate cases found. Safe to proceed with registration."
  }
}
```

### After Check
```sql
SELECT is_duplicate_checked, duplicate_case_uuids
FROM case_files WHERE uuid = '<case-uuid>';
-- is_duplicate_checked = true
-- duplicate_case_uuids = NULL (if no duplicates) or comma-separated UUIDs
```

### If Duplicates Found
```sql
-- Response will contain potentialDuplicates array with UUID, case number, party names
-- DB: duplicate_case_uuids = 'uuid1,uuid2'
```

---

## 16. API: PUT /clerk/cases/{uuid}/verify

Sets `is_jurisdiction_verified = true`.

```json
{ "remarks": "Jurisdiction verified — case properly filed in Coimbatore District Court" }
```

```sql
SELECT is_jurisdiction_verified, verification_remarks
FROM case_files WHERE uuid = '<case-uuid>';
-- is_jurisdiction_verified = true
```

### Negative Tests
| Test | Expected |
|---|---|
| Case not UNDER_SCRUTINY | 422 |

---

## 17. API: POST /clerk/cases/{uuid}/objections

### Request
```json
{
  "objectionType": "MISSING_DOCUMENT",
  "reason": "Vakalatnama document is missing",
  "missingDocuments": "Vakalatnama, Court Fee Receipt",
  "correctionRequired": "Please upload signed Vakalatnama and court fee receipt within 7 days"
}
```

### After Create
```sql
SELECT uuid, objection_type, reason, missing_documents, is_resolved, raised_by_clerk_uuid
FROM case_objections
WHERE case_id = (SELECT id FROM case_files WHERE uuid = '<case-uuid>')
ORDER BY created_at DESC LIMIT 1;

-- Expected:
-- objection_type = 'MISSING_DOCUMENT'
-- is_resolved = false
-- raised_by_clerk_uuid = clerk UUID
```

### Valid ObjectionType values
```
MISSING_DOCUMENT
INVALID_DOCUMENT
JURISDICTION_MISMATCH
DUPLICATE_CASE
INCOMPLETE_INFORMATION
OTHER
```

### Negative Tests
| Test | Expected |
|---|---|
| Invalid objectionType | 400 |
| Missing reason | 400 |
| Case not UNDER_SCRUTINY | 422 |

---

## 18. API: GET /clerk/cases/{uuid}/objections

```sql
-- Verify all objections
SELECT uuid, objection_type, reason, is_resolved, created_at
FROM case_objections
WHERE case_id = (SELECT id FROM case_files WHERE uuid = '<case-uuid>')
  AND is_deleted = false
ORDER BY created_at DESC;
```

---

## 19. API: PUT /clerk/cases/{uuid}/return

| Transition | `UNDER_SCRUTINY` → `RETURNED` |
|---|---|

### Request
```json
{ "remarks": "Returning case — missing documents must be uploaded before re-submission" }
```

### After Return
```sql
-- Case status changed
SELECT status, verification_remarks FROM case_files WHERE uuid = '<case-uuid>';
-- status = 'RETURNED'

-- Status history
SELECT from_status, to_status, remarks, changed_by_role, changed_at
FROM case_status_history
WHERE case_file_id = (SELECT id FROM case_files WHERE uuid = '<case-uuid>')
ORDER BY changed_at DESC LIMIT 1;
-- from_status = 'UNDER_SCRUTINY', to_status = 'RETURNED'

-- Advocate notification
SELECT title, message, notification_type FROM notifications
WHERE reference_uuid = '<case-uuid>' AND reference_type = 'CaseFile'
ORDER BY created_at DESC LIMIT 1;
-- title = 'Case Returned — Action Required'
```

### Negative Tests
| Test | Expected |
|---|---|
| Return a SUBMITTED case | 422 |
| Return a REGISTERED case | 422 |

---

## 20. API: PUT /clerk/cases/{uuid}/register ⭐ (MOST IMPORTANT)

| Transition | `UNDER_SCRUTINY` → `REGISTERED` |
|---|---|
| **Generates** | Official case number (`TN-COIMBATORE-TN-DC-001-2026-000001`) |

### Prerequisites (must be true)
```sql
-- 1. Status must be UNDER_SCRUTINY
SELECT status FROM case_files WHERE uuid = '<case-uuid>';
-- = 'UNDER_SCRUTINY'

-- 2. Jurisdiction must be verified
SELECT is_jurisdiction_verified FROM case_files WHERE uuid = '<case-uuid>';
-- = true (run verify API first if false)

-- 3. No existing official case number
SELECT official_case_number FROM case_files WHERE uuid = '<case-uuid>';
-- = NULL
```

### Request
```json
{ "remarks": "All documents verified. Case officially registered." }
```

### Expected Response
```json
{
  "data": {
    "uuid": "...",
    "caseNumber": "CASE-2026-000001",
    "officialCaseNumber": "TAMIL NADU-COIMBATORE-TN-DC-001-2026-000001",
    "caseTitle": "Ravi vs State of Tamil Nadu",
    "status": "REGISTERED",
    "registeredAt": "2026-07-11T07:30:00",
    "registeredByClerkName": "Priya Clerk",
    "judgeQueuePosition": 1,
    "message": "Case 'Ravi vs State of Tamil Nadu' has been officially registered as TAMIL NADU-COIMBATORE-TN-DC-001-2026-000001 and placed at position 1 in the judge assignment queue."
  }
}
```

### After Registration Verification
```sql
-- 1. Case status and official number
SELECT status, official_case_number, registered_at, registered_by_uuid,
       judge_queue_position, judge_queued_at
FROM case_files WHERE uuid = '<case-uuid>';
-- status = 'REGISTERED'
-- official_case_number = 'TN-COIMBATORE-...-2026-000001'
-- registered_at NOT NULL
-- judge_queue_position = 1

-- 2. Status history entry
SELECT from_status, to_status, changed_by_role, remarks
FROM case_status_history
WHERE case_file_id = (SELECT id FROM case_files WHERE uuid = '<case-uuid>')
ORDER BY changed_at DESC LIMIT 1;
-- from_status = 'UNDER_SCRUTINY', to_status = 'REGISTERED'

-- 3. Sequence counter incremented
SELECT court_id, year, last_seq FROM case_number_sequences
WHERE court_id = (SELECT court_id FROM case_files WHERE uuid = '<case-uuid>');
-- last_seq = 1 (or incremented)

-- 4. Official number is unique
SELECT count(*) FROM case_files
WHERE official_case_number = '<official-case-number>';
-- Expected: 1 (unique)

-- 5. Advocate notification sent
SELECT title, message, is_read FROM notifications
WHERE reference_uuid = '<case-uuid>' AND reference_type = 'CaseFile'
ORDER BY created_at DESC LIMIT 1;
-- title = 'Case Registered Successfully'
```

### Negative Tests
| Test | Expected |
|---|---|
| Register without jurisdiction verified | 422 — "Jurisdiction must be verified first" |
| Register non-UNDER_SCRUTINY case | 422 |
| Register already registered case | 422 — "Case already has an official number" |
| Register case from another court | 403 |

---

## 21. Official Case Number Format Verification

```sql
-- Test case number format
SELECT official_case_number FROM case_files WHERE status = 'REGISTERED';
-- Should follow: STATE-DISTRICT-COURTCODE-YEAR-SEQUENCE
-- Example: TAMIL NADU-COIMBATORE-TN-DC-001-2026-000001

-- Verify sequence increments
-- Register 3 cases for same court in same year
-- Expect: ...-000001, ...-000002, ...-000003

-- Verify uniqueness constraint
SELECT official_case_number, count(*) FROM case_files
WHERE official_case_number IS NOT NULL
GROUP BY official_case_number HAVING count(*) > 1;
-- Expected: 0 rows (all unique)
```

---

## 22. API: GET /clerk/cases/{uuid}/timeline

```
GET /api/v1/clerk/cases/{uuid}/timeline
```

### Expected Response
```json
{
  "data": [
    {
      "fromStatus": null,
      "toStatus": "DRAFT",
      "changedByRole": "ADVOCATE",
      "changedAt": "2026-07-11T06:00:00"
    },
    {
      "fromStatus": "DRAFT",
      "toStatus": "SUBMITTED",
      "changedByRole": "ADVOCATE",
      "changedAt": "2026-07-11T06:05:00"
    },
    {
      "fromStatus": "SUBMITTED",
      "toStatus": "UNDER_SCRUTINY",
      "changedByRole": "CLERK",
      "changedAt": "2026-07-11T07:00:00"
    },
    {
      "fromStatus": "UNDER_SCRUTINY",
      "toStatus": "REGISTERED",
      "changedByRole": "CLERK",
      "remarks": "All documents verified.",
      "changedAt": "2026-07-11T07:30:00"
    }
  ]
}
```

```sql
-- Verify all history entries
SELECT from_status, to_status, changed_by_role, changed_by_uuid, remarks, changed_at
FROM case_status_history
WHERE case_file_id = (SELECT id FROM case_files WHERE uuid = '<case-uuid>')
ORDER BY changed_at ASC;
```

---

## 23. Notification Verification (Clerk Events)

```sql
-- All notifications sent by clerk actions
SELECT n.title, n.message, n.notification_type, n.reference_type, n.is_read, n.created_at
FROM notifications n
JOIN users u ON u.id = n.recipient_id
WHERE n.reference_uuid = '<case-uuid>'
ORDER BY n.created_at DESC;
```

| Clerk Action | Notification Title |
|---|---|
| `return` | Case Returned — Action Required |
| `register` | Case Registered Successfully |
| Document rejected | Document Rejected — {filename} |
| Evidence rejected | Evidence Rejected — {title} |
| Duplicate check finds duplicates | Duplicate Case Warning |

---

## 24. Audit Log Verification

```sql
-- Verify audit entries for clerk actions
SELECT action, entity_type, entity_uuid, description, created_at
FROM audit_logs
WHERE entity_type IN ('CaseFile', 'Document', 'Evidence', 'CaseObjection')
  AND entity_uuid = '<entity-uuid>'
ORDER BY created_at DESC;
```

| Action | Trigger |
|---|---|
| `CASE_SCRUTINY_OPENED` | open-scrutiny |
| `CASE_VERIFIED` | verify |
| `CASE_RETURNED` | return |
| `CASE_REGISTERED` | register |
| `OBJECTION_RAISED` | POST /objections |
| `DUPLICATE_CHECK` | duplicate-check |
| `DOCUMENT_VERIFIED` | verify doc (approved=true) |
| `DOCUMENT_REJECTED` | verify doc (approved=false) |
| `EVIDENCE_VERIFIED` | verify evidence (approved=true) |
| `EVIDENCE_REJECTED` | verify evidence (approved=false) |

---

## 25. Clerk Success Criteria
- [x] Profile shows correct court assignment
- [x] Dashboard counters match DB counts
- [x] Only SUBMITTED cases appear in pending list
- [x] Cases from other courts not returned (court-scoped)
- [x] Status transition: SUBMITTED → UNDER_SCRUTINY ✅
- [x] Status transition: UNDER_SCRUTINY → RETURNED ✅
- [x] Status transition: UNDER_SCRUTINY → REGISTERED ✅
- [x] Invalid transitions rejected with 422
- [x] Official case number generated in correct format
- [x] Case number sequence increments atomically
- [x] Document verification updates `is_verified`, `verified_by_uuid`, `verified_at`
- [x] Document rejection requires `rejectionReason`
- [x] Evidence verification same as document
- [x] Objection created with `is_resolved = false`
- [x] Status history appended (never updated/deleted)
- [x] Advocate notified on: returned, registered, doc rejected, evidence rejected, duplicate warning
- [x] Audit log entry created for every action

---

## 26. Common Failure Reasons (All Modules)

| Error | Likely Cause | Fix |
|---|---|---|
| 401 Unauthorized | Security enabled, no JWT | Set `app.security.enabled=false` |
| 403 Forbidden | Role mismatch | Check `@PreAuthorize` annotation |
| 404 Not Found | UUID doesn't exist or is_deleted=true | Verify UUID in DB |
| 422 Business Rule Violation | Status transition invalid | Check current status first |
| 409 Conflict | Duplicate unique field | Check email/username uniqueness |
| 400 Validation Error | Missing/invalid field | Read validation message |
| 500 Internal Server Error | DB connection, null FK | Check app logs + DB constraints |
| `ClerkSecurityUtil` error | Auth disabled + Clerk endpoint | Needs JWT auth or mock clerk |
| Flyway migration failure | Schema conflict | Run `SELECT * FROM flyway_schema_history` |

---

## 27. Debugging Checklist

```sql
-- 1. Is the app running?
-- Check: curl http://localhost:8080/api/v1/actuator/health

-- 2. Is security disabled?
-- Check application.yml: app.security.enabled=false

-- 3. Check Flyway version
SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 5;

-- 4. Check for orphan FKs
SELECT cf.uuid, cf.court_id FROM case_files cf
LEFT JOIN courts c ON c.id = cf.court_id WHERE c.id IS NULL;

-- 5. Soft-deleted records showing up
SELECT count(*) FROM case_files WHERE is_deleted = true;

-- 6. Check notifications delivery
SELECT count(*), is_sent FROM notifications GROUP BY is_sent;

-- 7. Check for duplicate case numbers
SELECT official_case_number, count(*) FROM case_files
GROUP BY official_case_number HAVING count(*) > 1;

-- 8. Check sequence table
SELECT * FROM case_number_sequences ORDER BY year DESC;

-- 9. Status history completeness
SELECT cf.case_number, cf.status, max(h.changed_at) as last_transition
FROM case_files cf
LEFT JOIN case_status_history h ON h.case_file_id = cf.id
WHERE cf.is_deleted = false
GROUP BY cf.id, cf.case_number, cf.status
ORDER BY last_transition DESC LIMIT 10;

-- 10. Check objections
SELECT co.objection_type, co.is_resolved, cf.case_number
FROM case_objections co
JOIN case_files cf ON cf.id = co.case_id
WHERE co.is_deleted = false
ORDER BY co.created_at DESC LIMIT 10;
```

---

## 28. Full End-to-End Test Scenario

```
STEP 1: Register advocate → POST /auth/register
STEP 2: Register clerk    → POST /auth/register (role=CLERK)
STEP 3: Create court      → SQL INSERT (or admin API if exists)
STEP 4: Assign clerk to court → SQL UPDATE clerk_profiles SET court_id=...
STEP 5: Create case       → POST /advocate/cases
STEP 6: Upload document   → POST /advocate/cases/{uuid}/documents
STEP 7: Submit case       → POST /advocate/cases/{uuid}/submit
         → Verify: status = 'SUBMITTED'
STEP 8: Open scrutiny     → PUT /clerk/cases/{uuid}/open-scrutiny
         → Verify: status = 'UNDER_SCRUTINY'
STEP 9: Verify document   → PUT /clerk/cases/{uuid}/documents/{docUuid}/verify
         → Verify: documents.is_verified = true
STEP 10: Duplicate check  → POST /clerk/cases/{uuid}/duplicate-check
         → Verify: case_files.is_duplicate_checked = true
STEP 11: Verify case      → PUT /clerk/cases/{uuid}/verify
         → Verify: case_files.is_jurisdiction_verified = true
STEP 12: Register case    → PUT /clerk/cases/{uuid}/register
         → Verify: status = 'REGISTERED'
         → Verify: official_case_number IS NOT NULL
         → Verify: case_number_sequences.last_seq incremented
         → Verify: advocate notification sent
STEP 13: View timeline    → GET /clerk/cases/{uuid}/timeline
         → Verify: 4 entries in case_status_history
```

---

*Document generated: 2026-07-11 | Version: 1.0 | Module Coverage: Authentication, User, RBAC, Admin, Advocate, Clerk*
