package com.snippetsearcher.permission.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.snippetsearcher.permission.model.UserAccount;
import com.snippetsearcher.permission.repository.UserAccountRepository;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.oauth2.jwt.Jwt;

class UserProvisioningServiceTest {

    @Mock UserAccountRepository repo;
    @InjectMocks UserProvisioningService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void ensureUser_creates_new_when_not_exists_and_uses_namespaced_claims() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("sub-1");

        String NS = "https://snippetsearcher.com";
        when(jwt.getClaimAsString(NS + "/email")).thenReturn("ns@mail.com");
        when(jwt.getClaimAsString(NS + "/name")).thenReturn("NS Name");
        when(jwt.getClaimAsString(NS + "/picture")).thenReturn("pic-url");

        // claims "planos" null
        when(jwt.getClaimAsString("email")).thenReturn(null);
        when(jwt.getClaimAsString("name")).thenReturn(null);
        when(jwt.getClaimAsString("nickname")).thenReturn(null);
        when(jwt.getClaimAsString("picture")).thenReturn(null);

        when(jwt.getClaims()).thenReturn(Map.of(NS + "/email", "ns@mail.com"));

        when(repo.findByAuth0Sub("sub-1")).thenReturn(Optional.empty());
        when(repo.save(any(UserAccount.class))).thenAnswer(inv -> inv.getArgument(0, UserAccount.class));

        UserAccount u = service.ensureUser(jwt);

        assertEquals("sub-1", u.getAuth0Sub());
        assertEquals("ns@mail.com", u.getEmail());
        assertEquals("NS Name", u.getName());
        assertEquals("pic-url", u.getPicture());

        verify(repo).save(any(UserAccount.class));
    }

    @Test
    void ensureUser_updates_existing_and_sets_updatedAt() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("sub-2");

        String NS = "https://snippetsearcher.com";
        when(jwt.getClaimAsString(NS + "/email")).thenReturn("new@mail.com");
        when(jwt.getClaimAsString(NS + "/name")).thenReturn("New Name");
        when(jwt.getClaimAsString(NS + "/picture")).thenReturn("new-pic");

        when(jwt.getClaimAsString("email")).thenReturn(null);
        when(jwt.getClaimAsString("name")).thenReturn(null);
        when(jwt.getClaimAsString("nickname")).thenReturn(null);
        when(jwt.getClaimAsString("picture")).thenReturn(null);

        when(jwt.getClaims()).thenReturn(Map.of(NS + "/email", "new@mail.com"));

        UserAccount existing = spy(new UserAccount("sub-2", "old@mail.com", "Old", "old-pic"));

        when(repo.findByAuth0Sub("sub-2")).thenReturn(Optional.of(existing));
        when(repo.save(any(UserAccount.class))).thenAnswer(inv -> inv.getArgument(0, UserAccount.class));

        Instant before = existing.getUpdatedAt();

        UserAccount saved = service.ensureUser(jwt);

        assertSame(existing, saved);
        assertEquals("new@mail.com", saved.getEmail());
        assertEquals("New Name", saved.getName());
        assertEquals("new-pic", saved.getPicture());
        assertTrue(saved.getUpdatedAt().isAfter(before) || saved.getUpdatedAt().equals(before) == false);

        verify(repo).save(existing);
    }

    @Test
    void ensureUser_falls_back_to_unknown_email_and_name_when_missing() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("sub-3");

        String NS = "https://snippetsearcher.com";
        // todo nulo / blank
        when(jwt.getClaimAsString("email")).thenReturn("   ");
        when(jwt.getClaimAsString(NS + "/email")).thenReturn(null);

        when(jwt.getClaimAsString("name")).thenReturn("  ");
        when(jwt.getClaimAsString(NS + "/name")).thenReturn(null);
        when(jwt.getClaimAsString("nickname")).thenReturn(null);

        when(jwt.getClaimAsString("picture")).thenReturn(null);
        when(jwt.getClaimAsString(NS + "/picture")).thenReturn(null);

        when(jwt.getClaims()).thenReturn(Map.of());

        when(repo.findByAuth0Sub("sub-3")).thenReturn(Optional.empty());
        when(repo.save(any(UserAccount.class))).thenAnswer(inv -> inv.getArgument(0, UserAccount.class));

        UserAccount u = service.ensureUser(jwt);

        assertEquals("unknown@example.com", u.getEmail());
        // name fallback termina siendo email
        assertEquals("unknown@example.com", u.getName());
        assertNull(u.getPicture());
    }
}
