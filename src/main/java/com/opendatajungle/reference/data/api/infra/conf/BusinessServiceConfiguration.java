package com.opendatajungle.reference.data.api.infra.conf;

import com.opendatajungle.commons.business.service.AuthenticationUseCase;
import com.opendatajungle.reference.data.api.business.repository.GroupRepository;
import com.opendatajungle.reference.data.api.business.repository.GroupUserRepository;
import com.opendatajungle.reference.data.api.business.repository.PermissionRepository;
import com.opendatajungle.reference.data.api.business.repository.UserRepository;
import com.opendatajungle.reference.data.api.business.service.GroupService;
import com.opendatajungle.reference.data.api.business.service.GroupUseCase;
import com.opendatajungle.reference.data.api.business.service.GroupUserService;
import com.opendatajungle.reference.data.api.business.service.GroupUserUseCase;
import com.opendatajungle.reference.data.api.business.service.PermissionService;
import com.opendatajungle.reference.data.api.business.service.PermissionUseCase;
import com.opendatajungle.reference.data.api.business.service.UserService;
import com.opendatajungle.reference.data.api.business.service.UserUseCase;
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
    public GroupUseCase groupService(GroupRepository groupRepository,
                                     GroupUserUseCase groupUserUseCase,
                                     UserUseCase userUseCase) {
        return new GroupService(groupRepository, groupUserUseCase, userUseCase);
    }

    @Bean
    public GroupUserUseCase groupUserService(GroupUserRepository groupUserRepository,
                                             GroupRepository groupRepository,
                                             UserUseCase userUseCase,
                                             PermissionUseCase permissionUseCase) {
        return new GroupUserService(groupUserRepository, groupRepository, userUseCase, permissionUseCase);
    }
}
