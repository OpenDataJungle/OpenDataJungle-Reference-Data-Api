package com.opendatajungle.reference.data.api.client.controller;

import com.opendatajungle.reference.data.api.business.model.Group;
import com.opendatajungle.reference.data.api.business.service.GroupUseCase;
import com.opendatajungle.reference.data.api.client.dto.GroupRequest;
import com.opendatajungle.reference.data.api.client.dto.GroupResponse;
import com.opendatajungle.reference.data.api.client.dto.PaginatedResponse;
import com.opendatajungle.reference.data.api.client.mapper.PaginatedResponseMapper;
import com.opendatajungle.reference.data.api.client.security.SecurityExpressions;
import com.opendatajungle.reference.data.api.shared.PageResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
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

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_READ)
    @GetMapping
    public PaginatedResponse<GroupResponse> getAllGroups(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "50") @Min(1) @Max(100) int size) {
        PageResult<Group> businessResponse = groupUseCase.findAll(page, size, name);
        return PaginatedResponseMapper.toPaginatedResponse(businessResponse, GroupResponse::fromBusiness);
    }

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_READ)
    @GetMapping("/{id}")
    public GroupResponse getGroupById(@PathVariable UUID id) {
        return GroupResponse.fromBusiness(groupUseCase.getById(id));
    }

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_WRITE)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public GroupResponse createGroup(@Valid @RequestBody GroupRequest groupRequest) {
        Group createdGroup = groupUseCase.create(groupRequest.toBusiness());
        return GroupResponse.fromBusiness(createdGroup);
    }

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_WRITE)
    @PutMapping("/{id}")
    public GroupResponse updateGroup(
            @PathVariable UUID id,
            @Valid @RequestBody GroupRequest groupRequest) {
        Group updatedGroup = groupUseCase.update(id, groupRequest.toBusiness());
        return GroupResponse.fromBusiness(updatedGroup);
    }

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_DELETE)
    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable UUID id) {
        groupUseCase.delete(id);
    }
}
