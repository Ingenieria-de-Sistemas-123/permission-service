package com.snippetsearcher.permission.controller;

import com.snippetsearcher.permission.model.UserAccount;
import com.snippetsearcher.permission.service.UserProvisioningService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
public class MeController {
  private final UserProvisioningService provisioning;

  public MeController(UserProvisioningService provisioning) {
    this.provisioning = provisioning;
  }

  @PostMapping("/sync")
  public UserAccount sync(@AuthenticationPrincipal Jwt jwt) {
    return provisioning.ensureUser(jwt);
  }

  @GetMapping
  public UserAccount me(@AuthenticationPrincipal Jwt jwt) {
    return provisioning.ensureUser(jwt);
  }
}
