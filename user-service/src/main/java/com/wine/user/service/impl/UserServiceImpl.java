package com.wine.user.service.impl;

import com.wine.user.client.WinePairingServiceClient;
import com.wine.user.dto.UserWinePairingDTO;
import com.wine.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private WinePairingServiceClient winePairingServiceClient;

    @Override
    public List<UserWinePairingDTO> getAllUserWinePairings(Jwt jwt) {
        return winePairingServiceClient.getAllUserWinePairings(jwt);
    }
}
