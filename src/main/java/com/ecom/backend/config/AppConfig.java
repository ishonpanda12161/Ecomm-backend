package com.ecom.backend.config;


import com.ecom.backend.model.AppRoles;
import com.ecom.backend.model.Role;
import com.ecom.backend.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;


@Component
public class AppConfig {

    @Bean
    public CommandLineRunner initRole(RoleRepository roleRepository)
    {
        return args -> {
            Role user = new Role(AppRoles.ROLE_USER);
            Role admin = new Role(AppRoles.ROLE_ADMIN);
            Role guest = new Role(AppRoles.ROLE_SELLER);
            Set<Role> roles = new HashSet<>(Set.of(user,admin,guest));

            roles.forEach(role -> {
                if(roleRepository.findByRoleName(role.getRoleName()).isEmpty())
                {
                    roleRepository.save(role);
                }
            });
        };
    }
}
