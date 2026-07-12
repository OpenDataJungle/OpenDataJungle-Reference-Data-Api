package com.laulem.vectopath.referential.api.client.controller;

import com.laulem.vectopath.referential.api.client.dto.PaginatedResponse;
import com.laulem.vectopath.referential.api.client.dto.PermissionCreate;
import com.laulem.vectopath.referential.api.client.dto.PermissionResponse;
import com.laulem.vectopath.referential.api.client.dto.PermissionUpdate;
import com.laulem.vectopath.referential.api.client.service.PermissionClientService;
import com.laulem.vectopath.referential.api.infra.conf.security.SecurityExpressions;
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

    private final PermissionClientService permissionService;

    @PreAuthorize(SecurityExpressions.REFERENTIAL_READ)
    @GetMapping
    public PaginatedResponse<PermissionResponse> getAllPermissions(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "50") @Min(1) int size) {
        return permissionService.getAll(page, size);
    }

    @PreAuthorize(SecurityExpressions.REFERENTIAL_READ)
    @GetMapping("/{id}")
    public PermissionResponse getPermissionById(@PathVariable UUID id) {
        return permissionService.getById(id);
    }

    @PreAuthorize(SecurityExpressions.REFERENTIAL_WRITE)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PermissionResponse createPermission(@Valid @RequestBody PermissionCreate permissionCreate) {
        return permissionService.create(permissionCreate);
    }

    @PreAuthorize(SecurityExpressions.REFERENTIAL_WRITE)
    @PutMapping("/{id}")
    public PermissionResponse updatePermission(
            @PathVariable UUID id,
            @Valid @RequestBody PermissionUpdate permissionUpdate) {
        return permissionService.update(id, permissionUpdate);
    }

    @PreAuthorize(SecurityExpressions.REFERENTIAL_DELETE)
    @DeleteMapping("/{id}")
    public void deletePermission(@PathVariable UUID id) {
        permissionService.delete(id);
    }
}
