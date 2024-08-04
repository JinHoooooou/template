package org.project.portfolio.article.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.portfolio.article.dto.ArticleCreateRequestDto;
import org.project.portfolio.article.entity.Article;
import org.project.portfolio.article.repository.ArticleRepository;
import org.project.portfolio.global.constants.Message;
import org.project.portfolio.helper.dto.RequestDto;
import org.project.portfolio.user.entity.User;
import org.project.portfolio.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private ArticleRepository articleRepository;
  @Mock
  private BCryptPasswordEncoder passwordEncoder;
  @InjectMocks
  private ArticleService articleService;

  @Test
  @DisplayName("유효한 사용자 정보와 유효한 RequestDto가 주어질 때, Article Entity를 반환한다.")
  public void success_onValidUserInfoAndValidRequestDto_shouldReturnArticleEntity() {
    // Given: 유효한 사용자 email이 주어진다.
    String validEmail = "test@test.kr";
    // And: 유효한 ArticleCreateRequestDto가 주어진다.
    ArticleCreateRequestDto validArticleCreateRequestDto = RequestDto.validArticleCreateRequestDto();
    // Mocking
    User user = RequestDto.validSignUpRequestDto().toEntity(passwordEncoder);
    when(userRepository.findByEmail(validEmail)).thenReturn(Optional.of(user));

    // When: save()를 호출한다.
    articleService.save(validEmail, validArticleCreateRequestDto);

    // Then: ArticleRepository의 save()가 정상적으로 호출된다.
    verify(articleRepository, times(1)).save(any(Article.class));
  }

  @Test
  @DisplayName("사용자 정보가 유효하지 않을 때, Exception이 발생한다.")
  public void fail_onInvalidUserInfo_shouldThrowException() {
    // Given: 유효한 ArticleCreateRequestDto가 주어진다.
    ArticleCreateRequestDto validArticleCreateRequestDto = RequestDto.validArticleCreateRequestDto();
    // Mocking
    when(userRepository.findByEmail(any()))
        .thenThrow(new UsernameNotFoundException(Message.NOT_FOUND_USER));

    // When: save()를 호출한다.
    // Then: UsernameNotFoundException이 발생한다.
    assertThatThrownBy(() -> articleService.save("notFound@test.kr", validArticleCreateRequestDto))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessage(Message.NOT_FOUND_USER);

    // Then: ArticleRepository의 save()가 호출되지 않는다.
    verify(articleRepository, never()).save(any(Article.class));
  }
}
