package com.snippetsearcher.permission.repository;

import com.snippetsearcher.permission.model.UserAccount;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
  Optional<UserAccount> findByAuth0Sub(String auth0Sub);
}
