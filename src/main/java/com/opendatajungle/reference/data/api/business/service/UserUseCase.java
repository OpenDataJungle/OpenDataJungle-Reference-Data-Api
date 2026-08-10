package com.opendatajungle.reference.data.api.business.service;

import com.opendatajungle.reference.data.api.business.model.User;
import com.opendatajungle.reference.data.api.shared.PageResult;

import java.util.UUID;

public interface UserUseCase {
    PageResult<User> findAll(int page, int size, String username);

    User getById(UUID id);

    User create(User user);

    User update(UUID id, User user);

    void delete(UUID id);

    User getByUsername(String username);

    User getOrCreateCurrentUser();
}
