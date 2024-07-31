package org.project.portfolio.user.service;

import lombok.RequiredArgsConstructor;
import org.project.portfolio.global.constants.Message;
import org.project.portfolio.global.exception.DuplicateResourceException;
import org.project.portfolio.user.dto.SignUpRequestDto;
import org.project.portfolio.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public void signUp(SignUpRequestDto signUpRequestDto) {
    if (userRepository.existsByEmail(signUpRequestDto.getEmail())) {
      throw new DuplicateResourceException(Message.DUPLICATE_EMAIL);
    }
    if (userRepository.existsByUserId(signUpRequestDto.getUserId())) {
      throw new DuplicateResourceException(Message.DUPLICATE_USER_ID);
    }

    userRepository.save(signUpRequestDto.toEntity(passwordEncoder));
  }
}
