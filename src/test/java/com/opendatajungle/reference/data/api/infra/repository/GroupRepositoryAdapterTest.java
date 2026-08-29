package com.opendatajungle.reference.data.api.infra.repository;

import com.opendatajungle.commons.business.exception.NotFoundException;
import com.opendatajungle.reference.data.api.business.model.Group;
import com.opendatajungle.reference.data.api.infra.entity.GroupEntity;
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
import org.springframework.data.jpa.domain.Specification;

import java.time.Duration;
import java.time.Instant;
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
class GroupRepositoryAdapterTest {

    @Mock
    private GroupJpaRepository groupJpaRepository;

    @InjectMocks
    private GroupRepositoryAdapter groupRepositoryAdapter;

    @Test
    void findAll_shouldUseZeroBasedPageAndAscendingNameSort_whenCalled() {
        // Given
        GroupEntity entity = GroupEntity.builder().id(UUID.randomUUID()).name("root").description("Root group")
                .createdAt(Instant.now()).updatedAt(Instant.now()).build();
        Pageable pageable = PageRequest.of(0, 50, Sort.by("name").ascending());
        Page<GroupEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(groupJpaRepository.findAll(any(Specification.class), pageableCaptor.capture())).thenReturn(page);

        // When
        PageResult<Group> result = groupRepositoryAdapter.findAll(1, 50, null);

        // Then
        assertThat(pageableCaptor.getValue().getPageNumber()).isZero();
        assertThat(pageableCaptor.getValue().getSort()).isEqualTo(Sort.by("name").ascending());
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst().name()).isEqualTo("root");
        assertThat(result.currentPage()).isEqualTo(1);
    }

    @Test
    void findById_shouldReturnMappedGroup_whenEntityExists() {
        // Given
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Instant updatedAt = createdAt.plus(Duration.ofDays(1));

        GroupEntity entity = GroupEntity.builder()
                .id(id)
                .name("root")
                .description("Root group")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
        when(groupJpaRepository.findById(id)).thenReturn(Optional.of(entity));

        // When
        Optional<Group> result = groupRepositoryAdapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(id);
        assertThat(result.get().name()).isEqualTo("root");
        assertThat(result.get().description()).isEqualTo("Root group");
        assertThat(result.get().createdAt()).isEqualTo(createdAt);
        assertThat(result.get().updatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void findById_shouldReturnEmpty_whenEntityAbsent() {
        // Given
        UUID id = UUID.randomUUID();
        when(groupJpaRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Group> result = groupRepositoryAdapter.findById(id);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void save_shouldCreateNewEntityWithGeneratedId_whenGroupIdIsNull() {
        // Given
        Group group = Group.builder().name("root").description("Root group").build();
        ArgumentCaptor<GroupEntity> captor = ArgumentCaptor.forClass(GroupEntity.class);
        when(groupJpaRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Group result = groupRepositoryAdapter.save(group);

        // Then
        assertThat(captor.getValue().getId()).isNotNull();
        assertThat(captor.getValue().getName()).isEqualTo("root");
        assertThat(result.name()).isEqualTo("root");
        verify(groupJpaRepository, never()).findById(any());
    }

    @Test
    void save_shouldUpdateOnlyMutableFields_whenGroupIdExists() {
        // Given
        UUID id = UUID.randomUUID();
        Instant originalCreatedAt = Instant.now().minus(Duration.ofDays(1));
        GroupEntity existing = GroupEntity.builder().id(id).name("old").description("old description").createdAt(originalCreatedAt).build();
        Group group = Group.builder().id(id).name("updated").description("updated description").build();
        when(groupJpaRepository.findById(id)).thenReturn(Optional.of(existing));
        when(groupJpaRepository.save(existing)).thenReturn(existing);

        // When
        Group result = groupRepositoryAdapter.save(group);

        // Then
        assertThat(existing.getName()).isEqualTo("updated");
        assertThat(existing.getDescription()).isEqualTo("updated description");
        assertThat(existing.getId()).isEqualTo(id);
        assertThat(existing.getCreatedAt()).isEqualTo(originalCreatedAt);
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.name()).isEqualTo("updated");
    }

    @Test
    void save_shouldThrowNotFoundException_whenGroupIdDoesNotExist() {
        // Given
        UUID id = UUID.randomUUID();
        Group group = Group.builder().id(id).name("updated").build();
        when(groupJpaRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> groupRepositoryAdapter.save(group))
                .isInstanceOf(NotFoundException.class);
        verify(groupJpaRepository, never()).save(any());
    }

    @Test
    void existsById_shouldDelegateToJpaRepository_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();
        when(groupJpaRepository.existsById(id)).thenReturn(true);

        // When
        boolean result = groupRepositoryAdapter.existsById(id);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void existsByName_shouldDelegateToJpaRepository_whenCalled() {
        // Given
        when(groupJpaRepository.existsByName("root")).thenReturn(true);

        // When
        boolean result = groupRepositoryAdapter.existsByName("root");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void existsByNameAndIdNot_shouldDelegateToJpaRepository_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();
        when(groupJpaRepository.existsByNameAndIdNot("root", id)).thenReturn(false);

        // When
        boolean result = groupRepositoryAdapter.existsByNameAndIdNot("root", id);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void deleteById_shouldDelegateToJpaRepository_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        groupRepositoryAdapter.deleteById(id);

        // Then
        verify(groupJpaRepository).deleteById(id);
    }
}
