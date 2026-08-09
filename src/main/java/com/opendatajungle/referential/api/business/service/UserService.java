package com.opendatajungle.referential.api.business.service;

import com.opendatajungle.referential.api.business.exception.NotFoundException;
import com.opendatajungle.referential.api.business.exception.ParamException;
import com.opendatajungle.referential.api.business.model.User;
import com.opendatajungle.referential.api.business.repository.UserRepository;
import com.opendatajungle.referential.api.shared.PageResult;

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
    public User create(User user) {
        if (userRepository.existsByUsername(user.username())) {
            throw new ParamException(
                    "USER_USERNAME_ALREADY_EXISTS",
                    "A user with username '" + user.username() + "' already exists",
                    "username"
            );
        }
        return userRepository.save(user);
    }

    @Override
    public User update(UUID id, User user) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(USER, id.toString());
        }
        if (userRepository.existsByUsernameAndIdNot(user.username(), id)) {
            throw new ParamException(
                    "USER_USERNAME_ALREADY_EXISTS",
                    "A user with username '" + user.username() + "' already exists",
                    "username"
            );
        }
        User toUpdate = User.builder()
                .id(id)
                .firstName(user.firstName())
                .lastName(user.lastName())
                .username(user.username())
                .build();
        return userRepository.save(toUpdate);
    }

    @Override
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(USER, id.toString());
        }
        userRepository.deleteById(id);
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(USER, username));
    }

    @Override
    public User getOrCreateCurrentUser() {
        String username = authenticationUseCase.findCurrentUser()
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

        return userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.save(User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .username(username)
                        .build()));
    }
}
