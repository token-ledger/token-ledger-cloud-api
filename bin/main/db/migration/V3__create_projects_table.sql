CREATE TABLE projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    external_id VARCHAR(50) NOT NULL UNIQUE,
    member_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    project_key VARCHAR(255) NOT NULL,
    default_model VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL,
    monthly_budget_usd DECIMAL(18, 2),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT uk_projects_member_project_key UNIQUE (member_id, project_key),
    CONSTRAINT fk_projects_member FOREIGN KEY (member_id) REFERENCES members(id)
);

CREATE TABLE project_environments (
    project_id BIGINT NOT NULL,
    environment VARCHAR(50) NOT NULL,
    CONSTRAINT fk_project_environments_project FOREIGN KEY (project_id) REFERENCES projects(id)
);

CREATE INDEX idx_projects_member ON projects (member_id);
CREATE INDEX idx_projects_external_id ON projects (external_id);
CREATE INDEX idx_project_environments_environment ON project_environments (environment);
