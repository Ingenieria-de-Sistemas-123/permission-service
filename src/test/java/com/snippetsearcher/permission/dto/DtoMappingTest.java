package com.snippetsearcher.permission.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.snippetsearcher.permission.model.Permission;
import com.snippetsearcher.permission.model.PermissionType;
import com.snippetsearcher.permission.model.UserAccount;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DtoMappingTest {

    @Test
    void permissionResponse_from_maps_all_fields() {
        var p = Mockito.mock(Permission.class);

        var id = UUID.randomUUID();
        var snippetId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var createdAt = OffsetDateTime.now();

        Mockito.when(p.getId()).thenReturn(id);
        Mockito.when(p.getSnippetId()).thenReturn(snippetId);
        Mockito.when(p.getUserId()).thenReturn(userId);
        Mockito.when(p.getType()).thenReturn(PermissionType.OWNER);
        Mockito.when(p.getCreatedAt()).thenReturn(createdAt);

        var r = PermissionResponse.from(p);

        assertEquals(id, r.id);
        assertEquals(snippetId, r.snippetId);
        assertEquals(userId, r.userId);
        assertEquals(PermissionType.OWNER, r.type);
        assertEquals(createdAt, r.createdAt);
    }

    @Test
    void snippetPermissionResponse_from_maps_fields() {
        var p = Mockito.mock(Permission.class);
        var snippetId = UUID.randomUUID();

        Mockito.when(p.getSnippetId()).thenReturn(snippetId);
        Mockito.when(p.getType()).thenReturn(PermissionType.SHARED);

        var r = SnippetPermissionResponse.from(p);

        assertEquals(snippetId, r.snippetId);
        assertEquals(PermissionType.SHARED, r.type);
    }

    @Test
    void userDto_from_maps_fields() {
        var acc = Mockito.mock(UserAccount.class);
        var id = UUID.randomUUID();

        Mockito.when(acc.getId()).thenReturn(id);
        Mockito.when(acc.getName()).thenReturn("Tobias");
        Mockito.when(acc.getEmail()).thenReturn("tobias@mail.com");

        var dto = UserDto.from(acc);

        assertEquals(id, dto.id());
        assertEquals("Tobias", dto.name());
        assertEquals("tobias@mail.com", dto.email());
    }
}
