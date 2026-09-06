package com.opendatajungle.reference.data.api.business.service;

import com.opendatajungle.reference.data.api.business.model.Permission;
import com.opendatajungle.reference.data.api.shared.PageResult;

import java.util.UUID;

public interface PermissionUseCase {
    PageResult<Permission> findAll(int page, int size);

    Permission getById(UUID id);

    boolean existsById(UUID id);

    /**
     * Returns the well-known permission (seeded in SQL as "DEFAULT_ADMIN_PERMISSION").
     */
    Permission getDefaultAdminPermission();

    Permission create(Permission permission);

    Permission update(UUID id, Permission permission);

    void delete(UUID id);
}
