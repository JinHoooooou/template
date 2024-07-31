package org.project.portfolio.user.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.project.portfolio.global.constants.Message;
import org.project.portfolio.user.dto.SignUpRequestDto;
import org.project.portfolio.user.service.SignUpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SignUpController {

  private final SignUpService signUpService;

  @PostMapping("/signup")
  public ResponseEntity<Map> signup(@Valid @RequestBody SignUpRequestDto requestDto) {
    signUpService.signUp(requestDto);
    Map<String, Object> response = new HashMap<>();
    response.put("message", Message.CREATED);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
