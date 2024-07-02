package org.project.wine.gateway_service.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class KeycloakLogoutHandler implements ServerLogoutHandler {

    private final WebClient webClient;

    public KeycloakLogoutHandler(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<Void> logout(WebFilterExchange exchange, Authentication authentication) {
        OidcUser user = (OidcUser) authentication.getPrincipal();
        String endSessionEndpoint = user.getIssuer() + "/protocol/openid-connect/logout";
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(endSessionEndpoint)
                .queryParam("id_token_hint", user.getIdToken().getTokenValue())
                .queryParam("post_logout_redirect_uri", "http://localhost:8085/user/login");

        return webClient.get()
                .uri(builder.toUriString())
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        log.info("Logout da Keycloak effettuato con successo");
                        return Mono.empty();
                    } else {
                        log.error("Logout non riuscito");
                        return Mono.error(new RuntimeException("Logout da Keycloak non riuscito"));
                    }
                });
    }
}
