package com.opendatajungle.reference.data.api.business.repository;

import com.opendatajungle.reference.data.api.business.model.User;
import com.opendatajungle.reference.data.api.shared.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    PageResult<User> findAll(int page, int size, String username);

    Optional<User> findById(UUID id);

    User save(User user);

    boolean existsById(UUID id);

    boolean existsByAuthIdAndAuthIss(String authId, String authIss);

    void deleteById(UUID id);

    Optional<User> findByAuthIdAndAuthIss(String authId, String authIss);
}
