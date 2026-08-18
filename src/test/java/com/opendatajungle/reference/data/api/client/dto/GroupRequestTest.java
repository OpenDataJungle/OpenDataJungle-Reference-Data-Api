package com.opendatajungle.reference.data.api.client.dto;

import com.opendatajungle.reference.data.api.business.model.Group;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GroupRequestTest {

    @Test
    void toBusiness_shouldMapNameAndDescription_whenCalled() {
        // Given
        GroupRequest request = new GroupRequest("root", "Root group");

        // When
        Group group = request.toBusiness();

        // Then
        assertThat(group.id()).isNull();
        assertThat(group.name()).isEqualTo("root");
        assertThat(group.description()).isEqualTo("Root group");
    }
}
