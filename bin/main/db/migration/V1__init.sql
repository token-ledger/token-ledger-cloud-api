CREATE TABLE members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255),
    role VARCHAR(50) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    provider_id VARCHAR(255)
);

CREATE TABLE usage_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id VARCHAR(255) NOT NULL,
    idempotency_key VARCHAR(255) NOT NULL UNIQUE,
    project_id BIGINT,
    application_id BIGINT,
    user_id BIGINT,
    model_id VARCHAR(255) NOT NULL,
    input_tokens BIGINT NOT NULL,
    output_tokens BIGINT NOT NULL,
    total_tokens BIGINT NOT NULL,
    total_cost DECIMAL(18, 8) NOT NULL,
    currency_code VARCHAR(10) NOT NULL,
    status VARCHAR(30) NOT NULL,
    started_at DATETIME NOT NULL,
    finished_at DATETIME,
    latency_ms BIGINT,
    error_code VARCHAR(255),
    error_message VARCHAR(1000),
    created_at DATETIME NOT NULL
);

CREATE INDEX idx_usage_project_started ON usage_logs (project_id, started_at);
CREATE INDEX idx_usage_model_started ON usage_logs (model_id, started_at);
