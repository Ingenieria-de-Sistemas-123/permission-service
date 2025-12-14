package com.snippetsearcher.permission.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

class AuthorizationServiceTest {

    private final AuthorizationService service = new AuthorizationService();

    @Test
    void isAllowed_owner_can_read_and_write_prefixed_actions() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("sub-1");

        assertTrue(service.isAllowed(jwt, "read:snippets", "sid", "sub-1"));
        assertTrue(service.isAllowed(jwt, "write:snippets", "sid", "sub-1"));
        assertFalse(service.isAllowed(jwt, "READ", "sid", "sub-1"));
    }

    @Test
    void isAllowed_notOwner_denied() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("sub-me");

        assertFalse(service.isAllowed(jwt, "read:snippets", "sid", "someone-else"));
        assertFalse(service.isAllowed(jwt, "write:snippets", "sid", "someone-else"));
    }

    @Test
    void isAllowed_resourceOwnerSub_null_denied() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("sub-me");

        assertFalse(service.isAllowed(jwt, "read:snippets", "sid", null));
    }
}
