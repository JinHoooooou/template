package org.project.portfolio.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.project.portfolio.auth.dto.LoginResponseDto;
import org.project.portfolio.global.constants.Message;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@RequiredArgsConstructor
public class JsonLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private static final String RESPONSE_CONTENT_TYPE = "application/json;charset=utf-8";

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(RESPONSE_CONTENT_TYPE);

    LoginResponseDto responseBody = LoginResponseDto.builder()
        .message(Message.OK)
        .accessToken("temp access token")
        .build();

    new ObjectMapper().writeValue(response.getWriter(), responseBody);
  }
}
