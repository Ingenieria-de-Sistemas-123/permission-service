package com.snippetsearcher.permission.security;

import com.snippetsearcher.permission.security.jwt.AudienceValidator;
import com.snippetsearcher.permission.security.jwt.JwtAuthConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

// 👇 imports para CORS
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class OAuth2ResourceServerSecurityConfiguration {

  @Value("${auth0.issuer}")
  private String issuer;

  @Value("${auth0.audience}")
  private String audience;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            // 👇 habilitamos CORS
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth ->
                    auth
                            // health sin auth
                            .requestMatchers("/actuator/health").permitAll()
                            // preflight OPTIONS desde el front
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            // endpoints protegidos
                            .requestMatchers("/api/me/**", "/api/permissions/**").authenticated()
                            // el resto, por ahora, libre
                            .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth ->
                    oauth.jwt(jwt -> {
                      JwtAuthenticationConverter c = new JwtAuthenticationConverter();
                      c.setJwtGrantedAuthoritiesConverter(new JwtAuthConverter());
                      jwt.jwtAuthenticationConverter(c);
                    })
            );

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

  // 👇 Configuración CORS para permitir al front en localhost:5173
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    // origen del front
    config.setAllowedOrigins(List.of("http://localhost:5173"));
    // métodos permitidos
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    // cabeceras que vamos a usar
    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    // para poder enviar cookies/credenciales si algún día lo necesitas
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
