package com.laulem.vectopath.referential.api.client.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupRequest {
    @NotBlank(message = "Group name is required")
    private String name;

    private String description;

    public com.laulem.vectopath.referential.api.business.model.Group toBusiness() {
        return com.laulem.vectopath.referential.api.business.model.Group.builder()
                .name(this.name)
                .description(this.description)
                .build();
    }
}
