package com.opendatajungle.reference.data.api.business.service;

import com.opendatajungle.reference.data.api.business.model.GroupUser;
import com.opendatajungle.reference.data.api.shared.PageResult;

import java.util.UUID;

public interface GroupUserUseCase {
    PageResult<GroupUser> getGroupsByUserId(UUID userId, int page, int size);

    PageResult<GroupUser> getUsersByGroupId(UUID groupId, int page, int size);

    void addUserToGroup(UUID groupId, UUID userId, UUID permissionId);

    void removeUserFromGroup(UUID groupId, UUID userId);

    /**
     * Checks that the given user is an admin member of the given group
     * @param groupId
     * @param userId
     */
    void requireGroupAdmin(UUID groupId, UUID userId);

    boolean hasGroupAdmin(UUID groupId, UUID userId);

    /**
     * Grants the given user admin rights on the given group by attaching the well-known default
     * admin permission (see {@link PermissionUseCase#getDefaultAdminPermission()}).
     */
    void grantGroupAdmin(UUID groupId, UUID userId);

    /**
     * Grants the current user admin rights on the given group by attaching the well-known default
     * admin permission (see {@link PermissionUseCase#getDefaultAdminPermission()}).
     */
    void grantGroupAdminForCurrentUser(UUID groupId);
}
