package org.project.portfolio.user.signup.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.portfolio.PortfolioApplication;
import org.project.portfolio.global.config.SecurityConfig;
import org.project.portfolio.global.constants.Message;
import org.project.portfolio.helper.dto.RequestDto;
import org.project.portfolio.user.dto.SignUpRequestDto;
import org.project.portfolio.user.entity.User;
import org.project.portfolio.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = PortfolioApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class SignUpApiTest {

  @LocalServerPort
  private int port;
  @Autowired
  private TestRestTemplate restTemplate;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  public void setUp() {
    userRepository.deleteAll();
  }

  @AfterEach
  public void tearDown() {
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("유효한 RequestDto가 주어지고 DB에 중복된 리소스가 없을 때, User Entity가 DB에 저장되고 201 Created를 응답한다.")
  public void success_onValidRequestDtoAndNoDuplicateResource_shouldReturn201CreatedAndSaveUserEntityInDB()
      throws JsonProcessingException {
    // Given: 유효한 SignUpRequestDto가 주어진다.
    SignUpRequestDto validSignUpRequestDto = RequestDto.validSignUpRequestDto();

    // When: 회원 가입 API를 호출한다.
    ResponseEntity<String> response = restTemplate
        .postForEntity(createUrlWithPort("/api/v1/signup"), validSignUpRequestDto, String.class);

    // Then: Status Code는 201 Created이다.
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    // And: Response Body로 메시지가 반환된다.
    Map<String, String> responseBody = objectMapper.readValue(response.getBody(), Map.class);
    assertThat(responseBody.get("message")).isEqualTo(Message.CREATED);
    // And: DB에 User Entity가 저장된다.
    assertThat(userRepository.existsByEmail(validSignUpRequestDto.getEmail())).isTrue();
  }

  @Test
  @DisplayName("유효하지 않은 RequestDto가 주어질 때, User Entity가 DB에 저장되지 않고 400 Bad Request를 응답한다.")
  public void fail_onInvalidRequestDto_shouldReturn400BadRequestAndNotSaveUserEntityInDB()
      throws JsonProcessingException {
    // Given: 유효하지 않은 SignUpRequestDto가 주어진다.
    SignUpRequestDto invalidSignUpRequestDto = RequestDto.validSignUpRequestDto();
    invalidSignUpRequestDto.setUserId("invalid12");
    invalidSignUpRequestDto.setPassword("q1w2e3r4");
    invalidSignUpRequestDto.setUsername("ㅈㅎ");
    invalidSignUpRequestDto.setEmail("jinho@");
    invalidSignUpRequestDto.setPhone("01012345678");

    // When: 회원 가입 API를 호출한다.
    ResponseEntity<String> response = restTemplate
        .postForEntity(createUrlWithPort("/api/v1/signup"), invalidSignUpRequestDto, String.class);

    // Then: Status Code는 400 Bad Request이다.
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    // And: Response Body로 메시지가 반환된다.
    Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
    Map<String, String> details = (Map<String, String>) responseBody.get("details");
    assertThat(responseBody.get("message")).isEqualTo(Message.BAD_REQUEST);
    assertThat(details.get("userId")).isEqualTo(Message.INVALID_USER_ID);
    assertThat(details.get("password")).isEqualTo(Message.INVALID_PASSWORD);
    assertThat(details.get("username")).isEqualTo(Message.INVALID_USERNAME);
    assertThat(details.get("email")).isEqualTo(Message.INVALID_EMAIL);
    assertThat(details.get("phone")).isEqualTo(Message.INVALID_PHONE);
    // And: DB에 User Entity가 저장되지 않는다.
    assertThat(userRepository.existsByEmail(invalidSignUpRequestDto.getEmail())).isFalse();
  }

  @Test
  @DisplayName("이미 중복된 resource가 DB에 있을 때, User Entity가 DB에 저장되지 않고 409 Conflict를 응답한다.")
  public void fail_onDuplicateResourceInDB_shouldReturn409ConflictAndNotSaveUserEntityInDB()
      throws JsonProcessingException {
    // Given: 유효한 SignUpRequestDto가 주어진다.
    SignUpRequestDto validSignUpRequestDto = RequestDto.validSignUpRequestDto();
    // And: 중복된 리소스의 User Entity를 DB에 저장한다.
    User savedUser = User.builder()
        .userId(validSignUpRequestDto.getUserId())
        .password(validSignUpRequestDto.getPassword())
        .username(validSignUpRequestDto.getUsername())
        .email(validSignUpRequestDto.getEmail())
        .phone(validSignUpRequestDto.getPhone())
        .build();
    userRepository.save(savedUser);
    userRepository.flush();

    // When: 회원 가입 API를 호출한다.
    ResponseEntity<String> response = restTemplate
        .postForEntity(createUrlWithPort("/api/v1/signup"), validSignUpRequestDto, String.class);

    // Then: Status Code는 409 Conflict이다.
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    // And: Response Body로 메시지가 반환된다.
    Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
    assertThat(responseBody.get("message")).isEqualTo(Message.CONFLICT);
    assertThat(responseBody.get("detail")).isEqualTo(Message.DUPLICATE_EMAIL);
    // And: DB에 User Entity가 저장되지 않는다.
    assertThat(userRepository.findAll()).hasSize(1);
  }

  private String createUrlWithPort(String uri) {
    return "http://localhost:" + port + uri;
  }

}
