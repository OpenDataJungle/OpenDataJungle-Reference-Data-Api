package com.opendatajungle.reference.data.api.client.dto;

import com.opendatajungle.reference.data.api.business.model.User;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseTest {

    @Test
    void fromBusiness_shouldMapAllFields_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Instant updatedAt = createdAt.plus(Duration.ofDays(1));
        User user = User.builder()
                .id(id)
                .firstName("Ada")
                .lastName("Lovelace")
                .username("ada")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // When
        UserResponse response = UserResponse.fromBusiness(user);

        // Then
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getFirstName()).isEqualTo("Ada");
        assertThat(response.getLastName()).isEqualTo("Lovelace");
        assertThat(response.getUsername()).isEqualTo("ada");
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
        assertThat(response.getUpdatedAt()).isEqualTo(updatedAt);
    }
}
