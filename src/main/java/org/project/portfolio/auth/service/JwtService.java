package org.project.portfolio.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.project.portfolio.auth.dto.AuthenticatedUser;
import org.project.portfolio.user.entity.User;
import org.project.portfolio.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class JwtService {
  private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
  private static final String CLAIM_EMAIL = "email";
  private static final String CLAIM_AUTHORITIES = "auth";
  private static final String BEARER = "Bearer ";

  @Value("${jwt.secretKey}")
  private String secretKey;
  @Value("${jwt.access.header}")
  private String accessHeader;
  @Value("${jwt.access.expiration}")
  private Long accessTokenExpiration;

  public String createAccessToken(String email, String authorities) {
    return Jwts.builder()
        .subject(ACCESS_TOKEN_SUBJECT)
        .claim(CLAIM_EMAIL, email)
        .claim(CLAIM_AUTHORITIES, authorities)
        .expiration(new Date(new Date().getTime() + accessTokenExpiration))
        .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
        .compact();
  }

  public String createAccessToken(Authentication authentication) {
    AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
    String email = authenticatedUser.getUsername();
    String authorities = authenticatedUser.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    return this.createAccessToken(email, authorities);
  }

  public String extractAccessToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(accessHeader);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
      return bearerToken.replace(BEARER, "");
    }
    return null;
  }

  public void validate(String accessToken) {
    try {
      Jwts.parser()
          .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
          .build()
          .parseSignedClaims(accessToken);
    } catch (SignatureException e) {
      throw new JwtException("Invalid JWT signature");
    } catch (MalformedJwtException e) {
      throw new JwtException("Invalid JWT");
    } catch (ExpiredJwtException e) {
      throw new JwtException("Expired JWT");
    } catch (UnsupportedJwtException e) {
      throw new JwtException("Unsupported JWT");
    } catch (IllegalArgumentException e) {
      throw new JwtException("JWT compact of handler are invalid");
    } catch (NullPointerException e) {
      throw new JwtException("missed JWT");
    }
  }

  public String extractEmail(String accessToken) {
    return Jwts.parser()
        .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
        .build()
        .parseSignedClaims(accessToken)
        .getPayload()
        .get(CLAIM_EMAIL, String.class);
  }

  public List<SimpleGrantedAuthority> extractAuthorities(String accessToken) {
    String authorities = Jwts.parser()
        .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
        .build()
        .parseSignedClaims(accessToken)
        .getPayload()
        .get(CLAIM_AUTHORITIES, String.class);
    return Arrays.stream(authorities.split(", "))
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }
}
