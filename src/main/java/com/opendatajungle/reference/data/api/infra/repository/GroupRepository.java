package com.opendatajungle.reference.data.api.infra.repository;

import com.opendatajungle.reference.data.api.infra.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, UUID> {
}
