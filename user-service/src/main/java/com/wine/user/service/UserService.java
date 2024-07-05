package com.wine.user.service;

import com.wine.user.dto.UserInfo;
import com.wine.user.dto.UserInfoWithID;
import com.wine.user.dto.UserWinePairingDTO;
import com.wine.user.exception.UserAlreadyExistsException;
import com.wine.user.exception.UserNotFoundException;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface UserService {

    public List<UserWinePairingDTO> getAllUserWinePairings(Jwt jwt);

    public List<UserInfoWithID> getUsers();

    public UserInfo getUserProfileInfo(String userId);

    public void updateUser(String userId, UserInfo userInfo) throws UserAlreadyExistsException;

    public void deleteUser(String userId) throws UserNotFoundException;

    public void deleteUserProfile(Jwt jwt);

}
