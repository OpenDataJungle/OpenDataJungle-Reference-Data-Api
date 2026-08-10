package com.opendatajungle.reference.data.api.client.controller;

import com.opendatajungle.reference.data.api.business.model.User;
import com.opendatajungle.reference.data.api.business.service.UserUseCase;
import com.opendatajungle.reference.data.api.client.dto.PaginatedResponse;
import com.opendatajungle.reference.data.api.client.dto.UserRequest;
import com.opendatajungle.reference.data.api.client.dto.UserResponse;
import com.opendatajungle.reference.data.api.client.mapper.PaginatedResponseMapper;
import com.opendatajungle.reference.data.api.client.security.SecurityExpressions;
import com.opendatajungle.reference.data.api.shared.PageResult;
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
@RequestMapping("/api/v1/users")
@Validated
@AllArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_READ)
    @GetMapping
    public PaginatedResponse<UserResponse> getAllUsers(
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "50") @Min(1) int size) {
        PageResult<User> businessResponse = userUseCase.findAll(page, size, username);
        return PaginatedResponseMapper.toPaginatedResponse(businessResponse, UserResponse::fromBusiness);
    }

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_READ)
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable UUID id) {
        return UserResponse.fromBusiness(userUseCase.getById(id));
    }

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_READ)
    @GetMapping("/username/{username}")
    public UserResponse getUserByUsername(@PathVariable String username) {
        return UserResponse.fromBusiness(userUseCase.getByUsername(username));
    }

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_READ)
    @PostMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse getOrCreateMe() {
        return UserResponse.fromBusiness(userUseCase.getOrCreateCurrentUser());
    }

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_WRITE)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse createUser(@Valid @RequestBody UserRequest userRequest) {
        User createdUser = userUseCase.create(userRequest.toBusiness());
        return UserResponse.fromBusiness(createdUser);
    }

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_WRITE)
    @PutMapping("/{id}")
    public UserResponse updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequest userRequest) {
        User updatedUser = userUseCase.update(id, userRequest.toBusiness());
        return UserResponse.fromBusiness(updatedUser);
    }

    @PreAuthorize(SecurityExpressions.REFERENCEDATA_DELETE)
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userUseCase.delete(id);
    }
}
