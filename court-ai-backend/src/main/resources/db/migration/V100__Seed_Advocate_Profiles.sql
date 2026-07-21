-- ============================================================
-- V100__Seed_Advocate_Profiles.sql
-- Fixes missing advocate profile for demo test user which caused 403 errors
-- ============================================================

INSERT INTO advocate_profiles (
    uuid, user_id, bar_council_number, is_verified, verification_status,
    enrollment_date, state_bar_council, law_firm, specialization, years_of_practice,
    office_address, office_city, office_state, office_pincode,
    created_by, created_at, updated_at
)
SELECT 
    'aabb0001-adv-0000-0000-000000000004', 
    id, 
    'BAR/DEMO/2026/001', 
    TRUE, 
    'APPROVED',
    '2015-06-15',
    'State Bar Council',
    'Demo & Associates',
    'Corporate Law',
    10,
    '123 Legal Avenue',
    'Metropolis',
    'State',
    '123456',
    'SYSTEM',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM users 
WHERE email = 'advocate@judiciai.com'
ON CONFLICT (bar_council_number) DO NOTHING;
