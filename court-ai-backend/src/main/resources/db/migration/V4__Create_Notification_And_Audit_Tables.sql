-- ============================================================
-- V4__Create_Notification_And_Audit_Tables.sql
-- Flyway Migration: Notification and audit log tables
-- ============================================================

-- ============================================================
-- NOTIFICATIONS TABLE
-- ============================================================
CREATE TABLE notifications (
    id                 BIGSERIAL       PRIMARY KEY,
    uuid               VARCHAR(36)     NOT NULL UNIQUE,
    recipient_id       BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    notification_type  VARCHAR(30)     NOT NULL DEFAULT 'IN_APP',
    title              VARCHAR(200)    NOT NULL,
    message            TEXT            NOT NULL,
    reference_uuid     VARCHAR(36),
    reference_type     VARCHAR(100),
    is_read            BOOLEAN         NOT NULL DEFAULT FALSE,
    is_sent            BOOLEAN         NOT NULL DEFAULT FALSE,
    is_deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP,
    created_by         VARCHAR(100),
    updated_by         VARCHAR(100),

    CONSTRAINT chk_notification_type CHECK (notification_type IN ('IN_APP', 'EMAIL', 'SMS', 'PUSH'))
);

CREATE INDEX idx_notification_recipient ON notifications(recipient_id);
CREATE INDEX idx_notification_is_read   ON notifications(is_read);
CREATE INDEX idx_notification_type      ON notifications(notification_type);
CREATE INDEX idx_notification_sent      ON notifications(is_sent);

COMMENT ON TABLE notifications IS 'User notifications for case events and system alerts';

-- ============================================================
-- AUDIT LOGS TABLE
-- ============================================================
CREATE TABLE audit_logs (
    id            BIGSERIAL       PRIMARY KEY,
    uuid          VARCHAR(36)     NOT NULL UNIQUE,
    actor_uuid    VARCHAR(36),
    actor_email   VARCHAR(150),
    actor_role    VARCHAR(30),
    action        VARCHAR(100)    NOT NULL,
    entity_type   VARCHAR(100),
    entity_uuid   VARCHAR(36),
    description   VARCHAR(1000),
    ip_address    VARCHAR(45),
    request_id    VARCHAR(36),
    old_value     TEXT,
    new_value     TEXT,
    outcome       VARCHAR(20)     DEFAULT 'SUCCESS',
    is_deleted    BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP,
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100),

    CONSTRAINT chk_audit_outcome CHECK (outcome IN ('SUCCESS', 'FAILURE'))
);

CREATE INDEX idx_audit_actor_id   ON audit_logs(actor_uuid);
CREATE INDEX idx_audit_action     ON audit_logs(action);
CREATE INDEX idx_audit_entity     ON audit_logs(entity_type);
CREATE INDEX idx_audit_created_at ON audit_logs(created_at);
CREATE INDEX idx_audit_outcome    ON audit_logs(outcome);

COMMENT ON TABLE  audit_logs              IS 'Immutable audit trail of all significant system events';
COMMENT ON COLUMN audit_logs.old_value    IS 'JSON snapshot of entity state before the change';
COMMENT ON COLUMN audit_logs.new_value    IS 'JSON snapshot of entity state after the change';
COMMENT ON COLUMN audit_logs.request_id  IS 'Correlates with MDC requestId for log tracing';

-- ============================================================
-- DEFAULT ADMIN USER SEED DATA
-- Password: Admin@123! (BCrypt hash with factor 12)
-- CHANGE THIS PASSWORD IMMEDIATELY IN PRODUCTION
-- ============================================================
INSERT INTO users (
    uuid, username, email, password_hash, first_name, last_name,
    role, is_active, is_email_verified, is_locked, is_deleted,
    created_at, created_by
) VALUES (
    '00000000-0000-0000-0000-000000000001',
    'system_admin',
    'admin@courtai.com',
    '$2a$12$s/7bUYvwZvQ5VrEMl1UF4O1SFfT8Ylb8cBn7yRmMDxXlGHlS1JtVm',
    'System',
    'Administrator',
    'ROLE_ADMIN',
    TRUE,
    TRUE,
    FALSE,
    FALSE,
    CURRENT_TIMESTAMP,
    'SYSTEM'
) ON CONFLICT (email) DO NOTHING;
