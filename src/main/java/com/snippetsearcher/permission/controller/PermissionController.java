package com.snippetsearcher.permission.controller;

import com.snippetsearcher.permission.dto.AuthorResponse;
import com.snippetsearcher.permission.dto.CreatePermissionRequest;
import com.snippetsearcher.permission.dto.PermissionResponse;
import com.snippetsearcher.permission.model.Permission;
import com.snippetsearcher.permission.model.PermissionType;
import com.snippetsearcher.permission.service.PermissionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

  private final PermissionService service;

  public PermissionController(PermissionService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<PermissionResponse> create(
      @Valid @RequestBody CreatePermissionRequest req) {
    Permission p = service.create(req.snippetId, req.userId, req.type);
    return ResponseEntity.ok(PermissionResponse.from(p));
  }

  @DeleteMapping("/{snippetId}/{userId}")
  public ResponseEntity<Void> delete(@PathVariable UUID snippetId, @PathVariable UUID userId) {
    service.delete(snippetId, userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{snippetId}/author")
  public ResponseEntity<AuthorResponse> author(@PathVariable UUID snippetId) {
    var owner = service.getAuthor(snippetId);
    return ResponseEntity.ok(new AuthorResponse(snippetId, owner.getUserId()));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<PermissionResponse>> listByUser(
      @PathVariable UUID userId, @RequestParam(required = false) PermissionType type) {
    var all = service.listByUser(userId);
    if (type != null) {
      all = all.stream().filter(p -> p.getType() == type).toList();
    }
    return ResponseEntity.ok(all.stream().map(PermissionResponse::from).toList());
  }
}
