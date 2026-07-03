-- Baseline schema for the starter template.
-- Managed by Flyway; keep migrations immutable once merged.

CREATE TABLE users (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    full_name  VARCHAR(255),
    role       VARCHAR(32)  NOT NULL,
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6)  NOT NULL,
    version    BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE = InnoDB;

CREATE TABLE tasks (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    status      VARCHAR(32)  NOT NULL,
    due_date    DATE,
    owner_id    BIGINT       NOT NULL,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,
    version     BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT pk_tasks PRIMARY KEY (id),
    CONSTRAINT fk_tasks_owner FOREIGN KEY (owner_id) REFERENCES users (id)
) ENGINE = InnoDB;

CREATE INDEX idx_tasks_owner_id ON tasks (owner_id);
CREATE INDEX idx_tasks_owner_status ON tasks (owner_id, status);
