package com.wine.wine_query.service;


import com.wine.wine_query.dto.WineDTO;
import com.wine.wine_query.dto.WineEvent;
import com.wine.wine_query.exception.WineNotFoundException;

import java.util.List;

public interface WineService {


    public WineDTO getWineById(Long id) throws WineNotFoundException;

    public List<WineDTO> getAllWines();

    public List<WineDTO> searchWines(String wineName, String wineType, String grape, String region, String denomination, int year, double alcoholPercentage);

    public void processWineEvents(WineEvent wineEvent);
}