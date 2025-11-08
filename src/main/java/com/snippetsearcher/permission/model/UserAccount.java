package com.snippetsearcher.permission.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Setter;

@Entity
@Table(name = "user_accounts")
public class UserAccount {

  @Id
  @GeneratedValue
  @Column(columnDefinition = "uuid")
  private UUID id;

  @Column(name = "auth0_sub", nullable = false, unique = true)
  private String auth0Sub;

  @Setter
  @Column(nullable = false)
  private String email;

  @Setter @Column private String name;

  @Setter @Column private String picture;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  @Setter
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt = Instant.now();

  protected UserAccount() {}

  public UserAccount(String auth0Sub, String email, String name, String picture) {
    this.auth0Sub = auth0Sub;
    this.email = email;
    this.name = name;
    this.picture = picture;
  }

  public UUID getId() {
    return id;
  }

  public String getAuth0Sub() {
    return auth0Sub;
  }

  public String getEmail() {
    return email;
  }

  public String getName() {
    return name;
  }

  public String getPicture() {
    return picture;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
