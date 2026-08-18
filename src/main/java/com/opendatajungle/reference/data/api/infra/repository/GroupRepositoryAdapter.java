package com.opendatajungle.reference.data.api.infra.repository;

import com.opendatajungle.commons.business.exception.NotFoundException;
import com.opendatajungle.commons.business.exception.ParamException;
import com.opendatajungle.commons.util.StringUtils;
import com.opendatajungle.reference.data.api.business.model.Group;
import com.opendatajungle.reference.data.api.business.repository.GroupRepository;
import com.opendatajungle.reference.data.api.infra.entity.GroupEntity;
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
public class GroupRepositoryAdapter implements GroupRepository {
    private final GroupJpaRepository groupJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<Group> findAll(int page, int size, String name) {
        return findAllByCriteria(page, size, name);
    }

    private PageResult<Group> findAllByCriteria(int page, int size, String name) {
        Specification<GroupEntity> spec = (root, _, cb) -> {
            if (!StringUtils.isNullOrBlank(name)) {
                return cb.equal(root.get("name"), name);
            }
            return null;
        };

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("name").ascending());
        Page<GroupEntity> groupPage = groupJpaRepository.findAll(spec, pageable);

        return PageResult.<Group>builder()
                .content(groupPage.getContent().stream()
                        .map(this::toBusinessModel)
                        .toList())
                .totalElements(groupPage.getTotalElements())
                .totalPages(groupPage.getTotalPages())
                .currentPage(groupPage.getNumber() + 1)
                .pageSize(groupPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Group> findById(UUID id) {
        return groupJpaRepository.findById(id).map(this::toBusinessModel);
    }

    @Override
    @Transactional
    public Group save(Group group) {
        GroupEntity entity;
        if (group.id() != null) {
            entity = groupJpaRepository.findById(group.id()).orElseThrow(() -> new NotFoundException("Group", group.id().toString()));
            entity.setName(group.name());
            entity.setDescription(group.description());
        } else {
            entity = toEntity(group);
            entity.setId(UUID.randomUUID());
        }
        return toBusinessModel(groupJpaRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return groupJpaRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return groupJpaRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndIdNot(String name, UUID id) {
        return groupJpaRepository.existsByNameAndIdNot(name, id);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        groupJpaRepository.deleteById(id);
    }

    private Group toBusinessModel(GroupEntity entity) {
        return Group.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private GroupEntity toEntity(Group group) {
        return GroupEntity.builder()
                .id(group.id())
                .name(group.name())
                .description(group.description())
                .createdAt(group.createdAt())
                .updatedAt(group.updatedAt())
                .build();
    }
}
