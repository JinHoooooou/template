package org.project.portfolio.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.project.portfolio.global.constants.Message;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  // 필터에서 권한 오류 처리
  /*
    1. 엔드 포인트가 특정 권한을 필요로 할 때 (hasRole(), hasAuthority())
    인증 정보에 그 권한이 없는 경우

    2. 엔드 포인트가 anonymous() 권한일 때
    인증 정보를 포함한 경우
  */
  private static final String RESPONSE_CONTENT_TYPE = "application/json;charset=utf-8";
  private static final String SIGNUP_URL = "/api/v1/signup";

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException {
    response.setContentType(RESPONSE_CONTENT_TYPE);
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    String detail = "Invalid Access Denied";

    String url = request.getRequestURI();
    if (url.startsWith(SIGNUP_URL)) {
      detail = Message.ALREADY_LOGIN;
    }

    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("message", Message.FORBIDDEN);
    responseBody.put("detail", detail);

    new ObjectMapper().writeValue(response.getWriter(), responseBody);
  }
}
