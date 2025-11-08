package com.snippetsearcher.permission.security.jwt;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

@SuppressWarnings("unchecked")
public class JwtAuthConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
  @Override
  public Collection<GrantedAuthority> convert(Jwt jwt) {
    Set<String> authorities = new HashSet<>();

    Object perms = jwt.getClaims().get("permissions");
    if (perms instanceof Collection<?> collection) {
      collection.forEach(
          p -> {
            if (p instanceof String s) authorities.add("SCOPE_" + s);
          });
    }

    Object scope = jwt.getClaims().get("scope");
    if (scope instanceof String s) {
      Arrays.stream(s.split(" "))
          .filter(t -> !t.isBlank())
          .forEach(t -> authorities.add("SCOPE_" + t));
    }

    return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
  }
}
