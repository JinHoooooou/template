package org.project.portfolio.auth.login.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.portfolio.PortfolioApplication;
import org.project.portfolio.auth.dto.LoginRequestDto;
import org.project.portfolio.auth.dto.LoginResponseDto;
import org.project.portfolio.global.constants.Message;
import org.project.portfolio.helper.dto.RequestDto;
import org.project.portfolio.user.entity.User;
import org.project.portfolio.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = PortfolioApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class LoginApiTest {

  @LocalServerPort
  private int port;
  @Autowired
  private TestRestTemplate restTemplate;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  public void setUp() {
    User user = RequestDto.validSignUpRequestDto().toEntity(passwordEncoder);
    userRepository.save(user);
  }

  @AfterEach
  public void tearDown() {
    userRepository.deleteAll();
  }

  private String createUrlWithPort(String uri) {
    return "http://localhost:" + port + uri;
  }

  @Test
  @DisplayName("유효한 RequestDto가 주어지고 DB에 해당 리소스가 있을 때, Access Token과 200 OK를 응답한다.")
  public void success_onValidLoginDtoAndPresentResourceInDb_shouldReturn200OkAndAccessToken() {
    // Given: 유효한 LoginRequestDto가 주어진다.
    LoginRequestDto validLoginRequestDto = RequestDto.validLoginRequestDto();

    // When: 로그인 API를 호출한다.
    ResponseEntity<LoginResponseDto> response = restTemplate
        .postForEntity(createUrlWithPort("/api/v1/login"), validLoginRequestDto, LoginResponseDto.class);

    // Then: Status Code는 200 Ok이다.
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    // And: Response Body로 message와 accessToken이 반환된다.
    LoginResponseDto responseBody = response.getBody();
    assertThat(responseBody.getMessage()).isEqualTo(Message.OK);
    assertThat(responseBody.getAccessToken()).isNotEmpty();
  }

  @Test
  @DisplayName("유효하지 않은 RequestDto가 주어질 때, 400 Bad Request를 응답한다.")
  public void fail_onInvalidLoginDto_shouldReturn400BadRequest() {
    // Given: 유효하지 않은 LoginRequestDto가 주어진다.
    LoginRequestDto invalidLoginRequestDto = RequestDto.validLoginRequestDto();
    invalidLoginRequestDto.setEmail("invalid@");
    invalidLoginRequestDto.setPassword("q1w2e3r4");

    // When: 로그인 API를 호출한다.
    ResponseEntity<LoginResponseDto> response = restTemplate
        .postForEntity(createUrlWithPort("/api/v1/login"), invalidLoginRequestDto, LoginResponseDto.class);

    // Then: Status Code는 400 Bad Request이다.
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    // And: Response Body로 message와 detail이 반환된다.
    LoginResponseDto responseBody = response.getBody();
    assertThat(responseBody.getMessage()).isEqualTo(Message.BAD_REQUEST);
    assertThat(responseBody.getDetail()).contains(Message.INVALID_EMAIL, Message.INVALID_PASSWORD);
  }

  @Test
  @DisplayName("RequestDto에 해당하는 리소스가 없을 때, 401 Unauthorized를 응답한다.")
  public void fail_onNoResourceWithLoginDto_shouldReturn401Unauthorized() {
    // Given: DB에 해당 리소스가 없는 유효한 LoginRequestDto가 주어진다.
    LoginRequestDto validLoginRequestDto = RequestDto.validLoginRequestDto();
    validLoginRequestDto.setEmail("notFound123@naver.com");

    // When: 로그인 API를 호출한다.
    ResponseEntity<LoginResponseDto> response = restTemplate
        .postForEntity(createUrlWithPort("/api/v1/login"), validLoginRequestDto, LoginResponseDto.class);

    // Then: Status Code는 401 Unauthorized이다.
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    // And: Response Body로 message와 detail이 반환된다.
    LoginResponseDto responseBody = response.getBody();
    assertThat(responseBody.getMessage()).isEqualTo(Message.UNAUTHORIZED);
    assertThat(responseBody.getDetail()).isEqualTo(Message.NOT_MATCH_WITH_LOGIN_DTO);
  }

  @Test
  @DisplayName("이미 로그인 되어 있을 때, 403 Forbidden을 응답한다.")
  public void fail_onAlreadyLogin_shouldReturn403Forbidden() throws JsonProcessingException {
    // Given: 유효한 LoginRequestDto가 주어진다.
    LoginRequestDto validLoginRequestDto = RequestDto.validLoginRequestDto();
    // And: 로그인 하여 AccessToken을 발급 받는다.
    ResponseEntity<LoginResponseDto> firstLoginResponse = restTemplate.postForEntity(
        createUrlWithPort("/api/v1/login"),
        validLoginRequestDto,
        LoginResponseDto.class
    );
    String accessToken = firstLoginResponse.getBody().getAccessToken();

    // When: 유효한 JWT를 포함하여 로그인 API를 호출한다.
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.set("Content-Type", "application/json");

    ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity(
        createUrlWithPort("/api/v1/login"),
        buildHttpEntity(validLoginRequestDto, headers),
        LoginResponseDto.class
    );

    // Then: Status Code는 403 Forbidden이다.
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    // And: Response Body로 message와 detail이 반환된다.
    LoginResponseDto responseBody = response.getBody();
    assertThat(responseBody.getMessage()).isEqualTo(Message.FORBIDDEN);
    assertThat(responseBody.getDetail()).isEqualTo(Message.ALREADY_LOGIN);
  }

  @Test
  @DisplayName("지원하지 않는 Content-Type일 때, 415 Unsupported Media Type을 응답한다.")
  public void fail_onUnsupportedContentType_shouldReturn415UnsupportedMediaType() throws JsonProcessingException {
    // Given: 유효한 LoginRequestDto가 주어진다.
    LoginRequestDto validLoginRequestDto = RequestDto.validLoginRequestDto();

    // When: 지원하지 않는 Content-Type으로 로그인 API를 호출한다.
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "text/html");
    ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity(
        createUrlWithPort("/api/v1/login"),
        buildHttpEntity(validLoginRequestDto, headers),
        LoginResponseDto.class
    );

    // Then: Status Code는 415 Unsupported Media Type이다.
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    // And: Response Body로 message와 detail이 반환된다.
    LoginResponseDto responseBody = response.getBody();
    assertThat(responseBody.getMessage()).isEqualTo(Message.UNSUPPORTED_MEDIA_TYPE);
    assertThat(responseBody.getDetail()).isEqualTo(Message.ONLY_SUPPORTED_APPLICATION_JSON);
  }

  private HttpEntity<String> buildHttpEntity(LoginRequestDto loginRequestDto, HttpHeaders headers)
      throws JsonProcessingException {
    return new HttpEntity<>(objectMapper.writeValueAsString(loginRequestDto), headers);
  }
}
