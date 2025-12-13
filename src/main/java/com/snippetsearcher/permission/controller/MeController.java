package com.snippetsearcher.permission.controller;

import com.snippetsearcher.permission.dto.SnippetPermissionResponse;
import com.snippetsearcher.permission.model.UserAccount;
import com.snippetsearcher.permission.service.PermissionService;
import com.snippetsearcher.permission.service.UserProvisioningService;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
public class MeController {
  private final UserProvisioningService provisioning;
  private final PermissionService permissions;

  public MeController(UserProvisioningService provisioning, PermissionService permissionService) {
    this.provisioning = provisioning;
    this.permissions = permissionService;
  }

  @PostMapping("/sync")
  public UserAccount sync(@AuthenticationPrincipal Jwt jwt) {
    return provisioning.ensureUser(jwt);
  }

  @GetMapping
  public UserAccount me(@AuthenticationPrincipal Jwt jwt) {
    return provisioning.ensureUser(jwt);
  }

  @GetMapping("/snippets")
  public List<SnippetPermissionResponse> listMySnippets(@AuthenticationPrincipal Jwt jwt) {
    UserAccount user = provisioning.ensureUser(jwt);
    return permissions.listByUser(user.getId()).stream()
        .map(SnippetPermissionResponse::from)
        .toList();
  }
}
