package com.laulem.vectopath.referential.api.client.controller;

import com.laulem.vectopath.referential.api.business.model.User;
import com.laulem.vectopath.referential.api.business.service.UserUseCase;
import com.laulem.vectopath.referential.api.client.dto.PaginatedResponse;
import com.laulem.vectopath.referential.api.client.dto.UserCreate;
import com.laulem.vectopath.referential.api.client.dto.UserResponse;
import com.laulem.vectopath.referential.api.client.dto.UserUpdate;
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
@RequestMapping("/api/v1/users")
@Validated
@AllArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;

    @PreAuthorize(SecurityExpressions.REFERENTIAL_READ)
    @GetMapping
    public PaginatedResponse<UserResponse> getAllUsers(
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "50") @Min(1) int size) {
        PageResult<User> businessResponse = userUseCase.findAll(page, size, username);

        return PaginatedResponse.<UserResponse>builder()
                .content(businessResponse.content().stream()
                        .map(UserResponse::fromBusiness)
                        .toList())
                .totalElements(businessResponse.totalElements())
                .totalPages(businessResponse.totalPages())
                .currentPage(businessResponse.currentPage())
                .pageSize(businessResponse.pageSize())
                .build();
    }

    @PreAuthorize(SecurityExpressions.REFERENTIAL_READ)
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable UUID id) {
        return UserResponse.fromBusiness(userUseCase.getById(id));
    }

    @PreAuthorize(SecurityExpressions.REFERENTIAL_WRITE)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse createUser(@Valid @RequestBody UserCreate userCreate) {
        User createdUser = userUseCase.create(userCreate.toBusiness());
        return UserResponse.fromBusiness(createdUser);
    }

    @PreAuthorize(SecurityExpressions.REFERENTIAL_WRITE)
    @PutMapping("/{id}")
    public UserResponse updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdate userUpdate) {
        User updatedUser = userUseCase.update(id, userUpdate.toBusiness());
        return UserResponse.fromBusiness(updatedUser);
    }

    @PreAuthorize(SecurityExpressions.REFERENTIAL_DELETE)
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userUseCase.delete(id);
    }
}
