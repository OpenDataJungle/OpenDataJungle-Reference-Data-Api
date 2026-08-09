package com.opendatajungle.referential.api.business.service;

import java.util.List;
import java.util.Optional;

public interface AuthenticationUseCase {
    String DEFAULT_UNKNOWN_USERNAME = "anonymous";

    String getCurrentUser();

    Optional<String> findCurrentUser();

    Optional<String> findCurrentUserFirstName();

    Optional<String> findCurrentUserLastName();

    List<String> getAuthorities();

    Optional<String> getToken();
}
