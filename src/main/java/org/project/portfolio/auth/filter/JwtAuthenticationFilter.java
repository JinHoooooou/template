package org.project.portfolio.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.project.portfolio.auth.service.JwtService;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String accessToken = jwtService.extractAccessToken(request);
      jwtService.validate(accessToken);

      String email = jwtService.extractEmail(accessToken);
      List<SimpleGrantedAuthority> authorities = jwtService.extractAuthorities(accessToken);
      setSecurityContextHolder(email, authorities);
    } catch (Exception e) {
      request.setAttribute("exception", new AuthenticationServiceException(e.getLocalizedMessage()));
    }
    filterChain.doFilter(request, response);
  }

  private void setSecurityContextHolder(String email, List<SimpleGrantedAuthority> authorities) {
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(email, null, authorities)
    );
  }
}
