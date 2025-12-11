-- V20250827_0000__Create_menu_tables.sql
-- Create menu tables for dynamic menu control system

CREATE TABLE spms_menu (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    path VARCHAR(255) NOT NULL,
    icon VARCHAR(50),
    display_order INTEGER DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    parent_menu_id BIGINT,
    created_by VARCHAR(255),
    modified_by VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_menu_id) REFERENCES spms_menu(id) ON DELETE SET NULL
);

CREATE TABLE spms_menu_roles (
    menu_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (menu_id, role_id),
    FOREIGN KEY (menu_id) REFERENCES spms_menu(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES spms_role(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_spms_menu_title ON spms_menu(title);
CREATE INDEX idx_spms_menu_path ON spms_menu(path);
CREATE INDEX idx_spms_menu_active ON spms_menu(active);
CREATE INDEX idx_spms_menu_parent_id ON spms_menu(parent_menu_id);
CREATE INDEX idx_spms_menu_roles_menu_id ON spms_menu_roles(menu_id);
CREATE INDEX idx_spms_menu_roles_role_id ON spms_menu_roles(role_id);

-- Insert initial menu data for backward compatibility
INSERT INTO spms_menu (title, path, icon, display_order, active, parent_menu_id, created_by, modified_by)
VALUES 
('Dashboard', '/dashboard', 'dashboard', 1, TRUE, NULL, 'system', 'system'),
('Projects', '/projects', 'folder', 2, TRUE, NULL, 'system', 'system'),
('Processes', '/processes', 'settings', 3, TRUE, NULL, 'system', 'system'),
('Reports', '/reports', 'bar_chart', 4, TRUE, NULL, 'system', 'system'),
('Admin', '/admin', 'admin_panel_settings', 5, TRUE, NULL, 'system', 'system');

-- Associate all menus with admin role (role_id 1) by default
INSERT INTO spms_menu_roles (menu_id, role_id)
SELECT id, 1 FROM spms_menu;

-- Associate basic menus with user role (role_id 2)
INSERT INTO spms_menu_roles (menu_id, role_id)
SELECT id, 2 FROM spms_menu 
WHERE path IN ('/dashboard', '/projects');
