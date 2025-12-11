ALTER TABLE spms_division
    ADD COLUMN division_head_id BIGINT,
    ADD CONSTRAINT fk_division_head FOREIGN KEY (division_head_id) REFERENCES spms_user(id);
