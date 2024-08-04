package org.project.portfolio.article.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.project.portfolio.user.entity.User;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Article {

  @Id
  @Column(name = "article_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String title;
  @Column(nullable = false, length = 2000)
  private String contents;
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User writer;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdDate;
  @UpdateTimestamp
  private LocalDateTime lastModifiedDate;

  @Builder
  public Article(User writer, String title, String contents) {
    this.writer = writer;
    this.title = title;
    this.contents = contents;
  }

}
