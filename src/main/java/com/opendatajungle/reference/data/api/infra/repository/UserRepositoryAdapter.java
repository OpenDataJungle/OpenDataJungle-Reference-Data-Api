package com.opendatajungle.reference.data.api.infra.repository;

import com.opendatajungle.reference.data.api.business.model.User;
import com.opendatajungle.reference.data.api.business.repository.UserRepository;
import com.opendatajungle.reference.data.api.infra.entity.UserEntity;
import com.opendatajungle.reference.data.api.shared.PageResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class UserRepositoryAdapter implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<User> findAll(int page, int size, String username) {
        return findAllByCriteria(page, size, username);
    }

    private PageResult<User> findAllByCriteria(int page, int size, String username) {
        Specification<UserEntity> spec = (root, _, cb) -> {
            if (username != null && !username.isBlank()) {
                return cb.equal(root.get("username"), username);
            }
            return null;
        };

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("username").ascending());
        Page<UserEntity> userPage = userJpaRepository.findAll(spec, pageable);

        return PageResult.<User>builder()
                .content(userPage.getContent().stream()
                        .map(this::toBusinessModel)
                        .toList())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .currentPage(userPage.getNumber() + 1)
                .pageSize(userPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id).map(this::toBusinessModel);
    }

    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity;
        if (user.id() != null) {
            entity = userJpaRepository.findById(user.id()).orElseThrow();
            entity.setFirstName(user.firstName());
            entity.setLastName(user.lastName());
            entity.setUsername(user.username());
        } else {
            entity = toEntity(user);
            entity.setId(UUID.randomUUID());
        }
        return toBusinessModel(userJpaRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return userJpaRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsernameAndIdNot(String username, UUID id) {
        return userJpaRepository.existsByUsernameAndIdNot(username, id);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        userJpaRepository.deleteById(id);
    }

    private User toBusinessModel(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .username(entity.getUsername())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.id())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .username(user.username())
                .createdAt(user.createdAt())
                .updatedAt(user.updatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(this::toBusinessModel);
    }
}
