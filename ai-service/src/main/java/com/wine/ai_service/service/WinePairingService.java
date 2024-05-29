package com.wine.ai_service.service;


import com.wine.ai_service.dto.WineDTO;
import com.wine.ai_service.dto.WineInfo;
import com.wine.ai_service.dto.WinePairingDTO;
import com.wine.ai_service.model.WinePairing;

public interface WinePairingService {

    public WinePairingDTO generatePairing(WineDTO wineDTO);
    public String generateWinePairingInformation(String message);
    public String getWineInfoBasedOnFilters(String wineType, String region);
    public WineInfo obtainWineInfoBasedOnFilters(String wineType, String region);
    public WinePairingDTO getWinePairingById(Long id) throws Exception;
    public void saveWinePair(WinePairing winePairing);
}
