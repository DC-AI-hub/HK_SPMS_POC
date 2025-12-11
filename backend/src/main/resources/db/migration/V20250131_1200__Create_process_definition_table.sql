-- 创建流程定义表 (spms_process_def)
-- 创建时间: 2025-01-31
-- 描述: 为Process Management功能建立流程定义数据模型
-- 数据库: PostgreSQL

SET client_encoding = 'UTF8';

-- 创建流程定义表
CREATE TABLE spms_process_def (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    key VARCHAR(255) NOT NULL,
    description TEXT,
    owner_id BIGINT,
    business_owner_id BIGINT,
    
    -- 审计字段 (毫秒时间戳)
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    
    -- 外键约束
    CONSTRAINT fk_process_def_owner FOREIGN KEY (owner_id) REFERENCES spms_user(id),
    CONSTRAINT fk_process_def_business_owner FOREIGN KEY (business_owner_id) REFERENCES spms_user(id)
);

-- 创建索引
CREATE INDEX idx_process_def_name ON spms_process_def(name);
CREATE INDEX idx_process_def_key ON spms_process_def(key);
CREATE INDEX idx_process_def_owner ON spms_process_def(owner_id);
CREATE INDEX idx_process_def_business_owner ON spms_process_def(business_owner_id);

-- 添加表注释
COMMENT ON TABLE spms_process_def IS '流程定义表';
COMMENT ON COLUMN spms_process_def.id IS '流程定义ID';
COMMENT ON COLUMN spms_process_def.name IS '流程名称';
COMMENT ON COLUMN spms_process_def.key IS '流程关键字';
COMMENT ON COLUMN spms_process_def.description IS '流程描述';
COMMENT ON COLUMN spms_process_def.owner_id IS '流程负责人ID';
COMMENT ON COLUMN spms_process_def.business_owner_id IS '业务负责人ID';

