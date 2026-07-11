-- ============================================================
-- V5__Add_Auth_Columns_To_Users.sql
-- Adds new security and lifecycle columns to the users table
-- ============================================================

-- Account status (lifecycle management)
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS account_status      VARCHAR(30)  DEFAULT 'PENDING_VERIFICATION',
    ADD COLUMN IF NOT EXISTS full_name           VARCHAR(200),
    ADD COLUMN IF NOT EXISTS is_mobile_verified  BOOLEAN      NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS failed_login_attempts INTEGER    NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS account_locked_until TIMESTAMP,
    ADD COLUMN IF NOT EXISTS last_login          TIMESTAMP,
    ADD COLUMN IF NOT EXISTS last_logout         TIMESTAMP,
    ADD COLUMN IF NOT EXISTS profile_completion_percent INTEGER NOT NULL DEFAULT 0;

-- Remove the constraint check on role if the new phone_number unique constraint is needed
-- Update phone_number column length to match new 10-digit validation
ALTER TABLE users ALTER COLUMN phone_number TYPE VARCHAR(20);

-- Add unique constraint on phone_number (idempotent)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'uk_users_phone' AND table_name = 'users'
    ) THEN
        ALTER TABLE users ADD CONSTRAINT uk_users_phone UNIQUE (phone_number);
    END IF;
END $$;

-- New index for account_status
CREATE INDEX IF NOT EXISTS idx_users_account_status ON users(account_status);

-- Set initial account_status for existing users based on is_active flag
UPDATE users SET account_status = 'ACTIVE'               WHERE is_active = TRUE  AND is_deleted = FALSE AND account_status IS NULL;
UPDATE users SET account_status = 'INACTIVE'             WHERE is_active = FALSE AND is_deleted = FALSE AND account_status IS NULL;
UPDATE users SET account_status = 'SOFT_DELETED'         WHERE is_deleted = TRUE AND account_status IS NULL;
UPDATE users SET account_status = 'PENDING_VERIFICATION' WHERE account_status IS NULL;

-- Set full_name from first_name + last_name for existing records
UPDATE users SET full_name = CONCAT(first_name, ' ', last_name) WHERE full_name IS NULL;

COMMENT ON COLUMN users.account_status        IS 'ACTIVE, INACTIVE, LOCKED, SUSPENDED, PENDING_VERIFICATION, SOFT_DELETED';
COMMENT ON COLUMN users.full_name             IS 'Full display name — replaces separate first/last name for new users';
COMMENT ON COLUMN users.is_mobile_verified    IS 'Whether mobile OTP verification was completed';
COMMENT ON COLUMN users.failed_login_attempts IS 'Counter reset on successful login';
COMMENT ON COLUMN users.account_locked_until  IS 'Timed lock expiry for brute-force protection';
COMMENT ON COLUMN users.profile_completion_percent IS 'Calculated percentage of completed profile fields (0-100)';
