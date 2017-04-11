package com.codecool.neighbrotaxi.repository;

import com.codecool.neighbrotaxi.model.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for handling role table.
 */
@RepositoryRestResource(collectionResourceRel = "userRoles", path = "user-roles")
@Transactional
public interface RoleRepository extends JpaRepository<Role, Integer>{
    Role findByName(String name);
}
