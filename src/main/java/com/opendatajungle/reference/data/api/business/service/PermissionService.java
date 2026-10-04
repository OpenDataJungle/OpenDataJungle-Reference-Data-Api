package com.opendatajungle.reference.data.api.business.service;

import com.opendatajungle.commons.business.exception.AccessDeniedException;
import com.opendatajungle.commons.business.exception.NotFoundException;
import com.opendatajungle.commons.business.exception.ParamException;
import com.opendatajungle.reference.data.api.business.model.Permission;
import com.opendatajungle.reference.data.api.business.repository.PermissionRepository;
import com.opendatajungle.reference.data.api.shared.PageResult;

import java.util.UUID;

public class PermissionService implements PermissionUseCase {

    private static final String PERMISSION = "Permission";
    private static final String DEFAULT_ADMIN_PERMISSION = "DEFAULT_ADMIN_PERMISSION";

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public PageResult<Permission> findAll(int page, int size) {
        return permissionRepository.findAll(page, size);
    }

    @Override
    public Permission getById(UUID id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PERMISSION, id.toString()));
    }

    @Override
    public boolean existsById(UUID id) {
        return permissionRepository.existsById(id);
    }

    @Override
    public Permission getDefaultAdminPermission() {
        return permissionRepository.findByName(DEFAULT_ADMIN_PERMISSION)
                .orElseThrow(() -> new NotFoundException(PERMISSION, DEFAULT_ADMIN_PERMISSION));
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
        requireNotDefaultAdminPermission(getById(id));
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
        requireNotDefaultAdminPermission(getById(id));
        permissionRepository.deleteById(id);
    }

    private static void requireNotDefaultAdminPermission(Permission permission) {
        if (DEFAULT_ADMIN_PERMISSION.equals(permission.name())) {
            throw new AccessDeniedException("Permission " + DEFAULT_ADMIN_PERMISSION + " cannot be modified or deleted");
        }
    }
}
