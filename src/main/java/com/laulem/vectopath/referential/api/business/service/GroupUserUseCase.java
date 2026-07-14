package com.laulem.vectopath.referential.api.business.service;

import com.laulem.vectopath.referential.api.business.model.GroupUser;
import com.laulem.vectopath.referential.api.shared.PageResult;

import java.util.UUID;

public interface GroupUserUseCase {
    PageResult<GroupUser> getGroupsByUserId(UUID userId, int page, int size);

    PageResult<GroupUser> getUsersByGroupId(UUID groupId, int page, int size);

    void addUserToGroup(UUID groupId, UUID userId, UUID permissionId);

    void removeUserFromGroup(UUID groupId, UUID userId);
}
