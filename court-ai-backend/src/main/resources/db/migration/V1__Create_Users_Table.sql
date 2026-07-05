-- ============================================================
-- V1__Create_Users_Table.sql
-- Flyway Migration: Create the users table
-- Author: Court AI Dev Team
-- Date: 2024-01-01
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- USERS TABLE
-- ============================================================
CREATE TABLE users (
    id                BIGSERIAL       PRIMARY KEY,
    uuid              VARCHAR(36)     NOT NULL UNIQUE,
    username          VARCHAR(50)     NOT NULL UNIQUE,
    email             VARCHAR(150)    NOT NULL UNIQUE,
    password_hash     VARCHAR(255)    NOT NULL,
    first_name        VARCHAR(100)    NOT NULL,
    last_name         VARCHAR(100)    NOT NULL,
    phone_number      VARCHAR(20),
    role              VARCHAR(30)     NOT NULL,
    is_active         BOOLEAN         NOT NULL DEFAULT TRUE,
    is_email_verified BOOLEAN         NOT NULL DEFAULT FALSE,
    is_locked         BOOLEAN         NOT NULL DEFAULT FALSE,
    is_deleted        BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP,
    created_by        VARCHAR(100),
    updated_by        VARCHAR(100),

    CONSTRAINT chk_users_role CHECK (role IN (
        'ROLE_ADMIN', 'ROLE_JUDGE', 'ROLE_CLERK', 'ROLE_ADVOCATE'
    ))
);

-- Indexes
CREATE INDEX idx_users_email      ON users(email);
CREATE INDEX idx_users_role       ON users(role);
CREATE INDEX idx_users_is_deleted ON users(is_deleted);
CREATE INDEX idx_users_username   ON users(username);

COMMENT ON TABLE  users                  IS 'Core user accounts for all system participants';
COMMENT ON COLUMN users.uuid             IS 'Public-facing UUID (never expose internal id)';
COMMENT ON COLUMN users.password_hash    IS 'BCrypt-hashed password - never stored in plaintext';
COMMENT ON COLUMN users.role             IS 'Single role: ROLE_ADMIN, ROLE_JUDGE, ROLE_CLERK, ROLE_ADVOCATE';
COMMENT ON COLUMN users.is_deleted       IS 'Soft-delete flag - do not physically delete user records';
