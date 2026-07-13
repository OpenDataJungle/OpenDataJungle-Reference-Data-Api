package com.laulem.vectopath.referential.api.infra.conf;

import com.laulem.vectopath.referential.api.business.repository.PermissionRepository;
import com.laulem.vectopath.referential.api.business.repository.UserRepository;
import com.laulem.vectopath.referential.api.business.service.PermissionService;
import com.laulem.vectopath.referential.api.business.service.PermissionUseCase;
import com.laulem.vectopath.referential.api.business.service.UserService;
import com.laulem.vectopath.referential.api.business.service.UserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BusinessServiceConfiguration {

    @Bean
    public PermissionUseCase permissionService(PermissionRepository permissionRepository) {
        return new PermissionService(permissionRepository);
    }

    @Bean
    public UserUseCase userService(UserRepository userRepository) {
        return new UserService(userRepository);
    }
}
