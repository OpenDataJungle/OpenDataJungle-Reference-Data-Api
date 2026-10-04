package com.opendatajungle.reference.data.api.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opendatajungle.reference.data.api.client.dto.GroupRequest;
import com.opendatajungle.reference.data.api.client.dto.PermissionRequest;
import com.opendatajungle.reference.data.api.client.dto.UserRequest;
import com.opendatajungle.reference.data.api.testconfig.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
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

    private static RequestPostProcessor authenticated() {
        return request -> {
            Jwt jwt = Jwt.withTokenValue("token")
                    .header("alg", "none")
                    .claim("sub", "anonymous")
                    .claim("iss", "https://system.internal.localhost")
                    .claim("preferred_username", "anonymous")
                    .claim("given_name", "Anonymous")
                    .claim("family_name", "User")
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(3600))
                    .build();
            SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, List.of()));
            return request;
        };
    }

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
        mockMvc.perform(post("/api/v1/groups/" + groupId + "/users/" + userId + "/permissions/" + ROOT_PERMISSION_ID)
                        .with(authenticated()))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v1/groups/" + groupId + "/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[*].user.username", hasItem("grace")));
    }

    @Test
    void addUserToGroup_shouldReturnBadRequest_whenUserAlreadyInGroup() throws Exception {
        // When & Then: the seeded root group already has the anonymous user as a member
        mockMvc.perform(post("/api/v1/groups/" + ROOT_GROUP_ID + "/users/" + ROOT_USER_ID + "/permissions/" + ROOT_PERMISSION_ID)
                        .with(authenticated()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("USER_ALREADY_IN_GROUP"));
    }

    @Test
    void addUserToGroup_shouldReturnNotFound_whenGroupDoesNotExist() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/groups/" + UUID.randomUUID() + "/users/" + ROOT_USER_ID + "/permissions/" + ROOT_PERMISSION_ID)
                        .with(authenticated()))
                .andExpect(status().isNotFound());
    }

    @Test
    void addUserToGroup_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/groups/" + ROOT_GROUP_ID + "/users/" + UUID.randomUUID() + "/permissions/" + ROOT_PERMISSION_ID)
                        .with(authenticated()))
                .andExpect(status().isNotFound());
    }

    @Test
    void addUserToGroup_shouldReturnNotFound_whenPermissionDoesNotExist() throws Exception {
        // Given
        String groupId = createGroup("engineering", "Engineering group");
        String userId = createUser("Grace", "Hopper", "grace");

        // When & Then
        mockMvc.perform(post("/api/v1/groups/" + groupId + "/users/" + userId + "/permissions/" + UUID.randomUUID())
                        .with(authenticated()))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeUserFromGroup_shouldDeleteMembership_whenUserIsMember() throws Exception {
        // Given
        String groupId = createGroup("engineering", "Engineering group");
        String userId = createUser("Grace", "Hopper", "grace");
        mockMvc.perform(post("/api/v1/groups/" + groupId + "/users/" + userId + "/permissions/" + ROOT_PERMISSION_ID)
                        .with(authenticated()))
                .andExpect(status().isCreated());

        // When
        mockMvc.perform(delete("/api/v1/groups/" + groupId + "/users/" + userId)
                        .with(authenticated()))
                .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/api/v1/groups/" + groupId + "/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].user.username").value("anonymous"));
    }

    @Test
    void removeUserFromGroup_shouldReturnBadRequest_whenUserNotMember() throws Exception {
        // Given
        String groupId = createGroup("engineering", "Engineering group");
        String userId = createUser("Grace", "Hopper", "grace");

        // When & Then
        mockMvc.perform(delete("/api/v1/groups/" + groupId + "/users/" + userId)
                        .with(authenticated()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("USER_NOT_IN_GROUP"));
    }

    @Test
    void removeUserFromGroup_shouldLeaveGroupWithoutAdmin_whenLastAdminLeaves() throws Exception {
        // Given: the anonymous user created the group, so it is its only admin
        String groupId = createGroup("engineering", "Engineering group");

        // When
        mockMvc.perform(delete("/api/v1/groups/" + groupId + "/users/" + ROOT_USER_ID)
                        .with(authenticated()))
                .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/api/v1/groups/" + groupId + "/users"))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void forceGroupAdmin_shouldRestoreAnAdmin_whenGroupHasNoAdminLeft() throws Exception {
        // Given: the creator leaves, so the group has no admin
        String groupId = createGroup("orphan", "Orphan group");
        mockMvc.perform(delete("/api/v1/groups/" + groupId + "/users/" + ROOT_USER_ID).with(authenticated()))
                .andExpect(status().isOk());
        String userId = createUser("Grace", "Hopper", "grace");

        // When
        mockMvc.perform(put(forceGroupAdminPath(groupId, userId)))
                .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/api/v1/groups/" + groupId + "/users"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].user.username").value("grace"))
                .andExpect(jsonPath("$.content[0].permission.name").value("DEFAULT_ADMIN_PERMISSION"));
    }

    @Test
    void forceGroupAdmin_shouldUpgradePermission_whenUserIsAlreadyMember() throws Exception {
        // Given
        String groupId = createGroup("engineering", "Engineering group");
        String userId = createUser("Grace", "Hopper", "grace");
        String readOnlyPermissionId = createReadOnlyPermission();
        mockMvc.perform(post("/api/v1/groups/" + groupId + "/users/" + userId + "/permissions/" + readOnlyPermissionId)
                        .with(authenticated()))
                .andExpect(status().isCreated());

        // When
        mockMvc.perform(put(forceGroupAdminPath(groupId, userId)))
                .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/api/v1/users/" + userId + "/groups"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].permission.name").value("DEFAULT_ADMIN_PERMISSION"))
                .andExpect(jsonPath("$.content[0].permission.isAdmin").value(true));
    }

    @Test
    void forceGroupAdmin_shouldBeIdempotent_whenCalledTwice() throws Exception {
        // Given
        String groupId = createGroup("engineering", "Engineering group");
        String userId = createUser("Grace", "Hopper", "grace");

        // When
        mockMvc.perform(put(forceGroupAdminPath(groupId, userId))).andExpect(status().isOk());
        mockMvc.perform(put(forceGroupAdminPath(groupId, userId))).andExpect(status().isOk());

        // Then: the creator and grace
        mockMvc.perform(get("/api/v1/groups/" + groupId + "/users"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void forceGroupAdmin_shouldReturnNotFound_whenGroupDoesNotExist() throws Exception {
        // When & Then
        mockMvc.perform(put(forceGroupAdminPath(UUID.randomUUID().toString(), ROOT_USER_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    void forceGroupAdmin_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        // Given
        String groupId = createGroup("engineering", "Engineering group");

        // When & Then
        mockMvc.perform(put(forceGroupAdminPath(groupId, UUID.randomUUID().toString())))
                .andExpect(status().isNotFound());
    }

    private String forceGroupAdminPath(String groupId, String userId) {
        return "/api/v1/groups/" + groupId + "/users/" + userId + "/force-admin";
    }

    private String createGroup(String name, String description) throws Exception {
        String response = mockMvc.perform(post("/api/v1/groups")
                        .with(authenticated())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest(name, description))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }

    private String createUser(String firstName, String lastName, String username) throws Exception {
        String response = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest(firstName, lastName, username, username + "-auth-id", "https://system.internal.localhost"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }

    private String createReadOnlyPermission() throws Exception {
        String response = mockMvc.perform(post("/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PermissionRequest("read-only", "Read only", true, false, false))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }
}
