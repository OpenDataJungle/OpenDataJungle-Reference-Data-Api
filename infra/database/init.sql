CREATE SCHEMA IF NOT EXISTS third_party_ref;
SET search_path TO third_party_ref, public;


DROP TABLE IF EXISTS third_party_ref.file_group_permissions;
DROP TABLE IF EXISTS third_party_ref.folder_group_permissions;
DROP TABLE IF EXISTS third_party_ref.group_users;

DROP TABLE IF EXISTS third_party_ref.files;
DROP TABLE IF EXISTS third_party_ref.folders;

DROP TABLE IF EXISTS third_party_ref.permissions;
DROP TABLE IF EXISTS third_party_ref.groups;
DROP TABLE IF EXISTS third_party_ref.users;

CREATE TABLE third_party_ref.users (
                       id UUID PRIMARY KEY,
                       name VARCHAR NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE third_party_ref.groups (
                        id UUID PRIMARY KEY,
                        name VARCHAR NOT NULL UNIQUE,
                        description TEXT,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE third_party_ref.group_users (
                             group_id UUID NOT NULL REFERENCES third_party_ref.groups(id) ON DELETE CASCADE,
                             user_id UUID NOT NULL REFERENCES third_party_ref.users(id) ON DELETE CASCADE,
                             PRIMARY KEY (group_id, user_id)
);

CREATE TABLE third_party_ref.permissions (
                             id UUID PRIMARY KEY,
                             name VARCHAR NOT NULL UNIQUE,
                             description TEXT,
                             can_read BOOLEAN NOT NULL DEFAULT FALSE,
                             can_write BOOLEAN NOT NULL DEFAULT FALSE,
                             is_admin BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE third_party_ref.folders (
                         id UUID PRIMARY KEY,
                         name VARCHAR NOT NULL,
                         path VARCHAR NOT NULL UNIQUE,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE third_party_ref.files (
                       id UUID PRIMARY KEY,
                       folder_id UUID NOT NULL REFERENCES third_party_ref.folders(id) ON DELETE CASCADE,
                       name VARCHAR NOT NULL,
                       path VARCHAR NOT NULL UNIQUE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE third_party_ref.folder_group_permissions (
                                          folder_id UUID NOT NULL REFERENCES third_party_ref.folders(id) ON DELETE CASCADE,
                                          group_id UUID NOT NULL REFERENCES third_party_ref.groups(id) ON DELETE CASCADE,
                                          permission_id UUID NOT NULL REFERENCES third_party_ref.permissions(id),
                                          PRIMARY KEY (folder_id, group_id)
);

CREATE TABLE third_party_ref.file_group_permissions (
                                        file_id UUID NOT NULL REFERENCES third_party_ref.files(id) ON DELETE CASCADE,
                                        group_id UUID NOT NULL REFERENCES third_party_ref.groups(id) ON DELETE CASCADE,
                                        permission_id UUID NOT NULL REFERENCES third_party_ref.permissions(id),
                                        PRIMARY KEY (file_id, group_id)
);

