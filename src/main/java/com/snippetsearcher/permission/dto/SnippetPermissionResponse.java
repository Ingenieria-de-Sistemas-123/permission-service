package com.snippetsearcher.permission.dto;

import com.snippetsearcher.permission.model.Permission;
import com.snippetsearcher.permission.model.PermissionType;
import java.util.UUID;

public class SnippetPermissionResponse {
  public UUID snippetId;
  public PermissionType type;

  public static SnippetPermissionResponse from(Permission permission) {
    var response = new SnippetPermissionResponse();
    response.snippetId = permission.getSnippetId();
    response.type = permission.getType();
    return response;
  }
}
