package com.opendatajungle.reference.data.api.business.service;

import com.opendatajungle.commons.business.exception.NotFoundException;
import com.opendatajungle.commons.business.exception.ParamException;
import com.opendatajungle.commons.business.service.AuthenticationUseCase;
import com.opendatajungle.reference.data.api.business.model.User;
import com.opendatajungle.reference.data.api.business.repository.UserRepository;
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

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationUseCase authenticationUseCase;

    @InjectMocks
    private UserService userService;

    @Test
    void findAll_shouldDelegateToRepository_whenCalled() {
        // Given
        PageResult<User> expected = PageResult.<User>builder()
                .content(List.of())
                .totalElements(0)
                .totalPages(0)
                .currentPage(1)
                .pageSize(50)
                .build();
        when(userRepository.findAll(1, 50, "anonymous")).thenReturn(expected);

        // When
        PageResult<User> result = userService.findAll(1, 50, "anonymous");

        // Then
        assertThat(result).isSameAs(expected);
    }

    @Test
    void getById_shouldReturnUser_whenUserExists() {
        // Given
        UUID id = UUID.randomUUID();
        User user = User.builder().id(id).username("anonymous").build();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // When
        User result = userService.getById(id);

        // Then
        assertThat(result).isSameAs(user);
    }

    @Test
    void getById_shouldThrowNotFoundException_whenUserAbsent() {
        // Given
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found with id: " + id);
    }

    @Test
    void update_shouldSaveUser_whenIdExistsAndUsernameFree() {
        // Given
        UUID id = UUID.randomUUID();
        User user = User.builder().firstName("firstName").lastName("lastName").username("username").build();
        when(userRepository.existsById(id)).thenReturn(true);
        when(userRepository.existsByUsernameAndIdNot("username", id)).thenReturn(false);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any())).thenReturn(user);

        // When
        User result = userService.update(id, user);

        // Then
        verify(userRepository).save(captor.capture());
        User toUpdate = captor.getValue();
        assertThat(toUpdate.id()).isEqualTo(id);
        assertThat(toUpdate.firstName()).isEqualTo("firstName");
        assertThat(toUpdate.lastName()).isEqualTo("lastName");
        assertThat(toUpdate.username()).isEqualTo("username");
        assertThat(toUpdate.createdAt()).isNull();
        assertThat(toUpdate.updatedAt()).isNull();
        assertThat(result).isSameAs(user);
    }

    @Test
    void update_shouldThrowNotFoundException_whenUserAbsent() {
        // Given
        UUID id = UUID.randomUUID();
        User user = User.builder().username("username").build();
        when(userRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.update(id, user))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found with id: " + id);
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowParamException_whenUsernameTakenByAnotherUser() {
        // Given
        UUID id = UUID.randomUUID();
        User user = User.builder().username("duplicate").build();
        when(userRepository.existsById(id)).thenReturn(true);
        when(userRepository.existsByUsernameAndIdNot("duplicate", id)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.update(id, user))
                .isInstanceOf(ParamException.class)
                .satisfies(ex -> {
                    ParamException paramException = (ParamException) ex;
                    assertThat(paramException.getCode()).isEqualTo("USER_USERNAME_ALREADY_EXISTS");
                    assertThat(paramException.getField()).isEqualTo("username");
                });
        verify(userRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteUser_whenUserExists() {
        // Given
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(true);

        // When
        userService.delete(id);

        // Then
        verify(userRepository).deleteById(id);
    }

    @Test
    void delete_shouldThrowNotFoundException_whenUserAbsent() {
        // Given
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.delete(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found with id: " + id);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void getByUsername_shouldReturnUser_whenUserExists() {
        // Given
        User user = User.builder().username("ada").build();
        when(userRepository.findByUsername("ada")).thenReturn(Optional.of(user));

        // When
        User result = userService.getByUsername("ada");

        // Then
        assertThat(result).isSameAs(user);
    }

    @Test
    void getByUsername_shouldThrowNotFoundException_whenUserAbsent() {
        // Given
        when(userRepository.findByUsername("ada")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getByUsername("ada"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found with id: ada");
    }

    @Test
    void getOrCreateCurrentUser_shouldReturnExistingUser_whenUsernameAlreadyRegistered() {
        // Given
        User existing = User.builder().id(UUID.randomUUID()).username("ada").firstName("Ada").lastName("Lovelace").build();
        when(authenticationUseCase.findCurrentUser()).thenReturn(Optional.of("ada"));
        when(authenticationUseCase.findCurrentUserFirstName()).thenReturn(Optional.of("Ada"));
        when(authenticationUseCase.findCurrentUserLastName()).thenReturn(Optional.of("Lovelace"));
        when(userRepository.findByUsername("ada")).thenReturn(Optional.of(existing));

        // When
        User result = userService.getOrCreateCurrentUser();

        // Then
        assertThat(result).isSameAs(existing);
        verify(userRepository, never()).save(any());
    }

    @Test
    void getOrCreateCurrentUser_shouldCreateUser_whenUsernameNotYetRegistered() {
        // Given
        when(authenticationUseCase.findCurrentUser()).thenReturn(Optional.of("ada"));
        when(authenticationUseCase.findCurrentUserFirstName()).thenReturn(Optional.of("Ada"));
        when(authenticationUseCase.findCurrentUserLastName()).thenReturn(Optional.of("Lovelace"));
        when(userRepository.findByUsername("ada")).thenReturn(Optional.empty());
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        User saved = User.builder().id(UUID.randomUUID()).username("ada").firstName("Ada").lastName("Lovelace").build();
        when(userRepository.save(any())).thenReturn(saved);

        // When
        User result = userService.getOrCreateCurrentUser();

        // Then
        verify(userRepository).save(captor.capture());
        User toCreate = captor.getValue();
        assertThat(toCreate.id()).isNull();
        assertThat(toCreate.username()).isEqualTo("ada");
        assertThat(toCreate.firstName()).isEqualTo("Ada");
        assertThat(toCreate.lastName()).isEqualTo("Lovelace");
        assertThat(result).isSameAs(saved);
    }

    @Test
    void getOrCreateCurrentUser_shouldThrowParamException_whenCurrentUsernameBlank() {
        // Given
        when(authenticationUseCase.findCurrentUser()).thenReturn(Optional.of("  "));

        // When & Then
        assertThatThrownBy(() -> userService.getOrCreateCurrentUser())
                .isInstanceOf(ParamException.class)
                .satisfies(ex -> {
                    ParamException paramException = (ParamException) ex;
                    assertThat(paramException.getCode()).isEqualTo("USER_USERNAME_REQUIRED");
                    assertThat(paramException.getField()).isEqualTo("username");
                });
    }

    @Test
    void getOrCreateCurrentUser_shouldThrowParamException_whenCurrentFirstNameBlank() {
        // Given
        when(authenticationUseCase.findCurrentUser()).thenReturn(Optional.of("ada"));
        when(authenticationUseCase.findCurrentUserFirstName()).thenReturn(Optional.of(""));

        // When & Then
        assertThatThrownBy(() -> userService.getOrCreateCurrentUser())
                .isInstanceOf(ParamException.class)
                .satisfies(ex -> {
                    ParamException paramException = (ParamException) ex;
                    assertThat(paramException.getCode()).isEqualTo("USER_FIRST_NAME_REQUIRED");
                    assertThat(paramException.getField()).isEqualTo("firstName");
                });
    }

    @Test
    void getOrCreateCurrentUser_shouldThrowParamException_whenCurrentLastNameBlank() {
        // Given
        when(authenticationUseCase.findCurrentUser()).thenReturn(Optional.of("ada"));
        when(authenticationUseCase.findCurrentUserFirstName()).thenReturn(Optional.of("Ada"));
        when(authenticationUseCase.findCurrentUserLastName()).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getOrCreateCurrentUser())
                .isInstanceOf(ParamException.class)
                .satisfies(ex -> {
                    ParamException paramException = (ParamException) ex;
                    assertThat(paramException.getCode()).isEqualTo("USER_LAST_NAME_REQUIRED");
                    assertThat(paramException.getField()).isEqualTo("lastName");
                });
    }
}
