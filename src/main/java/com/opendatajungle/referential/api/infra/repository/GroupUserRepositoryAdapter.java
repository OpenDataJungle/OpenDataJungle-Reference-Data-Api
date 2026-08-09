package com.opendatajungle.referential.api.infra.repository;

import com.opendatajungle.referential.api.business.model.Group;
import com.opendatajungle.referential.api.business.model.GroupUser;
import com.opendatajungle.referential.api.business.model.Permission;
import com.opendatajungle.referential.api.business.model.User;
import com.opendatajungle.referential.api.business.repository.GroupUserRepository;
import com.opendatajungle.referential.api.infra.entity.GroupEntity;
import com.opendatajungle.referential.api.infra.entity.GroupUserEntity;
import com.opendatajungle.referential.api.infra.entity.PermissionEntity;
import com.opendatajungle.referential.api.infra.entity.UserEntity;
import com.opendatajungle.referential.api.shared.PageResult;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GroupUserRepositoryAdapter implements GroupUserRepository {

    private final GroupUserJpaRepository groupUserJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<GroupUser> findGroupsByUserId(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("group.name").ascending());
        Page<GroupUserEntity> groupUserPage = groupUserJpaRepository.findByUserIdWithPermission(user.id(), pageable);

        return PageResult.<GroupUser>builder()
                .content(groupUserPage.getContent().stream()
                        .map(group -> toGroupUserBusinessModel(group, user))
                        .filter(Objects::nonNull)
                        .toList())
                .totalElements(groupUserPage.getTotalElements())
                .totalPages(groupUserPage.getTotalPages())
                .currentPage(groupUserPage.getNumber() + 1)
                .pageSize(groupUserPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<GroupUser> findUsersByGroupId(Group group, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("user.username").ascending());
        Page<GroupUserEntity> groupUserPage = groupUserJpaRepository.findByGroupIdWithPermission(group.id(), pageable);

        return PageResult.<GroupUser>builder()
                .content(groupUserPage.getContent().stream()
                        .map(user -> toGroupUserBusinessModel(user, group))
                        .filter(Objects::nonNull)
                        .toList())
                .totalElements(groupUserPage.getTotalElements())
                .totalPages(groupUserPage.getTotalPages())
                .currentPage(groupUserPage.getNumber() + 1)
                .pageSize(groupUserPage.getSize())
                .build();
    }

    private GroupUser toGroupUserBusinessModel(final GroupUserEntity user, final Group group) {
        return GroupUser.builder()
                .user(toUserBusinessModel(user.getUser()))
                .group(group)
                .permission(toPermissionBusinessModel(user.getPermission()))
                .build();
    }

    @Override
    @Transactional
    public void addUserToGroup(UUID groupId, UUID userId, UUID permissionId) {
        groupUserJpaRepository.addUserToGroup(groupId, userId, permissionId);
    }

    @Override
    @Transactional
    public void removeUserFromGroup(UUID groupId, UUID userId) {
        groupUserJpaRepository.removeUserFromGroup(groupId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserInGroup(UUID groupId, UUID userId) {
        return groupUserJpaRepository.isUserInGroup(groupId, userId);
    }

    private GroupUser toGroupUserBusinessModel(GroupUserEntity entity, User user) {
        return GroupUser.builder()
                .user(user)
                .group(toGroupBusinessModel(entity.getGroup()))
                .permission(toPermissionBusinessModel(entity.getPermission()))
                .build();
    }

    private User toUserBusinessModel(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .username(entity.getUsername())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private Group toGroupBusinessModel(GroupEntity entity) {
        return Group.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private Permission toPermissionBusinessModel(PermissionEntity entity) {
        return Permission.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .canRead(entity.getCanRead())
                .canWrite(entity.getCanWrite())
                .isAdmin(entity.getIsAdmin())
                .build();
    }
}
