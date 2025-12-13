package com.snippetsearcher.permission.service;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

  public boolean isAllowed(Jwt jwt, String action, String snippetId, String resourceOwnerSub) {
    String sub = jwt.getSubject();

    // regla base: si es owner, puede leer y escribir
    if (resourceOwnerSub != null && resourceOwnerSub.equals(sub)) {
      return action.startsWith("read:") || action.startsWith("write:");
    }

    // reglas adicionales: a futuro consultar BD de permisos
    return false;
  }
}
