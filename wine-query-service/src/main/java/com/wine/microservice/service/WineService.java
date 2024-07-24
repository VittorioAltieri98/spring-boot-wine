package com.wine.microservice.service;


import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.dto.WineEvent;
import com.wine.microservice.dto.WineResponseDTO;
import com.wine.microservice.exception.WineNotFoundException;

import java.util.List;

public interface WineService {


    public WineDTO getWineById(Long id) throws WineNotFoundException;

    public List<WineDTO> getAllWines();

    public List<WineDTO> searchWines(String wineName, String wineType, String grape, String region, String denomination, int year, double alcoholPercentage);

    public WineResponseDTO getWineDetailsWithPairings(Long wineId);

}