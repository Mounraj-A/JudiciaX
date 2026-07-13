-- ============================================================
-- V21 — Admin Module: admin support tables
-- AI-Powered Judicial Case Management & Prioritization System
-- ============================================================

-- ── system_configurations ─────────────────────────────────

CREATE TABLE IF NOT EXISTS system_configurations (
    id                  BIGINT        NOT NULL,
    uuid                VARCHAR(36)   NOT NULL,
    config_key          VARCHAR(100)  NOT NULL,
    config_value        VARCHAR(2000),
    description         VARCHAR(500),
    category            VARCHAR(50)   NOT NULL DEFAULT 'GENERAL',
    is_editable         BOOLEAN       NOT NULL DEFAULT TRUE,
    updated_by_admin    VARCHAR(36),
    last_updated_at     TIMESTAMP,
    created_at          TIMESTAMP     NOT NULL,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    is_deleted          BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_sys_config      PRIMARY KEY (id),
    CONSTRAINT uq_sys_config_uuid UNIQUE (uuid),
    CONSTRAINT uq_sys_config_key  UNIQUE (config_key)
);

CREATE INDEX IF NOT EXISTS idx_sysconfig_key        ON system_configurations(config_key);
CREATE INDEX IF NOT EXISTS idx_sysconfig_category   ON system_configurations(category);
CREATE INDEX IF NOT EXISTS idx_sysconfig_is_deleted ON system_configurations(is_deleted);

-- Seed default system configurations
INSERT INTO system_configurations (id, uuid, config_key, config_value, description, category, is_editable, created_at, is_deleted)
VALUES
    (nextval('base_seq'), gen_random_uuid()::text, 'PASSWORD_MIN_LENGTH',        '8',     'Minimum password length',               'SECURITY',      true,  now(), false),
    (nextval('base_seq'), gen_random_uuid()::text, 'PASSWORD_MAX_ATTEMPTS',      '5',     'Max failed login attempts before lock',  'SECURITY',      true,  now(), false),
    (nextval('base_seq'), gen_random_uuid()::text, 'SESSION_TIMEOUT_MINUTES',    '30',    'Session idle timeout in minutes',        'SESSION',       true,  now(), false),
    (nextval('base_seq'), gen_random_uuid()::text, 'AI_ENABLED',                 'true',  'Enable AI case prioritization',          'AI',            true,  now(), false),
    (nextval('base_seq'), gen_random_uuid()::text, 'AI_MODEL_VERSION',           '1.0.0', 'AI model version in use',               'AI',            true,  now(), false),
    (nextval('base_seq'), gen_random_uuid()::text, 'AI_PRIORITY_THRESHOLD',      '75',    'Priority score threshold for HIGH',      'AI',            true,  now(), false),
    (nextval('base_seq'), gen_random_uuid()::text, 'AI_CONFIDENCE_THRESHOLD',    '80',    'Minimum confidence score for use',       'AI',            true,  now(), false),
    (nextval('base_seq'), gen_random_uuid()::text, 'AI_EXPLAINABILITY_ENABLED',  'true',  'Enable XAI factor reporting',            'AI',            true,  now(), false),
    (nextval('base_seq'), gen_random_uuid()::text, 'MAX_UPLOAD_SIZE_MB',         '50',    'Maximum file upload size in MB',         'UPLOAD',        true,  now(), false),
    (nextval('base_seq'), gen_random_uuid()::text, 'OTP_EXPIRY_MINUTES',         '10',    'OTP validity window in minutes',         'SECURITY',      true,  now(), false),
    (nextval('base_seq'), gen_random_uuid()::text, 'WORKING_HOURS_START',        '09:00', 'Court working hours start time',         'WORKING_HOURS', true,  now(), false),
    (nextval('base_seq'), gen_random_uuid()::text, 'WORKING_HOURS_END',          '17:00', 'Court working hours end time',           'WORKING_HOURS', true,  now(), false),
    (nextval('base_seq'), gen_random_uuid()::text, 'EMAIL_NOTIFICATIONS_ENABLED','true',  'Enable email notification delivery',     'NOTIFICATION',  true,  now(), false)
