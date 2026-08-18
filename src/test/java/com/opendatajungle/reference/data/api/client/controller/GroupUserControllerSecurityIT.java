package com.opendatajungle.reference.data.api.client.controller;

import com.opendatajungle.reference.data.api.testconfig.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("security-it")
@Transactional
@Import(TestcontainersConfiguration.class)
class GroupUserControllerSecurityIT {

    private static final String SEEDED_ID = "00000000-0000-0000-0000-000000000001";
    private static final String UNKNOWN_ID = "11111111-1111-1111-1111-111111111111";
    private static final String READ_SCOPE = "referencedata.read";
    private static final String WRITE_SCOPE = "referencedata.write";
    private static final String DELETE_SCOPE = "referencedata.delete";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private MockHttpServletRequestBuilder withScopes(MockHttpServletRequestBuilder builder, String... scopes) {
        SimpleGrantedAuthority[] authorities = Arrays.stream(scopes)
                .map(SimpleGrantedAuthority::new)
                .toArray(SimpleGrantedAuthority[]::new);
        return builder.with(jwt().authorities(authorities));
    }

    // --- getGroupsByUserId ---

    @Test
    void getGroupsByUserId_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + SEEDED_ID + "/groups"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getGroupsByUserId_shouldReturn403_whenMissingReadScope() throws Exception {
        mockMvc.perform(withScopes(get("/api/v1/users/" + SEEDED_ID + "/groups"), WRITE_SCOPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void getGroupsByUserId_shouldReturn200_whenReadScopePresent() throws Exception {
        mockMvc.perform(withScopes(get("/api/v1/users/" + SEEDED_ID + "/groups"), READ_SCOPE))
                .andExpect(status().isOk());
    }

    // --- getUsersByGroupId ---

    @Test
    void getUsersByGroupId_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/groups/" + SEEDED_ID + "/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUsersByGroupId_shouldReturn403_whenMissingReadScope() throws Exception {
        mockMvc.perform(withScopes(get("/api/v1/groups/" + SEEDED_ID + "/users"), WRITE_SCOPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUsersByGroupId_shouldReturn200_whenReadScopePresent() throws Exception {
        mockMvc.perform(withScopes(get("/api/v1/groups/" + SEEDED_ID + "/users"), READ_SCOPE))
                .andExpect(status().isOk());
    }

    // --- addUserToGroup ---

    @Test
    void addUserToGroup_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(post(addUserToGroupPath(UNKNOWN_ID, UNKNOWN_ID, UNKNOWN_ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addUserToGroup_shouldReturn403_whenMissingWriteScope() throws Exception {
        mockMvc.perform(withScopes(post(addUserToGroupPath(UNKNOWN_ID, UNKNOWN_ID, UNKNOWN_ID)), READ_SCOPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void addUserToGroup_shouldPassAuthorizationAndReachBusinessLogic_whenWriteScopePresent() throws Exception {
        // Authorization passes (no 403); request reaches GroupUserService, which reports 404 for an unknown group id
        mockMvc.perform(withScopes(post(addUserToGroupPath(UNKNOWN_ID, UNKNOWN_ID, UNKNOWN_ID)), WRITE_SCOPE))
                .andExpect(status().isNotFound());
    }

    // --- removeUserFromGroup ---

    @Test
    void removeUserFromGroup_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(delete(removeUserFromGroupPath(SEEDED_ID, SEEDED_ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void removeUserFromGroup_shouldReturn403_whenJwtOnlyHasReadScope() throws Exception {
        mockMvc.perform(withScopes(delete(removeUserFromGroupPath(SEEDED_ID, SEEDED_ID)), READ_SCOPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void removeUserFromGroup_shouldReturn403_whenJwtOnlyHasWriteScope() throws Exception {
        mockMvc.perform(withScopes(delete(removeUserFromGroupPath(SEEDED_ID, SEEDED_ID)), WRITE_SCOPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void removeUserFromGroup_shouldPassAuthorizationAndReachBusinessLogic_whenDeleteScopePresent() throws Exception {
        // Authorization passes (no 403); request reaches GroupUserService, which reports 404 for an unknown group id
        mockMvc.perform(withScopes(delete(removeUserFromGroupPath(UNKNOWN_ID, UNKNOWN_ID)), DELETE_SCOPE))
                .andExpect(status().isNotFound());
    }

    private String addUserToGroupPath(String groupId, String userId, String permissionId) {
        return "/api/v1/groups/" + groupId + "/users/" + userId + "/permissions/" + permissionId;
    }

    private String removeUserFromGroupPath(String groupId, String userId) {
        return "/api/v1/groups/" + groupId + "/users/" + userId;
    }
}
