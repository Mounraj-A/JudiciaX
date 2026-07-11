-- ============================================================
-- V8__Create_Session_Tables.sql
-- user_sessions and login_history tables
-- ============================================================

-- ============================================================
-- USER SESSIONS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS user_sessions (
    id                  BIGSERIAL    PRIMARY KEY,
    uuid                VARCHAR(36)  NOT NULL UNIQUE,
    user_id             BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    access_token_hash   VARCHAR(64),
    refresh_token_hash  VARCHAR(64),
    ip_address          VARCHAR(45),
    browser             VARCHAR(100),
    device              VARCHAR(100),
    operating_system    VARCHAR(100),
    login_time          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    logout_time         TIMESTAMP,
    last_activity_at    TIMESTAMP,
    is_trusted_device   BOOLEAN      NOT NULL DEFAULT FALSE,
    status              VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),

    CONSTRAINT chk_session_status CHECK (status IN ('ACTIVE','EXPIRED','LOGGED_OUT','REVOKED'))
);

CREATE INDEX IF NOT EXISTS idx_session_user_id      ON user_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_session_status       ON user_sessions(status);
CREATE INDEX IF NOT EXISTS idx_session_access_hash  ON user_sessions(access_token_hash);
CREATE INDEX IF NOT EXISTS idx_session_refresh_hash ON user_sessions(refresh_token_hash);
CREATE INDEX IF NOT EXISTS idx_session_ip_address   ON user_sessions(ip_address);

-- ============================================================
-- LOGIN HISTORY TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS login_history (
    id               BIGSERIAL    PRIMARY KEY,
    uuid             VARCHAR(36)  NOT NULL UNIQUE,
    user_id          BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    login_time       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    logout_time      TIMESTAMP,
    ip_address       VARCHAR(45),
    device           VARCHAR(100),
    browser          VARCHAR(100),
    operating_system VARCHAR(100),
    is_trusted_device BOOLEAN     NOT NULL DEFAULT FALSE,
    status           VARCHAR(20)  NOT NULL DEFAULT 'SUCCESS',
    session_id       VARCHAR(36),
    is_deleted       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP,
    created_by       VARCHAR(100),
    updated_by       VARCHAR(100),

    CONSTRAINT chk_login_status CHECK (status IN ('SUCCESS','FAILED','LOCKED','BLOCKED'))
);

CREATE INDEX IF NOT EXISTS idx_lh_user_id    ON login_history(user_id);
CREATE INDEX IF NOT EXISTS idx_lh_login_time ON login_history(login_time);
CREATE INDEX IF NOT EXISTS idx_lh_status     ON login_history(status);
CREATE INDEX IF NOT EXISTS idx_lh_ip_address ON login_history(ip_address);

COMMENT ON TABLE user_sessions IS 'Active and historical user sessions across devices';
COMMENT ON TABLE login_history IS 'Immutable record of all login attempts';
