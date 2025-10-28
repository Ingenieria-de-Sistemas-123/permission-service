package com.snippetsearcher.permission.repository;

import com.snippetsearcher.permission.model.Permission;
import com.snippetsearcher.permission.model.PermissionType;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
  Optional<Permission> findBySnippetIdAndType(UUID snippetId, PermissionType type);

  Optional<Permission> findBySnippetIdAndUserId(UUID snippetId, UUID userId);

  List<Permission> findAllByUserId(UUID userId);

  boolean existsBySnippetIdAndType(UUID snippetId, PermissionType type);

  void deleteBySnippetIdAndUserId(UUID snippetId, UUID userId);
}
