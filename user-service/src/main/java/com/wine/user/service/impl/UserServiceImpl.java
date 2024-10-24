package com.wine.user.service.impl;

import com.wine.user.client.WinePairingServiceClient;
import com.wine.user.dto.UserInfo;
import com.wine.user.dto.UserInfoWithID;
import com.wine.user.dto.UserWinePairingDTO;
import com.wine.user.exception.UserAlreadyExistsException;
import com.wine.user.exception.UserNotFoundException;
import com.wine.user.service.UserService;
import jakarta.ws.rs.NotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Value("${keycloak.realm}")
    public String realm;

    private final Keycloak keycloak;

    @Autowired
    private WinePairingServiceClient winePairingServiceClient;

    public UserServiceImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }


    @Override
    public List<UserWinePairingDTO> getAllUserWinePairings(Jwt jwt) {
        return winePairingServiceClient.getAllUserWinePairings(jwt);
    }

    @Override
    public List<UserInfoWithID> getUsers() {
        List<UserRepresentation> usersRepresentation = keycloak.realm(realm).users().list();
        List<UserInfoWithID> users = usersRepresentation.stream()
                .map(rep -> mapToUserInfoWithId(rep))
                .collect(Collectors.toList());

        return users;
    }

    @Override
    public UserInfo getUserProfileInfo(String userId) {
        UserResource userResource = keycloak.realm(realm).users().get(userId);
        UserRepresentation userRepresentation = userResource.toRepresentation();
        return mapToUserInfo(userRepresentation);
    }

    @Override
    public void updateUser(String userId, UserInfo userInfo) throws UserAlreadyExistsException {
        UserResource userResource = keycloak.realm(realm).users().get(userId);
        UserRepresentation userRepresentation = userResource.toRepresentation();

        //Check if already exists a user with that username
        List<UserRepresentation> users = keycloak.realm(realm).users().search(userInfo.getUsername(), true);
        for (UserRepresentation user : users) {
            if (!user.getId().equals(userId)) {
                throw new UserAlreadyExistsException("Username " + userInfo.getUsername() + " già esistente");
            }
        }
        userRepresentation.setUsername(userInfo.getUsername());
        userRepresentation.setFirstName(userInfo.getFirstName());
        userRepresentation.setLastName(userInfo.getLastName());

        userResource.update(userRepresentation);
    }

    @Override
    public void deleteUser(String userId) throws UserNotFoundException {
        try {
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            winePairingServiceClient.deleteAllUserWinePairing(userId);
            userResource.remove();
        } catch(NotFoundException ex) {
            throw new UserNotFoundException("Utente con ID " + userId + " non trovato");
        }
    }

    @Override
    public void deleteUserProfile(Jwt jwt) {
        String userId = jwt.getSubject();

        winePairingServiceClient.deleteAllUserWinePairing(userId);
        List<UserRepresentation> users = keycloak.realm(realm).users().search(userId, true);

        keycloak.realm(realm).users().delete(userId);
    }


    public UserInfo mapToUserInfo(UserRepresentation userRep) {
        UserInfo response = UserInfo.builder()
                .username(userRep.getUsername())
                .email(userRep.getEmail())
                .firstName(userRep.getFirstName())
                .lastName(userRep.getLastName())
                .build();

        return response;
    }

    public UserInfoWithID mapToUserInfoWithId(UserRepresentation userRep) {
        UserInfoWithID response = UserInfoWithID.builder()
                .userId(userRep.getId())
                .username(userRep.getUsername())
                .email(userRep.getEmail())
                .firstName(userRep.getFirstName())
                .lastName(userRep.getLastName())
                .build();

        return response;
    }


}
