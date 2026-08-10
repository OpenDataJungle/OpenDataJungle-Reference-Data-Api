package com.opendatajungle.reference.data.api.client.mapper;

import com.opendatajungle.reference.data.api.client.dto.PaginatedResponse;
import com.opendatajungle.reference.data.api.shared.PageResult;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class PaginatedResponseMapper {
    public static <S, T> PaginatedResponse<T> toPaginatedResponse(
            PageResult<S> pageResult,
            Function<S, T> mapper) {
        return PaginatedResponse.<T>builder()
                .content(pageResult.content().stream()
                        .map(mapper)
                        .toList())
                .totalElements(pageResult.totalElements())
                .totalPages(pageResult.totalPages())
                .currentPage(pageResult.currentPage())
                .pageSize(pageResult.pageSize())
                .build();
    }
}
