package org.project.wine.gateway_service.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ServerLogoutSuccessHandler serverLogoutSuccessHandler) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/eureka/**", "/user/**", "/logout/success").permitAll()
                        .anyExchange().authenticated())
                .oauth2Login(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutSuccessHandler(serverLogoutSuccessHandler))
                //.exceptionHandling(e -> e.authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint("/user/login")))
                .securityContextRepository(new WebSessionServerSecurityContextRepository())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    @Bean
    public ServerLogoutSuccessHandler keycloakLogoutSuccessHandler(ReactiveClientRegistrationRepository repository) {

        OidcClientInitiatedServerLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedServerLogoutSuccessHandler(repository);

        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/logout/success");

        return oidcLogoutSuccessHandler;
    }
}