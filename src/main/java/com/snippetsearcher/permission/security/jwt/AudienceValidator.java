package com.snippetsearcher.permission.security.jwt;

import java.util.List;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;

public class AudienceValidator implements OAuth2TokenValidator<Jwt> {
  private final String expectedAudience;

  public AudienceValidator(String expectedAudience) {
    this.expectedAudience = expectedAudience;
  }

  @Override
  public OAuth2TokenValidatorResult validate(Jwt token) {
    List<String> aud = token.getAudience();
    if (aud != null && aud.contains(expectedAudience)) {
      return OAuth2TokenValidatorResult.success();
    }
    return OAuth2TokenValidatorResult.failure(
        new OAuth2Error("invalid_token", "Invalid audience", null));
  }
}
