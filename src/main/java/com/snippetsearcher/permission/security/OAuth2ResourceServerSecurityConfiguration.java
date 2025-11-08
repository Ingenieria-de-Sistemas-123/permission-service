package com.snippetsearcher.permission.security;

import com.snippetsearcher.permission.security.jwt.AudienceValidator;
import com.snippetsearcher.permission.security.jwt.JwtAuthConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class OAuth2ResourceServerSecurityConfiguration {

  @Value("${auth0.issuer}")
  private String issuer;

  @Value("${auth0.audience}")
  private String audience;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/actuator/health")
                    .permitAll()
                    .requestMatchers("/me/**", "/api/permissions/**")
                    .authenticated()
                    .anyRequest()
                    .permitAll())
        .oauth2ResourceServer(
            oauth ->
                oauth.jwt(
                    jwt -> {
                      JwtAuthenticationConverter c = new JwtAuthenticationConverter();
                      c.setJwtGrantedAuthoritiesConverter(new JwtAuthConverter());
                      jwt.jwtAuthenticationConverter(c);
                    }));
    return http.build();
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuer);
    OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
    OAuth2TokenValidator<Jwt> withAudience = new AudienceValidator(audience);
    jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience));
    return jwtDecoder;
  }
}
