package com.opendatajungle.reference.data.api.client.mapper;

import com.opendatajungle.reference.data.api.business.model.Group;
import com.opendatajungle.reference.data.api.client.dto.GroupResponse;
import com.opendatajungle.reference.data.api.client.dto.PaginatedResponse;
import com.opendatajungle.reference.data.api.shared.PageResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaginatedResponseMapperTest {

    @Test
    void toPaginatedResponse_shouldMapContentAndPagingMetadata_whenCalled() {
        // Given
        Group group = Group.builder().id(UUID.randomUUID()).name("root").description("Root group").build();
        PageResult<Group> pageResult = PageResult.<Group>builder()
                .content(List.of(group))
                .totalElements(200)
                .totalPages(4)
                .currentPage(3)
                .pageSize(50)
                .build();

        // When
        PaginatedResponse<GroupResponse> result = PaginatedResponseMapper.toPaginatedResponse(pageResult, GroupResponse::fromBusiness);

        // Then
        assertThat(result.content()).containsExactly(GroupResponse.fromBusiness(group));
        assertThat(result.totalElements()).isEqualTo(200);
        assertThat(result.totalPages()).isEqualTo(4);
        assertThat(result.currentPage()).isEqualTo(3);
        assertThat(result.pageSize()).isEqualTo(50);
    }

    @Test
    void toPaginatedResponse_shouldReturnEmptyContent_whenPageResultContentIsEmpty() {
        // Given
        PageResult<Group> pageResult = PageResult.<Group>builder()
                .content(List.of())
                .totalElements(0)
                .totalPages(0)
                .currentPage(1)
                .pageSize(50)
                .build();

        // When
        PaginatedResponse<GroupResponse> result = PaginatedResponseMapper.toPaginatedResponse(pageResult, GroupResponse::fromBusiness);

        // Then
        assertThat(result.content()).isEmpty();
    }
}
