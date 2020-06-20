package com.lessons.onespring.repositories;

import com.lessons.onespring.entities.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Optional<Privilege> findById(Long id);
    Set<Privilege> findAllByIdIn(Set<Long> ids);
    boolean existsAllByIdIn(Set<Long> ids);
}
