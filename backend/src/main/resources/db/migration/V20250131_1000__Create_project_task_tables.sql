-- M1模块: 创建项目和任务表
-- 创建时间: 2025-01-31
-- 描述: 建立基础的项目和任务数据模型
-- 数据库: PostgreSQL

SET client_encoding = 'UTF8';

-- 创建项目表
CREATE TABLE spms_project (
    id BIGSERIAL PRIMARY KEY,
    project_code VARCHAR(50) UNIQUE NOT NULL,
    project_name VARCHAR(200) NOT NULL,
    description TEXT,
    manager_id BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    
    -- 扩展字段
    budget DECIMAL(15,2),
    start_date DATE,
    end_date DATE,
    priority INTEGER,
    project_type VARCHAR(50),
    client_id BIGINT,
    external_project_id VARCHAR(100),
    tags VARCHAR(500),
    custom_fields JSONB,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- 审计字段
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    
    -- 外键约束
    CONSTRAINT fk_project_manager FOREIGN KEY (manager_id) REFERENCES spms_user(id)
);

-- 创建索引
CREATE INDEX idx_project_code ON spms_project(project_code);
CREATE INDEX idx_project_status ON spms_project(status);
CREATE INDEX idx_project_manager ON spms_project(manager_id);
CREATE INDEX idx_project_active ON spms_project(is_active);

-- 添加表注释
COMMENT ON TABLE spms_project IS '项目表';
COMMENT ON COLUMN spms_project.id IS '项目ID';
COMMENT ON COLUMN spms_project.project_code IS '项目代码';
COMMENT ON COLUMN spms_project.project_name IS '项目名称';
COMMENT ON COLUMN spms_project.description IS '项目描述';
COMMENT ON COLUMN spms_project.manager_id IS '项目经理ID';
COMMENT ON COLUMN spms_project.status IS '项目状态: ACTIVE, INACTIVE, COMPLETED';
COMMENT ON COLUMN spms_project.custom_fields IS '自定义字段存储';

-- 创建任务表
CREATE TABLE spms_task (
    id BIGSERIAL PRIMARY KEY,
    task_number VARCHAR(50) UNIQUE NOT NULL,
    task_name VARCHAR(200) NOT NULL,
    description TEXT,
    project_id BIGINT NOT NULL,
    
    -- 扩展字段
    custom_fields JSONB,
    external_task_id VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- 审计字段
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    
    -- 外键约束
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES spms_project(id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX idx_task_number ON spms_task(task_number);
CREATE INDEX idx_task_project ON spms_task(project_id);
CREATE INDEX idx_task_active ON spms_task(is_active);

-- 添加表注释
COMMENT ON TABLE spms_task IS '任务表';
COMMENT ON COLUMN spms_task.id IS '任务ID';
COMMENT ON COLUMN spms_task.task_number IS '任务编号';
COMMENT ON COLUMN spms_task.task_name IS '任务名称';
COMMENT ON COLUMN spms_task.description IS '任务描述';
COMMENT ON COLUMN spms_task.project_id IS '所属项目ID';
COMMENT ON COLUMN spms_task.custom_fields IS '自定义字段存储';

-- 插入测试数据（可选）
INSERT INTO spms_project (project_code, project_name, description, status, created_at, updated_at, created_by, updated_by) VALUES 
('SPMS_2025', 'SPMS系统升级项目', '升级SPMS系统功能', 'ACTIVE', EXTRACT(EPOCH FROM NOW()) * 1000, EXTRACT(EPOCH FROM NOW()) * 1000, 4, 4),
('TEST_PROJECT', '测试项目', '用于测试M1模块功能的项目', 'ACTIVE', EXTRACT(EPOCH FROM NOW()) * 1000, EXTRACT(EPOCH FROM NOW()) * 1000, 4, 4);

INSERT INTO spms_task (task_number, task_name, description, project_id, created_at, updated_at, created_by, updated_by) VALUES 
('SPMS-001', '需求分析', '分析系统功能需求', 1, EXTRACT(EPOCH FROM NOW()) * 1000, EXTRACT(EPOCH FROM NOW()) * 1000, 4, 4),
('SPMS-002', '数据库设计', '设计项目和任务数据模型', 1, EXTRACT(EPOCH FROM NOW()) * 1000, EXTRACT(EPOCH FROM NOW()) * 1000, 4, 4),
('TEST-001', '单元测试', '编写M1模块单元测试', 2, EXTRACT(EPOCH FROM NOW()) * 1000, EXTRACT(EPOCH FROM NOW()) * 1000, 4, 4);
