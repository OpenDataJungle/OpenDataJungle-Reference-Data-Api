package com.opendatajungle.reference.data.api.business.service;

import com.opendatajungle.commons.business.exception.NotFoundException;
import com.opendatajungle.commons.business.exception.ParamException;
import com.opendatajungle.commons.business.service.AuthenticationUseCase;
import com.opendatajungle.reference.data.api.business.model.User;
import com.opendatajungle.reference.data.api.business.repository.UserRepository;
import com.opendatajungle.reference.data.api.shared.PageResult;

import java.util.Map;
import java.util.UUID;

public class UserService implements UserUseCase {

    private static final String USER = "User";

    private final UserRepository userRepository;
    private final AuthenticationUseCase authenticationUseCase;

    public UserService(UserRepository userRepository, AuthenticationUseCase authenticationUseCase) {
        this.userRepository = userRepository;
        this.authenticationUseCase = authenticationUseCase;
    }

    @Override
    public PageResult<User> findAll(int page, int size, String username) {
        return userRepository.findAll(page, size, username);
    }

    @Override
    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER, id.toString()));
    }

    @Override
    public boolean existsById(UUID id) {
        return userRepository.existsById(id);
    }

    @Override
    public User create(User user) {
        if (user.authId() == null || user.authId().isBlank()) {
            throw new ParamException(
                    "USER_AUTH_ID_REQUIRED",
                    "User authId is required",
                    "authId"
            );
        }
        if (user.authIss() == null || user.authIss().isBlank()) {
            throw new ParamException(
                    "USER_AUTH_ISS_REQUIRED",
                    "User authIss is required",
                    "authIss"
            );
        }

        if (userRepository.existsByAuthIdAndAuthIss(user.authId(), user.authIss())) {
            throw authIdentityAlreadyExists(user.authId(), user.authIss());
        }
        return userRepository.save(user);
    }

    @Override
    public User update(UUID id, User user) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(USER, id.toString());
        }
        User toUpdate = User.builder()
                .id(id)
                .firstName(user.firstName())
                .lastName(user.lastName())
                .username(user.username())
                .build();
        return userRepository.save(toUpdate);
    }

    private static ParamException authIdentityAlreadyExists(String authId, String authIss) {
        return new ParamException(
                "USER_AUTH_IDENTITY_ALREADY_EXISTS",
                "A user with authIss '" + authIss + "' and authId '" + authId + "' already exists",
                "authId",
                Map.of("authIss", authIss)
        );
    }

    @Override
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(USER, id.toString());
        }
        userRepository.deleteById(id);
    }

    @Override
    public User getOrCreateCurrentUser() {
        String authId = authenticationUseCase.findCurrentUserAuthId()
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new ParamException(
                        "USER_AUTH_ID_REQUIRED",
                        "Current authenticated user has no authId",
                        "authId"
                ));
        String authIss = authenticationUseCase.findCurrentUserAuthIss()
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new ParamException(
                        "USER_AUTH_ISS_REQUIRED",
                        "Current authenticated user has no authIss",
                        "authIss"
                ));
        String username = authenticationUseCase.findCurrentUserUsername()
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new ParamException(
                        "USER_USERNAME_REQUIRED",
                        "Current authenticated user has no username",
                        "username"
                ));
        String firstName = authenticationUseCase.findCurrentUserFirstName()
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new ParamException(
                        "USER_FIRST_NAME_REQUIRED",
                        "Current authenticated user has no first name",
                        "firstName"
                ));
        String lastName = authenticationUseCase.findCurrentUserLastName()
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new ParamException(
                        "USER_LAST_NAME_REQUIRED",
                        "Current authenticated user has no last name",
                        "lastName"
                ));

        return userRepository.findByAuthIdAndAuthIss(authId, authIss)
                .orElseGet(() -> userRepository.save(User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .username(username)
                        .authId(authId)
                        .authIss(authIss)
                        .build()));
    }
}
