-- ==========================================================
-- V24__Notification_Module.sql
-- Description: Creates schema for Enterprise Notification Engine
-- ==========================================================

-- 1. Notification Templates
CREATE TABLE notification_templates (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(36) UNIQUE NOT NULL,
    code VARCHAR(100) UNIQUE NOT NULL,
    title VARCHAR(200) NOT NULL,
    subject VARCHAR(200),
    body TEXT NOT NULL,
    channel VARCHAR(30) NOT NULL,
    variables TEXT, -- JSON or comma separated string of required variables
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_notif_template_code ON notification_templates(code);
CREATE INDEX idx_notif_template_channel ON notification_templates(channel);

-- 2. Notification Preferences
CREATE TABLE notification_preferences (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(36) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    channel VARCHAR(30) NOT NULL,
    notification_type VARCHAR(100) NOT NULL,
    is_enabled BOOLEAN DEFAULT TRUE NOT NULL,
    quiet_hours_start TIME,
    quiet_hours_end TIME,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    UNIQUE(user_id, channel, notification_type)
);

CREATE INDEX idx_notif_pref_user ON notification_preferences(user_id);

-- 3. Notification Deliveries
CREATE TABLE notification_deliveries (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(36) UNIQUE NOT NULL,
    notification_id BIGINT NOT NULL REFERENCES notifications(id) ON DELETE CASCADE,
    recipient_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    channel VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL, -- PENDING, PROCESSING, DELIVERED, READ, FAILED, ARCHIVED
    sent_at TIMESTAMP WITH TIME ZONE,
    delivered_at TIMESTAMP WITH TIME ZONE,
    read_at TIMESTAMP WITH TIME ZONE,
    read_device VARCHAR(255),
    read_ip VARCHAR(100),
    retry_count INT DEFAULT 0 NOT NULL,
    failure_reason TEXT,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_notif_delivery_notif ON notification_deliveries(notification_id);
CREATE INDEX idx_notif_delivery_recipient ON notification_deliveries(recipient_id);
CREATE INDEX idx_notif_delivery_status ON notification_deliveries(status);

-- 4. Notification Events (Event Log)
CREATE TABLE notification_events (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(36) UNIQUE NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    reference_uuid VARCHAR(36),
    module VARCHAR(100) NOT NULL,
    payload TEXT, -- JSON representation of the event
    status VARCHAR(30) NOT NULL, -- PENDING, PROCESSED, FAILED
    processed_at TIMESTAMP WITH TIME ZONE,
    error_message TEXT,
    retry_count INT DEFAULT 0 NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_notif_event_status ON notification_events(status);
CREATE INDEX idx_notif_event_type ON notification_events(event_type);
