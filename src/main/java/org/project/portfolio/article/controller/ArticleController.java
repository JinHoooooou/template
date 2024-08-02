package org.project.portfolio.article.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.project.portfolio.article.dto.ArticleCreateRequestDto;
import org.project.portfolio.global.constants.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/articles")
public class ArticleController {

  @PostMapping("")
  public ResponseEntity<Map> create(@Valid @RequestBody ArticleCreateRequestDto articleCreateRequestDto) {
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("message", Message.CREATED);

    return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
  }

}
