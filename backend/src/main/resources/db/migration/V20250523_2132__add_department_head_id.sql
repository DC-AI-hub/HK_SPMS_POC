ALTER TABLE spms_department ADD COLUMN department_head_id UUID;
ALTER TABLE spms_department ADD CONSTRAINT fk_department_head 
  FOREIGN KEY (department_head_id) REFERENCES spms_user(id);
