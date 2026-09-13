package com.opendatajungle.reference.data.api.business.service;

import com.opendatajungle.commons.business.exception.AccessDeniedException;
import com.opendatajungle.commons.business.exception.NotFoundException;
import com.opendatajungle.commons.business.exception.ParamException;
import com.opendatajungle.reference.data.api.business.model.Group;
import com.opendatajungle.reference.data.api.business.model.GroupUser;
import com.opendatajungle.reference.data.api.business.model.Permission;
import com.opendatajungle.reference.data.api.business.model.User;
import com.opendatajungle.reference.data.api.business.repository.GroupRepository;
import com.opendatajungle.reference.data.api.business.repository.GroupUserRepository;
import com.opendatajungle.reference.data.api.shared.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupUserServiceTest {

    @Mock
    private GroupUserRepository groupUserRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserUseCase userUseCase;

    @Mock
    private PermissionUseCase permissionUseCase;

    @InjectMocks
    private GroupUserService groupUserService;

    private static final UUID CURRENT_USER_ID = UUID.randomUUID();
    private static final User CURRENT_USER = User.builder().id(CURRENT_USER_ID).username("alex").build();

    @Test
    void getGroupsByUserId_shouldReturnPage_whenUserExists() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).username("alex").build();
        PageResult<GroupUser> expected = PageResult.<GroupUser>builder().content(List.of()).build();
        when(userUseCase.getById(userId)).thenReturn(user);
        when(groupUserRepository.findGroupsByUserId(user, 1, 50)).thenReturn(expected);

        // When
        PageResult<GroupUser> result = groupUserService.getGroupsByUserId(userId, 1, 50);

        // Then
        assertThat(result).isSameAs(expected);
    }

    @Test
    void getGroupsByUserId_shouldThrowNotFoundException_whenUserAbsent() {
        // Given
        UUID userId = UUID.randomUUID();
        when(userUseCase.getById(userId)).thenThrow(new NotFoundException("User", userId.toString()));

        // When & Then
        assertThatThrownBy(() -> groupUserService.getGroupsByUserId(userId, 1, 50))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found with id: " + userId);
    }

    @Test
    void getUsersByGroupId_shouldReturnPage_whenGroupExists() {
        // Given
        UUID groupId = UUID.randomUUID();
        Group group = Group.builder().id(groupId).name("root").build();
        PageResult<GroupUser> expected = PageResult.<GroupUser>builder().content(List.of()).build();
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupUserRepository.findUsersByGroupId(group, 1, 50)).thenReturn(expected);

        // When
        PageResult<GroupUser> result = groupUserService.getUsersByGroupId(groupId, 1, 50);

        // Then
        assertThat(result).isSameAs(expected);
    }

    @Test
    void getUsersByGroupId_shouldThrowNotFoundException_whenGroupAbsent() {
        // Given
        UUID groupId = UUID.randomUUID();
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> groupUserService.getUsersByGroupId(groupId, 1, 50))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Group not found with id: " + groupId);
    }

    @Test
    void addUserToGroup_shouldDelegateToRepository_whenGroupUserAndPermissionExistAndNotAlreadyMember() {
        // Given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(userUseCase.existsById(userId)).thenReturn(true);
        when(permissionUseCase.existsById(permissionId)).thenReturn(true);
        when(userUseCase.getOrCreateCurrentUser()).thenReturn(CURRENT_USER);
        when(groupUserRepository.isUserAdminOfGroup(groupId, CURRENT_USER_ID)).thenReturn(true);
        when(groupUserRepository.isUserInGroup(groupId, userId)).thenReturn(false);

        // When
        groupUserService.addUserToGroup(groupId, userId, permissionId);

        // Then
        verify(groupUserRepository).addUserToGroup(groupId, userId, permissionId);
    }

    @Test
    void addUserToGroup_shouldThrowNotFoundException_whenGroupAbsent() {
        // Given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();
        when(groupRepository.existsById(groupId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> groupUserService.addUserToGroup(groupId, userId, permissionId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Group not found with id: " + groupId);
        verify(groupUserRepository, never()).addUserToGroup(any(), any(), any());
    }

    @Test
    void addUserToGroup_shouldThrowNotFoundException_whenUserAbsent() {
        // Given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(userUseCase.existsById(userId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> groupUserService.addUserToGroup(groupId, userId, permissionId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found with id: " + userId);
        verify(groupUserRepository, never()).addUserToGroup(any(), any(), any());
    }

    @Test
    void addUserToGroup_shouldThrowNotFoundException_whenPermissionAbsent() {
        // Given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(userUseCase.existsById(userId)).thenReturn(true);
        when(permissionUseCase.existsById(permissionId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> groupUserService.addUserToGroup(groupId, userId, permissionId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Permission not found with id: " + permissionId);
        verify(groupUserRepository, never()).addUserToGroup(any(), any(), any());
    }

    @Test
    void addUserToGroup_shouldThrowAccessDeniedException_whenCallerIsNotGroupAdmin() {
        // Given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(userUseCase.existsById(userId)).thenReturn(true);
        when(permissionUseCase.existsById(permissionId)).thenReturn(true);
        when(userUseCase.getOrCreateCurrentUser()).thenReturn(CURRENT_USER);
        when(groupUserRepository.isUserAdminOfGroup(groupId, CURRENT_USER_ID)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> groupUserService.addUserToGroup(groupId, userId, permissionId))
                .isInstanceOf(AccessDeniedException.class);
        verify(groupUserRepository, never()).addUserToGroup(any(), any(), any());
    }

    @Test
    void addUserToGroup_shouldThrowParamException_whenUserAlreadyInGroup() {
        // Given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(userUseCase.existsById(userId)).thenReturn(true);
        when(permissionUseCase.existsById(permissionId)).thenReturn(true);
        when(userUseCase.getOrCreateCurrentUser()).thenReturn(CURRENT_USER);
        when(groupUserRepository.isUserAdminOfGroup(groupId, CURRENT_USER_ID)).thenReturn(true);
        when(groupUserRepository.isUserInGroup(groupId, userId)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> groupUserService.addUserToGroup(groupId, userId, permissionId))
                .isInstanceOf(ParamException.class)
                .satisfies(ex -> {
                    ParamException paramException = (ParamException) ex;
                    assertThat(paramException.getCode()).isEqualTo("USER_ALREADY_IN_GROUP");
                    assertThat(paramException.getField()).isEqualTo("userId");
                });
        verify(groupUserRepository, never()).addUserToGroup(any(), any(), any());
    }

    @Test
    void removeUserFromGroup_shouldDelegateToRepository_whenUserIsMember() {
        // Given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(userUseCase.existsById(userId)).thenReturn(true);
        when(userUseCase.getOrCreateCurrentUser()).thenReturn(CURRENT_USER);
        when(groupUserRepository.isUserAdminOfGroup(groupId, CURRENT_USER_ID)).thenReturn(true);
        when(groupUserRepository.isUserInGroup(groupId, userId)).thenReturn(true);

        // When
        groupUserService.removeUserFromGroup(groupId, userId);

        // Then
        verify(groupUserRepository).removeUserFromGroup(groupId, userId);
    }

    @Test
    void removeUserFromGroup_shouldThrowNotFoundException_whenGroupAbsent() {
        // Given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(groupRepository.existsById(groupId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> groupUserService.removeUserFromGroup(groupId, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Group not found with id: " + groupId);
        verify(groupUserRepository, never()).removeUserFromGroup(any(), any());
    }

    @Test
    void removeUserFromGroup_shouldThrowNotFoundException_whenUserAbsent() {
        // Given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(userUseCase.existsById(userId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> groupUserService.removeUserFromGroup(groupId, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found with id: " + userId);
        verify(groupUserRepository, never()).removeUserFromGroup(any(), any());
    }

    @Test
    void removeUserFromGroup_shouldThrowAccessDeniedException_whenCallerIsNotGroupAdmin() {
        // Given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(userUseCase.existsById(userId)).thenReturn(true);
        when(userUseCase.getOrCreateCurrentUser()).thenReturn(CURRENT_USER);
        when(groupUserRepository.isUserAdminOfGroup(groupId, CURRENT_USER_ID)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> groupUserService.removeUserFromGroup(groupId, userId))
                .isInstanceOf(AccessDeniedException.class);
        verify(groupUserRepository, never()).removeUserFromGroup(any(), any());
    }

    @Test
    void removeUserFromGroup_shouldThrowParamException_whenUserNotMember() {
        // Given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(userUseCase.existsById(userId)).thenReturn(true);
        when(userUseCase.getOrCreateCurrentUser()).thenReturn(CURRENT_USER);
        when(groupUserRepository.isUserAdminOfGroup(groupId, CURRENT_USER_ID)).thenReturn(true);
        when(groupUserRepository.isUserInGroup(groupId, userId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> groupUserService.removeUserFromGroup(groupId, userId))
                .isInstanceOf(ParamException.class)
                .satisfies(ex -> {
                    ParamException paramException = (ParamException) ex;
                    assertThat(paramException.getCode()).isEqualTo("USER_NOT_IN_GROUP");
                    assertThat(paramException.getField()).isEqualTo("userId");
                });
        verify(groupUserRepository, never()).removeUserFromGroup(any(), any());
    }

    @Test
    void grantGroupAdmin_shouldAttachDefaultAdminPermissionToUserGroupTuple_whenCalled() {
        // Given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID defaultAdminPermissionId = UUID.randomUUID();
        Permission defaultAdminPermission = Permission.builder().id(defaultAdminPermissionId).name("DEFAULT_ADMIN_PERMISSION").isAdmin(true).build();
        when(permissionUseCase.getDefaultAdminPermission()).thenReturn(defaultAdminPermission);

        // When
        groupUserService.grantGroupAdmin(groupId, userId);

        // Then
        verify(groupUserRepository).addUserToGroup(groupId, userId, defaultAdminPermissionId);
    }

    @Test
    void requireGroupAdmin_shouldThrowAccessDeniedException_whenUserIsNotAdminOfGroup() {
        // Given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(groupUserRepository.isUserAdminOfGroup(groupId, userId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> groupUserService.requireGroupAdmin(groupId, userId))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void requireGroupAdmin_shouldNotThrow_whenUserIsAdminOfGroup() {
        // Given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(groupUserRepository.isUserAdminOfGroup(groupId, userId)).thenReturn(true);

        // When & Then
        groupUserService.requireGroupAdmin(groupId, userId);
    }
}
