package com.opendatajungle.reference.data.api.client.dto;

import com.opendatajungle.reference.data.api.business.model.User;
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

    public User toBusiness() {
        return User.builder()
                .firstName(this.firstName)
                .lastName(this.lastName)
                .username(this.username)
                .build();
    }
}
