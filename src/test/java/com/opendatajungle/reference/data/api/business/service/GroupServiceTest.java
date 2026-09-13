package com.opendatajungle.reference.data.api.business.service;

import com.opendatajungle.commons.business.exception.AccessDeniedException;
import com.opendatajungle.commons.business.exception.NotFoundException;
import com.opendatajungle.commons.business.exception.ParamException;
import com.opendatajungle.reference.data.api.business.model.Group;
import com.opendatajungle.reference.data.api.business.model.User;
import com.opendatajungle.reference.data.api.business.repository.GroupRepository;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupUserUseCase groupUserUseCase;

    @Mock
    private UserUseCase userUseCase;

    @InjectMocks
    private GroupService groupService;

    @Test
    void findAll_shouldDelegateToRepository_whenCalled() {
        // Given
        PageResult<Group> expected = PageResult.<Group>builder()
                .content(List.of())
                .totalElements(0)
                .totalPages(0)
                .currentPage(1)
                .pageSize(50)
                .build();
        when(groupRepository.findAll(1, 50, "root")).thenReturn(expected);

        // When
        PageResult<Group> result = groupService.findAll(1, 50, "root");

        // Then
        assertThat(result).isSameAs(expected);
    }

    @Test
    void getById_shouldReturnGroup_whenGroupExists() {
        // Given
        UUID id = UUID.randomUUID();
        Group group = Group.builder().id(id).name("root").build();
        when(groupRepository.findById(id)).thenReturn(Optional.of(group));

        // When
        Group result = groupService.getById(id);

        // Then
        assertThat(result).isSameAs(group);
    }

    @Test
    void getById_shouldThrowNotFoundException_whenGroupAbsent() {
        // Given
        UUID id = UUID.randomUUID();
        when(groupRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> groupService.getById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Group not found with id: " + id);
    }

    @Test
    void create_shouldSaveGroupAndGrantAdminToCreator_whenNameNotAlreadyExists() {
        // Given
        UUID groupId = UUID.randomUUID();
        Group group = Group.builder().name("root").description("Root group").build();
        Group saved = Group.builder().id(groupId).name("root").description("Root group").build();
        when(groupRepository.existsByName("root")).thenReturn(false);
        when(groupRepository.save(group)).thenReturn(saved);

        // When
        Group result = groupService.create(group);

        // Then
        assertThat(result).isSameAs(saved);
        verify(groupUserUseCase).grantGroupAdminForCurrentUser(groupId);
    }

    @Test
    void create_shouldThrowParamException_whenNameAlreadyExists() {
        // Given
        Group group = Group.builder().name("root").build();
        when(groupRepository.existsByName("root")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> groupService.create(group))
                .isInstanceOf(ParamException.class)
                .satisfies(ex -> {
                    ParamException paramException = (ParamException) ex;
                    assertThat(paramException.getCode()).isEqualTo("GROUP_NAME_ALREADY_EXISTS");
                    assertThat(paramException.getField()).isEqualTo("name");
                });
        verify(groupRepository, never()).save(any());
        verify(groupUserUseCase, never()).grantGroupAdminForCurrentUser(any());
    }

    @Test
    void update_shouldSaveGroup_whenIdExistsAndNameFree() {
        // Given
        UUID id = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        Group group = Group.builder().name("updated").description("Updated description").build();
        User currentUser = User.builder().id(currentUserId).username("alex").build();
        when(groupRepository.existsById(id)).thenReturn(true);
        when(userUseCase.getOrCreateCurrentUser()).thenReturn(currentUser);
        when(groupRepository.existsByNameAndIdNot("updated", id)).thenReturn(false);
        ArgumentCaptor<Group> captor = ArgumentCaptor.forClass(Group.class);
        when(groupRepository.save(any())).thenReturn(group);

        // When
        Group result = groupService.update(id, group);

        // Then
        verify(groupUserUseCase).requireGroupAdmin(id, currentUserId);
        verify(groupRepository).save(captor.capture());
        Group toUpdate = captor.getValue();
        assertThat(toUpdate.id()).isEqualTo(id);
        assertThat(toUpdate.name()).isEqualTo("updated");
        assertThat(toUpdate.description()).isEqualTo("Updated description");
        assertThat(toUpdate.createdAt()).isNull();
        assertThat(toUpdate.updatedAt()).isNull();
        assertThat(result).isSameAs(group);
    }

    @Test
    void update_shouldThrowNotFoundException_whenGroupAbsent() {
        // Given
        UUID id = UUID.randomUUID();
        Group group = Group.builder().name("updated").build();
        when(groupRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> groupService.update(id, group))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Group not found with id: " + id);
        verify(groupRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowAccessDeniedException_whenCallerIsNotGroupAdmin() {
        // Given
        UUID id = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        Group group = Group.builder().name("updated").build();
        User currentUser = User.builder().id(currentUserId).username("alex").build();
        when(groupRepository.existsById(id)).thenReturn(true);
        when(userUseCase.getOrCreateCurrentUser()).thenReturn(currentUser);
        doThrow(new AccessDeniedException("not admin")).when(groupUserUseCase).requireGroupAdmin(id, currentUserId);

        // When & Then
        assertThatThrownBy(() -> groupService.update(id, group))
                .isInstanceOf(AccessDeniedException.class);
        verify(groupRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowParamException_whenNameTakenByAnotherGroup() {
        // Given
        UUID id = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        Group group = Group.builder().name("duplicate").build();
        User currentUser = User.builder().id(currentUserId).username("alex").build();
        when(groupRepository.existsById(id)).thenReturn(true);
        when(userUseCase.getOrCreateCurrentUser()).thenReturn(currentUser);
        when(groupRepository.existsByNameAndIdNot("duplicate", id)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> groupService.update(id, group))
                .isInstanceOf(ParamException.class)
                .satisfies(ex -> {
                    ParamException paramException = (ParamException) ex;
                    assertThat(paramException.getCode()).isEqualTo("GROUP_NAME_ALREADY_EXISTS");
                    assertThat(paramException.getField()).isEqualTo("name");
                });
        verify(groupRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteGroup_whenGroupExists() {
        // Given
        UUID id = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        User currentUser = User.builder().id(currentUserId).username("alex").build();
        when(groupRepository.existsById(id)).thenReturn(true);
        when(userUseCase.getOrCreateCurrentUser()).thenReturn(currentUser);

        // When
        groupService.delete(id);

        // Then
        verify(groupUserUseCase).requireGroupAdmin(id, currentUserId);
        verify(groupRepository).deleteById(id);
    }

    @Test
    void delete_shouldThrowNotFoundException_whenGroupAbsent() {
        // Given
        UUID id = UUID.randomUUID();
        when(groupRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> groupService.delete(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Group not found with id: " + id);
        verify(groupRepository, never()).deleteById(any());
    }

    @Test
    void delete_shouldThrowAccessDeniedException_whenCallerIsNotGroupAdmin() {
        // Given
        UUID id = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        User currentUser = User.builder().id(currentUserId).username("alex").build();
        when(groupRepository.existsById(id)).thenReturn(true);
        when(userUseCase.getOrCreateCurrentUser()).thenReturn(currentUser);
        doThrow(new AccessDeniedException("not admin")).when(groupUserUseCase).requireGroupAdmin(id, currentUserId);

        // When & Then
        assertThatThrownBy(() -> groupService.delete(id))
                .isInstanceOf(AccessDeniedException.class);
        verify(groupRepository, never()).deleteById(any());
    }
}
