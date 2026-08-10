package com.opendatajungle.reference.data.api.business.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record User(
        UUID id,
        String firstName,
        String lastName,
        String username,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static final class UserBuilder {
        private UUID id;
        private String firstName;
        private String lastName;
        private String username;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

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

        public UserBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public User build() {
            return new User(id, firstName, lastName, username, createdAt, updatedAt);
        }
    }
}
