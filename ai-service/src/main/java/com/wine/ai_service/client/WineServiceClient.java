package com.wine.ai_service.client;

import com.wine.ai_service.dto.WineDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WineServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    public WineDTO getWineById(Long wineId){
        String wineServiceUrl = "http://localhost:8084/wine/" + wineId;
        return restTemplate.getForObject(wineServiceUrl, WineDTO.class);
    }

}
