-- ============================================================
-- V10__Create_Privacy_Settings_Table.sql
-- User privacy and notification preference settings
-- ============================================================

CREATE TABLE IF NOT EXISTS user_privacy_settings (
    id                   BIGSERIAL    PRIMARY KEY,
    uuid                 VARCHAR(36)  NOT NULL UNIQUE,
    user_id              BIGINT       NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    email_notifications  BOOLEAN      NOT NULL DEFAULT TRUE,
    sms_notifications    BOOLEAN      NOT NULL DEFAULT FALSE,
    dark_mode            BOOLEAN      NOT NULL DEFAULT FALSE,
    language             VARCHAR(10)  NOT NULL DEFAULT 'en',
    is_deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP,
    created_by           VARCHAR(100),
    updated_by           VARCHAR(100)
);

COMMENT ON TABLE user_privacy_settings IS 'Per-user notification and UI preferences';
