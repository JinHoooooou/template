package org.project.portfolio.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.project.portfolio.global.constants.Message;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  // 필터에서 인증 실패 처리
  /*
    엔드 포인트가 인증을 필요로 할 때(authenticated(), hasRole(), hasAuthority())
    JwtAuthenticationFilter에서 AuthenticationException이 발생하는 경우
    JsonLoginFilter는 고려 X (JsonLogin Success/Failure Handler에서 처리)
  */

  private static final String RESPONSE_CONTENT_TYPE = "application/json;charset=utf-8";

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException {
    response.setContentType(RESPONSE_CONTENT_TYPE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("message", Message.UNAUTHORIZED);
    responseBody.put("detail", Message.NOT_LOGGED_IN);

    new ObjectMapper().writeValue(response.getWriter(), responseBody);
  }
}
