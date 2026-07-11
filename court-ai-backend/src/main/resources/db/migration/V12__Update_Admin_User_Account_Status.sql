-- ============================================================
-- V12__Update_Admin_User_Account_Status.sql
-- Updates the seeded admin user to use the new accountStatus field
-- ============================================================

UPDATE users
SET account_status       = 'ACTIVE',
    full_name            = 'System Administrator',
    is_mobile_verified   = TRUE,
    failed_login_attempts = 0,
    profile_completion_percent = 100
WHERE email = 'admin@courtai.com'
  AND is_deleted = FALSE;
