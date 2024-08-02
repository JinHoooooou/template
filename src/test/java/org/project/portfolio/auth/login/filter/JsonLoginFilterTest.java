package org.project.portfolio.auth.login.filter;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.portfolio.auth.dto.AuthenticatedUser;
import org.project.portfolio.auth.dto.LoginRequestDto;
import org.project.portfolio.auth.service.AuthService;
import org.project.portfolio.global.constants.Message;
import org.project.portfolio.helper.dto.RequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class JsonLoginFilterTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private AuthService authService;
  @MockBean
  private PasswordEncoder passwordEncoder;

  @Test
  @DisplayName("LoginRequestDto의 필드가 모두 유효할 때, 200 Ok과 Access Token을 응답해야한다.")
  public void success_onValidLoginRequestDto_shouldReturn200OkAndAccessToken() throws Exception {
    // Given: 유효한 LoginRequestDto가 주어진다.
    LoginRequestDto validLoginRequestDto = RequestDto.validLoginRequestDto();
    // Mocking
    AuthenticatedUser mockUser = new AuthenticatedUser(
        org.project.portfolio.user.entity.User.builder()
            .email(validLoginRequestDto.getEmail())
            .password(validLoginRequestDto.getPassword())
            .build()
    );
    when(authService.loadUserByUsername(validLoginRequestDto.getEmail())).thenReturn(mockUser);
    when(passwordEncoder.matches(any(), any())).thenReturn(true);

    // When: Login API를 호출한다.
    ResultActions resultActions = callApiWith(validLoginRequestDto, MediaType.APPLICATION_JSON);

    // Then: Status는 200 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 message와 accessToken을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.OK)));
    resultActions.andExpect(jsonPath("$.accessToken").exists());
  }

  @Test
  @DisplayName("LoginRequestDto가 유효하지 않을 때, 400 Bad Request를 응답해야한다.")
  public void fail_onInvalidEmailInLoginRequestDto_shouldReturn400BadRequest() throws Exception {
    // Given: 유효하지 않은 LoginRequestDto가 주어진다.
    LoginRequestDto invalidLoginRequestDto = RequestDto.validLoginRequestDto();
    invalidLoginRequestDto.setEmail("test@");
    invalidLoginRequestDto.setPassword("q1w2e3r4");

    // When: Login API를 호출한다.
    ResultActions resultActions = callApiWith(invalidLoginRequestDto, MediaType.APPLICATION_JSON);

    // Then: Status는 400 Bad Request이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.BAD_REQUEST)));
    resultActions.andExpect(jsonPath("$.detail", containsString(Message.INVALID_EMAIL)));
    resultActions.andExpect(jsonPath("$.detail", containsString(Message.INVALID_PASSWORD)));
  }

  @Test
  @DisplayName("email에 대한 리소스가 없을 때, 401 Unauthorized를 응답해야한다.")
  public void fail_onNoResourceWithEmail_shouldReturn401Unauthorized() throws Exception {
    // Given: 유효한 LoginRequestDto가 주어진다.
    LoginRequestDto validLoginRequestDto = RequestDto.validLoginRequestDto();
    // Mocking
    when(authService.loadUserByUsername(validLoginRequestDto.getEmail()))
        .thenThrow(new UsernameNotFoundException(Message.NOT_MATCH_WITH_LOGIN_DTO));

    // When: Login API를 호출한다.
    ResultActions resultActions = callApiWith(validLoginRequestDto, MediaType.APPLICATION_JSON);

    // Then: Status는 401 Unauthorized이다.
    resultActions.andExpect(status().isUnauthorized());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.UNAUTHORIZED)));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_MATCH_WITH_LOGIN_DTO)));
  }

  @Test
  @DisplayName("password가 리소스와 일치하지 않을 때, 401 Unauthroized를 응답해야한다.")
  public void fail_onNotEqualsPasswordWithResource_shouldReturn401Unauthorized() throws Exception {
    // Given: 유효한 LoginRequestDto가 주어진다.
    LoginRequestDto validLoginRequestDto = RequestDto.validLoginRequestDto();
    validLoginRequestDto.setPassword("q1w2e3r4t5!!");
    // Mocking
    User mockUser = new User(
        validLoginRequestDto.getEmail(),
        "q1w2e3r4t5!@",
        Collections.singleton(new SimpleGrantedAuthority("USER"))
    );
    when(authService.loadUserByUsername(validLoginRequestDto.getEmail())).thenReturn(mockUser);
    when(passwordEncoder.matches(any(), any())).thenReturn(false);

    // When: Login API를 호출한다.
    ResultActions resultActions = callApiWith(validLoginRequestDto, MediaType.APPLICATION_JSON);

    // Then: Status는 401 Unauthorized이다.
    resultActions.andExpect(status().isUnauthorized());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.UNAUTHORIZED)));
  }

  @Test
  @DisplayName("이미 로그인되어 있을 때, 403 Forbidden을 응답해야한다.")
  public void fail_onAlreadyLogin_shouldReturn403Forbidden() throws Exception {
    // Given: 유효한 LoginRequestDto가 주어진다.
    LoginRequestDto validLoginRequestDto = RequestDto.validLoginRequestDto();
    // Mocking
    User mockUser = new User(
        validLoginRequestDto.getEmail(),
        validLoginRequestDto.getPassword(),
        Collections.singleton(new SimpleGrantedAuthority("USER"))
    );
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities()));

    // When: Login API를 호출한다.
    ResultActions resultActions = callApiWith(validLoginRequestDto, MediaType.APPLICATION_JSON);

    // Then: Status는 403 Forbidden이다.
    resultActions.andExpect(status().isForbidden());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.FORBIDDEN)));
    resultActions.andExpect(jsonPath("$.detail", containsString(Message.ALREADY_LOGIN)));
  }

  @Test
  @DisplayName("Content-Type이 application/json이 아닐 때, 415 Unsupported Media Type을 응답해야한다.")
  public void fail_onUnsupportedMediaTypeInHeader_shouldReturn415UnsupportedMediaType() throws Exception {
    // Given: 유효한 LoginRequestDto가 주어진다.
    LoginRequestDto validLoginRequestDto = RequestDto.validLoginRequestDto();

    // When: Login API를 "application/json"이 아닌 다른 Content-Type으로 호출한다.
    ResultActions resultActions = callApiWith(validLoginRequestDto, MediaType.TEXT_PLAIN);

    // Then: Status는 415 Unsupported Media Type이다.
    resultActions.andExpect(status().isUnsupportedMediaType());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.UNSUPPORTED_MEDIA_TYPE)));
    resultActions.andExpect(jsonPath("$.detail", containsString(Message.ONLY_SUPPORTED_APPLICATION_JSON)));
  }

  private ResultActions callApiWith(LoginRequestDto validLoginRequestDto, MediaType contentType) throws Exception {
    String requestBody = objectMapper.writeValueAsString(validLoginRequestDto);
    return mockMvc.perform(
        post("/api/v1/login")
            .content(requestBody)
            .contentType(contentType)
    );
  }
}
