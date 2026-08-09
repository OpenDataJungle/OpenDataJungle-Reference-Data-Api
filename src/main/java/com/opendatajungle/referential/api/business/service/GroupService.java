package com.opendatajungle.referential.api.business.service;

import com.opendatajungle.referential.api.business.exception.NotFoundException;
import com.opendatajungle.referential.api.business.exception.ParamException;
import com.opendatajungle.referential.api.business.model.Group;
import com.opendatajungle.referential.api.business.repository.GroupRepository;
import com.opendatajungle.referential.api.shared.PageResult;

import java.util.UUID;

public class GroupService implements GroupUseCase {

    private static final String GROUP = "Group";

    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public PageResult<Group> findAll(int page, int size, String name) {
        return groupRepository.findAll(page, size, name);
    }

    @Override
    public Group getById(UUID id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(GROUP, id.toString()));
    }

    @Override
    public Group create(Group group) {
        if (groupRepository.existsByName(group.name())) {
            throw new ParamException(
                    "GROUP_NAME_ALREADY_EXISTS",
                    "A group with name '" + group.name() + "' already exists",
                    "name"
            );
        }
        return groupRepository.save(group);
    }

    @Override
    public Group update(UUID id, Group group) {
        if (!groupRepository.existsById(id)) {
            throw new NotFoundException(GROUP, id.toString());
        }
        if (groupRepository.existsByNameAndIdNot(group.name(), id)) {
            throw new ParamException(
                    "GROUP_NAME_ALREADY_EXISTS",
                    "A group with name '" + group.name() + "' already exists",
                    "name"
            );
        }
        Group toUpdate = Group.builder()
                .id(id)
                .name(group.name())
                .description(group.description())
                .build();
        return groupRepository.save(toUpdate);
    }

    @Override
    public void delete(UUID id) {
        if (!groupRepository.existsById(id)) {
            throw new NotFoundException(GROUP, id.toString());
        }
        groupRepository.deleteById(id);
    }
}
