package com.laulem.vectopath.referential.api.infra.repository;

import com.laulem.vectopath.referential.api.business.model.Group;
import com.laulem.vectopath.referential.api.business.model.User;
import com.laulem.vectopath.referential.api.business.repository.GroupUserRepository;
import com.laulem.vectopath.referential.api.infra.entity.GroupEntity;
import com.laulem.vectopath.referential.api.infra.entity.UserEntity;
import com.laulem.vectopath.referential.api.shared.PageResult;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class GroupUserRepositoryAdapter implements GroupUserRepository {

    private final UserJpaRepository userJpaRepository;
    private final GroupJpaRepository groupJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<Group> findGroupsByUserId(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("name").ascending());
        Page<GroupEntity> groupPage = groupJpaRepository.findAllByUsersId(userId, pageable);

        return PageResult.<Group>builder()
                .content(groupPage.getContent().stream()
                        .map(this::toGroupBusinessModel)
                        .toList())
                .totalElements(groupPage.getTotalElements())
                .totalPages(groupPage.getTotalPages())
                .currentPage(groupPage.getNumber() + 1)
                .pageSize(groupPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<User> findUsersByGroupId(UUID groupId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("username").ascending());
        Page<UserEntity> userPage = userJpaRepository.findAllByGroupsId(groupId, pageable);

        return PageResult.<User>builder()
                .content(userPage.getContent().stream()
                        .map(this::toUserBusinessModel)
                        .toList())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .currentPage(userPage.getNumber() + 1)
                .pageSize(userPage.getSize())
                .build();
    }

    @Override
    @Transactional
    public void addUserToGroup(UUID groupId, UUID userId) {
        userJpaRepository.addUserToGroup(groupId, userId);
    }

    @Override
    @Transactional
    public void removeUserFromGroup(UUID groupId, UUID userId) {
        userJpaRepository.removeUserFromGroup(groupId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserInGroup(UUID groupId, UUID userId) {
        return groupJpaRepository.isUserInGroup(groupId, userId);
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
}
