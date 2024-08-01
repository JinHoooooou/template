package org.project.portfolio.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {

  private String message;
  @JsonInclude(Include.NON_NULL)
  private String detail;
  @JsonInclude(Include.NON_NULL)
  private String accessToken;
}
