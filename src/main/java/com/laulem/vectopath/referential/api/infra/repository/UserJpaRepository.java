package com.laulem.vectopath.referential.api.infra.repository;

import com.laulem.vectopath.referential.api.infra.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, UUID id);

    Page<UserEntity> findAllByGroupsId(UUID groupId, Pageable pageable);

    @Modifying
    @Query(value = "DELETE FROM referential.group_users WHERE group_id = :groupId AND user_id = :userId", nativeQuery = true)
    void removeUserFromGroup(@Param("groupId") UUID groupId, @Param("userId") UUID userId);

    @Modifying
    @Query(value = "INSERT INTO referential.group_users (group_id, user_id) VALUES (:groupId, :userId)", nativeQuery = true)
    void addUserToGroup(@Param("groupId") UUID groupId, @Param("userId") UUID userId);
}
