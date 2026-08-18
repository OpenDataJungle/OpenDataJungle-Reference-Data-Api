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
class GroupControllerSecurityIT {

    private static final String GROUPS_PATH = "/api/v1/groups";
    private static final String SEEDED_GROUP_ID = "00000000-0000-0000-0000-000000000001";
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

    // --- getAllGroups ---

    @Test
    void getAllGroups_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(get(GROUPS_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllGroups_shouldReturn403_whenMissingReadScope() throws Exception {
        mockMvc.perform(withScopes(get(GROUPS_PATH), WRITE_SCOPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllGroups_shouldReturn200_whenReadScopePresent() throws Exception {
        mockMvc.perform(withScopes(get(GROUPS_PATH), READ_SCOPE))
                .andExpect(status().isOk());
    }

    // --- getGroupById ---

    @Test
    void getGroupById_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(get(GROUPS_PATH + "/" + SEEDED_GROUP_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getGroupById_shouldReturn403_whenMissingReadScope() throws Exception {
        mockMvc.perform(withScopes(get(GROUPS_PATH + "/" + SEEDED_GROUP_ID), WRITE_SCOPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void getGroupById_shouldReturn200_whenReadScopePresent() throws Exception {
        mockMvc.perform(withScopes(get(GROUPS_PATH + "/" + SEEDED_GROUP_ID), READ_SCOPE))
                .andExpect(status().isOk());
    }

    // --- createGroup ---

    @Test
    void createGroup_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(post(GROUPS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest("engineering", "Engineering group"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createGroup_shouldReturn403_whenMissingWriteScope() throws Exception {
        mockMvc.perform(withScopes(post(GROUPS_PATH), READ_SCOPE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest("engineering", "Engineering group"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void createGroup_shouldReturn201_whenWriteScopePresent() throws Exception {
        mockMvc.perform(withScopes(post(GROUPS_PATH), WRITE_SCOPE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest("engineering", "Engineering group"))))
                .andExpect(status().isCreated());
    }

    // --- updateGroup ---

    @Test
    void updateGroup_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(put(GROUPS_PATH + "/" + SEEDED_GROUP_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest("updated", "Updated"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateGroup_shouldReturn403_whenMissingWriteScope() throws Exception {
        mockMvc.perform(withScopes(put(GROUPS_PATH + "/" + SEEDED_GROUP_ID), READ_SCOPE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest("updated", "Updated"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateGroup_shouldPassAuthorizationAndReachBusinessLogic_whenWriteScopePresent() throws Exception {
        // Authorization passes (no 403); request reaches GroupService, which reports 404 for an unknown id
        mockMvc.perform(withScopes(put(GROUPS_PATH + "/" + UNKNOWN_ID), WRITE_SCOPE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest("updated", "Updated"))))
                .andExpect(status().isNotFound());
    }

    // --- deleteGroup ---

    @Test
    void deleteGroup_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(delete(GROUPS_PATH + "/" + SEEDED_GROUP_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteGroup_shouldReturn403_whenJwtOnlyHasReadScope() throws Exception {
        mockMvc.perform(withScopes(delete(GROUPS_PATH + "/" + SEEDED_GROUP_ID), READ_SCOPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteGroup_shouldReturn403_whenJwtOnlyHasWriteScope() throws Exception {
        mockMvc.perform(withScopes(delete(GROUPS_PATH + "/" + SEEDED_GROUP_ID), WRITE_SCOPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteGroup_shouldPassAuthorizationAndReachBusinessLogic_whenDeleteScopePresent() throws Exception {
        // Authorization passes (no 403); request reaches GroupService, which reports 404 for an unknown id
        mockMvc.perform(withScopes(delete(GROUPS_PATH + "/" + UNKNOWN_ID), DELETE_SCOPE))
                .andExpect(status().isNotFound());
    }
}
