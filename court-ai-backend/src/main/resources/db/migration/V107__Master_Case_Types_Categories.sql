-- ============================================================
-- V107__Master_Case_Types_Categories.sql
-- Flyway Migration: Enterprise Master Data module
-- Creates case_types and alters case_categories to link to it.
-- ============================================================

-- ============================================================
-- CASE TYPES
-- ============================================================
CREATE TABLE case_types (
    id              BIGSERIAL       PRIMARY KEY,
    uuid            VARCHAR(36)     NOT NULL UNIQUE,
    type_code       VARCHAR(20)     NOT NULL UNIQUE,
    type_name       VARCHAR(100)    NOT NULL,
    description     VARCHAR(500),
    display_order   INT,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100)
);

CREATE INDEX idx_case_types_code       ON case_types(type_code);
CREATE INDEX idx_case_types_is_deleted ON case_types(is_deleted);
COMMENT ON TABLE case_types IS 'Master case types like Civil, Criminal, etc.';

-- ============================================================
-- SEED CASE TYPES
-- ============================================================
INSERT INTO case_types (uuid, type_code, type_name, display_order) VALUES
(gen_random_uuid()::varchar, 'CIVIL', 'Civil', 1),
(gen_random_uuid()::varchar, 'CRIMINAL', 'Criminal', 2),
(gen_random_uuid()::varchar, 'FAMILY', 'Family', 3),
(gen_random_uuid()::varchar, 'COMMERCIAL', 'Commercial', 4),
(gen_random_uuid()::varchar, 'CONSUMER', 'Consumer', 5),
(gen_random_uuid()::varchar, 'LABOUR', 'Labour', 6),
(gen_random_uuid()::varchar, 'TAX', 'Tax', 7),
(gen_random_uuid()::varchar, 'MACT', 'Motor Accident (MACT)', 8),
(gen_random_uuid()::varchar, 'CONSTITUTIONAL', 'Constitutional', 9),
(gen_random_uuid()::varchar, 'ENVIRONMENT', 'Environment', 10),
(gen_random_uuid()::varchar, 'REVENUE', 'Revenue', 11),
(gen_random_uuid()::varchar, 'SERVICE', 'Service', 12),
(gen_random_uuid()::varchar, 'CYBER', 'Cyber', 13),
(gen_random_uuid()::varchar, 'ELECTION', 'Election', 14),
(gen_random_uuid()::varchar, 'ELECTRICITY', 'Electricity', 15),
(gen_random_uuid()::varchar, 'EDUCATION', 'Education', 16),
(gen_random_uuid()::varchar, 'ARBITRATION', 'Arbitration', 17),
(gen_random_uuid()::varchar, 'COMPANY_LAW', 'Company Law', 18),
(gen_random_uuid()::varchar, 'IPR', 'Intellectual Property', 19),
(gen_random_uuid()::varchar, 'OTHER', 'Other', 20);

-- ============================================================
-- MODIFY CASE CATEGORIES
-- ============================================================
-- Delete existing arbitrary case_categories before altering schema to avoid constraint violations
DELETE FROM case_categories;

ALTER TABLE case_categories 
ADD COLUMN case_type_id BIGINT;

ALTER TABLE case_categories
ADD CONSTRAINT fk_category_case_type FOREIGN KEY (case_type_id) REFERENCES case_types(id) ON DELETE RESTRICT;

CREATE INDEX idx_case_categories_case_type_id ON case_categories(case_type_id);

-- ============================================================
-- SEED CASE CATEGORIES
-- ============================================================
DO $$
DECLARE
    v_civil_id BIGINT;
    v_criminal_id BIGINT;
    v_family_id BIGINT;
    v_commercial_id BIGINT;
    v_consumer_id BIGINT;
    v_labour_id BIGINT;
    v_tax_id BIGINT;
    v_mact_id BIGINT;
    v_constitutional_id BIGINT;
    v_environment_id BIGINT;
    v_revenue_id BIGINT;
    v_service_id BIGINT;
    v_cyber_id BIGINT;
    v_election_id BIGINT;
    v_electricity_id BIGINT;
    v_education_id BIGINT;
    v_arbitration_id BIGINT;
    v_company_law_id BIGINT;
    v_ipr_id BIGINT;
    v_other_id BIGINT;
