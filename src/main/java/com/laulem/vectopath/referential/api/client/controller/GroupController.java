package com.laulem.vectopath.referential.api.client.controller;

import com.laulem.vectopath.referential.api.business.model.Group;
import com.laulem.vectopath.referential.api.business.service.GroupUseCase;
import com.laulem.vectopath.referential.api.client.dto.GroupCreate;
import com.laulem.vectopath.referential.api.client.dto.GroupResponse;
import com.laulem.vectopath.referential.api.client.dto.GroupUpdate;
import com.laulem.vectopath.referential.api.client.dto.PaginatedResponse;
import com.laulem.vectopath.referential.api.client.security.SecurityExpressions;
import com.laulem.vectopath.referential.api.shared.PageResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
@Validated
@AllArgsConstructor
public class GroupController {

    private final GroupUseCase groupUseCase;

    @PreAuthorize(SecurityExpressions.REFERENTIAL_READ)
    @GetMapping
    public PaginatedResponse<GroupResponse> getAllGroups(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "50") @Min(1) int size) {
        PageResult<Group> businessResponse = groupUseCase.findAll(page, size, name);

        return PaginatedResponse.<GroupResponse>builder()
                .content(businessResponse.content().stream()
                        .map(GroupResponse::fromBusiness)
                        .toList())
                .totalElements(businessResponse.totalElements())
                .totalPages(businessResponse.totalPages())
                .currentPage(businessResponse.currentPage())
                .pageSize(businessResponse.pageSize())
                .build();
    }

    @PreAuthorize(SecurityExpressions.REFERENTIAL_READ)
    @GetMapping("/{id}")
    public GroupResponse getGroupById(@PathVariable UUID id) {
        return GroupResponse.fromBusiness(groupUseCase.getById(id));
    }

    @PreAuthorize(SecurityExpressions.REFERENTIAL_WRITE)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public GroupResponse createGroup(@Valid @RequestBody GroupCreate groupCreate) {
        Group createdGroup = groupUseCase.create(groupCreate.toBusiness());
        return GroupResponse.fromBusiness(createdGroup);
    }

    @PreAuthorize(SecurityExpressions.REFERENTIAL_WRITE)
    @PutMapping("/{id}")
    public GroupResponse updateGroup(
            @PathVariable UUID id,
            @Valid @RequestBody GroupUpdate groupUpdate) {
        Group updatedGroup = groupUseCase.update(id, groupUpdate.toBusiness());
        return GroupResponse.fromBusiness(updatedGroup);
    }

    @PreAuthorize(SecurityExpressions.REFERENTIAL_DELETE)
    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable UUID id) {
        groupUseCase.delete(id);
    }
}
