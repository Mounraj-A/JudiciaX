-- ============================================================
-- Setup Test Users for Endpoint Testing
-- ============================================================

-- Advocate test user (Test@1234)
INSERT INTO users (
    uuid, username, email, password_hash, first_name, last_name, full_name,
    phone_number, role, account_status, is_active, is_email_verified,
    is_locked, is_deleted, failed_login_attempts, profile_completion_percent,
    created_at, created_by
) VALUES (
    '99990000-0000-0000-0000-000000000001',
    'test_advocate',
    'advocate.test@judiciai.com',
    '/iHZzglg/itwfS',
    'Test', 'Advocate', 'Test Advocate',
    '9800000001', 'ROLE_ADVOCATE', 'ACTIVE', TRUE, TRUE,
    FALSE, FALSE, 0, 50,
    CURRENT_TIMESTAMP, 'TEST_SETUP'
) ON CONFLICT (email) DO UPDATE SET
    password_hash = '/iHZzglg/itwfS',
    account_status = 'ACTIVE',
    is_active = TRUE,
    is_email_verified = TRUE,
    is_locked = FALSE,
    is_deleted = FALSE;

-- Advocate profile
INSERT INTO advocate_profiles (uuid, user_id, bar_council_number, is_deleted, created_at, created_by)
SELECT '99990000-aaaa-0000-0000-000000000001', u.id, 'TEST/0001/2024', FALSE, CURRENT_TIMESTAMP, 'TEST_SETUP'
FROM users u WHERE u.email = 'advocate.test@judiciai.com'
ON CONFLICT DO NOTHING;

-- Ensure clerk profile exists for clerk.test@judiciai.com
INSERT INTO clerk_profiles (uuid, user_id, employee_id, is_deleted, created_at, created_by)
SELECT '99990000-bbbb-0000-0000-000000000001', u.id, 'CLK-TEST-001', FALSE, CURRENT_TIMESTAMP, 'TEST_SETUP'
FROM users u WHERE u.email = 'clerk.test@judiciai.com'
ON CONFLICT DO NOTHING;

SELECT 'Setup complete' AS status;