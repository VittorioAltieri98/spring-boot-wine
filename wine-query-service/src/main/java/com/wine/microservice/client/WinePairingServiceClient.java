package com.wine.microservice.client;

import com.wine.microservice.dto.WinePairingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WinePairingServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    public WinePairingDTO getWinePairingByWineId(Long wineId){
        String winePairingServiceUrl = "http://localhost:8082/ai/winePairing/by-wine-id/" + wineId;
        return restTemplate.getForObject(winePairingServiceUrl, WinePairingDTO.class);
    }
}
