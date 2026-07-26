package com.laulem.vectopath.referential.api.infra.service;

import com.laulem.vectopath.referential.api.business.service.AuthenticationUseCase;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SecurityContextAuthenticationService implements AuthenticationUseCase {

    @Override
    public String getCurrentUser() {
        return findCurrentUser().orElse(DEFAULT_UNKNOWN_USERNAME);
    }

    @Override
    public Optional<String> findCurrentUser() {
        return getCurrentJwt().map(jwt -> jwt.getClaimAsString("preferred_username")); // TODO : Use sub
    }

    @Override
    public Optional<String> findCurrentUserFirstName() {
        return getCurrentJwt().map(jwt -> jwt.getClaimAsString("given_name"));
    }

    @Override
    public Optional<String> findCurrentUserLastName() {
        return getCurrentJwt().map(jwt -> jwt.getClaimAsString("family_name"));
    }

    @Override
    public List<String> getAuthorities() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(auth -> auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .orElse(Collections.emptyList());
    }

    @Override
    public Optional<String> getToken() {
        return getCurrentJwt().map(Jwt::getTokenValue);
    }

    private Optional<Jwt> getCurrentJwt() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(Jwt.class::isInstance)
                .map(Jwt.class::cast);
    }
}
