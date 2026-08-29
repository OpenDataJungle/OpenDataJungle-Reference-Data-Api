package com.opendatajungle.reference.data.api.infra.repository;

import com.opendatajungle.commons.business.exception.NotFoundException;
import com.opendatajungle.reference.data.api.business.model.User;
import com.opendatajungle.reference.data.api.infra.entity.UserEntity;
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
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    @Test
    void findAll_shouldUseZeroBasedPageAndAscendingUsernameSort_whenCalled() {
        // Given
        UserEntity entity = UserEntity.builder().id(UUID.randomUUID()).username("ada").build();
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        Pageable pageable = PageRequest.of(0, 50, Sort.by("username").ascending());
        Page<UserEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(userJpaRepository.findAll(any(Specification.class), pageableCaptor.capture())).thenReturn(page);

        // When
        PageResult<User> result = userRepositoryAdapter.findAll(1, 50, null);

        // Then
        assertThat(pageableCaptor.getValue().getPageNumber()).isZero();
        assertThat(pageableCaptor.getValue().getSort()).isEqualTo(Sort.by("username").ascending());
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst().username()).isEqualTo("ada");
    }

    @Test
    void findById_shouldReturnMappedUser_whenEntityExists() {
        // Given
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Instant updatedAt = createdAt.plus(Duration.ofDays(1));
        UserEntity entity = UserEntity
                .builder()
                .id(id)
                .username("ada")
                .firstName("Ada")
                .lastName("Lovelace")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
        when(userJpaRepository.findById(id)).thenReturn(Optional.of(entity));

        // When
        Optional<User> result = userRepositoryAdapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().username()).isEqualTo("ada");
        assertThat(result.get().firstName()).isEqualTo("Ada");
        assertThat(result.get().lastName()).isEqualTo("Lovelace");
        assertThat(result.get().createdAt()).isEqualTo(createdAt);
        assertThat(result.get().updatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void save_shouldCreateNewEntityWithGeneratedId_whenUserIdIsNull() {
        // Given
        User user = User.builder().firstName("Ada").lastName("Lovelace").username("ada").build();
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        when(userJpaRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userRepositoryAdapter.save(user);

        // Then
        assertThat(captor.getValue().getId()).isNotNull();
        assertThat(result.username()).isEqualTo("ada");
        verify(userJpaRepository, never()).findById(any());
    }

    @Test
    void save_shouldUpdateOnlyMutableFields_whenUserIdExists() {
        // Given
        UUID id = UUID.randomUUID();
        UserEntity existing = UserEntity.builder().id(id).firstName("old").lastName("old").username("old").build();
        User user = User.builder().id(id).firstName("Ada").lastName("Lovelace").username("updated").build();
        when(userJpaRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userJpaRepository.save(existing)).thenReturn(existing);

        // When
        User result = userRepositoryAdapter.save(user);

        // Then
        assertThat(existing.getFirstName()).isEqualTo("Ada");
        assertThat(existing.getLastName()).isEqualTo("Lovelace");
        assertThat(existing.getUsername()).isEqualTo("updated");
        assertThat(result.username()).isEqualTo("updated");
    }

    @Test
    void save_shouldThrowNotFoundException_whenUserIdDoesNotExist() {
        // Given
        UUID id = UUID.randomUUID();
        User user = User.builder().id(id).username("updated").build();
        when(userJpaRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userRepositoryAdapter.save(user))
                .isInstanceOf(NotFoundException.class);
        verify(userJpaRepository, never()).save(any());
    }

    @Test
    void existsById_shouldDelegateToJpaRepository_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();
        when(userJpaRepository.existsById(id)).thenReturn(true);

        // When
        boolean result = userRepositoryAdapter.existsById(id);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void existsByUsername_shouldDelegateToJpaRepository_whenCalled() {
        // Given
        when(userJpaRepository.existsByUsername("ada")).thenReturn(true);

        // When
        boolean result = userRepositoryAdapter.existsByUsername("ada");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void existsByUsernameAndIdNot_shouldDelegateToJpaRepository_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();
        when(userJpaRepository.existsByUsernameAndIdNot("ada", id)).thenReturn(false);

        // When
        boolean result = userRepositoryAdapter.existsByUsernameAndIdNot("ada", id);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void deleteById_shouldDelegateToJpaRepository_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        userRepositoryAdapter.deleteById(id);

        // Then
        verify(userJpaRepository).deleteById(id);
    }

    @Test
    void findByUsername_shouldReturnMappedUser_whenEntityExists() {
        // Given
        UserEntity entity = UserEntity.builder().id(UUID.randomUUID()).username("ada").build();
        when(userJpaRepository.findByUsername("ada")).thenReturn(Optional.of(entity));

        // When
        Optional<User> result = userRepositoryAdapter.findByUsername("ada");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().username()).isEqualTo("ada");
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenEntityAbsent() {
        // Given
        when(userJpaRepository.findByUsername("ada")).thenReturn(Optional.empty());

        // When
        Optional<User> result = userRepositoryAdapter.findByUsername("ada");

        // Then
        assertThat(result).isEmpty();
    }
}
