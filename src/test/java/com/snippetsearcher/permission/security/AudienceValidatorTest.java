package com.snippetsearcher.permission.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.snippetsearcher.permission.security.jwt.AudienceValidator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

class AudienceValidatorTest {

  @Test
  void validate_success_when_audience_contains_expected() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getAudience()).thenReturn(List.of("a", "b", "expected"));

    AudienceValidator v = new AudienceValidator("expected");

    var res = v.validate(jwt);

    assertFalse(res.hasErrors());
    assertTrue(res.getErrors().isEmpty());
  }

  @Test
  void validate_failure_when_audience_missing_or_null() {
    AudienceValidator v = new AudienceValidator("expected");

    Jwt jwt1 = mock(Jwt.class);
    when(jwt1.getAudience()).thenReturn(List.of("a", "b"));

    var r1 = v.validate(jwt1);
    assertTrue(r1.hasErrors());
    assertFalse(r1.getErrors().isEmpty());

    Jwt jwt2 = mock(Jwt.class);
    when(jwt2.getAudience()).thenReturn(null);

    var r2 = v.validate(jwt2);
    assertTrue(r2.hasErrors());
    assertFalse(r2.getErrors().isEmpty());
  }
}
