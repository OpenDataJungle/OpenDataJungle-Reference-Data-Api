package com.laulem.vectopath.referential.api.client.dto;

import com.laulem.vectopath.referential.api.business.model.Group;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupCreate {
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
