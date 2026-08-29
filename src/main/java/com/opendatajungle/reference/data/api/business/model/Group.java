package com.opendatajungle.reference.data.api.business.model;

import java.time.Instant;
import java.util.UUID;

public record Group(
        UUID id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
    public static GroupBuilder builder() {
        return new GroupBuilder();
    }

    public static final class GroupBuilder {
        private UUID id;
        private String name;
        private String description;
        private Instant createdAt;
        private Instant updatedAt;

        private GroupBuilder() {
        }

        public GroupBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public GroupBuilder name(String name) {
            this.name = name;
            return this;
        }

        public GroupBuilder description(String description) {
            this.description = description;
            return this;
        }

        public GroupBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public GroupBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Group build() {
            return new Group(id, name, description, createdAt, updatedAt);
        }
    }
}
