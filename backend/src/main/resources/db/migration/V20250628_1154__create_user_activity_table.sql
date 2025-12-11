CREATE TABLE spms_user_activity (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  action_type VARCHAR(50) NOT NULL,
  entity_type VARCHAR(50),
  entity_id BIGINT,
  details JSON,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES spms_users(id)
);

CREATE INDEX idx_user_activity_user ON spms_user_activity(user_id);
CREATE INDEX idx_user_activity_time ON spms_user_activity(created_at);
