package com.wine.user.client;

import com.wine.user.dto.UserWinePairingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class WinePairingServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    public List<UserWinePairingDTO> getAllUserWinePairings(Jwt jwt) {
        String winePairingServiceUrl = "http://localhost:8082/ai/user/my-pairings";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt.getTokenValue());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange(
                winePairingServiceUrl,
                HttpMethod.GET,
                entity,
                List.class
        );

        return response.getBody();
    }

    public void deleteAllUserWinePairing(String userId) {
        String userWinePairingServiceDeleteUrl = "http://localhost:8082/ai/user/"+ userId +"/pairings/delete";

        restTemplate.delete(userWinePairingServiceDeleteUrl);
    }
}