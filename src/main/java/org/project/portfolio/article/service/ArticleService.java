package org.project.portfolio.article.service;

import lombok.RequiredArgsConstructor;
import org.project.portfolio.article.dto.ArticleCreateRequestDto;
import org.project.portfolio.article.repository.ArticleRepository;
import org.project.portfolio.global.constants.Message;
import org.project.portfolio.user.entity.User;
import org.project.portfolio.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {

  private final ArticleRepository articleRepository;
  private final UserRepository userRepository;

  public void save(String email, ArticleCreateRequestDto articleCreateRequestDto) {
    User user = userRepository.findByEmail(email).orElseThrow(
        () -> new UsernameNotFoundException(Message.NOT_FOUND_USER)
    );
    articleRepository.save(articleCreateRequestDto.toEntity(user));
  }
}
