-- V3: Initial data (optional)
-- Insert sample users and affiliates for testing

-- Insert admin user
-- All passwords are: Admin123
-- BCrypt hash generated via application
INSERT INTO users (username, password, email, enabled) VALUES
('admin', '$2a$10$IVf1NfcgVrWiv8cwc6jdkeRcTnLV8BHvs74JWl8UhCJMuAK5/83zK', 'admin@coopcredit.com', true),
('analyst1', '$2a$10$IVf1NfcgVrWiv8cwc6jdkeRcTnLV8BHvs74JWl8UhCJMuAK5/83zK', 'analyst@coopcredit.com', true),
('affiliate1', '$2a$10$IVf1NfcgVrWiv8cwc6jdkeRcTnLV8BHvs74JWl8UhCJMuAK5/83zK', 'affiliate@coopcredit.com', true);

-- Insert roles
INSERT INTO user_roles (user_id, role) VALUES
((SELECT id FROM users WHERE username = 'admin'), 'ROLE_ADMIN'),
((SELECT id FROM users WHERE username = 'analyst1'), 'ROLE_ANALISTA'),
((SELECT id FROM users WHERE username = 'affiliate1'), 'ROLE_AFILIADO');

-- Insert sample affiliates  
INSERT INTO affiliates (document, full_name, salary, affiliation_date, status) VALUES
('1017654321', 'Juan Pérez García', 3500000.00, '2023-01-15', 'ACTIVE'),
('1098765432', 'María Rodríguez López', 5000000.00, '2022-06-20', 'ACTIVE'),
('1234567890', 'Carlos Martínez Silva', 2800000.00, '2024-10-01', 'ACTIVE');
