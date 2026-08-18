package com.opendatajungle.reference.data.api.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opendatajungle.reference.data.api.client.dto.UserRequest;
import com.opendatajungle.reference.data.api.testconfig.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
@Transactional
@Import(TestcontainersConfiguration.class)
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_shouldPersistAndReturnCreatedUser_whenUsernameIsUnique() throws Exception {
        // Given
        UserRequest request = new UserRequest("Grace", "Hopper", "grace");

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.username").value("grace"))
                .andExpect(jsonPath("$.firstName").value("Grace"))
                .andExpect(jsonPath("$.lastName").value("Hopper"));
    }

    @Test
    void createUser_shouldReturnBadRequest_whenUsernameAlreadyExists() throws Exception {
        // Given
        UserRequest request = new UserRequest("Another", "Anonymous", "anonymous");

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("USER_USERNAME_ALREADY_EXISTS"));
    }

    @Test
    void getAllUsers_shouldReturnSeededAnonymousUser_whenFilteringByUsername() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users").param("username", "anonymous").param("page", "1").param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("anonymous"));
    }

    @Test
    void getUserById_shouldReturnSeededAnonymousUser_whenIdExists() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users/00000000-0000-0000-0000-000000000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("anonymous"));
    }

    @Test
    void getUserByUsername_shouldReturnSeededAnonymousUser_whenUsernameExists() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users/username/anonymous"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("anonymous"));
    }

    @Test
    void getUserByUsername_shouldReturnNotFound_whenUsernameDoesNotExist() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users/username/does-not-exist"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_shouldPersistChanges_whenUserExists() throws Exception {
        // Given
        String created = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Before", "Update", "to-update"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = objectMapper.readTree(created).get("id").asText();

        // When & Then
        mockMvc.perform(put("/api/v1/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("After", "Update", "updated"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updated"))
                .andExpect(jsonPath("$.lastName").value("Update"))
                .andExpect(jsonPath("$.firstName").value("After"));
    }

    @Test
    void deleteUser_shouldRemoveUser_whenUserExists() throws Exception {
        // Given
        String created = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("To", "Delete", "to-delete"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = objectMapper.readTree(created).get("id").asText();

        // When
        mockMvc.perform(delete("/api/v1/users/" + id))
                .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/api/v1/users/" + id))
                .andExpect(status().isNotFound());
    }
}
