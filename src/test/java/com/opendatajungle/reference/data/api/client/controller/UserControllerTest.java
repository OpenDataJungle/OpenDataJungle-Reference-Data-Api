package com.opendatajungle.reference.data.api.client.controller;

import com.opendatajungle.reference.data.api.business.model.User;
import com.opendatajungle.reference.data.api.business.service.UserUseCase;
import com.opendatajungle.reference.data.api.client.dto.PaginatedResponse;
import com.opendatajungle.reference.data.api.client.dto.UserRequest;
import com.opendatajungle.reference.data.api.client.dto.UserResponse;
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
class UserControllerTest {

    @Mock
    private UserUseCase userUseCase;

    @InjectMocks
    private UserController userController;

    @Test
    void getAllUsers_shouldReturnMappedPage_whenCalled() {
        // Given
        User user = User.builder().id(UUID.randomUUID()).username("alex").build();
        PageResult<User> pageResult = PageResult.<User>builder()
                .content(List.of(user))
                .totalElements(200)
                .totalPages(4)
                .currentPage(1)
                .pageSize(50)
                .build();
        when(userUseCase.findAll(1, 50, "alex")).thenReturn(pageResult);

        // When
        PaginatedResponse<UserResponse> response = userController.getAllUsers("alex", 1, 50);

        // Then
        assertThat(response.content()).containsExactly(UserResponse.fromBusiness(user));
    }

    @Test
    void getUserById_shouldReturnMappedUser_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();
        User user = User.builder().id(id).username("alex").build();
        when(userUseCase.getById(id)).thenReturn(user);

        // When
        UserResponse response = userController.getUserById(id);

        // Then
        assertThat(response).isEqualTo(UserResponse.fromBusiness(user));
    }

    @Test
    void getUserByUsername_shouldReturnMappedUser_whenCalled() {
        // Given
        User user = User.builder().id(UUID.randomUUID()).username("alex").build();
        when(userUseCase.getByUsername("alex")).thenReturn(user);

        // When
        UserResponse response = userController.getUserByUsername("alex");

        // Then
        assertThat(response).isEqualTo(UserResponse.fromBusiness(user));
    }

    @Test
    void getOrCreateMe_shouldReturnMappedCurrentUser_whenCalled() {
        // Given
        User user = User.builder().id(UUID.randomUUID()).username("alex").build();
        when(userUseCase.getOrCreateCurrentUser()).thenReturn(user);

        // When
        UserResponse response = userController.getOrCreateMe();

        // Then
        assertThat(response).isEqualTo(UserResponse.fromBusiness(user));
    }

    @Test
    void createUser_shouldDelegateToUseCaseWithMappedBusinessObject_whenCalled() {
        // Given
        UserRequest request = new UserRequest("Ada", "Lovelace", "ada");
        User created = User.builder().id(UUID.randomUUID()).username("ada").build();
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userUseCase.create(captor.capture())).thenReturn(created);

        // When
        UserResponse response = userController.createUser(request);

        // Then
        assertThat(captor.getValue().username()).isEqualTo("ada");
        assertThat(captor.getValue().firstName()).isEqualTo("Ada");
        assertThat(captor.getValue().lastName()).isEqualTo("Lovelace");
        assertThat(response).isEqualTo(UserResponse.fromBusiness(created));
    }


    @Test
    void updateUser_shouldDelegateToUseCaseWithIdAndMappedBusinessObject_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();
        UserRequest request = new UserRequest("alex", "lem", "alexx");
        User updated = User.builder().id(id).username("alexx").build();
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userUseCase.update(eq(id), captor.capture())).thenReturn(updated);

        // When
        UserResponse response = userController.updateUser(id, request);

        // Then
        assertThat(captor.getValue().username()).isEqualTo("alexx");
        assertThat(captor.getValue().firstName()).isEqualTo("alex");
        assertThat(captor.getValue().lastName()).isEqualTo("lem");
        assertThat(response).isEqualTo(UserResponse.fromBusiness(updated));
    }

    @Test
    void deleteUser_shouldDelegateToUseCase_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        userController.deleteUser(id);

        // Then
        verify(userUseCase).delete(id);
    }
}
