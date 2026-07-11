-- ============================================================
-- V6__Create_RBAC_Tables.sql
-- Creates roles, permissions, and role_permissions join table
-- Uses base_seq (Hibernate shared sequence) for id generation
-- to align with @SequenceGenerator(sequenceName = "base_seq")
-- ============================================================

-- ============================================================
-- PERMISSIONS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS permissions (
    id          BIGINT       NOT NULL DEFAULT nextval('base_seq') PRIMARY KEY,
    uuid        VARCHAR(36)  NOT NULL UNIQUE,
    code        VARCHAR(60)  NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    module      VARCHAR(100) NOT NULL,
    is_deleted  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_permissions_code   ON permissions(code);
CREATE INDEX IF NOT EXISTS idx_permissions_module ON permissions(module);

-- ============================================================
-- ROLES TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS roles (
    id           BIGINT       NOT NULL DEFAULT nextval('base_seq') PRIMARY KEY,
    uuid         VARCHAR(36)  NOT NULL UNIQUE,
    name         VARCHAR(30)  NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    description  VARCHAR(500),
    is_deleted   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP,
    created_by   VARCHAR(100),
    updated_by   VARCHAR(100),

    CONSTRAINT chk_roles_name CHECK (name IN ('ROLE_ADMIN','ROLE_JUDGE','ROLE_CLERK','ROLE_ADVOCATE'))
);

CREATE INDEX IF NOT EXISTS idx_roles_name ON roles(name);

-- ============================================================
-- ROLE_PERMISSIONS JOIN TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id       BIGINT NOT NULL REFERENCES roles(id)       ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

COMMENT ON TABLE roles            IS 'RBAC roles mapping to UserRole enum';
COMMENT ON TABLE permissions      IS 'Fine-grained permission codes for method-level security';
COMMENT ON TABLE role_permissions IS 'Many-to-many mapping between roles and permissions';
