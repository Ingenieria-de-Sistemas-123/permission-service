package com.snippetsearcher.permission.service;

import com.snippetsearcher.permission.model.UserAccount;
import com.snippetsearcher.permission.repository.UserAccountRepository;
import java.time.Instant;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserProvisioningService {

  private final UserAccountRepository repo;

  private static final String NS = "https://snippetsearcher.com";

  public UserProvisioningService(UserAccountRepository repo) {
    this.repo = repo;
  }

  public UserAccount ensureUser(Jwt jwt) {
    System.out.println("JWT CLAIMS >>> " + jwt.getClaims());

    String sub = jwt.getSubject();

    String email =
        firstNonBlank(
            jwt.getClaimAsString("email"), // por si algún día viene plano
            jwt.getClaimAsString(NS + "/email"), // <- el que TENÉS ahora
            "unknown@example.com");

    String name =
        firstNonBlank(
            jwt.getClaimAsString("name"),
            jwt.getClaimAsString(NS + "/name"),
            jwt.getClaimAsString("nickname"),
            email);

    String pic =
        firstNonBlank(jwt.getClaimAsString("picture"), jwt.getClaimAsString(NS + "/picture"));

    System.out.println(
        "RESOLVED USER >>> sub="
            + sub
            + ", email="
            + email
            + ", name="
            + name
            + ", picture="
            + pic);

    return repo.findByAuth0Sub(sub)
        .map(
            u -> {
              u.setEmail(email);
              u.setName(name);
              u.setPicture(pic);
              u.setUpdatedAt(Instant.now());
              return repo.save(u);
            })
        .orElseGet(() -> repo.save(new UserAccount(sub, email, name, pic)));
  }

  private static String firstNonBlank(String... values) {
    if (values == null) return null;
    for (String v : values) {
      if (v != null && !v.isBlank()) return v;
    }
    return null;
  }
}
