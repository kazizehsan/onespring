CREATE TABLE privileges
(
    id             INT(20)      AUTO_INCREMENT NOT NULL,
    name           VARCHAR(70)                 NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE users
(
    id             INT(20)      AUTO_INCREMENT NOT NULL,
    email          VARCHAR(254)                NOT NULL,
    password       VARCHAR(72)                 NOT NULL,
    enabled        BIT(1)                      NOT NULL,
    name           VARCHAR(70)                 NOT NULL,
    photo          VARCHAR(200),
    dob            DATE,
    deleted_at     TIMESTAMP                   NULL,
    created_at     TIMESTAMP                   NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP                   NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    created_by     VARCHAR(254),
    updated_by     VARCHAR(254),
    account_status TINYINT                     NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE users_privileges
(
    user_id        INT(20) NOT NULL,
    privilege_id   INT(20) NOT NULL
);




INSERT INTO privileges (name)
VALUES ('ADMIN'),
       ('USER');

INSERT INTO users (email, password, enabled, name, created_by, updated_by, updated_at)
VALUES ('admin@onespring.com', '$2a$10$rbn56HsICy2nc5YNVhAkG.yIXBzhrh/g3kjIghny128aKhkYkfY4G', 1, 'Admin',
        'admin@onespring.com',
        'admin@onespring.com', NOW());

INSERT INTO users_privileges(user_id, privilege_id)
VALUES (1, 1);




ALTER TABLE privileges
    ADD CONSTRAINT uk_privileges_name UNIQUE (name);

ALTER TABLE users
    ADD CONSTRAINT uk_users_email UNIQUE (email, deleted_at);

ALTER TABLE users_privileges
    ADD FOREIGN KEY (privilege_id)
        REFERENCES privileges (id);

ALTER TABLE users_privileges
    ADD FOREIGN KEY (user_id)
        REFERENCES users (id);