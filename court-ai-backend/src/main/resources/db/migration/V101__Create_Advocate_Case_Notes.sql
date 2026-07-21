CREATE TABLE advocate_case_notes (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(255) NOT NULL UNIQUE,
    case_id BIGINT NOT NULL,
    advocate_id BIGINT NOT NULL,
    note_title VARCHAR(300) NOT NULL,
    note_content TEXT NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    
    CONSTRAINT fk_anote_case FOREIGN KEY (case_id) REFERENCES case_files (id) ON DELETE CASCADE,
    CONSTRAINT fk_anote_advocate FOREIGN KEY (advocate_id) REFERENCES advocate_profiles (id) ON DELETE CASCADE
);

CREATE INDEX idx_anote_case_id ON advocate_case_notes(case_id);
CREATE INDEX idx_anote_advocate_id ON advocate_case_notes(advocate_id);
CREATE INDEX idx_anote_is_deleted ON advocate_case_notes(is_deleted);
