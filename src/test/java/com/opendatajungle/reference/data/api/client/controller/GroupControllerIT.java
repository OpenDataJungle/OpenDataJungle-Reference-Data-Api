package com.opendatajungle.reference.data.api.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opendatajungle.reference.data.api.client.dto.GroupRequest;
import com.opendatajungle.reference.data.api.testconfig.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
@Transactional
@Import(TestcontainersConfiguration.class)
class GroupControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static RequestPostProcessor authenticated() {
        return request -> {
            Jwt jwt = Jwt.withTokenValue("token")
                    .header("alg", "none")
                    .claim("sub", "tester")
                    .claim("iss", "https://system.internal.localhost")
                    .claim("preferred_username", "tester")
                    .claim("given_name", "Test")
                    .claim("family_name", "User")
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(3600))
                    .build();
            SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, List.of()));
            return request;
        };
    }

    @Test
    void createGroup_shouldPersistAndReturnCreatedGroup_whenNameIsUnique() throws Exception {
        // Given
        GroupRequest request = new GroupRequest("engineering", "Engineering group");

        // When & Then
        mockMvc.perform(post("/api/v1/groups")
                        .with(authenticated())
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
                        .with(authenticated())
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
                        .with(authenticated())
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
                        .with(authenticated())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest("to-update", "Before update"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = objectMapper.readTree(location).get("id").asText();

        // When & Then
        mockMvc.perform(put("/api/v1/groups/" + id)
                        .with(authenticated())
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
                        .with(authenticated())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteGroup_shouldRemoveGroup_whenGroupExists() throws Exception {
        // Given
        String created = mockMvc.perform(post("/api/v1/groups")
                        .with(authenticated())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest("to-delete", "Will be deleted"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = objectMapper.readTree(created).get("id").asText();

        // When
        mockMvc.perform(delete("/api/v1/groups/" + id)
                        .with(authenticated()))
                .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/api/v1/groups/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createGroup_shouldStoreLowerCasedName_andRejectCaseVariants() throws Exception {
        // When & Then: stored in lower case
        mockMvc.perform(post("/api/v1/groups")
                        .with(authenticated())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest("Data_Team.Eu-West", "Data team"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("data_team.eu-west"));

        // When & Then: a case variant is a duplicate
        mockMvc.perform(post("/api/v1/groups")
                        .with(authenticated())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest("DATA_TEAM.EU-WEST", "Duplicate"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("GROUP_NAME_ALREADY_EXISTS"));

        // When & Then: search by any case variant
        mockMvc.perform(get("/api/v1/groups").param("name", "Data_Team.EU-West"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].name").value("data_team.eu-west"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Équipe", "data team", "team2", "team@corp", " root"})
    void createGroup_shouldReturnBadRequest_whenNameHasForbiddenCharacters(String name) throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/groups")
                        .with(authenticated())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest(name, "Invalid"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.field").value("name"))
                .andExpect(jsonPath("$.message").value("Group name must contain only letters a-z, A-Z and the characters '-', '_', '.'"));
    }

    @Test
    void createGroup_shouldGrantAdminToCreator_whenTokenHasNoGivenOrFamilyName() throws Exception {
        // Given: typical client_credentials (service account) token
        RequestPostProcessor serviceAccount = request -> {
            Jwt jwt = Jwt.withTokenValue("token")
                    .header("alg", "none")
                    .claim("sub", "service-account-id")
                    .claim("iss", "https://system.internal.localhost")
                    .claim("preferred_username", "service-account-knowledge-api")
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(3600))
                    .build();
            SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, List.of()));
            return request;
        };

        // When
        String created = mockMvc.perform(post("/api/v1/groups")
                        .with(serviceAccount)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest("service-group", "Created by a service"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = objectMapper.readTree(created).get("id").asText();

        // Then: the service account is the admin of the new group
        mockMvc.perform(get("/api/v1/groups/" + id + "/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].user.username").value("service-account-knowledge-api"))
                .andExpect(jsonPath("$.content[0].permission.isAdmin").value(true));
    }

    @Test
    void createGroup_shouldAccept255CharactersAndReject256_forNameAndDescription() throws Exception {
        // Given
        String max = "a".repeat(255);
        String tooLong = "b".repeat(256);

        // When & Then
        mockMvc.perform(post("/api/v1/groups")
                        .with(authenticated())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest(max, max))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/groups")
                        .with(authenticated())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest(tooLong, "ok"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.field").value("name"))
                .andExpect(jsonPath("$.message").value("Group name must not exceed 255 characters"));

        mockMvc.perform(post("/api/v1/groups")
                        .with(authenticated())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest("short", tooLong))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("description"))
                .andExpect(jsonPath("$.message").value("Group description must not exceed 255 characters"));
        ;
    }
}
