package com.opendatajungle.reference.data.api.infra.repository;

import com.opendatajungle.reference.data.api.business.model.Group;
import com.opendatajungle.reference.data.api.business.model.GroupUser;
import com.opendatajungle.reference.data.api.business.model.User;
import com.opendatajungle.reference.data.api.infra.entity.GroupEntity;
import com.opendatajungle.reference.data.api.infra.entity.GroupUserEntity;
import com.opendatajungle.reference.data.api.infra.entity.PermissionEntity;
import com.opendatajungle.reference.data.api.infra.entity.UserEntity;
import com.opendatajungle.reference.data.api.shared.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupUserRepositoryAdapterTest {

    @Mock
    private GroupUserJpaRepository groupUserJpaRepository;

    @InjectMocks
    private GroupUserRepositoryAdapter groupUserRepositoryAdapter;

    @Test
    void findGroupsByUserId_shouldSortByGroupNameAndKeepPassedInUser_whenCalled() {
        // Given
        User user = User.builder().id(UUID.randomUUID()).username("ada").build();
        UUID groupId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();
        GroupUserEntity entity = GroupUserEntity.builder()
                .groupId(groupId)
                .userId(user.id())
                .permissionId(permissionId)
                .group(GroupEntity.builder().id(groupId).name("root").description("Root group").build())
                .permission(PermissionEntity.builder().id(permissionId).name("root_permission").canRead(true).canWrite(true).isAdmin(true).build())
                .build();
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        Pageable pageable = PageRequest.of(0, 50, Sort.by("group.name").ascending());
        Page<GroupUserEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(groupUserJpaRepository.findByUserIdWithPermission(eq(user.id()), pageableCaptor.capture())).thenReturn(page);

        // When
        PageResult<GroupUser> result = groupUserRepositoryAdapter.findGroupsByUserId(user, 1, 50);

        // Then
        assertThat(pageableCaptor.getValue().getSort()).isEqualTo(Sort.by("group.name").ascending());
        assertThat(result.content()).hasSize(1);
        GroupUser groupUser = result.content().getFirst();
        assertThat(groupUser.user()).isSameAs(user);
        assertThat(groupUser.group().id()).isEqualTo(groupId);
        assertThat(groupUser.group().name()).isEqualTo("root");
        assertThat(groupUser.permission().id()).isEqualTo(permissionId);
    }

    @Test
    void findUsersByGroupId_shouldSortByUserUsernameAndKeepPassedInGroup_whenCalled() {
        // Given
        Group group = Group.builder().id(UUID.randomUUID()).name("root").build();
        UUID userId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();
        GroupUserEntity entity = GroupUserEntity.builder()
                .groupId(group.id())
                .userId(userId)
                .permissionId(permissionId)
                .user(UserEntity.builder().id(userId).username("ada").firstName("Ada").lastName("Lovelace").build())
                .permission(PermissionEntity.builder().id(permissionId).name("root_permission").canRead(true).canWrite(true).isAdmin(true).build())
                .build();
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        Pageable pageable = PageRequest.of(0, 50, Sort.by("user.username").ascending());
        Page<GroupUserEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(groupUserJpaRepository.findByGroupIdWithPermission(eq(group.id()), pageableCaptor.capture())).thenReturn(page);

        // When
        PageResult<GroupUser> result = groupUserRepositoryAdapter.findUsersByGroupId(group, 1, 50);

        // Then
        assertThat(pageableCaptor.getValue().getSort()).isEqualTo(Sort.by("user.username").ascending());
        assertThat(result.content()).hasSize(1);
        GroupUser groupUser = result.content().getFirst();
        assertThat(groupUser.group()).isSameAs(group);
        assertThat(groupUser.user().id()).isEqualTo(userId);
        assertThat(groupUser.user().username()).isEqualTo("ada");
        assertThat(groupUser.permission().id()).isEqualTo(permissionId);
    }

    @Test
    void addUserToGroup_shouldDelegateToJpaRepository_whenCalled() {
        // Given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();

        // When
        groupUserRepositoryAdapter.addUserToGroup(groupId, userId, permissionId);

        // Then
        verify(groupUserJpaRepository).addUserToGroup(groupId, userId, permissionId);
    }

    @Test
    void removeUserFromGroup_shouldDelegateToJpaRepository_whenCalled() {
        // Given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // When
        groupUserRepositoryAdapter.removeUserFromGroup(groupId, userId);

        // Then
        verify(groupUserJpaRepository).removeUserFromGroup(groupId, userId);
    }

    @Test
    void isUserInGroup_shouldDelegateToJpaRepository_whenCalled() {
        // Given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(groupUserJpaRepository.isUserInGroup(groupId, userId)).thenReturn(true);

        // When
        boolean result = groupUserRepositoryAdapter.isUserInGroup(groupId, userId);

        // Then
        assertThat(result).isTrue();
    }
}
