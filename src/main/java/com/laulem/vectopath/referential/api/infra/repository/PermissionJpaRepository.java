package com.laulem.vectopath.referential.api.infra.repository;

import com.laulem.vectopath.referential.api.infra.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, UUID> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);
}
