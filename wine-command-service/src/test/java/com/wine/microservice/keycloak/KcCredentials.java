package com.wine.microservice.keycloak;

import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.stereotype.Service;

@Service
public class KcCredentials {

    public KeycloakBuilder newKeycloakBuilderWithPasswordCredentials(String username, String password) {
        return KeycloakBuilder.builder()
                .realm("springboot-microservice-realm")
                .serverUrl("http://localhost:8081")
                .clientId("admin-cli")
                .clientSecret("jjmGXtMwi6po23Cxahldp3svXzHlUxQz")
                .username(username)
                .password(password);
    }
}
