package com.opendatajungle.reference.data.api.business.model;

import java.time.Instant;
import java.util.UUID;

public record User(
        UUID id,
        String firstName,
        String lastName,
        String username,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static final class UserBuilder {
        private UUID id;
        private String firstName;
        private String lastName;
        private String username;
        private Instant createdAt;
        private Instant updatedAt;

        private UserBuilder() {
        }

        public UserBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public UserBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public User build() {
            return new User(id, firstName, lastName, username, createdAt, updatedAt);
        }
    }
}
