package com.snippetsearcher.permission.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.snippetsearcher.permission.model.Permission;
import com.snippetsearcher.permission.model.PermissionType;
import com.snippetsearcher.permission.repository.PermissionRepository;
import com.snippetsearcher.permission.web.errors.ConflictException;
import com.snippetsearcher.permission.web.errors.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class PermissionServiceTest {

    @Mock PermissionRepository repo;
    @InjectMocks
    PermissionService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_throwsConflict_when_permissionAlreadyExists_forSnippetAndUser() {
        UUID snippetId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(repo.findBySnippetIdAndUserId(snippetId, userId)).thenReturn(Optional.of(mock(Permission.class)));

        var ex =
                assertThrows(
                        ConflictException.class,
                        () -> service.create(snippetId, userId, PermissionType.SHARED));

        assertEquals("Permission already exists for snippet+user.", ex.getMessage());
        verify(repo, never()).save(any());
    }

    @Test
    void create_throwsConflict_when_typeOwner_and_ownerAlreadyExistsForSnippet() {
        UUID snippetId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(repo.findBySnippetIdAndUserId(snippetId, userId)).thenReturn(Optional.empty());
        when(repo.existsBySnippetIdAndType(snippetId, PermissionType.OWNER)).thenReturn(true);

        var ex =
                assertThrows(
                        ConflictException.class,
                        () -> service.create(snippetId, userId, PermissionType.OWNER));

        assertEquals("Snippet already has an OWNER.", ex.getMessage());
        verify(repo, never()).save(any());
    }

    @Test
    void create_saves_and_returns_permission_when_valid() {
        UUID snippetId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(repo.findBySnippetIdAndUserId(snippetId, userId)).thenReturn(Optional.empty());
        when(repo.existsBySnippetIdAndType(snippetId, PermissionType.OWNER)).thenReturn(false);

        when(repo.save(any(Permission.class)))
                .thenAnswer(inv -> inv.getArgument(0, Permission.class));

        Permission created = service.create(snippetId, userId, PermissionType.SHARED);

        assertNotNull(created);
        assertEquals(snippetId, created.getSnippetId());
        assertEquals(userId, created.getUserId());
        assertEquals(PermissionType.SHARED, created.getType());
        assertNotNull(created.getCreatedAt());

        verify(repo).save(any(Permission.class));
    }

    @Test
    void delete_throwsNotFound_when_permissionDoesNotExist() {
        UUID snippetId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(repo.findBySnippetIdAndUserId(snippetId, userId)).thenReturn(Optional.empty());

        var ex = assertThrows(NotFoundException.class, () -> service.delete(snippetId, userId));
        assertEquals("Permission not found.", ex.getMessage());
        verify(repo, never()).delete(any());
    }

    @Test
    void delete_deletes_permission_when_exists() {
        UUID snippetId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Permission perm = mock(Permission.class);
        when(repo.findBySnippetIdAndUserId(snippetId, userId)).thenReturn(Optional.of(perm));

        service.delete(snippetId, userId);

        verify(repo).delete(perm);
    }

    @Test
    void getAuthor_returns_owner_permission() {
        UUID snippetId = UUID.randomUUID();
        Permission owner = mock(Permission.class);

        when(repo.findBySnippetIdAndType(snippetId, PermissionType.OWNER)).thenReturn(Optional.of(owner));

        assertSame(owner, service.getAuthor(snippetId));
    }

    @Test
    void getAuthor_throwsNotFound_when_noOwner() {
        UUID snippetId = UUID.randomUUID();
        when(repo.findBySnippetIdAndType(snippetId, PermissionType.OWNER)).thenReturn(Optional.empty());

        var ex = assertThrows(NotFoundException.class, () -> service.getAuthor(snippetId));
        assertEquals("Owner not found for snippet.", ex.getMessage());
    }

    @Test
    void listByUser_delegates_to_repo() {
        UUID userId = UUID.randomUUID();
        List<Permission> perms = List.of(mock(Permission.class), mock(Permission.class));

        when(repo.findAllByUserId(userId)).thenReturn(perms);

        assertSame(perms, service.listByUser(userId));
    }
}
