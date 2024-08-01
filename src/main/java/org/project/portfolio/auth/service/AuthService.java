package org.project.portfolio.auth.service;

import lombok.RequiredArgsConstructor;
import org.project.portfolio.auth.dto.AuthenticatedUser;
import org.project.portfolio.global.constants.Message;
import org.project.portfolio.user.entity.User;
import org.project.portfolio.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(username).orElseThrow(
        () -> new UsernameNotFoundException(Message.NOT_FOUND_EMAIL)
    );
    return new AuthenticatedUser(user);
  }
}
