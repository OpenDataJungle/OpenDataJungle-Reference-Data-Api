package com.opendatajungle.reference.data.api.client.dto;

import com.opendatajungle.reference.data.api.business.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserUpdateRequestTest {

    @Test
    void toBusiness_shouldMapEditableFieldsOnly_whenCalled() {
        // Given
        UserUpdateRequest request = new UserUpdateRequest("Ada", "Lovelace", "ada");

        // When
        User user = request.toBusiness();

        // Then
        assertThat(user.id()).isNull();
        assertThat(user.firstName()).isEqualTo("Ada");
        assertThat(user.lastName()).isEqualTo("Lovelace");
        assertThat(user.username()).isEqualTo("ada");
        assertThat(user.authId()).isNull();
        assertThat(user.authIss()).isNull();
    }
}
