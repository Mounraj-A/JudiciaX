-- Add extended fields to case_parties for detailed Petitioner/Respondent info
ALTER TABLE case_parties
ADD COLUMN party_category VARCHAR(50), -- INDIVIDUAL, ORGANIZATION
ADD COLUMN gender VARCHAR(20),
ADD COLUMN date_of_birth DATE,
ADD COLUMN aadhaar_number VARCHAR(100),
ADD COLUMN district VARCHAR(100),
ADD COLUMN state VARCHAR(100),
ADD COLUMN pin_code VARCHAR(20),
ADD COLUMN occupation VARCHAR(100),
ADD COLUMN representative_name VARCHAR(200);

-- Create table for Legal Information
CREATE TABLE case_legal_info (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(255) NOT NULL UNIQUE,
    case_id BIGINT NOT NULL UNIQUE,
    
    -- Police/FIR details
    police_station VARCHAR(200),
    fir_number VARCHAR(100),
    crime_number VARCHAR(100),
    previous_case_number VARCHAR(100),
    
    -- Legal Provisions (stored as JSON arrays or text for simplicity)
    acts TEXT,
    sections TEXT,
    rules TEXT,
    articles TEXT,
    legal_provisions TEXT,
    precedent_references TEXT,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_legal_info_case FOREIGN KEY (case_id) REFERENCES case_files (id) ON DELETE CASCADE
);
CREATE INDEX idx_legal_info_case ON case_legal_info(case_id);

-- Create table for Case Payments
CREATE TABLE case_payments (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(255) NOT NULL UNIQUE,
    case_id BIGINT NOT NULL,
    advocate_id BIGINT NOT NULL,
    
    amount DECIMAL(10,2) NOT NULL,
    payment_status VARCHAR(50) NOT NULL, -- PENDING, COMPLETED, FAILED
    payment_method VARCHAR(50),          -- CREDIT_CARD, UPI, NET_BANKING
    transaction_id VARCHAR(255),
    receipt_url VARCHAR(500),
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_payment_case FOREIGN KEY (case_id) REFERENCES case_files (id) ON DELETE CASCADE,
    CONSTRAINT fk_payment_advocate FOREIGN KEY (advocate_id) REFERENCES advocate_profiles (id) ON DELETE CASCADE
);
CREATE INDEX idx_payment_case ON case_payments(case_id);
CREATE INDEX idx_payment_advocate ON case_payments(advocate_id);
