package com.wine.auth.service;

import com.wine.auth.config.KeycloakProvider;
import com.wine.auth.dto.CreateUserRequest;
import com.wine.auth.dto.CreateUserResponse;
import com.wine.auth.exception.UserAlreadyExistsException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class KeycloakAdminClientService {

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

    public AccessTokenResponse getAccessToken(String username, String password) {
        Keycloak keycloak1 = kcCredentials.newKeycloakBuilderWithPasswordCredentials(username, password).build();
        AccessTokenResponse accessTokenResponse = null;
        try {
            accessTokenResponse = keycloak1.tokenManager().getAccessToken();
            return accessTokenResponse;
        } catch (BadRequestException ex) {
            throw new BadRequestException(ex.getMessage());
        }
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
