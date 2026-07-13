package com.laulem.vectopath.referential.api.infra.repository;

import com.laulem.vectopath.referential.api.infra.entity.GroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupJpaRepository extends JpaRepository<GroupEntity, UUID>, JpaSpecificationExecutor<GroupEntity> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);

    Page<GroupEntity> findAllByUsersId(UUID userId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM GroupEntity g JOIN g.users u WHERE g.id = :groupId AND u.id = :userId")
    boolean isUserInGroup(@Param("groupId") UUID groupId, @Param("userId") UUID userId);
}
