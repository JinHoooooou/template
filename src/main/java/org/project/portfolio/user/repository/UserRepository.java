package org.project.portfolio.user.repository;

import org.project.portfolio.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);

  boolean existsByUserId(String userId);
}