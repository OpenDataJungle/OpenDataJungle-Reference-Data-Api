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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("security-it")
@Transactional
@Import(TestcontainersConfiguration.class)
class UserControllerSecurityIT {

    private static final String USERS_PATH = "/api/v1/users";
    private static final String SEEDED_USER_ID = "00000000-0000-0000-0000-000000000001";
    private static final String SEEDED_USERNAME = "anonymous";
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

    // --- getAllUsers ---

    @Test
    void getAllUsers_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(get(USERS_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllUsers_shouldReturn403_whenMissingReadScope() throws Exception {
        mockMvc.perform(withScopes(get(USERS_PATH), WRITE_SCOPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_shouldReturn200_whenReadScopePresent() throws Exception {
        mockMvc.perform(withScopes(get(USERS_PATH), READ_SCOPE))
                .andExpect(status().isOk());
    }

    // --- getUserById ---

    @Test
    void getUserById_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(get(USERS_PATH + "/" + SEEDED_USER_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserById_shouldReturn403_whenMissingReadScope() throws Exception {
        mockMvc.perform(withScopes(get(USERS_PATH + "/" + SEEDED_USER_ID), WRITE_SCOPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserById_shouldReturn200_whenReadScopePresent() throws Exception {
        mockMvc.perform(withScopes(get(USERS_PATH + "/" + SEEDED_USER_ID), READ_SCOPE))
                .andExpect(status().isOk());
    }

    // --- getUserByUsername ---

    @Test
    void getUserByUsername_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(get(USERS_PATH + "/username/" + SEEDED_USERNAME))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserByUsername_shouldReturn403_whenMissingReadScope() throws Exception {
        mockMvc.perform(withScopes(get(USERS_PATH + "/username/" + SEEDED_USERNAME), WRITE_SCOPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserByUsername_shouldReturn200_whenReadScopePresent() throws Exception {
        mockMvc.perform(withScopes(get(USERS_PATH + "/username/" + SEEDED_USERNAME), READ_SCOPE))
                .andExpect(status().isOk());
    }

    // --- getOrCreateMe ---

    @Test
    void getOrCreateMe_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(post(USERS_PATH + "/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getOrCreateMe_shouldReturn403_whenMissingReadScope() throws Exception {
        mockMvc.perform(withScopes(post(USERS_PATH + "/me"), WRITE_SCOPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void getOrCreateMe_shouldCreateUser_whenCurrentJwtUserNotYetRegistered() throws Exception {
        // getOrCreateMe reads "preferred_username"/"given_name"/"family_name" JWT claims via
        // SecurityContextHolder, which is only populated under the real SecurityConfiguration.
        mockMvc.perform(post(USERS_PATH + "/me")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority(READ_SCOPE))
                                .jwt(jwtBuilder -> jwtBuilder
                                        .claim("preferred_username", "newuser")
                                        .claim("given_name", "New")
                                        .claim("family_name", "User"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.lastName").value("User"));
    }

    @Test
    void getOrCreateMe_shouldReturnExistingUser_whenCurrentJwtUserAlreadyRegistered() throws Exception {
        mockMvc.perform(post(USERS_PATH + "/me")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority(READ_SCOPE))
                                .jwt(jwtBuilder -> jwtBuilder
                                        .claim("preferred_username", SEEDED_USERNAME)
                                        .claim("given_name", "Anonymous")
                                        .claim("family_name", "User"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SEEDED_USER_ID))
                .andExpect(jsonPath("$.username").value(SEEDED_USERNAME));
    }

    // --- createUser ---

    @Test
    void createUser_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(post(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("New", "User", "newuser"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createUser_shouldReturn403_whenMissingWriteScope() throws Exception {
        mockMvc.perform(withScopes(post(USERS_PATH), READ_SCOPE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("New", "User", "newuser"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void createUser_shouldReturn201_whenWriteScopePresent() throws Exception {
        mockMvc.perform(withScopes(post(USERS_PATH), WRITE_SCOPE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("New", "User", "newuser"))))
                .andExpect(status().isCreated());
    }

    // --- updateUser ---

    @Test
    void updateUser_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(put(USERS_PATH + "/" + SEEDED_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Updated", "User", "updateduser"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUser_shouldReturn403_whenMissingWriteScope() throws Exception {
        mockMvc.perform(withScopes(put(USERS_PATH + "/" + SEEDED_USER_ID), READ_SCOPE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Updated", "User", "updateduser"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUser_shouldPassAuthorizationAndReachBusinessLogic_whenWriteScopePresent() throws Exception {
        // Authorization passes (no 403); request reaches UserService, which reports 404 for an unknown id
        mockMvc.perform(withScopes(put(USERS_PATH + "/" + UNKNOWN_ID), WRITE_SCOPE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Updated", "User", "updateduser"))))
                .andExpect(status().isNotFound());
    }

    // --- deleteUser ---

    @Test
    void deleteUser_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(delete(USERS_PATH + "/" + SEEDED_USER_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUser_shouldReturn403_whenJwtOnlyHasReadScope() throws Exception {
        mockMvc.perform(withScopes(delete(USERS_PATH + "/" + SEEDED_USER_ID), READ_SCOPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUser_shouldReturn403_whenJwtOnlyHasWriteScope() throws Exception {
        mockMvc.perform(withScopes(delete(USERS_PATH + "/" + SEEDED_USER_ID), WRITE_SCOPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUser_shouldPassAuthorizationAndReachBusinessLogic_whenDeleteScopePresent() throws Exception {
        // Authorization passes (no 403); request reaches UserService, which reports 404 for an unknown id
        mockMvc.perform(withScopes(delete(USERS_PATH + "/" + UNKNOWN_ID), DELETE_SCOPE))
                .andExpect(status().isNotFound());
    }
}
