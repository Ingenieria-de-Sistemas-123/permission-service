package com.snippetsearcher.permission.dto;

import java.util.UUID;

public class AuthorResponse {
  public UUID snippetId;
  public UUID authorUserId;

  public AuthorResponse(UUID snippetId, UUID authorUserId) {
    this.snippetId = snippetId;
    this.authorUserId = authorUserId;
  }
}
