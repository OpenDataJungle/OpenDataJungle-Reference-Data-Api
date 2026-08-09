package com.opendatajungle.referential.api.business.repository;

import com.opendatajungle.referential.api.business.model.Group;
import com.opendatajungle.referential.api.shared.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface GroupRepository {
    PageResult<Group> findAll(int page, int size, String name);

    Optional<Group> findById(UUID id);

    Group save(Group group);

    boolean existsById(UUID id);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);

    void deleteById(UUID id);
}
