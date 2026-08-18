package com.opendatajungle.reference.data.api.client.controller;

import com.opendatajungle.reference.data.api.business.model.Group;
import com.opendatajungle.reference.data.api.business.model.GroupUser;
import com.opendatajungle.reference.data.api.business.model.Permission;
import com.opendatajungle.reference.data.api.business.model.User;
import com.opendatajungle.reference.data.api.business.service.GroupUserUseCase;
import com.opendatajungle.reference.data.api.client.dto.GroupUserResponse;
import com.opendatajungle.reference.data.api.client.dto.PaginatedResponse;
import com.opendatajungle.reference.data.api.shared.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupUserControllerTest {

    @Mock
    private GroupUserUseCase groupUserUseCase;

    @InjectMocks
    private GroupUserController groupUserController;

    private static GroupUser aGroupUser() {
        return GroupUser.builder()
                .user(User.builder().id(UUID.randomUUID()).username("alex").build())
                .group(Group.builder().id(UUID.randomUUID()).name("root").build())
                .permission(Permission.builder().id(UUID.randomUUID()).name("root_permission").build())
                .build();
    }

    @Test
    void getGroupsByUserId_shouldReturnMappedPage_whenCalled() {
        // Given
        UUID userId = UUID.randomUUID();
        GroupUser groupUser = aGroupUser();
        PageResult<GroupUser> pageResult = PageResult.<GroupUser>builder().content(List.of(groupUser))
                .currentPage(1)
                .pageSize(50)
                .totalElements(100)
                .totalPages(2)
                .build();
        when(groupUserUseCase.getGroupsByUserId(userId, 1, 50)).thenReturn(pageResult);

        // When
        PaginatedResponse<GroupUserResponse> response = groupUserController.getGroupsByUserId(userId, 1, 50);

        // Then
        assertThat(response.content()).containsExactly(GroupUserResponse.fromBusiness(groupUser));
    }

    @Test
    void getUsersByGroupId_shouldReturnMappedPage_whenCalled() {
        // Given
        UUID groupId = UUID.randomUUID();
        GroupUser groupUser = aGroupUser();
        PageResult<GroupUser> pageResult = PageResult.<GroupUser>builder().content(List.of(groupUser))
                .currentPage(1)
                .pageSize(50)
                .totalElements(100)
                .totalPages(2)
                .build();
        when(groupUserUseCase.getUsersByGroupId(groupId, 1, 50)).thenReturn(pageResult);

        // When
        PaginatedResponse<GroupUserResponse> response = groupUserController.getUsersByGroupId(groupId, 1, 50);

        // Then
        assertThat(response.content()).containsExactly(GroupUserResponse.fromBusiness(groupUser));
    }

    @Test
    void addUserToGroup_shouldDelegateToUseCase_whenCalled() {
        // Given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();

        // When
        groupUserController.addUserToGroup(groupId, userId, permissionId);

        // Then
        verify(groupUserUseCase).addUserToGroup(groupId, userId, permissionId);
    }

    @Test
    void removeUserFromGroup_shouldDelegateToUseCase_whenCalled() {
        // Given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // When
        groupUserController.removeUserFromGroup(groupId, userId);

        // Then
        verify(groupUserUseCase).removeUserFromGroup(groupId, userId);
    }
}
