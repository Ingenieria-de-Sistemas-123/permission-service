package com.snippetsearcher.permission.service;

import com.snippetsearcher.permission.model.UserAccount;
import com.snippetsearcher.permission.repository.UserAccountRepository;
import java.time.Instant;
import java.util.Optional;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserProvisioningService {
  private final UserAccountRepository repo;

  public UserProvisioningService(UserAccountRepository repo) {
    this.repo = repo;
  }

  public UserAccount ensureUser(Jwt jwt) {
    String sub = jwt.getSubject();
    String email = Optional.ofNullable(jwt.getClaimAsString("email")).orElse("unknown@example.com");
    String name = Optional.ofNullable(jwt.getClaimAsString("name")).orElse(email);
    String pic = Optional.ofNullable(jwt.getClaimAsString("picture")).orElse(null);

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
}
