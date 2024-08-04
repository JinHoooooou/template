package org.project.portfolio.article.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.portfolio.PortfolioApplication;
import org.project.portfolio.article.dto.ArticleCreateRequestDto;
import org.project.portfolio.article.dto.ArticleCreateResponseDto;
import org.project.portfolio.article.entity.Article;
import org.project.portfolio.article.repository.ArticleRepository;
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
public class CreateArticleApiTest {

  private static final String LOGIN_API_URL = "/api/v1/login";
  private static final String CREATE_ARTICLE_API_URL = "/api/v1/articles";

  private User user;

  @LocalServerPort
  private int port;
  @Autowired
  private TestRestTemplate restTemplate;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ArticleRepository articleRepository;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  public void setUp() {
    User user = RequestDto.validSignUpRequestDto().toEntity(passwordEncoder);
    this.user = userRepository.save(user);
    articleRepository.deleteAll();
  }

  @AfterEach
  public void tearDown() {
    articleRepository.deleteAll();
    userRepository.deleteAll();
  }

  private String createUrlWithPort(String uri) {
    return "http://localhost:" + port + uri;
  }

  @Test
  @DisplayName("로그인 상태이고, 유효한 RequestDto가 주어질 때, Article Entity를 저장하고 201 Created를 응답한다.")
  public void success_onLoggedInAndValidRequestDto_shouldSaveArticleEntityAndReturn201Created()
      throws JsonProcessingException {
    // Given: 유효한 ArticleCreateRequestDto가 주어진다.
    ArticleCreateRequestDto validArticleCreateRequestDto = RequestDto.validArticleCreateRequestDto();
    // And: 로그인 API 요청으로 Access Token을 발급받는다.
    LoginRequestDto validLoginRequestDto = RequestDto.validLoginRequestDto();
    ResponseEntity<LoginResponseDto> loginApiResponse = restTemplate
        .postForEntity(createUrlWithPort(LOGIN_API_URL), validLoginRequestDto, LoginResponseDto.class);
    String accessToken = loginApiResponse.getBody().getAccessToken();

    // When: accessToken을 헤더에 담아 게시글 생성 API를 호출한다.
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.set("Content-Type", "application/json");
    String requestBody = objectMapper.writeValueAsString(validArticleCreateRequestDto);
    HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);
    ResponseEntity<ArticleCreateResponseDto> response = restTemplate
        .postForEntity(createUrlWithPort(CREATE_ARTICLE_API_URL), httpEntity, ArticleCreateResponseDto.class);

    // Then: Status Code는 201 Created이다.
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    // And: Response Body로 message와 detail이 반환된다.
    ArticleCreateResponseDto responseBody = response.getBody();
    assertThat(responseBody.getMessage()).isEqualTo(Message.CREATED);
    assertThat(responseBody.getDetail()).isEqualTo(Message.SUCCESS_CREATE);
    // And: assert Article Entity
    assertArticle(validArticleCreateRequestDto);
  }

  @Test
  @DisplayName("로그인 상태이고, 유효하지 않은 RequestDto가 주어질 때, Article Entity를 저장하지 않고 400 BadRequest를 응답한다.")
  public void fail_onLoggedInAndInvalidRequestDto_shouldNotSaveArticleEntityAndReturn400BadRequest()
      throws JsonProcessingException {
    // Given: 유효하지않은 ArticleCreateRequestDto가 주어진다.
    ArticleCreateRequestDto invalidArticleCreateRequestDto = RequestDto.validArticleCreateRequestDto();
    invalidArticleCreateRequestDto.setTitle("           ");
    invalidArticleCreateRequestDto.setContents("             ");
    // And: 로그인 API 요청으로 Access Token을 발급받는다.
    LoginRequestDto validLoginRequestDto = RequestDto.validLoginRequestDto();
    ResponseEntity<LoginResponseDto> loginApiResponse = restTemplate
        .postForEntity(createUrlWithPort(LOGIN_API_URL), validLoginRequestDto, LoginResponseDto.class);
    String accessToken = loginApiResponse.getBody().getAccessToken();

    // When: accessToken을 헤더에 담아 게시글 생성 API를 호출한다.
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.set("Content-Type", "application/json");
    String requestBody = objectMapper.writeValueAsString(invalidArticleCreateRequestDto);
    HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);
    ResponseEntity<Map> response = restTemplate
        .postForEntity(createUrlWithPort(CREATE_ARTICLE_API_URL), httpEntity, Map.class);

    // Then: Status Code는 400 BadRequest이다.
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    // And: Response Body로 message와 details가 반환된다.
    Map<String, Object> responseBody = response.getBody();
    Map<String, String> details = (Map<String, String>) responseBody.get("details");
    assertThat(responseBody.get("message")).isEqualTo(Message.BAD_REQUEST);
    assertThat(details.get("title")).isEqualTo(Message.INVALID_TITLE);
    assertThat(details.get("contents")).isEqualTo(Message.INVALID_CONTENTS);
    assertThat(articleRepository.findAll()).hasSize(0);
  }

  private void assertArticle(ArticleCreateRequestDto validArticleCreateRequestDto) {
    Article article = articleRepository.findById(1L).get();
    assertThat(article.getTitle()).isEqualTo(validArticleCreateRequestDto.getTitle());
    assertThat(article.getContents()).isEqualTo(validArticleCreateRequestDto.getContents());
    assertThat(article.getCreatedDate().toLocalDate()).isEqualTo(LocalDate.now());
    assertThat(article.getLastModifiedDate().toLocalDate()).isEqualTo(LocalDate.now());
    assertThat(article.getWriter().getId()).isEqualTo(this.user.getId());
  }
}
