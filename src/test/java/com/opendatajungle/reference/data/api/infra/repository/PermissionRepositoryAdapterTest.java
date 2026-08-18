package com.opendatajungle.reference.data.api.infra.repository;

import com.opendatajungle.commons.business.exception.NotFoundException;
import com.opendatajungle.reference.data.api.business.model.Permission;
import com.opendatajungle.reference.data.api.infra.entity.PermissionEntity;
import com.opendatajungle.reference.data.api.shared.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionRepositoryAdapterTest {

    @Mock
    private PermissionJpaRepository permissionJpaRepository;

    @InjectMocks
    private PermissionRepositoryAdapter permissionRepositoryAdapter;

    @Test
    void findAll_shouldUseZeroBasedPageAndAscendingNameSort_whenCalled() {
        // Given
        PermissionEntity entity = PermissionEntity.builder().id(UUID.randomUUID()).name("root_permission")
                .canRead(true).canWrite(true).isAdmin(true).build();
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        Pageable pageable = PageRequest.of(0, 50, Sort.by("name").ascending());
        Page<PermissionEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(permissionJpaRepository.findAll(pageableCaptor.capture())).thenReturn(page);

        // When
        PageResult<Permission> result = permissionRepositoryAdapter.findAll(1, 50);

        // Then
        assertThat(pageableCaptor.getValue().getPageNumber()).isZero();
        assertThat(pageableCaptor.getValue().getSort()).isEqualTo(Sort.by("name").ascending());
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst().name()).isEqualTo("root_permission");
    }

    @Test
    void findById_shouldReturnMappedPermission_whenEntityExists() {
        // Given
        UUID id = UUID.randomUUID();
        PermissionEntity entity = PermissionEntity
                .builder()
                .id(id)
                .name("root_permission")
                .canRead(true)
                .canWrite(true)
                .isAdmin(true)
                .build();
        when(permissionJpaRepository.findById(id)).thenReturn(Optional.of(entity));

        // When
        Optional<Permission> result = permissionRepositoryAdapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(id);
        assertThat(result.get().name()).isEqualTo("root_permission");
        assertThat(result.get().canRead()).isTrue();
        assertThat(result.get().canWrite()).isTrue();
        assertThat(result.get().isAdmin()).isTrue();
    }

    @Test
    void save_shouldCreateNewEntityWithGeneratedId_whenPermissionIdIsNull() {
        // Given
        Permission permission = Permission.builder().name("root_permission").canRead(true).canWrite(true).isAdmin(true).build();
        ArgumentCaptor<PermissionEntity> captor = ArgumentCaptor.forClass(PermissionEntity.class);
        when(permissionJpaRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Permission result = permissionRepositoryAdapter.save(permission);

        // Then
        assertThat(captor.getValue().getId()).isNotNull();
        assertThat(result.name()).isEqualTo("root_permission");
        assertThat(result.canRead()).isTrue();
        assertThat(result.canWrite()).isTrue();
        assertThat(result.isAdmin()).isTrue();
        verify(permissionJpaRepository, never()).findById(any());
    }

    @Test
    void save_shouldUpdateAllMutableFields_whenPermissionIdExists() {
        // Given
        UUID id = UUID.randomUUID();
        PermissionEntity existing = PermissionEntity.builder().id(id).name("old").canRead(false).canWrite(false).isAdmin(false).build();
        Permission permission = Permission.builder().id(id).name("updated").description("updated description").canRead(true).canWrite(true).isAdmin(true).build();
        when(permissionJpaRepository.findById(id)).thenReturn(Optional.of(existing));
        when(permissionJpaRepository.save(existing)).thenReturn(existing);

        // When
        Permission result = permissionRepositoryAdapter.save(permission);

        // Then
        assertThat(existing.getId()).isEqualTo(id);
        assertThat(existing.getName()).isEqualTo("updated");
        assertThat(existing.getCanRead()).isTrue();
        assertThat(existing.getCanWrite()).isTrue();
        assertThat(existing.getIsAdmin()).isTrue();

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.name()).isEqualTo("updated");
        assertThat(result.description()).isEqualTo("updated description");
        assertThat(result.canRead()).isTrue();
        assertThat(result.canWrite()).isTrue();
        assertThat(result.isAdmin()).isTrue();
    }

    @Test
    void save_shouldThrowNotFoundException_whenPermissionIdDoesNotExist() {
        // Given
        UUID id = UUID.randomUUID();
        Permission permission = Permission.builder().id(id).name("updated").build();
        when(permissionJpaRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> permissionRepositoryAdapter.save(permission))
                .isInstanceOf(NotFoundException.class);
        verify(permissionJpaRepository, never()).save(any());
    }

    @Test
    void existsById_shouldDelegateToJpaRepository_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();
        when(permissionJpaRepository.existsById(id)).thenReturn(true);

        // When
        boolean result = permissionRepositoryAdapter.existsById(id);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void existsByName_shouldDelegateToJpaRepository_whenCalled() {
        // Given
        when(permissionJpaRepository.existsByName("root_permission")).thenReturn(true);

        // When
        boolean result = permissionRepositoryAdapter.existsByName("root_permission");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void existsByNameAndIdNot_shouldDelegateToJpaRepository_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();
        when(permissionJpaRepository.existsByNameAndIdNot("root_permission", id)).thenReturn(false);

        // When
        boolean result = permissionRepositoryAdapter.existsByNameAndIdNot("root_permission", id);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void deleteById_shouldDelegateToJpaRepository_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        permissionRepositoryAdapter.deleteById(id);

        // Then
        verify(permissionJpaRepository).deleteById(id);
    }
}
