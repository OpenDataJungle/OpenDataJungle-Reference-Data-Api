package com.opendatajungle.referential.api.business.service;

import com.opendatajungle.referential.api.business.model.Permission;
import com.opendatajungle.referential.api.shared.PageResult;

import java.util.UUID;

public interface PermissionUseCase {
    PageResult<Permission> findAll(int page, int size);

    Permission getById(UUID id);

    Permission create(Permission permission);

    Permission update(UUID id, Permission permission);

    void delete(UUID id);
}
