package com.seabattle.Course_work.repositories;

import com.seabattle.Course_work.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RoleRepository extends JpaRepository<Role, Long> {
}