package com.laulem.vectopath.referential.api.business.model;

import java.util.UUID;

public record Permission(
        UUID id,
        String name,
        String description,
        Boolean canRead,
        Boolean canWrite,
        Boolean isAdmin
) {
    public static PermissionBuilder builder() {
        return new PermissionBuilder();
    }

    public static final class PermissionBuilder {
        private UUID id;
        private String name;
        private String description;
        private Boolean canRead;
        private Boolean canWrite;
        private Boolean isAdmin;

        private PermissionBuilder() {
        }

        public PermissionBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public PermissionBuilder name(String name) {
            this.name = name;
            return this;
        }

        public PermissionBuilder description(String description) {
            this.description = description;
            return this;
        }

        public PermissionBuilder canRead(Boolean canRead) {
            this.canRead = canRead;
            return this;
        }

        public PermissionBuilder canWrite(Boolean canWrite) {
            this.canWrite = canWrite;
            return this;
        }

        public PermissionBuilder isAdmin(Boolean isAdmin) {
            this.isAdmin = isAdmin;
            return this;
        }

        public Permission build() {
            return new Permission(id, name, description, canRead, canWrite, isAdmin);
        }
    }
}
