package org.project.portfolio.article.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.portfolio.article.dto.ArticleCreateRequestDto;
import org.project.portfolio.article.dto.ArticleCreateResponseDto;
import org.project.portfolio.article.service.ArticleService;
import org.project.portfolio.global.constants.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
public class ArticleController {

  private final ArticleService articleService;

  @PostMapping("")
  public ResponseEntity<ArticleCreateResponseDto> create(
      @Valid @RequestBody ArticleCreateRequestDto articleCreateRequestDto) {
    Authentication client = SecurityContextHolder.getContext().getAuthentication();
    String email = String.valueOf(client.getPrincipal());

    articleService.save(email, articleCreateRequestDto);
    ArticleCreateResponseDto responseBody = ArticleCreateResponseDto.builder()
        .message(Message.CREATED)
        .detail(Message.SUCCESS_CREATE)
        .build();

    return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
  }

}
