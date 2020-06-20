CREATE TABLE audit_logs
(
    id         BIGINT(20) UNSIGNED AUTO_INCREMENT NOT NULL,
    created_at TIMESTAMP                 NOT NULL DEFAULT NOW(),
    created_by VARCHAR(254),
    origin     VARCHAR(45),
    action     VARCHAR(500),
    extra_info TEXT,
    success    BIT(1)                    NOT NULL,
    PRIMARY KEY (id)
);