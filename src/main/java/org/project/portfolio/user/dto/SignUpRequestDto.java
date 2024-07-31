package org.project.portfolio.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.portfolio.global.constants.Message;
import org.project.portfolio.global.constants.RegExp;
import org.project.portfolio.user.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {

  @Pattern(regexp = RegExp.USER_ID, message = Message.INVALID_USER_ID)
  private String userId;
  @Pattern(regexp = RegExp.PASSWORD, message = Message.INVALID_PASSWORD)
  private String password;
  @Pattern(regexp = RegExp.USERNAME, message = Message.INVALID_USERNAME)
  private String username;
  @Pattern(regexp = RegExp.EMAIL, message = Message.INVALID_EMAIL)
  private String email;
  @Pattern(regexp = RegExp.PHONE, message = Message.INVALID_PHONE)
  private String phone;

  public User toEntity(PasswordEncoder passwordEncoder) {
    return User.builder()
        .userId(this.userId)
        .password(passwordEncoder.encode(this.password))
        .username(this.username)
        .email(this.email)
        .phone(this.phone)
        .build();
  }
}