ON CONFLICT (config_key) DO NOTHING;

-- ── system_announcements ───────────────────────────────────

CREATE TABLE IF NOT EXISTS system_announcements (
    id                  BIGINT        NOT NULL,
    uuid                VARCHAR(36)   NOT NULL,
    title               VARCHAR(300)  NOT NULL,
    message             TEXT          NOT NULL,
    priority            VARCHAR(20)   NOT NULL DEFAULT 'MEDIUM',
    target_role         VARCHAR(30)   NOT NULL DEFAULT 'ALL',
    start_date          DATE,
    end_date            DATE,
    is_active           BOOLEAN       NOT NULL DEFAULT FALSE,
    created_by_admin    VARCHAR(36),
    created_at          TIMESTAMP     NOT NULL,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    is_deleted          BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_sys_announcement      PRIMARY KEY (id),
    CONSTRAINT uq_sys_announcement_uuid UNIQUE (uuid)
);

CREATE INDEX IF NOT EXISTS idx_announcement_target     ON system_announcements(target_role);
CREATE INDEX IF NOT EXISTS idx_announcement_active     ON system_announcements(is_active);
CREATE INDEX IF NOT EXISTS idx_announcement_is_deleted ON system_announcements(is_deleted);

-- ── maintenance_windows ────────────────────────────────────

CREATE TABLE IF NOT EXISTS maintenance_windows (
    id                  BIGINT        NOT NULL,
    uuid                VARCHAR(36)   NOT NULL,
    title               VARCHAR(200)  NOT NULL,
    description         TEXT,
    start_time          TIMESTAMP     NOT NULL,
    end_time            TIMESTAMP     NOT NULL,
    status              VARCHAR(20)   NOT NULL DEFAULT 'SCHEDULED',
    created_by_admin    VARCHAR(36),
    created_at          TIMESTAMP     NOT NULL,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    is_deleted          BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_maintenance_window      PRIMARY KEY (id),
    CONSTRAINT uq_maintenance_window_uuid UNIQUE (uuid)
);

CREATE INDEX IF NOT EXISTS idx_maint_status     ON maintenance_windows(status);
CREATE INDEX IF NOT EXISTS idx_maint_start_time ON maintenance_windows(start_time);
CREATE INDEX IF NOT EXISTS idx_maint_is_deleted ON maintenance_windows(is_deleted);

-- ── login_security_events ──────────────────────────────────

CREATE TABLE IF NOT EXISTS login_security_events (
    id          BIGINT        NOT NULL,
    uuid        VARCHAR(36)   NOT NULL,
    event_type  VARCHAR(50)   NOT NULL,
    user_id     BIGINT,
    ip_address  VARCHAR(45),
    browser     VARCHAR(200),
    device      VARCHAR(200),
    status      VARCHAR(20)   NOT NULL DEFAULT 'LOGGED',
    details     VARCHAR(1000),
    event_time  TIMESTAMP,
    created_at  TIMESTAMP     NOT NULL,
    updated_at  TIMESTAMP,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    is_deleted  BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_lse      PRIMARY KEY (id),
    CONSTRAINT uq_lse_uuid UNIQUE (uuid),
    CONSTRAINT fk_lse_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_lse_user_id    ON login_security_events(user_id);
CREATE INDEX IF NOT EXISTS idx_lse_event_type ON login_security_events(event_type);
CREATE INDEX IF NOT EXISTS idx_lse_status     ON login_security_events(status);
CREATE INDEX IF NOT EXISTS idx_lse_created_at ON login_security_events(created_at);
CREATE INDEX IF NOT EXISTS idx_lse_ip_address ON login_security_events(ip_address);
