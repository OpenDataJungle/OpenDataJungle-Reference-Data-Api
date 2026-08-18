package com.opendatajungle.reference.data.api.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opendatajungle.reference.data.api.client.dto.GroupRequest;
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
class GroupControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createGroup_shouldPersistAndReturnCreatedGroup_whenNameIsUnique() throws Exception {
        // Given
        GroupRequest request = new GroupRequest("engineering", "Engineering group");

        // When & Then
        mockMvc.perform(post("/api/v1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.name").value("engineering"))
                .andExpect(jsonPath("$.description").value("Engineering group"));
    }

    @Test
    void createGroup_shouldReturnBadRequest_whenNameAlreadyExists() throws Exception {
        // Given
        GroupRequest request = new GroupRequest("root", "Duplicate of the seeded root group");

        // When & Then
        mockMvc.perform(post("/api/v1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("GROUP_NAME_ALREADY_EXISTS"));
    }

    @Test
    void createGroup_shouldReturnBadRequest_whenNameIsBlank() throws Exception {
        // Given
        GroupRequest request = new GroupRequest("", "Missing name");

        // When & Then
        mockMvc.perform(post("/api/v1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void getAllGroups_shouldReturnSeededRootGroup_whenFilteringByName() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/groups").param("name", "root").param("page", "1").param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("root"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getGroupById_shouldReturnSeededRootGroup_whenIdExists() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/groups/00000000-0000-0000-0000-000000000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("root"));
    }

    @Test
    void getGroupById_shouldReturnNotFound_whenIdDoesNotExist() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/groups/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void updateGroup_shouldPersistChanges_whenGroupExists() throws Exception {
        // Given
        String location = mockMvc.perform(post("/api/v1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest("to-update", "Before update"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = objectMapper.readTree(location).get("id").asText();

        // When & Then
        mockMvc.perform(put("/api/v1/groups/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest("updated", "After update"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated"))
                .andExpect(jsonPath("$.description").value("After update"));
    }

    @Test
    void updateGroup_shouldReturnNotFound_whenGroupDoesNotExist() throws Exception {
        // Given
        GroupRequest request = new GroupRequest("updated", "Updated description");

        // When & Then
        mockMvc.perform(put("/api/v1/groups/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteGroup_shouldRemoveGroup_whenGroupExists() throws Exception {
        // Given
        String created = mockMvc.perform(post("/api/v1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest("to-delete", "Will be deleted"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = objectMapper.readTree(created).get("id").asText();

        // When
        mockMvc.perform(delete("/api/v1/groups/" + id))
                .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/api/v1/groups/" + id))
                .andExpect(status().isNotFound());
    }
}
