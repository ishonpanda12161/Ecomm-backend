package com.ecom.backend.repository;

import com.ecom.backend.model.AppRoles;
import com.ecom.backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<?> findByRoleName(AppRoles appRoles);
    Set<Role> findByRoleNameIn(Set<AppRoles> roleName);
}
