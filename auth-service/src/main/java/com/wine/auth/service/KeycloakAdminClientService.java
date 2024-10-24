package com.wine.auth.service;


import com.wine.auth.dto.*;
import com.wine.auth.exception.InvalidUserCredentialsException;
import com.wine.auth.exception.UserAlreadyExistsException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class KeycloakAdminClientService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issueUrl;

    @Value("${spring.security.oauth2.client.registration.oauth2-client-credentials.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.oauth2-client-credentials.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.oauth2-client-credentials.authorization-grant-type}")
    private String grantType;

    @Value("${keycloak.realm}")
    public String realm;

    private Keycloak keycloak;

    @Autowired
    private KcCredentials kcCredentials;


    public KeycloakAdminClientService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }


    public CreateUserResponse createKeycloakUser(CreateUserRequest createUserRequest) throws UserAlreadyExistsException {

        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> users = usersResource.search(createUserRequest.getUsername(), true);
        if(!users.isEmpty()) {
            throw new UserAlreadyExistsException("Username " + createUserRequest.getUsername() + " già esistente");
        }

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(createUserRequest.getUsername());
        user.setEmail(createUserRequest.getEmail());
        user.setFirstName(createUserRequest.getFirstName());
        user.setLastName(createUserRequest.getLastName());
        user.setEmailVerified(true);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(createUserRequest.getPassword());
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        user.setCredentials(Collections.singletonList(credentialRepresentation));

        Response response = usersResource.create(user);

        //String id = keycloak.realm(realm).users().search(createUserRequest.getUsername()).get(0).getId();
        String userId = CreatedResponseUtil.getCreatedId(response);
        RoleRepresentation role = keycloak.realm(realm).roles().get("user").toRepresentation();
        keycloak.realm(realm).users().get(userId).roles().realmLevel().add(Arrays.asList(role));

        return mapToUserResponse(user);
    }

    public AccessTokenResponse getAccessToken(String username, String password) throws InvalidUserCredentialsException {
        Keycloak keycloak1 = kcCredentials.newKeycloakBuilderWithPasswordCredentials(username, password).build();
        AccessTokenResponse accessTokenResponse = null;
        try {
            accessTokenResponse = keycloak1.tokenManager().getAccessToken();
            return accessTokenResponse;
        } catch (NotAuthorizedException ex) {
            throw new InvalidUserCredentialsException("Username o Password non validi");
        }
    }


    public ResponseEntity<LogoutResponse> logout(TokenRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("refresh_token", request.getToken());


        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map,headers);

        ResponseEntity<LogoutResponse> response = restTemplate.postForEntity("http://localhost:8081/realms/springboot-microservice-realm/protocol/openid-connect/logout", httpEntity, LogoutResponse.class);

        LogoutResponse res = new LogoutResponse();
        if(response.getStatusCode().is2xxSuccessful()) {
            res.setMessage("Logged out successfully");
        } else {
            res.setMessage("Logout failed");
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    public ResponseEntity<IntrospectResponse> introspect(TokenRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("token", request.getToken());

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map,headers);

        ResponseEntity<IntrospectResponse> response = restTemplate.postForEntity("http://localhost:8081/realms/springboot-microservice-realm/protocol/openid-connect/token/introspect", httpEntity, IntrospectResponse.class);
        return new ResponseEntity<>(response.getBody(),HttpStatus.OK);
    }



    private UsersResource getUsersResource() {
        RealmResource realmResource = keycloak.realm(realm);
        return realmResource.users();
    }

    public CreateUserResponse mapToUserResponse(UserRepresentation userRep) {
        CreateUserResponse response = CreateUserResponse.builder()
                .username(userRep.getUsername())
                .email(userRep.getEmail())
                .firstName(userRep.getFirstName())
                .lastName(userRep.getLastName())
                .build();

        return response;
    }

}
