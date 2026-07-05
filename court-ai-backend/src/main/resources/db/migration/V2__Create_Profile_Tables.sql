-- ============================================================
-- V2__Create_Profile_Tables.sql
-- Flyway Migration: Create Judge, Clerk, and Advocate profile tables
-- ============================================================

-- ============================================================
-- JUDGE PROFILES TABLE
-- ============================================================
CREATE TABLE judge_profiles (
    id                   BIGSERIAL    PRIMARY KEY,
    uuid                 VARCHAR(36)  NOT NULL UNIQUE,
    user_id              BIGINT       NOT NULL UNIQUE REFERENCES users(id) ON DELETE RESTRICT,
    judge_id_number      VARCHAR(50)  UNIQUE,
    court_name           VARCHAR(200),
    designation          VARCHAR(100),
    specialization       VARCHAR(200),
    years_of_experience  INTEGER,
    is_deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP,
    created_by           VARCHAR(100),
    updated_by           VARCHAR(100)
);

CREATE INDEX idx_judge_user_id    ON judge_profiles(user_id);
CREATE INDEX idx_judge_is_deleted ON judge_profiles(is_deleted);

COMMENT ON TABLE judge_profiles IS 'Extended profile data for users with ROLE_JUDGE';

-- ============================================================
-- CLERK PROFILES TABLE
-- ============================================================
CREATE TABLE clerk_profiles (
    id             BIGSERIAL    PRIMARY KEY,
    uuid           VARCHAR(36)  NOT NULL UNIQUE,
    user_id        BIGINT       NOT NULL UNIQUE REFERENCES users(id) ON DELETE RESTRICT,
    employee_id    VARCHAR(50)  UNIQUE,
    court_section  VARCHAR(100),
    department     VARCHAR(100),
    is_deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP,
    created_by     VARCHAR(100),
    updated_by     VARCHAR(100)
);

CREATE INDEX idx_clerk_user_id ON clerk_profiles(user_id);

COMMENT ON TABLE clerk_profiles IS 'Extended profile data for users with ROLE_CLERK';

-- ============================================================
-- ADVOCATE PROFILES TABLE
-- ============================================================
CREATE TABLE advocate_profiles (
    id                   BIGSERIAL    PRIMARY KEY,
    uuid                 VARCHAR(36)  NOT NULL UNIQUE,
    user_id              BIGINT       NOT NULL UNIQUE REFERENCES users(id) ON DELETE RESTRICT,
    bar_council_number   VARCHAR(100) UNIQUE,
    law_firm             VARCHAR(200),
    specialization       VARCHAR(200),
    years_of_practice    INTEGER,
    is_verified          BOOLEAN      NOT NULL DEFAULT FALSE,
    is_deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP,
    created_by           VARCHAR(100),
    updated_by           VARCHAR(100)
);

CREATE INDEX idx_advocate_user_id     ON advocate_profiles(user_id);
CREATE INDEX idx_advocate_bar_number  ON advocate_profiles(bar_council_number);

COMMENT ON TABLE advocate_profiles IS 'Extended profile data for users with ROLE_ADVOCATE';
