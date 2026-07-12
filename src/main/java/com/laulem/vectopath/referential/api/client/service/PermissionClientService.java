package com.laulem.vectopath.referential.api.client.service;

import com.laulem.vectopath.referential.api.business.model.Permission;
import com.laulem.vectopath.referential.api.business.service.PermissionService;
import com.laulem.vectopath.referential.api.client.dto.PaginatedResponse;
import com.laulem.vectopath.referential.api.client.dto.PermissionCreate;
import com.laulem.vectopath.referential.api.client.dto.PermissionResponse;
import com.laulem.vectopath.referential.api.client.dto.PermissionUpdate;
import com.laulem.vectopath.referential.api.shared.PageResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class PermissionClientService {
    private final PermissionService permissionBusinessService;

    public PaginatedResponse<PermissionResponse> getAll(int page, int size) {
        PageResult<Permission> businessResponse = permissionBusinessService.getAll(page, size);

        return PaginatedResponse.<PermissionResponse>builder()
                .content(businessResponse.content().stream()
                        .map(PermissionResponse::fromBusiness)
                        .toList())
                .totalElements(businessResponse.totalElements())
                .totalPages(businessResponse.totalPages())
                .currentPage(businessResponse.currentPage())
                .pageSize(businessResponse.pageSize())
                .build();
    }

    public PermissionResponse getById(UUID id) {
        return PermissionResponse.fromBusiness(permissionBusinessService.getById(id));
    }

    public PermissionResponse create(PermissionCreate permissionCreate) {
        Permission createdPermission = permissionBusinessService.create(permissionCreate.toBusiness());
        return PermissionResponse.fromBusiness(createdPermission);
    }

    public PermissionResponse update(UUID id, PermissionUpdate permissionUpdate) {
        Permission updatedPermission = permissionBusinessService.update(id, permissionUpdate.toBusiness());
        return PermissionResponse.fromBusiness(updatedPermission);
    }

    public void delete(UUID id) {
        permissionBusinessService.delete(id);
    }
}
