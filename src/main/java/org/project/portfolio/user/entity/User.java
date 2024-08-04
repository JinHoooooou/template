package org.project.portfolio.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class User {

  @Id
  @Column(name = "user_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true, nullable = false)

  private String email;
  @Column(nullable = false)
  private String password;
  @Column(name = "userid", unique = true, nullable = false)
  private String userId;
  @Column(nullable = false)
  private String username;
  @Column(nullable = false)
  private String phone;
}
