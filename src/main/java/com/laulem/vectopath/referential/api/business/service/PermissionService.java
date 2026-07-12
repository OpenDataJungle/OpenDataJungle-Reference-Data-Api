package com.laulem.vectopath.referential.api.business.service;

import com.laulem.vectopath.referential.api.business.model.Permission;
import com.laulem.vectopath.referential.api.shared.PageResult;

import java.util.UUID;

public interface PermissionService {
    PageResult<Permission> getAll(int page, int size);

    Permission getById(UUID id);

    Permission create(Permission permission);

    Permission update(UUID id, Permission permission);

    void delete(UUID id);
}
