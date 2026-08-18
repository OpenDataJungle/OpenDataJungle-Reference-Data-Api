package com.opendatajungle.reference.data.api.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opendatajungle.reference.data.api.client.dto.GroupRequest;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
@Transactional
@Import(TestcontainersConfiguration.class)
class GroupUserControllerIT {

    private static final String ROOT_GROUP_ID = "00000000-0000-0000-0000-000000000001";
    private static final String ROOT_USER_ID = "00000000-0000-0000-0000-000000000001";
    private static final String ROOT_PERMISSION_ID = "00000000-0000-0000-0000-000000000001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getGroupsByUserId_shouldReturnSeededRootGroup_whenCalledForSeededUser() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users/" + ROOT_USER_ID + "/groups").param("page", "1").param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].group.name").value("root"));
    }

    @Test
    void getUsersByGroupId_shouldReturnSeededAnonymousUser_whenCalledForSeededGroup() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/groups/" + ROOT_GROUP_ID + "/users").param("page", "1").param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].user.username").value("anonymous"));
    }

    @Test
    void addUserToGroup_shouldCreateMembership_whenGroupUserAndPermissionExist() throws Exception {
        // Given
        String groupId = createGroup("engineering", "Engineering group");
        String userId = createUser("Grace", "Hopper", "grace");

        // When & Then
        mockMvc.perform(post("/api/v1/groups/" + groupId + "/users/" + userId + "/permissions/" + ROOT_PERMISSION_ID))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v1/groups/" + groupId + "/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].user.username").value("grace"));
    }

    @Test
    void addUserToGroup_shouldReturnBadRequest_whenUserAlreadyInGroup() throws Exception {
        // When & Then: the seeded root group already has the anonymous user as a member
        mockMvc.perform(post("/api/v1/groups/" + ROOT_GROUP_ID + "/users/" + ROOT_USER_ID + "/permissions/" + ROOT_PERMISSION_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("USER_ALREADY_IN_GROUP"));
    }

    @Test
    void addUserToGroup_shouldReturnNotFound_whenGroupDoesNotExist() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/groups/" + UUID.randomUUID() + "/users/" + ROOT_USER_ID + "/permissions/" + ROOT_PERMISSION_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void addUserToGroup_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/groups/" + ROOT_GROUP_ID + "/users/" + UUID.randomUUID() + "/permissions/" + ROOT_PERMISSION_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void addUserToGroup_shouldReturnNotFound_whenPermissionDoesNotExist() throws Exception {
        // Given
        String groupId = createGroup("engineering", "Engineering group");
        String userId = createUser("Grace", "Hopper", "grace");

        // When & Then
        mockMvc.perform(post("/api/v1/groups/" + groupId + "/users/" + userId + "/permissions/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeUserFromGroup_shouldDeleteMembership_whenUserIsMember() throws Exception {
        // Given
        String groupId = createGroup("engineering", "Engineering group");
        String userId = createUser("Grace", "Hopper", "grace");
        mockMvc.perform(post("/api/v1/groups/" + groupId + "/users/" + userId + "/permissions/" + ROOT_PERMISSION_ID))
                .andExpect(status().isCreated());

        // When
        mockMvc.perform(delete("/api/v1/groups/" + groupId + "/users/" + userId))
                .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/api/v1/groups/" + groupId + "/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void removeUserFromGroup_shouldReturnBadRequest_whenUserNotMember() throws Exception {
        // Given
        String groupId = createGroup("engineering", "Engineering group");

        // When & Then
        mockMvc.perform(delete("/api/v1/groups/" + groupId + "/users/" + ROOT_USER_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("USER_NOT_IN_GROUP"));
    }

    private String createGroup(String name, String description) throws Exception {
        String response = mockMvc.perform(post("/api/v1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest(name, description))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }

    private String createUser(String firstName, String lastName, String username) throws Exception {
        String response = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest(firstName, lastName, username))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }
}
