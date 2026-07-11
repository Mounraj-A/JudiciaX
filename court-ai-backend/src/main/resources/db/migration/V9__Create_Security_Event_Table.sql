-- ============================================================
-- V9__Create_Security_Event_Table.sql
-- Security events for admin monitoring dashboard
-- ============================================================

CREATE TABLE IF NOT EXISTS security_events (
    id           BIGSERIAL    PRIMARY KEY,
    uuid         VARCHAR(36)  NOT NULL UNIQUE,
    user_id      BIGINT       REFERENCES users(id) ON DELETE SET NULL,
    event_type   VARCHAR(60)  NOT NULL,
    description  VARCHAR(1000),
    ip_address   VARCHAR(45),
    device       VARCHAR(100),
    browser      VARCHAR(100),
    severity     VARCHAR(20)  NOT NULL DEFAULT 'LOW',
    event_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actor_email  VARCHAR(150),
    is_deleted   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP,
    created_by   VARCHAR(100),
    updated_by   VARCHAR(100),

    CONSTRAINT chk_se_severity CHECK (severity IN ('LOW','MEDIUM','HIGH','CRITICAL'))
);

CREATE INDEX IF NOT EXISTS idx_se_user_id    ON security_events(user_id);
CREATE INDEX IF NOT EXISTS idx_se_event_type ON security_events(event_type);
CREATE INDEX IF NOT EXISTS idx_se_created_at ON security_events(event_time);
CREATE INDEX IF NOT EXISTS idx_se_ip_address ON security_events(ip_address);
CREATE INDEX IF NOT EXISTS idx_se_severity   ON security_events(severity);

COMMENT ON TABLE security_events IS 'Immutable record of all security-relevant events for admin monitoring';
