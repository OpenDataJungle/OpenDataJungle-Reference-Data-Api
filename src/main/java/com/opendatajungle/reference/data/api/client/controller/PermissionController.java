package com.opendatajungle.reference.data.api.client.controller;

import com.opendatajungle.reference.data.api.business.model.Permission;
import com.opendatajungle.reference.data.api.business.service.PermissionUseCase;
import com.opendatajungle.reference.data.api.client.dto.PaginatedResponse;
import com.opendatajungle.reference.data.api.client.dto.PermissionRequest;
import com.opendatajungle.reference.data.api.client.dto.PermissionResponse;
import com.opendatajungle.reference.data.api.client.mapper.PaginatedResponseMapper;
import com.opendatajungle.reference.data.api.client.security.SecurityExpressions;
import com.opendatajungle.reference.data.api.shared.PageResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/permissions")
@Validated
@AllArgsConstructor
public class PermissionController {

    private final PermissionUseCase permissionUseCase;

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_READ)
    @GetMapping
    public PaginatedResponse<PermissionResponse> getAllPermissions(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "50") @Min(1) int size) {
        PageResult<Permission> businessResponse = permissionUseCase.findAll(page, size);
        return PaginatedResponseMapper.toPaginatedResponse(businessResponse, PermissionResponse::fromBusiness);
    }

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_READ)
    @GetMapping("/{id}")
    public PermissionResponse getPermissionById(@PathVariable UUID id) {
        return PermissionResponse.fromBusiness(permissionUseCase.getById(id));
    }

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_WRITE)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PermissionResponse createPermission(@Valid @RequestBody PermissionRequest permissionRequest) {
        Permission createdPermission = permissionUseCase.create(permissionRequest.toBusiness());
        return PermissionResponse.fromBusiness(createdPermission);
    }

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_WRITE)
    @PutMapping("/{id}")
    public PermissionResponse updatePermission(
            @PathVariable UUID id,
            @Valid @RequestBody PermissionRequest permissionRequest) {
        Permission updatedPermission = permissionUseCase.update(id, permissionRequest.toBusiness());
        return PermissionResponse.fromBusiness(updatedPermission);
    }

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_DELETE)
    @DeleteMapping("/{id}")
    public void deletePermission(@PathVariable UUID id) {
        permissionUseCase.delete(id);
    }
}
