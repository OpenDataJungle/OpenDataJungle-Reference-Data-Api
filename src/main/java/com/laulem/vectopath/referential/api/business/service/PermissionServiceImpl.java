package com.laulem.vectopath.referential.api.business.service;

import com.laulem.vectopath.referential.api.business.exception.NotFoundException;
import com.laulem.vectopath.referential.api.business.exception.ParamException;
import com.laulem.vectopath.referential.api.business.model.Permission;
import com.laulem.vectopath.referential.api.business.repository.PermissionRepository;
import com.laulem.vectopath.referential.api.shared.PageResult;

import java.util.UUID;

public class PermissionServiceImpl implements PermissionService {

    private static final String PERMISSION = "Permission";

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public PageResult<Permission> getAll(int page, int size) {
        return permissionRepository.findAll(page, size);
    }

    @Override
    public Permission getById(UUID id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PERMISSION, id.toString()));
    }

    @Override
    public Permission create(Permission permission) {
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
    public Permission update(UUID id, Permission permission) {
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
        Permission toUpdate = Permission.builder()
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
