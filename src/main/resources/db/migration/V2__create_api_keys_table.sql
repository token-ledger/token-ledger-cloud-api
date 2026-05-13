CREATE TABLE api_keys (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_key VARCHAR(255) NOT NULL UNIQUE,
    member_id BIGINT NOT NULL,
    name VARCHAR(255),
    created_at DATETIME NOT NULL,
    last_used_at DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_api_keys_member FOREIGN KEY (member_id) REFERENCES members(id)
);

CREATE INDEX idx_api_keys_member ON api_keys (member_id);
CREATE INDEX idx_api_keys_value ON api_keys (api_key);
