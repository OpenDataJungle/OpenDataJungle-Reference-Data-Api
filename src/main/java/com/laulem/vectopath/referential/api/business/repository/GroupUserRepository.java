package com.laulem.vectopath.referential.api.business.repository;

import com.laulem.vectopath.referential.api.business.model.Group;
import com.laulem.vectopath.referential.api.business.model.User;
import com.laulem.vectopath.referential.api.shared.PageResult;

import java.util.UUID;

public interface GroupUserRepository {
    PageResult<Group> findGroupsByUserId(UUID userId, int page, int size);

    PageResult<User> findUsersByGroupId(UUID groupId, int page, int size);

    void addUserToGroup(UUID groupId, UUID userId);

    void removeUserFromGroup(UUID groupId, UUID userId);

    boolean isUserInGroup(UUID groupId, UUID userId);
}
