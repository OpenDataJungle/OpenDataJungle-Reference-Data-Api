package com.laulem.vectopath.referential.api.business.service;

import com.laulem.vectopath.referential.api.business.model.Group;
import com.laulem.vectopath.referential.api.business.model.User;
import com.laulem.vectopath.referential.api.shared.PageResult;

import java.util.UUID;

public interface GroupUserUseCase {
    PageResult<Group> getGroupsByUserId(UUID userId, int page, int size);

    PageResult<User> getUsersByGroupId(UUID groupId, int page, int size);

    void addUserToGroup(UUID groupId, UUID userId);

    void removeUserFromGroup(UUID groupId, UUID userId);
}
