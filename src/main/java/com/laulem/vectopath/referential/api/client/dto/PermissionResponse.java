package com.laulem.vectopath.referential.api.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.laulem.vectopath.referential.api.business.model.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionResponse {
    private UUID id;
    private String name;
    private String description;
    private Boolean canRead;
    private Boolean canWrite;
    private Boolean isAdmin;

    public static PermissionResponse fromBusiness(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.id())
                .name(permission.name())
                .description(permission.description())
                .canRead(permission.canRead())
                .canWrite(permission.canWrite())
                .isAdmin(permission.isAdmin())
                .build();
    }
}
