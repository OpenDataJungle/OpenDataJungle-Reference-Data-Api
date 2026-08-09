package com.opendatajungle.referential.api.infra.repository;

import com.opendatajungle.referential.api.infra.entity.GroupUserEntity;
import com.opendatajungle.referential.api.infra.entity.GroupUserIdEmbeddable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupUserJpaRepository extends JpaRepository<GroupUserEntity, GroupUserIdEmbeddable> {
    @Query("SELECT gu FROM GroupUserEntity gu JOIN FETCH gu.permission WHERE gu.userId = :userId")
    Page<GroupUserEntity> findByUserIdWithPermission(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(gu) > 0 THEN true ELSE false END FROM GroupUserEntity gu WHERE gu.groupId = :groupId AND gu.userId = :userId")
    boolean isUserInGroup(@Param("groupId") UUID groupId, @Param("userId") UUID userId);


    @Modifying
    @Query(value = "DELETE FROM referential.group_users WHERE group_id = :groupId AND user_id = :userId", nativeQuery = true)
    void removeUserFromGroup(@Param("groupId") UUID groupId, @Param("userId") UUID userId);

    @Modifying
    @Query(value = "INSERT INTO referential.group_users (group_id, user_id, permission_id) VALUES (:groupId, :userId, :permissionId)", nativeQuery = true)
    void addUserToGroup(@Param("groupId") UUID groupId, @Param("userId") UUID userId, @Param("permissionId") UUID permissionId);

    @Query("SELECT gu FROM GroupUserEntity gu JOIN FETCH gu.permission WHERE gu.groupId = :groupId")
    Page<GroupUserEntity> findByGroupIdWithPermission(@Param("groupId") UUID groupId, Pageable pageable);
}
