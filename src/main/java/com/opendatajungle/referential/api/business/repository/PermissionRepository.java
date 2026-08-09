package com.opendatajungle.referential.api.business.repository;

import com.opendatajungle.referential.api.business.model.Permission;
import com.opendatajungle.referential.api.shared.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository {
    PageResult<Permission> findAll(int page, int size);

    Optional<Permission> findById(UUID id);

    Permission save(Permission permission);

    boolean existsById(UUID id);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);

    void deleteById(UUID id);
}
