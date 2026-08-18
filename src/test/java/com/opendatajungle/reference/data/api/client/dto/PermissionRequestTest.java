package com.opendatajungle.reference.data.api.client.dto;

import com.opendatajungle.reference.data.api.business.model.Permission;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionRequestTest {

    @Test
    void toBusiness_shouldMapAllFields_whenCalled() {
        // Given
        PermissionRequest request = new PermissionRequest("root_permission", "Root permission", true, true, true);

        // When
        Permission permission = request.toBusiness();

        // Then
        assertThat(permission.id()).isNull();
        assertThat(permission.name()).isEqualTo("root_permission");
        assertThat(permission.description()).isEqualTo("Root permission");
        assertThat(permission.canRead()).isTrue();
        assertThat(permission.canWrite()).isTrue();
        assertThat(permission.isAdmin()).isTrue();
    }
}
