package com.wine.microservice.key;

import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.stereotype.Service;

@Service
public class KcCredentials {

//    @Value("${keycloak.auth-server-url}")
//    public String serverURL;
//
//    @Value("${keycloak.realm}")
//    public String realm;
//
//    @Value("${keycloak.resource}")
//    public String clientID;
//
//    @Value("${keycloak.credentials.secret}")
//    public String clientSecret;

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
