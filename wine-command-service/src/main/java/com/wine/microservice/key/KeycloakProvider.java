package com.wine.microservice.key;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakProvider {

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


    @Bean
    public Keycloak keycloak() {

        return KeycloakBuilder.builder()
                .realm("springboot-microservice-realm")
                .serverUrl("http://localhost:8081")
                .clientId("admin-cli")
                .clientSecret("jjmGXtMwi6po23Cxahldp3svXzHlUxQz")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();

    }



//
//    public JsonNode refreshToken(String refreshToken) throws UnirestException {
//        String url = serverURL + "/realms/" + realm + "/protocol/openid-connect/token";
//        return Unirest.post(url)
//                .header("Content-Type", "application/x-www-form-urlencoded")
//                .field("client_id", clientID)
//                .field("client_secret", clientSecret)
//                .field("refresh_token", refreshToken)
//                .field("grant_type", "refresh_token")
//                .asJson().getBody();
//    }
}
