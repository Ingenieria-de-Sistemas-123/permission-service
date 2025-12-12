package com.snippetsearcher.permission.service;

import com.snippetsearcher.permission.model.UserAccount;
import com.snippetsearcher.permission.repository.UserAccountRepository;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserAccountRepository repo;

    public UserService(UserAccountRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public UserAccount getBySub(String auth0Sub) {
        return repo
                .findByAuth0Sub(auth0Sub)
                .orElseThrow(() -> new IllegalStateException("User not found for sub=" + auth0Sub));
    }

    @Transactional(readOnly = true)
    public List<UserAccount> listOthers(String auth0Sub) {
        UserAccount me = getBySub(auth0Sub);
        UUID myId = me.getId();
        return repo.findAll().stream()
                .filter(u -> !u.getId().equals(myId))
                .toList();
    }
}
