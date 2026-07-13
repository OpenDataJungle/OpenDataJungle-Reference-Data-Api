package com.laulem.vectopath.referential.api.business.service;

import com.laulem.vectopath.referential.api.business.exception.NotFoundException;
import com.laulem.vectopath.referential.api.business.exception.ParamException;
import com.laulem.vectopath.referential.api.business.model.User;
import com.laulem.vectopath.referential.api.business.repository.UserRepository;
import com.laulem.vectopath.referential.api.shared.PageResult;

import java.util.UUID;

public class UserService implements UserUseCase {

    private static final String USER = "User";

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
