package com.snippetsearcher.permission;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snippetsearcher.permission.config.GlobalExceptionHandler;
import com.snippetsearcher.permission.controller.*;
import com.snippetsearcher.permission.dto.CreatePermissionRequest;
import com.snippetsearcher.permission.model.Permission;
import com.snippetsearcher.permission.model.PermissionType;
import com.snippetsearcher.permission.model.UserAccount;
import com.snippetsearcher.permission.service.AuthorizationService;
import com.snippetsearcher.permission.service.PermissionService;
import com.snippetsearcher.permission.service.UserProvisioningService;
import com.snippetsearcher.permission.service.UserService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
        controllers = {
                AuthorizationController.class,
                MeController.class,
                PermissionController.class,
                StatusController.class,
                UserController.class
        },
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PermissionControllersWebMvcTest {


    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockBean JwtDecoder jwtDecoder;
    @MockBean AuthorizationService authorizationService;
    @MockBean UserProvisioningService userProvisioningService;
    @MockBean PermissionService permissionService;
    @MockBean UserService userService;

    @Test
    void status_health_returnsUP() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("UP"));
    }

    @Test
    void authorization_authorize_allowed_true() throws Exception {
        when(authorizationService.isAllowed(any(), eq("READ"), anyString(), anyString())).thenReturn(true);

        var body =
                """
                {"action":"READ","snippetId":"%s","resourceOwnerSub":"owner-sub"}
                """
                        .formatted(UUID.randomUUID());

        mvc.perform(post("/api/permissions/authorize").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allowed").value(true))
                .andExpect(jsonPath("$.reason").value("ok"));
    }

    @Test
    void authorization_authorize_allowed_false() throws Exception {
        when(authorizationService.isAllowed(any(), eq("WRITE"), anyString(), anyString())).thenReturn(false);

        var body =
                """
                {"action":"WRITE","snippetId":"%s","resourceOwnerSub":"owner-sub"}
                """
                        .formatted(UUID.randomUUID());

        mvc.perform(post("/api/permissions/authorize").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allowed").value(false))
                .andExpect(jsonPath("$.reason").value("denied"));
    }

    @Test
    void me_sync_returns_userAccount() throws Exception {
        var acc = Mockito.mock(UserAccount.class);
        var id = UUID.randomUUID();
        when(acc.getId()).thenReturn(id);
        when(acc.getName()).thenReturn("Toto");
        when(acc.getEmail()).thenReturn("toto@mail.com");

        when(userProvisioningService.ensureUser(any())).thenReturn(acc);

        mvc.perform(post("/api/me/sync").with(jwt().jwt(j -> j.subject("sub"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Toto"))
                .andExpect(jsonPath("$.email").value("toto@mail.com"));
    }

    @Test
    void me_me_returns_userAccount() throws Exception {
        var acc = Mockito.mock(UserAccount.class);
        var id = UUID.randomUUID();
        when(acc.getId()).thenReturn(id);
        when(acc.getName()).thenReturn("Toto");
        when(acc.getEmail()).thenReturn("toto@mail.com");

        when(userProvisioningService.ensureUser(any())).thenReturn(acc);

        mvc.perform(get("/api/me").with(jwt().jwt(j -> j.subject("sub"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Toto"))
                .andExpect(jsonPath("$.email").value("toto@mail.com"));
    }

    @Test
    void me_listMySnippets_maps_permissions_to_response() throws Exception {
        var acc = Mockito.mock(UserAccount.class);
        var userId = UUID.randomUUID();
        when(acc.getId()).thenReturn(userId);
        when(userProvisioningService.ensureUser(any())).thenReturn(acc);

        var p1 = Mockito.mock(Permission.class);
        var p2 = Mockito.mock(Permission.class);

        var s1 = UUID.randomUUID();
        var s2 = UUID.randomUUID();

        when(p1.getSnippetId()).thenReturn(s1);
        when(p1.getType()).thenReturn(PermissionType.OWNER);

        when(p2.getSnippetId()).thenReturn(s2);
        when(p2.getType()).thenReturn(PermissionType.SHARED);

        when(permissionService.listByUser(userId)).thenReturn(List.of(p1, p2));

        mvc.perform(get("/api/me/snippets").with(jwt().jwt(j -> j.subject("sub"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].snippetId").value(s1.toString()))
                .andExpect(jsonPath("$[0].type").value("OWNER"))
                .andExpect(jsonPath("$[1].snippetId").value(s2.toString()))
                .andExpect(jsonPath("$[1].type").value("SHARED"));
    }

    @Test
    void permissions_create_returns_permissionResponse() throws Exception {
        var p = Mockito.mock(Permission.class);
        var id = UUID.randomUUID();
        var snippetId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var createdAt = OffsetDateTime.now();

        when(p.getId()).thenReturn(id);
        when(p.getSnippetId()).thenReturn(snippetId);
        when(p.getUserId()).thenReturn(userId);
        when(p.getType()).thenReturn(PermissionType.SHARED);
        when(p.getCreatedAt()).thenReturn(createdAt);

        when(permissionService.create(eq(snippetId), eq(userId), eq(PermissionType.SHARED))).thenReturn(p);

        var req = new CreatePermissionRequest();
        req.snippetId = snippetId;
        req.userId = userId;
        req.type = PermissionType.SHARED;

        mvc.perform(
                        post("/api/permissions")
                                .with(jwt().jwt(j -> j.subject("sub")))
                                .contentType(APPLICATION_JSON)
                                .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.snippetId").value(snippetId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.type").value("SHARED"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void permissions_delete_returns_204() throws Exception {
        var snippetId = UUID.randomUUID();
        var userId = UUID.randomUUID();

        mvc.perform(delete("/api/permissions/{snippetId}/{userId}", snippetId, userId)
                        .with(jwt().jwt(j -> j.subject("sub"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void permissions_author_returns_authorResponse() throws Exception {
        var snippetId = UUID.randomUUID();
        var ownerPerm = Mockito.mock(Permission.class);
        var authorUserId = UUID.randomUUID();
        when(ownerPerm.getUserId()).thenReturn(authorUserId);

        when(permissionService.getAuthor(snippetId)).thenReturn(ownerPerm);

        mvc.perform(get("/api/permissions/{snippetId}/author", snippetId)
                        .with(jwt().jwt(j -> j.subject("sub"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.snippetId").value(snippetId.toString()))
                .andExpect(jsonPath("$.authorUserId").value(authorUserId.toString()));
    }

    @Test
    void permissions_listByUser_withoutType_returns_all() throws Exception {
        var userId = UUID.randomUUID();

        var p1 = Mockito.mock(Permission.class);
        var p2 = Mockito.mock(Permission.class);

        when(p1.getId()).thenReturn(UUID.randomUUID());
        when(p1.getSnippetId()).thenReturn(UUID.randomUUID());
        when(p1.getUserId()).thenReturn(userId);
        when(p1.getType()).thenReturn(PermissionType.OWNER);
        when(p1.getCreatedAt()).thenReturn(OffsetDateTime.now());

        when(p2.getId()).thenReturn(UUID.randomUUID());
        when(p2.getSnippetId()).thenReturn(UUID.randomUUID());
        when(p2.getUserId()).thenReturn(userId);
        when(p2.getType()).thenReturn(PermissionType.SHARED);
        when(p2.getCreatedAt()).thenReturn(OffsetDateTime.now());

        when(permissionService.listByUser(userId)).thenReturn(List.of(p1, p2));

        mvc.perform(get("/api/permissions/user/{userId}", userId)
                        .with(jwt().jwt(j -> j.subject("sub"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void permissions_listByUser_withType_filters() throws Exception {
        var userId = UUID.randomUUID();

        var p1 = Mockito.mock(Permission.class);
        var p2 = Mockito.mock(Permission.class);

        when(p1.getType()).thenReturn(PermissionType.OWNER);
        when(p2.getType()).thenReturn(PermissionType.SHARED);

        when(permissionService.listByUser(userId)).thenReturn(List.of(p1, p2));

        mvc.perform(get("/api/permissions/user/{userId}?type=OWNER", userId)
                        .with(jwt().jwt(j -> j.subject("sub"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].type").value("OWNER"));
    }
    
}
