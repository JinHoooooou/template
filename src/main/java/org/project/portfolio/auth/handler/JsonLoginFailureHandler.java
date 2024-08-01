package org.project.portfolio.auth.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.project.portfolio.auth.dto.LoginResponseDto;
import org.project.portfolio.global.constants.Message;
import org.project.portfolio.global.exception.UnsupportedMediaTypeException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class JsonLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  private static final String RESPONSE_CONTENT_TYPE = "application/json;charset=utf-8";

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException {
    response.setContentType(RESPONSE_CONTENT_TYPE);
    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    String message = "Invalid Login Error";

    if (exception instanceof InsufficientAuthenticationException) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      message = Message.BAD_REQUEST;
    } else if (exception instanceof AuthenticationServiceException) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      message = Message.FORBIDDEN;
    } else if (exception instanceof UsernameNotFoundException) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      message = Message.NOT_FOUND;
    } else if (exception instanceof UnsupportedMediaTypeException) {
      response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
      message = Message.UNSUPPORTED_MEDIA_TYPE;
    }

    LoginResponseDto responseBody = LoginResponseDto.builder()
        .message(message)
        .detail(exception.getLocalizedMessage())
        .build();

    new ObjectMapper().writeValue(response.getWriter(), responseBody);
  }
}
