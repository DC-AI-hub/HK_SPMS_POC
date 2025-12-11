-- V20250829_0000__Create_system_config_tables.sql
-- Create system configuration tables for dynamic system settings management

CREATE TABLE system_config (
    id BIGSERIAL PRIMARY KEY,
    key VARCHAR(255) NOT NULL UNIQUE,
    value TEXT NOT NULL,
    data_type VARCHAR(20) NOT NULL CHECK (data_type IN ('STRING', 'NUMBER', 'BOOLEAN', 'JSON', 'ARRAY')),
    description TEXT,
    category VARCHAR(100),
    is_read_only BOOLEAN DEFAULT FALSE,
    default_value TEXT,
    validation_rules TEXT,
    created_by VARCHAR(255) NOT NULL,
    modified_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE system_config_history (
    id BIGSERIAL PRIMARY KEY,
    config_id BIGINT NOT NULL,
    old_value TEXT,
    new_value TEXT NOT NULL,
    change_type VARCHAR(20) NOT NULL CHECK (change_type IN ('CREATE', 'UPDATE', 'DELETE', 'RESET')),
    changed_by VARCHAR(255) NOT NULL,
    change_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (config_id) REFERENCES system_config(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_system_config_key ON system_config(key);
CREATE INDEX idx_system_config_category ON system_config(category);
CREATE INDEX idx_system_config_data_type ON system_config(data_type);
CREATE INDEX idx_system_config_history_config_id ON system_config_history(config_id);
CREATE INDEX idx_system_config_history_change_timestamp ON system_config_history(change_timestamp);
CREATE INDEX idx_system_config_history_changed_by ON system_config_history(changed_by);

-- Insert initial system configuration data
INSERT INTO system_config (key, value, data_type, description, category, is_read_only, default_value, validation_rules, created_by, modified_by)
VALUES 
('system.name', 'SPMS Application', 'STRING', 'The name of the application', 'General', TRUE, 'SPMS Application', '{"minLength": 1, "maxLength": 100}', 'system', 'system'),
('system.version', '1.0.0', 'STRING', 'The current version of the application', 'General', TRUE, '1.0.0', '{"pattern": "^\\d+\\.\\d+\\.\\d+$"}', 'system', 'system'),
('system.theme.primaryColor', '#007bff', 'STRING', 'Primary color for the application theme', 'Theme', FALSE, '#007bff', '{"pattern": "^#[0-9A-Fa-f]{6}$"}', 'system', 'system'),
('system.theme.secondaryColor', '#6c757d', 'STRING', 'Secondary color for the application theme', 'Theme', FALSE, '#6c757d', '{"pattern": "^#[0-9A-Fa-f]{6}$"}', 'system', 'system'),
('system.features.darkMode', 'true', 'BOOLEAN', 'Enable dark mode feature', 'Features', FALSE, 'true', '{}', 'system', 'system'),
('system.features.notifications', 'true', 'BOOLEAN', 'Enable notifications feature', 'Features', FALSE, 'true', '{}', 'system', 'system'),
('system.session.timeout', '3600', 'NUMBER', 'Session timeout in seconds', 'Security', FALSE, '3600', '{"min": 300, "max": 86400}', 'system', 'system'),
('system.logging.level', 'INFO', 'STRING', 'Default logging level', 'Logging', FALSE, 'INFO', '{"enum": ["TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL"]}', 'system', 'system');
