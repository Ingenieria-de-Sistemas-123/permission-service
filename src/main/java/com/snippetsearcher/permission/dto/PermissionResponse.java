package com.snippetsearcher.permission.dto;

import com.snippetsearcher.permission.model.Permission;
import com.snippetsearcher.permission.model.PermissionType;
import java.time.OffsetDateTime;
import java.util.UUID;

public class PermissionResponse {
  public UUID id;
  public UUID snippetId;
  public UUID userId;
  public PermissionType type;
  public OffsetDateTime createdAt;

  public static PermissionResponse from(Permission p) {
    var r = new PermissionResponse();
    r.id = p.getId();
    r.snippetId = p.getSnippetId();
    r.userId = p.getUserId();
    r.type = p.getType();
    r.createdAt = p.getCreatedAt();
    return r;
  }
}
