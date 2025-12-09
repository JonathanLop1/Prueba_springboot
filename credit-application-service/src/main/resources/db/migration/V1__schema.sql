-- V1: Initial schema creation
-- Create tables for users, affiliates, credit_applications, risk_evaluations

-- Users table for authentication
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- User roles table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Affiliates table
CREATE TABLE affiliates (
    id BIGSERIAL PRIMARY KEY,
    document VARCHAR(15) NOT NULL UNIQUE,
    full_name VARCHAR(200) NOT NULL,
    salary DECIMAL(15, 2) NOT NULL,
    affiliation_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT chk_salary_positive CHECK (salary > 0),
    CONSTRAINT chk_status_valid CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

-- Credit applications table
CREATE TABLE credit_applications (
    id BIGSERIAL PRIMARY KEY,
    affiliate_id BIGINT NOT NULL,
    requested_amount DECIMAL(15, 2) NOT NULL,
    term_months INTEGER NOT NULL,
    proposed_rate DECIMAL(5, 2) NOT NULL,
    application_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    rejection_reason VARCHAR(500),
    CONSTRAINT fk_application_affiliate FOREIGN KEY (affiliate_id) REFERENCES affiliates(id),
    CONSTRAINT chk_amount_positive CHECK (requested_amount > 0),
    CONSTRAINT chk_term_positive CHECK (term_months > 0),
    CONSTRAINT chk_rate_positive CHECK (proposed_rate > 0),
    CONSTRAINT chk_application_status_valid CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

-- Risk evaluations table
CREATE TABLE risk_evaluations (
    id BIGSERIAL PRIMARY KEY,
    credit_application_id BIGINT NOT NULL UNIQUE,
    document VARCHAR(15) NOT NULL,
    score INTEGER NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    detail VARCHAR(500),
    evaluation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_evaluation_application FOREIGN KEY (credit_application_id) REFERENCES credit_applications(id),
    CONSTRAINT chk_score_range CHECK (score >= 300 AND score <= 950),
    CONSTRAINT chk_risk_level_valid CHECK (risk_level IN ('LOW', 'MEDIUM', 'HIGH'))
);

-- Create indexes for performance
CREATE INDEX idx_affiliate_document ON affiliates(document);
CREATE INDEX idx_application_status ON credit_applications(status);
CREATE INDEX idx_application_affiliate ON credit_applications(affiliate_id);
CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_user_email ON users(email);
