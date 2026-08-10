package com.opendatajungle.reference.data.api.business.service;

import com.opendatajungle.reference.data.api.business.model.Group;
import com.opendatajungle.reference.data.api.shared.PageResult;

import java.util.UUID;

public interface GroupUseCase {
    PageResult<Group> findAll(int page, int size, String name);

    Group getById(UUID id);

    Group create(Group group);

    Group update(UUID id, Group group);

    void delete(UUID id);
}
