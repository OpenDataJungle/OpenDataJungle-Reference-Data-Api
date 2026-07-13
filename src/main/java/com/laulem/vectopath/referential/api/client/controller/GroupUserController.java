package com.laulem.vectopath.referential.api.client.controller;

import com.laulem.vectopath.referential.api.business.model.Group;
import com.laulem.vectopath.referential.api.business.model.User;
import com.laulem.vectopath.referential.api.business.service.GroupUserUseCase;
import com.laulem.vectopath.referential.api.client.dto.GroupResponse;
import com.laulem.vectopath.referential.api.client.dto.PaginatedResponse;
import com.laulem.vectopath.referential.api.client.dto.UserResponse;
import com.laulem.vectopath.referential.api.client.mapper.PaginatedResponseMapper;
import com.laulem.vectopath.referential.api.client.security.SecurityExpressions;
import com.laulem.vectopath.referential.api.shared.PageResult;
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

    @PreAuthorize(SecurityExpressions.REFERENTIAL_READ)
    @GetMapping("/users/{userId}/groups")
    public PaginatedResponse<GroupResponse> getGroupsByUserId(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "50") @Min(1) int size) {
        PageResult<Group> businessResponse = groupUserUseCase.getGroupsByUserId(userId, page, size);
        return PaginatedResponseMapper.toPaginatedResponse(businessResponse, GroupResponse::fromBusiness);
    }

    @PreAuthorize(SecurityExpressions.REFERENTIAL_READ)
    @GetMapping("/groups/{groupId}/users")
    public PaginatedResponse<UserResponse> getUsersByGroupId(
            @PathVariable UUID groupId,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "50") @Min(1) int size) {
        PageResult<User> businessResponse = groupUserUseCase.getUsersByGroupId(groupId, page, size);
        return PaginatedResponseMapper.toPaginatedResponse(businessResponse, UserResponse::fromBusiness);
    }

    @PreAuthorize(SecurityExpressions.REFERENTIAL_WRITE)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/groups/{groupId}/users/{userId}")
    public void addUserToGroup(
            @PathVariable UUID groupId,
            @PathVariable UUID userId) {
        groupUserUseCase.addUserToGroup(groupId, userId);
    }

    @PreAuthorize(SecurityExpressions.REFERENTIAL_WRITE)
    @DeleteMapping("/groups/{groupId}/users/{userId}")
    public void removeUserFromGroup(
            @PathVariable UUID groupId,
            @PathVariable UUID userId) {
        groupUserUseCase.removeUserFromGroup(groupId, userId);
    }
}
