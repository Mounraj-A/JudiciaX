-- ============================================================
-- V7__Create_Auth_Token_Tables.sql
-- Token tables: refresh_tokens, password_reset_tokens,
-- email_verification_tokens, mobile_otps, password_histories
-- ============================================================

-- ============================================================
-- REFRESH TOKENS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id          BIGSERIAL    PRIMARY KEY,
    uuid        VARCHAR(36)  NOT NULL UNIQUE,
    user_id     BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash  VARCHAR(64)  NOT NULL UNIQUE,
    expiry_date TIMESTAMP    NOT NULL,
    used        BOOLEAN      NOT NULL DEFAULT FALSE,
    session_id  VARCHAR(36),
    ip_address  VARCHAR(45),
    is_deleted  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100)
);
CREATE INDEX IF NOT EXISTS idx_refresh_token_hash ON refresh_tokens(token_hash);
CREATE INDEX IF NOT EXISTS idx_refresh_token_user ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_token_used ON refresh_tokens(used);

-- ============================================================
-- PASSWORD RESET TOKENS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id          BIGSERIAL    PRIMARY KEY,
    uuid        VARCHAR(36)  NOT NULL UNIQUE,
    user_id     BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash  VARCHAR(64)  NOT NULL UNIQUE,
    expiry_date TIMESTAMP    NOT NULL,
    used        BOOLEAN      NOT NULL DEFAULT FALSE,
    ip_address  VARCHAR(45),
    is_deleted  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100)
);
CREATE INDEX IF NOT EXISTS idx_prt_token_hash ON password_reset_tokens(token_hash);
CREATE INDEX IF NOT EXISTS idx_prt_user_id    ON password_reset_tokens(user_id);

-- ============================================================
-- EMAIL VERIFICATION TOKENS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS email_verification_tokens (
    id          BIGSERIAL    PRIMARY KEY,
    uuid        VARCHAR(36)  NOT NULL UNIQUE,
    user_id     BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash  VARCHAR(64)  NOT NULL UNIQUE,
    expiry_date TIMESTAMP    NOT NULL,
    used        BOOLEAN      NOT NULL DEFAULT FALSE,
    is_deleted  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100)
);
CREATE INDEX IF NOT EXISTS idx_evt_token_hash ON email_verification_tokens(token_hash);
CREATE INDEX IF NOT EXISTS idx_evt_user_id    ON email_verification_tokens(user_id);

-- ============================================================
-- MOBILE OTPS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS mobile_otps (
    id            BIGSERIAL    PRIMARY KEY,
    uuid          VARCHAR(36)  NOT NULL UNIQUE,
    user_id       BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    otp_hash      VARCHAR(255) NOT NULL,
    phone_number  VARCHAR(15)  NOT NULL,
    expiry_date   TIMESTAMP    NOT NULL,
    used          BOOLEAN      NOT NULL DEFAULT FALSE,
    attempt_count INTEGER      NOT NULL DEFAULT 0,
    is_deleted    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP,
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100)
);
CREATE INDEX IF NOT EXISTS idx_otp_user_id      ON mobile_otps(user_id);
CREATE INDEX IF NOT EXISTS idx_otp_phone_number ON mobile_otps(phone_number);
CREATE INDEX IF NOT EXISTS idx_otp_used         ON mobile_otps(used);

-- ============================================================
-- PASSWORD HISTORIES TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS password_histories (
    id            BIGSERIAL    PRIMARY KEY,
    uuid          VARCHAR(36)  NOT NULL UNIQUE,
    user_id       BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    password_hash VARCHAR(255) NOT NULL,
    changed_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP,
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100)
);
CREATE INDEX IF NOT EXISTS idx_ph_user_id    ON password_histories(user_id);
CREATE INDEX IF NOT EXISTS idx_ph_created_at ON password_histories(created_at);

COMMENT ON TABLE refresh_tokens             IS 'SHA-256 hashed refresh tokens for secure token rotation';
COMMENT ON TABLE password_reset_tokens      IS 'One-time password reset tokens (SHA-256 hashed)';
COMMENT ON TABLE email_verification_tokens  IS 'Email verification tokens (SHA-256 hashed)';
COMMENT ON TABLE mobile_otps               IS 'BCrypt hashed 6-digit mobile OTPs';
COMMENT ON TABLE password_histories        IS 'BCrypt hashed previous passwords for reuse prevention';
