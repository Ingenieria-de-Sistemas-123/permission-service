package com.snippetsearcher.permission.controller;

import com.snippetsearcher.permission.dto.AuthorizationRequest;
import com.snippetsearcher.permission.dto.AuthorizationResponse;
import com.snippetsearcher.permission.service.AuthorizationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permissions")
public class AuthorizationController {

  private final AuthorizationService service;

  public AuthorizationController(AuthorizationService service) {
    this.service = service;
  }

  @PostMapping("/authorize")
  public AuthorizationResponse authorize(
      @AuthenticationPrincipal Jwt jwt, @RequestBody AuthorizationRequest req) {
    boolean allowed = service.isAllowed(jwt, req.action(), req.snippetId(), req.resourceOwnerSub());
    return new AuthorizationResponse(allowed, allowed ? "ok" : "denied");
  }
}
