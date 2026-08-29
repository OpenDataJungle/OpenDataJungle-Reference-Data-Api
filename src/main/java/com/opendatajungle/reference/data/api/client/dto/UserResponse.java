package com.opendatajungle.reference.data.api.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.opendatajungle.reference.data.api.business.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private Instant createdAt;
    private Instant updatedAt;

    public static UserResponse fromBusiness(User user) {
        return UserResponse.builder()
                .id(user.id())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .username(user.username())
                .createdAt(user.createdAt())
                .updatedAt(user.updatedAt())
                .build();
    }
}
