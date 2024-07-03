package com.wine.user.service;

import com.wine.user.dto.UserWinePairingDTO;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface UserService {

    public List<UserWinePairingDTO> getAllUserWinePairings(Jwt jwt);

}
