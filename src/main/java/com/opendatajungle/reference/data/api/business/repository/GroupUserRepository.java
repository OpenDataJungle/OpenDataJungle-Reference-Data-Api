package com.opendatajungle.reference.data.api.business.repository;

import com.opendatajungle.reference.data.api.business.model.Group;
import com.opendatajungle.reference.data.api.business.model.GroupUser;
import com.opendatajungle.reference.data.api.business.model.User;
import com.opendatajungle.reference.data.api.shared.PageResult;

import java.util.UUID;

public interface GroupUserRepository {
    PageResult<GroupUser> findGroupsByUserId(User user, int page, int size);

    PageResult<GroupUser> findUsersByGroupId(Group group, int page, int size);

    void addUserToGroup(UUID groupId, UUID userId, UUID permissionId);

    void removeUserFromGroup(UUID groupId, UUID userId);

    boolean isUserInGroup(UUID groupId, UUID userId);
}
