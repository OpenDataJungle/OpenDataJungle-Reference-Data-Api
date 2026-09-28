package com.opendatajungle.reference.data.api.infra.repository;

import com.opendatajungle.reference.data.api.infra.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {

    boolean existsByAuthIdAndAuthIss(String authId, String authIss);

    Optional<UserEntity> findByAuthIdAndAuthIss(String authId, String authIss);
}
