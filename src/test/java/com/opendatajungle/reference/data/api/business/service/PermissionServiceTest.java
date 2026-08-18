package com.opendatajungle.reference.data.api.business.service;

import com.opendatajungle.commons.business.exception.NotFoundException;
import com.opendatajungle.commons.business.exception.ParamException;
import com.opendatajungle.reference.data.api.business.model.Permission;
import com.opendatajungle.reference.data.api.business.repository.PermissionRepository;
import com.opendatajungle.reference.data.api.shared.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// TODO: Only Group Admin should create / update / delete permissions.
@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionService permissionService;

    @Test
    void findAll_shouldDelegateToRepository_whenCalled() {
        // Given
        PageResult<Permission> expected = PageResult.<Permission>builder()
                .content(List.of())
                .totalElements(0)
                .totalPages(0)
                .currentPage(1)
                .pageSize(50)
                .build();
        when(permissionRepository.findAll(1, 50)).thenReturn(expected);

        // When
        PageResult<Permission> result = permissionService.findAll(1, 50);

        // Then
        assertThat(result).isSameAs(expected);
    }

    @Test
    void getById_shouldReturnPermission_whenPermissionExists() {
        // Given
        UUID id = UUID.randomUUID();
        Permission permission = Permission.builder().id(id).name("root_permission").build();
        when(permissionRepository.findById(id)).thenReturn(Optional.of(permission));

        // When
        Permission result = permissionService.getById(id);

        // Then
        assertThat(result).isSameAs(permission);
    }

    @Test
    void getById_shouldThrowNotFoundException_whenPermissionAbsent() {
        // Given
        UUID id = UUID.randomUUID();
        when(permissionRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> permissionService.getById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Permission not found with id: " + id);
    }

    @Test
    void create_shouldSavePermission_whenNameNotTaken() {
        // Given
        Permission permission = Permission.builder().name("root_permission").canRead(true).canWrite(true).isAdmin(false).build();
        Permission saved = Permission.builder().id(UUID.randomUUID()).name("root_permission").build();
        when(permissionRepository.existsByName("root_permission")).thenReturn(false);
        when(permissionRepository.save(permission)).thenReturn(saved);

        // When
        Permission result = permissionService.create(permission);

        // Then
        assertThat(result).isSameAs(saved);
    }

    @Test
    void create_shouldThrowParamException_whenNameAlreadyExists() {
        // Given
        Permission permission = Permission.builder().name("root_permission").build();
        when(permissionRepository.existsByName("root_permission")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> permissionService.create(permission))
                .isInstanceOf(ParamException.class)
                .satisfies(ex -> {
                    ParamException paramException = (ParamException) ex;
                    assertThat(paramException.getCode()).isEqualTo("PERMISSION_NAME_ALREADY_EXISTS");
                    assertThat(paramException.getField()).isEqualTo("name");
                });
        verify(permissionRepository, never()).save(any());
    }

    @Test
    void update_shouldSavePermission_whenIdExistsAndNameFree() {
        // Given
        UUID id = UUID.randomUUID();
        Permission permission = Permission.builder()
                .name("updated")
                .description("Updated description")
                .canRead(true)
                .canWrite(false)
                .isAdmin(true)
                .build();
        when(permissionRepository.existsById(id)).thenReturn(true);
        when(permissionRepository.existsByNameAndIdNot("updated", id)).thenReturn(false);
        ArgumentCaptor<Permission> captor = ArgumentCaptor.forClass(Permission.class);
        when(permissionRepository.save(any())).thenReturn(permission);

        // When
        Permission result = permissionService.update(id, permission);

        // Then
        verify(permissionRepository).save(captor.capture());
        Permission toUpdate = captor.getValue();
        assertThat(toUpdate.id()).isEqualTo(id);
        assertThat(toUpdate.name()).isEqualTo("updated");
        assertThat(toUpdate.description()).isEqualTo("Updated description");
        assertThat(toUpdate.canRead()).isTrue();
        assertThat(toUpdate.canWrite()).isFalse();
        assertThat(toUpdate.isAdmin()).isTrue();
        assertThat(result).isSameAs(permission);
    }

    @Test
    void update_shouldThrowNotFoundException_whenPermissionAbsent() {
        // Given
        UUID id = UUID.randomUUID();
        Permission permission = Permission.builder().name("updated").build();
        when(permissionRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> permissionService.update(id, permission))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Permission not found with id: " + id);
        verify(permissionRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowParamException_whenNameTakenByAnotherPermission() {
        // Given
        UUID id = UUID.randomUUID();
        Permission permission = Permission.builder().name("duplicate").build();
        when(permissionRepository.existsById(id)).thenReturn(true);
        when(permissionRepository.existsByNameAndIdNot("duplicate", id)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> permissionService.update(id, permission))
                .isInstanceOf(ParamException.class)
                .satisfies(ex -> {
                    ParamException paramException = (ParamException) ex;
                    assertThat(paramException.getCode()).isEqualTo("PERMISSION_NAME_ALREADY_EXISTS");
                    assertThat(paramException.getField()).isEqualTo("name");
                });
        verify(permissionRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeletePermission_whenPermissionExists() {
        // Given
        UUID id = UUID.randomUUID();
        when(permissionRepository.existsById(id)).thenReturn(true);

        // When
        permissionService.delete(id);

        // Then
        verify(permissionRepository).deleteById(id);
    }

    @Test
    void delete_shouldThrowNotFoundException_whenPermissionAbsent() {
        // Given
        UUID id = UUID.randomUUID();
        when(permissionRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> permissionService.delete(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Permission not found with id: " + id);
        verify(permissionRepository, never()).deleteById(any());
    }
}
