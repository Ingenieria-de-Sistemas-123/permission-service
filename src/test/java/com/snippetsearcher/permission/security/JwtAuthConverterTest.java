package com.snippetsearcher.permission.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.snippetsearcher.permission.security.jwt.JwtAuthConverter;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

class JwtAuthConverterTest {

    @Test
    void convert_reads_permissions_collection_and_scope_string() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaims())
                .thenReturn(
                        Map.of(
                                "permissions", List.of("read:snippets", 123, "write:snippets"),
                                "scope", "openid  profile   email"));

        var converter = new JwtAuthConverter();
        var auths = converter.convert(jwt);

        var names = auths.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        // permissions list
        assertTrue(names.contains("SCOPE_read:snippets"));
        assertTrue(names.contains("SCOPE_write:snippets"));

        // scope string tokens (ignora blanks)
        assertTrue(names.contains("SCOPE_openid"));
        assertTrue(names.contains("SCOPE_profile"));
        assertTrue(names.contains("SCOPE_email"));
    }

    @Test
    void convert_handles_missing_or_wrong_types_gracefully() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaims()).thenReturn(Map.of("permissions", "not-a-collection"));

        var converter = new JwtAuthConverter();
        var auths = converter.convert(jwt);

        assertNotNull(auths);
    }
}
