package com.opendatajungle.reference.data.api.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opendatajungle.reference.data.api.client.dto.PermissionRequest;
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

import java.util.UUID;

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
class PermissionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createPermission_shouldPersistAndReturnCreatedPermission_whenNameIsUnique() throws Exception {
        // Given
        PermissionRequest request = new PermissionRequest("editor", "Editor permission", true, true, false);

        // When & Then
        mockMvc.perform(post("/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.name").value("editor"))
                .andExpect(jsonPath("$.canRead").value(true))
                .andExpect(jsonPath("$.canWrite").value(true))
                .andExpect(jsonPath("$.isAdmin").value(false));
    }

    @Test
    void createPermission_shouldReturnBadRequest_whenNameAlreadyExists() throws Exception {
        // Given
        PermissionRequest request = new PermissionRequest("root_permission", "Duplicate of the seeded permission", true, true, true);

        // When & Then
        mockMvc.perform(post("/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PERMISSION_NAME_ALREADY_EXISTS"));
    }

    @Test
    void getAllPermissions_shouldReturnSeededRootPermission_whenCalled() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/permissions").param("page", "1").param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.name == 'root_permission')]").exists());
    }

    @Test
    void getPermissionById_shouldReturnSeededRootPermission_whenIdExists() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/permissions/00000000-0000-0000-0000-000000000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("root_permission"));
    }

    @Test
    void getPermissionById_shouldReturnNotFound_whenIdDoesNotExist() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/permissions/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePermission_shouldPersistChanges_whenPermissionExists() throws Exception {
        // Given
        String created = mockMvc.perform(post("/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PermissionRequest("to-update", "Before update", false, false, false))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = objectMapper.readTree(created).get("id").asText();

        // When & Then
        mockMvc.perform(put("/api/v1/permissions/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PermissionRequest("updated", "After update", true, true, true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("After update"))
                .andExpect(jsonPath("$.name").value("updated"))
                .andExpect(jsonPath("$.canWrite").value(true))
                .andExpect(jsonPath("$.isAdmin").value(true))
                .andExpect(jsonPath("$.canRead").value(true));
    }

    @Test
    void deletePermission_shouldRemovePermission_whenPermissionExists() throws Exception {
        // Given
        String created = mockMvc.perform(post("/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PermissionRequest("to-delete", "Will be deleted", false, false, false))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = objectMapper.readTree(created).get("id").asText();

        // When
        mockMvc.perform(delete("/api/v1/permissions/" + id))
                .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/api/v1/permissions/" + id))
                .andExpect(status().isNotFound());
    }
}
