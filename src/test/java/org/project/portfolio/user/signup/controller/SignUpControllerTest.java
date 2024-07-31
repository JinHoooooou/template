package org.project.portfolio.user.signup.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.portfolio.global.config.SecurityConfig;
import org.project.portfolio.global.constants.Message;
import org.project.portfolio.helper.dto.RequestDto;
import org.project.portfolio.user.controller.SignUpController;
import org.project.portfolio.user.dto.SignUpRequestDto;
import org.project.portfolio.user.service.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(SignUpController.class)
@Import(SecurityConfig.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class SignUpControllerTest {

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private SignUpService signUpService;

  @Test
  @DisplayName("SignUpRequestDto의 필드가 모두 유효할 때, 201 Created를 응답해야한다.")
  public void success_onValidSignUpRequestDto_shouldReturn201Created() throws Exception {
    // Given: 유효한 SignUpRequestDto가 주어진다.
    SignUpRequestDto validSignUpRequestDto = RequestDto.validSignUpRequestDto();

    // When: SignUp API를 호출한다.
    ResultActions resultActions = callApiWith(validSignUpRequestDto);

    // Then: Status는 201이다.
    resultActions.andExpect(status().isCreated());
    // And: Response Body로 message를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.CREATED)));
  }

  @Test
  @DisplayName("userId가 유효하지 않을 때, 400 Bad Request를 응답해야한다.")
  public void fail_onInvalidUserIdInSignUpRequestDto_shouldReturn400BadRequest() throws Exception {
    // Given: 유효하지 않은 userId가 주어진다.
    SignUpRequestDto invalidSignUpRequestDto = RequestDto.validSignUpRequestDto();
    invalidSignUpRequestDto.setUserId("invalid23");

    // When: SignUp API를 호출한다.
    ResultActions resultActions = callApiWith(invalidSignUpRequestDto);

    // Then: Status는 400이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.BAD_REQUEST)));
    resultActions.andExpect(jsonPath("$.details.userId", is(Message.INVALID_USER_ID)));
  }

  @Test
  @DisplayName("password가 유효하지 않을 때, 400 Bad Request를 응답해야한다.")
  public void fail_onInvalidPasswordInSignUpRequestDto_shouldReturn400BadRequest() throws Exception {
    // Given: 유효하지 않은 password가 주어진다.
    SignUpRequestDto invalidSignUpRequestDto = RequestDto.validSignUpRequestDto();
    invalidSignUpRequestDto.setPassword("q1w2e3r4!");

    // When: SignUp API를 호출한다.
    ResultActions resultActions = callApiWith(invalidSignUpRequestDto);

    // Then: Status는 400이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.BAD_REQUEST)));
    resultActions.andExpect(jsonPath("$.details.password", is(Message.INVALID_PASSWORD)));
  }

  @Test
  @DisplayName("username이 유효하지 않을 때, 400 Bad Request를 응답해야한다.")
  public void fail_onInvalidUsernameInSignUpRequestDto_shouldReturn400BadRequest() throws Exception {
    // Given: 유효하지 않은 username가 주어진다.
    SignUpRequestDto invalidSignUpRequestDto = RequestDto.validSignUpRequestDto();
    invalidSignUpRequestDto.setUsername("ㅈㅎ");

    // When: SignUp API를 호출한다.
    ResultActions resultActions = callApiWith(invalidSignUpRequestDto);

    // Then: Status는 400이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.BAD_REQUEST)));
    resultActions.andExpect(jsonPath("$.details.username", is(Message.INVALID_USERNAME)));
  }

  @Test
  @DisplayName("email이 유효하지 않을 때, 400 Bad Request를 응답해야한다.")
  public void fail_onInvalidEmailInSignUpRequestDto_shouldReturn400BadRequest() throws Exception {
    // Given: 유효하지 않은 email이 주어진다.
    SignUpRequestDto invalidSignUpRequestDto = RequestDto.validSignUpRequestDto();
    invalidSignUpRequestDto.setEmail("jinho@na");

    // When: SignUp API를 호출한다.
    ResultActions resultActions = callApiWith(invalidSignUpRequestDto);

    // Then: Status는 400이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.BAD_REQUEST)));
    resultActions.andExpect(jsonPath("$.details.email", is(Message.INVALID_EMAIL)));
  }

  @Test
  @DisplayName("phone이 유효하지 않을 때, 400 Bad Request를 응답해야한다.")
  public void fail_onInvalidPhoneInSignUpRequestDto_shouldReturn400BadRequest() throws Exception {
    // Given: 유효하지 않은 phone이 주어진다.
    SignUpRequestDto invalidSignUpRequestDto = RequestDto.validSignUpRequestDto();
    invalidSignUpRequestDto.setPhone("01012345678");

    // When: SignUp API를 호출한다.
    ResultActions resultActions = callApiWith(invalidSignUpRequestDto);

    // Then: Status는 400이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.BAD_REQUEST)));
    resultActions.andExpect(jsonPath("$.details.phone", is(Message.INVALID_PHONE)));
  }

  @Test
  @DisplayName("두 개 이상의 필드가 유효하지 않을 때, details도 그 수만큼 Response Body로 응답한다.")
  public void fail_onSeveralInvalidFieldInSignUpRequestDto_shouldReturnDetailsAsWell() throws Exception {
    // Given: 유효하지 않은 SignUpRequestDto가 주어진다.
    SignUpRequestDto invalidSignUpRequestDto = RequestDto.validSignUpRequestDto();
    invalidSignUpRequestDto.setUserId("invalid23");
    invalidSignUpRequestDto.setPassword("q1w2e3r4!");
    invalidSignUpRequestDto.setUsername("ㅈㅎ");
    invalidSignUpRequestDto.setEmail("jinho@na");
    invalidSignUpRequestDto.setPhone("01012345678");

    // When: SignUp API를 호출한다.
    ResultActions resultActions = callApiWith(invalidSignUpRequestDto);

    // Then: Status는 400이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.BAD_REQUEST)));
    resultActions.andExpect(jsonPath("$.details.userId", is(Message.INVALID_USER_ID)));
    resultActions.andExpect(jsonPath("$.details.password", is(Message.INVALID_PASSWORD)));
    resultActions.andExpect(jsonPath("$.details.username", is(Message.INVALID_USERNAME)));
    resultActions.andExpect(jsonPath("$.details.email", is(Message.INVALID_EMAIL)));
    resultActions.andExpect(jsonPath("$.details.phone", is(Message.INVALID_PHONE)));
  }

  private ResultActions callApiWith(SignUpRequestDto signUpRequestDto) throws Exception {
    String requestBody = objectMapper.writeValueAsString(signUpRequestDto);
    return mockMvc.perform(MockMvcRequestBuilders
        .post("/api/v1/signup")
        .content(requestBody)
        .contentType(MediaType.APPLICATION_JSON)
    );
  }

}
