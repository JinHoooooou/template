package org.project.portfolio.global.config;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.project.portfolio.auth.filter.JsonLoginFilter;
import org.project.portfolio.auth.handler.JsonLoginFailureHandler;
import org.project.portfolio.auth.handler.JsonLoginSuccessHandler;
import org.project.portfolio.auth.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final Validator validator;
  private final AuthService authService;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.csrf(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable);

    httpSecurity.headers(frame -> frame.frameOptions(FrameOptionsConfig::sameOrigin));

    httpSecurity.addFilterAt(jsonLoginFilter(), UsernamePasswordAuthenticationFilter.class);

    return httpSecurity.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(authService);
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    authenticationProvider.setHideUserNotFoundExceptions(false);

    return new ProviderManager(authenticationProvider);
  }

  @Bean
  public JsonLoginSuccessHandler jsonLoginSuccessHandler() {
    return new JsonLoginSuccessHandler();
  }

  @Bean
  public JsonLoginFailureHandler jsonLoginFailureHandler() {
    return new JsonLoginFailureHandler();
  }

  @Bean
  public AbstractAuthenticationProcessingFilter jsonLoginFilter() {
    JsonLoginFilter jsonLoginFilter = new JsonLoginFilter(validator);
    jsonLoginFilter.setAuthenticationManager(authenticationManager());
    jsonLoginFilter.setAuthenticationSuccessHandler(jsonLoginSuccessHandler());
    jsonLoginFilter.setAuthenticationFailureHandler(jsonLoginFailureHandler());

    return jsonLoginFilter;
  }
}
