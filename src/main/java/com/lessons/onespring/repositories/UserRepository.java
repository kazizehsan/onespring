package com.lessons.onespring.repositories;

import com.lessons.onespring.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Set<User> findAllByIdIn(Set<Long> ids);

    boolean existsAllByIdIn(Set<Long> ids);
}
