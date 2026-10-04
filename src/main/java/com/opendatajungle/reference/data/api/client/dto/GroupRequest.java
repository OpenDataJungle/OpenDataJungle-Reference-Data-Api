package com.opendatajungle.reference.data.api.client.dto;

import com.opendatajungle.reference.data.api.business.model.Group;
import com.opendatajungle.reference.data.api.shared.FieldConstraints;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupRequest {
    @NotBlank(message = "Group name is required")
    @Pattern(regexp = "[a-zA-Z._-]+", message = "Group name must contain only letters a-z, A-Z and the characters '-', '_', '.'")
    @Size(max = FieldConstraints.MAX_TEXT_LENGTH, message = "Group name must not exceed {max} characters")
    private String name;

    @Size(max = FieldConstraints.MAX_TEXT_LENGTH, message = "Group description must not exceed {max} characters")
    private String description;

    public Group toBusiness() {
        return Group.builder()
                .name(this.name)
                .description(this.description)
                .build();
    }
}
