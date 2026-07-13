package com.laulem.vectopath.referential.api.business.service;

import com.laulem.vectopath.referential.api.business.exception.NotFoundException;
import com.laulem.vectopath.referential.api.business.exception.ParamException;
import com.laulem.vectopath.referential.api.business.repository.PermissionRepository;
import com.laulem.vectopath.referential.api.shared.PageResult;

import java.util.UUID;

public class PermissionService implements PermissionUseCase {

    private static final String PERMISSION = "Permission";

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public PageResult<com.laulem.vectopath.referential.api.business.model.Permission> getAll(int page, int size) {
        return permissionRepository.findAll(page, size);
    }

    @Override
    public com.laulem.vectopath.referential.api.business.model.Permission getById(UUID id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PERMISSION, id.toString()));
    }

    @Override
    public com.laulem.vectopath.referential.api.business.model.Permission create(com.laulem.vectopath.referential.api.business.model.Permission permission) {
        if (permissionRepository.existsByName(permission.name())) {
            throw new ParamException(
                    "PERMISSION_NAME_ALREADY_EXISTS",
                    "A permission with name '" + permission.name() + "' already exists",
                    "name"
            );
        }
        return permissionRepository.save(permission);
    }

    @Override
    public com.laulem.vectopath.referential.api.business.model.Permission update(UUID id, com.laulem.vectopath.referential.api.business.model.Permission permission) {
        if (!permissionRepository.existsById(id)) {
            throw new NotFoundException(PERMISSION, id.toString());
        }
        if (permissionRepository.existsByNameAndIdNot(permission.name(), id)) {
            throw new ParamException(
                    "PERMISSION_NAME_ALREADY_EXISTS",
                    "A permission with name '" + permission.name() + "' already exists",
                    "name"
            );
        }
        com.laulem.vectopath.referential.api.business.model.Permission toUpdate = com.laulem.vectopath.referential.api.business.model.Permission.builder()
                .id(id)
                .name(permission.name())
                .description(permission.description())
                .canRead(permission.canRead())
                .canWrite(permission.canWrite())
                .isAdmin(permission.isAdmin())
                .build();
        return permissionRepository.save(toUpdate);
    }

    @Override
    public void delete(UUID id) {
        if (!permissionRepository.existsById(id)) {
            throw new NotFoundException(PERMISSION, id.toString());
        }
        permissionRepository.deleteById(id);
    }
}
