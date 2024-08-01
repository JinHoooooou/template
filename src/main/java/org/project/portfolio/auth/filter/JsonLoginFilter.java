package org.project.portfolio.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import org.project.portfolio.auth.dto.LoginRequestDto;
import org.project.portfolio.global.constants.Message;
import org.project.portfolio.global.exception.UnsupportedMediaTypeException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class JsonLoginFilter extends AbstractAuthenticationProcessingFilter {

  private static final String LOGIN_REQUEST_URL = "/api/v1/login";
  private static final String LOGIN_REQUEST_HTTP_METHOD = "POST";
  private static final String LOGIN_REQUEST_CONTENT_TYPE = "application/json";
  private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
      new AntPathRequestMatcher(LOGIN_REQUEST_URL, LOGIN_REQUEST_HTTP_METHOD);

  private final Validator validator;

  public JsonLoginFilter(Validator validator) {
    super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
    this.validator = validator;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, IOException {
    if (!isApplicationJson(request.getContentType())) {
      throw new UnsupportedMediaTypeException(Message.ONLY_SUPPORTED_APPLICATION_JSON);
    }
    if (isAlreadyLogin()) {
      throw new AuthenticationServiceException(Message.ALREADY_LOGIN);
    }

    LoginRequestDto loginRequestDto = parseDto(request);
    return getAuthentication(loginRequestDto);
  }

  private boolean isApplicationJson(String contentType) {
    return contentType != null && contentType.equals(LOGIN_REQUEST_CONTENT_TYPE);
  }

  private boolean isAlreadyLogin() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null && authentication.isAuthenticated();
  }

  private LoginRequestDto parseDto(HttpServletRequest request) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    LoginRequestDto loginRequestDto = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
    Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(loginRequestDto);
    if (!violations.isEmpty()) {
      String details = violations.stream()
          .map(ConstraintViolation::getMessage)
          .collect(Collectors.joining(", "));
      throw new InsufficientAuthenticationException(details);
    }
    return loginRequestDto;
  }

  private Authentication getAuthentication(LoginRequestDto loginRequestDto) {
    UsernamePasswordAuthenticationToken authentication =
        UsernamePasswordAuthenticationToken.unauthenticated(loginRequestDto.getEmail(), loginRequestDto.getPassword());
    return this.getAuthenticationManager().authenticate(authentication);
  }
}
