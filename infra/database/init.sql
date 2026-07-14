CREATE SCHEMA IF NOT EXISTS referential;
SET search_path TO referential, public;

DROP TABLE IF EXISTS referential.group_users;

DROP TABLE IF EXISTS referential.permissions;
DROP TABLE IF EXISTS referential.groups;
DROP TABLE IF EXISTS referential.users;

CREATE TABLE referential.users (
                       id UUID PRIMARY KEY,
                       first_name VARCHAR NOT NULL,
                       last_name VARCHAR NOT NULL,
                       username VARCHAR NOT NULL UNIQUE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE referential.groups (
                        id UUID PRIMARY KEY,
                        name VARCHAR NOT NULL UNIQUE,
                        description TEXT,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE referential.permissions (
                                         id UUID PRIMARY KEY,
                                         name VARCHAR NOT NULL UNIQUE,
                                         description TEXT,
                                         can_read BOOLEAN NOT NULL DEFAULT FALSE,
                                         can_write BOOLEAN NOT NULL DEFAULT FALSE,
                                         is_admin BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE referential.group_users (
                             group_id UUID NOT NULL REFERENCES referential.groups(id) ON DELETE CASCADE,
                             user_id UUID NOT NULL REFERENCES referential.users(id) ON DELETE CASCADE,
                             permission_id UUID NOT NULL REFERENCES referential.permissions(id),
                             PRIMARY KEY (group_id, user_id)
);




