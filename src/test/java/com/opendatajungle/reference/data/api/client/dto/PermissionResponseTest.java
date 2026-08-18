package com.opendatajungle.reference.data.api.client.dto;

import com.opendatajungle.reference.data.api.business.model.Permission;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionResponseTest {

    @Test
    void fromBusiness_shouldMapAllFields_whenCalled() {
        // Given
        UUID id = UUID.randomUUID();
        Permission permission = Permission.builder()
                .id(id)
                .name("root_permission")
                .description("Root permission")
                .canRead(true)
                .canWrite(false)
                .isAdmin(true)
                .build();

        // When
        PermissionResponse response = PermissionResponse.fromBusiness(permission);

        // Then
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getName()).isEqualTo("root_permission");
        assertThat(response.getDescription()).isEqualTo("Root permission");
        assertThat(response.getCanRead()).isTrue();
        assertThat(response.getCanWrite()).isFalse();
        assertThat(response.getIsAdmin()).isTrue();
    }
}
