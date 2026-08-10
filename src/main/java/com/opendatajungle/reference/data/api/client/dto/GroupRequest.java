package com.opendatajungle.reference.data.api.client.dto;

import com.opendatajungle.reference.data.api.business.model.Group;
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

    public Group toBusiness() {
        return Group.builder()
                .name(this.name)
                .description(this.description)
                .build();
    }
}
