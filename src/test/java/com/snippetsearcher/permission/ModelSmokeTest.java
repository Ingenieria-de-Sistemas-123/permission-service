package com.snippetsearcher.permission;

import static org.junit.jupiter.api.Assertions.*;

import com.snippetsearcher.permission.model.Permission;
import com.snippetsearcher.permission.model.PermissionType;
import com.snippetsearcher.permission.model.UserAccount;
import com.snippetsearcher.permission.web.errors.ConflictException;
import com.snippetsearcher.permission.web.errors.NotFoundException;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ModelSmokeTest {

    @Test
    void permission_getters_setters_and_ctor() {
        UUID snippetId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Permission p = new Permission(snippetId, userId, PermissionType.SHARED);

        assertEquals(snippetId, p.getSnippetId());
        assertEquals(userId, p.getUserId());
        assertEquals(PermissionType.SHARED, p.getType());
        assertNotNull(p.getCreatedAt());

        UUID newId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        p.setId(newId);
        p.setSnippetId(UUID.randomUUID());
        p.setUserId(UUID.randomUUID());
        p.setType(PermissionType.OWNER);
        p.setCreatedAt(now);

        assertEquals(newId, p.getId());
        assertEquals(PermissionType.OWNER, p.getType());
        assertEquals(now, p.getCreatedAt());
    }

    @Test
    void userAccount_ctor_and_setters() {
        UserAccount u = new UserAccount("sub", "a@mail.com", "Name", "pic");
        assertEquals("sub", u.getAuth0Sub());
        assertEquals("a@mail.com", u.getEmail());
        assertEquals("Name", u.getName());
        assertEquals("pic", u.getPicture());
        assertNotNull(u.getCreatedAt());
        assertNotNull(u.getUpdatedAt());

        u.setEmail("b@mail.com");
        u.setName("Name2");
        u.setPicture("pic2");

        assertEquals("b@mail.com", u.getEmail());
        assertEquals("Name2", u.getName());
        assertEquals("pic2", u.getPicture());
    }

    @Test
    void exceptions_store_message() {
        var c = new ConflictException("x");
        var n = new NotFoundException("y");

        assertEquals("x", c.getMessage());
        assertEquals("y", n.getMessage());
    }
}
