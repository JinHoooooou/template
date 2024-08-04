package org.project.portfolio.article.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.portfolio.article.entity.Article;
import org.project.portfolio.global.constants.Message;
import org.project.portfolio.user.entity.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCreateRequestDto {

  @NotBlank(message = Message.INVALID_TITLE)
  @Size(min = 1, max = 200, message = Message.INVALID_TITLE)
  private String title;
  @NotBlank(message = Message.INVALID_CONTENTS)
  @Size(min = 1, max = 1000, message = Message.INVALID_CONTENTS)
  private String contents;

}
