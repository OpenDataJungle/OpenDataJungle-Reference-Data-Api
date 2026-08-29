package com.opendatajungle.reference.data.api.client.dto;

import com.opendatajungle.reference.data.api.business.model.Group;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GroupResponseTest {

    @Test
    void fromBusiness_shouldMapAllFields_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Instant updatedAt = createdAt.plus(Duration.ofDays(1));
        Group group = Group.builder()
                .id(id)
                .name("root")
                .description("Root group")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // When
        GroupResponse response = GroupResponse.fromBusiness(group);

        // Then
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getName()).isEqualTo("root");
        assertThat(response.getDescription()).isEqualTo("Root group");
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
        assertThat(response.getUpdatedAt()).isEqualTo(updatedAt);
    }
}
