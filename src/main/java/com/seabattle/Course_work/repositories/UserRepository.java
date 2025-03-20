package com.seabattle.Course_work.repositories;

import com.seabattle.Course_work.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByLogin(String login);
}
