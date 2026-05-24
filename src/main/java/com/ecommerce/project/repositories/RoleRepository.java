package com.ecommerce.project.repositories;

import com.ecommerce.project.models.AppRole;
import com.ecommerce.project.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(AppRole roleName);
}
