package com.snippetsearcher.permission.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.snippetsearcher.permission.model.UserAccount;
import com.snippetsearcher.permission.repository.UserAccountRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class UserServiceTest {

    @Mock UserAccountRepository repo;
    @InjectMocks UserService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBySub_returns_user_when_found() {
        UserAccount u = mock(UserAccount.class);
        when(repo.findByAuth0Sub("sub")).thenReturn(Optional.of(u));

        assertSame(u, service.getBySub("sub"));
    }

    @Test
    void getBySub_throws_when_not_found() {
        when(repo.findByAuth0Sub("sub")).thenReturn(Optional.empty());

        var ex = assertThrows(IllegalStateException.class, () -> service.getBySub("sub"));
        assertTrue(ex.getMessage().contains("User not found for sub=sub"));
    }

    @Test
    void listOthers_filters_out_me() {
        UUID meId = UUID.randomUUID();

        UserAccount me = mock(UserAccount.class);
        when(me.getId()).thenReturn(meId);

        UserAccount other1 = mock(UserAccount.class);
        when(other1.getId()).thenReturn(UUID.randomUUID());

        UserAccount other2 = mock(UserAccount.class);
        when(other2.getId()).thenReturn(UUID.randomUUID());

        when(repo.findByAuth0Sub("sub-me")).thenReturn(Optional.of(me));
        when(repo.findAll()).thenReturn(List.of(me, other1, other2));

        var res = service.listOthers("sub-me");

        assertEquals(2, res.size());
        assertFalse(res.contains(me));
    }
}
