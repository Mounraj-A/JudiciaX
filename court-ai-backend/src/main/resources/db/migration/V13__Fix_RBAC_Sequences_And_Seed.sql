-- ============================================================
-- V13__Fix_RBAC_Sequences_And_Seed.sql
-- 1. Ensures base_seq default on id columns (for Hibernate-created tables)
-- 2. Seeds all RBAC permissions, roles, and role-permission mappings
-- ============================================================

-- Ensure base_seq exists
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_sequences WHERE schemaname = 'public' AND sequencename = 'base_seq') THEN
        CREATE SEQUENCE base_seq START 1 INCREMENT 50;
    END IF;
END $$;

-- Fix id defaults on all auth/RBAC tables that Hibernate may have pre-created
DO $$
DECLARE
    tbl TEXT;
    tbls TEXT[] := ARRAY[
        'permissions','roles',
        'refresh_tokens','password_reset_tokens','email_verification_tokens',
        'mobile_otps','password_histories','user_sessions','login_history',
        'security_events','user_privacy_settings'
    ];
BEGIN
    FOREACH tbl IN ARRAY tbls
    LOOP
        IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema='public' AND table_name=tbl)
           AND EXISTS (
               SELECT 1 FROM information_schema.columns
               WHERE table_schema='public' AND table_name=tbl AND column_name='id'
                 AND (column_default IS NULL OR column_default NOT LIKE '%base_seq%')
           )
        THEN
            EXECUTE format('ALTER TABLE %I ALTER COLUMN id SET DEFAULT nextval(''base_seq'')', tbl);
            -- Also ensure created_at has a default for seed rows
            EXECUTE format('ALTER TABLE %I ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP', tbl);
        END IF;
    END LOOP;
END $$;

-- ============================================================
-- SEED: PERMISSIONS
-- ============================================================
INSERT INTO permissions (uuid, code, name, description, module, is_deleted, created_at, created_by) VALUES
('10000001-0000-0000-0000-000000000001','CREATE_CASE',        'Create Case',         'File a new case',                    'CASE_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000002','UPLOAD_DOCUMENT',    'Upload Document',      'Upload case documents',              'CASE_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000003','TRACK_CASE',         'Track Case',           'Track own case status',              'CASE_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000004','DOWNLOAD_ORDER',     'Download Order',       'Download court orders',              'CASE_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000005','VIEW_CASE_DETAILS',  'View Case Details',    'View detailed case information',     'CASE_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000011','FILE_CASE',          'File Case',            'Officially file a case',             'CASE_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000012','ASSIGN_JUDGE',       'Assign Judge',         'Assign judge to a case',             'CASE_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000013','MANAGE_SCHEDULE',    'Manage Schedule',      'Schedule and reschedule hearings',   'CASE_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000014','MANAGE_DOCUMENTS',   'Manage Documents',     'Manage all case documents',          'CASE_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000015','VIEW_ALL_CASES',     'View All Cases',       'View all cases in the system',       'CASE_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000021','VIEW_CASE',          'View Case',            'View assigned cases',                'CASE_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000022','MODIFY_PRIORITY',    'Modify Priority',      'Change case priority',               'AI_MANAGEMENT',   FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000023','SCHEDULE_HEARING',   'Schedule Hearing',     'Schedule court hearings',            'CASE_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000024','UPLOAD_ORDER',       'Upload Order',         'Upload court orders',                'CASE_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000025','SET_VERDICT',        'Set Verdict',          'Record case verdict',                'CASE_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000026','REVIEW_AI_ANALYSIS', 'Review AI Analysis',   'View and act on AI case analysis',   'AI_MANAGEMENT',   FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000031','MANAGE_USERS',       'Manage Users',         'Full user management access',        'USER_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000032','VIEW_AUDIT',         'View Audit Logs',      'Access system audit logs',           'AUDIT',           FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000033','SYSTEM_SETTINGS',    'System Settings',      'Configure system settings',          'SYSTEM',          FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000034','APPROVE_USER',       'Approve User',         'Approve user registrations',         'USER_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000035','REJECT_USER',        'Reject User',          'Reject user registrations',          'USER_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000036','LOCK_USER',          'Lock User',            'Lock user accounts',                 'USER_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000037','UNLOCK_USER',        'Unlock User',          'Unlock user accounts',               'USER_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000038','RESET_PASSWORD',     'Reset Password',       'Reset user passwords',               'USER_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000039','ASSIGN_ROLE',        'Assign Role',          'Change user roles',                  'USER_MANAGEMENT', FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000040','VIEW_SECURITY_EVENTS','View Security Events','View security monitoring events',    'SECURITY',        FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000041','VIEW_DASHBOARD',     'View Dashboard',       'Access admin dashboard',             'SYSTEM',          FALSE, NOW(), 'SYSTEM'),
('10000001-0000-0000-0000-000000000042','MANAGE_NOTIFICATIONS','Manage Notifications','Manage system notifications',        'SYSTEM',          FALSE, NOW(), 'SYSTEM')
ON CONFLICT (code) DO NOTHING;

-- ============================================================
-- SEED: ROLES
-- ============================================================
INSERT INTO roles (uuid, name, display_name, description, is_deleted, created_at, created_by) VALUES
('20000001-0000-0000-0000-000000000001','ROLE_ADMIN',    'System Administrator','Full system access',         FALSE, NOW(), 'SYSTEM'),
('20000001-0000-0000-0000-000000000002','ROLE_JUDGE',    'Judge',               'Judicial authority',         FALSE, NOW(), 'SYSTEM'),
('20000001-0000-0000-0000-000000000003','ROLE_CLERK',    'Court Clerk',         'Case filing and scheduling', FALSE, NOW(), 'SYSTEM'),
('20000001-0000-0000-0000-000000000004','ROLE_ADVOCATE', 'Advocate',            'Client representation',      FALSE, NOW(), 'SYSTEM')
ON CONFLICT (name) DO NOTHING;

-- ============================================================
-- ROLE-PERMISSION MAPPINGS
-- ============================================================

-- ROLE_ADVOCATE
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name='ROLE_ADVOCATE'
  AND p.code IN ('CREATE_CASE','UPLOAD_DOCUMENT','TRACK_CASE','DOWNLOAD_ORDER','VIEW_CASE_DETAILS')
ON CONFLICT DO NOTHING;

-- ROLE_CLERK
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name='ROLE_CLERK'
  AND p.code IN ('FILE_CASE','ASSIGN_JUDGE','MANAGE_SCHEDULE','MANAGE_DOCUMENTS','VIEW_ALL_CASES','VIEW_CASE_DETAILS','UPLOAD_DOCUMENT')
ON CONFLICT DO NOTHING;

-- ROLE_JUDGE
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name='ROLE_JUDGE'
  AND p.code IN ('VIEW_CASE','MODIFY_PRIORITY','SCHEDULE_HEARING','UPLOAD_ORDER','SET_VERDICT','REVIEW_AI_ANALYSIS','VIEW_ALL_CASES')
ON CONFLICT DO NOTHING;

-- ROLE_ADMIN gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name='ROLE_ADMIN'
ON CONFLICT DO NOTHING;
