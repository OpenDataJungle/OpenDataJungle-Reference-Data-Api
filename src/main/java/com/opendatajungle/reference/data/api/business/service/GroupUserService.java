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

import java.util.UUID;

public class GroupUserService implements GroupUserUseCase {

    private static final String GROUP = "Group";
    private static final String USER = "User";
    private static final String PERMISSION = "Permission";

    private final GroupUserRepository groupUserRepository;
    private final GroupRepository groupRepository;
    private final UserUseCase userUseCase;
    private final PermissionUseCase permissionUseCase;

    public GroupUserService(GroupUserRepository groupUserRepository,
                            GroupRepository groupRepository,
                            UserUseCase userUseCase,
                            PermissionUseCase permissionUseCase) {
        this.groupUserRepository = groupUserRepository;
        this.groupRepository = groupRepository;
        this.userUseCase = userUseCase;
        this.permissionUseCase = permissionUseCase;
    }

    @Override
    public PageResult<GroupUser> getGroupsByUserId(UUID userId, int page, int size) {
        User user = userUseCase.getById(userId);
        return groupUserRepository.findGroupsByUserId(user, page, size);
    }

    @Override
    public PageResult<GroupUser> getUsersByGroupId(UUID groupId, int page, int size) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException(GROUP, groupId.toString()));
        return groupUserRepository.findUsersByGroupId(group, page, size);
    }

    @Override
    public void addUserToGroup(UUID groupId, UUID userId, UUID permissionId) {
        if (!groupRepository.existsById(groupId)) {
            throw new NotFoundException(GROUP, groupId.toString());
        }
        if (!userUseCase.existsById(userId)) {
            throw new NotFoundException(USER, userId.toString());
        }
        if (!permissionUseCase.existsById(permissionId)) {
            throw new NotFoundException(PERMISSION, permissionId.toString());
        }
        requireGroupAdmin(groupId, userUseCase.getOrCreateCurrentUser().id());
        if (groupUserRepository.isUserInGroup(groupId, userId)) {
            throw new ParamException(
                    "USER_ALREADY_IN_GROUP",
                    "User is already a member of this group",
                    "userId"
            );
        }
        groupUserRepository.addUserToGroup(groupId, userId, permissionId);
    }

    @Override
    public void removeUserFromGroup(UUID groupId, UUID userId) {
        if (!groupRepository.existsById(groupId)) {
            throw new NotFoundException(GROUP, groupId.toString());
        }
        if (!userUseCase.existsById(userId)) {
            throw new NotFoundException(USER, userId.toString());
        }
        requireGroupAdmin(groupId, userUseCase.getOrCreateCurrentUser().id());
        if (!groupUserRepository.isUserInGroup(groupId, userId)) {
            throw new ParamException(
                    "USER_NOT_IN_GROUP",
                    "User is not a member of this group",
                    "userId"
            );
        }
        groupUserRepository.removeUserFromGroup(groupId, userId);
    }

    @Override
    public void requireGroupAdmin(UUID groupId, UUID userId) {
        if (!hasGroupAdmin(groupId, userId)) {
            throw new AccessDeniedException("Only an admin of group " + groupId + " can perform this action");
        }
    }

    @Override
    public boolean hasGroupAdmin(UUID groupId, UUID userId) {
        return groupUserRepository.isUserAdminOfGroup(groupId, userId);
    }

    @Override
    public void grantGroupAdmin(UUID groupId, UUID userId) {
        Permission defaultAdminPermission = permissionUseCase.getDefaultAdminPermission();
        groupUserRepository.addUserToGroup(groupId, userId, defaultAdminPermission.id());
    }

    @Override
    public void grantGroupAdminForCurrentUser(UUID groupId) {
        grantGroupAdmin(groupId, userUseCase.getOrCreateCurrentUser().id());
    }
}
