package com.wine.wine_service.service;

import com.wine.wine_service.dto.WineDTO;
import com.wine.wine_service.exception.LinkAlreadyExistsException;
import com.wine.wine_service.exception.WineAlreadyExistsException;
import com.wine.wine_service.exception.WineNotFoundException;

import java.util.List;

public interface WineService {

    public WineDTO createWine(WineDTO wineDTO) throws WineAlreadyExistsException;

    public WineDTO getWineById(Long id) throws WineNotFoundException;

    public List<WineDTO> getAllWines();

    public WineDTO updateWine(Long id, WineDTO wineDTO) throws WineNotFoundException;

    public void deleteWine(Long id) throws WineNotFoundException;

    public List<WineDTO> searchWines(String wineName, String wineType, String grape, String region, String denomination, int year, double alcoholPercentage);

    public WineDTO addLinkToWine(Long id, String link) throws WineNotFoundException, LinkAlreadyExistsException;

    public boolean isValidLink(String link);
}