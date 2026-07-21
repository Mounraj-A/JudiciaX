-- ============================================================
-- V99__Seed_Test_Users.sql
-- Test/Demo user accounts for all four roles
-- Password for all: Admin@1234
-- BCrypt hash (strength 12): $2b$12$OO6QpXoaa.dNrMSvI9vkreWyhr0gfloQ8s.4nPee9Ze2hdElR3x8S
-- ============================================================

-- Activate existing judge test account first
UPDATE users
SET account_status          = 'ACTIVE',
    is_email_verified       = TRUE,
    is_active               = TRUE,
    failed_login_attempts   = 0,
    profile_completion_percent = 100
WHERE email = 'judge.test@judiciai.com' AND is_deleted = FALSE;

-- Activate all PENDING_VERIFICATION demo users
UPDATE users
SET account_status        = 'ACTIVE',
    is_email_verified     = TRUE,
    is_active             = TRUE,
    failed_login_attempts = 0
WHERE account_status = 'PENDING_VERIFICATION' AND is_deleted = FALSE;

-- ── Upsert clean test accounts ─────────────────────────────────────────────
-- ADMIN  (already exists — just ensure ACTIVE)
UPDATE users
SET account_status        = 'ACTIVE',
    is_email_verified     = TRUE,
    is_active             = TRUE,
    failed_login_attempts = 0,
    full_name             = 'System Administrator',
    profile_completion_percent = 100
WHERE email = 'admin@courtai.com' AND is_deleted = FALSE;

-- JUDGE demo account
INSERT INTO users (
    uuid, username, email, password_hash, first_name, last_name,
    full_name, phone_number, role, is_active, is_email_verified,
    is_locked, is_deleted, account_status, failed_login_attempts,
    profile_completion_percent, created_by
)
VALUES (
    'aabb0001-demo-0000-0000-000000000002',
    'demo.judge',
    'judge@judiciai.com',
    '$2b$12$OO6QpXoaa.dNrMSvI9vkreWyhr0gfloQ8s.4nPee9Ze2hdElR3x8S',
    'Demo', 'Judge', 'Demo Judge',
    '9000000002', 'ROLE_JUDGE',
    TRUE, TRUE, FALSE, FALSE,
    'ACTIVE', 0, 100, 'SYSTEM'
)
ON CONFLICT (email) DO UPDATE
  SET account_status      = 'ACTIVE',
      is_email_verified   = TRUE,
      is_active           = TRUE,
      password_hash       = '$2b$12$OO6QpXoaa.dNrMSvI9vkreWyhr0gfloQ8s.4nPee9Ze2hdElR3x8S',
      failed_login_attempts = 0;

-- CLERK demo account
INSERT INTO users (
    uuid, username, email, password_hash, first_name, last_name,
    full_name, phone_number, role, is_active, is_email_verified,
    is_locked, is_deleted, account_status, failed_login_attempts,
    profile_completion_percent, created_by
)
VALUES (
    'aabb0001-demo-0000-0000-000000000003',
    'demo.clerk',
    'clerk@judiciai.com',
    '$2b$12$OO6QpXoaa.dNrMSvI9vkreWyhr0gfloQ8s.4nPee9Ze2hdElR3x8S',
    'Demo', 'Clerk', 'Demo Clerk',
    '9000000003', 'ROLE_CLERK',
    TRUE, TRUE, FALSE, FALSE,
    'ACTIVE', 0, 100, 'SYSTEM'
)
ON CONFLICT (email) DO UPDATE
  SET account_status      = 'ACTIVE',
      is_email_verified   = TRUE,
      is_active           = TRUE,
      password_hash       = '$2b$12$OO6QpXoaa.dNrMSvI9vkreWyhr0gfloQ8s.4nPee9Ze2hdElR3x8S',
      failed_login_attempts = 0;

-- ADVOCATE demo account
INSERT INTO users (
    uuid, username, email, password_hash, first_name, last_name,
    full_name, phone_number, role, is_active, is_email_verified,
    is_locked, is_deleted, account_status, failed_login_attempts,
    profile_completion_percent, created_by
)
VALUES (
    'aabb0001-demo-0000-0000-000000000004',
    'demo.advocate',
    'advocate@judiciai.com',
    '$2b$12$OO6QpXoaa.dNrMSvI9vkreWyhr0gfloQ8s.4nPee9Ze2hdElR3x8S',
    'Demo', 'Advocate', 'Demo Advocate',
    '9000000004', 'ROLE_ADVOCATE',
    TRUE, TRUE, FALSE, FALSE,
    'ACTIVE', 0, 100, 'SYSTEM'
)
ON CONFLICT (email) DO UPDATE
  SET account_status      = 'ACTIVE',
      is_email_verified   = TRUE,
      is_active           = TRUE,
      password_hash       = '$2b$12$OO6QpXoaa.dNrMSvI9vkreWyhr0gfloQ8s.4nPee9Ze2hdElR3x8S',
      failed_login_attempts = 0;
