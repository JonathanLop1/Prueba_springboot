-- V2: Relations and additional constraints
-- This migration adds any additional relationship constraints and optimizations

-- Add foreign key indexes for better join performance (if not already created in V1)
-- These are already handled in V1, but this file exists for future enhancements

-- Add batch size optimization hint
COMMENT ON TABLE affiliates IS 'Stores affiliate information with lazy-loaded relationships';
COMMENT ON TABLE credit_applications IS 'Stores credit application requests with affiliate relationship';
COMMENT ON TABLE risk_evaluations IS 'Stores external risk evaluation results';
COMMENT ON TABLE users IS 'Stores user authentication and authorization data';
