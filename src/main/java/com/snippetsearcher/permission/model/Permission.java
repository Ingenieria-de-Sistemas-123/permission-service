package com.snippetsearcher.permission.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "permissions",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_permissions_snippet_user",
          columnNames = {"snippet_id", "user_id"})
    })
public class Permission {

  @Id @GeneratedValue private UUID id;

  @Column(name = "snippet_id", nullable = false)
  private UUID snippetId;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 12)
  private PermissionType type;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt = OffsetDateTime.now();

  protected Permission() {
    // JPA
  }

  public Permission(UUID snippetId, UUID userId, PermissionType type) {
    this.snippetId = snippetId;
    this.userId = userId;
    this.type = type;
  }

  public UUID getId() {
    return id;
  }

  public UUID getSnippetId() {
    return snippetId;
  }

  public UUID getUserId() {
    return userId;
  }

  public PermissionType getType() {
    return type;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public void setSnippetId(UUID snippetId) {
    this.snippetId = snippetId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public void setType(PermissionType type) {
    this.type = type;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
