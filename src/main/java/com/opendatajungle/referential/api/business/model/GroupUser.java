package com.opendatajungle.referential.api.business.model;

public record GroupUser(
        User user,
        Group group,
        Permission permission
) {
    public static GroupUserBuilder builder() {
        return new GroupUserBuilder();
    }

    public static final class GroupUserBuilder {
        private User user;
        private Group group;
        private Permission permission;

        private GroupUserBuilder() {
        }

        public GroupUserBuilder user(User user) {
            this.user = user;
            return this;
        }

        public GroupUserBuilder group(Group group) {
            this.group = group;
            return this;
        }

        public GroupUserBuilder permission(Permission permission) {
            this.permission = permission;
            return this;
        }

        public GroupUser build() {
            return new GroupUser(user, group, permission);
        }
    }
}
