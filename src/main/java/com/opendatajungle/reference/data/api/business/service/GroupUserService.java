package com.opendatajungle.reference.data.api.business.service;

import com.opendatajungle.commons.business.exception.NotFoundException;
import com.opendatajungle.commons.business.exception.ParamException;
import com.opendatajungle.reference.data.api.business.model.Group;
import com.opendatajungle.reference.data.api.business.model.GroupUser;
import com.opendatajungle.reference.data.api.business.model.User;
import com.opendatajungle.reference.data.api.business.repository.GroupRepository;
import com.opendatajungle.reference.data.api.business.repository.GroupUserRepository;
import com.opendatajungle.reference.data.api.business.repository.PermissionRepository;
import com.opendatajungle.reference.data.api.business.repository.UserRepository;
import com.opendatajungle.reference.data.api.shared.PageResult;

import java.util.UUID;

public class GroupUserService implements GroupUserUseCase {

    private static final String USER = "User";
    private static final String GROUP = "Group";
    private static final String PERMISSION = "Permission";

    private final GroupUserRepository groupUserRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PermissionRepository permissionRepository;

    public GroupUserService(GroupUserRepository groupUserRepository,
                            UserRepository userRepository,
                            GroupRepository groupRepository,
                            PermissionRepository permissionRepository) {
        this.groupUserRepository = groupUserRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public PageResult<GroupUser> getGroupsByUserId(UUID userId, int page, int size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(USER, userId.toString()));
        return groupUserRepository.findGroupsByUserId(user, page, size);
    }

    @Override
    public PageResult<GroupUser> getUsersByGroupId(UUID groupId, int page, int size) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException(GROUP, groupId.toString()));
        if (!groupRepository.existsById(groupId)) {
            throw new NotFoundException(GROUP, groupId.toString());
        }
        return groupUserRepository.findUsersByGroupId(group, page, size);
    }

    @Override
    public void addUserToGroup(UUID groupId, UUID userId, UUID permissionId) {
        if (!groupRepository.existsById(groupId)) {
            throw new NotFoundException(GROUP, groupId.toString());
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(USER, userId.toString());
        }
        if (!permissionRepository.existsById(permissionId)) {
            throw new NotFoundException(PERMISSION, permissionId.toString());
        }
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
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(USER, userId.toString());
        }
        if (!groupUserRepository.isUserInGroup(groupId, userId)) {
            throw new ParamException(
                    "USER_NOT_IN_GROUP",
                    "User is not a member of this group",
                    "userId"
            );
        }
        groupUserRepository.removeUserFromGroup(groupId, userId);
    }
}
