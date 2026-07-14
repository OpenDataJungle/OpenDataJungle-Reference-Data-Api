package com.laulem.vectopath.referential.api.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.laulem.vectopath.referential.api.business.model.GroupUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupUserResponse {
    private GroupResponse group;
    private PermissionResponse permission;
    private UserResponse user;

    public static GroupUserResponse fromBusiness(GroupUser groupUser) {
        return GroupUserResponse.builder()
                .group(GroupResponse.fromBusiness(groupUser.group()))
                .permission(PermissionResponse.fromBusiness(groupUser.permission()))
                .user(UserResponse.fromBusiness(groupUser.user()))
                .build();
    }
}
