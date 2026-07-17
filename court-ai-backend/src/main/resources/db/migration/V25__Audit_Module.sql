-- ======================================================================
-- V25__Audit_Module.sql
-- Creates tables for the Enterprise Audit & Compliance Module
-- ======================================================================

CREATE TABLE IF NOT EXISTS audit_events (
    id BIGINT PRIMARY KEY DEFAULT nextval('base_seq'),
    uuid VARCHAR(36) NOT NULL UNIQUE,
    correlation_id VARCHAR(100),
    module VARCHAR(50),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_uuid VARCHAR(36),
    actor_uuid VARCHAR(36),
    actor_role VARCHAR(50),
    actor_name VARCHAR(150),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50),
    ip_address VARCHAR(45),
    browser VARCHAR(255),
    device VARCHAR(255),
    remarks TEXT,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_audit_events_correlation_id ON audit_events(correlation_id);
CREATE INDEX idx_audit_events_module ON audit_events(module);
CREATE INDEX idx_audit_events_action ON audit_events(action);
CREATE INDEX idx_audit_events_entity_uuid ON audit_events(entity_uuid);
CREATE INDEX idx_audit_events_actor_uuid ON audit_events(actor_uuid);
CREATE INDEX idx_audit_events_timestamp ON audit_events(timestamp);

CREATE TABLE IF NOT EXISTS security_audits (
    id BIGINT PRIMARY KEY DEFAULT nextval('base_seq'),
    uuid VARCHAR(36) NOT NULL UNIQUE,
    correlation_id VARCHAR(100),
    event_type VARCHAR(100) NOT NULL,
    ip_address VARCHAR(45),
    details TEXT,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_security_audits_correlation_id ON security_audits(correlation_id);
CREATE INDEX idx_security_audits_event_type ON security_audits(event_type);

CREATE TABLE IF NOT EXISTS compliance_audits (
    id BIGINT PRIMARY KEY DEFAULT nextval('base_seq'),
    uuid VARCHAR(36) NOT NULL UNIQUE,
    correlation_id VARCHAR(100),
    violation_type VARCHAR(100) NOT NULL,
    details TEXT,
    compliance_status VARCHAR(50),
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_compliance_audits_correlation_id ON compliance_audits(correlation_id);
CREATE INDEX idx_compliance_audits_violation_type ON compliance_audits(violation_type);

CREATE TABLE IF NOT EXISTS audit_attachments (
    id BIGINT PRIMARY KEY DEFAULT nextval('base_seq'),
    uuid VARCHAR(36) NOT NULL UNIQUE,
    audit_event_uuid VARCHAR(36) NOT NULL,
    file_url VARCHAR(1000) NOT NULL,
    file_type VARCHAR(50),
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    
    CONSTRAINT fk_audit_attachment_event FOREIGN KEY (audit_event_uuid) REFERENCES audit_events(uuid) ON DELETE CASCADE
);

CREATE INDEX idx_audit_attachments_event_uuid ON audit_attachments(audit_event_uuid);

CREATE TABLE IF NOT EXISTS audit_integrity (
    id BIGINT PRIMARY KEY DEFAULT nextval('base_seq'),
    uuid VARCHAR(36) NOT NULL UNIQUE,
    audit_event_uuid VARCHAR(36) NOT NULL,
    previous_hash VARCHAR(256),
    current_hash VARCHAR(256) NOT NULL,
    verification_status VARCHAR(50),
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    
    CONSTRAINT fk_audit_integrity_event FOREIGN KEY (audit_event_uuid) REFERENCES audit_events(uuid) ON DELETE CASCADE
);

CREATE INDEX idx_audit_integrity_event_uuid ON audit_integrity(audit_event_uuid);
CREATE INDEX idx_audit_integrity_current_hash ON audit_integrity(current_hash);
