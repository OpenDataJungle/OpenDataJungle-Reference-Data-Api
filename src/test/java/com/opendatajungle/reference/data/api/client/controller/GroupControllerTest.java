package com.opendatajungle.reference.data.api.client.controller;

import com.opendatajungle.reference.data.api.business.model.Group;
import com.opendatajungle.reference.data.api.business.service.GroupUseCase;
import com.opendatajungle.reference.data.api.client.dto.GroupRequest;
import com.opendatajungle.reference.data.api.client.dto.GroupResponse;
import com.opendatajungle.reference.data.api.client.dto.PaginatedResponse;
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
class GroupControllerTest {

    @Mock
    private GroupUseCase groupUseCase;

    @InjectMocks
    private GroupController groupController;

    @Test
    void getAllGroups_shouldReturnMappedPage_whenCalled() {
        // Given
        Group group = Group.builder().id(UUID.randomUUID()).name("root").description("Root group").build();
        PageResult<Group> pageResult = PageResult.<Group>builder()
                .content(List.of(group))
                .totalElements(200)
                .totalPages(4)
                .currentPage(2)
                .pageSize(50)
                .build();
        when(groupUseCase.findAll(1, 50, "root")).thenReturn(pageResult);

        // When
        PaginatedResponse<GroupResponse> response = groupController.getAllGroups("root", 1, 50);

        // Then
        assertThat(response.content()).containsExactly(GroupResponse.fromBusiness(group));
        assertThat(response.totalElements()).isEqualTo(200);
        assertThat(response.totalPages()).isEqualTo(4);
        assertThat(response.currentPage()).isEqualTo(2);
        assertThat(response.pageSize()).isEqualTo(50);
    }

    @Test
    void getGroupById_shouldReturnMappedGroup_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();
        Group group = Group.builder().id(id).name("root").build();
        when(groupUseCase.getById(id)).thenReturn(group);

        // When
        GroupResponse response = groupController.getGroupById(id);

        // Then
        assertThat(response).isEqualTo(GroupResponse.fromBusiness(group));
    }

    @Test
    void createGroup_shouldDelegateToUseCaseWithMappedBusinessObject_whenCalled() {
        // Given
        GroupRequest request = new GroupRequest("root", "Root group");
        Group created = Group.builder().id(UUID.randomUUID()).name("root").description("Root group").build();
        ArgumentCaptor<Group> captor = ArgumentCaptor.forClass(Group.class);
        when(groupUseCase.create(captor.capture())).thenReturn(created);

        // When
        GroupResponse response = groupController.createGroup(request);

        // Then
        assertThat(captor.getValue().name()).isEqualTo("root");
        assertThat(captor.getValue().description()).isEqualTo("Root group");
        assertThat(response).isEqualTo(GroupResponse.fromBusiness(created));
    }

    @Test
    void updateGroup_shouldDelegateToUseCaseWithIdAndMappedBusinessObject_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();
        GroupRequest request = new GroupRequest("new name", "Updated description");
        Group updated = Group.builder().id(id).name("new name").description("Updated description").build();
        ArgumentCaptor<Group> captor = ArgumentCaptor.forClass(Group.class);
        when(groupUseCase.update(eq(id), captor.capture())).thenReturn(updated);

        // When
        GroupResponse response = groupController.updateGroup(id, request);

        // Then
        assertThat(captor.getValue().name()).isEqualTo("new name");
        assertThat(response).isEqualTo(GroupResponse.fromBusiness(updated));
    }

    @Test
    void deleteGroup_shouldDelegateToUseCase_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        groupController.deleteGroup(id);

        // Then
        verify(groupUseCase).delete(id);
    }
}
