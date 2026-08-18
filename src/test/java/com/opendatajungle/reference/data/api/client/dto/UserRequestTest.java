package com.opendatajungle.reference.data.api.client.dto;

import com.opendatajungle.reference.data.api.business.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRequestTest {

    @Test
    void toBusiness_shouldMapAllFields_whenCalled() {
        // Given
        UserRequest request = new UserRequest("Ada", "Lovelace", "ada");

        // When
        User user = request.toBusiness();

        // Then
        assertThat(user.id()).isNull();
        assertThat(user.firstName()).isEqualTo("Ada");
        assertThat(user.lastName()).isEqualTo("Lovelace");
        assertThat(user.username()).isEqualTo("ada");
    }
}
