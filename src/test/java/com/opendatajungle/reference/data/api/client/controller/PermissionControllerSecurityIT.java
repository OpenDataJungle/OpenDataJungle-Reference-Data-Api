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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("security-it")
@Transactional
@Import(TestcontainersConfiguration.class)
class PermissionControllerSecurityIT {

    private static final String PERMISSIONS_PATH = "/api/v1/permissions";
    private static final String SEEDED_PERMISSION_ID = "00000000-0000-0000-0000-000000000001";
    private static final String UNKNOWN_ID = "11111111-1111-1111-1111-111111111111";
    private static final String READ_SCOPE = "referencedata.read";
    private static final String WRITE_SCOPE = "referencedata.write";
    private static final String DELETE_SCOPE = "referencedata.delete";

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    private PermissionRequest samplePermission(String name) {
        return new PermissionRequest(name, "A sample permission", true, false, false);
    }

    // --- getAllPermissions ---

    @Test
    void getAllPermissions_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(get(PERMISSIONS_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllPermissions_shouldReturn403_whenMissingReadScope() throws Exception {
        mockMvc.perform(withScopes(get(PERMISSIONS_PATH), WRITE_SCOPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllPermissions_shouldReturn200_whenReadScopePresent() throws Exception {
        mockMvc.perform(withScopes(get(PERMISSIONS_PATH), READ_SCOPE))
                .andExpect(status().isOk());
    }

    // --- getPermissionById ---

    @Test
    void getPermissionById_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(get(PERMISSIONS_PATH + "/" + SEEDED_PERMISSION_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getPermissionById_shouldReturn403_whenMissingReadScope() throws Exception {
        mockMvc.perform(withScopes(get(PERMISSIONS_PATH + "/" + SEEDED_PERMISSION_ID), WRITE_SCOPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPermissionById_shouldReturn200_whenReadScopePresent() throws Exception {
        mockMvc.perform(withScopes(get(PERMISSIONS_PATH + "/" + SEEDED_PERMISSION_ID), READ_SCOPE))
                .andExpect(status().isOk());
    }

    // --- createPermission ---

    @Test
    void createPermission_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(post(PERMISSIONS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(samplePermission("read_only"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createPermission_shouldReturn403_whenMissingWriteScope() throws Exception {
        mockMvc.perform(withScopes(post(PERMISSIONS_PATH), READ_SCOPE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(samplePermission("read_only"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void createPermission_shouldReturn201_whenWriteScopePresent() throws Exception {
        mockMvc.perform(withScopes(post(PERMISSIONS_PATH), WRITE_SCOPE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(samplePermission("read_only"))))
                .andExpect(status().isCreated());
    }

    // --- updatePermission ---

    @Test
    void updatePermission_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(put(PERMISSIONS_PATH + "/" + SEEDED_PERMISSION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(samplePermission("updated_permission"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updatePermission_shouldReturn403_whenMissingWriteScope() throws Exception {
        mockMvc.perform(withScopes(put(PERMISSIONS_PATH + "/" + SEEDED_PERMISSION_ID), READ_SCOPE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(samplePermission("updated_permission"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void updatePermission_shouldPassAuthorizationAndReachBusinessLogic_whenWriteScopePresent() throws Exception {
        // Authorization passes (no 403); request reaches PermissionService, which reports 404 for an unknown id
        mockMvc.perform(withScopes(put(PERMISSIONS_PATH + "/" + UNKNOWN_ID), WRITE_SCOPE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(samplePermission("updated_permission"))))
                .andExpect(status().isNotFound());
    }

    // --- deletePermission ---

    @Test
    void deletePermission_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(delete(PERMISSIONS_PATH + "/" + SEEDED_PERMISSION_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deletePermission_shouldReturn403_whenJwtOnlyHasReadScope() throws Exception {
        mockMvc.perform(withScopes(delete(PERMISSIONS_PATH + "/" + SEEDED_PERMISSION_ID), READ_SCOPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePermission_shouldReturn403_whenJwtOnlyHasWriteScope() throws Exception {
        mockMvc.perform(withScopes(delete(PERMISSIONS_PATH + "/" + SEEDED_PERMISSION_ID), WRITE_SCOPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePermission_shouldPassAuthorizationAndReachBusinessLogic_whenDeleteScopePresent() throws Exception {
        // Authorization passes (no 403); request reaches PermissionService, which reports 404 for an unknown id
        mockMvc.perform(withScopes(delete(PERMISSIONS_PATH + "/" + UNKNOWN_ID), DELETE_SCOPE))
                .andExpect(status().isNotFound());
    }
}
