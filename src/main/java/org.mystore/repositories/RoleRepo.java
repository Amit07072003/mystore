package org.mystore.repositories;

import org.mystore.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface RoleRepo extends JpaRepository<Role,Long> {
    List<Role> findAllByRoleNameIn(List<String> roleNames);

    // Optional helper method to find role by value (like "USER", "ADMIN")

    Role findByRoleName(String roleName);

}
