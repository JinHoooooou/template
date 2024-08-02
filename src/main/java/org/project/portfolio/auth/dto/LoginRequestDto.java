package org.project.portfolio.auth.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.portfolio.global.constants.Message;
import org.project.portfolio.global.constants.RegExp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

  @Pattern(regexp = RegExp.EMAIL, message = Message.INVALID_EMAIL)
  private String email;
  @Pattern(regexp = RegExp.PASSWORD, message = Message.INVALID_PASSWORD)
  private String password;
}