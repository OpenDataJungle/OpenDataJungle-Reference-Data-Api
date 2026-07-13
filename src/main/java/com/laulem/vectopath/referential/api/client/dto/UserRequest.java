package com.laulem.vectopath.referential.api.client.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "User first name is required")
    private String firstName;

    @NotBlank(message = "User last name is required")
    private String lastName;

    @NotBlank(message = "User username is required")
    private String username;

    public com.laulem.vectopath.referential.api.business.model.User toBusiness() {
        return com.laulem.vectopath.referential.api.business.model.User.builder()
                .firstName(this.firstName)
                .lastName(this.lastName)
                .username(this.username)
                .build();
    }
}
