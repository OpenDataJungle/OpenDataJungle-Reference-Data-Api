package com.opendatajungle.reference.data.api.client.dto;

import com.opendatajungle.reference.data.api.business.model.Group;
import com.opendatajungle.reference.data.api.business.model.GroupUser;
import com.opendatajungle.reference.data.api.business.model.Permission;
import com.opendatajungle.reference.data.api.business.model.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GroupUserResponseTest {

    @Test
    void fromBusiness_shouldMapNestedGroupUserAndPermission_whenCalled() {
        // Given
        Group group = Group.builder().id(UUID.randomUUID()).name("root").build();
        User user = User.builder().id(UUID.randomUUID()).username("ada").build();
        Permission permission = Permission.builder().id(UUID.randomUUID()).name("root_permission").build();
        GroupUser groupUser = GroupUser.builder().group(group).user(user).permission(permission).build();

        // When
        GroupUserResponse response = GroupUserResponse.fromBusiness(groupUser);

        // Then
        assertThat(response.getGroup()).isEqualTo(GroupResponse.fromBusiness(group));
        assertThat(response.getUser()).isEqualTo(UserResponse.fromBusiness(user));
        assertThat(response.getPermission()).isEqualTo(PermissionResponse.fromBusiness(permission));
    }
}