BEGIN
    SELECT id INTO v_civil_id FROM case_types WHERE type_code = 'CIVIL';
    SELECT id INTO v_criminal_id FROM case_types WHERE type_code = 'CRIMINAL';
    SELECT id INTO v_family_id FROM case_types WHERE type_code = 'FAMILY';
    SELECT id INTO v_commercial_id FROM case_types WHERE type_code = 'COMMERCIAL';
    SELECT id INTO v_consumer_id FROM case_types WHERE type_code = 'CONSUMER';
    SELECT id INTO v_labour_id FROM case_types WHERE type_code = 'LABOUR';
    SELECT id INTO v_tax_id FROM case_types WHERE type_code = 'TAX';
    SELECT id INTO v_mact_id FROM case_types WHERE type_code = 'MACT';
    SELECT id INTO v_constitutional_id FROM case_types WHERE type_code = 'CONSTITUTIONAL';
    SELECT id INTO v_environment_id FROM case_types WHERE type_code = 'ENVIRONMENT';
    SELECT id INTO v_revenue_id FROM case_types WHERE type_code = 'REVENUE';
    SELECT id INTO v_service_id FROM case_types WHERE type_code = 'SERVICE';
    SELECT id INTO v_cyber_id FROM case_types WHERE type_code = 'CYBER';
    SELECT id INTO v_election_id FROM case_types WHERE type_code = 'ELECTION';
    SELECT id INTO v_electricity_id FROM case_types WHERE type_code = 'ELECTRICITY';
    SELECT id INTO v_education_id FROM case_types WHERE type_code = 'EDUCATION';
    SELECT id INTO v_arbitration_id FROM case_types WHERE type_code = 'ARBITRATION';
    SELECT id INTO v_company_law_id FROM case_types WHERE type_code = 'COMPANY_LAW';
    SELECT id INTO v_ipr_id FROM case_types WHERE type_code = 'IPR';
    SELECT id INTO v_other_id FROM case_types WHERE type_code = 'OTHER';

    -- CIVIL
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_PROP', 'Property Dispute'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_PART', 'Partition Suit'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_MREC', 'Money Recovery Suit'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_DEBT', 'Recovery of Debt'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_CONT', 'Contract Dispute'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_BREACH', 'Breach of Contract'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_SPEC', 'Specific Performance'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_DECL', 'Declaration Suit'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_PINJ', 'Permanent Injunction'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_TINJ', 'Temporary Injunction'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_MINJ', 'Mandatory Injunction'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_POSS', 'Possession Suit'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_EVIC', 'Eviction Suit'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_RENT', 'Rent Control Matter'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_LAND', 'Land Acquisition'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_EASE', 'Easement Rights'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_MORT', 'Mortgage Suit'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_PROB', 'Probate'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_SUCC', 'Succession Certificate'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_LTR', 'Letters of Administration'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_TRST', 'Trust Dispute'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_PARTN', 'Partnership Dispute'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_COMP', 'Company Petition'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_ARBP', 'Arbitration Petition'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_EXEC', 'Execution Petition'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_APP', 'Civil Appeal'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_REV', 'Civil Revision'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_RVW', 'Civil Review'),
    (gen_random_uuid()::varchar, v_civil_id, 'CIV_MISC', 'Miscellaneous Civil Petition');

    -- CRIMINAL
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_MUR', 'Murder'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_AMUR', 'Attempt to Murder'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_THFT', 'Theft'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_ROB', 'Robbery'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_DAC', 'Dacoity'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_BURG', 'Burglary'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_TRES', 'House Trespass'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_CHET', 'Cheating'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_BREA', 'Criminal Breach of Trust'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_FORG', 'Forgery'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_FRAU', 'Fraud'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_KID', 'Kidnapping'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_ASSL', 'Assault'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_DOME', 'Domestic Violence'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_DOWR', 'Dowry Harassment'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_SEX', 'Sexual Harassment'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_RAPE', 'Rape'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_POCSO', 'POCSO'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_CYBR', 'Cyber Crime'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_ECON', 'Economic Offence'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_CORR', 'Corruption'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_MONY', 'Money Laundering'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_NDPS', 'NDPS'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_BAIL', 'Bail Application'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_ABAIL', 'Anticipatory Bail'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_APP', 'Criminal Appeal'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_REV', 'Criminal Revision'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_FIR', 'FIR Matter'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_CHRG', 'Charge Sheet Matter'),
    (gen_random_uuid()::varchar, v_criminal_id, 'CRM_MISC', 'Miscellaneous Criminal Petition');

    -- FAMILY
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_family_id, 'FAM_DIV', 'Divorce'),
    (gen_random_uuid()::varchar, v_family_id, 'FAM_MDIV', 'Mutual Divorce'),
    (gen_random_uuid()::varchar, v_family_id, 'FAM_JSEP', 'Judicial Separation'),
    (gen_random_uuid()::varchar, v_family_id, 'FAM_CUST', 'Child Custody'),
    (gen_random_uuid()::varchar, v_family_id, 'FAM_VISI', 'Child Visitation'),
    (gen_random_uuid()::varchar, v_family_id, 'FAM_GUAR', 'Guardianship'),
    (gen_random_uuid()::varchar, v_family_id, 'FAM_ADOP', 'Adoption'),
    (gen_random_uuid()::varchar, v_family_id, 'FAM_MAIN', 'Maintenance'),
    (gen_random_uuid()::varchar, v_family_id, 'FAM_DOM', 'Domestic Violence'),
    (gen_random_uuid()::varchar, v_family_id, 'FAM_REG', 'Marriage Registration'),
    (gen_random_uuid()::varchar, v_family_id, 'FAM_SETT', 'Family Settlement'),
    (gen_random_uuid()::varchar, v_family_id, 'FAM_PROP', 'Family Property Dispute'),
    (gen_random_uuid()::varchar, v_family_id, 'FAM_SUCC', 'Succession');

    -- COMMERCIAL
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_commercial_id, 'COM_BANK', 'Banking Dispute'),
    (gen_random_uuid()::varchar, v_commercial_id, 'COM_INSU', 'Insurance Claim'),
    (gen_random_uuid()::varchar, v_commercial_id, 'COM_COMP', 'Company Dispute'),
    (gen_random_uuid()::varchar, v_commercial_id, 'COM_SHAR', 'Shareholder Dispute'),
    (gen_random_uuid()::varchar, v_commercial_id, 'COM_INSO', 'Insolvency'),
    (gen_random_uuid()::varchar, v_commercial_id, 'COM_BANKR', 'Bankruptcy'),
    (gen_random_uuid()::varchar, v_commercial_id, 'COM_MSME', 'MSME Recovery'),
    (gen_random_uuid()::varchar, v_commercial_id, 'COM_IP', 'Intellectual Property'),
    (gen_random_uuid()::varchar, v_commercial_id, 'COM_TEND', 'Tender Dispute'),
    (gen_random_uuid()::varchar, v_commercial_id, 'COM_ARB', 'Commercial Arbitration');

    -- CONSUMER
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_consumer_id, 'CNS_GOOD', 'Defective Goods'),
    (gen_random_uuid()::varchar, v_consumer_id, 'CNS_SERV', 'Deficiency in Service'),
    (gen_random_uuid()::varchar, v_consumer_id, 'CNS_MED', 'Medical Negligence'),
    (gen_random_uuid()::varchar, v_consumer_id, 'CNS_BANK', 'Banking Complaint'),
    (gen_random_uuid()::varchar, v_consumer_id, 'CNS_INSU', 'Insurance Complaint'),
    (gen_random_uuid()::varchar, v_consumer_id, 'CNS_HOUS', 'Housing Complaint'),
    (gen_random_uuid()::varchar, v_consumer_id, 'CNS_EDU', 'Education Complaint'),
    (gen_random_uuid()::varchar, v_consumer_id, 'CNS_TELC', 'Telecom Complaint'),
    (gen_random_uuid()::varchar, v_consumer_id, 'CNS_ECOM', 'E-Commerce Complaint');

    -- LABOUR
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_labour_id, 'LBR_TERM', 'Illegal Termination'),
    (gen_random_uuid()::varchar, v_labour_id, 'LBR_SAL', 'Salary Recovery'),
    (gen_random_uuid()::varchar, v_labour_id, 'LBR_IND', 'Industrial Dispute'),
    (gen_random_uuid()::varchar, v_labour_id, 'LBR_PF', 'PF Dispute'),
    (gen_random_uuid()::varchar, v_labour_id, 'LBR_ESI', 'ESI Dispute'),
    (gen_random_uuid()::varchar, v_labour_id, 'LBR_GRAT', 'Gratuity'),
    (gen_random_uuid()::varchar, v_labour_id, 'LBR_BON', 'Bonus'),
    (gen_random_uuid()::varchar, v_labour_id, 'LBR_PROM', 'Promotion'),
    (gen_random_uuid()::varchar, v_labour_id, 'LBR_PENS', 'Pension'),
    (gen_random_uuid()::varchar, v_labour_id, 'LBR_SERV', 'Service Matter');

    -- TAX
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_tax_id, 'TAX_INC', 'Income Tax Appeal'),
    (gen_random_uuid()::varchar, v_tax_id, 'TAX_GST', 'GST Appeal'),
    (gen_random_uuid()::varchar, v_tax_id, 'TAX_CUST', 'Customs'),
    (gen_random_uuid()::varchar, v_tax_id, 'TAX_EXC', 'Excise'),
    (gen_random_uuid()::varchar, v_tax_id, 'TAX_VAT', 'VAT'),
    (gen_random_uuid()::varchar, v_tax_id, 'TAX_SRV', 'Service Tax'),
    (gen_random_uuid()::varchar, v_tax_id, 'TAX_PROP', 'Property Tax'),
    (gen_random_uuid()::varchar, v_tax_id, 'TAX_REC', 'Tax Recovery');

    -- MOTOR ACCIDENT
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_mact_id, 'MAC_COMP', 'Compensation Claim'),
    (gen_random_uuid()::varchar, v_mact_id, 'MAC_FATL', 'Fatal Accident'),
    (gen_random_uuid()::varchar, v_mact_id, 'MAC_INJ', 'Injury Compensation'),
    (gen_random_uuid()::varchar, v_mact_id, 'MAC_INSU', 'Insurance Claim'),
    (gen_random_uuid()::varchar, v_mact_id, 'MAC_APP', 'MACT Appeal');

    -- CONSTITUTIONAL
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_constitutional_id, 'CON_WRIT', 'Writ Petition'),
    (gen_random_uuid()::varchar, v_constitutional_id, 'CON_HAB', 'Habeas Corpus'),
    (gen_random_uuid()::varchar, v_constitutional_id, 'CON_MAN', 'Mandamus'),
    (gen_random_uuid()::varchar, v_constitutional_id, 'CON_CERT', 'Certiorari'),
    (gen_random_uuid()::varchar, v_constitutional_id, 'CON_PROH', 'Prohibition'),
    (gen_random_uuid()::varchar, v_constitutional_id, 'CON_QUO', 'Quo Warranto'),
    (gen_random_uuid()::varchar, v_constitutional_id, 'CON_PIL', 'Public Interest Litigation'),
    (gen_random_uuid()::varchar, v_constitutional_id, 'CON_FUND', 'Fundamental Rights'),
    (gen_random_uuid()::varchar, v_constitutional_id, 'CON_ELEC', 'Election Petition');

    -- ENVIRONMENT
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_environment_id, 'ENV_AIR', 'Air Pollution'),
    (gen_random_uuid()::varchar, v_environment_id, 'ENV_WAT', 'Water Pollution'),
    (gen_random_uuid()::varchar, v_environment_id, 'ENV_FOR', 'Forest Matter'),
    (gen_random_uuid()::varchar, v_environment_id, 'ENV_WILD', 'Wildlife'),
    (gen_random_uuid()::varchar, v_environment_id, 'ENV_MIN', 'Mining'),
    (gen_random_uuid()::varchar, v_environment_id, 'ENV_CLR', 'Environmental Clearance');

    -- REVENUE
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_revenue_id, 'REV_MUT', 'Mutation'),
    (gen_random_uuid()::varchar, v_revenue_id, 'REV_PAT', 'Patta'),
    (gen_random_uuid()::varchar, v_revenue_id, 'REV_SURV', 'Survey Dispute'),
    (gen_random_uuid()::varchar, v_revenue_id, 'REV_REC', 'Land Records'),
    (gen_random_uuid()::varchar, v_revenue_id, 'REV_APP', 'Revenue Appeal');

    -- SERVICE
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_service_id, 'SRV_REC', 'Recruitment'),
    (gen_random_uuid()::varchar, v_service_id, 'SRV_PROM', 'Promotion'),
    (gen_random_uuid()::varchar, v_service_id, 'SRV_TRAN', 'Transfer'),
    (gen_random_uuid()::varchar, v_service_id, 'SRV_SUSP', 'Suspension'),
    (gen_random_uuid()::varchar, v_service_id, 'SRV_PENS', 'Pension'),
    (gen_random_uuid()::varchar, v_service_id, 'SRV_SEN', 'Seniority'),
    (gen_random_uuid()::varchar, v_service_id, 'SRV_RET', 'Retirement');

    -- CYBER
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_cyber_id, 'CYB_ONL', 'Online Fraud'),
    (gen_random_uuid()::varchar, v_cyber_id, 'CYB_ID', 'Identity Theft'),
    (gen_random_uuid()::varchar, v_cyber_id, 'CYB_HACK', 'Hacking'),
    (gen_random_uuid()::varchar, v_cyber_id, 'CYB_PHIS', 'Phishing'),
    (gen_random_uuid()::varchar, v_cyber_id, 'CYB_DATA', 'Data Theft'),
    (gen_random_uuid()::varchar, v_cyber_id, 'CYB_STLK', 'Cyber Stalking'),
    (gen_random_uuid()::varchar, v_cyber_id, 'CYB_DEF', 'Digital Defamation');

    -- ELECTION
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_election_id, 'ELC_PET', 'Election Petition'),
    (gen_random_uuid()::varchar, v_election_id, 'ELC_DISQ', 'Candidate Disqualification'),
    (gen_random_uuid()::varchar, v_election_id, 'ELC_ROLL', 'Electoral Roll Dispute'),
    (gen_random_uuid()::varchar, v_election_id, 'ELC_LOC', 'Local Body Election');

    -- ELECTRICITY
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_electricity_id, 'ELE_THFT', 'Electricity Theft'),
    (gen_random_uuid()::varchar, v_electricity_id, 'ELE_BILL', 'Wrong Billing'),
    (gen_random_uuid()::varchar, v_electricity_id, 'ELE_MTR', 'Meter Dispute'),
    (gen_random_uuid()::varchar, v_electricity_id, 'ELE_SUPP', 'Power Supply Dispute');

    -- EDUCATION
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_education_id, 'EDU_ADM', 'Admission Dispute'),
    (gen_random_uuid()::varchar, v_education_id, 'EDU_EXAM', 'Examination Dispute'),
    (gen_random_uuid()::varchar, v_education_id, 'EDU_DEG', 'Degree Verification'),
    (gen_random_uuid()::varchar, v_education_id, 'EDU_SCH', 'Scholarship');

    -- ARBITRATION
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_arbitration_id, 'ARB_DOM', 'Domestic Arbitration'),
    (gen_random_uuid()::varchar, v_arbitration_id, 'ARB_INTL', 'International Arbitration'),
    (gen_random_uuid()::varchar, v_arbitration_id, 'ARB_AWRD', 'Arbitration Award'),
    (gen_random_uuid()::varchar, v_arbitration_id, 'ARB_APP', 'Arbitration Appeal');

    -- COMPANY LAW
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_company_law_id, 'CMP_OPP', 'Oppression & Mismanagement'),
    (gen_random_uuid()::varchar, v_company_law_id, 'CMP_WIND', 'Winding Up'),
    (gen_random_uuid()::varchar, v_company_law_id, 'CMP_MRG', 'Merger'),
    (gen_random_uuid()::varchar, v_company_law_id, 'CMP_DISQ', 'Director Disqualification');

    -- INTELLECTUAL PROPERTY
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_ipr_id, 'IPR_TM', 'Trademark'),
    (gen_random_uuid()::varchar, v_ipr_id, 'IPR_COPY', 'Copyright'),
    (gen_random_uuid()::varchar, v_ipr_id, 'IPR_PAT', 'Patent'),
    (gen_random_uuid()::varchar, v_ipr_id, 'IPR_DES', 'Design Registration'),
    (gen_random_uuid()::varchar, v_ipr_id, 'IPR_TRD', 'Trade Secret');

    -- OTHER
    INSERT INTO case_categories (uuid, case_type_id, category_code, category_name) VALUES
    (gen_random_uuid()::varchar, v_other_id, 'OTH_CONT', 'Contempt Petition'),
    (gen_random_uuid()::varchar, v_other_id, 'OTH_REV', 'Review Petition'),
    (gen_random_uuid()::varchar, v_other_id, 'OTH_REVI', 'Revision Petition'),
    (gen_random_uuid()::varchar, v_other_id, 'OTH_TRAN', 'Transfer Petition'),
    (gen_random_uuid()::varchar, v_other_id, 'OTH_CAV', 'Caveat Petition'),
    (gen_random_uuid()::varchar, v_other_id, 'OTH_CUR', 'Curative Petition'),
    (gen_random_uuid()::varchar, v_other_id, 'OTH_HR', 'Human Rights'),
    (gen_random_uuid()::varchar, v_other_id, 'OTH_TRIB', 'Tribunal Appeal'),
    (gen_random_uuid()::varchar, v_other_id, 'OTH_LOK', 'Lok Adalat Matter');
END $$;

-- ============================================================
-- MODIFY CASE FILES
-- ============================================================
ALTER TABLE case_files
ADD COLUMN case_type_id BIGINT;

-- Link up case_files case_type_id based on string case_type
UPDATE case_files cf
SET case_type_id = ct.id
FROM case_types ct
WHERE cf.case_type = ct.type_code;

ALTER TABLE case_files 
ADD CONSTRAINT fk_case_file_type FOREIGN KEY (case_type_id) REFERENCES case_types(id) ON DELETE RESTRICT;

ALTER TABLE case_files DROP COLUMN case_type;

CREATE INDEX idx_case_files_type_id ON case_files(case_type_id);
