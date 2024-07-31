package org.project.portfolio.user.signup.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.portfolio.global.constants.Message;
import org.project.portfolio.global.exception.DuplicateResourceException;
import org.project.portfolio.helper.dto.RequestDto;
import org.project.portfolio.user.dto.SignUpRequestDto;
import org.project.portfolio.user.entity.User;
import org.project.portfolio.user.repository.UserRepository;
import org.project.portfolio.user.service.SignUpService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class SignUpServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @InjectMocks
  private SignUpService signUpService;

  @Test
  @DisplayName("DB에 중복된 email이 없고 중복된 userId가 없을 때, User Entity를 반환한다.")
  public void success_onNoDuplicateEmailAndNoDuplicateUserIdInDB_shouldReturnUserEntity() {
    // Given: 유효한 SignUpRequestDto가 주어진다.
    SignUpRequestDto validSignUpRequestDto = RequestDto.validSignUpRequestDto();
    // Mocking
    when(userRepository.existsByEmail(validSignUpRequestDto.getEmail())).thenReturn(false);
    when(userRepository.existsByUserId(validSignUpRequestDto.getUserId())).thenReturn(false);

    // When: signUp()을 호출한다.
    signUpService.signUp(validSignUpRequestDto);

    // Then: UserRepository의 save()가 정상적으로 호출된다.
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("DB에 중복된 email이 있을 때, Exception이 발생한다.")
  public void fail_onDuplicateEmailInDB_shouldThrowException() {
    // Given: 유효한 SignupRequestDto가 주어진다.
    SignUpRequestDto validSignUpRequestDto = RequestDto.validSignUpRequestDto();
    // Mocking
    when(userRepository.existsByEmail(validSignUpRequestDto.getEmail())).thenReturn(true);

    // When: signUp()을 호출한다.
    // Then: DuplicateResourceException이 발생한다.
    assertThatThrownBy(() -> signUpService.signUp(validSignUpRequestDto))
        .isInstanceOf(DuplicateResourceException.class)
        .hasMessage(Message.DUPLICATE_EMAIL);
    // And: UserRepository의 save()가 호출되지 않는다.
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("DB에 중복된 userId가 있을 때, Exception이 발생한다.")
  public void fail_onDuplicateUserIdInDB_shouldThrowException() {
    // Given: 유효한 SignupRequestDto가 주어진다.
    SignUpRequestDto validSignUpRequestDto = RequestDto.validSignUpRequestDto();
    // Mocking
    when(userRepository.existsByUserId(validSignUpRequestDto.getUserId())).thenReturn(true);

    // When: signUp()을 호출한다.
    // Then: DuplicateResourceException이 발생한다.
    assertThatThrownBy(() -> signUpService.signUp(validSignUpRequestDto))
        .isInstanceOf(DuplicateResourceException.class)
        .hasMessage(Message.DUPLICATE_USER_ID);
    // And: UserRepository의 save()가 호출되지 않는다.
    verify(userRepository, never()).save(any(User.class));
  }
}
