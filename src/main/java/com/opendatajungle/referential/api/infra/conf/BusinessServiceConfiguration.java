package com.opendatajungle.referential.api.infra.conf;

import com.opendatajungle.referential.api.business.repository.GroupRepository;
import com.opendatajungle.referential.api.business.repository.GroupUserRepository;
import com.opendatajungle.referential.api.business.repository.PermissionRepository;
import com.opendatajungle.referential.api.business.repository.UserRepository;
import com.opendatajungle.referential.api.business.service.AuthenticationUseCase;
import com.opendatajungle.referential.api.business.service.GroupService;
import com.opendatajungle.referential.api.business.service.GroupUseCase;
import com.opendatajungle.referential.api.business.service.GroupUserService;
import com.opendatajungle.referential.api.business.service.GroupUserUseCase;
import com.opendatajungle.referential.api.business.service.PermissionService;
import com.opendatajungle.referential.api.business.service.PermissionUseCase;
import com.opendatajungle.referential.api.business.service.UserService;
import com.opendatajungle.referential.api.business.service.UserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BusinessServiceConfiguration {

    @Bean
    public PermissionUseCase permissionService(PermissionRepository permissionRepository) {
        return new PermissionService(permissionRepository);
    }

    @Bean
    public UserUseCase userService(UserRepository userRepository, AuthenticationUseCase authenticationUseCase) {
        return new UserService(userRepository, authenticationUseCase);
    }

    @Bean
    public GroupUseCase groupService(GroupRepository groupRepository) {
        return new GroupService(groupRepository);
    }

    @Bean
    public GroupUserUseCase groupUserService(GroupUserRepository groupUserRepository,
                                             UserRepository userRepository,
                                             GroupRepository groupRepository,
                                             PermissionRepository permissionRepository) {
        return new GroupUserService(groupUserRepository, userRepository, groupRepository, permissionRepository);
    }
}
