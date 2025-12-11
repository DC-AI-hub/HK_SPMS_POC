-- 扩展节假日表字段
-- 创建时间: 2025-01-31
-- 描述: 为节假日表添加国家代码、名称、类型字段和软删除字段
-- 数据库: PostgreSQL

SET client_encoding = 'UTF8';

-- 检查表是否存在，如果不存在则创建（兼容性处理）
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'spms_holiday') THEN
        -- 如果表不存在，创建表
        CREATE TABLE spms_holiday (
            id BIGSERIAL PRIMARY KEY,
            holiday_date DATE NOT NULL,
            country VARCHAR(10) NOT NULL DEFAULT 'CN',
            name VARCHAR(200) NOT NULL,
            type VARCHAR(50) NOT NULL DEFAULT 'PUBLIC_HOLIDAY',
            is_active BOOLEAN NOT NULL DEFAULT TRUE,
            created_at BIGINT NOT NULL,
            updated_at BIGINT NOT NULL,
            created_by BIGINT NOT NULL,
            updated_by BIGINT NOT NULL
        );
        
        -- 创建索引
        CREATE INDEX idx_holiday_date ON spms_holiday(holiday_date);
        CREATE INDEX idx_holiday_country ON spms_holiday(country);
        CREATE INDEX idx_holiday_active ON spms_holiday(is_active);
        
        -- 添加唯一约束（同一日期、同一国家不能重复）
        CREATE UNIQUE INDEX idx_holiday_date_country ON spms_holiday(holiday_date, country) WHERE is_active = TRUE;
        
        -- 添加表注释
        COMMENT ON TABLE spms_holiday IS '节假日表';
        COMMENT ON COLUMN spms_holiday.id IS '节假日ID';
        COMMENT ON COLUMN spms_holiday.holiday_date IS '节假日日期';
        COMMENT ON COLUMN spms_holiday.country IS '国家代码（CN/HK/US/UK）';
        COMMENT ON COLUMN spms_holiday.name IS '节假日名称';
        COMMENT ON COLUMN spms_holiday.type IS '节假日类型（PUBLIC_HOLIDAY/COMPANY_HOLIDAY）';
        COMMENT ON COLUMN spms_holiday.is_active IS '是否激活（用于软删除）';
        
        RETURN;
    END IF;
END $$;

-- 如果表已存在，添加新字段
DO $$
BEGIN
    -- 添加 country 字段（如果不存在）
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'spms_holiday' AND column_name = 'country') THEN
        ALTER TABLE spms_holiday ADD COLUMN country VARCHAR(10) DEFAULT 'CN';
    END IF;
    
    -- 添加 name 字段（如果不存在）
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'spms_holiday' AND column_name = 'name') THEN
        ALTER TABLE spms_holiday ADD COLUMN name VARCHAR(200);
    END IF;
    
    -- 添加 type 字段（如果不存在）
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'spms_holiday' AND column_name = 'type') THEN
        ALTER TABLE spms_holiday ADD COLUMN type VARCHAR(50) DEFAULT 'PUBLIC_HOLIDAY';
    END IF;
    
    -- 添加 is_active 字段（如果不存在）
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'spms_holiday' AND column_name = 'is_active') THEN
        ALTER TABLE spms_holiday ADD COLUMN is_active BOOLEAN DEFAULT TRUE;
    END IF;
END $$;

-- 更新现有数据的默认值
UPDATE spms_holiday 
SET country = COALESCE(country, 'CN'),
    name = COALESCE(name, 'Holiday ' || holiday_date::text),
    type = COALESCE(type, 'PUBLIC_HOLIDAY'),
    is_active = COALESCE(is_active, TRUE)
WHERE country IS NULL OR name IS NULL OR type IS NULL OR is_active IS NULL;

-- 设置字段为 NOT NULL（如果还没有设置）
DO $$
BEGIN
    -- 设置 country 为 NOT NULL
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'spms_holiday' AND column_name = 'country' AND is_nullable = 'YES') THEN
        ALTER TABLE spms_holiday ALTER COLUMN country SET NOT NULL;
    END IF;
    
    -- 设置 name 为 NOT NULL
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'spms_holiday' AND column_name = 'name' AND is_nullable = 'YES') THEN
        ALTER TABLE spms_holiday ALTER COLUMN name SET NOT NULL;
    END IF;
    
    -- 设置 type 为 NOT NULL
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'spms_holiday' AND column_name = 'type' AND is_nullable = 'YES') THEN
        ALTER TABLE spms_holiday ALTER COLUMN type SET NOT NULL;
    END IF;
    
    -- 设置 is_active 为 NOT NULL
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'spms_holiday' AND column_name = 'is_active' AND is_nullable = 'YES') THEN
        ALTER TABLE spms_holiday ALTER COLUMN is_active SET NOT NULL;
    END IF;
END $$;

-- 创建索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_holiday_country ON spms_holiday(country);
CREATE INDEX IF NOT EXISTS idx_holiday_active ON spms_holiday(is_active);

-- 添加唯一约束（同一日期、同一国家不能重复，仅对激活的记录）
-- 注意：PostgreSQL 不支持在唯一索引中使用 WHERE 子句，但可以使用部分唯一索引
DROP INDEX IF EXISTS idx_holiday_date_country;
CREATE UNIQUE INDEX idx_holiday_date_country_active 
ON spms_holiday(holiday_date, country) 
WHERE is_active = TRUE;

