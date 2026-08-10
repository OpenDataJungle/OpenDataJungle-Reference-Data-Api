package com.opendatajungle.reference.data.api.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.opendatajungle.reference.data.api.business.model.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupResponse {
    private UUID id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GroupResponse fromBusiness(Group group) {
        return GroupResponse.builder()
                .id(group.id())
                .name(group.name())
                .description(group.description())
                .createdAt(group.createdAt())
                .updatedAt(group.updatedAt())
                .build();
    }
}
