package com.snippetsearcher.permission.dto;

import com.snippetsearcher.permission.model.PermissionType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CreatePermissionRequest {
  @NotNull public UUID snippetId;
  @NotNull public UUID userId;
  @NotNull public PermissionType type;
}
