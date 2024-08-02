package org.project.portfolio.article.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.portfolio.article.dto.ArticleCreateRequestDto;
import org.project.portfolio.auth.dto.LoginRequestDto;
import org.project.portfolio.auth.service.AuthService;
import org.project.portfolio.auth.service.JwtService;
import org.project.portfolio.global.config.SecurityConfig;
import org.project.portfolio.global.constants.Message;
import org.project.portfolio.helper.dto.RequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ArticleController.class)
@Import(SecurityConfig.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ArticleControllerTest {

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private AuthService authService;
  @MockBean
  public JwtService jwtService;
  private static final String TEST_ACCESS_TOKEN = "test access token";

  @Test
  @DisplayName("로그인 한 상태이고, 유효한 ArticleCreateRequestDto가 주어졌을 때, 201 Created를 응답해야한다.")
  public void success_onLoginAndValidArticleCreateRequestDto_shouldReturn201Created() throws Exception {
    // Given: 유효한 ArticleCreateRequestDto가 주어진다.
    ArticleCreateRequestDto validArticleCreateRequestDto = RequestDto.validArticleCreateRequestDto();
    // Mocking
    mockJwtService();

    // When: Create Article API를 호출한다.
    ResultActions resultActions = callApiWith(validArticleCreateRequestDto, TEST_ACCESS_TOKEN);

    // Then: Status는 201이다.
    resultActions.andExpect(status().isCreated());
    // And: Response Body로 message를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.CREATED)));
  }

  @Test
  @DisplayName("로그인 한 상태이고, title이 유효하지 않을 때, 400 Bad Request를 응답해야한다.")
  public void fail_onLoginAndInvalidTitleInArticleCreateRequestDto_shouldReturn400BadRequest() throws Exception {
    // Given: 유효하지 않은 title이 주어진다.
    ArticleCreateRequestDto invalidArticleCreateRequestDto = RequestDto.validArticleCreateRequestDto();
    invalidArticleCreateRequestDto.setTitle("       ");
    // Mocking
    mockJwtService();

    // When: Create Article API를 호출한다.
    ResultActions resultActions = callApiWith(invalidArticleCreateRequestDto, TEST_ACCESS_TOKEN);

    // Then: Status는 400이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.BAD_REQUEST)));
    resultActions.andExpect(jsonPath("$.details.title", is(Message.INVALID_TITLE)));
  }

  @Test
  @DisplayName("로그인 한 상태이고, contents가 유효하지 않을 때, 400 Bad Request를 응답해야한다.")
  public void fail_onLoginAndInvalidContentsInArticleCreateRequestDto_shouldReturn400BadRequest() throws Exception {
    // Given: 유효하지 않은 contents가 주어진다.
    ArticleCreateRequestDto invalidArticleCreateRequestDto = RequestDto.validArticleCreateRequestDto();
    invalidArticleCreateRequestDto.setContents("           ");
    // Mocking
    mockJwtService();

    // When: Create Article API를 호출한다.
    ResultActions resultActions = callApiWith(invalidArticleCreateRequestDto, TEST_ACCESS_TOKEN);

    // Then: Status는 400이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.BAD_REQUEST)));
    resultActions.andExpect(jsonPath("$.details.contents", is(Message.INVALID_CONTENTS)));
  }

  @Test
  @DisplayName("로그인 상태가 아닐 때, 401 Unauthorized를 응답해야한다.")
  public void fail_onNotLogin_shouldReturn401Unauthorized() throws Exception {
    // Given: 유효한 ArticleCreateRequestDto가 주어진다.
    ArticleCreateRequestDto validArticleCreateRequestDto = RequestDto.validArticleCreateRequestDto();
    // Mocking
    doThrow(new JwtException("JWT Exception")).when(jwtService).validate(any());

    // When: Create Article API를 호출한다.
    ResultActions resultActions = callApiWith(validArticleCreateRequestDto, "invalidAccessToken");

    // Then: Status는 401이다.
    resultActions.andExpect(status().isUnauthorized());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.UNAUTHORIZED)));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_LOGGED_IN)));
  }

  private void mockJwtService() {
    LoginRequestDto validLoginRequestDto = RequestDto.validLoginRequestDto();
    when(jwtService.extractAccessToken(any()))
        .thenReturn(TEST_ACCESS_TOKEN);
    when(jwtService.extractEmail(TEST_ACCESS_TOKEN))
        .thenReturn(validLoginRequestDto.getEmail());
    when(jwtService.extractAuthorities(TEST_ACCESS_TOKEN))
        .thenReturn(Collections.singletonList(new SimpleGrantedAuthority("USER")));
  }

  private ResultActions callApiWith(ArticleCreateRequestDto articleCreateRequestDto, String accessToken)
      throws Exception {
    String requestBody = objectMapper.writeValueAsString(articleCreateRequestDto);
    return mockMvc.perform(
        post("/api/v1/articles")
            .header("Authorization", "Bearer " + accessToken)
            .content(requestBody)
            .contentType(MediaType.APPLICATION_JSON)
    );
  }

}
