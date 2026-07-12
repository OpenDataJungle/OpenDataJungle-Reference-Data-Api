package com.laulem.vectopath.referential.api.infra.repository;

import com.laulem.vectopath.referential.api.infra.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FolderRepository extends JpaRepository<FolderEntity, UUID> {
}
