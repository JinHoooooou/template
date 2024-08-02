package org.project.portfolio.helper.dto;

import org.project.portfolio.auth.dto.LoginRequestDto;
import org.project.portfolio.user.dto.SignUpRequestDto;

public class RequestDto {

  public static SignUpRequestDto validSignUpRequestDto() {
    return SignUpRequestDto.builder()
        .userId("testId")
        .password("q1w2e3r4t5!@")
        .username("테스트")
        .email("test@test.kr")
        .phone("010-1234-5678")
        .build();
  }

  public static LoginRequestDto validLoginRequestDto() {
    return LoginRequestDto.builder()
        .email("test@test.kr")
        .password("q1w2e3r4t5!@")
        .build();
  }
}
