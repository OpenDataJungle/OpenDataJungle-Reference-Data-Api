package com.opendatajungle.reference.data.api.client.controller;

import com.opendatajungle.reference.data.api.business.model.GroupUser;
import com.opendatajungle.reference.data.api.business.service.GroupUserUseCase;
import com.opendatajungle.reference.data.api.client.dto.GroupUserResponse;
import com.opendatajungle.reference.data.api.client.dto.PaginatedResponse;
import com.opendatajungle.reference.data.api.client.mapper.PaginatedResponseMapper;
import com.opendatajungle.reference.data.api.client.security.SecurityExpressions;
import com.opendatajungle.reference.data.api.shared.PageResult;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Validated
@AllArgsConstructor
public class GroupUserController {

    private final GroupUserUseCase groupUserUseCase;

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_READ)
    @GetMapping("/users/{userId}/groups")
    public PaginatedResponse<GroupUserResponse> getGroupsByUserId(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "50") @Min(1) int size) {
        PageResult<GroupUser> businessResponse = groupUserUseCase.getGroupsByUserId(userId, page, size);
        return PaginatedResponseMapper.toPaginatedResponse(businessResponse, GroupUserResponse::fromBusiness);
    }

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_READ)
    @GetMapping("/groups/{groupId}/users")
    public PaginatedResponse<GroupUserResponse> getUsersByGroupId(
            @PathVariable UUID groupId,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "50") @Min(1) int size) {
        PageResult<GroupUser> businessResponse = groupUserUseCase.getUsersByGroupId(groupId, page, size);
        return PaginatedResponseMapper.toPaginatedResponse(businessResponse, GroupUserResponse::fromBusiness);
    }

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_WRITE)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/groups/{groupId}/users/{userId}/permissions/{permissionId}")
    public void addUserToGroup(
            @PathVariable UUID groupId,
            @PathVariable UUID userId,
            @PathVariable UUID permissionId) {
        groupUserUseCase.addUserToGroup(groupId, userId, permissionId);
    }

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_WRITE)
    @DeleteMapping("/groups/{groupId}/users/{userId}")
    public void removeUserFromGroup(
            @PathVariable UUID groupId,
            @PathVariable UUID userId) {
        groupUserUseCase.removeUserFromGroup(groupId, userId);
    }
}
