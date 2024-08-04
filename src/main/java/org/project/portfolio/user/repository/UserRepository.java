package org.project.portfolio.user.repository;

import java.util.Optional;
import org.project.portfolio.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);

  boolean existsByUserId(String userId);

  Optional<User> findByEmail(String username);
}
