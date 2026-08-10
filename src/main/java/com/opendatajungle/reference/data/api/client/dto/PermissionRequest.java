package com.opendatajungle.reference.data.api.client.dto;

import com.opendatajungle.reference.data.api.business.model.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRequest {
    @NotBlank(message = "Permission name is required")
    private String name;

    @NotBlank(message = "Permission description is required")
    private String description;

    @NotNull(message = "canRead is required")
    private Boolean canRead;

    @NotNull(message = "canWrite is required")
    private Boolean canWrite;

    @NotNull(message = "isAdmin is required")
    private Boolean isAdmin;

    public Permission toBusiness() {
        return Permission.builder()
                .name(this.name)
                .description(this.description)
                .canRead(this.canRead)
                .canWrite(this.canWrite)
                .isAdmin(this.isAdmin)
                .build();
    }
}
