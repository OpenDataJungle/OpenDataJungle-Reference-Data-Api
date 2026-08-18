package com.opendatajungle.reference.data.api.client.controller;

import com.opendatajungle.reference.data.api.business.model.Permission;
import com.opendatajungle.reference.data.api.business.service.PermissionUseCase;
import com.opendatajungle.reference.data.api.client.dto.PaginatedResponse;
import com.opendatajungle.reference.data.api.client.dto.PermissionRequest;
import com.opendatajungle.reference.data.api.client.dto.PermissionResponse;
import com.opendatajungle.reference.data.api.shared.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionControllerTest {

    @Mock
    private PermissionUseCase permissionUseCase;

    @InjectMocks
    private PermissionController permissionController;

    @Test
    void getAllPermissions_shouldReturnMappedPage_whenCalled() {
        // Given
        Permission permission = Permission.builder().id(UUID.randomUUID()).name("root_permission").canRead(true).canWrite(true).isAdmin(true).build();
        PageResult<Permission> pageResult = PageResult.<Permission>builder()
                .content(List.of(permission))
                .totalElements(200)
                .totalPages(4)
                .currentPage(1)
                .pageSize(50)
                .build();
        when(permissionUseCase.findAll(1, 50)).thenReturn(pageResult);

        // When
        PaginatedResponse<PermissionResponse> response = permissionController.getAllPermissions(1, 50);

        // Then
        assertThat(response.content()).containsExactly(PermissionResponse.fromBusiness(permission));
    }

    @Test
    void getPermissionById_shouldReturnMappedPermission_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();
        Permission permission = Permission.builder().id(id).name("root_permission").build();
        when(permissionUseCase.getById(id)).thenReturn(permission);

        // When
        PermissionResponse response = permissionController.getPermissionById(id);

        // Then
        assertThat(response).isEqualTo(PermissionResponse.fromBusiness(permission));
    }

    @Test
    void createPermission_shouldDelegateToUseCaseWithMappedBusinessObject_whenCalled() {
        // Given
        PermissionRequest request = new PermissionRequest("root_permission", "Root permission", true, true, true);
        Permission created = Permission.builder().id(UUID.randomUUID()).name("root_permission").build();
        ArgumentCaptor<Permission> captor = ArgumentCaptor.forClass(Permission.class);
        when(permissionUseCase.create(captor.capture())).thenReturn(created);

        // When
        PermissionResponse response = permissionController.createPermission(request);

        // Then
        assertThat(captor.getValue().name()).isEqualTo("root_permission");
        assertThat(captor.getValue().canRead()).isTrue();
        assertThat(captor.getValue().canWrite()).isTrue();
        assertThat(captor.getValue().isAdmin()).isTrue();
        assertThat(response).isEqualTo(PermissionResponse.fromBusiness(created));
    }

    @Test
    void updatePermission_shouldDelegateToUseCaseWithIdAndMappedBusinessObject_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();
        PermissionRequest request = new PermissionRequest("alex", "Updated description", false, false, false);
        Permission updated = Permission.builder().id(id).name("updated").build();
        ArgumentCaptor<Permission> captor = ArgumentCaptor.forClass(Permission.class);
        when(permissionUseCase.update(eq(id), captor.capture())).thenReturn(updated);

        // When
        PermissionResponse response = permissionController.updatePermission(id, request);

        // Then
        assertThat(captor.getValue().name()).isEqualTo("alex");
        assertThat(captor.getValue().canRead()).isFalse();
        assertThat(captor.getValue().canWrite()).isFalse();
        assertThat(captor.getValue().isAdmin()).isFalse();
        assertThat(response).isEqualTo(PermissionResponse.fromBusiness(updated));
    }

    @Test
    void deletePermission_shouldDelegateToUseCase_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        permissionController.deletePermission(id);

        // Then
        verify(permissionUseCase).delete(id);
    }
}
