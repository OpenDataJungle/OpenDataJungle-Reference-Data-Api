package com.opendatajungle.referential.api.infra.repository;

import com.opendatajungle.referential.api.business.model.Permission;
import com.opendatajungle.referential.api.business.repository.PermissionRepository;
import com.opendatajungle.referential.api.infra.entity.PermissionEntity;
import com.opendatajungle.referential.api.shared.PageResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class PermissionRepositoryAdapter implements PermissionRepository {
    private final PermissionJpaRepository permissionJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<Permission> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("name").ascending());
        Page<PermissionEntity> permissionPage = permissionJpaRepository.findAll(pageable);

        return PageResult.<Permission>builder()
                .content(permissionPage.getContent().stream()
                        .map(this::toBusinessModel)
                        .toList())
                .totalElements(permissionPage.getTotalElements())
                .totalPages(permissionPage.getTotalPages())
                .currentPage(permissionPage.getNumber() + 1)
                .pageSize(permissionPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Permission> findById(UUID id) {
        return permissionJpaRepository.findById(id).map(this::toBusinessModel);
    }

    @Override
    @Transactional
    public Permission save(Permission permission) {
        PermissionEntity entity;
        if (permission.id() != null) {
            entity = permissionJpaRepository.findById(permission.id()).orElseThrow();
            entity.setName(permission.name());
            entity.setDescription(permission.description());
            entity.setCanRead(permission.canRead());
            entity.setCanWrite(permission.canWrite());
            entity.setIsAdmin(permission.isAdmin());
        } else {
            entity = toEntity(permission);
            entity.setId(UUID.randomUUID());
        }
        return toBusinessModel(permissionJpaRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return permissionJpaRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return permissionJpaRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndIdNot(String name, UUID id) {
        return permissionJpaRepository.existsByNameAndIdNot(name, id);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        permissionJpaRepository.deleteById(id);
    }

    private Permission toBusinessModel(PermissionEntity entity) {
        return Permission.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .canRead(entity.getCanRead())
                .canWrite(entity.getCanWrite())
                .isAdmin(entity.getIsAdmin())
                .build();
    }

    private PermissionEntity toEntity(Permission permission) {
        return PermissionEntity.builder()
                .id(permission.id())
                .name(permission.name())
                .description(permission.description())
                .canRead(permission.canRead())
                .canWrite(permission.canWrite())
                .isAdmin(permission.isAdmin())
                .build();
    }
}
