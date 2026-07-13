CREATE SCHEMA IF NOT EXISTS referential;
SET search_path TO referential, public;


DROP TABLE IF EXISTS referential.file_group_permissions;
DROP TABLE IF EXISTS referential.folder_group_permissions;
DROP TABLE IF EXISTS referential.group_users;

DROP TABLE IF EXISTS referential.files;
DROP TABLE IF EXISTS referential.folders;

DROP TABLE IF EXISTS referential.permissions;
DROP TABLE IF EXISTS referential.groups;
DROP TABLE IF EXISTS referential.users;

CREATE TABLE referential.users (
                       id UUID PRIMARY KEY,
                       name VARCHAR NOT NULL,
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

CREATE TABLE referential.group_users (
                             group_id UUID NOT NULL REFERENCES referential.groups(id) ON DELETE CASCADE,
                             user_id UUID NOT NULL REFERENCES referential.users(id) ON DELETE CASCADE,
                             PRIMARY KEY (group_id, user_id)
);

CREATE TABLE referential.permissions (
                             id UUID PRIMARY KEY,
                             name VARCHAR NOT NULL UNIQUE,
                             description TEXT,
                             can_read BOOLEAN NOT NULL DEFAULT FALSE,
                             can_write BOOLEAN NOT NULL DEFAULT FALSE,
                             is_admin BOOLEAN NOT NULL DEFAULT FALSE
);

-- Déplacer

CREATE TABLE referential.folders (
                         id UUID PRIMARY KEY,
                         name VARCHAR NOT NULL,
                         path VARCHAR NOT NULL UNIQUE,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE referential.files (
                       id UUID PRIMARY KEY,
                       folder_id UUID NOT NULL REFERENCES referential.folders(id) ON DELETE CASCADE,
                       name VARCHAR NOT NULL,
                       path VARCHAR NOT NULL UNIQUE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE referential.folder_group_permissions (
                                          folder_id UUID NOT NULL REFERENCES referential.folders(id) ON DELETE CASCADE,
                                          group_id UUID NOT NULL REFERENCES referential.groups(id) ON DELETE CASCADE,
                                          permission_id UUID NOT NULL REFERENCES referential.permissions(id),
                                          PRIMARY KEY (folder_id, group_id)
);

CREATE TABLE referential.file_group_permissions (
                                        file_id UUID NOT NULL REFERENCES referential.files(id) ON DELETE CASCADE,
                                        group_id UUID NOT NULL REFERENCES referential.groups(id) ON DELETE CASCADE,
                                        permission_id UUID NOT NULL REFERENCES referential.permissions(id),
                                        PRIMARY KEY (file_id, group_id)
);

