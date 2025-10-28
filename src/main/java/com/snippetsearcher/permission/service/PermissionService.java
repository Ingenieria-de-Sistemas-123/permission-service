package com.snippetsearcher.permission.service;

import com.snippetsearcher.permission.model.Permission;
import com.snippetsearcher.permission.model.PermissionType;
import com.snippetsearcher.permission.repository.PermissionRepository;
import com.snippetsearcher.permission.web.errors.ConflictException;
import com.snippetsearcher.permission.web.errors.NotFoundException;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionService {

  private final PermissionRepository repo;

  public PermissionService(PermissionRepository repo) {
    this.repo = repo;
  }

  @Transactional
  public Permission create(UUID snippetId, UUID userId, PermissionType type) {
    // No duplicar permiso exacto
    repo.findBySnippetIdAndUserId(snippetId, userId)
        .ifPresent(
            existing -> {
              throw new ConflictException("Permission already exists for snippet+user.");
            });

    // Un único OWNER por snippet
    if (type == PermissionType.OWNER
        && repo.existsBySnippetIdAndType(snippetId, PermissionType.OWNER)) {
      throw new ConflictException("Snippet already has an OWNER.");
    }

    Permission p = new Permission(snippetId, userId, type);
    return repo.save(p);
  }

  @Transactional
  public void delete(UUID snippetId, UUID userId) {
    var perm =
        repo.findBySnippetIdAndUserId(snippetId, userId)
            .orElseThrow(() -> new NotFoundException("Permission not found."));
    repo.delete(perm);
  }

  @Transactional(readOnly = true)
  public Permission getAuthor(UUID snippetId) {
    return repo.findBySnippetIdAndType(snippetId, PermissionType.OWNER)
        .orElseThrow(() -> new NotFoundException("Owner not found for snippet."));
  }

  @Transactional(readOnly = true)
  public List<Permission> listByUser(UUID userId) {
    return repo.findAllByUserId(userId);
  }
}
